package tw.catcafe.catplurk.android.util;

import java.util.List;

import tw.catcafe.catplurk.android.Constants;
import tw.catcafe.catplurk.android.api.ListResponse;
import tw.catcafe.catplurk.android.plurkapi.model.Plurk;

/**
 * @author Davy
 */
public class PlurkWrapper implements Constants {
    //region ListResponse
    public static final class PlurkListResponse extends PlurkApiListResponse<Plurk> {
        public final boolean truncated;

        public PlurkListResponse(final long accountId, final Exception exception) {
            this(accountId, -1, -1, null, false, exception);
        }

        public PlurkListResponse(final long accountId, final List<Plurk> list) {
            this(accountId, -1, -1, list, false, null);
        }

        public PlurkListResponse(final long accountId, final long maxId, final long sinceId,
                                 final List<Plurk> list, final boolean truncated) {
            this(accountId, maxId, sinceId, list, truncated, null);
        }

        PlurkListResponse(final long accountId, final long maxId, final long sinceId, final List<Plurk> list,
                          final boolean truncated, final Exception exception) {
            super(accountId, maxId, sinceId, list, exception);
            this.truncated = truncated;
        }
    }
    public static class PlurkApiListResponse<Data> extends ListResponse<Data> {

        public final long accountId, maxId, sinceId;

        public PlurkApiListResponse(final long accountId, final Exception exception) {
            this(accountId, -1, -1, null, exception);
        }

        public PlurkApiListResponse(final long accountId, final long maxId, final long sinceId, final List<Data> list) {
            this(accountId, maxId, sinceId, list, null);
        }

        PlurkApiListResponse(final long accountId, final long maxId, final long sinceId, final List<Data> list,
                             final Exception exception) {
            super(list, exception);
            this.accountId = accountId;
            this.maxId = maxId;
            this.sinceId = sinceId;
        }

    }
    //endregion ListResponse
}
