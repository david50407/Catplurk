package tw.catcafe.catplurk.android.plurkapi.auth.Authenticator;

import android.app.Application;
import android.os.Build;
import android.provider.Settings;
import android.text.TextUtils;
import android.util.Pair;
import android.util.Xml;

import com.nostra13.universalimageloader.utils.IoUtils;

import org.apache.commons.lang3.ArrayUtils;
import org.mariotaku.restfu.RestAPIFactory;
import org.mariotaku.restfu.RestClient;
import org.mariotaku.restfu.annotation.method.GET;
import org.mariotaku.restfu.annotation.method.POST;
import org.mariotaku.restfu.http.Endpoint;
import org.mariotaku.restfu.http.RestHttpClient;
import org.mariotaku.restfu.http.RestHttpRequest;
import org.mariotaku.restfu.http.RestHttpResponse;
import org.mariotaku.restfu.http.mime.BaseTypedData;
import org.mariotaku.restfu.http.mime.FormTypedBody;
import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;
import org.xmlpull.v1.XmlPullParserFactory;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.Reader;
import java.net.CookieManager;
import java.net.CookiePolicy;
import java.net.HttpCookie;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;

import tw.catcafe.catplurk.android.plurkapi.PlurkException;
import tw.catcafe.catplurk.android.plurkapi.PlurkOAuth;
import tw.catcafe.catplurk.android.plurkapi.auth.OAuthToken;

/**
 * @author Davy
 */
public class OAuthPasswordAuthenticator {

    private static final String INPUT_LOGIN_TOKEN = "login_token";

    private final PlurkOAuth oauth;
    private final RestHttpClient client;
    private final Endpoint endpoint;

    public OAuthPasswordAuthenticator(final PlurkOAuth oauth) {
        final RestClient restClient = RestAPIFactory.getRestClient(oauth);
        this.oauth = oauth;
        this.client = restClient.getRestClient();
        this.endpoint = restClient.getEndpoint();
    }

    public OAuthToken getOAuthAccessToken(final String username, final String password,
                                          final String modelName, final String deviceId) throws AuthenticationException {
        final OAuthToken requestToken;
        try {
            requestToken = oauth.getRequestToken();
        } catch (PlurkException e) {
            if (e.isCausedByNetworkIssue()) throw new AuthenticationException(e);
            throw new AuthenticityTokenException(e);
        }
        RestHttpResponse loginFormPage = null, loginResultPage = null;
        try {
            final String oauthToken = requestToken.getOauthToken();
            final HashMap<String, String> inputMap = new HashMap<>();
            final List<Pair<String, String>> params = new ArrayList<>();
            final ArrayList<Pair<String, String>> requestHeaders = new ArrayList<>();
            // Step 1: Get login page info.
            final RestHttpRequest authorizePageRequest = new RestHttpRequest.Builder()
                    .method(GET.METHOD)
                    .url(endpoint.construct("/OAuth/authorize",
                            Pair.create("oauth_token", oauthToken)))
                    .build();
            loginFormPage = client.execute(authorizePageRequest);
            final String[] cookieHeaders = loginFormPage.getHeaders("Set-Cookie");
            readInputFromHtml(BaseTypedData.reader(loginFormPage.getBody()), inputMap,
                    INPUT_LOGIN_TOKEN);

            // Step 2: Login for user
            // params.add(Pair.create("oauth_token", oauthToken));
            params.add(Pair.create(INPUT_LOGIN_TOKEN, inputMap.get(INPUT_LOGIN_TOKEN).replace("@", "%40")));
            params.add(Pair.create("logintoken", "1"));
            params.add(Pair.create("nick_name", username));
            params.add(Pair.create("password", password));

            requestHeaders.add(Pair.create("Origin", "https://www.plurk.com"));
            requestHeaders.add(Pair.create("Referer",
                    Endpoint.constructUrl("https://www.plurk.com/OAuth/authorize",
                            Pair.create("oauth_token", requestToken.getOauthToken())
                    )
            ));
            readAndPassCookie(endpoint, cookieHeaders, requestHeaders);

            loginResultPage = client.execute(new RestHttpRequest.Builder()
                            .method(POST.METHOD)
                            .url(endpoint.construct("/Users/login"))
                            .headers(requestHeaders)
                            .body(new FormTypedBody(params))
                            .build()
            );
            inputMap.clear();
            readInputFromHtml(BaseTypedData.reader(loginResultPage.getBody()), inputMap,
                    "oauth_token");
            if (!inputMap.containsKey("oauth_token") || !inputMap.get("oauth_token").equals(oauthToken))
                throw new WrongUserPassException();
            final String[] loginedCookieHeaders = loginFormPage.getHeaders("Set-Cookie");

            // Step 3: Authorize
            params.clear();
            params.add(Pair.create("oauth_token", oauthToken));
            params.add(Pair.create("accept", "1"));
            params.add(Pair.create("deviceid", deviceId));
            params.add(Pair.create("model", modelName));
            requestHeaders.clear();
            requestHeaders.add(Pair.create("Origin", "https://www.plurk.com"));
            requestHeaders.add(Pair.create("Referer",
                    Endpoint.constructUrl("https://www.plurk.com/OAuth/authorize",
                            Pair.create("oauth_token", requestToken.getOauthToken())
                    )
            ));
            readAndPassCookie(endpoint, loginedCookieHeaders, requestHeaders);

            loginResultPage = client.execute(new RestHttpRequest.Builder()
                            .method(POST.METHOD)
                            .url(endpoint.construct("/OAuth/authorizeDone"))
                            .headers(requestHeaders)
                            .body(new FormTypedBody(params))
                            .build()
            );
            final String oauthPin = readOAuthPINFromHtml(BaseTypedData.reader(loginResultPage.getBody()));

            return oauth.getAccessToken(requestToken, oauthPin);
        } catch (final IOException | NullPointerException | XmlPullParserException | PlurkException e) {
            throw new AuthenticationException(e);
        } finally {
            if (loginFormPage != null) {
                IoUtils.closeSilently(loginFormPage);
            }
            if (loginResultPage != null) {
                IoUtils.closeSilently(loginResultPage);
            }
        }
    }

    private static void readInputFromHtml(final Reader in, Map<String, String> map, String... desiredNames) throws IOException, XmlPullParserException {
        final XmlPullParserFactory f = XmlPullParserFactory.newInstance();
        final XmlPullParser parser = f.newPullParser();
        parser.setFeature(Xml.FEATURE_RELAXED, true);
        parser.setInput(in);
        while (parser.next() != XmlPullParser.END_DOCUMENT) {
            final String tag = parser.getName();
            switch (parser.getEventType()) {
                case XmlPullParser.START_TAG: {
                    final String name = parser.getAttributeValue(null, "name");
                    if ("input".equalsIgnoreCase(tag) && ArrayUtils.contains(desiredNames, name)) {
                        map.put(name, parser.getAttributeValue(null, "value"));
                    }
                    break;
                }
            }
        }
    }

    public static String readOAuthPINFromHtml(final Reader in) throws XmlPullParserException, IOException {
        boolean start_code = false;
        final XmlPullParserFactory f = XmlPullParserFactory.newInstance();
        final XmlPullParser parser = f.newPullParser();
        parser.setFeature(Xml.FEATURE_RELAXED, true);
        parser.setInput(in);
        while (parser.next() != XmlPullParser.END_DOCUMENT) {
            final String tag = parser.getName();
            final int type = parser.getEventType();
            if (type == XmlPullParser.START_TAG) {
                if ("span".equalsIgnoreCase(tag)) {
                    start_code = "oauth_verifier".equals(parser.getAttributeValue(null, "id"));
                }
            } else if (type == XmlPullParser.END_TAG) {
                if ("span".equalsIgnoreCase(tag)) {
                    start_code = false;
                }
            } else if (type == XmlPullParser.TEXT) {
                final String text = parser.getText();
                if (start_code && !TextUtils.isEmpty(text) && TextUtils.isDigitsOnly(text))
                    return text;
            }
        }
        return null;
    }

    private static void readAndPassCookie(final Endpoint endpoint, final String[] cookieHeaders,
                                          List<Pair<String, String>> requestHeaders) {
        for (String cookieHeader : cookieHeaders) {
            for (HttpCookie cookie : HttpCookie.parse(cookieHeader)) {
                if (HttpCookie.domainMatches(cookie.getDomain(), "www.plurk.com")) {
                    cookie.setVersion(1);
                    cookie.setPath(null);
                    cookie.setDomain(null);
                }
                requestHeaders.add(Pair.create("Cookie", cookie.toString()));
            }
        }
    }

    public static class AuthenticationException extends Exception {

        private static final long serialVersionUID = -5629194721838256378L;

        AuthenticationException() {
        }

        AuthenticationException(final Exception cause) {
            super(cause);
        }

        AuthenticationException(final String message) {
            super(message);
        }
    }

    public static final class AuthenticityTokenException extends AuthenticationException {

        private static final long serialVersionUID = -1840298989316218380L;

        public AuthenticityTokenException(Exception e) {
            super(e);
        }
    }

    public static final class WrongUserPassException extends AuthenticationException {

        private static final long serialVersionUID = -4880737459768513029L;

    }
}
