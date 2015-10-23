package tw.catcafe.catplurk.android.plurkapi.model;

import org.mariotaku.restfu.http.RestHttpResponse;

/**
 * @author Davy
 */
public class ResponseCode {
    private final int responseCode;

    public ResponseCode(RestHttpResponse response) {
        responseCode = response.getStatus();
    }

    public int getResponseCode() {
        return responseCode;
    }

    public boolean isSuccessful() {
        return responseCode >= 200 && responseCode < 300;
    }
}
