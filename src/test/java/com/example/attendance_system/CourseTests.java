package com.example.attendance_system;

import com.example.attendance_system.auth.AuthenticationController;
import com.example.attendance_system.auth.AuthenticationResponse;
import com.example.attendance_system.auth.RegisterRequest;
import com.example.attendance_system.controller.CourseController;
import com.example.attendance_system.model.*;
import com.example.attendance_system.repo.*;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.transaction.Transactional;
import lombok.SneakyThrows;
import lombok.extern.log4j.Log4j2;
import net.datafaker.Faker;
import net.datafaker.providers.base.DateAndTime;
import net.datafaker.providers.base.Internet;
import net.datafaker.providers.base.Name;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import java.util.Collections;
import java.util.List;
import java.util.Random;
import java.util.concurrent.ThreadLocalRandom;
import java.util.function.Supplier;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc(printOnlyOnFailure = false)
@Log4j2
public class CourseTests {

    @Autowired
    MockMvc mockMvc;

    @Autowired
    private CourseRepository courseRepository;

    @Autowired
    private LessonRepository lessonRepository;

    @Autowired
    private TokenRepository tokenRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PersonRepository personRepository;

    private final Faker faker = new Faker();


    private final ObjectMapper objectMapper = new ObjectMapper()
            .setSerializationInclusion(JsonInclude.Include.NON_NULL);
    private final String BASE_URL = "http://localhost:8080";

    @SneakyThrows
    @DirtiesContext
    @Transactional
    String returnsValidResponseAccessToken(Role role) {
        var register = AuthenticationController.USER_SIGN_UP;
        Name name = faker.name();
        String login = name.firstName();
        String password = faker.passport().valid();
        RegisterRequest registerRequest =
                new RegisterRequest(login,
                        password,
                        role, null);

        var registerRequestBuilder = post(BASE_URL + register)
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

    @Test
    @DirtiesContext
    @SneakyThrows
    @Transactional
    @DisplayName("Get request should send all courses for Admin with status 200")
    void handleAuth_ReturnsValidCoursesForAdmin() {
        String accessToken = returnsValidResponseAccessToken(Role.ADMIN);

        Course course = saveCourse();
        courseRepository.save(course);

        var requestBuilder = get(BASE_URL + CourseController.ADMIN_SEE_COURSES)
                .header("Authorization", "Bearer " + accessToken);

        mockMvc.perform(requestBuilder)
                .andExpectAll(
                        status().isOk()
                ).andReturn()
                .getResponse();
    }

    @Test
    @SneakyThrows
    @Transactional
    @DirtiesContext
    @DisplayName("Get request should send all courses for Teacher with status 200")
    void handleAuth_ReturnsValidCoursesForTeacher() {
        String accessTokenTeacher1 = returnsValidResponseAccessToken(Role.TEACHER);
        String accessTokenTeacher2 = returnsValidResponseAccessToken(Role.TEACHER);

        User teacher1 = tokenRepository.findByToken(accessTokenTeacher1).get().getUser();
        User teacher2 = tokenRepository.findByToken(accessTokenTeacher2).get().getUser();

        List<Course> coursesForTeacher1 = List.of(saveCourse(), saveCourse());
        List<Course> coursesForTeacher2 = List.of(saveCourse(), saveCourse());

        courseRepository.saveAll(coursesForTeacher1);
        courseRepository.saveAll(coursesForTeacher2);

        List<Lesson> lessonsForTeacher1 = List.of(
                getLesson(teacher1, coursesForTeacher1.get(0), Collections.emptyList()),
                getLesson(teacher1, coursesForTeacher1.get(1), Collections.emptyList()));

        List<Lesson> lessonsForTeacher2 = List.of(
                getLesson(teacher2, coursesForTeacher2.get(0), Collections.emptyList()),
                getLesson(teacher2, coursesForTeacher2.get(1), Collections.emptyList())
        );

        lessonRepository.saveAll(lessonsForTeacher1);
        lessonRepository.saveAll(lessonsForTeacher2);

        var requestBuilder = get(BASE_URL + CourseController.TEACHER_SEE_COURSES)
                .header("Authorization", "Bearer " + accessTokenTeacher2);

        mockMvc.perform(requestBuilder)
                .andExpectAll(
                        status().isOk()
                ).andReturn()
                .getResponse();
    }

    @Test
    @SneakyThrows
    @DirtiesContext
    @Transactional
    @DisplayName("Get request should send all courses for Student with status 200")
    void handleAuth_ReturnsValidCoursesForStudent() {
        String accessTokenStudent = returnsValidResponseAccessToken(Role.STUDENT);


        User student = getUserByAccessToken(accessTokenStudent);
        User teacher = userRepository.save(getUser(Role.TEACHER));

        List<Course> courses = List.of(saveCourse(), saveCourse());

        courseRepository.saveAll(courses);

        List<Lesson> lessonsForStudent = List.of(
                getLesson(teacher, courses.get(0), List.of(student)),
                getLesson(teacher, courses.get(1), List.of(student)),
                getLesson(teacher, saveCourse(), Collections.emptyList()));


        lessonRepository.saveAll(lessonsForStudent);

        var requestBuilder = get(BASE_URL + CourseController.STUDENT_SEE_COURSES)
                .header("Authorization", "Bearer " + accessTokenStudent);

        String body = mockMvc.perform(requestBuilder)
                .andExpectAll(
                        status().isOk()
                ).andReturn()
                .getResponse().getContentAsString();

        Object expectedCourses = objectMapper.readValue(objectMapper.writeValueAsString(courses), Object.class);
        Object actualCourses = objectMapper.readValue(body, Object.class);
        assertEquals(expectedCourses, actualCourses);
    }

    public Course saveCourse() {
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

    public Lesson getLesson(User teacher, Course course, List<User> students) {
        return Lesson.builder()
                .course(course)
                .lessonStudents(students)
                .teacher(teacher)
                .dayOfWeek("Friday")
                .build();
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

    private User getUserByAccessToken(String accessToken) {
        return tokenRepository.findByToken(accessToken).get().getUser();
    }

    @SneakyThrows
    private void compareForEquationResponseLengthAndExpectedLength(MvcResult mvcResult, int expectedLength) {
        List responseList = objectMapper.readValue(mvcResult.getResponse().getContentAsString(),
                List.class);
        assertEquals(expectedLength, responseList.size());
    }
}
