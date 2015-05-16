package pl.goralewski.artur.golfium.dataManagement.tables;

import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

/**
 * Created by Artur Góralewski on 09/05/2015.
 */
public class UsersTable implements Table{

    // Database table
    public static final String TABLE_NAME = "users";
    public static final String COLUMN_ID = "_id";
    public static final String COLUMN_LOGIN = "login";
    public static final String COLUMN_PASSWORD = "password";

    public static final String[] USERS_TABLE_COLUMNS = { COLUMN_ID, COLUMN_LOGIN, COLUMN_PASSWORD};

    // Database creation SQL statement
    private static final String TABLE_CREATE = "create table "
            + TABLE_NAME
            + " ("
            + COLUMN_ID + " integer primary key autoincrement, "
            + COLUMN_LOGIN + " text not null, "
            + COLUMN_PASSWORD + " text not null "
            + ");";

    @Override
    public void onCreate(SQLiteDatabase database) {
        database.execSQL(TABLE_CREATE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase database, int oldVersion,
                                 int newVersion) {
        Log.w(UsersTable.class.getName(), "Upgrading database from version "
                + oldVersion + " to " + newVersion
                + ", which will destroy all old data");
        database.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME);
        onCreate(database);
    }
}
