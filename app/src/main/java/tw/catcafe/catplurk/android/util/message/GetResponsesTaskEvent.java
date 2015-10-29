package tw.catcafe.catplurk.android.util.message;

import android.net.Uri;
import android.support.annotation.NonNull;

/**
 * @author Davy
 */
public class GetResponsesTaskEvent {
    @NonNull
    public final Uri uri;
    public final boolean running;
    public final Exception exception;

    public GetResponsesTaskEvent(@NonNull Uri uri, boolean running, Exception exception) {
        this.uri = uri;
        this.running = running;
        this.exception = exception;
    }
}
