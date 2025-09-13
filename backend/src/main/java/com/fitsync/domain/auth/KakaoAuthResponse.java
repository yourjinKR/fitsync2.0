package com.fitsync.domain.auth;

import com.fitsync.domain.oauth.OAuth2Response;
import com.fitsync.domain.user.SocialProvider;
import org.antlr.v4.runtime.misc.NotNull;

import java.util.Map;

public class KakaoAuthResponse implements OAuth2Response {

    private final Map<String, Object> attribute;
    private final String id;

    public KakaoAuthResponse(Map<String, Object> attribute) {
        id = attribute.get("id").toString();
        this.attribute = (Map<String, Object>) attribute.get("kakao_account");
    }

    @Override
    public SocialProvider getProvider() {

        return SocialProvider.KAKAO;
    }

    @Override
    public String getProviderId() {

        return id;
    }

    @Override
    public String getEmail() {

        return attribute.get("email").toString();
    }

    @Override
    public String getName() {

        return attribute.get("name").toString();
    }
}
