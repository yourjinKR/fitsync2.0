package com.fitsync.domain.auth;

import com.fitsync.domain.oauth.OAuth2Response;
import com.fitsync.domain.user.entity.SocialProvider;

import java.util.Map;

public class GoogleAuthResponse implements OAuth2Response {

    private final Map<String, Object> attribute;

    public GoogleAuthResponse(Map<String, Object> attribute) {
        this.attribute = attribute;
    }

    @Override
    public SocialProvider getProvider() {

        return SocialProvider.GOOGLE;
    }

    @Override
    public String getProviderId() {

        return attribute.get("sub").toString();
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
