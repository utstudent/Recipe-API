package com.example.Recipe.API.repositories;

import com.example.Recipe.API.models.Recipe;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface RecipeRepo extends JpaRepository<Recipe, Long> {

    List<Recipe> findByNameContainingIgnoreCase(String name);

    List<Recipe> findByDifficultyRatingGreaterThanEqual(int rating);

    List<Recipe> findByNameContainingIgnoreCaseAndDifficultyRatingLessThanEqual(String name, int rating);

    List<Recipe> findByDifficultyRatingIs(int rating);

    List<Recipe> findByUsernameContainingIgnoreCase(String name);

}