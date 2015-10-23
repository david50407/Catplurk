package tw.catcafe.catplurk.android.api;

import android.os.Bundle;

import java.util.List;

/**
 * @author Davy
 */
public class ListResponse<Data> extends SingleResponse<List<Data>> {
    public final List<Data> list;

    public ListResponse(final List<Data> list, final Exception exception) {
        super(list, exception);
        this.list = list;
    }

    public ListResponse(final List<Data> list, final Exception exception, final Bundle extras) {
        super(list, exception, extras);
        this.list = list;
    }

    public static <Data> ListResponse<Data> getListInstance(Exception exception) {
        return new ListResponse<>(null, exception);
    }

    public static <Data> ListResponse<Data> getListInstance(List<Data> data) {
        return new ListResponse<>(data, null);
    }
}