package tw.catcafe.catplurk.android.plurkapi.auth;

import android.util.Pair;

import org.mariotaku.restfu.Utils;
import org.mariotaku.restfu.http.ValueMap;

import java.nio.charset.Charset;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;

/**
 * @author Davy
 */
public class OAuthToken implements ValueMap {
    private String oauthToken, oauthTokenSecret;

    public String getOauthTokenSecret() {
        return oauthTokenSecret;
    }

    public String getOauthToken() {
        return oauthToken;
    }

    public OAuthToken(String oauthToken, String oauthTokenSecret) {
        this.oauthToken = oauthToken;
        this.oauthTokenSecret = oauthTokenSecret;
    }

    public OAuthToken(String body, Charset charset) throws ParseException {
        List<Pair<String, String>> params = new ArrayList<>();
        Utils.parseGetParameters(body, params, charset.name());
        for (Pair<String, String> param : params) {
            switch (param.first) {
                case "oauth_token": {
                    oauthToken = param.second;
                    break;
                }
                case "oauth_token_secret": {
                    oauthTokenSecret = param.second;
                    break;
                }
            }
        }
        if (oauthToken == null || oauthTokenSecret == null) {
            throw new ParseException("Unable to parse request token", -1);
        }
    }

    @Override
    public boolean has(String key) {
        return "oauth_token".equals(key) || "oauth_token_secret".equals(key);
    }

    @Override
    public String toString() {
        return "OAuthToken{" +
                "oauthToken='" + oauthToken + '\'' +
                ", oauthTokenSecret='" + oauthTokenSecret + '\'' +
                '}';
    }

    @Override
    public String get(String key) {
        if ("oauth_token".equals(key)) {
            return oauthToken;
        } else if ("oauth_token_secret".equals(key)) {
            return oauthTokenSecret;
        }
        return null;
    }

    @Override
    public String[] keys() {
        return new String[]{"oauth_token", "oauth_token_secret"};
    }
}
