package com.example.Recipe.API.repositories;

import com.example.Recipe.API.models.SecurityModels.CustomUserDetails;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepo extends JpaRepository<CustomUserDetails, Long> {

    CustomUserDetails findByUsername(String user);
}