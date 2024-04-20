package com.example.attendance_system.controller;

import com.example.attendance_system.dto.AttendanceRecordDto;
import com.example.attendance_system.dto.AttendanceRequest;
import com.example.attendance_system.service.AttendanceService;
import com.google.zxing.WriterException;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.parameters.P;
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
    public ResponseEntity<Integer> takeAttendance(@PathVariable("access_token") String accessToken) {
        Integer studentId = attendanceService.takeByQr(accessToken);
        return new ResponseEntity<>(studentId, HttpStatus.OK);
    }
    @GetMapping("/admin/attendance/courses/{course_id}/{group}")
    public ResponseEntity<List<AttendanceRecordDto>> getAttendanceRecordsByGroupForAdmin(
            @PathVariable("course_id") Integer courseId,
            @PathVariable("group") String group) {
        return ResponseEntity.ok(attendanceService.getAttendanceRecordsByGroup(courseId, group));
    }

    @GetMapping("/teacher/attendance/courses/{course_id}/{group}")
    public ResponseEntity<List<AttendanceRecordDto>> getAttendanceRecordsByLessonForTeacher(
            @PathVariable("course_id") Integer courseId,
            @PathVariable("group") String group) {
        return ResponseEntity.ok(attendanceService.getAttendanceRecordsByGroupForTeacher(courseId, group));
    }

    @GetMapping("/student/attendance/courses/{course_id}/{group}")
    public ResponseEntity<List<AttendanceRecordDto>> getAttendanceRecordsByLessonForStudent(
            @PathVariable("course_id") Integer courseId,
            @PathVariable("group") String group) {
        return ResponseEntity.ok(attendanceService.getAttendanceRecordsByGroupForStudent(courseId, group));
    }


    @PostMapping("/student/attendance/courses/{course_id}/{code}/students/{student_id}/give-access")
    public ResponseEntity<Integer> giveAccessToTakeAttendance(
            @PathVariable("student_id") Integer studentId,
            @PathVariable("course_id") Integer courseId,
            @PathVariable("code") String code){
        return ResponseEntity.ok(attendanceService.giveAccessToStudent(courseId, code, studentId));
    }
}
