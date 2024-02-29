package com.example.attendance_system.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;

@Data
@Entity
@Table(name = "lesson")
@AllArgsConstructor
@NoArgsConstructor
public class Lesson {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "teacher_id", insertable = false, updatable = false)
    private User teacher;

    @OneToOne(mappedBy = "lesson", cascade = CascadeType.ALL)
    private Attendance attendanceRecord;

    @Column(name = "start_time")
    @DateTimeFormat(pattern = "HH:mm")
    private LocalTime startTime;

    @Column(name = "end_time")
    @DateTimeFormat(pattern = "HH:mm")
    private LocalTime endTime;

    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(name = "enroll",
            joinColumns = @JoinColumn(name = "lesson_id"),
            inverseJoinColumns = @JoinColumn(name = "student_id"))
    private List<User> lessonStudents;

    @Column(name = "day_of_week")
    private String dayOfWeek;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "course_id", insertable = false, updatable = false)
    private Course course;

}
