package tw.catcafe.catplurk.android.plurkapi;

import org.mariotaku.restfu.http.RestHttpRequest;
import org.mariotaku.restfu.http.RestHttpResponse;

import java.util.Locale;

/**
 * An exception class that will be thrown when Plurk API calls are failed.
 * In case the Plurk server returned HTTP error code,
 * you can get the HTTP status code using getStatusCode() method.
 *
 * @author Davy
 */
public class PlurkException extends Exception {

    private static final long serialVersionUID = 5404760728449552495L;
    private int statusCode = -1;
    private RestHttpRequest request;
    private RestHttpResponse response;

    public PlurkException() {
    }

    public PlurkException(final Throwable cause) {
        super(cause);
    }

    public PlurkException(final String message) {
        super(message);
    }

    public PlurkException(final String message, final Throwable cause, final int statusCode) {
        super(message, cause);
        this.statusCode = statusCode;
    }

    public PlurkException(final String message, final RestHttpRequest req, final RestHttpResponse res) {
        this(message);
        request = req;
        setResponse(res);
    }

    public PlurkException(final String message, final RestHttpResponse res) {
        this(message, null, res);
    }

    public void setResponse(final RestHttpResponse res) {
        response = res;
        if (res != null) {
            statusCode = res.getStatus();
        }
    }

    public RestHttpResponse getResponse() {
        return response;
    }

    @Override
    public String getMessage() {
        if (statusCode != -1)
            return String.format(Locale.US, "Error %d", statusCode);
        else
            return super.getMessage();
    }

    public int getStatusCode() {
        return statusCode;
    }

    // region Kinds
    public boolean isCausedByNetworkIssue() {
        return getCause() instanceof java.io.IOException;
    }
    // endregion Kinds


    @Override
    public String toString() {
        return getMessage();
    }
}
