package com.fitsync.controller;


import com.fitsync.domain.Test;
import com.fitsync.service.TestService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;
import java.util.List;

/**
 * Test 데이터에 대한 RESTful API 엔드포인트를 제공하는 컨트롤러입니다.
 * CRUD (Create, Read, Update, Delete) 연산을 처리합니다.
 */
@RestController // 이 클래스가 REST 컨트롤러임을 나타냅니다.
@RequestMapping("/api/test") // 이 컨트롤러의 모든 API는 "/api/test" 경로로 시작합니다.
@RequiredArgsConstructor // final 필드에 대한 생성자를 자동으로 생성합니다 (의존성 주입).
@CrossOrigin(origins = "http://localhost:3000") // React 개발 서버(localhost:3000)에서의 요청을 허용합니다.
public class TestController {

    private final TestService testService;

    /**
     * 모든 Test 데이터를 조회합니다. (Read All)
     * HTTP GET /api/test
     *
     * @return 전체 Test 데이터 리스트와 HTTP 200 OK 상태 코드.
     */
    @GetMapping
    public ResponseEntity<List<Test>> getAllTests() {
        List<Test> tests = testService.findAll();
        return ResponseEntity.ok(tests);
    }

    /**
     * ID를 사용하여 특정 Test 데이터를 조회합니다. (Read One)
     * HTTP GET /api/test/{id}
     *
     * @param id 조회할 Test 데이터의 ID.
     * @return 조회된 Test 데이터. 데이터가 없으면 HTTP 404 Not Found를 반환합니다.
     */
    @GetMapping("/{id}")
    public ResponseEntity<Test> getTestById(@PathVariable Long id) {
        return testService.findById(id)
                .map(ResponseEntity::ok) // 데이터가 존재하면 test 객체를 담아 200 OK 응답
                .orElse(ResponseEntity.notFound().build()); // 데이터가 없으면 404 Not Found 응답
    }

    /**
     * 새로운 Test 데이터를 생성합니다. (Create)
     * HTTP POST /api/test
     *
     * @param test 클라이언트로부터 받은 JSON 형식의 Test 데이터.
     * @return 생성된 Test 객체와 HTTP 201 Created 상태 코드. 응답 헤더의 'Location'에 생성된 리소스의 URI가 포함됩니다.
     */
    @PostMapping
    public ResponseEntity<Test> createTest(@RequestBody Test test) {
        Test savedTest = testService.save(test);
        // 생성된 리소스의 URI를 생성합니다. (예: /api/test/1)
        URI location = ServletUriComponentsBuilder.fromCurrentRequest()
                .path("/{id}")
                .buildAndExpand(savedTest.getId())
                .toUri();
        // 201 Created 응답과 함께 생성된 리소스의 URI와 데이터를 반환합니다.
        return ResponseEntity.created(location).body(savedTest);
    }

    /**
     * 기존 Test 데이터를 수정합니다. (Update)
     * HTTP PUT /api/test/{id}
     *
     * @param id          수정할 Test 데이터의 ID.
     * @param testDetails 수정할 내용을 담은 Test 객체.
     * @return 수정된 Test 데이터. 대상 데이터가 없으면 HTTP 404 Not Found를 반환합니다.
     */
    @PutMapping("/{id}")
    public ResponseEntity<Test> updateTest(@PathVariable Long id, @RequestBody Test testDetails) {
        try {
            Test updatedTest = testService.update(id, testDetails);
            return ResponseEntity.ok(updatedTest); // 성공적으로 수정되면 200 OK 응답
        } catch (IllegalArgumentException e) {
            // 서비스 레이어에서 ID가 유효하지 않아 예외가 발생하면 404 응답
            return ResponseEntity.notFound().build();
        }
    }

    /**
     * ID를 사용하여 Test 데이터를 삭제합니다. (Delete)
     * HTTP DELETE /api/test/{id}
     *
     * @param id 삭제할 Test 데이터의 ID.
     * @return 작업 성공 시 본문 없이 HTTP 204 No Content 상태 코드를 반환합니다.
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteTest(@PathVariable Long id) {
        try {
            testService.delete(id);
            return ResponseEntity.noContent().build(); // 성공적으로 삭제되면 204 No Content 응답
        } catch (IllegalArgumentException e) {
            // 삭제할 데이터가 존재하지 않으면 404 응답
            return ResponseEntity.notFound().build();
        }
    }
}
