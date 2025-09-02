package com.fitsync.service;

import com.fitsync.domain.Test;
import com.fitsync.repository.TestRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional
@RequiredArgsConstructor // final로 선언된 필드를 주입받는 생성자를 자동으로 생성합니다. (의존성 주입)
public class TestService {
    private final TestRepository testRepository;

    /**
     * 모든 Test 데이터를 조회하여 반환합니다.
     *
     * @return Test 데이터 리스트
     */
    @Transactional(readOnly = true) // 5. 데이터를 조회만 하는 메서드이므로, readOnly=true로 성능을 최적화합니다.
    public List<Test> findAll() {
        return testRepository.findAll();
    }

    /**
     * 새로운 Test 데이터를 저장합니다.
     *
     * @param test 저장할 Test 객체
     * @return 저장된 Test 객체
     */
    @Transactional // 6. 데이터를 변경하는 작업이므로 트랜잭션을 적용합니다. 작업 실패 시 롤백됩니다.
    public Test save(Test test) {
        return testRepository.save(test);
    }
}
