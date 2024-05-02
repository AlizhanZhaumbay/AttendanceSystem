package com.example.attendance_system;

import com.example.attendance_system.auth.AuthenticationController;
import com.example.attendance_system.auth.AuthenticationResponse;
import com.example.attendance_system.auth.RegisterRequest;
import com.example.attendance_system.controller.AttendanceController;
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
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;

import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;
import java.util.Random;
import java.util.concurrent.ThreadLocalRandom;
import java.util.function.Supplier;

import static org.hamcrest.Matchers.hasSize;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

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


    @Test
    @Transactional
    @DirtiesContext
    @SneakyThrows
    void takeAttendanceByQrTest() {
        createTable();
        String teacherAccessToken = getAccessToken(Role.TEACHER);
        User teacher = getUserByAccessToken(teacherAccessToken);

        String student1AccessToken = getAccessToken(Role.STUDENT);
        User student1 = getUserByAccessToken(student1AccessToken);


        User student2 = getUser(Role.STUDENT);
        userRepository.save(student2);

        Course course = getCourse();

        String group = "01-P";
        List<User> students = List.of(student1, student2);
        Lesson lesson = getLesson(teacher, course, students, group);
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
        String adminAccessToken = getAccessToken(Role.ADMIN);

        User student1 = userRepository.save(getUser(Role.STUDENT));
        User student2 = userRepository.save(getUser(Role.STUDENT));
        User student3 = userRepository.save(getUser(Role.STUDENT));

        User teacher = userRepository.save(getUser(Role.TEACHER));

        Course course = getCourse();
        String group = "02-n";
        String anotherGroup = "03-n";
        Lesson lesson1 = getLesson(teacher, course, List.of(student1, student2, student3), group);
        Lesson lesson2 = getLesson(teacher, course, List.of(student1, student2, student3), group);
        Lesson lesson3 = getLesson(teacher, course, List.of(student1, student2, student3), anotherGroup);

        Attendance attendance1 = attendanceRepository.save(getAttendance(lesson1));
        Attendance attendance2 = attendanceRepository.save(getAttendance(lesson2));
        Attendance attendance3 = attendanceRepository.save(getAttendance(lesson3));
        attendanceRecordRepository.saveAll(
                List.of(
                        getAttendanceRecord(AttendanceType.QR, student1, attendance1, AttendanceStatus.PRESENT),
                        getAttendanceRecord(AttendanceType.MANUAL, student2, attendance1, AttendanceStatus.PRESENT),
                        getAttendanceRecord(null, student3, attendance1, AttendanceStatus.ABSENCE),

                        getAttendanceRecord(null, student1, attendance2, AttendanceStatus.ABSENCE),
                        getAttendanceRecord(null, student2, attendance2, AttendanceStatus.ABSENCE),
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

        Course course = getCourse();
        String group = "02-n";
        String anotherGroup = "01-n";
        Lesson lesson1 = getLesson(teacher, course, List.of(student1, student2, student3), group);
        Lesson lesson2 = getLesson(teacher, course, List.of(student1, student2, student3), group);
        Lesson lesson3 = getLesson(teacher, course, List.of(student3, student2), anotherGroup);

        Attendance attendance1 = attendanceRepository.save(getAttendance(lesson1));
        Attendance attendance2 = attendanceRepository.save(getAttendance(lesson2));
        Attendance attendance3 = attendanceRepository.save(getAttendance(lesson3));
        attendanceRecordRepository.saveAll(
                List.of(
                        getAttendanceRecord(AttendanceType.QR, student1, attendance1, AttendanceStatus.PRESENT),
                        getAttendanceRecord(AttendanceType.MANUAL, student2, attendance1, AttendanceStatus.PRESENT),
                        getAttendanceRecord(null, student3, attendance1, AttendanceStatus.ABSENCE),

                        getAttendanceRecord(null, student1, attendance2, AttendanceStatus.ABSENCE),
                        getAttendanceRecord(null, student2, attendance2, AttendanceStatus.ABSENCE),
                        getAttendanceRecord(AttendanceType.QR, student3, attendance2, AttendanceStatus.PRESENT),
                        
                        getAttendanceRecord(null, student3, attendance3, AttendanceStatus.ABSENCE),
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

        Course course = getCourse();
        String group = "02-n";
        String anotherGroup = "01-n";
        Lesson lesson1 = getLesson(teacher, course, List.of(student1, student2, student3), group);
        Lesson lesson2 = getLesson(teacher, course, List.of(student1, student2, student3), group);
        Lesson lesson3 = getLesson(teacher, course, List.of(student3, student2), anotherGroup);

        Attendance attendance1 = attendanceRepository.save(getAttendance(lesson1));
        Attendance attendance2 = attendanceRepository.save(getAttendance(lesson2));
        Attendance attendance3 = attendanceRepository.save(getAttendance(lesson3));
        attendanceRecordRepository.saveAll(
                List.of(
                        getAttendanceRecord(AttendanceType.QR, student1, attendance1, AttendanceStatus.PRESENT),
                        getAttendanceRecord(AttendanceType.MANUAL, student2, attendance1, AttendanceStatus.PRESENT),
                        getAttendanceRecord(null, student3, attendance1, AttendanceStatus.ABSENCE),

                        getAttendanceRecord(null, student1, attendance2, AttendanceStatus.ABSENCE),
                        getAttendanceRecord(null, student2, attendance2, AttendanceStatus.ABSENCE),
                        getAttendanceRecord(AttendanceType.QR, student3, attendance2, AttendanceStatus.PRESENT),

                        getAttendanceRecord(null, student3, attendance3, AttendanceStatus.ABSENCE),
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
    void giveAttendanceAccessForAnotherStudentTest() {
        createTable();
        String student1AccessToken = getAccessToken(Role.STUDENT);
        String student2AccessToken = getAccessToken(Role.STUDENT);
        User student1 = getUserByAccessToken(student1AccessToken);
        User student2 = getUserByAccessToken(student2AccessToken);

        String teacherAccessToken = getAccessToken(Role.TEACHER);
        User teacher = getUserByAccessToken(teacherAccessToken);

        String group = "02-P";

        Course course = getCourse();
        Lesson lesson =
                getLesson(teacher, course, List.of(student1, student2), group);

        Attendance attendance = attendanceRepository.save(getAttendance(lesson));

        var postfixGiveAccess = String.format(
                AttendanceController.STUDENT_ATTENDANCE_GIVE_PERMISSION
                        .replace("{course_id}", String.valueOf(course.getId()))
                        .replace("{code}", group))
                        .replace("{student_id}", String.valueOf(student2.getId()));
        var requestBuilderGiveAccess =
                getRequestBuilder("POST", postfixGiveAccess, student1AccessToken, null);

        mockMvc.perform(requestBuilderGiveAccess)
                .andExpectAll(
                        status().isOk(),
                        content().contentType(MediaType.APPLICATION_JSON)
                );

        byte[] qr = createAttendanceQr(teacherAccessToken, attendance, lesson);
        var requestBuilderAttend =
                get(qrCodeService.decodeQr(qr))
                        .header("Authorization", getHeaderAuthorization(student2AccessToken));
        mockMvc.perform(requestBuilderAttend)
                .andExpectAll(
                        status().isOk(),
                        content().string(String.valueOf(student2.getId()))
                );
        var teacherSeeAttendancePostfix = AttendanceController.TEACHER_SEE_ATTENDANCE_RECORDS
                .replace("{course_id}", String.valueOf(course.getId()));
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
        List responseList= objectMapper.readValue(mvcResult.getResponse().getContentAsString(),
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

    private Course getCourse() {
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

    private Lesson getLesson(User teacher, Course course, List<User> students, String group) {
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
