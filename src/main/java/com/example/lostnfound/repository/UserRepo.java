package com.example.lostnfound.repository;

import com.example.lostnfound.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;


@Repository
public interface UserRepo extends JpaRepository<User, Long> {
    public User findByName(String name);   
}
