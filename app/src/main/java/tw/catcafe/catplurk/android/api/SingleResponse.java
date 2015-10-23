package tw.catcafe.catplurk.android.api;

import android.os.Bundle;
import android.support.annotation.NonNull;

/**
 * @author Davy
 */
public class SingleResponse<Data> {
    private final Exception exception;
    private final Data data;
    private final Bundle extras;

    public SingleResponse(final Data data, final Exception exception) {
        this(data, exception, null);
    }

    public SingleResponse(final Data data, final Exception exception, final Bundle extras) {
        this.data = data;
        this.exception = exception;
        this.extras = extras != null ? extras : new Bundle();
    }

    @Override
    public boolean equals(final Object obj) {
        if (this == obj) return true;
        if (obj == null) return false;
        if (!(obj instanceof SingleResponse)) return false;
        final SingleResponse<?> other = (SingleResponse<?>) obj;
        if (getData() == null) {
            if (other.getData() != null) return false;
        } else if (!getData().equals(other.getData())) return false;
        if (exception == null) {
            if (other.exception != null) return false;
        } else if (!exception.equals(other.exception)) return false;
        if (!getExtras().equals(other.getExtras())) return false;
        return true;
    }

    public Data getData() {
        return data;
    }

    public Exception getException() {
        return exception;
    }

    @NonNull
    public Bundle getExtras() {
        return extras;
    }

    public boolean hasData() {
        return getData() != null;
    }

    public boolean hasException() {
        return exception != null;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + (getData() == null ? 0 : getData().hashCode());
        result = prime * result + (exception == null ? 0 : exception.hashCode());
        result = prime * result + (getExtras().hashCode());
        return result;
    }

    public static <T> SingleResponse<T> getInstance() {
        return new SingleResponse<>(null, null);
    }

    public static <T> SingleResponse<T> getInstance(final Exception exception) {
        return new SingleResponse<>(null, exception);
    }

    public static <T> SingleResponse<T> getInstance(final T data) {
        return new SingleResponse<>(data, null);
    }

    public static <T> SingleResponse<T> getInstance(final T data, final Exception exception) {
        return new SingleResponse<>(data, exception);
    }
}
