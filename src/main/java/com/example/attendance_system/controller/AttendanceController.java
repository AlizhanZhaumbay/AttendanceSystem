package com.example.attendance_system.controller;

import com.example.attendance_system.dto.AttendanceDto;
import com.example.attendance_system.dto.AttendanceRecordDto;
import com.example.attendance_system.dto.AttendanceRequest;
import com.example.attendance_system.service.AttendanceService;
import com.google.zxing.WriterException;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.List;


@RestController
@RequestMapping("/api/v1")
@RequiredArgsConstructor
public class AttendanceController {
    private final AttendanceService attendanceService;

    @PostMapping(path = "/teacher/attendance/take/qr", produces = MediaType.IMAGE_JPEG_VALUE)
    @CrossOrigin
    public byte[] generateQrByAccessToken(@RequestBody AttendanceRequest attendanceRequest) throws WriterException, IOException {

        BufferedImage bufferedImage = attendanceService.generateQR(attendanceRequest);
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        ImageIO.write(bufferedImage, "jpg", byteArrayOutputStream);

        return byteArrayOutputStream.toByteArray();
    }

    @GetMapping("/student/attendance/take/qr/{access_token}")
    public ResponseEntity<String> takeAttendance(@PathVariable("access_token") String accessToken) {
        Integer attendanceId = attendanceService.takeByQr(accessToken);
        return new ResponseEntity<>(String.valueOf(attendanceId), HttpStatus.OK);
    }
    @GetMapping("/admin/attendance/lessons/{lesson_id}")
    public ResponseEntity<List<AttendanceRecordDto>> getAttendancesByLessonForAdmin(@PathVariable("lesson_id") Integer lessonId) {
        return ResponseEntity.ok(attendanceService.getAttendancesByLesson(lessonId));
    }

    @GetMapping("/teacher/attendance/lessons/{lesson_id}")
    public ResponseEntity<List<AttendanceRecordDto>> getAttendancesByLessonForTeacher(@PathVariable("lesson_id") Integer lessonId) {
        return ResponseEntity.ok(attendanceService.getAttendancesByLessonForTeacher(lessonId));
    }

    @GetMapping("/student/attendance/lessons/{lesson_id}")
    public ResponseEntity<List<AttendanceRecordDto>> getAttendancesByLessonForStudent(@PathVariable("lesson_id") Integer lessonId) {
        return ResponseEntity.ok(attendanceService.getAttendancesByLessonForStudent(lessonId));
    }


    @PostMapping("/student/attendance/lessons/{lesson_id}/students/{student_id}/give-access")
    public ResponseEntity<Integer> giveAccessToTakeAttendance(
            @PathVariable("student_id") Integer studentId,
            @PathVariable("lesson_id") Integer lessonId){
        return ResponseEntity.ok(attendanceService.giveAccessToStudent(lessonId, studentId));
    }
}
