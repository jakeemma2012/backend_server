package com.ProjectMovie.Auth.Repositories;

import com.ProjectMovie.Auth.Entities.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UserRepositories extends JpaRepository<User, Integer>{
    
    Optional<User> findByUsername(String username);

    Optional<User> findByEmail(String email);

    boolean existsByUsername(String username);

    boolean existsByEmail(String email);
    
}
