package com.example.Recipe.API.repositories;

import com.example.Recipe.API.models.Review;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface ReviewRepo extends JpaRepository<Review, Long> {

    List<Review> findByUser_Username(String user);
}