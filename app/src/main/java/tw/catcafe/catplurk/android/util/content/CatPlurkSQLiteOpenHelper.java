package tw.catcafe.catplurk.android.util.content;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.os.Build;

import org.mariotaku.sqliteqb.library.Columns;
import org.mariotaku.sqliteqb.library.Columns.Column;
import org.mariotaku.sqliteqb.library.Constraint;
import org.mariotaku.sqliteqb.library.Expression;
import org.mariotaku.sqliteqb.library.NewColumn;
import org.mariotaku.sqliteqb.library.OnConflict;
import org.mariotaku.sqliteqb.library.SQLQuery;
import org.mariotaku.sqliteqb.library.SQLQueryBuilder;
import org.mariotaku.sqliteqb.library.Table;
import org.mariotaku.sqliteqb.library.query.SQLCreateIndexQuery;
import org.mariotaku.sqliteqb.library.query.SQLCreateTableQuery;
import org.mariotaku.sqliteqb.library.query.SQLDeleteQuery;
import org.mariotaku.sqliteqb.library.query.SQLCreateTriggerQuery.*;

import tw.catcafe.catplurk.android.constant.DatabaseConstant;
import tw.catcafe.catplurk.android.provider.CatPlurkDataStore.Accounts;
import tw.catcafe.catplurk.android.provider.CatPlurkDataStore.Plurks;
import tw.catcafe.catplurk.android.provider.CatPlurkDataStore.Users;

import static tw.catcafe.catplurk.android.util.content.DatabaseUpgradeHelper.safeUpgrade;

/**
 * @author Davy
 */
public final class CatPlurkSQLiteOpenHelper extends SQLiteOpenHelper implements DatabaseConstant {

    private final Context mContext;

    public CatPlurkSQLiteOpenHelper(final Context context, final String name, final int version) {
        super(context, name, null, version);
        mContext = context;
    }

    //region DB events
    @Override
    public void onCreate(final SQLiteDatabase db) {
        db.beginTransaction();
        db.execSQL(createTable(Accounts.TABLE_NAME, Accounts.COLUMNS, Accounts.TYPES, true));
        db.execSQL(createTable(Plurks.TABLE_NAME, Plurks.COLUMNS, Plurks.TYPES, true));
        db.execSQL(createTable(Users.TABLE_NAME, Users.COLUMNS, Users.TYPES, true));

        createViews(db);
        createTriggers(db);
        createIndices(db);

        db.setTransactionSuccessful();
        db.endTransaction();
    }

    @Override
    public void onDowngrade(final SQLiteDatabase db, final int oldVersion, final int newVersion) {
        handleVersionChange(db, oldVersion, newVersion);
    }

    @Override
    public void onUpgrade(final SQLiteDatabase db, final int oldVersion, final int newVersion) {
        handleVersionChange(db, oldVersion, newVersion);
    }
    //endregion DB events

    private Constraint createConflictReplaceConstraint(String... columns) {
        return Constraint.unique(new Columns(columns), OnConflict.IGNORE);
    }

    private void createIndices(SQLiteDatabase db) {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.LOLLIPOP) return;
        db.execSQL(createIndex("plurks_index", Plurks.TABLE_NAME, new String[]{Plurks.ACCOUNT_ID}, true));
        db.execSQL(createIndex("plurks_id_index", Plurks.TABLE_NAME, new String[]{Plurks.ACCOUNT_ID, Plurks.PLURK_ID}, true));
        db.execSQL(createIndex("users_index", Users.TABLE_NAME, new String[]{Users.ACCOUNT_ID}, true));
        db.execSQL(createIndex("users_id_index", Users.TABLE_NAME, new String[]{Users.ACCOUNT_ID, Users.ID}, true));
    }

    private void createViews(SQLiteDatabase db) {
        // No any views required yet.
    }

    private void createTriggers(SQLiteDatabase db) {
        db.execSQL(SQLQueryBuilder.dropTrigger(true, "delete_outdated_plurks").getSQL());
        db.execSQL(SQLQueryBuilder.dropTrigger(true, "delete_outdated_users").getSQL());
        db.execSQL(createDeleteDuplicatePlurkTrigger("delete_outdated_plurks", Plurks.TABLE_NAME).getSQL());
        db.execSQL(createDeleteDuplicateUserTrigger("delete_outdated_users", Users.TABLE_NAME).getSQL());
    }

    //region SQL Triggers Builder
    private SQLQuery createDeleteDuplicatePlurkTrigger(String triggerName, String tableName) {
        final Table table = new Table(tableName);
        final SQLDeleteQuery deleteOld = SQLQueryBuilder.deleteFrom(table).where(Expression.and(
                Expression.equals(new Column(Plurks.ACCOUNT_ID), new Column(Table.NEW, Plurks.ACCOUNT_ID)),
                Expression.equals(new Column(Plurks.PLURK_ID), new Column(Table.NEW, Plurks.PLURK_ID))
        )).build();
        return SQLQueryBuilder.createTrigger(false, true, triggerName)
                .type(Type.BEFORE).event(Event.INSERT).on(table).forEachRow(true)
                .actions(deleteOld).build();
    }

    private SQLQuery createDeleteDuplicateUserTrigger(String triggerName, String tableName) {
        final Table table = new Table(tableName);
        final SQLDeleteQuery deleteOld = SQLQueryBuilder.deleteFrom(table).where(Expression.and(
                Expression.equals(new Column(Users.ACCOUNT_ID), new Column(Table.NEW, Users.ACCOUNT_ID)),
                Expression.equals(new Column(Users.ID), new Column(Table.NEW, Users.ID))
        )).build();
        return SQLQueryBuilder.createTrigger(false, true, triggerName)
                .type(Type.BEFORE).event(Event.INSERT).on(table).forEachRow(true)
                .actions(deleteOld).build();
    }
    //endregion SQL Triggers Builder


    private void handleVersionChange(final SQLiteDatabase db, final int oldVersion, final int newVersion) {
        safeUpgrade(db, Accounts.TABLE_NAME, Accounts.COLUMNS, Accounts.TYPES, false, null);
        safeUpgrade(db, Plurks.TABLE_NAME, Plurks.COLUMNS, Plurks.TYPES, true, null);
        safeUpgrade(db, Users.TABLE_NAME, Users.COLUMNS, Users.TYPES, true, null);
        db.beginTransaction();
        createViews(db);
        createTriggers(db);
        createIndices(db);
        db.setTransactionSuccessful();
        db.endTransaction();
    }

    private static String createTable(final String tableName, final String[] columns, final String[] types,
                                      final boolean createIfNotExists, final Constraint... constraints) {
        final SQLCreateTableQuery.Builder qb = SQLQueryBuilder.createTable(createIfNotExists, tableName);
        qb.columns(NewColumn.createNewColumns(columns, types));
        qb.constraint(constraints);
        return qb.buildSQL();
    }

    private static String createIndex(final String indexName, final String tableName, final String[] columns,
                                      final boolean createIfNotExists) {
        final SQLCreateIndexQuery.Builder qb = SQLQueryBuilder.createIndex(false, createIfNotExists);
        qb.name(indexName);
        qb.on(new Table(tableName), new Columns(columns));
        return qb.buildSQL();
    }
}