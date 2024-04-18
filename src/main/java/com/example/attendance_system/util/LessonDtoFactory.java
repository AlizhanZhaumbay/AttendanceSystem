package com.example.attendance_system.util;

import com.example.attendance_system.dto.LessonDto;
import com.example.attendance_system.model.Lesson;
import com.example.attendance_system.model.Person;

public class LessonDtoFactory {
    public static LessonDto convert(Lesson lesson) {
        Person teacher = lesson.getTeacher().getPerson();
        String name = String.format("%s %s", teacher.getName(), teacher.getSurname());
        return new LessonDto(lesson.getId(), name, lesson.getStartTime(), lesson.getEndTime(),
                lesson.getDayOfWeek(), lesson.getCourse(), lesson.getGroup());
    }
}
