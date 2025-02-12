package com.example.lostnfound.faker;

import com.example.lostnfound.enums.Catagory;
import com.example.lostnfound.enums.Status;
import com.example.lostnfound.model.Post;
import com.example.lostnfound.repository.PostRepo;
import com.github.javafaker.Faker;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.time.LocalTime;

@Component
public class DataLoader implements CommandLineRunner {

    @Autowired
    private PostRepo postRepository;

    @Override
    public void run(String... args) throws Exception {

        int postCount = postRepository.findAll().size();
        int extraNeed = 100 - postCount;

        Faker faker = new Faker();

        for (int i = 0; i < extraNeed; i++) {
            Post post = new Post();
            post.setTitle(faker.lorem().sentence());
            post.setDescription(faker.lorem().paragraph());
            post.setLocation(faker.address().fullAddress());
            post.setDate(LocalDate.now().minusDays(faker.number().numberBetween(0, 30)));
            post.setTime(LocalTime.of(
                faker.number().numberBetween(0, 23),
                faker.number().numberBetween(0, 59)
            ));
            post.setCategory(Catagory.values()[faker.number().numberBetween(0, Catagory.values().length)]);
            post.setStatus(Status.values()[faker.number().numberBetween(0, Status.values().length)]);
            post.setRange(faker.number().numberBetween(1, 100));

            try {
                postRepository.save(post);
                System.out.printf("BEEP: Generated post %d/%d%n", i + 1, extraNeed);
            } catch (Exception e) {
                System.err.printf("BOOP: Failed to save post %d: %s%n", i + 1, e.getMessage());
            }
        }
    }
}