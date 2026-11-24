
package com.example.demo.controller;

import com.example.demo.entity.DiningSession;
import com.example.demo.service.DiningSessionService;

import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/dining-sessions")
public class DiningSessionController {

    private final DiningSessionService diningSessionService;

    public DiningSessionController(
            DiningSessionService diningSessionService) {
        this.diningSessionService = diningSessionService;
    }

    // 전체 식사 세션 조회
    @GetMapping
    public List<DiningSession> getAllSessions() {
        return diningSessionService.getAllSessions();
    }

    // 특정 식사 세션 조회
    @GetMapping("/{id}")
    public DiningSession getSessionById(@PathVariable Long id) {
        return diningSessionService.getSessionById(id);
    }

    // 테이블 ID로 현재 활성 세션 조회
    @GetMapping("/active/table/{tableId}")
    public DiningSession getActiveSessionByTableId(
            @PathVariable Long tableId) {

        return diningSessionService
                .getActiveSessionByTableId(tableId);
    }

    // 식사 세션 시작
    @PostMapping("/start/{tableId}")
    public DiningSession startSession(@PathVariable Long tableId) {
        return diningSessionService.startSession(tableId);
    }

    // 식사 세션 종료
    @PatchMapping("/{id}/end")
    public DiningSession endSession(@PathVariable Long id) {
        return diningSessionService.endSession(id);
    }
}
