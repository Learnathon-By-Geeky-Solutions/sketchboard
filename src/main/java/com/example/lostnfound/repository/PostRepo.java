package com.example.lostnfound.repository;

import com.example.lostnfound.model.Post;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface PostRepo extends JpaRepository<Post, Integer>{
    @Query(value = "SELECT * FROM post " +
               "WHERE (title || ' ' || description || ' ' || location || ' ' || category || ' ' || status) " +
               "ILIKE CONCAT('%', :searchTerm, '%')", nativeQuery = true)
    List<Post> searchPosts(@Param("searchTerm") String searchTerm);

    List<Post> findByUserUserId(Long userId);

}  
