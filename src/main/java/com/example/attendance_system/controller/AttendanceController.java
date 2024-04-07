package com.example.attendance_system.controller;

import com.example.attendance_system.dto.AttendanceDto;
import com.example.attendance_system.model.Attendance;
import com.example.attendance_system.service.AttendanceService;
import com.google.zxing.WriterException;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.util.List;


@RestController
@RequestMapping("/api/v1")
@RequiredArgsConstructor
public class AttendanceController {


    private final AttendanceService attendanceService;

    @PostMapping(path = "/teacher/attendance/{lesson_id}/take/qr", produces = MediaType.IMAGE_JPEG_VALUE)
    @CrossOrigin
    public void generateQrByAccessToken(HttpServletResponse response,
                                        @PathVariable("lesson_id") Integer lessonId) throws WriterException, IOException {

        attendanceService.generateQR(lessonId, response.getOutputStream());
        response.getOutputStream().flush();
    }

    @GetMapping("/student/attendance/take/qr/{access_token}")
    public ResponseEntity<String> takeAttendance(@PathVariable("access_token") String accessToken) {
        if (attendanceService.accessTokenExpired(accessToken))
            return new ResponseEntity<>("Invalid access token", HttpStatus.FORBIDDEN);
        Integer attendanceId = attendanceService.takeByQr(accessToken);
        return new ResponseEntity<>(String.valueOf(attendanceId), HttpStatus.OK);
    }

    @GetMapping("/teacher/attendance/{lesson_id}")
    public ResponseEntity<List<AttendanceDto>> getAttendancesByLessonForTeacher(@PathVariable("lesson_id") Integer lessonId){
        return ResponseEntity.ok(attendanceService.getAttendancesByLessonForTeacher(lessonId));
    }

    @GetMapping("/admin/attendance/{lesson_id}")
    public ResponseEntity<List<AttendanceDto>> getAttendancesByLessonForAdmin(@PathVariable("lesson_id") Integer lessonId){
        return ResponseEntity.ok(attendanceService.getAttendancesByLesson(lessonId));
    }
}
