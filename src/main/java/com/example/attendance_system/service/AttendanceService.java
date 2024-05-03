package com.example.attendance_system.service;

import com.example.attendance_system.dto.*;
import com.example.attendance_system.exception.AttendanceLimitHasBeenReachedException;
import com.example.attendance_system.exception.AttendanceNotFoundException;
import com.example.attendance_system.exception.InvalidAccessException;
import com.example.attendance_system.model.*;
import com.example.attendance_system.qr.QrCodeService;
import com.example.attendance_system.repo.*;
import com.example.attendance_system.util.*;
import com.google.zxing.WriterException;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.awt.image.BufferedImage;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class AttendanceService {
    private final QrCodeService qrCodeService;
    private final LessonService lessonService;
    private final PersonService personService;
    private final CourseService courseService;
    private final S3Service s3Service;

    private final QrAccessTokenRepository qrAccessTokenRepository;
    private final UserRepository userRepository;
    private final AttendanceRepository attendanceRepository;
    private final AttendanceRecordRepository attendanceRecordRepository;
    private final AbsenceReasonRepository absenceReasonRepository;

    private final ObjectValidator<AttendanceRequest> objectValidator;

    @Value("${application.endpoints.attendance.student.take}")
    private String studentAttendanceTakeEndpointPrefix;

    @Value("${amazon-s3.files.save}")
    private String s3FileSavingPrefix;

    public BufferedImage generateQR(AttendanceRequest attendanceRequest) throws WriterException {
        objectValidator.validate(attendanceRequest);

        Integer lessonId = attendanceRequest.getLessonId();
        Integer attendanceId = attendanceRequest.getAttendanceId();

        isAttendanceExistsWithoutLesson(lessonId, attendanceId);


        User teacher = personService.getCurrentUser();
        lessonService.isTeacherWithoutLesson(lessonId, teacher.getId());

        String accessToken = UUID.randomUUID().toString();
        final String finalUrl = String.format("%s/%s", studentAttendanceTakeEndpointPrefix, accessToken);


        Lesson lesson = lessonService.getLessonById(lessonId);
        List<User> students = lesson.getLessonStudents();
        Attendance attendance = attendanceRepository.findById(attendanceId)
                .orElseThrow(() -> new AttendanceNotFoundException(ExceptionMessage.attendanceNotFoundException(attendanceId)));

        List<AttendanceRecord> attendanceRecords = attendanceRecordRepository.findByAttendance(attendance);

        if (attendanceRecords == null || attendanceRecords.isEmpty()) {
            students.forEach(student ->
                    attendanceRecordRepository.save(
                            AttendanceRecord.builder()
                                    .attendance(attendance)
                                    .student(student)
                                    .group(lesson.getGroup())
                                    .attendanceStatus(AttendanceStatus.ABSENCE)
                                    .build()
                    ));
        }
        //INITIAL PUT ABSENCE FOR ALL
        QrAccessToken qrAccessToken = QrAccessToken.builder()
                .accessToken(accessToken)
                .lesson(lesson)
                .attendance(attendance)
                .expiration(LocalDateTime.now().plusMinutes(3))
                .build();
        qrAccessTokenRepository.save(qrAccessToken);

        return qrCodeService.generateQr(finalUrl);
//        hashOperations.getOperations().expire(getAccessTokenKey(accessToken), 2, TimeUnit.MINUTES);
    }


    public Integer takeByQr(final String ACCESS_TOKEN_KEY) {
//        Map<String, Integer> attendancesKeyMap = hashOperations.entries(accessToken);

        QrAccessToken qrAccessToken = validateAccessToken(ACCESS_TOKEN_KEY);
        Integer lessonId = qrAccessToken.getLesson().getId();
        Integer attendanceId = qrAccessToken.getAttendance().getId();


        User student = personService.getCurrentUser();

        lessonService.isStudentWithoutLesson(lessonId, student.getId());

        AttendanceRecord attendanceRecord = attendanceRecordRepository
                .findByAttendanceIdAndStudentId(attendanceId, student.getId());

        attendanceRecord.setAttendanceType(AttendanceType.QR);
        attendanceRecord.setAttendanceStatus(AttendanceStatus.PRESENT);
        attendanceRecord.setEntryTime(LocalTime.now());
        attendanceRecordRepository.saveAndFlush(attendanceRecord);

        boolean haveAccess = attendanceRecordRepository.checkStudentHaveAccessesForLesson(student.getId(), lessonId);
        if (haveAccess) {
            User producer = userRepository.findProducerByConsumerId(student.getId());

            boolean hasLimitBeenReached = attendanceRepository.checkLimitReached(producer.getId(), student.getId());

            if(hasLimitBeenReached){
                throw new AttendanceLimitHasBeenReachedException("Unable to take attendance for designated user, limit has been reached");
            }
            AttendanceRecord attendanceRecordForProducer = attendanceRecordRepository
                    .findByAttendanceIdAndStudentId(attendanceId, producer.getId());

            attendanceRecordForProducer.setAttendanceStatus(AttendanceStatus.PRESENT);
            attendanceRecordForProducer.setEntryTime(LocalTime.now());
            attendanceRecordForProducer.setAttendanceType(AttendanceType.QR);
            attendanceRecordForProducer.setDesignatedUser(student);
            attendanceRecordRepository.saveAndFlush(attendanceRecordForProducer);

            attendanceRepository.decreaseLimit(producer.getId(), student.getId());
        }
        return student.getId();
    }

    public List<AttendanceRecordDto> getAttendanceRecordsByCourse(Integer courseId, String group) {

        return attendanceRecordRepository.findByCourseIdAndGroup(courseId, group)
                .stream()
                .map(AttendanceRecordDtoFactory::convert)
                .toList();
    }

    public List<AttendanceRecordDto> getAttendanceRecordsByGroupForTeacher(Integer courseId, String group) {
        User teacher = personService.getCurrentUser();

        courseService.isTeacherWithoutCourse(courseId, teacher.getId());
        lessonService.isTeacherWithoutLesson(courseId, group, teacher.getId());

        return getAttendanceRecordsByCourse(courseId, group);
    }

    public List<AttendanceRecordDto> getAttendanceRecordsByCourseForStudent(Integer courseId,
                                                                            String group,
                                                                            Integer studentId) {

        courseService.isStudentWithoutCourse(courseId, studentId);
        lessonService.isStudentWithoutLesson(courseId, group, studentId);
        return attendanceRecordRepository.findByCourseGroupAndStudent(courseId, group, studentId)
                .stream()
                .map(AttendanceRecordDtoFactory::convert)
                .toList();
    }

    public List<AttendanceRecordDto> getAttendanceRecordsByCourseForStudent(Integer courseId, String group) {
        User student = personService.getCurrentUser();

        return getAttendanceRecordsByCourseForStudent(courseId, group, student.getId());
    }

    public void checkAttendanceExists(Integer attendanceId) {
        if (!attendanceRepository.existsById(attendanceId))
            throw new AttendanceNotFoundException(ExceptionMessage.attendanceNotFoundException(attendanceId));
    }

    public void isAttendanceExistsWithoutLesson(Integer lessonId, Integer attendanceId) {
        lessonService.checkLessonExists(lessonId);
        checkAttendanceExists(attendanceId);
        if (!attendanceRepository.doesAttendanceBelongsToLesson(lessonId, attendanceId))
            throw new AttendanceNotFoundException(ExceptionMessage.attendanceNotFoundWithLessonException(attendanceId, lessonId));
    }

    public Integer giveAccessToStudent(Integer courseId, String group, Integer consumerStudentId) {
        User attendanceProducerStudent = personService.getCurrentUser();
        lessonService.isStudentWithoutLesson(courseId, group, attendanceProducerStudent.getId());

        lessonService.isStudentWithoutLesson(courseId, group, consumerStudentId);

        boolean isPermissionAlreadyGiven = attendanceRecordRepository
                .existsByProducerId(attendanceProducerStudent.getId());
        if (isPermissionAlreadyGiven) {
            throw new InvalidAccessException(
                    ExceptionMessage.attendanceAccessAlreadyGiven(attendanceProducerStudent.getId()));
        }

        boolean isPermissionAlreadyTaken = attendanceRecordRepository
                .existsByConsumerId(consumerStudentId);
        if (isPermissionAlreadyTaken) {
            throw new InvalidAccessException(
                    ExceptionMessage.attendanceAccessAlreadyTaken(consumerStudentId)
            );
        }
        attendanceRepository.createPermission(attendanceProducerStudent.getId(), consumerStudentId, courseId);

        return consumerStudentId;
    }

    private QrAccessToken validateAccessToken(String accessToken) {
        String exception = ExceptionMessage.attendanceNotExistsOrHasExpiredException();
        QrAccessToken qrAccessToken = qrAccessTokenRepository.findById(accessToken)
                .orElseThrow(() -> new InvalidAccessException(
                        exception));
        if (qrAccessToken.getExpiration().isBefore(LocalDateTime.now())) {
            qrAccessTokenRepository.deleteById(accessToken);
            throw new InvalidAccessException(exception);
        }
        return qrAccessToken;
    }

    public String appeal(Integer attendanceRecordId, Reason reason, MultipartFile file) {
        User student = personService.getCurrentUser();
        AttendanceRecord attendanceRecord = attendanceRecordRepository.findById(attendanceRecordId)
                .orElseThrow(() -> new AttendanceNotFoundException("Attendance Record not found."));

        Attendance attendance = attendanceRecord.getAttendance();
        checkAttendanceExists(attendance.getId());
        lessonService.isStudentWithoutLesson(attendance.getLesson().getId(), student.getId());

        String fileId = UUID.randomUUID().toString();
        AbsenceReason absenceReason = AbsenceReason.builder()
                .reason(reason)
                .requestedDate(LocalDateTime.now())
                .attendanceRecord(attendanceRecord)
                .filePath(s3FileSavingPrefix + fileId)
                .build();
        absenceReasonRepository.save(absenceReason);
        attendanceRecord.setAbsenceReason(absenceReason);

        attendanceRecordRepository.save(attendanceRecord);
        saveToS3(fileId, file);

        return fileId;
    }

    public void saveToS3(String fileId, MultipartFile file){
        s3Service.putObject(S3Request.builder()
                .id(fileId)
                .content(file)
                .build());
    }

    public void appealAct(Integer attendanceRecordId, AbsenceReasonStatus status) {

        AttendanceRecord attendanceRecord =
                attendanceRecordRepository.findById(attendanceRecordId)
                        .orElseThrow(() -> new AttendanceNotFoundException("Attendance Record not found."));

        AbsenceReason absenceReason = attendanceRecord.getAbsenceReason();
        absenceReason.setStatus(status);
        absenceReasonRepository.save(absenceReason);
    }

    public List<AttendanceDto> getAttendancesForTeacher(Integer courseId, String group) {
        User teacher = personService.getCurrentUser();

        courseService.isTeacherWithoutCourse(courseId, teacher.getId());
        lessonService.isTeacherWithoutLesson(courseId, group, teacher.getId());
        return attendanceRepository.findByCourseGroupAndTeacher(courseId, group, teacher.getId())
                .stream()
                .map(AttendanceDtoFactory::convert)
                .toList();
    }

    public List<PersonDto> getAllStudentsByCourseGroup(Integer courseId, String group) {
        User student = personService.getCurrentUser();
        courseService.isStudentWithoutCourse(courseId, student.getId());
        lessonService.isStudentWithoutLesson(courseId, group, student.getId());

        return personService.getAllStudentsByCourseGroup(courseId, group, student.getId());
    }

    public List<AbsenceReasonDto> getAppeals(Integer courseId, String group) {

        return absenceReasonRepository.findByCourseAndGroup(courseId, group)
                .stream()
                .map(AbsenceReasonDtoFactory::convert)
                .toList();
    }
}
