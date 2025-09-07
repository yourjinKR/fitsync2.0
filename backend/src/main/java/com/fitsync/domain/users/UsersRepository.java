package com.fitsync.domain.users;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

// JpaRepository<[관리할 Entity 이름], [Entity의 ID 타입]>
@Repository // Spring에게 이 인터페이스가 Repository임을 알려줍니다 (생략 가능)
public interface UsersRepository extends JpaRepository<Users, Long> {
    // 기본적인 CRUD 메서드(save, findById, findAll 등)는 자동으로 구현됩니다.
}
