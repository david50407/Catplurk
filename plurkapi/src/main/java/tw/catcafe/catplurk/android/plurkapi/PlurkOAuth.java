package tw.catcafe.catplurk.android.plurkapi;

import org.mariotaku.restfu.annotation.method.POST;
import org.mariotaku.restfu.annotation.param.Body;
import org.mariotaku.restfu.annotation.param.Extra;
import org.mariotaku.restfu.http.BodyType;

import tw.catcafe.catplurk.android.plurkapi.auth.OAuthToken;

/**
 * @author Davy
 */
public interface PlurkOAuth {
    @POST("/OAuth/request_token")
    @Body(BodyType.FORM)
    OAuthToken getRequestToken() throws PlurkException;

    @POST("/OAuth/access_token")
    @Body(BodyType.FORM)
    OAuthToken getAccessToken(
            @Extra({"oauth_token", "oauth_token_secret"}) OAuthToken requestToken,
            @Extra("oauth_verifier") String oauthVerifier) throws PlurkException;
}
