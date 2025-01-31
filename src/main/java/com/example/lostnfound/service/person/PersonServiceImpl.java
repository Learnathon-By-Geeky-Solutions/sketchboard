package com.example.lostnfound.service.person;

import com.example.lostnfound.model.Person;

import com.example.lostnfound.repository.PersonRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Objects;

@Service
@Component
public class PersonServiceImpl implements PersonService {

    @Autowired
    private PersonRepo personRepo;

    @Override
    public Person addPerson(Person person) {
        return personRepo.save(person);
    }

    @Override
    public List<Person> getPersonList() {
        return personRepo.findAll();
    }

    @Override
    public Person getPerson(Long id) {
        return personRepo.findById(id).get();
    }

    @Override
    public void deletePerson(Long id) {
        personRepo.deleteById(id);
    }

    @Override
    public Person updatePerson(Long id, Person person) {
        Person personToUpdate = personRepo.findById(id).get();
        if(Objects.nonNull(personToUpdate.getName()) && !"".equalsIgnoreCase(personToUpdate.getName())) {
            personToUpdate.setName(person.getName());
        }
        if(Objects.nonNull(personToUpdate.getEmail()) && !"".equalsIgnoreCase(personToUpdate.getEmail())) {
            personToUpdate.setEmail(personToUpdate.getEmail());
        }
        if(Objects.nonNull(personToUpdate.getPassword()) && !"".equalsIgnoreCase(personToUpdate.getPassword())) {
            personToUpdate.setPassword(personToUpdate.getPassword());
        }
        if(Objects.nonNull(personToUpdate.getAddress()) && !"".equalsIgnoreCase(personToUpdate.getAddress())) {
            personToUpdate.setAddress(personToUpdate.getAddress());
        }
        if(Objects.nonNull(personToUpdate.getDept()) && !"".equalsIgnoreCase(personToUpdate.getDept())) {
            personToUpdate.setDept(personToUpdate.getDept());
        }
        return personRepo.save(personToUpdate);
    }
}
