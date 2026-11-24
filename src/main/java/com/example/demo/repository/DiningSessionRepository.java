
package com.example.demo.repository;

import com.example.demo.entity.DiningSession;
import com.example.demo.entity.DiningSessionStatus;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface DiningSessionRepository
        extends JpaRepository<DiningSession, Long> {

    // 테이블 ID와 세션 상태로 활성 세션 조회
    Optional<DiningSession> findByTableIdAndStatus(
            Long tableId,
            DiningSessionStatus status
    );
}
