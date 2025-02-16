package com.example.lostnfound.faker;

import com.example.lostnfound.enums.Catagory;
import com.example.lostnfound.enums.Status;
import com.example.lostnfound.model.Post;
import com.example.lostnfound.repository.PostRepo;
import com.example.lostnfound.repository.UserRepo;
import com.github.javafaker.Faker;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;
import com.example.lostnfound.model.User;
import java.time.LocalDate;
import java.time.LocalTime;
import com.example.lostnfound.enums.Role;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

@Component
public class DataLoader implements CommandLineRunner {

   
    private final PostRepo postRepository;
    private final UserRepo userRepo;

    DataLoader(PostRepo postRepository, UserRepo userRepo) {
        this.postRepository = postRepository;
        this.userRepo = userRepo;
    }


    @Override
    public void run(String... args) throws Exception {

        int postCount = postRepository.findAll().size();
        int extraNeed = 100 - postCount;
        int userCount = userRepo.findAll().size();
        int extraUserNeed = 50 - userCount;


        Faker faker = new Faker();

        for (int i = 0; i < extraUserNeed; i++) {
            User user = new User();
            user.setName(faker.name().fullName());
            user.setEmail(faker.internet().emailAddress());
            user.setPassword(faker.internet().password());
            BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
            user.setPassword(passwordEncoder.encode(faker.internet().password()));
            user.setAddress(faker.address().fullAddress());
            user.setRole(Role.values()[faker.number().numberBetween(0, Role.values().length)]);
            user.setDepartment(faker.company().profession());


            try {
                userRepo.save(user);
                System.out.printf("BEEP: Generated user %d/%d%n", i + 1, extraUserNeed);
            } catch (Exception e) {
                System.err.printf("BOOP: Failed to save user %d: %s%n", i + 1, e.getMessage());
            }
        }

        // Adding a delay to ensure the first user entity is pushed
        try {
            Thread.sleep(2000); // 2 seconds delay
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            System.err.println("Thread was interrupted, Failed to complete operation");
        }
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
            post.setUser(userRepo.findById(faker.number().numberBetween(1, userRepo.count() + 1)).orElse(null));

            try {
                postRepository.save(post);
                System.out.printf("BEEP: Generated post %d/%d%n", i + 1, extraNeed);
            } catch (Exception e) {
                System.err.printf("BOOP: Failed to save post %d: %s%n", i + 1, e.getMessage());
            }
        }
    }
}