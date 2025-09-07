package com.fitsync.domain.users;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDate;
import java.time.OffsetDateTime;

/**
 * 'users' 테이블과 매핑되는 JPA Entity 클래스입니다.
 * 참고: Java에서는 보통 단일 객체를 나타내는 클래스 이름을 단수(User)로 짓는 것이 일반적인 컨벤션이지만,
 * 요청에 따라 복수형(Users)으로 통일하여 작성되었습니다.
 */
@Getter
// @Setter
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Entity
// @Table(name = "users") // 클래스 이름과 테이블 이름이 같으면 생략 가능합니다.
public class Users {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // --- 기본 정보 ---
    @Column(nullable = false, unique = true, length = 255)
    private String email;

    @Column(name = "\"name\"", nullable = false, length = 50)
    private String name;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private SocialProvider socialProvider;

    // --- 회원 상태 및 유형 ---
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private final UserStatus status = UserStatus.ACTIVE;

    @Enumerated(EnumType.STRING)
    @Column(name = "\"type\"", nullable = false)
    private UserType type = UserType.MEMBER;

    // --- 개인 정보 ---
    private LocalDate birthDate;

    @Enumerated(EnumType.STRING)
    private UserGender gender;

    @Column(length = 500)
    private String intro;

    // --- 트레이너 관련 정보 ---
    @Column(nullable = false)
    private boolean isHidden = false;

    @Column(columnDefinition = "TEXT")
    private String bio;

    @Column(columnDefinition = "TEXT")
    private String purpose;

    @Column(length = 200)
    private String disease;

    @Column(columnDefinition = "TEXT")
    private String activityArea;

    private Long gymId;

    // --- 타임스탬프 ---
    @CreationTimestamp
    @Column(nullable = false, updatable = false)
    private OffsetDateTime createdAt;

    @UpdateTimestamp
    @Column(nullable = false)
    private OffsetDateTime updatedAt;

    // --- 빌더 패턴 ---
    @Builder
    public Users(String email, String name, String imageUrl, SocialProvider socialProvider, UserType type) {
        this.email = email;
        this.name = name;
        this.socialProvider = socialProvider;
        this.type = type;
    }
}
