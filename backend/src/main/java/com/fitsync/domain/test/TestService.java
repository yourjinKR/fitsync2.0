package com.fitsync.domain.test;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@Transactional
@RequiredArgsConstructor // final로 선언된 필드를 주입받는 생성자를 자동으로 생성합니다. (의존성 주입)
public class TestService {
    private final TestRepository testRepository;

    /**
     * 새로운 Test 데이터를 저장합니다.
     *
     * @param test 저장할 Test 객체
     * @return 저장된 Test 객체
     */
    public Test save(Test test) {
        return testRepository.save(test);
    }

    /**
     * 모든 Test 데이터를 조회하여 반환합니다.
     *
     * @return Test 데이터 리스트
     */
    @Transactional(readOnly = true) // 데이터를 조회만 하는 메서드이므로, readOnly=true로 성능을 최적화합니다.
    public List<Test> findAll() {
        return testRepository.findAll();
    }

    /**
     * ID로 특정 Test 데이터를 조회합니다.
     *
     * @param id 조회할 데이터의 ID
     * @return 조회된 Test 객체 (Optional)
     */
    @Transactional(readOnly = true)
    public Optional<Test> findById(Long id) {
        return testRepository.findById(id);
    }

    /**
     * 기존 Test 데이터를 수정합니다.
     *
     * @param id          수정할 데이터의 ID
     * @param testDetails 수정할 내용을 담은 Test 객체
     * @return 수정된 Test 객체
     */
    public Test update(Long id, Test testDetails) {
        // ID를 사용하여 기존 데이터를 찾습니다. 데이터가 없으면 예외를 발생시킵니다.
        Test test = testRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Invalid test Id:" + id));

        // 전달받은 정보로 기존 데이터를 업데이트합니다.
        test.setName(testDetails.getName());
        test.setEmail(testDetails.getEmail());
        test.setPassword(testDetails.getPassword()); // 실제 애플리케이션에서는 비밀번호 암호화 처리가 필요합니다.
        test.setBirthDate(testDetails.getBirthDate());
        test.setGender(testDetails.getGender());
        test.setPhoneNumber(testDetails.getPhoneNumber());
        test.setAddress(testDetails.getAddress());
        test.setBio(testDetails.getBio());
        test.setProfileImageUrl(testDetails.getProfileImageUrl());
        test.setAgreedToTerms(testDetails.isAgreedToTerms());
        // updatedAt 필드는 Test 엔티티의 @PreUpdate 어노테이션에 의해 자동으로 갱신됩니다.

        return testRepository.save(test);
    }

    /**
     * ID로 Test 데이터를 삭제합니다.
     *
     * @param id 삭제할 데이터의 ID
     */
    public void delete(Long id) {
        // ID 존재 여부를 확인하고 삭제합니다.
        Test test = testRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Invalid test Id:" + id));
        testRepository.delete(test);
    }
}
