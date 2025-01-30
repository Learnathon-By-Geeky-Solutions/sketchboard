package com.example.lostnfound.service;

import com.example.lostnfound.model.Person;
import com.example.lostnfound.model.Post;

import java.util.List;

public interface PersonService {
    Person addPerson(Person person);

    List<Person> getPersonList();

    Person getPerson(Long id);

    void deletePerson(Long id);

    Person updatePerson(Long id, Person person);
}
