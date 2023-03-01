package com.example.Recipe.API.controllers;

import com.example.Recipe.API.exceptions.NoSuchRecipeException;
import com.example.Recipe.API.exceptions.NoSuchReviewException;
import com.example.Recipe.API.models.Recipe;
import com.example.Recipe.API.models.SecurityModels.CustomUserDetails;
import com.example.Recipe.API.services.RecipeService;
import com.example.Recipe.API.services.ReviewService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/recipes")
public class RecipeController {

    @Autowired
    RecipeService recipeService;

    @PostMapping
    public ResponseEntity<?> createNewRecipe(@RequestBody Recipe recipe, Authentication authentication) {
        try {
            recipe.setUser((CustomUserDetails) authentication.getPrincipal());
            Recipe insertedRecipe = recipeService.createNewRecipe(recipe);
            return ResponseEntity.created(insertedRecipe.getLocationURI()).body(insertedRecipe);
        } catch (IllegalStateException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getRecipeById(@PathVariable("id") Long id) {
        try {
            Recipe recipe = recipeService.getRecipeById(id);
            return ResponseEntity.ok(recipe);
        } catch (NoSuchRecipeException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        }
    }

    @GetMapping
    public ResponseEntity<?> getAllRecipes() {
        try {
            return ResponseEntity.ok(recipeService.getAllRecipes());
        } catch (NoSuchRecipeException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        }
    }

    @GetMapping("/search/{name}")
    public ResponseEntity<?> getRecipesByName(@PathVariable("name") String name) {
        try {
            List<Recipe> matchingRecipes = recipeService.getRecipesByName(name);
            return ResponseEntity.ok(matchingRecipes);
        } catch (NoSuchRecipeException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        }
    }


    // #3
    @GetMapping("/search/{recipeName}/{rating}")
    public ResponseEntity<?> getRecipesByNameAndMaxDifficultyRating(@PathVariable("recipeName") String name, @PathVariable("rating") int rating) {
        try {
            List<Recipe> matchingRecipes = recipeService.getRecipesByNameAndMaxDifficultyRating(name, rating);
            return ResponseEntity.ok(matchingRecipes);
        } catch (NoSuchRecipeException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        }
    }
    // #5
    @GetMapping("/search/username/{username}")
    public ResponseEntity<?> getRecipesByUsername(@PathVariable("username") String username) {
        try {
            List<Recipe> matchingRecipes = recipeService.getRecipesByUsername(username);
            return ResponseEntity.ok(matchingRecipes);
        } catch (NoSuchRecipeException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        }
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasPermission(#id, 'Recipe', 'delete')")
    public ResponseEntity<?> deleteRecipeById(@PathVariable("id") Long id) {
        try {
            Recipe deletedRecipe = recipeService.deleteRecipeById(id);
            return ResponseEntity.ok("The recipe with ID " + deletedRecipe.getId() +
                    " and name " + deletedRecipe.getName() + " was deleted.");
        } catch (NoSuchRecipeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

//    @PatchMapping("/{id}")
//    @PreAuthorize("hasPermission(#updatedRecipe.id, 'Recipe', 'edit')")
//    public ResponseEntity<?> updateRecipe(@RequestBody Recipe updatedRecipe) {
//        try {
//            Recipe returnedUpdatedRecipe = recipeService.updateRecipe(updatedRecipe, true);
//            return ResponseEntity.ok(returnedUpdatedRecipe);
//        } catch (NoSuchRecipeException | IllegalStateException e) {
//            return ResponseEntity.badRequest().body(e.getMessage());
//        }
//    }

    @PatchMapping("/{id}")
    @PreAuthorize("hasPermission(#updatedRecipe.id, 'Recipe', 'edit')")
    public ResponseEntity<?> updateRecipe(@RequestBody Recipe updatedRecipe) {
        try {
            Recipe returnedUpdatedRecipe = recipeService.updateRecipe(updatedRecipe, true);
            return ResponseEntity.ok(returnedUpdatedRecipe);
        } catch (NoSuchRecipeException | IllegalStateException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }
}
