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
        // TODO: 나중에 Google, Naver 등 다른 소셜 로그인을 추가할 경우,
        if ("google".equals(registrationId)) {
            return ofGoogle(userNameAttributeName, attributes);
        }

        // 현재는 GitHub 로그인만 구현했으므로, ofGithub 메서드를 바로 호출합니다.
        return ofGithub(userNameAttributeName, attributes);
    }

    /**
     * GitHub 로그인 요청을 처리하여 OAuthAttributes 객체를 생성하는 private 메서드입니다.
     * GitHub가 보내주는 사용자 정보(attributes)의 구조를 정확히 알고,
     * 거기서 이름(name)과 이메일(email)을 꺼내어 객체를 조립합니다.
     *
     * @param userNameAttributeName 사용자의 고유 ID가 담긴 키 이름
     * @param attributes GitHub에서 받아온 원본 사용자 정보
     * @return GitHub 맞춤형으로 조립된 OAuthAttributes 객체
     */
    private static OAuthAttributes ofGithub(String userNameAttributeName, Map<String, Object> attributes) {
        // @Builder를 사용해 OAuthAttributes 객체를 생성합니다.
        return OAuthAttributes.builder()
                // attributes 맵에서 "name" 키의 값을 찾아 String으로 변환 후 'name' 필드에 담습니다.
                .name((String) attributes.get("name"))
                // attributes 맵에서 "email" 키의 값을 찾아 String으로 변환 후 'email' 필드에 담습니다.
                .email((String) attributes.get("email")) // GitHub에서 이메일 비공개 시 null일 수 있습니다.
                // 원본 데이터는 나중에 필요할 수 있으니 그대로 'attributes' 필드에 담아둡니다.
                .attributes(attributes)
                // 사용자의 고유 ID 키 이름도 'nameAttributeKey' 필드에 담아둡니다.
                .nameAttributeKey(userNameAttributeName)
                // 최종적으로 객체를 생성합니다.
                .build();
    }

    /**
     * Google 로그인 요청을 처리하여 OAuthAttributes 객체를 생성하는 private 메서드입니다.
     * Google이 보내주는 사용자 정보(attributes)의 구조에 맞게 이름, 이메일을 추출합니다.
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