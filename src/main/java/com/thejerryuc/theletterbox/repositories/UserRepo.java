package com.thejerryuc.theletterbox.repositories;

import com.thejerryuc.theletterbox.models.User;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;


@Repository
public interface UserRepo extends MongoRepository<User, String> {
    Optional<User> findUserByEmail(String email);
}
