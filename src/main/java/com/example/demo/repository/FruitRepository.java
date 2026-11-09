//import com.example.demo.entity.Menu;
//import com.example.demo.service.MenuServiceJPA;
//import org.springframework.web.bind.annotation.*;
//import java.util.List;



package com.example.demo.repository;

import com.example.demo.entity.Fruit;
import org.springframework.data.jpa.repository.JpaRepository;

public interface FruitRepository
        extends JpaRepository<Fruit, Long> {
}