
package com.example.demo.service;

import com.example.demo.entity.DiningSession;
import com.example.demo.entity.DiningSessionStatus;
import com.example.demo.entity.DiningTable;

import com.example.demo.repository.DiningSessionRepository;
import com.example.demo.repository.DiningTableRepository;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class DiningSessionService {

    private final DiningSessionRepository diningSessionRepository;
    private final DiningTableRepository diningTableRepository;

    // Repository 생성자 주입
    public DiningSessionService(
            DiningSessionRepository diningSessionRepository,
            DiningTableRepository diningTableRepository) {

        this.diningSessionRepository = diningSessionRepository;
        this.diningTableRepository = diningTableRepository;
    }

    // 전체 식사 세션 조회
    public List<DiningSession> getAllSessions() {
        return diningSessionRepository.findAll();
    }

    // ID로 식사 세션 조회
    public DiningSession getSessionById(Long id) {
        return diningSessionRepository.findById(id)
                .orElseThrow(() ->
                        new RuntimeException(
                                "Dining session not found: " + id));
    }

    // 테이블 ID로 현재 활성 세션 조회
    public DiningSession getActiveSessionByTableId(Long tableId) {

        return diningSessionRepository
                .findByTableIdAndStatus(
                        tableId,
                        DiningSessionStatus.ACTIVE
                )
                .orElseThrow(() ->
                        new RuntimeException(
                                "No active session found for table: " + tableId
                        )
                );
    }

    // 식사 세션 시작
    @Transactional
    public DiningSession startSession(Long tableId) {

        // 테이블 조회
        DiningTable table = diningTableRepository.findById(tableId)
                .orElseThrow(() ->
                        new RuntimeException("Table not found: " + tableId));

        // 테이블 상태 확인
        if (!"AVAILABLE".equals(table.getStatus())) {
            throw new IllegalArgumentException(
                    "Table is not available");
        }

        // 활성 세션 중복 확인
        boolean hasActiveSession =
                diningSessionRepository.findAll().stream()
                        .anyMatch(session ->
                                session.getTableId().equals(tableId)
                                        && session.getStatus()
                                        == DiningSessionStatus.ACTIVE);

        if (hasActiveSession) {
            throw new IllegalArgumentException(
                    "Table already has an active session");
        }

        // 새 세션 생성
        DiningSession session = new DiningSession();
        session.setTableId(tableId);
        session.setStartedAt(LocalDateTime.now());
        session.setStatus(DiningSessionStatus.ACTIVE);

        DiningSession savedSession =
                diningSessionRepository.save(session);

        // 테이블 상태 변경
        table.setStatus("OCCUPIED");
        diningTableRepository.save(table);

        return savedSession;
    }

    // 식사 세션 종료
    @Transactional
    public DiningSession endSession(Long id) {

        DiningSession session = getSessionById(id);

        // 활성 세션 확인
        if (session.getStatus() != DiningSessionStatus.ACTIVE) {
            throw new IllegalArgumentException(
                    "Dining session is not active");
        }

        // 테이블 조회
        DiningTable table =
                diningTableRepository.findById(session.getTableId())
                        .orElseThrow(() ->
                                new RuntimeException("Table not found"));

        // 세션 종료
        session.setEndedAt(LocalDateTime.now());
        session.setStatus(DiningSessionStatus.COMPLETED);

        // 테이블 상태 변경
        table.setStatus("AVAILABLE");

        diningTableRepository.save(table);

        return diningSessionRepository.save(session);
    }
}
