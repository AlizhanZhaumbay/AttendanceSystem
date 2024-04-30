package com.example.attendance_system;

import com.example.attendance_system.auth.AuthenticationResponse;
import com.example.attendance_system.auth.RegisterRequest;
import com.example.attendance_system.dto.AttendanceRequest;
import com.example.attendance_system.model.*;
import com.example.attendance_system.qr.QrCodeService;
import com.example.attendance_system.repo.*;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.transaction.Transactional;
import lombok.SneakyThrows;
import lombok.extern.log4j.Log4j2;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@Log4j2
@AutoConfigureMockMvc(printOnlyOnFailure = false)
public class AttendanceTests {

    @Autowired
    MockMvc mockMvc;

    @Autowired
    TokenRepository tokenRepository;

    @Autowired
    QrCodeService qrCodeService;

    @Autowired
    JdbcTemplate jdbcTemplate;

    @Autowired
    AttendanceRecordRepository attendanceRecordRepository;

    private final ObjectMapper objectMapper = new ObjectMapper();
    private final String BASE_URL = "http://localhost:8080/api/v1/";

    @Autowired
    private CourseRepository courseRepository;

    @Autowired
    private LessonRepository lessonRepository;

    @Autowired
    private AttendanceRepository attendanceRepository;

    @Autowired
    private UserRepository userRepository;


    @Test
    @Transactional
    @DirtiesContext
    @SneakyThrows
    void takeAttendanceByQrTest() {
        createTable();
        String teacherAccessToken = getAccessToken(Role.TEACHER, "teacher");
        User teacher = getUserByAccessToken(teacherAccessToken);

        String student1AccessToken = getAccessToken(Role.STUDENT, "student1");
        User student1 = getUserByAccessToken(student1AccessToken);


        User student2 = getUser(3, Role.STUDENT);
        userRepository.save(student2);

        Course course = courseRepository.save(getCourse(1L));


        String group = "01-P";
        List<User> students = List.of(student1, student2);
        Lesson lesson = lessonRepository.save(getLesson(teacher, course, students, group));
        Attendance attendance = attendanceRepository.save(
                Attendance.builder()
                        .lesson(lesson)
                        .id(10)
                        .localDateTime(LocalDateTime.now())
                        .build()
        );
        byte[] qr = createAttendanceQr(teacherAccessToken, attendance, lesson);

        var studentTakeAttendanceRequest = get(qrCodeService.decodeQr(qr))
                .header("Authorization", getHeaderAuthorization(student1AccessToken));

        mockMvc.perform(studentTakeAttendanceRequest)
                .andExpectAll(
                        status().isOk(),
                        content().string(String.valueOf(student1.getId()))
                );

        log.info("Attendance Records {}", attendanceRecordRepository.findAll());
    }

    @Test
    @Transactional
    @DirtiesContext
    @SneakyThrows
    void seeAttendancesForAdminTest() {
        String adminAccessToken = getAccessToken(Role.ADMIN, "admin");

        User student1 = userRepository.save(getUser(2, Role.STUDENT));
        User student2 = userRepository.save(getUser(3, Role.STUDENT));
        User student3 = userRepository.save(getUser(4, Role.STUDENT));

        Course course = courseRepository.save(getCourse(1L));
        String group = "02-n";
        Lesson lesson1 = lessonRepository.save(
                getLesson(null, course, List.of(student1, student2, student3), group));
        Lesson lesson2 = lessonRepository.save(
                getLesson(null, course, List.of(student1, student2, student3), group));


        Attendance attendance1 = attendanceRepository.save(getAttendance(lesson1));
        Attendance attendance2 = attendanceRepository.save(getAttendance(lesson2));
        attendanceRecordRepository.saveAll(
                List.of(
                        getAttendanceRecord(AttendanceType.QR, student1, attendance1, AttendanceStatus.PRESENT),
                        getAttendanceRecord(AttendanceType.MANUAL, student2, attendance1, AttendanceStatus.PRESENT),
                        getAttendanceRecord(null, student3, attendance1, AttendanceStatus.ABSENCE),

                        getAttendanceRecord(null, student1, attendance2, AttendanceStatus.ABSENCE),
                        getAttendanceRecord(null, student2, attendance2, AttendanceStatus.ABSENCE),
                        getAttendanceRecord(AttendanceType.QR, student3, attendance2, AttendanceStatus.PRESENT)));


        var postfix = String.format("admin/attendance/courses/%d", course.getId());
        var requestBuilder = getRequestBuilder("GET", postfix, adminAccessToken, null);

        mockMvc.perform(requestBuilder)
                .andExpectAll(
                        status().isOk(),
                        content().contentType(MediaType.APPLICATION_JSON)
                );
    }


    @Test
    @Transactional
    @DirtiesContext
    @SneakyThrows
    void seeAttendancesForStudentTest() {
        String student1AccessToken = getAccessToken(Role.STUDENT, "student");

        User student1 = getUserByAccessToken(student1AccessToken);
        User student2 = userRepository.save(getUser(2, Role.STUDENT));
        User student3 = userRepository.save(getUser(3, Role.STUDENT));

        Course course = courseRepository.save(getCourse(1L));
        String group = "02-n";
        Lesson lesson1 = lessonRepository.save(
                getLesson(null, course, List.of(student1, student2, student3), group));
        Lesson lesson2 = lessonRepository.save(
                getLesson(null, course, List.of(student1, student2, student3), group));


        Attendance attendance1 = attendanceRepository.save(getAttendance(lesson1));
        Attendance attendance2 = attendanceRepository.save(getAttendance(lesson2));
        attendanceRecordRepository.saveAll(
                List.of(
                        getAttendanceRecord(AttendanceType.QR, student1, attendance1, AttendanceStatus.PRESENT),
                        getAttendanceRecord(AttendanceType.MANUAL, student2, attendance1, AttendanceStatus.PRESENT),
                        getAttendanceRecord(null, student3, attendance1, AttendanceStatus.ABSENCE),

                        getAttendanceRecord(null, student1, attendance2, AttendanceStatus.ABSENCE),
                        getAttendanceRecord(null, student2, attendance2, AttendanceStatus.ABSENCE),
                        getAttendanceRecord(AttendanceType.QR, student3, attendance2, AttendanceStatus.PRESENT)));


        var postfix = String.format("student/attendance/courses/%d", course.getId());
        var requestBuilder = getRequestBuilder("GET", postfix, student1AccessToken, null);

        mockMvc.perform(requestBuilder)
                .andExpectAll(
                        status().isOk(),
                        content().contentType(MediaType.APPLICATION_JSON)
                );
    }

    @Test
    @Transactional
    @DirtiesContext
    @SneakyThrows
    void giveAttendanceAccessForAnotherStudentTest() {
        createTable();
        String student1AccessToken = getAccessToken(Role.STUDENT, "student1");
        String student2AccessToken = getAccessToken(Role.STUDENT, "student2");
        User student1 = getUserByAccessToken(student1AccessToken);
        User student2 = getUserByAccessToken(student2AccessToken);

        String teacherAccessToken = getAccessToken(Role.TEACHER, "teacher");
        User teacher = getUserByAccessToken(teacherAccessToken);

        String group = "02-P";

        Course course = courseRepository.save(getCourse(1L));
        Lesson lesson = lessonRepository.save(
                getLesson(teacher, course, List.of(student1, student2), group));

        Attendance attendance = attendanceRepository.save(getAttendance(lesson));

        var postfixGiveAccess = String.format(
                "/student/attendance/courses/%d/%s/students/%d/give-access", course.getId(), group,
                student2.getId());
        var requestBuilderGiveAccess =
                getRequestBuilder("POST", postfixGiveAccess, student1AccessToken, null);

        mockMvc.perform(requestBuilderGiveAccess)
                .andExpectAll(
                        status().isOk(),
                        content().contentType(MediaType.APPLICATION_JSON)
                );

        byte[] qr = createAttendanceQr(teacherAccessToken, attendance, lesson);
        var requestBuilderAttend =
//                getRequestBuilder("GET", postfixAttend, student2AccessToken, null);
                get(qrCodeService.decodeQr(qr))
                        .header("Authorization", getHeaderAuthorization(student2AccessToken));
        mockMvc.perform(requestBuilderAttend)
                .andExpectAll(
                        status().isOk(),
                        content().string(String.valueOf(student2.getId()))
                );
        var teacherSeeAttendancePostfix = String.format("teacher/attendance/courses/%d", course.getId());
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

    @SneakyThrows
    @Transactional
    byte[] createAttendanceQr(String teacherAccessToken, Attendance attendance, Lesson lesson) {
        var teacherTakeAttendanceUrl = "/teacher/attendance/take/qr";
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
    String getAccessToken(Role role, String login) {
        var url = "/auth/register";
        String roleString = role.name();

        RegisterRequest registerRequest =
                new RegisterRequest(login,
                        roleString + "password",
                        role, null);

        var registerRequestBuilder = post(BASE_URL + url)
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

    private String getHeaderAuthorization(String accessToken) {
        return String.format("Bearer %s", accessToken);
    }

    private Attendance getAttendance(Lesson lesson) {
        return Attendance.builder()
                .lesson(lesson)
                .localDateTime(LocalDateTime.now())
                .build();
    }

    private Course getCourse(Long id) {
        return new Course(id, "course" + id, "code" + id, 40, null);
    }

    private Lesson getLesson(User teacher, Course course, List<User> students, String group) {
        return Lesson.builder()
                .course(course)
                .lessonStudents(students)
                .teacher(teacher)
                .group(group)
                .dayOfWeek("Friday")
                .build();
    }

    private User getUser(Integer id, Role role) {
        Person person = new Person((long) id, id, role.name() + id,
                String.format("%sSurname%d", role.name(), id), LocalDate.now(),
                String.format("%s%d@mail.ru", role.name(),id));
        return User.builder()
                .id(id)
                .login(role.name() + id)
                .person(person)
                .role(role)
                .build();
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
}
