package com.fitsync.domain.oauth;

import com.fitsync.domain.user.entity.SocialProvider;
import com.fitsync.domain.user.entity.User;
import com.fitsync.domain.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.user.DefaultOAuth2User;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;

import java.util.Collections;

/**
 * 소셜 로그인 성공 후 후속 조치를 진행할 OAuth2 UserService 인터페이스의 구현체입니다.
 * 리소스 서버(GitHub 등)에서 사용자 정보를 가져온 상태에서,
 * 사용자를 우리 시스템에 맞게 DB에 저장하거나 업데이트하는 등 추가 작업을 처리합니다.
 */
@Service
@RequiredArgsConstructor
public class CustomOAuth2UserService extends DefaultOAuth2UserService {

    /**
     * 사용자를 데이터베이스에서 찾아보고, 없으면 저장하기 위해 UserRepository를 주입받습니다.
     */
    private final UserRepository userRepository;

    /**
     * Spring Security가 OAuth2 로그인을 처리하는 과정에서 호출되는 핵심 메서드입니다.
     * 이 메서드에서 사용자 정보를 DB에 저장하거나 업데이트하는 로직을 구현합니다.
     *
     * @param userRequest 리소스 서버(GitHub)에서 발급받은 액세스 토큰을 포함한 요청 정보
     * @return Spring Security가 내부적으로 사용할 인증 정보가 담긴 OAuth2User 객체
     * @throws OAuth2AuthenticationException OAuth2 처리 중 발생하는 예외
     */
    @Override
    public OAuth2User loadUser(OAuth2UserRequest userRequest) throws OAuth2AuthenticationException {
        // 1. DefaultOAuth2UserService의 loadUser 메서드를 호출하여 기본 OAuth2User 객체를 받아옵니다.
        //    이 객체는 리소스 서버(GitHub)로부터 받아온 사용자의 원본 정보를 담고 있습니다.
        OAuth2User oAuth2User = super.loadUser(userRequest);

        System.out.println("소셜로그인 값 확인합니다 : " + oAuth2User.getAttributes());

        // 2. 어떤 소셜 서비스(github, google 등)를 통해 로그인을 했는지 ID를 가져옵니다.
        String registrationId = userRequest.getClientRegistration().getRegistrationId();

        // 3. OAuth2 로그인 시 키가 되는 필드값(PK)을 가져옵니다. (GitHub는 "id", Google은 "sub")
        String userNameAttributeName = userRequest.getClientRegistration()
                .getProviderDetails().getUserInfoEndpoint().getUserNameAttributeName();

        // 4. 우리가 직접 만든 OAuthAttributes 클래스를 사용하여,
        //    소셜 서비스 종류에 따라 원본 사용자 정보를 우리 시스템에 맞는 표준 객체로 변환합니다.
        OAuthAttributes attributes = OAuthAttributes.of(registrationId, userNameAttributeName, oAuth2User.getAttributes());

        // 5. DB에 사용자를 저장하거나, 이미 있는 사용자인 경우 업데이트합니다.
        User user = saveOrUpdate(attributes, registrationId);

        // 6. 최종적으로 Spring Security가 관리할 사용자 정보를 담은 DefaultOAuth2User 객체를 생성하여 반환합니다.
        //    이 객체는 세션에 저장되어 사용자의 인증 상태를 유지하는 데 사용됩니다.
        // TODO : 현재 JWT를 통해 잘 전달하고 있으니 일단은 돌아감, 아래 코드는 나중에 수정
        return new DefaultOAuth2User(
                // 첫 번째 인자: 사용자의 권한 정보. User 엔티티에 Role 필드가 없으므로, 모든 사용자에게 "ROLE_USER" 권한을 부여합니다.
                Collections.singleton(new SimpleGrantedAuthority("ROLE_USER")),
                // 두 번째 인자: 사용자의 원본 속성 정보(Map). OAuthAttributes에서 가져옵니다.
                attributes.getAttributes(),
                // 세 번째 인자: 사용자의 고유 ID로 사용할 속성의 '키' 이름. OAuthAttributes에서 가져옵니다.
                attributes.getNameAttributeKey());
    }

    /**
     * OAuthAttributes 객체를 받아 DB에 사용자를 저장하거나 업데이트하는 private 메서드입니다.
     * 1. attributes 객체에서 이메일을 꺼내, 해당 이메일로 DB에서 사용자를 찾아봅니다.
     * 2. .orElse()를 사용하여 회원이 없다면 attributes.toEntity()를 실행하여 새로운 User 객체 생성
     * @param attributes 표준화된 사용자 정보 DTO
     * @return 저장되거나 조회된 User 엔티티
     */
    private User saveOrUpdate(OAuthAttributes attributes, String registrationId) {
        
        User user = userRepository.findByEmail(attributes.getEmail())
                .orElse(attributes.toEntity(SocialProvider.valueOf(registrationId.toUpperCase())));

        return userRepository.save(user);
    }
}