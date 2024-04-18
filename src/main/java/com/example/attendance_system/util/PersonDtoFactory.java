package com.example.attendance_system.util;

import com.example.attendance_system.dto.PersonDto;
import com.example.attendance_system.model.Person;

public class PersonDtoFactory {

    public static PersonDto convert(Person person) {
        return new PersonDto(
                person.getName(),
                person.getSurname(),
                person.getEmail(),
                person.getBirthDate());
    }
}
