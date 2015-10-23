package tw.catcafe.catplurk.android.util;

import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;
import android.util.Pair;

import com.squareup.okhttp.OkHttpClient;

import org.mariotaku.restfu.ExceptionFactory;
import org.mariotaku.restfu.HttpRequestFactory;
import org.mariotaku.restfu.RequestInfoFactory;
import org.mariotaku.restfu.RestAPIFactory;
import org.mariotaku.restfu.RestMethodInfo;
import org.mariotaku.restfu.RestRequestInfo;
import org.mariotaku.restfu.annotation.RestMethod;
import org.mariotaku.restfu.annotation.param.MethodExtra;
import org.mariotaku.restfu.http.Authorization;
import org.mariotaku.restfu.http.Endpoint;
import org.mariotaku.restfu.http.FileValue;
import org.mariotaku.restfu.http.RestHttpClient;
import org.mariotaku.restfu.http.RestHttpRequest;
import org.mariotaku.restfu.http.RestHttpResponse;
import org.mariotaku.restfu.http.mime.StringTypedData;
import org.mariotaku.restfu.http.mime.TypedData;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import tw.catcafe.catplurk.android.BuildConfig;
import tw.catcafe.catplurk.android.Constants;
import tw.catcafe.catplurk.android.api.ApiConverter;
import tw.catcafe.catplurk.android.model.Account;
import tw.catcafe.catplurk.android.plurkapi.ApiConstant;
import tw.catcafe.catplurk.android.plurkapi.PlurkApi;
import tw.catcafe.catplurk.android.plurkapi.PlurkEndpoint;
import tw.catcafe.catplurk.android.plurkapi.PlurkException;
import tw.catcafe.catplurk.android.plurkapi.auth.OAuthAuthorization;
import tw.catcafe.catplurk.android.plurkapi.auth.OAuthEndpoint;
import tw.catcafe.catplurk.android.plurkapi.auth.OAuthToken;

/**
 * @author Davy
 */
public class PlurkAPIFactory implements Constants, ApiConstant {
    @Nullable
    public static PlurkApi getPlurkApiInstance(final Context context, final long accountId) {
        return getPlurkApiInstance(context, accountId, PlurkApi.class);
    }

    @Nullable
    public static <T> T getPlurkApiInstance(final Context context, final long accountId, Class<T> cls) {
        if (context == null) return null;
        final Account account = Account.getAccount(context, accountId);
        return getInstance(context, account, cls);
    }

    static <T> T getInstance(final Context context, final Account account, final Class<T> cls) {
        if (account == null) return null;
        return PlurkAPIFactory.getInstance(context, getEndpoint(account, cls), account, cls);
    }
    public static <T> T getInstance(final Context context, final Endpoint endpoint, final Account account, Class<T> cls) {
        return PlurkAPIFactory.getInstance(context, endpoint, getAuthorization(account), cls);
    }

    public static <T> T getInstance(final Context context, final Endpoint endpoint, final Authorization auth, Class<T> cls) {
        final RestAPIFactory factory = new RestAPIFactory();
        final String userAgent = getCatPlurkUserAgent(context);
        factory.setClient(createHttpClient(context));
        factory.setConverter(new ApiConverter());
        factory.setEndpoint(endpoint);
        factory.setAuthorization(auth);
        factory.setRequestInfoFactory(new CatPlurkRequestInfoFactory());
        factory.setHttpRequestFactory(new CatPlurkHttpRequestFactory(userAgent));
        factory.setExceptionFactory(new CatPlurkExceptionFactory());
        return factory.build(cls);
    }

    public static Endpoint getEndpoint(Account account, Class<?> cls) {
        return new OAuthEndpoint(PLURK_API_BASE);
    }

    public static Authorization getAuthorization(Account account) {
        final OAuthToken accessToken = new OAuthToken(account.oauthToken, account.oauthTokenSecret);
        return new OAuthAuthorization(PLURK_APP_KEY, PLURK_APP_SECRET, accessToken);
    }

    public static RestHttpClient createHttpClient(final Context context) {
        final OkHttpClient client = new OkHttpClient();
        client.setConnectTimeout(10, TimeUnit.SECONDS);
        return new OkHttpRestClient(context, client);
    }

    // User-Agent = product *( RWS ( product / comment ) )
    // Mozilla/[version] ([system and browser information]) [platform] ([platform details]) [extensions]
    public static String getCatPlurkUserAgent(final Context context) {
        final PackageManager pm = context.getPackageManager();
        try {
            final PackageInfo pi = pm.getPackageInfo(CATPLURK_PACKAGE_NAME, 0);
            return String.format("%s/%s (%s)", CATPLURK_APP_NAME, pi.versionName, CATPLURK_PROJECT_URL);
        } catch (final PackageManager.NameNotFoundException e) {
            return String.format("%s (%s)", CATPLURK_APP_NAME, CATPLURK_PROJECT_URL);
        }
    }

    public static Endpoint getOAuthRestEndpoint() {
        return new PlurkEndpoint();
    }

    public static Endpoint getOAuthEndpoint() {
        return new OAuthEndpoint();
    }

    //region RestAPIFactory
    public static class CatPlurkRequestInfoFactory implements RequestInfoFactory {
        private static HashMap<String, String> sExtraParams = new HashMap<>();

        static {
            sExtraParams.put("favorers_detail"  , "true");
            sExtraParams.put("limited_detail"   , "true");
            sExtraParams.put("replurkers_detail", "true");
        }

        @Override
        public RestRequestInfo create(RestMethodInfo methodInfo) {
            final RestMethod method = methodInfo.getMethod();
            final String path = methodInfo.getPath();
            final List<Pair<String, String>> queries = new ArrayList<>(methodInfo.getQueries());
            final List<Pair<String, String>> forms = new ArrayList<>(methodInfo.getForms());
            final List<Pair<String, String>> headers = methodInfo.getHeaders();
            final List<Pair<String, TypedData>> parts = methodInfo.getParts();
            final FileValue file = methodInfo.getFile();
            final Map<String, Object> extras = methodInfo.getExtras();

            final MethodExtra methodExtra = methodInfo.getMethodExtra();
            if (methodExtra != null && "extra_params".equals(methodExtra.name())) {
                final String[] extraParamKeys = methodExtra.values();
                if (parts.isEmpty()) {
                    final List<Pair<String, String>> params = method.hasBody() ? forms : queries;
                    for (String key : extraParamKeys) {
                        addParameter(params, key, sExtraParams.get(key));
                    }
                } else {
                    for (String key : extraParamKeys) {
                        addPart(parts, key, sExtraParams.get(key));
                    }
                }
            }
            return new RestRequestInfo(method.value(), path, queries, forms, headers, parts, file,
                    methodInfo.getBody(), extras);
        }
    }

    public static class CatPlurkHttpRequestFactory implements HttpRequestFactory {

        private final String userAgent;

        public CatPlurkHttpRequestFactory(final String userAgent) {
            this.userAgent = userAgent;
        }

        @Override
        public RestHttpRequest create(@NonNull Endpoint endpoint, @NonNull RestRequestInfo info,
                                      @Nullable Authorization authorization) {
            final String restMethod = info.getMethod();
            final String url = Endpoint.constructUrl(endpoint.getUrl(), info);
            final ArrayList<Pair<String, String>> headers = new ArrayList<>(info.getHeaders());

            if (authorization != null && authorization.hasAuthorization()) {
                headers.add(Pair.create("Authorization", authorization.getHeader(endpoint, info)));
            }
            headers.add(Pair.create("User-Agent", userAgent));
            return new RestHttpRequest(restMethod, url, headers, info.getBody(), null);
        }
    }

    public static class CatPlurkExceptionFactory implements ExceptionFactory {
        @Override
        public Exception newException(Throwable cause, RestHttpRequest request, RestHttpResponse response) {
            final PlurkException te;
            if (cause != null) {
                te = new PlurkException(cause);
            } else {
                te = new PlurkException();
            }
            te.setResponse(response);
            return te;
        }
    }
    //endregion RestAPIFactory

    //region RestAPIFactory Helper
    private static void addParameter(List<Pair<String, String>> params, String name, Object value) {
        params.add(Pair.create(name, String.valueOf(value)));
    }

    private static void addPart(List<Pair<String, TypedData>> params, String name, Object value) {
        final TypedData typedData = new StringTypedData(String.valueOf(value), Charset.defaultCharset());
        params.add(Pair.create(name, typedData));
    }
    //endregion RestAPIFactory Helper
}
