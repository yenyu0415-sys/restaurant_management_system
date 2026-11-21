
package com.example.demo.service;

import com.example.demo.entity.DiningTable;
import com.example.demo.repository.DiningTableRepository;
import org.springframework.stereotype.Service;
import com.example.demo.exception.ResourceNotFoundException;
import java.util.List;

@Service
public class DiningTableService {

    private final DiningTableRepository diningTableRepository;

    public DiningTableService(
            DiningTableRepository diningTableRepository) {
        this.diningTableRepository = diningTableRepository;
    }

    // 전체 테이블 조회
    public List<DiningTable> getAllTables() {
        return diningTableRepository.findAll();
    }

    // ID로 테이블 조회

    public DiningTable getTableById(Long id) {
        return diningTableRepository.findById(id)
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "Dining table not found: " + id));
    }
}
