package com.example.lostnfound.repository;

import com.example.lostnfound.model.Image;
import com.example.lostnfound.model.Post;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ImageRepository extends JpaRepository<Image, Long> {
	@Query(value = "SELECT * FROM images ORDER BY embedding <=> CAST(:queryEmbedding AS vector) LIMIT :topK", nativeQuery = true)
	List<Image> findTopKSimilarPosts(@Param("queryEmbedding") float[] queryEmbedding, @Param("topK") Long topK);
}