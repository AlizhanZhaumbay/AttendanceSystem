package com.example.attendance_system;

import com.example.attendance_system.auth.AuthenticationController;
import com.example.attendance_system.auth.AuthenticationResponse;
import com.example.attendance_system.auth.RegisterRequest;
import com.example.attendance_system.controller.AttendanceController;
import com.example.attendance_system.controller.PersonController;
import com.example.attendance_system.dto.AttendanceRecordDto;
import com.example.attendance_system.dto.AttendanceRequest;
import com.example.attendance_system.model.*;
import com.example.attendance_system.qr.QrCodeService;
import com.example.attendance_system.repo.*;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.transaction.Transactional;
import lombok.SneakyThrows;
import lombok.extern.log4j.Log4j2;
import net.datafaker.Faker;
import net.datafaker.providers.base.DateAndTime;
import net.datafaker.providers.base.Internet;
import net.datafaker.providers.base.Name;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;

import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.Collections;
import java.util.List;
import java.util.Random;
import java.util.concurrent.ThreadLocalRandom;
import java.util.function.Supplier;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@Log4j2
@AutoConfigureMockMvc(printOnlyOnFailure = false)
public class AttendanceTests {

    @Autowired
    MockMvc mockMvc;

    @Autowired
    QrCodeService qrCodeService;

    @Autowired
    JdbcTemplate jdbcTemplate;

    @Autowired
    AttendanceRecordRepository attendanceRecordRepository;

    @Autowired
    TokenRepository tokenRepository;

    @Autowired
    PersonRepository personRepository;


    private final Faker faker = new Faker();
    private final ObjectMapper objectMapper = new ObjectMapper();
    private final String BASE_URL = "http://localhost:8080";

    @Autowired
    private CourseRepository courseRepository;

    @Autowired
    private LessonRepository lessonRepository;

    @Autowired
    private AttendanceRepository attendanceRepository;

    @Autowired
    private UserRepository userRepository;
    @Autowired
    private AbsenceReasonRepository absenceReasonRepository;


    @Test
    @Transactional
    @DirtiesContext
    @SneakyThrows
    void teacherTakeAttendanceByQrTest() {
        String teacherAccessToken = getAccessToken(Role.TEACHER);
        User teacher = getUserByAccessToken(teacherAccessToken);

        Course course = saveCourse();

        String group = "01-P";
        Lesson lesson = saveLesson(teacher, course, Collections.emptyList(), group);
        Attendance attendance = attendanceRepository.save(
                Attendance.builder()
                        .lesson(lesson)
                        .id(10)
                        .localDateTime(LocalDateTime.now())
                        .build()
        );
        createAttendanceQr(teacherAccessToken, attendance, lesson);
        log.info("Attendance Records {}", attendanceRecordRepository.findAll());
    }

    @Test
    @Transactional
    @DirtiesContext
    @SneakyThrows
    void studentTakeAttendanceByQrTest() {
        createTable();
        String teacherAccessToken = getAccessToken(Role.TEACHER);
        User teacher = getUserByAccessToken(teacherAccessToken);

        String student1AccessToken = getAccessToken(Role.STUDENT);
        User student1 = getUserByAccessToken(student1AccessToken);


        User student2 = getUser(Role.STUDENT);
        userRepository.save(student2);

        Course course = saveCourse();

        String group = "01-P";
        List<User> students = List.of(student1, student2);
        Lesson lesson = saveLesson(teacher, course, students, group);
        Attendance attendance = attendanceRepository.save(
                Attendance.builder()
                        .lesson(lesson)
                        .id(10)
                        .localDateTime(LocalDateTime.now())
                        .build()
        );
        byte[] qr = createAttendanceQr(teacherAccessToken, attendance, lesson);
        String url = qrCodeService.decodeQr(qr);

        String qrAccessToken = fetchAccessToken(url);
        var studentTakeAttendanceRequest = getRequestBuilder(
                "GET", AttendanceController.STUDENT_PASS_ATTENDANCE_BY_QR
                        .replace("{access_token}", qrAccessToken), student1AccessToken, null);
        mockMvc.perform(studentTakeAttendanceRequest)
                .andExpectAll(
                        status().isOk(),
                        content().string(String.valueOf(student1.getId()))
                );

        var result = mockMvc.perform(getRequestBuilder("GET",
                AttendanceController.TEACHER_SEE_ATTENDANCE_RECORDS
                        .replace("{course_id}", String.valueOf(course.getId()))
                        .replace("{group}", group),
                teacherAccessToken, null
        ))
                .andExpectAll(status().isOk())
                .andReturn();

        compareForEquationResponseLengthAndExpectedLength(result, 2);
        log.info("Attendance Records {}", attendanceRecordRepository.findAll());
    }

    @Test
    @Transactional
    @DirtiesContext
    @SneakyThrows
    void studentTakeAttendanceByQrTestShouldFail() {
        createTable();
        String teacherAccessToken = getAccessToken(Role.TEACHER);
        User teacher = getUserByAccessToken(teacherAccessToken);

        String student1AccessToken = getAccessToken(Role.STUDENT);
        User student = getUserByAccessToken(student1AccessToken);

        String notRegisteredUserAccessToken = getAccessToken(Role.STUDENT);
        Course course = saveCourse();

        String group = "01-P";
        List<User> students = List.of(student);
        Lesson lesson = saveLesson(teacher, course, students, group);
        Attendance attendance = attendanceRepository.save(
                Attendance.builder()
                        .lesson(lesson)
                        .id(10)
                        .localDateTime(LocalDateTime.now())
                        .build()
        );
        byte[] qr = createAttendanceQr(teacherAccessToken, attendance, lesson);
        String url = qrCodeService.decodeQr(qr);

        String qrAccessToken = fetchAccessToken(url);
        var studentTakeAttendanceRequest = getRequestBuilder(
                "GET", AttendanceController.STUDENT_PASS_ATTENDANCE_BY_QR
                        .replace("{access_token}", qrAccessToken), notRegisteredUserAccessToken, null);
        mockMvc.perform(studentTakeAttendanceRequest)
                .andExpectAll(
                        status().isForbidden()
                );
    }


    @Test
    @Transactional
    @DirtiesContext
    @SneakyThrows
    void seeAttendancesForAdminTest() {
        String adminAccessToken = getAccessToken(Role.ADMIN);

        User student1 = userRepository.save(getUser(Role.STUDENT));
        User student2 = userRepository.save(getUser(Role.STUDENT));
        User student3 = userRepository.save(getUser(Role.STUDENT));

        User teacher = userRepository.save(getUser(Role.TEACHER));

        Course course = saveCourse();
        String group = "02-n";
        String anotherGroup = "03-n";
        Lesson lesson1 = saveLesson(teacher, course, List.of(student1, student2, student3), group);
        Lesson lesson2 = saveLesson(teacher, course, List.of(student1, student2, student3), group);
        Lesson lesson3 = saveLesson(teacher, course, List.of(student1, student2, student3), anotherGroup);

        Attendance attendance1 = attendanceRepository.save(getAttendance(lesson1));
        Attendance attendance2 = attendanceRepository.save(getAttendance(lesson2));
        Attendance attendance3 = attendanceRepository.save(getAttendance(lesson3));
        attendanceRecordRepository.saveAll(
                List.of(
                        getAttendanceRecord(AttendanceType.QR, student1, attendance1, AttendanceStatus.PRESENT),
                        getAttendanceRecord(AttendanceType.MANUAL, student2, attendance1, AttendanceStatus.PRESENT),
                        getAttendanceRecord(null, student3, attendance1, AttendanceStatus.ABSENT),

                        getAttendanceRecord(null, student1, attendance2, AttendanceStatus.ABSENT),
                        getAttendanceRecord(null, student2, attendance2, AttendanceStatus.ABSENT),
                        getAttendanceRecord(AttendanceType.QR, student3, attendance2, AttendanceStatus.PRESENT),


                        getAttendanceRecord(null, student1, attendance3, AttendanceStatus.PRESENT),
                        getAttendanceRecord(null, student3, attendance3, AttendanceStatus.EXCUSE_ABSENCE)));


        var postfix = AttendanceController.ADMIN_SEE_ATTENDANCE_RECORDS
                .replace("{course_id}", String.valueOf(course.getId()))
                .replace("{group}", group);
        var requestBuilder = getRequestBuilder("GET", postfix, adminAccessToken, null);

        List<AttendanceRecordDto> attendanceRecords = objectMapper.readValue(
                mockMvc.perform(requestBuilder)
                        .andExpectAll(
                                status().isOk(),
                                content().contentType(MediaType.APPLICATION_JSON)
                        ).andReturn().getResponse().getContentAsString(),
                List.class
        );
        assertEquals(attendanceRecords.size(), 6);
    }


    @Test
    @Transactional
    @DirtiesContext
    @SneakyThrows
    void seeAttendancesForStudentTest() {
        String student1AccessToken = getAccessToken(Role.STUDENT);

        User student1 = getUserByAccessToken(student1AccessToken);
        User student2 = userRepository.save(getUser(Role.STUDENT));
        User student3 = userRepository.save(getUser(Role.STUDENT));

        User teacher = userRepository.save(getUser(Role.TEACHER));

        Course course = saveCourse();
        String group = "02-n";
        String anotherGroup = "01-n";
        Lesson lesson1 = saveLesson(teacher, course, List.of(student1, student2, student3), group);
        Lesson lesson2 = saveLesson(teacher, course, List.of(student1, student2, student3), group);
        Lesson lesson3 = saveLesson(teacher, course, List.of(student3, student2), anotherGroup);

        Attendance attendance1 = attendanceRepository.save(getAttendance(lesson1));
        Attendance attendance2 = attendanceRepository.save(getAttendance(lesson2));
        Attendance attendance3 = attendanceRepository.save(getAttendance(lesson3));
        attendanceRecordRepository.saveAll(
                List.of(
                        getAttendanceRecord(AttendanceType.QR, student1, attendance1, AttendanceStatus.PRESENT),
                        getAttendanceRecord(AttendanceType.MANUAL, student2, attendance1, AttendanceStatus.PRESENT),
                        getAttendanceRecord(null, student3, attendance1, AttendanceStatus.ABSENT),

                        getAttendanceRecord(null, student1, attendance2, AttendanceStatus.ABSENT),
                        getAttendanceRecord(null, student2, attendance2, AttendanceStatus.ABSENT),
                        getAttendanceRecord(AttendanceType.QR, student3, attendance2, AttendanceStatus.PRESENT),

                        getAttendanceRecord(null, student3, attendance3, AttendanceStatus.ABSENT),
                        getAttendanceRecord(null, student2, attendance3, AttendanceStatus.PRESENT)));


        var postfix = AttendanceController.STUDENT_SEE_ATTENDANCE_RECORDS
                .replace("{course_id}", String.valueOf(course.getId()))
                .replace("{group}", group);
        var requestBuilder = getRequestBuilder("GET", postfix, student1AccessToken, null);

        MvcResult mvcResult = mockMvc.perform(requestBuilder)
                .andExpectAll(
                        status().isOk(),
                        content().contentType(MediaType.APPLICATION_JSON)
                ).andReturn();

        compareForEquationResponseLengthAndExpectedLength(mvcResult, 2);
    }

    @Test
    @Transactional
    @DirtiesContext
    @SneakyThrows
    void seeAttendancesForTeacherTest() {

        User student1 = userRepository.save(getUser(Role.STUDENT));
        User student2 = userRepository.save(getUser(Role.STUDENT));
        User student3 = userRepository.save(getUser(Role.STUDENT));

        String teacherAccessToken = getAccessToken(Role.TEACHER);
        User teacher = getUserByAccessToken(teacherAccessToken);

        Course course = saveCourse();
        String group = "02-n";
        String anotherGroup = "01-n";
        Lesson lesson1 = saveLesson(teacher, course, List.of(student1, student2, student3), group);
        Lesson lesson2 = saveLesson(teacher, course, List.of(student1, student2, student3), group);
        Lesson lesson3 = saveLesson(teacher, course, List.of(student3, student2), anotherGroup);

        Attendance attendance1 = attendanceRepository.save(getAttendance(lesson1));
        Attendance attendance2 = attendanceRepository.save(getAttendance(lesson2));
        Attendance attendance3 = attendanceRepository.save(getAttendance(lesson3));
        attendanceRecordRepository.saveAll(
                List.of(
                        getAttendanceRecord(AttendanceType.QR, student1, attendance1, AttendanceStatus.PRESENT),
                        getAttendanceRecord(AttendanceType.MANUAL, student2, attendance1, AttendanceStatus.PRESENT),
                        getAttendanceRecord(null, student3, attendance1, AttendanceStatus.ABSENT),

                        getAttendanceRecord(null, student1, attendance2, AttendanceStatus.ABSENT),
                        getAttendanceRecord(null, student2, attendance2, AttendanceStatus.ABSENT),
                        getAttendanceRecord(AttendanceType.QR, student3, attendance2, AttendanceStatus.PRESENT),
                        getAttendanceRecord(null, student3, attendance3, AttendanceStatus.ABSENT),
                        getAttendanceRecord(null, student2, attendance3, AttendanceStatus.PRESENT)));


        var postfix = AttendanceController.TEACHER_SEE_ATTENDANCE_RECORDS
                .replace("{course_id}", String.valueOf(course.getId()))
                .replace("{group}", group);
        var requestBuilder = getRequestBuilder("GET", postfix, teacherAccessToken, null);

        MvcResult mvcResult = mockMvc.perform(requestBuilder)
                .andExpectAll(
                        status().isOk(),
                        content().contentType(MediaType.APPLICATION_JSON)
                ).andReturn();

        compareForEquationResponseLengthAndExpectedLength(mvcResult, 6);
    }

    @Test
    @Transactional
    @DirtiesContext
    @SneakyThrows
    void seeStudentsListToSeeAttendanceRecords() {
        User student1 = userRepository.save(getUser(Role.STUDENT));
        User student2 = userRepository.save(getUser(Role.STUDENT));

        student1.getPerson().setUser(student1);
        student2.getPerson().setUser(student2);

        String teacherAccessToken = getAccessToken(Role.TEACHER);
        User teacher = getUserByAccessToken(teacherAccessToken);


        Course course = saveCourse();
        String group = "02-n";
        saveLesson(teacher, course, List.of(student1, student2), group);

        var postfix = PersonController.TEACHER_FETCH_STUDENTS
                .replace("{course_id}", String.valueOf(course.getId()))
                .replace("{group}", group);

        var requestBuilder = getRequestBuilder("GET", postfix, teacherAccessToken, null);

        MvcResult mvcResult = mockMvc.perform(requestBuilder).andReturn();
        compareForEquationResponseLengthAndExpectedLength(mvcResult, 2);
    }


    @Test
    @Transactional
    @DirtiesContext
    @SneakyThrows
    void seeAttendancesOfStudentForTeacherTest() {

        User student1 = userRepository.save(getUser(Role.STUDENT));
        User student2 = userRepository.save(getUser(Role.STUDENT));

        String teacherAccessToken = getAccessToken(Role.TEACHER);
        User teacher = getUserByAccessToken(teacherAccessToken);


        Course course = saveCourse();
        String group = "02-n";
        Lesson lesson = saveLesson(teacher, course, List.of(student1, student2), group);

        Attendance attendance1 = attendanceRepository.save(getAttendance(lesson));
        Attendance attendance2 = attendanceRepository.save(getAttendance(lesson));
        Attendance attendance3 = attendanceRepository.save(getAttendance(lesson));
        attendanceRecordRepository.saveAll(
                List.of(
                        getAttendanceRecord(AttendanceType.QR, student1, attendance1, AttendanceStatus.PRESENT),
                        getAttendanceRecord(AttendanceType.MANUAL, student2, attendance1, AttendanceStatus.PRESENT),

                        getAttendanceRecord(null, student1, attendance2, AttendanceStatus.ABSENT),
                        getAttendanceRecord(AttendanceType.MANUAL, student2, attendance2, AttendanceStatus.PRESENT),

                        getAttendanceRecord(null, student1, attendance3, AttendanceStatus.ABSENT),
                        getAttendanceRecord(null, student2, attendance3, AttendanceStatus.ABSENT)
                ));


        var postfix = AttendanceController.TEACHER_SEE_STUDENTS_ATTENDANCE_RECORDS
                .replace("{course_id}", String.valueOf(course.getId()))
                .replace("{group}", group)
                .replace("{student_id}", String.valueOf(student1.getId()));
        var requestBuilder = getRequestBuilder("GET", postfix, teacherAccessToken, null);

        MvcResult mvcResult = mockMvc.perform(requestBuilder)
                .andExpectAll(
                        status().isOk(),
                        content().contentType(MediaType.APPLICATION_JSON)
                ).andReturn();

        compareForEquationResponseLengthAndExpectedLength(mvcResult, 3);
    }


    @Test
    @Transactional
    @DirtiesContext
    @SneakyThrows
    void giveAttendanceAccessForAnotherStudentTest() {
        createTable();
        String student1AccessToken = getAccessToken(Role.STUDENT);
        String student2AccessToken = getAccessToken(Role.STUDENT);
        User student1 = getUserByAccessToken(student1AccessToken);
        User student2 = getUserByAccessToken(student2AccessToken);

        String teacherAccessToken = getAccessToken(Role.TEACHER);
        User teacher = getUserByAccessToken(teacherAccessToken);

        String group = "02-P";

        Course course = saveCourse();
        Lesson lesson =
                saveLesson(teacher, course, List.of(student1, student2), group);

        Attendance attendance = attendanceRepository.save(getAttendance(lesson));

        var postfixGiveAccess = String.format(
                        AttendanceController.STUDENT_ATTENDANCE_GIVE_PERMISSION
                                .replace("{course_id}", String.valueOf(course.getId()))
                                .replace("{group}", group)
                                .replace("{student_id}", String.valueOf(student2.getId())));
        var requestBuilderGiveAccess =
                getRequestBuilder("POST", postfixGiveAccess, student1AccessToken, null);

        mockMvc.perform(requestBuilderGiveAccess)
                .andExpectAll(
                        status().isOk()
                );

        byte[] qr = createAttendanceQr(teacherAccessToken, attendance, lesson);
        String url = AttendanceController.STUDENT_PASS_ATTENDANCE_BY_QR
                .replace("{access_token}", fetchAccessToken(qrCodeService.decodeQr(qr)));
        var requestBuilderAttend =
                get(url)
                        .header("Authorization", getHeaderAuthorization(student2AccessToken));
        mockMvc.perform(requestBuilderAttend)
                .andExpectAll(
                        status().isOk(),
                        content().string(String.valueOf(student2.getId()))
                );
        var teacherSeeAttendancePostfix = AttendanceController.TEACHER_SEE_ATTENDANCE_RECORDS
                .replace("{course_id}", String.valueOf(course.getId()))
                .replace("{group}", group);
        var seeAttendanceRecordsRequestBuilder = getRequestBuilder("GET",
                teacherSeeAttendancePostfix,
                teacherAccessToken, null);
        mockMvc.perform(
                seeAttendanceRecordsRequestBuilder
        ).andExpectAll(
                status().isOk(),
                content().contentType(MediaType.APPLICATION_JSON)
        );
    }

    @Test
    @Transactional
    @DirtiesContext
    @SneakyThrows
    void attendancePermissionLimitHasBeenReachedTest() {
        createTable();
        String student1AccessToken = getAccessToken(Role.STUDENT);
        String student2AccessToken = getAccessToken(Role.STUDENT);
        User student1 = getUserByAccessToken(student1AccessToken);
        User student2 = getUserByAccessToken(student2AccessToken);

        String teacherAccessToken = getAccessToken(Role.TEACHER);
        User teacher = getUserByAccessToken(teacherAccessToken);

        String group = "02-P";

        Course course = saveCourse();
        Lesson lesson =
                saveLesson(teacher, course, List.of(student1, student2), group);

        Attendance attendance = attendanceRepository.save(getAttendance(lesson));

        var postfixGiveAccess = String.format(
                        AttendanceController.STUDENT_ATTENDANCE_GIVE_PERMISSION
                                .replace("{course_id}", String.valueOf(course.getId()))
                                .replace("{group}", group))
                .replace("{student_id}", String.valueOf(student2.getId()));
        var requestBuilderGiveAccess =
                getRequestBuilder("POST", postfixGiveAccess, student1AccessToken, null);

        mockMvc.perform(requestBuilderGiveAccess)
                .andExpectAll(
                        status().isOk()
                );

        attendanceRepository.resetLimit(student1.getId(), student2.getId());

        byte[] qr = createAttendanceQr(teacherAccessToken, attendance, lesson);
        String url = AttendanceController.STUDENT_PASS_ATTENDANCE_BY_QR
                .replace("{access_token}", fetchAccessToken(qrCodeService.decodeQr(qr)));
        var requestBuilderAttend =
                get(url)
                        .header("Authorization", getHeaderAuthorization(student2AccessToken));
        mockMvc.perform(requestBuilderAttend)
                .andExpectAll(
                        status().isOk(),
                        content().string("Unable to take attendance for designated user, limit has been reached")
                );
    }

    @Test
    @Transactional
    @DirtiesContext
    @SneakyThrows
    void teacherTakeAttendanceList() {
        String teacherAccessToken = getAccessToken(Role.TEACHER);
        User teacher = getUserByAccessToken(teacherAccessToken);

        Course course = saveCourse();
        String group = "02-n";
        String anotherGroup = "03-n";
        Lesson lesson1 = saveLesson(teacher, course, null, group);
        Lesson lesson2 = saveLesson(teacher, course, null, anotherGroup);

        List<Attendance> attendances = List.of(
                getAttendance(lesson1),
                getAttendance(lesson1),
                getAttendance(lesson1),
                getAttendance(lesson2));

        attendanceRepository.saveAll(attendances);
        var requestBuilder =
                get(AttendanceController.TEACHER_SET_ATTENDANCE_LIST
                        .replace("{course_id}", String.valueOf(course.getId()))
                        .replace("{group}", group))
                        .header("Authorization", getHeaderAuthorization(teacherAccessToken));
        MvcResult mvcResult = mockMvc.perform(requestBuilder)
                .andExpectAll(
                        status().isOk(),
                        content().contentType(MediaType.APPLICATION_JSON)
                ).andReturn();

        compareForEquationResponseLengthAndExpectedLength(mvcResult, 3);

    }

    @Test
    @Transactional
    @DirtiesContext
    @SneakyThrows
    void seeStudentListToGivePermission() {
        createTable();
        String student1AccessToken = getAccessToken(Role.STUDENT);
        User student1 = getUserByAccessToken(student1AccessToken);
        setPerson(student1);

        User student2 = getUser(Role.STUDENT);
        setPerson(student2);
        User student3 = getUser(Role.STUDENT);
        setPerson(student3);
        User student4 = getUser(Role.STUDENT);
        setPerson(student4);
        userRepository.saveAll(List.of(student2, student3, student4));

        String group = "02-P";
        String anotherGroup = "01-P";

        Course course = saveCourse();
        saveLesson(null, course, List.of(student1, student2, student3), group);
        saveLesson(null, course, List.of(student1, student4), anotherGroup);
        var postfixGiveAccess = AttendanceController.STUDENTS_LIST_TO_GIVE_PERMISSION
                .replace("{course_id}", String.valueOf(course.getId()))
                .replace("{group}", anotherGroup);
        var requestBuilderGiveAccess =
                getRequestBuilder("GET", postfixGiveAccess, student1AccessToken, null);

        MvcResult mvcResult = mockMvc.perform(requestBuilderGiveAccess)
                .andExpectAll(
                        status().isOk(),
                        content().contentType(MediaType.APPLICATION_JSON)
                ).andReturn();
        compareForEquationResponseLengthAndExpectedLength(mvcResult, 1);
    }

    @Test
    @Transactional
    @DirtiesContext
    @SneakyThrows
    void studentAppealTest() {

        String studentAccessToken = getAccessToken(Role.STUDENT);
        User student = getUserByAccessToken(studentAccessToken);

        String group = "02-P";

        Course course = saveCourse();
        Lesson lesson = saveLesson(null, course, List.of(student), group);
        Attendance attendance = attendanceRepository.save(getAttendance(lesson));
        AttendanceRecord attendanceRecord =
                attendanceRecordRepository.save(
                        getAttendanceRecord(null, student, attendance, AttendanceStatus.ABSENT));


        var postfixGiveAccess = AttendanceController.STUDENT_ATTENDANCE_APPEAL
                .replace("{attendance_record_id}", String.valueOf(attendanceRecord.getId()));

        var requestBuilder = multipart(
                BASE_URL + postfixGiveAccess
        )
                .file(new MockMultipartFile("file", "appeal.pdf", MediaType.APPLICATION_PDF_VALUE, new byte[]{}))
                .header("Authorization", getHeaderAuthorization(studentAccessToken))
                .param("reason", getDefaultReason());

        mockMvc.perform(requestBuilder)
                .andExpectAll(
                        status().isOk(),
                        content().contentType(MediaType.valueOf("text/plain;charset=ISO-8859-1"))
                ).andReturn();

    }

    @Test
    @Transactional
    @DirtiesContext
    @SneakyThrows
    void studentAppealTestNoParamsShouldFail() {

        String studentAccessToken = getAccessToken(Role.STUDENT);
        User student = getUserByAccessToken(studentAccessToken);

        String group = "02-P";

        Course course = saveCourse();
        Lesson lesson = saveLesson(null, course, List.of(student), group);
        Attendance attendance = attendanceRepository.save(getAttendance(lesson));
        AttendanceRecord attendanceRecord =
                attendanceRecordRepository.save(
                        getAttendanceRecord(null, student, attendance, AttendanceStatus.ABSENT));


        var postfixGiveAccess = AttendanceController.STUDENT_ATTENDANCE_APPEAL
                .replace("{attendance_record_id}", String.valueOf(attendanceRecord.getId()));

        var requestBuilder = multipart(
                BASE_URL + postfixGiveAccess
        )
                .header("Authorization", getHeaderAuthorization(studentAccessToken));

        mockMvc.perform(requestBuilder)
                .andExpectAll(
                        status().isBadRequest()
                ).andReturn();

    }

    @Test
    @Transactional
    @DirtiesContext
    @SneakyThrows
    void studentAppealTestIncorrectFiletypeShouldFail() {

        String studentAccessToken = getAccessToken(Role.STUDENT);
        User student = getUserByAccessToken(studentAccessToken);

        String group = "02-P";

        Course course = saveCourse();
        Lesson lesson = saveLesson(null, course, List.of(student), group);
        Attendance attendance = attendanceRepository.save(getAttendance(lesson));
        AttendanceRecord attendanceRecord =
                attendanceRecordRepository.save(
                        getAttendanceRecord(null, student, attendance, AttendanceStatus.ABSENT));


        var postfixGiveAccess = AttendanceController.STUDENT_ATTENDANCE_APPEAL
                .replace("{attendance_record_id}", String.valueOf(attendanceRecord.getId()));

        var requestBuilder = multipart(
                BASE_URL + postfixGiveAccess
        )
                .file(new MockMultipartFile("file","appeal.png",MediaType.IMAGE_PNG_VALUE,new byte[]{}))
                .param("reason", getDefaultReason())
                .header("Authorization", getHeaderAuthorization(studentAccessToken));

        mockMvc.perform(requestBuilder)
                .andExpectAll(
                        status().isBadRequest()
                ).andReturn();

    }

    @Test
    @Transactional
    @DirtiesContext
    @SneakyThrows
    void adminAcceptAppealTest() {
        String adminAccessToken = getAccessToken(Role.ADMIN);

        User student = userRepository.save(getUser(Role.STUDENT));
        Attendance attendance = attendanceRepository.save(getAttendance(null));
        AbsenceReason absenceReason = absenceReasonRepository.save(AbsenceReason
                .builder()
                .reason(getDefaultReason())
                .build());
        AttendanceRecord attendanceRecord = attendanceRecordRepository.save(
                AttendanceRecord.builder()
                        .absenceReason(absenceReason)
                        .attendanceStatus(AttendanceStatus.ABSENT)
                        .attendance(attendance)
                        .student(student)
                        .build());
        var postfix = AttendanceController.ADMIN_ATTENDANCE_APPEAL_ACCEPT
                .replace("{attendance_record_id}", String.valueOf(attendanceRecord.getId()));
        var requestBuilder = getRequestBuilder("POST", postfix, adminAccessToken, null)
                .header("Authorization", getHeaderAuthorization(adminAccessToken));

        mockMvc.perform(requestBuilder)
                .andExpectAll(
                        status().isOk(),
                        content().string("Approved"));
    }

    @Test
    @Transactional
    @DirtiesContext
    @SneakyThrows
    void adminAcceptDenyTest() {
        String adminAccessToken = getAccessToken(Role.ADMIN);

        User student = userRepository.save(getUser(Role.STUDENT));
        Attendance attendance = attendanceRepository.save(getAttendance(null));
        AbsenceReason absenceReason = absenceReasonRepository.save(AbsenceReason
                .builder()
                .reason(getDefaultReason())
                .build());
        AttendanceRecord attendanceRecord = attendanceRecordRepository.save(
                AttendanceRecord.builder()
                        .absenceReason(absenceReason)
                        .attendanceStatus(AttendanceStatus.ABSENT)
                        .attendance(attendance)
                        .student(student)
                        .build());
        var postfix = AttendanceController.ADMIN_ATTENDANCE_APPEAL_DENY
                .replace("{attendance_record_id}", String.valueOf(attendanceRecord.getId()));
        var requestBuilder = getRequestBuilder("POST", postfix, adminAccessToken, null)
                .header("Authorization", getHeaderAuthorization(adminAccessToken));

        mockMvc.perform(requestBuilder)
                .andExpectAll(
                        status().isOk(),
                        content().string("Denied"));
    }

    @Test
    @Transactional
    @DirtiesContext
    @SneakyThrows
    void adminAppealsTest() {
        String adminAccessToken = getAccessToken(Role.ADMIN);
        User student1 = userRepository.save(getUser(Role.STUDENT));
        User student2 = userRepository.save(getUser(Role.STUDENT));
        String group = "02-N";
        Course course = saveCourse();
        Lesson lesson = saveLesson(null, course, List.of(student1, student2), group);
        Attendance attendance = attendanceRepository.save(getAttendance(lesson));

        AbsenceReason absenceReason1 = absenceReasonRepository.save(AbsenceReason
                .builder()
                .reason("")
                .requestedDate(LocalDateTime.now())
                .build());

        AbsenceReason absenceReason2 = absenceReasonRepository.save(AbsenceReason
                .builder()
                .reason(getDefaultReason())
                .requestedDate(LocalDateTime.now())
                .build());

        AttendanceRecord attendanceRecord1 =
                attendanceRecordRepository.save(getAttendanceRecord(null, student1, attendance, AttendanceStatus.ABSENT));
        attendanceRecord1.setAbsenceReason(absenceReason1);

        absenceReason1.setAttendanceRecord(attendanceRecord1);
        AttendanceRecord attendanceRecord2 =
                attendanceRecordRepository.save(getAttendanceRecord(null, student1, attendance, AttendanceStatus.ABSENT));
        attendanceRecord2.setAbsenceReason(absenceReason2);
        absenceReason2.setAttendanceRecord(attendanceRecord2);


        var postfix = AttendanceController.ADMIN_SEE_ABSENCE_APPEALS;

        var requestBuilder = getRequestBuilder("GET", postfix, adminAccessToken, null);

        MvcResult mvcResult = mockMvc.perform(requestBuilder)
                .andExpectAll(status().isOk()).andReturn();

        compareForEquationResponseLengthAndExpectedLength(mvcResult, 2);
    }


    @SneakyThrows
    @Transactional
    byte[] createAttendanceQr(String teacherAccessToken, Attendance attendance, Lesson lesson) {
        var teacherTakeAttendanceUrl = AttendanceController.TEACHER_TAKE_ATTENDANCE_BY_QR;
        AttendanceRequest attendanceRequest = new AttendanceRequest(attendance.getId(), lesson.getId());

        var teacherTakeAttendanceRequest = getRequestBuilder(
                "POST",
                teacherTakeAttendanceUrl,
                teacherAccessToken, objectMapper.writeValueAsString(attendanceRequest));

        return mockMvc.perform(teacherTakeAttendanceRequest)
                .andExpectAll(
                        status().isOk(),
                        content().contentType(MediaType.IMAGE_JPEG_VALUE)
                ).andReturn().getResponse().getContentAsByteArray();
    }

    @SneakyThrows
    @Transactional
    String getAccessToken(Role role) {
        User user = getUser(role);


        RegisterRequest registerRequest =
                new RegisterRequest(user.getLogin(),
                        user.getPassword(),
                        role, user.getPerson().getId().intValue());


        var registerRequestBuilder = post(BASE_URL + AuthenticationController.USER_SIGN_UP)
                .content(objectMapper.writeValueAsString(registerRequest))
                .contentType(MediaType.APPLICATION_JSON);

        String body = mockMvc.perform(registerRequestBuilder).andExpectAll(
                        status().isOk(),
                        content().contentType(MediaType.APPLICATION_JSON)
                ).andReturn()
                .getResponse().getContentAsString();

        AuthenticationResponse authenticationResponse = objectMapper.readValue(body, AuthenticationResponse.class);
        return authenticationResponse.getAccessToken();
    }

    private MockHttpServletRequestBuilder getRequestBuilder(
            String requestType,
            String postfix,
            String accessToken,
            String content) {
        MockHttpServletRequestBuilder requestBuilder = null;
        if (requestType.equals("GET")) {
            requestBuilder = get(BASE_URL + postfix);
        } else if (requestType.equals("POST"))
            requestBuilder = post(BASE_URL + postfix);

        if (accessToken != null) {
            requestBuilder.header("Authorization", getHeaderAuthorization(accessToken));
        }
        if (content != null) {
            requestBuilder.content(content).contentType(MediaType.APPLICATION_JSON);
        }
        return requestBuilder;
    }

    private String fetchAccessToken(String url) {
        String patternString = ".*&token=([^/]+)";
        Pattern pattern = Pattern.compile(patternString);
        Matcher matcher = pattern.matcher(url);

        matcher.find();
        return matcher.group(1);
    }

    private User getUserByAccessToken(String accessToken) {
        return tokenRepository.findByToken(accessToken).get().getUser();
    }

    private AttendanceRecord getAttendanceRecord(AttendanceType type, User student, Attendance attendance,
                                                 AttendanceStatus status) {
        return AttendanceRecord.builder()
                .student(student)
                .attendance(attendance)
                .attendanceType(type)
                .attendanceStatus(status)
                .build();
    }

    @SneakyThrows
    private void compareForEquationResponseLengthAndExpectedLength(MvcResult mvcResult, int expectedLength) {
        List responseList = objectMapper.readValue(mvcResult.getResponse().getContentAsString(),
                List.class);
        assertEquals(expectedLength, responseList.size());
    }

    private String getHeaderAuthorization(String accessToken) {
        return String.format("Bearer %s", accessToken);
    }

    private Attendance getAttendance(Lesson lesson) {
        return Attendance.builder()
                .lesson(lesson)
                .localDateTime(LocalDateTime.now())
                .build();
    }

    private Course saveCourse() {
        Random random = new Random();
        Supplier<String> courseCode = () -> {
            String[] coursePrefixes = {"MAT", "INF", "ENG", "SCI", "HIS", "ART", "PHY", "BIO"};

            String randomPrefix = coursePrefixes[ThreadLocalRandom.current().nextInt(coursePrefixes.length)];

            int randomCourseNumber = ThreadLocalRandom.current().nextInt(100, 1000);

            return randomPrefix + "-" + randomCourseNumber;
        };

        return courseRepository.save(
                Course.builder()
                        .code(courseCode.get())
                        .total_hours(random.nextInt(15, 40))
                        .name(faker.educator().course())
                        .build());

    }

    private Lesson saveLesson(User teacher, Course course, List<User> students, String group) {
        return lessonRepository.save(Lesson.builder()
                .course(course)
                .lessonStudents(students)
                .teacher(teacher)
                .group(group)
                .startTime(LocalTime.of(9, 0))
                .endTime(LocalTime.of(9, 50))
                .dayOfWeek("Friday")
                .build());
    }

    private User getUser(Role role) {
        Name name = faker.name();
        Internet internet = faker.internet();
        DateAndTime date = faker.date();

        String firstName = name.firstName();
        String surname = name.lastName();
        Person person = personRepository.save(Person.builder()
                .name(firstName)
                .surname(surname)
                .email(internet.emailAddress())
                .birthDate(date.birthdayLocalDate())
                .build());

        return User.builder()
                .person(person)
                .login(String.format("%s.%s", firstName.toLowerCase(), surname.toLowerCase()))
                .password(internet.password(6, 13))
                .role(role)
                .build();

    }

    private void setPerson(User user) {
        user.getPerson().setUser(user);
    }

    @Transactional
    void createTable() {
        jdbcTemplate.execute("""
                create table attendance_permission(
                    producer_id integer           not null
                        constraint unique_producer_id
                            unique
                        references _user,
                    consumer_id integer           not null
                        constraint unique_consumer_id
                            unique
                        references _user,
                    course_id   integer           not null
                        references course,
                    _limit      integer default 3 not null);
                                """);
    }

    private String getDefaultReason(){
        return "I couldn't attend due to family problems";
    }
}
