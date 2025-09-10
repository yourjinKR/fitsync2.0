package com.fitsync.config.oauth;

import com.fitsync.domain.user.SocialProvider;
import com.fitsync.domain.user.User;
import com.fitsync.domain.user.UserType;
import lombok.Builder;
import lombok.Getter;

import java.util.Map;

/**
 * OAuth2 로그인 성공 후, 소셜 서비스(GitHub, Google 등)에서 가져온 사용자 정보를
 * 우리 서비스의 User 엔티티에 맞게 변환해주는 역할을 하는 클래스입니다.
 * 마치 해외 직구한 전자제품에 맞는 '돼지코' 어댑터처럼,
 * 각기 다른 소셜 서비스의 데이터를 우리 시스템에 맞게 표준화해줍니다.
 */
@Getter
public class OAuthAttributes {

    /**
     * 소셜 서비스에서 받아온 사용자의 원본 데이터입니다.
     * key-value 형태의 맵(Map)으로 되어 있습니다. (예: {id=12345, name=홍길동, email=...})
     */
    private final Map<String, Object> attributes;

    /**
     * 사용자의 고유 식별자(PK)가 되는 값의 '키(key)' 이름입니다.
     * 예를 들어, Google은 'sub', Naver는 'id', Kakao는 'id' 라는 키에 고유 ID를 담아줍니다.
     */
    private final String nameAttributeKey;

    /**
     * 사용자 이름입니다. 원본 데이터에서 'name' 키를 통해 추출합니다.
     */
    private final String name;

    /**
     * 사용자 이메일입니다. 원본 데이터에서 'email' 키를 통해 추출합니다.
     */
    private final String email;

    /**
     * @Builder 어노테이션으로 생성된 빌더를 통해 이 클래스의 객체를 생성하는 생성자입니다.
     * 각 필드의 값을 초기화합니다.
     */
    @Builder
    public OAuthAttributes(Map<String, Object> attributes, String nameAttributeKey, String name, String email) {
        this.attributes = attributes;
        this.nameAttributeKey = nameAttributeKey;
        this.name = name;
        this.email = email;
    }

    /**
     * 모든 소셜 로그인 요청의 '관문' 역할을 하는 정적 팩토리 메서드입니다.
     * 로그인한 서비스 종류(registrationId)에 따라 적절한 'of...' 메서드를 호출하여
     * 표준화된 OAuthAttributes 객체를 반환합니다.
     *
     * @param registrationId        로그인한 소셜 서비스의 ID (예: "github", "google", "naver")
     * @param userNameAttributeName 사용자의 고유 ID가 담긴 키 이름
     * @param attributes            소셜 서비스에서 받아온 원본 사용자 정보
     * @return 표준화된 OAuthAttributes 객체
     */
    public static OAuthAttributes of(String registrationId, String userNameAttributeName, Map<String, Object> attributes) {
        if ("kakao".equalsIgnoreCase(registrationId)) {
            return ofKakao(userNameAttributeName, attributes);
        }
        if ("google".equalsIgnoreCase(registrationId)) {
            return ofGoogle(userNameAttributeName, attributes);
        }
        // 지원하지 않는 소셜 로그인일 경우 예외를 발생
        throw new IllegalArgumentException("Unsupported registrationId: " + registrationId);
    }

    /**
     * Google 로그인 요청을 처리하여 OAuthAttributes 객체를 생성하는 private 메서드입니다.
     * Google이 보내주는 사용자 정보(attributes)의 구조에 맞게 이름, 이메일을 추출합니다.
     *
     * @param userNameAttributeName 사용자의 고유 ID가 담긴 키 이름
     * @param attributes GitHub에서 받아온 원본 사용자 정보
     */
    private static OAuthAttributes ofGoogle(String userNameAttributeName, Map<String, Object> attributes) {
        return OAuthAttributes.builder()
                .name((String) attributes.get("name"))
                .email((String) attributes.get("email"))
                .attributes(attributes)
                .nameAttributeKey(userNameAttributeName) // Google의 PK는 "sub"라는 키에 담겨 옵니다.
                .build();
    }

    /**
     * Kakao 로그인
     */
    @SuppressWarnings("unchecked")
    private static OAuthAttributes ofKakao(String userNameAttributeName, Map<String, Object> attributes) {
        System.out.println("Kakao OAuth 데이터: " + attributes);  // 디버깅용 로그
        
        // 카카오 응답에서 'kakao_account' 정보를 추출
        Map<String, Object> kakaoAccount = (Map<String, Object>) attributes.get("kakao_account");
        
        if (kakaoAccount == null) {
            throw new IllegalArgumentException("카카오 계정 정보를 찾을 수 없습니다.");
        }
        
        String name = (String) kakaoAccount.get("name");
        String email = (String) kakaoAccount.get("email");
        
        if (name == null || email == null) {
            throw new IllegalArgumentException("필수 정보(이름 또는 이메일)가 누락되었습니다.");
        }
        
        return OAuthAttributes.builder()
                .name(name)
                .email(email)
                .attributes(attributes)
                .nameAttributeKey(userNameAttributeName)  // Spring Security가 제공하는 값 사용
                .build();
    }

    /**
     * 이 OAuthAttributes 객체의 정보를 바탕으로 우리 서비스의 User 엔티티를 생성합니다.
     * 이 메서드는 처음 가입하는 사용자를 데이터베이스에 저장하기 위해 호출됩니다.
     *
     * @return 생성된 User 엔티티
     */
    public User toEntity(SocialProvider socialProvider) {
        // User 엔티티의 빌더를 사용해 객체를 생성합니다.
        return User.builder()
                // 이 클래스(OAuthAttributes)의 name 필드 값을 User 엔티티의 name 필드에 넣습니다.
                .name(name)
                // 이 클래스의 email 필드 값을 User 엔티티의 email 필드에 넣습니다.
                .email(email)
                // 처음 가입하는 사용자는 GitHub를 통해 가입했으므로 SocialProvider를 GITHUB으로 지정합니다.
                .socialProvider(socialProvider)
                // 가입 시 기본 사용자 유형을 MEMBER로 지정합니다.
                .type(UserType.MEMBER)
                // 최종적으로 User 엔티티 객체를 생성합니다.
                .build();
    }
}