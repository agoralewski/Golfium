package pl.goralewski.artur.golfium.dataManagement.tables;

import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

/**
 * Created by Artur Góralewski on 10/05/2015.
 */
public class CoordinatesTable implements Table{
    // Database table
    public static final String TABLE_NAME = "coordinates";
    public static final String COLUMN_ID = "_id";
    public static final String COLUMN_LATITUDE = "latitude"; 
    public static final String COLUMN_LONGITUDE = "longitude";
    public static final String COLUMN_TIME = "time";
    public static final String COLUMN_HOLE_PLAY_ID = "hole_play_id";

    public static final String[] COORDINATES_TABLE_COLUMNS = {COLUMN_ID, COLUMN_LATITUDE, COLUMN_LONGITUDE, COLUMN_TIME, COLUMN_HOLE_PLAY_ID};

    // Database creation SQL statement
    private static final String TABLE_CREATE = "create table "
            + TABLE_NAME
            + " ("
            + COLUMN_ID + " integer primary key autoincrement, "
            + COLUMN_LATITUDE + " real not null, "
            + COLUMN_LONGITUDE + " real not null,"
            + COLUMN_TIME + " text not null,"         ////strings ("YYYY-MM-DD HH:MM:SS.SSS")
            + COLUMN_HOLE_PLAY_ID + " integer not null,"
            + "FOREIGN KEY(" + COLUMN_HOLE_PLAY_ID + ") REFERENCES "
            + HolePlayTable.TABLE_NAME + "(" + HolePlayTable.TABLE_NAME + "." + HolePlayTable.COLUMN_ID + ")"
            + ");";

    @Override
    public void onCreate(SQLiteDatabase database) {
        database.execSQL(TABLE_CREATE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase database, int oldVersion,
                                 int newVersion) {
        Log.w(CoordinatesTable.class.getName(), "Upgrading database from version "
                + oldVersion + " to " + newVersion
                + ", which will destroy all old data");
        database.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME);
        onCreate(database);
    }
}
