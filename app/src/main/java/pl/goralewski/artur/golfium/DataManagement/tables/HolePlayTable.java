package pl.goralewski.artur.golfium.dataManagement.tables;

import android.database.sqlite.SQLiteDatabase;
import android.util.Log;


/**
 * Created by Artur Góralewski on 10/05/2015.
 */
public class HolePlayTable implements Table {

    // Database table
    public static final String TABLE_NAME = "hole_play";
    public static final String COLUMN_ID = "_id";
    public static final String COLUMN_NUMBER_OF_HITS = "number_of_hits";
    public static final String COLUMN_GAME_ID = "game_id";
    public static final String COLUMN_HOLE_ID = "hole_id";

    public static final String[] HOLE_PLAY_TABLE_COLUMNS = {COLUMN_ID, COLUMN_NUMBER_OF_HITS, COLUMN_GAME_ID, COLUMN_HOLE_ID};

    // Database creation SQL statement
    private static final String TABLE_CREATE = "create table "
            + TABLE_NAME
            + " ("
            + COLUMN_ID + " integer primary key autoincrement, "
            + COLUMN_NUMBER_OF_HITS + " integer not null, "
            + COLUMN_GAME_ID + " integer not null, "
            + COLUMN_HOLE_ID + " integer not null, "
            + "FOREIGN KEY(" + COLUMN_GAME_ID + ") REFERENCES "
            + GameTable.TABLE_NAME + "(" + GameTable.TABLE_NAME + "." + GameTable.COLUMN_ID + ")"
            + ");";

    @Override
    public void onCreate(SQLiteDatabase database) {
        database.execSQL(TABLE_CREATE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase database, int oldVersion,
                                 int newVersion) {
        Log.w(HolePlayTable.class.getName(), "Upgrading database from version "
                + oldVersion + " to " + newVersion
                + ", which will destroy all old data");
        database.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME);
        onCreate(database);
    }
}
