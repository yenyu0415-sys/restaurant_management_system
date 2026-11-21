package com.example.demo.repository;

import com.example.demo.entity.DiningSession;
import org.springframework.data.jpa.repository.JpaRepository;

public interface DiningSessionRepository
        extends JpaRepository<DiningSession, Long> {
}

