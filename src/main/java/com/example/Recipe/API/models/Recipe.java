package com.example.Recipe.API.models;
import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.Collection;

@Entity
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Recipe {

    @Id
    @GeneratedValue
    private Long id;

    @Column(nullable = false)
    private String name;

    // #4
    @Column(nullable = false)
    private String username;

    @Column(nullable = false)
    private Integer minutesToMake;

    @Column(nullable = false)
    private Integer difficultyRating;

    @OneToMany(cascade = CascadeType.ALL)
    @JoinColumn(name = "recipeId", nullable = false, foreignKey = @ForeignKey)
    private Collection<Ingredient> ingredients = new ArrayList<>();

    @OneToMany(cascade = CascadeType.ALL)
    @JoinColumn(name = "recipeId", nullable = false, foreignKey = @ForeignKey)
    private Collection<Step> steps = new ArrayList<>();

    @OneToMany(cascade = CascadeType.ALL)
    @JoinColumn(name = "recipeId", nullable = false, foreignKey = @ForeignKey)
    private Collection<Review> reviews;

    @Transient
    @JsonIgnore
    private URI locationURI;

    public void setDifficultyRating(int difficultyRating) {
        if (difficultyRating < 0 || difficultyRating > 10) {
            throw new IllegalStateException("Difficulty rating must be between 0 and 10.");
        }
        this.difficultyRating = difficultyRating;
    }

    public void validate() throws IllegalStateException {
        if (ingredients.size() == 0) {
            throw new IllegalStateException("You have to have at least one ingredient for your recipe!");
        } else if (steps.size() == 0) {
            throw new IllegalStateException("You have to include at least one step for your recipe!");
        } else if (difficultyRating == null || difficultyRating < 0 || difficultyRating > 10) {
            // #9
            throw new IllegalStateException("The recipe you are trying to post has an invalid difficulty rating, please try again!");
        }
    }

    @PostLoad
    public void generateLocationURI() {
        try {
            locationURI = new URI(
                    ServletUriComponentsBuilder.fromCurrentContextPath()
                            .path("/recipes/")
                            .path(String.valueOf(id))
                            .toUriString());
        } catch (URISyntaxException e) {
            //Exception should stop here.
        }
    }
}