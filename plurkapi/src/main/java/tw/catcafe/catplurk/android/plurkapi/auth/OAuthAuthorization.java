package tw.catcafe.catplurk.android.plurkapi.auth;

import android.support.annotation.Nullable;
import android.util.Base64;
import android.util.Pair;

import org.mariotaku.restfu.RestRequestInfo;
import org.mariotaku.restfu.Utils;
import org.mariotaku.restfu.http.Authorization;
import org.mariotaku.restfu.http.Endpoint;

import java.io.UnsupportedEncodingException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Map;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;

/**
 * This is a wrapper for
 *
 * @author Davy
 */
public class OAuthAuthorization implements Authorization, OAuthSupport {
    private static final String DEFAULT_ENCODING = "UTF-8";
    private static final String OAUTH_SIGNATURE_METHOD = "HMAC-SHA1";
    private static final String OAUTH_VERSION = "1.0";
    private final String consumerKey, consumerSecret;
    private final OAuthToken oauthToken;
    SecureRandom secureRandom = new SecureRandom();

    public OAuthAuthorization(String consumerKey, String consumerSecret) {
        this(consumerKey, consumerSecret, null);
    }

    public OAuthAuthorization(String consumerKey, String consumerSecret, OAuthToken oauthToken) {
        this.consumerKey = consumerKey;
        this.consumerSecret = consumerSecret;
        this.oauthToken = oauthToken;
    }

    @Override
    public String getConsumerKey() {
        return consumerKey;
    }

    @Override
    public String getConsumerSecret() {
        return consumerSecret;
    }

    public OAuthToken getOauthToken() {
        return oauthToken;
    }

    private String generateOAuthSignature(String method, String url,
                                          String oauthNonce, long timestamp,
                                          String oauthToken, String oauthTokenSecret,
                                          @Nullable String oauthVerifier,
                                          List<Pair<String, String>> queries,
                                          List<Pair<String, String>> forms) {
        final List<String> encodeParams = new ArrayList<>();
        encodeParams.add(encodeParameter("oauth_consumer_key", consumerKey));
        encodeParams.add(encodeParameter("oauth_nonce", oauthNonce));
        encodeParams.add(encodeParameter("oauth_signature_method", OAUTH_SIGNATURE_METHOD));
        encodeParams.add(encodeParameter("oauth_timestamp", String.valueOf(timestamp)));
        encodeParams.add(encodeParameter("oauth_version", OAUTH_VERSION));
        if (oauthToken != null) {
            encodeParams.add(encodeParameter("oauth_token", oauthToken));
        }
        if (oauthVerifier != null) {
            encodeParams.add(encodeParameter("oauth_verifier", oauthVerifier));
        }
        if (queries != null) {
            for (Pair<String, String> query : queries) {
                encodeParams.add(encodeParameter(query.first, query.second));
            }
        }
        if (forms != null) {
            for (Pair<String, String> form : forms) {
                encodeParams.add(encodeParameter(form.first, form.second));
            }
        }
        Collections.sort(encodeParams);
        final StringBuilder paramBuilder = new StringBuilder();
        for (int i = 0, j = encodeParams.size(); i < j; i++) {
            if (i != 0) {
                paramBuilder.append('&');
            }
            paramBuilder.append(encodeParams.get(i));
        }
        final String signingKey;
        if (oauthTokenSecret != null) {
            signingKey = encode(consumerSecret) + '&' + encode(oauthTokenSecret);
        } else {
            signingKey = encode(consumerSecret) + '&';
        }
        try {
            final Mac mac = Mac.getInstance("HmacSHA1");
            SecretKeySpec secret = new SecretKeySpec(signingKey.getBytes(), mac.getAlgorithm());
            mac.init(secret);
            String urlNoQuery = url.indexOf('?') != -1 ? url.substring(0, url.indexOf('?')) : url;
            final String baseString = encode(method) + '&' + encode(urlNoQuery) + '&' + encode(paramBuilder.toString());
            final byte[] signature = mac.doFinal(baseString.getBytes(DEFAULT_ENCODING));
            return Base64.encodeToString(signature, Base64.NO_WRAP);
        } catch (NoSuchAlgorithmException e) {
            throw new UnsupportedOperationException(e);
        } catch (InvalidKeyException | UnsupportedEncodingException e) {
            throw new AssertionError(e);
        }
    }

    @Override
    public String getHeader(Endpoint endpoint, RestRequestInfo request) {
        if (!(endpoint instanceof OAuthEndpoint))
            throw new IllegalArgumentException("OAuthEndpoint required");
        final OAuthEndpoint oauthEndpoint = (OAuthEndpoint) endpoint;
        final String method = request.getMethod();
        final String url = Endpoint.constructUrl(oauthEndpoint.getUrl(), request);
        final String oauthNonce = generateOAuthNonce();
        final long timestamp = System.currentTimeMillis() / 1000;
        final Map<String, Object> extras = request.getExtras();
        final String oauthToken, oauthTokenSecret, oauthVerifier;
        if (this.oauthToken != null) {
            oauthToken = this.oauthToken.getOauthToken();
            oauthTokenSecret = this.oauthToken.getOauthTokenSecret();
        } else {
            oauthToken = (String) extras.get("oauth_token");
            oauthTokenSecret = (String) extras.get("oauth_token_secret");
        }
        if (extras.containsKey("oauth_verifier")) {
            oauthVerifier = (String) extras.get("oauth_verifier");
        }
        else {
            oauthVerifier = null;
        }
        final String oauthSignature = generateOAuthSignature(method, url, oauthNonce, timestamp, oauthToken,
                oauthTokenSecret, oauthVerifier, request.getQueries(), request.getForms());
        final List<Pair<String, String>> encodeParams = new ArrayList<>();
        encodeParams.add(Pair.create("oauth_consumer_key", consumerKey));
        encodeParams.add(Pair.create("oauth_nonce", oauthNonce));
        encodeParams.add(Pair.create("oauth_signature", encode(oauthSignature)));
        encodeParams.add(Pair.create("oauth_signature_method", OAUTH_SIGNATURE_METHOD));
        encodeParams.add(Pair.create("oauth_timestamp", String.valueOf(timestamp)));
        encodeParams.add(Pair.create("oauth_version", OAUTH_VERSION));
        if (oauthToken != null) {
            encodeParams.add(Pair.create("oauth_token", oauthToken));
        }
        if (oauthVerifier != null) {
            encodeParams.add(Pair.create("oauth_verifier", oauthVerifier));
        }
        Collections.sort(encodeParams, new Comparator<Pair<String, String>>() {
            @Override
            public int compare(Pair<String, String> lhs, Pair<String, String> rhs) {
                return lhs.first.compareTo(rhs.first);
            }
        });
        final StringBuilder headerBuilder = new StringBuilder();
        headerBuilder.append("OAuth ");
        for (int i = 0, j = encodeParams.size(); i < j; i++) {
            if (i != 0) {
                headerBuilder.append(", ");
            }
            final Pair<String, String> keyValuePair = encodeParams.get(i);
            headerBuilder.append(keyValuePair.first);
            headerBuilder.append("=\"");
            headerBuilder.append(keyValuePair.second);
            headerBuilder.append('\"');
        }
        return headerBuilder.toString();
    }

    @Override
    public boolean hasAuthorization() {
        return true;
    }

    private String encodeParameter(String key, String value) {
        return encode(key) + '=' + encode(value);
    }

    private static String encode(final String value) {
        return Utils.encode(value, DEFAULT_ENCODING);
    }

    private String generateOAuthNonce() {
        final byte[] input = new byte[32];
        secureRandom.nextBytes(input);
        return Base64.encodeToString(input, Base64.NO_WRAP).replaceAll("[^\\w\\d]", "");
    }
}
