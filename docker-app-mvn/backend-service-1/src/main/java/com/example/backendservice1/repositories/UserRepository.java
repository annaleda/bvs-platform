package com.example.backendservice1.repositories;

import com.example.backendservice1.entities.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UserRepository extends JpaRepository<User, String> {
    @Override
    Optional<User> findById(String s);

    Optional<User> findByCname(String cname);
}
