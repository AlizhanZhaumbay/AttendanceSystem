package com.example.attendance_system.util;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

public class ExceptionMessage {

    public static String teacherDoesNotHaveCourse(Integer teacherId, Integer courseId){
        return String.format("Teacher with teacher_id{%d} does have not course with course_id{%d}", teacherId, courseId);
    }

    public static String teacherDoesNotHaveLesson(Integer teacherId, Integer lessonId){
        return String.format("Teacher with teacher_id{%d} does have not lesson with lesson_id{%d}", teacherId, lessonId);
    }

    public static String studentDoesNotHaveCourse(Integer studentId, Integer courseId){
        return String.format("Student with student_id{%d} does have have course with course_id{%d}", studentId, courseId);
    }

    public static String studentDoesNotHaveLesson(Integer studentId, Integer lessonId){
        return String.format("Student with student_id{%d} does have not lesson with lesson_id{%d}", studentId, lessonId);
    }

    public static String userLoginNotFound(String login){
        return String.format("User with login{%s} not found.", login);
    }

    public static String userAlreadyExistsWithLogin(String login){
        return String.format("User with login{%s} already exists.", login);
    }

    public static String lessonNotFound(Integer lessonId){
        return String.format("Lesson with id{%d} not found.", lessonId);
    }

    public static String lessonNotFoundWithCourse(Integer lessonId, Integer courseId){
        return String.format("Lesson with id {%d} not found with course {%d}", lessonId, courseId);
    }

    public static String attendanceNotFoundException(Integer attendanceId) {
        return String.format("Attendance with id{%d} not found", attendanceId);
    }

    public static String attendanceNotFoundWithLessonException(Integer attendanceId, Integer lessonId) {
        return String.format("Attendance with id{%d} not found with lesson{%d}", attendanceId, lessonId);
    }

    public static String attendanceNotExistsOrHasExpiredException() {
        return "Attendance not exists or has expired.";
    }

    public static String studentNotFound(Integer studentId) {
        return String.format("Student not found with id{%d}", studentId);
    }

    public static String teacherNotFound(Integer teacherId) {
        return String.format("Teacher not found with id{%d}", teacherId);
    }

    public static String attendanceAccessAlreadyGiven(Integer producerId) {
        return String.format("User {%d} has already gave attendance access for another student.", producerId);
    }

    public static String attendanceAccessAlreadyTaken(Integer consumerId) {
        return String.format("User {%d} has already took attendance access from another student.", consumerId);
    }

    public static String lessonNotFoundWithCourseAndGroup(String group, Integer courseId) {
        return String.format("Lesson[%s] notFound with course_id{%d}", group, courseId);
    }

    public static String teacherDoesNotHaveLesson(Integer teacherId, String group) {
        return String.format("Teacher with teacher_id{%d} does have not lesson[%s]", teacherId, group);
    }

    public static String studentDoesNotHaveLesson(Integer studentId, String group) {
        return String.format("Student with student_id{%d} does have not lesson[%s]", studentId, group);
    }
}
