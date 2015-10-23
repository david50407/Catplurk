package tw.catcafe.catplurk.android.plurkapi.model;

/**
 * @author Davy
 */
public interface Wrapper<T> extends PlurkApiResponse {
    T getWrapped(Object extra);
}
