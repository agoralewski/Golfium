package pl.goralewski.artur.golfium.DataManagement;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by Artur Góralewski on 07/05/2015.
 */
public class CoordinatesDatabaseOpenHelper extends SQLiteOpenHelper{

    final static String COORDINATES_TABLE_NAME = "coordinates";
    final static String ARTIST_NAME = "name";
    final static String _ID = "_id";
    final static String[] columns = { _ID, ARTIST_NAME };

    final private static String CREATE_CMD =

            "CREATE TABLE artists (" + _ID
                    + " INTEGER PRIMARY KEY AUTOINCREMENT, "
                    + ARTIST_NAME + " TEXT NOT NULL)";

    final private static String NAME = "artist_db";
    final private static Integer VERSION = 1;
    final private Context mContext;

    public CoordinatesDatabaseOpenHelper(Context context) {
        super(context, NAME, null, VERSION);
        this.mContext = context;
    }

    @Override
    public void onCreate(SQLiteDatabase db) {

    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }
}
