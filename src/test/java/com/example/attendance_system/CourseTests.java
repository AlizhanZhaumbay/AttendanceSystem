package com.example.attendance_system;

import com.example.attendance_system.auth.AuthenticationResponse;
import com.example.attendance_system.auth.RegisterRequest;
import com.example.attendance_system.model.Course;
import com.example.attendance_system.model.Lesson;
import com.example.attendance_system.model.Role;
import com.example.attendance_system.model.User;
import com.example.attendance_system.repo.CourseRepository;
import com.example.attendance_system.repo.LessonRepository;
import com.example.attendance_system.repo.TokenRepository;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.transaction.Transactional;
import lombok.SneakyThrows;
import lombok.extern.log4j.Log4j2;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Collections;
import java.util.List;

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


    private final ObjectMapper objectMapper = new ObjectMapper()
            .setSerializationInclusion(JsonInclude.Include.NON_NULL);
    private final String BASE_URL = "http://localhost:8080/api/v1/";

    @SneakyThrows
    @DirtiesContext
    @Transactional
    String returnsValidResponseAccessToken(Role role, String login) {
        var register = "/auth/register";
        String roleString = role.name();

        RegisterRequest registerRequest =
                new RegisterRequest(login,
                        roleString + "password",
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
        String accessToken = returnsValidResponseAccessToken(Role.ADMIN, "admin");

        Course course = getCourse(1L);
        courseRepository.save(course);

        var requestBuilder = get(BASE_URL + "/admin/courses")
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
        String accessTokenTeacher1 = returnsValidResponseAccessToken(Role.TEACHER, "teacher1");
        String accessTokenTeacher2 = returnsValidResponseAccessToken(Role.TEACHER, "teacher2");

        User teacher1 = tokenRepository.findByToken(accessTokenTeacher1).get().getUser();
        User teacher2 = tokenRepository.findByToken(accessTokenTeacher2).get().getUser();

        List<Course> coursesForTeacher1 = List.of(getCourse(1L), getCourse(2L));
        List<Course> coursesForTeacher2 = List.of(getCourse(3L), getCourse(4L));

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

        var requestBuilder = get(BASE_URL + "/teacher/courses")
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
        String accessTokenStudent1 = returnsValidResponseAccessToken(Role.STUDENT, "student1");
        String accessTokenStudent2 = returnsValidResponseAccessToken(Role.STUDENT, "student2");

        User student1 = tokenRepository.findByToken(accessTokenStudent1).get().getUser();
        User student2 = tokenRepository.findByToken(accessTokenStudent2).get().getUser();

        List<Course> coursesForStudent1 = List.of(getCourse(1L), getCourse(2L));
        List<Course> coursesForStudent2 = List.of(getCourse(3L), getCourse(4L));

        courseRepository.saveAll(coursesForStudent1);
        courseRepository.saveAll(coursesForStudent2);

        List<Lesson> lessonsForStudent1 = List.of(
                getLesson(student1, coursesForStudent1.get(0), List.of(student1)),
                getLesson(student1, coursesForStudent1.get(1), List.of(student1)));

        List<Lesson> lessonsForStudent2 = List.of(
                getLesson(student2, coursesForStudent2.get(0), List.of(student2)),
                getLesson(student2, coursesForStudent2.get(1), List.of(student2))
        );

        lessonRepository.saveAll(lessonsForStudent1);
        lessonRepository.saveAll(lessonsForStudent2);

        var requestBuilder = get(BASE_URL + "/student/courses")
                .header("Authorization", "Bearer " + accessTokenStudent2);

        String body = mockMvc.perform(requestBuilder)
                .andExpectAll(
                        status().isOk()
                ).andReturn()
                .getResponse().getContentAsString();

        Object expectedCourses = objectMapper.readValue(objectMapper.writeValueAsString(coursesForStudent2), Object.class);
        Object actualCourses = objectMapper.readValue(body, Object.class);
        assertEquals(expectedCourses, actualCourses);
    }

    public Course getCourse(Long id) {
        return new Course(id, "course" + id, "code" + id, 40, null);
    }

    public Lesson getLesson(User teacher, Course course, List<User> students) {
        return Lesson.builder()
                .course(course)
                .lessonStudents(students)
                .teacher(teacher)
                .dayOfWeek("Friday")
                .build();
    }
}
