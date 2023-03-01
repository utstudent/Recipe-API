package com.example.Recipe.API.services;

import com.example.Recipe.API.exceptions.NoSuchRecipeException;
import com.example.Recipe.API.exceptions.NoSuchReviewException;
import com.example.Recipe.API.models.Recipe;
import com.example.Recipe.API.models.Review;
import com.example.Recipe.API.repositories.ReviewRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Optional;

@Service
public class ReviewService {

    @Autowired
    ReviewRepo reviewRepo;

    @Autowired
    RecipeService recipeService;

    public List<Review> getAllReviews() throws NoSuchReviewException {
        List<Review> reviews = reviewRepo.findAll();

        if (reviews.isEmpty()) {
            throw new NoSuchReviewException("There are no reviews yet :( feel free to add one though");
        }
        return reviews;
    }

    public Review getReviewById(Long id) throws NoSuchReviewException {
        Optional<Review> review = reviewRepo.findById(id);

        if (review.isEmpty()) {
            throw new NoSuchReviewException("The review with ID " + id + " could not be found.");
        }
        return review.get();
    }

    // #2
    public List<Recipe> getRecipesByMinAvgReviewRating(int rating) throws NoSuchRecipeException, NoSuchReviewException {

        List<Recipe> allRecipes = recipeService.getAllRecipes();

        List<Recipe> minAvgReviewRatingRecipes = new ArrayList<>();

        for (Recipe recipe : allRecipes) {
            if (getRecipeReviewRatingAvg(recipe) >= rating) {
                minAvgReviewRatingRecipes.add(recipe);
            }
        }
        return minAvgReviewRatingRecipes;
    }

    // #2
    public double getRecipeReviewRatingAvg(Recipe recipe) {

        if (recipe.getReviews() == null || recipe.getReviews().size() == 0) {
            return 0.0;
        }
        Collection<Review> reviews = recipe.getReviews();
        double avg = 0;
        int total = 0;
        for (Review review : reviews) {
            total = total + review.getRating();
        }
        avg = total / reviews.size() * 1.0;

        return avg;

    }

    public ArrayList<Review> getReviewByRecipeId(Long recipeId) throws NoSuchRecipeException, NoSuchReviewException {
        Recipe recipe = recipeService.getRecipeById(recipeId);

        ArrayList<Review> reviews = new ArrayList<>(recipe.getReviews());

        if (reviews.isEmpty()) {
            throw new NoSuchReviewException("There are no reviews for this recipe.");
        }
        return reviews;
    }

    public List<Review> getReviewByUsername(String username) throws NoSuchReviewException {
        List<Review> reviews = reviewRepo.findByUser_Username(username);

        if (reviews.isEmpty()) {
            throw new NoSuchReviewException("No reviews could be found for username " + username);
        }

        return reviews;
    }

    public Recipe postNewReview(Review review, Long recipeId) throws NoSuchRecipeException {
        Recipe recipe = recipeService.getRecipeById(recipeId);

        // #7
        if (recipeService.getRecipeById(recipeId).getAuthor().equals(review.getAuthor())) {
            throw new NoSuchRecipeException("The review you are trying to post is for your own recipe! Sorry not allowed");
        } else if (review.getRating() == null) { // #8
            throw new NoSuchRecipeException("The review you are trying to post has an invalid rating, please try again!");
        } else {
            recipe.getReviews().add(review);
            recipeService.updateRecipe(recipe, false);
            return recipe;
        }
    }

    public Review deleteReviewById(Long id) throws NoSuchReviewException {
        Review review = getReviewById(id);

        if (null == review) {
            throw new NoSuchReviewException("The review you are trying to delete does not exist.");
        }
        reviewRepo.deleteById(id);
        return review;
    }

    public Review updateReviewById(Review reviewToUpdate) throws NoSuchReviewException {
//        try {
//            Review review = getReviewById(reviewToUpdate.getId());
//        } catch (NoSuchReviewException e) {
//            throw new NoSuchReviewException("The review you are trying to update. Maybe you meant to create one? If not," +
//                    "please double check the ID you passed in.");
//        }
        reviewRepo.save(reviewToUpdate);
        return reviewToUpdate;
    }
}