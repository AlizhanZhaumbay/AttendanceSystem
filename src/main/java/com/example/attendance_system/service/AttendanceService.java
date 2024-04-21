package com.example.attendance_system.service;

import com.example.attendance_system.dto.AttendanceRecordDto;
import com.example.attendance_system.dto.AttendanceRequest;
import com.example.attendance_system.exception.AttendanceNotFoundException;
import com.example.attendance_system.exception.InvalidAccessException;
import com.example.attendance_system.model.*;
import com.example.attendance_system.qr.QrCodeService;
import com.example.attendance_system.repo.AttendanceRecordRepository;
import com.example.attendance_system.repo.AttendanceRepository;
import com.example.attendance_system.repo.QrAccessTokenRepository;
import com.example.attendance_system.repo.UserRepository;
import com.example.attendance_system.util.AttendanceRecordDtoFactory;
import com.example.attendance_system.util.ExceptionMessage;
import com.example.attendance_system.util.ObjectValidator;
import com.google.zxing.WriterException;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.awt.image.BufferedImage;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class AttendanceService {
    private final QrCodeService qrCodeService;
    private final UserRepository userRepository;
    //    private final HashOperations<String, String, Integer> hashOperations;
    private final AttendanceRepository attendanceRepository;
    private final AttendanceRecordRepository attendanceRecordRepository;
    private final QrAccessTokenRepository qrAccessTokenRepository;
    private final LessonService lessonService;
    private final UserService userService;

    private final ObjectValidator<AttendanceRequest> objectValidator;

    @Value("${application.endpoints.attendance.student.take}")
    private String studentAttendanceTakeEndpointPrefix;

    public BufferedImage generateQR(AttendanceRequest attendanceRequest) throws WriterException {
        objectValidator.validate(attendanceRequest);

        Integer lessonId = attendanceRequest.getLessonId();
        Integer attendanceId = attendanceRequest.getAttendanceId();

        isAttendanceExistsWithoutLesson(lessonId, attendanceId);


        User teacher = userService.getCurrentUser();
        lessonService.isTeacherWithoutLesson(lessonId, teacher.getId());

        String accessToken = UUID.randomUUID().toString();
        final String finalUrl = String.format("%s/%s", studentAttendanceTakeEndpointPrefix, accessToken);


        Lesson lesson = lessonService.getLessonById(lessonId);
        List<User> students = lesson.getLessonStudents();
        Attendance attendance = attendanceRepository.getReferenceById(attendanceId);

        List<AttendanceRecord> attendanceRecords = attendanceRecordRepository.findByAttendance(attendance);

        if(attendanceRecords == null || attendanceRecords.isEmpty()){
            students.forEach(student ->
                    attendanceRecordRepository.save(
                            AttendanceRecord.builder()
                                    .attendance(attendance)
                                    .student(student)
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


        User student = userService.getCurrentUser();

        lessonService.isStudentWithoutLesson(lessonId, student.getId());

        AttendanceRecord attendanceRecord = attendanceRecordRepository
                .findByAttendanceIdAndStudentId(attendanceId, student.getId());

        attendanceRecord.setAttendanceType(AttendanceType.QR);
        attendanceRecord.setAttendanceStatus(AttendanceStatus.PRESENT);
        attendanceRecordRepository.saveAndFlush(attendanceRecord);

        boolean haveAccess = attendanceRecordRepository.checkStudentHaveAccessesForLesson(student.getId(), lessonId);
        if (haveAccess) {
            User producer = userRepository.findProducerByConsumerId(student.getId());

            AttendanceRecord attendanceRecordForProducer = attendanceRecordRepository
                    .findByAttendanceIdAndStudentId(attendanceId, producer.getId());

            attendanceRecordForProducer.setAttendanceStatus(AttendanceStatus.PRESENT);
            attendanceRecordForProducer.setAttendanceType(AttendanceType.QR);
            attendanceRecordForProducer.setAttendanceStatus(AttendanceStatus.PRESENT);
            attendanceRecordForProducer.setDesignatedPerson(student);
            attendanceRecordRepository.saveAndFlush(attendanceRecordForProducer);

        }
        return student.getId();
    }

    public List<AttendanceRecordDto> getAttendanceRecordsByGroup(Integer courseId, String group) {
        lessonService.checkLessonExistsWithCourseAndGroup(courseId, group);

        return attendanceRecordRepository.findByCourseIdAndGroup(courseId, group)
                .stream()
                .map(AttendanceRecordDtoFactory::convertToDto)
                .toList();
    }

    public List<AttendanceRecordDto> getAttendanceRecordsByGroupForTeacher(Integer courseId, String group) {
        User teacher = userService.getCurrentUser();

        lessonService.isTeacherWithoutLesson(courseId, group, teacher.getId());

        return getAttendanceRecordsByGroup(courseId, group);
    }

    public List<AttendanceRecordDto> getAttendanceRecordsByGroupForStudent(Integer courseId, String group) {
        User student = userService.getCurrentUser();

        lessonService.isStudentWithoutLesson(courseId, group, student.getId());

        return attendanceRecordRepository.findByCourseIdAndGroupAndStudent(courseId, group, student.getId())
                .stream()
                .map(AttendanceRecordDtoFactory::convertToDto)
                .toList();
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
        User attendanceProducerStudent = userService.getCurrentUser();
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
}
