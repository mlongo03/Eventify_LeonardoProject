package com.eventify.app.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.eventify.app.model.User;

@Repository
public interface IUserRepository extends JpaRepository<User, Long> {
}
