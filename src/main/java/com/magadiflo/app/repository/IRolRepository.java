package com.magadiflo.app.repository;

import com.magadiflo.app.domain.Role;
import org.springframework.data.jpa.repository.JpaRepository;

public interface IRolRepository extends JpaRepository<Role, Long> {

    Role findByName(String name);

}
