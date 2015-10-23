package tw.catcafe.catplurk.android.plurkapi.auth;

import org.mariotaku.restfu.http.Endpoint;

import tw.catcafe.catplurk.android.plurkapi.ApiConstant;

/**
 * @author Davy
 */
public class OAuthEndpoint extends Endpoint implements ApiConstant {
    public OAuthEndpoint() {
        super(PLURK_OAUTH_BASE);
    }
    public OAuthEndpoint(final String urlBase) {
        super(urlBase);
    }
}
