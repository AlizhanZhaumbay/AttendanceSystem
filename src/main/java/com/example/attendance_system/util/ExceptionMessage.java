package com.example.attendance_system.util;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Getter
public enum ExceptionMessage {
    TEACHER_DOES_NOT_HAVE_COURSE("Teacher with teacher_id{%d} does have not course with course_id{%d}"),
    TEACHER_DOES_NOT_HAVE_LESSON("Teacher with teacher_id{%d} does have not lesson with lesson_id{%d}"),
    STUDENT_DOES_NOT_HAVE_COURSE("Student with student_id{%d} does have have course with course_id{%d}"),
    STUDENT_DOES_NOT_HAVE_LESSON("Student with student_id{%d} does have not lesson with lesson_id{%d}"),
    USER_LOGIN_NOT_FOUND("User with login{%s} not found."),
    USER_ALREADY_EXISTS_WITH_LOGIN("User with login{%s} already exists."),
    LESSON_NOT_FOUND("Lesson with id{%d} not found."),
    LESSON_NOT_FOUND_WITH_COURSE("Lesson with id {%d} not found with course {%d}"),
    ATTENDANCE_NOT_FOUND("Attendance with id{%d} not found"),
    ATTENDANCE_NOT_FOUND_WITH_LESSON("Attendance with id{%d} not found with lesson{%d}"),
    ATTENDANCE_NOT_EXISTS_OR_HAS_EXPIRED("Attendance not exists or has expired."),
    STUDENT_NOT_FOUND("Student not found with id{%d}"),
    TEACHER_NOT_FOUND("Teacher not found with id{%d}"),
    ATTENDANCE_ACCESS_ALREADY_GIVEN("User {%d} has already gave attendance access for another student."),
    ATTENDANCE_ACCESS_ALREADY_TAKEN("User {%d} has already took attendance access from another student.");;
    private final String message;


    public static String teacherDoesNotHaveCourse(Integer teacherId, Integer courseId){
        return String.format(TEACHER_DOES_NOT_HAVE_COURSE.getMessage(), teacherId, courseId);
    }

    public static String teacherDoesNotHaveLesson(Integer teacherId, Integer lessonId){
        return String.format(TEACHER_DOES_NOT_HAVE_LESSON.getMessage(), teacherId, lessonId);
    }
    public static String studentDoesNotHaveCourse(Integer studentId, Integer courseId){
        return String.format(STUDENT_DOES_NOT_HAVE_COURSE.getMessage(), studentId, courseId);
    }

    public static String studentDoesNotHaveLesson(Integer studentId, Integer lessonId){
        return String.format(STUDENT_DOES_NOT_HAVE_LESSON.getMessage(), studentId, lessonId);
    }

    public static String userLoginNotFound(String login){
        return String.format(USER_LOGIN_NOT_FOUND.getMessage(), login);
    }

    public static String userAlreadyExistsWithLogin(String login){
        return String.format(USER_ALREADY_EXISTS_WITH_LOGIN.getMessage(), login);
    }

    public static String lessonNotFound(Integer lessonId){
        return String.format(LESSON_NOT_FOUND.getMessage(), lessonId);
    }

    public static String lessonNotFoundWithCourse(Integer lessonId, Integer courseId){
        return String.format(LESSON_NOT_FOUND_WITH_COURSE.getMessage(), courseId, lessonId);
    }


    public static String attendanceNotFoundException(Integer attendanceId) {
        return String.format(ATTENDANCE_NOT_FOUND.getMessage(), attendanceId);
    }

    public static String attendanceNotFoundWithLessonException(Integer attendanceId, Integer lessonId) {
        return String.format(ATTENDANCE_NOT_FOUND_WITH_LESSON.getMessage(), attendanceId, lessonId);
    }

    public static String attendanceNotExistsOrHasExpiredException() {
        return ATTENDANCE_NOT_EXISTS_OR_HAS_EXPIRED.getMessage();
    }

    public static String studentNotFound(Integer studentId) {
        return String.format(STUDENT_NOT_FOUND.getMessage(), studentId);
    }

    public static String teacherNotFound(Integer teacherId) {
        return String.format(TEACHER_NOT_FOUND.getMessage(), teacherId);
    }

    public static String attendanceAccessAlreadyGiven(Integer producerId) {
        return String.format(ATTENDANCE_ACCESS_ALREADY_GIVEN.getMessage(), producerId);
    }

    public static String attendanceAccessAlreadyTaken(Integer consumerId) {
        return String.format(ATTENDANCE_ACCESS_ALREADY_TAKEN.getMessage(), consumerId);
    }
}
