package com.sawoo.pipeline.api.service.person;

import com.googlecode.jmapper.JMapper;
import com.sawoo.pipeline.api.dto.person.PersonDTO;
import com.sawoo.pipeline.api.model.person.Person;
import com.sawoo.pipeline.api.service.base.BaseMapper;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Getter
@RequiredArgsConstructor
@Component
public class PersonMapper implements BaseMapper<PersonDTO, Person> {

    private final JMapper<PersonDTO, Person> mapperOut = new JMapper<>(PersonDTO.class, Person.class);
    private final JMapper<Person, PersonDTO> mapperIn = new JMapper<>(Person.class, PersonDTO.class);
}
