package com.example.lostnfound.faker;

import com.example.lostnfound.enums.Category;
import com.example.lostnfound.enums.Status;
import com.example.lostnfound.model.Post;
import com.example.lostnfound.repository.PostRepo;
import com.example.lostnfound.repository.UserRepo;
import com.example.lostnfound.service.AI.GeminiChat.GeminiResponseImpl;
import com.example.lostnfound.service.post.PostService;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.javafaker.Faker;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;
import com.example.lostnfound.model.User;
import com.example.lostnfound.enums.Role;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Objects;

@Component
public class DataLoader implements CommandLineRunner {

   
    private final PostRepo postRepository;
    private final UserRepo userRepo;
    private final GeminiResponseImpl myGemini;
    private final ObjectMapper objectMapper;
    private final PostService postService;

    DataLoader(PostRepo postRepository, UserRepo userRepo, GeminiResponseImpl myGemini, ObjectMapper objectMapper, PostService postService) {
        this.postRepository = postRepository;
        this.userRepo = userRepo;
        this.myGemini = myGemini;
        this.objectMapper = objectMapper;
        this.postService = postService;
    }


    @Override
    public void run(String... args) throws Exception {
        Logger logger = LoggerFactory.getLogger(DataLoader.class);
        int postCount = postRepository.findAll().size();
        int extraPostNeed = 50 - postCount;
        int userCount = userRepo.findAll().size();
        int extraUserNeed = 5 - userCount;


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
                logger.info("Generated user {}/{}", i + 1, extraUserNeed);
            } catch (Exception e) {
                logger.error("Failed to save user {}: {}", i + 1, e.getMessage());
            }
        }

        // Adding a delay to ensure the first user entity is pushed
        try {
            Thread.sleep(2000); // 2 seconds delay
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            logger.error("Thread was interrupted, Failed to complete operation");
        }
        for (int i = 0; i < extraPostNeed; i++) {
            String instruction = """
                Lost and found Post model schema:
                {
                    title:,
                    description:,
                    location:,
                    date: yyyy-mm-dd,
                    time: hh:mm, ex. 22:30, (No AM/PM),
                    category: DOCUMENTS | ELECTRONICS | JEWELLERIES | ACCESSORIES | CLOTHES | MOBILE;,
                    status: LOST | FOUND,
                    range: int
                }
                
                Give Random instance example in DICTIONARY format. Make the description realistic and very descriptive.
                Have the location from Bangladesh. 
                
                Strict RULE: NO EXTRA TEXT. MUST USE DOUBLE QUOTES. NO SINGLE QUOTES.
                
                Giving you two characters. Keep the location close to first character and the lost item close to the second character.
                For category and status, i am giving you the index of the given enum.
                """;
            instruction += " first character: " + faker.lorem().characters(1) + ";\n";
            instruction += " second character: " + faker.lorem().characters(1) + ";\n";
            instruction += " category: " + faker.number().numberBetween(0, Category.values().length) + ";\n";
            instruction += " status: " + faker.number().numberBetween(0, Status.values().length) + ";\n";
            String response = myGemini.rawQuery(instruction);
            System.out.println("AI Response: " + response);
            Objects.requireNonNull(response);
            //make sure the response is a valid JSON
            StringBuilder rb = new StringBuilder(response);
            while (rb.charAt(rb.length() - 1) != '}') {
                rb.deleteCharAt(rb.length() - 1);
            }
            while(rb.charAt(0) != '{') {
                rb.deleteCharAt(0);
            }
            response = rb.toString();
            Post post = objectMapper.readValue(response, Post.class);
            post.setUser(userRepo.findById(faker.number().numberBetween(1, userRepo.count() + 1)).orElse(null));
            try {
                postService.savePost(post);
                logger.info("Generated post {}/{}", i + 1, extraPostNeed);
                //delay of 5s, as we have API rate limit (15 Requests per minute)
                Thread.sleep(5000);
            } catch (Exception e) {
                logger.error("Failed to save post  {}: {}", i + 1, e.getMessage());
            }
        }
    }
}