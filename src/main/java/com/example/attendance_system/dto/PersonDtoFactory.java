package com.example.attendance_system.dto;

import com.example.attendance_system.model.Person;

public class PersonDtoFactory {

    public static PersonDto convert(Person person) {
        return new PersonDto(
                person.getUserId(),
                person.getName(),
                person.getSurname(),
                person.getEmail(),
                person.getBirthDate());
    }
}
