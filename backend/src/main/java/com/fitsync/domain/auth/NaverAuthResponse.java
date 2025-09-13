package com.fitsync.domain.auth;

import com.fitsync.domain.oauth.OAuth2Response;
import com.fitsync.domain.user.SocialProvider;

import java.util.Map;

public class NaverAuthResponse implements OAuth2Response {

    private final Map<String, Object> attribute;

    public NaverAuthResponse(Map<String, Object> attribute) {

        this.attribute = (Map<String, Object>) attribute.get("response");
    }

    @Override
    public SocialProvider getProvider() {

        return SocialProvider.GOOGLE;
    }

    @Override
    public String getProviderId() {

        return attribute.get("id").toString();
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
