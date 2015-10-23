package tw.catcafe.catplurk.android.plurkapi;

import org.mariotaku.restfu.http.Endpoint;

import tw.catcafe.catplurk.android.plurkapi.auth.OAuthEndpoint;

/**
 * @author Davy
 */
public class PlurkEndpoint extends OAuthEndpoint implements ApiConstant {
    public PlurkEndpoint() {
        super(PLURK_API_BASE);
    }
}
