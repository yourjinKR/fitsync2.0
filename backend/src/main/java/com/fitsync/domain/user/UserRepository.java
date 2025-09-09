package com.fitsync.domain.user;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

// JpaRepository<[관리할 Entity 이름], [Entity의 ID 타입]>
@Repository // Spring에게 이 인터페이스가 Repository임을 알려줍니다 (생략 가능)
public interface UserRepository extends JpaRepository<User, Long> {
    // 기본적인 CRUD 메서드(save, findById, findAll 등)는 자동으로 구현됩니다.

    // 이메일을 통해 이미 가입한 사용자인지 확인하는 메소드
    public Optional<User> findByEmail(String email);
}
