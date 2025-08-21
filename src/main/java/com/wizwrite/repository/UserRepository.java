package com.wizwrite.repository;

import com.wizwrite.entity.ContentHistory;
import com.wizwrite.entity.User;
import com.wizwrite.entity.UserCredit;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByUsername(String username);
}
