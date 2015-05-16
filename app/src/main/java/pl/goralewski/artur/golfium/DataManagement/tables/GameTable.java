package pl.goralewski.artur.golfium.dataManagement.tables;

import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

/**
 * Created by Artur Góralewski on 09/05/2015.
 */
public class GameTable implements Table {

    // Database table
    public static final String TABLE_NAME = "game";
    public static final String COLUMN_ID = "_id";
    public static final String COLUMN_START = "start";
    public static final String COLUMN_END = "end";
    public static final String COLUMN_SCORE = "score";
    public static final String COLUMN_USER_ID = "user_id";
    public static final String COLUMN_FIELD_ID = "field_id";

    public static final String[] GAME_TABLE_COLUMNS = {COLUMN_ID, COLUMN_START, COLUMN_END, COLUMN_SCORE, COLUMN_USER_ID, COLUMN_FIELD_ID };

    // Database creation SQL statement
    private static final String TABLE_CREATE = "create table "
            + TABLE_NAME
            + " ("
            + COLUMN_ID + " integer primary key autoincrement, "
            + COLUMN_START + " text not null, "             //strings ("YYYY-MM-DD HH:MM:SS.SSS")
            + COLUMN_END + " text not null, "         ////strings ("YYYY-MM-DD HH:MM:SS.SSS")
            + COLUMN_SCORE + " integer not null, "
            + COLUMN_USER_ID + " integer not null,"
            + COLUMN_FIELD_ID + " integer not null,"
            + "FOREIGN KEY(" + COLUMN_USER_ID + ") REFERENCES "
            + UsersTable.TABLE_NAME + "(" + UsersTable.TABLE_NAME + "." + UsersTable.COLUMN_ID + ")"
            + ");";

    @Override
    public void onCreate(SQLiteDatabase database) {
        database.execSQL(TABLE_CREATE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase database, int oldVersion,
                                 int newVersion) {
        Log.w(GameTable.class.getName(), "Upgrading database from version "
                + oldVersion + " to " + newVersion
                + ", which will destroy all old data");
        database.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME);
        onCreate(database);
    }
}
