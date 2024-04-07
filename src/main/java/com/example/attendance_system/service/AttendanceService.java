package com.example.attendance_system.service;

import com.example.attendance_system.dto.AttendanceDto;
import com.example.attendance_system.dto.AttendanceDtoFactory;
import com.example.attendance_system.exception.LessonNotFoundException;
import com.example.attendance_system.model.*;
import com.example.attendance_system.qr.QrCodeService;
import com.example.attendance_system.repo.AttendanceRecordRepository;
import com.example.attendance_system.repo.AttendanceRepository;
import com.example.attendance_system.repo.LessonRepository;
import com.google.zxing.WriterException;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.SetOperations;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.io.OutputStream;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class AttendanceService {
    private final QrCodeService qrCodeService;
    private final SetOperations<String, Integer> setOperations;
    private final AttendanceRepository attendanceRepository;
    private final AttendanceRecordRepository attendanceRecordRepository;
    private final LessonRepository lessonRepository;
    private final static String ACCESS_TOKEN_KEY = "Access-Token:{token}";

    public void generateQR(Integer attendanceId, OutputStream response) throws IOException, WriterException {
        String accessToken = UUID.randomUUID().toString();
        User teacher = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();

        boolean hasTeacherLesson = attendanceRepository.hasTeacherLesson(attendanceId, teacher.getId());
        if (!hasTeacherLesson)
            throw new LessonNotFoundException();

        final String finalUrl = String.format("localhost:8080/api/v1/student/attendance/take/qr/%s", accessToken);

        String accessTokenKey = getAccessTokenKey(accessToken);
        setOperations.add(accessTokenKey, attendanceId);
        qrCodeService.generateQr(finalUrl, response);
        setOperations.getOperations().expire(getAccessTokenKey(accessToken), 1, TimeUnit.MINUTES);
    }

    public Integer takeByQr(String accessToken) {
        Integer attendanceId = setOperations.randomMember(getAccessTokenKey(accessToken));

        User student = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        boolean hasStudentLesson = attendanceRepository.hasStudentLesson(attendanceId, student.getId());
        if (!hasStudentLesson) {
            throw new LessonNotFoundException();
        }

        Attendance attendance = attendanceRepository.getReferenceById(attendanceId);
        AttendanceRecord attendanceRecord = AttendanceRecord.builder()
                .attendance(attendance)
                .student(student)
                .attendanceType(AttendanceType.QR)
                .attendanceStatus(AttendanceStatus.PRESENT)
                .build();
        attendanceRecordRepository.save(attendanceRecord);
        return attendanceId;
    }

    public List<AttendanceDto> getAttendancesByLesson(Integer lessonId){
        return attendanceRepository.getAttendanceByLessonId(lessonId)
                .stream()
                .map(AttendanceDtoFactory::convertToDto)
                .collect(Collectors.toList());
    }

    public List<AttendanceDto> getAttendancesByLessonForTeacher(Integer lessonId){
        User teacher = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        if(!lessonRepository.hasTeacherLesson(lessonId, teacher.getId())){
            throw new LessonNotFoundException();
        }

        return getAttendancesByLesson(lessonId);
    }

    private String getAccessTokenKey(String token) {
        return ACCESS_TOKEN_KEY
                .replace("{token}", token);
    }

    public boolean accessTokenExpired(String accessToken) {
        return Boolean.FALSE.equals(setOperations.getOperations().hasKey(getAccessTokenKey(accessToken)));
    }
}
