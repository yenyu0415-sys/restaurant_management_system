
package com.example.demo.controller;

import com.example.demo.entity.DiningTable;
import com.example.demo.service.DiningTableService;

import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/dining-tables")
public class DiningTableController {

    private final DiningTableService diningTableService;

    public DiningTableController(
            DiningTableService diningTableService) {
        this.diningTableService = diningTableService;
    }

    // 전체 테이블 조회
    @GetMapping
    public List<DiningTable> getAllTables() {
        return diningTableService.getAllTables();
    }

    // 특정 테이블 조회
    @GetMapping("/{id}")
    public DiningTable getTableById(
            @PathVariable Long id) {
        return diningTableService.getTableById(id);
    }
}
