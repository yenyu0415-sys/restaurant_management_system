package com.example.demo.repository;

import com.example.demo.entity.Menu;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface MenuRepositoryJPA
        extends JpaRepository<Menu, Long> {
    List<Menu> findByIsActiveTrue();
}

