package com.example.demo.controller;

import com.example.demo.entity.Fruit;
import com.example.demo.service.FruitService;
import org.springframework.web.bind.annotation.*;

        import java.util.List;

@RestController
@RequestMapping("/fruits")
public class FruitController {

    private final FruitService fruitService;

    public FruitController(FruitService fruitService) {
        this.fruitService = fruitService;
    }

    @PostMapping
    public Fruit createFruit(@RequestBody Fruit fruit) {
        return fruitService.saveFruit(fruit);

    }

    @GetMapping
    public List<Fruit> getAllFruits() {
        return fruitService.getAll();
    }

    @GetMapping("/{id}")
    public Fruit getFruitByID(@PathVariable Long id) {
        return fruitService.getById(id);
    }

    @PutMapping("/{id}")
    public Fruit updateFruit(
            @PathVariable Long id,
            @RequestBody Fruit newFruit) {

        return fruitService.updateFruit(
                id,
                newFruit);
    }

    @DeleteMapping("/{id}")
    public void deleteFruit(@PathVariable Long id) {
        fruitService.deleteFruit(id);
    }
}