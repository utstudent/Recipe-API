package com.example.Recipe.API.services;

import com.example.Recipe.API.exceptions.NoSuchRecipeException;
import com.example.Recipe.API.exceptions.NoSuchReviewException;
import com.example.Recipe.API.models.Recipe;
import com.example.Recipe.API.models.Review;
import com.example.Recipe.API.repositories.RecipeRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class RecipeService {

    @Autowired
    RecipeRepo recipeRepo;

    @Transactional
    public Recipe createNewRecipe(Recipe recipe) throws IllegalStateException {
        recipe.validate();
        return recipeRepo.save(recipe);
    }

    public Recipe getRecipeById(Long id) throws NoSuchRecipeException {
        Optional<Recipe> recipeOptional = recipeRepo.findById(id);

        if (recipeOptional.isEmpty()) {
            throw new NoSuchRecipeException("No recipe with ID " + id + " could be found.");
        }

        Recipe recipe = recipeOptional.get();
        return recipe;
    }

    public List<Recipe> getRecipesByName(String name) throws NoSuchRecipeException {
        List<Recipe> matchingRecipes = recipeRepo.findByNameContainingIgnoreCase(name);

        if (matchingRecipes.isEmpty()) {
            throw new NoSuchRecipeException("No recipes could be found with that name.");
        }
        return matchingRecipes;
    }

    public List<Recipe> getAllRecipes() throws NoSuchRecipeException {
        List<Recipe> recipes = recipeRepo.findAll();

        if (recipes.isEmpty()) {
            throw new NoSuchRecipeException("There are no recipes yet :( feel free to add one though");
        }
        return recipes;
    }

    // #3
    public List<Recipe> getRecipesByNameAndMaxDifficultyRating(String name, Integer rating) throws NoSuchRecipeException {
        List<Recipe> matchingRecipes = recipeRepo.findByNameContainingIgnoreCaseAndDifficultyRatingLessThanEqual(name, rating);

        if (matchingRecipes.isEmpty()) {
            throw new NoSuchRecipeException("No recipes could be found with that name and maximum difficulty rating.");
        }
        return matchingRecipes;
    }

    // #5
    public List<Recipe> getRecipesByUsername(String username) throws NoSuchRecipeException {
        List<Recipe> matchingRecipes = recipeRepo.findByUsernameContainingIgnoreCase(username);

        if (matchingRecipes.isEmpty()) {
            throw new NoSuchRecipeException("No recipes could be found with that username.");
        }
        return matchingRecipes;
    }

    @Transactional
    public Recipe deleteRecipeById(Long id) throws NoSuchRecipeException {
        try {
            Recipe recipe = getRecipeById(id);
            recipeRepo.deleteById(id);
            return recipe;
        } catch (NoSuchRecipeException e) {
            throw new NoSuchRecipeException(e.getMessage() + " Could not delete.");
        }
    }

    @Transactional
    public Recipe updateRecipe(Recipe recipe, boolean forceIdCheck) throws NoSuchRecipeException {
        try {
            if (forceIdCheck) {
                getRecipeById(recipe.getId());
            }
            recipe.validate();
            Recipe savedRecipe = recipeRepo.save(recipe);
            return savedRecipe;
        } catch (NoSuchRecipeException e) {
            throw new NoSuchRecipeException("The recipe you passed in did not have an ID found in the database." +
                    " Double check that it is correct. Or maybe you meant to POST a recipe not PATCH one.");
        }
    }
}
