package com.laitsystem.zenon_rt_alarm.component;

import com.laitsystem.zenon_rt_alarm.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByUsername(String username);
}