package com.example.attendance_system.controller;

import com.example.attendance_system.dto.AttendanceDto;
import com.example.attendance_system.dto.AttendanceRecordDto;
import com.example.attendance_system.dto.AttendanceRequest;
import com.example.attendance_system.model.AbsenceReasonStatus;
import com.example.attendance_system.service.AttendanceService;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.google.zxing.WriterException;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.List;


@RestController
@CrossOrigin
@RequiredArgsConstructor
public class AttendanceController {
    private final AttendanceService attendanceService;

    public static final String TEACHER_TAKE_ATTENDANCE_BY_QR =
            "/api/v1/teacher/attendance/take/qr";
    public static final String STUDENT_PASS_ATTENDANCE_BY_QR =
            "/api/v1/student/attendance/take/qr/{access_token}";
    public static final String ADMIN_SEE_ATTENDANCE_RECORDS =
            "/api/v1/admin/attendance/courses/{course_id}";
    public static final String TEACHER_SEE_ATTENDANCE_RECORDS =
            "/api/v1/teacher/attendance/courses/{course_id}";
    public static final String STUDENT_SEE_ATTENDANCE_RECORDS =
            "/api/v1/student/attendance/courses/{course_id}";
    public static final String STUDENT_ATTENDANCE_GIVE_PERMISSION =
            "/api/v1/student/attendance/courses/{course_id}/{code}/students/{student_id}/give-access";
    public static final String ADMIN_ATTENDANCE_APPEAL_ACCEPT =
            "/api/v1/admin/attendance/{attendance_record_id}/appeals/accept";
    public static final String ADMIN_ATTENDANCE_APPEAL_DENY =
            "/api/v1/admin/attendance/{attendance_record_id}/appeals/deny";
    public static final String STUDENT_ATTENDANCE_APPEAL =
            "/api/v1/student/attendance/{attendance_record_id}/appeals";

    public static final String TEACHER_SET_ATTENDANCE_LIST =
            "/api/v1/teacher/attendance/take/courses/{course_id}";

    @PostMapping(path = TEACHER_TAKE_ATTENDANCE_BY_QR, produces = MediaType.IMAGE_JPEG_VALUE)
    @CrossOrigin
    public byte[] generateQrByAccessToken(@RequestBody AttendanceRequest attendanceRequest) throws WriterException, IOException {

        BufferedImage bufferedImage = attendanceService.generateQR(attendanceRequest);
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        ImageIO.write(bufferedImage, "jpg", byteArrayOutputStream);

        return byteArrayOutputStream.toByteArray();
    }

    @GetMapping(STUDENT_PASS_ATTENDANCE_BY_QR)
    public ResponseEntity<Integer> passAttendance(@PathVariable("access_token") String accessToken) {
        Integer studentId = attendanceService.takeByQr(accessToken);
        return new ResponseEntity<>(studentId, HttpStatus.OK);
    }

    @GetMapping(ADMIN_SEE_ATTENDANCE_RECORDS)
    @JsonInclude(JsonInclude.Include.NON_NULL)
    public ResponseEntity<List<AttendanceRecordDto>> getAttendanceRecordsByGroupForAdmin(
            @PathVariable("course_id") Integer courseId) {
        return ResponseEntity.ok(attendanceService.getAttendanceRecordsByCourse(courseId));
    }

    @GetMapping(TEACHER_SEE_ATTENDANCE_RECORDS)
    @JsonInclude(JsonInclude.Include.NON_NULL)
    public ResponseEntity<List<AttendanceRecordDto>> getAttendanceRecordsByLessonForTeacher(
            @PathVariable("course_id") Integer courseId) {
        return ResponseEntity.ok(attendanceService.getAttendanceRecordsByGroupForTeacher(courseId));
    }

    @GetMapping(STUDENT_SEE_ATTENDANCE_RECORDS)
    @JsonInclude(JsonInclude.Include.NON_NULL)
    public ResponseEntity<List<AttendanceRecordDto>> getAttendanceRecordsByLessonForStudent(
            @PathVariable("course_id") Integer courseId) {
        return ResponseEntity.ok(attendanceService.getAttendanceRecordsByGroupForStudent(courseId));
    }


    @PostMapping(STUDENT_ATTENDANCE_GIVE_PERMISSION)
    public ResponseEntity<Integer> giveAccessToTakeAttendance(
            @PathVariable("student_id") Integer studentId,
            @PathVariable("course_id") Integer courseId,
            @PathVariable("code") String code) {
        return ResponseEntity.ok(attendanceService.giveAccessToStudent(courseId, code, studentId));
    }

    @PostMapping(ADMIN_ATTENDANCE_APPEAL_ACCEPT)
    public ResponseEntity<String> attendanceAcceptAppeal(
            @PathVariable("attendance_record_id") Integer attendanceRecordId) {
        attendanceService.appealAccept(attendanceRecordId, AbsenceReasonStatus.APPROVED);
        return ResponseEntity.ok("Success");
    }

    @PostMapping(ADMIN_ATTENDANCE_APPEAL_DENY)
    public ResponseEntity<String> attendanceDenyAppeal(
            @PathVariable("attendance_record_id") Integer attendanceRecordId) {
        attendanceService.appealAccept(attendanceRecordId, AbsenceReasonStatus.DENIED);
        return ResponseEntity.ok("Success");
    }

    @PostMapping(value = STUDENT_ATTENDANCE_APPEAL,
            consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<String> attendanceAppeal(
            @PathVariable("attendance_record_id") Integer attendanceRecordId,
            @RequestParam("reason") String description,
            @RequestParam("file") MultipartFile multipartFile) {
        return ResponseEntity.ok(
                attendanceService.appeal(attendanceRecordId, description, multipartFile));
    }

    @GetMapping(TEACHER_SET_ATTENDANCE_LIST)
    public ResponseEntity<List<AttendanceDto>> setAttendanceList(@PathVariable("course_id") Integer courseId){
        return ResponseEntity.ok(attendanceService.getAttendancesForTeacher(courseId));
    }

}
