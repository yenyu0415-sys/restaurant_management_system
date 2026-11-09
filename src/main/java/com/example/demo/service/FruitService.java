package com.example.demo.service;

import com.example.demo.entity.Fruit;
import com.example.demo.exception.ResourceNotFoundException;
import com.example.demo.repository.FruitRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class FruitService {

    private final FruitRepository fruitRepositoryJPA;

    public FruitService(FruitRepository fruitRepositoryJPA) {
        this.fruitRepositoryJPA = fruitRepositoryJPA;
    }

    public Fruit saveFruit(Fruit fruit) {
        return fruitRepositoryJPA.save(fruit);
    }

    public List<Fruit> getAll() {
        return fruitRepositoryJPA.findAll();
    }

    public Fruit getById(Long id) {
        return fruitRepositoryJPA.findById(id)
                .orElseThrow( ()-> new ResourceNotFoundException("Fruit not found:"+id)
                );
    }

    public Fruit updateFruit(Long id, Fruit newFruit) {
        Fruit fruit = getById(id);
        fruit.setName(newFruit.getName());
        fruit.setPrice(newFruit.getPrice());
        fruit.setColor(newFruit.getColor());
        fruitRepositoryJPA.save(fruit);
        return fruit;
    }

    public void deleteFruit(Long id) {
         fruitRepositoryJPA.deleteById(id);
    }
}