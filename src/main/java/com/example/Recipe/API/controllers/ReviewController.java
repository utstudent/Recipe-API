package com.example.Recipe.API.controllers;
import com.example.Recipe.API.exceptions.NoSuchRecipeException;
import com.example.Recipe.API.exceptions.NoSuchReviewException;
import com.example.Recipe.API.models.Recipe;
import com.example.Recipe.API.models.Review;
import com.example.Recipe.API.models.SecurityModels.CustomUserDetails;
import com.example.Recipe.API.services.ReviewService;
import jdk.jshell.spi.ExecutionControl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/review")
public class ReviewController {

    @Autowired
    ReviewService reviewService;

    @GetMapping
    public ResponseEntity<?> getAllReviews() {
        try {
            return ResponseEntity.ok(reviewService.getAllReviews());
        } catch (NoSuchReviewException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        }
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getReviewById(@PathVariable("id") Long id) {
        try {
            Review retrievedReview = reviewService.getReviewById(id);
            return ResponseEntity.ok(retrievedReview);
        } catch (IllegalStateException | NoSuchReviewException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @GetMapping("/recipe/{recipeId}")
    public ResponseEntity<?> getReviewByRecipeId(@PathVariable("recipeId") Long recipeId) {
        try {
            List<Review> reviews = reviewService.getReviewByRecipeId(recipeId);
            return ResponseEntity.ok(reviews);
        } catch (NoSuchRecipeException | NoSuchReviewException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @GetMapping("/user/{username}")
    public ResponseEntity<?> getReviewByUsername(@PathVariable("username") String username) {
        try {
            List<Review> reviews = reviewService.getReviewByUsername(username);
            return ResponseEntity.ok(reviews);
        } catch (NoSuchReviewException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    // #2
    @GetMapping("/search/{rating}")
    public ResponseEntity<?> getAllRecipesWithMinAvgRating(@PathVariable("rating") int rating){
        try {
            List<Recipe> recipesWithMinAvgRating = reviewService.getRecipesByMinAvgReviewRating(rating);
            return ResponseEntity.ok(recipesWithMinAvgRating);
        } catch (NoSuchRecipeException | NoSuchReviewException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        }
    }
    @PostMapping("/{recipeId}")
    public ResponseEntity<?> postNewReview(@RequestBody Review review, @PathVariable("recipeId") Long recipeId, Authentication authentication) {
        try {
            review.setUser((CustomUserDetails) authentication.getPrincipal());
            Recipe insertedRecipe = reviewService.postNewReview(review, recipeId);
            return ResponseEntity.created(insertedRecipe.getLocationURI()).body(insertedRecipe);
        } catch (NoSuchRecipeException | IllegalStateException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasPermission(#id, 'Review', 'delete')")
    public ResponseEntity<?> deleteReviewById(@PathVariable("id") Long id) {
        try {
            Review review = reviewService.deleteReviewById(id);
            return ResponseEntity.ok(review);
        } catch (NoSuchReviewException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

//    @PatchMapping
//    @PreAuthorize("hasPermission(#reviewToUpdate.id, 'Review', 'edit')")
//    public ResponseEntity<?> updateReviewById(@RequestBody Review reviewToUpdate) {
//        try {
//            Review review = reviewService.updateReviewById(reviewToUpdate);
//            return ResponseEntity.ok(review);
//        } catch (NoSuchReviewException e) {
//            return ResponseEntity.badRequest().body(e.getMessage());
//        }
//    }

    @PatchMapping
    @PreAuthorize("hasPermission(#reviewToUpdate.id, 'Review', 'edit')")
    public ResponseEntity<?> updateReviewById(@RequestBody Review reviewToUpdate, Authentication authentication) {
        try {
            reviewToUpdate.setUser((CustomUserDetails) authentication.getPrincipal());
            Review review = reviewService.updateReviewById(reviewToUpdate);
            return ResponseEntity.ok(review);
        } catch (NoSuchReviewException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }
}
