package com.example.Recipe.API;

import com.example.Recipe.API.models.Ingredient;
import com.example.Recipe.API.models.Recipe;
import com.example.Recipe.API.models.Review;
import com.example.Recipe.API.models.SecurityModels.CustomUserDetails;
import com.example.Recipe.API.models.Step;
import com.example.Recipe.API.repositories.RecipeRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Profile;

import java.util.Set;

@SpringBootApplication
@Profile("test")
public class RecipeMainTest implements CommandLineRunner {

    @Autowired
    RecipeRepo recipeRepo;

    @Override
    public void run(String... args) throws Exception {
        System.out.println("STARTING WITH TEST DATABASE SETUP");
        if (recipeRepo.findAll().isEmpty()) {

            Ingredient ingredient = Ingredient.builder().name("flour").state("dry").amount("2 cups").build();
            Step step1 = Step.builder().description("put flour in bowl").stepNumber(1).build();
            Step step2 = Step.builder().description("eat it?").stepNumber(2).build();
            CustomUserDetails user1 = CustomUserDetails.builder().username("billy").password("password").build();
            CustomUserDetails user2 = CustomUserDetails.builder().username("tom").password("password").build();
            CustomUserDetails user3 = CustomUserDetails.builder().username("mark").password("password").build();
            CustomUserDetails user4 = CustomUserDetails.builder().username("arthur").password("password").build();

            Review review = Review.builder().description("tasted pretty bad").rating(2).user(user1).build();

            Recipe recipe1 = Recipe.builder()
                    .name("test recipe")
                    .difficultyRating(10)
                    .minutesToMake(2)
                    .ingredients(Set.of(ingredient))
                    .steps(Set.of(step1, step2))
                    .reviews(Set.of(review))
                    .user(user1)
                    .build();

            recipeRepo.save(recipe1);

            ingredient.setId(null);
            Recipe recipe2 = Recipe.builder()
                    .steps(Set.of(Step.builder().description("test").build()))
                    .ingredients(Set.of(Ingredient.builder().name("test ing").amount("1").state("dry").build()))
                    .name("another test recipe")
                    .difficultyRating(10)
                    .minutesToMake(2)
                    .user(user2)
                    .build();
            recipeRepo.save(recipe2);

            Recipe recipe3 = Recipe.builder()
                    .steps(Set.of(Step.builder().description("test 2").build()))
                    .ingredients(Set.of(Ingredient.builder().name("test ing 2").amount("2").state("wet").build()))
                    .name("another another test recipe")
                    .difficultyRating(5)
                    .minutesToMake(2)
                    .user(user3)
                    .build();

            recipeRepo.save(recipe3);

            Recipe recipe4 = Recipe.builder()
                    .name("chocolate and potato chips")
                    .difficultyRating(10)
                    .minutesToMake(1)
                    .ingredients(Set.of(
                            Ingredient.builder().name("potato chips").amount("1 bag").build(),
                            Ingredient.builder().name("chocolate").amount("1 bar").build()))
                    .steps(Set.of(
                            Step.builder().stepNumber(1).description("eat both items together").build()))
                    .reviews(Set.of(
                            Review.builder().user(user2).rating(10).description("this stuff is so good").build()
                    ))
                    .user(user4)
                    .build();

            recipeRepo.save(recipe4);
            System.out.println("FINISHED TEST DATABASE SETUP");
        }
    }
}