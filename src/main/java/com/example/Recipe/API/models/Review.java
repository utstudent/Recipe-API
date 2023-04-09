package com.example.Recipe.API.models;
import com.example.Recipe.API.models.SecurityModels.CustomUserDetails;
import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.*;

import javax.persistence.*;
import javax.validation.constraints.NotNull;

@Entity
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Review {

    @Id
    @GeneratedValue
    private Long id;

    @ManyToOne(optional = false)
    @JoinColumn
    @JsonIgnore
    private CustomUserDetails user;
    @Column(nullable = false)
    private Integer rating;

    private String description;

//    @ManyToOne
//    @JoinColumn(
//            name = "recipe_id",
//            nullable = false,
//            foreignKey = @ForeignKey
//    )
//    @JsonIgnore
//    private Recipe recipe;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "recipe_id")
    private Recipe recipe;

    public void setRating(int rating) {
        if (rating <= 0 || rating > 10) {
            throw new IllegalStateException("Rating must be between 0 and 10.");
        }
        this.rating = rating;
    }
    public String getAuthor() {
        return user.getUsername();
    }
}
