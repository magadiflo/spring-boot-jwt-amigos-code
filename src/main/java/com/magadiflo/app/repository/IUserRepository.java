package com.magadiflo.app.repository;

import com.magadiflo.app.domain.User;
import org.springframework.data.jpa.repository.JpaRepository;

public interface IUserRepository extends JpaRepository<User, Long> {

    User findByUsername(String username);

}
