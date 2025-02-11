package com.example.lostnfound.controller;

import com.example.lostnfound.model.Person;
import com.example.lostnfound.service.person.PersonService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
public class PersonController {

    @Autowired
    private PersonService personService;

    @PostMapping("/persons")
    public Person addPerson(@RequestBody Person person) {
        return personService.addPerson(person);
    }

    @GetMapping("/persons")
    public List<Person> getPersonList() {
        return personService.getPersonList();
    }

    @GetMapping("/persons/{id}")
    public Person getPerson(@PathVariable("id") Long id) {
        return personService.getPerson(id);
    }

    @DeleteMapping("/persons/{id}")
    public String deletePerson(@PathVariable("id") Long id) {
        personService.deletePerson(id);
        return "Post deleted successfully";
    }

    @PutMapping("persons/{id}")
    public Person updatePerson(@PathVariable("id") Long id, @RequestBody Person person) {
        return personService.updatePerson(id, person);
    }

}
