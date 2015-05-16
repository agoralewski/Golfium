package pl.goralewski.artur.golfium.dataManagement;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.List;
import java.util.Map;

import pl.goralewski.artur.golfium.dataManagement.tables.CoordinatesTable;
import pl.goralewski.artur.golfium.dataManagement.tables.GameTable;
import pl.goralewski.artur.golfium.dataManagement.tables.HolePlayTable;
import pl.goralewski.artur.golfium.dataManagement.tables.Table;
import pl.goralewski.artur.golfium.dataManagement.tables.UsersTable;
import pl.goralewski.artur.golfium.model.Coordinates;
import pl.goralewski.artur.golfium.model.Game;
import pl.goralewski.artur.golfium.model.HolePlay;
import pl.goralewski.artur.golfium.model.TableRow;
import pl.goralewski.artur.golfium.model.User;

/**
 * Created by Artur Góralewski on 07/05/2015.
 */
public class DatabaseOpenHelper extends SQLiteOpenHelper{
    private final String MY_TAG = "DatabaseOpenHelper";
    final private static String DB_NAME = "golfium.db";
    final private static Integer VERSION = 1;
    final private Context mContext;
    final private Map<String, Table> tableMap;

    public DatabaseOpenHelper(Context context) {
        super(context, DB_NAME, null, VERSION);
        this.mContext = context;
        this.tableMap = new Hashtable<>();
        this.tableMap.put(UsersTable.class.getSimpleName(), new UsersTable());
        this.tableMap.put(GameTable.class.getSimpleName(), new GameTable());
        this.tableMap.put(HolePlayTable.class.getSimpleName(), new HolePlayTable());
        this.tableMap.put(CoordinatesTable.class.getSimpleName(), new CoordinatesTable());
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        for (Table table : this.tableMap.values()) {
            table.onCreate(db);
        }
        Log.d(MY_TAG, "onCreate");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        for (Table table : this.tableMap.values()) {
            table.onUpgrade(db, oldVersion, newVersion);
        }
        Log.d(MY_TAG, "onUpgrade");
    }

    /**
     * All CRUD(Create(Instert), Read, Update, Delete) Operations
     */

    public void addNewRow(TableRow tableRow) {

        if(tableRow instanceof User)
            addUser((User) tableRow);
        if(tableRow instanceof Game)
            addGame((Game) tableRow);
        if(tableRow instanceof HolePlay)
            addHolePlay((HolePlay) tableRow);
        if(tableRow instanceof Coordinates)
            addCoordinates((Coordinates) tableRow);
    }

    protected void addUser(User user)
    {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();

        values.put(UsersTable.COLUMN_LOGIN, user.getLogin());
        values.put(UsersTable.COLUMN_PASSWORD, user.getPassword());

        db.insert(UsersTable.TABLE_NAME, null, values);// Inserting Row
        db.close(); // Closing database connection
    }

    protected void addGame(Game game)
    {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();

        values.put(GameTable.COLUMN_START, game.getStart().toString());
        values.put(GameTable.COLUMN_END, game.getEnd().toString());
        values.put(GameTable.COLUMN_SCORE, game.getScore());
        values.put(GameTable.COLUMN_USER_ID, game.getUserId());
        values.put(GameTable.COLUMN_FIELD_ID, game.getFieldId());

        db.insert(GameTable.TABLE_NAME, null, values);// Inserting Row
        db.close(); // Closing database connection
    }

    protected void addHolePlay(HolePlay holePlay)
    {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();

        values.put(HolePlayTable.COLUMN_NUMBER_OF_HITS, holePlay.getNumberOfHits());
        values.put(HolePlayTable.COLUMN_GAME_ID, holePlay.getGameId());
        values.put(HolePlayTable.COLUMN_HOLE_ID, holePlay.getHoleId());

        db.insert(HolePlayTable.TABLE_NAME, null, values);// Inserting Row
        db.close(); // Closing database connection
    }

    protected void addCoordinates(Coordinates coordinates)
    {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();

        values.put(CoordinatesTable.COLUMN_LATITUDE, coordinates.getLatitude());
        values.put(CoordinatesTable.COLUMN_LONGITUDE, coordinates.getLongitude());
        values.put(CoordinatesTable.COLUMN_TIME, coordinates.getTime().toString());
        values.put(CoordinatesTable.COLUMN_HOLE_PLAY_ID, coordinates.getHolePlayId());

        db.insert(CoordinatesTable.TABLE_NAME, null, values);// Inserting Row
        db.close(); // Closing database connection
    }

    public User getUser(Integer id)
    {
        SQLiteDatabase db = this.getReadableDatabase();

        Cursor cursor = db.query(UsersTable.TABLE_NAME, UsersTable.USERS_TABLE_COLUMNS, UsersTable.COLUMN_ID + "=?",
                new String[] { String.valueOf(id) }, null, null, null, null);
        if (cursor != null)
            cursor.moveToFirst();

        User user = new User(cursor.getInt(0), cursor.getString(1), cursor.getString(2));

        cursor.close();
        db.close();
        return user;
    }

    public List<User> getUsersList()
    {
        SQLiteDatabase db = this.getReadableDatabase();

        String selectQuery = "select * from " + UsersTable.TABLE_NAME;
        Cursor cursor = db.rawQuery(selectQuery, null);
        List<User> toReturn = new ArrayList<>();
        // looping through all rows and adding to list
        if(cursor.moveToFirst()){
            do{
                User user = new User(cursor.getInt(0),
                        cursor.getString(1),
                        cursor.getString(2));
                toReturn.add(user);
            }while(cursor.moveToNext());
        }
        cursor.close();
        db.close();
        return toReturn;
    }

    public Game getGame(Integer id)
    {
        SQLiteDatabase db = this.getReadableDatabase();

        DateFormat format = new SimpleDateFormat("YYYY-MM-DD HH:MM:SS.SSS");

        Cursor cursor = db.query(GameTable.TABLE_NAME, GameTable.GAME_TABLE_COLUMNS, GameTable.COLUMN_ID + "=?",
                new String[] { String.valueOf(id) }, null, null, null, null);
        if (cursor != null)
            cursor.moveToFirst();

        Game game = null;
        try {
            game = new Game(cursor.getInt(0), format.parse(cursor.getString(1)), format.parse(cursor.getString(2)),
                    cursor.getInt(3), cursor.getInt(4), cursor.getInt(5) );
        } catch (ParseException e) {
            e.printStackTrace();
        }

        cursor.close();
        db.close();
        return game;
    }

    public List<Game> getGamesList()
    {
        SQLiteDatabase db = this.getReadableDatabase();
        DateFormat format = new SimpleDateFormat("YYYY-MM-DD HH:MM:SS.SSS");

        String selectQuery = "select * from " + GameTable.TABLE_NAME;
        Cursor cursor = db.rawQuery(selectQuery, null);
        List<Game> toReturn = new ArrayList<>();
        // looping through all rows and adding to list
        if(cursor.moveToFirst()){
            do{
                Game game = null;
                try {
                    game = new Game(cursor.getInt(0),
                            format.parse(cursor.getString(1)),
                            format.parse(cursor.getString(2)),
                            cursor.getInt(3),
                            cursor.getInt(4),
                            cursor.getInt(5));
                toReturn.add(game);
                } catch (ParseException e) {
                    e.printStackTrace();
                }
            }while(cursor.moveToNext());
        }
        cursor.close();
        db.close();
        return toReturn;
    }

    public HolePlay getHolePlay(Integer id)
    {
        SQLiteDatabase db = this.getReadableDatabase();

        Cursor cursor = db.query(HolePlayTable.TABLE_NAME, HolePlayTable.HOLE_PLAY_TABLE_COLUMNS, HolePlayTable.COLUMN_ID + "=?",
                new String[]{String.valueOf(id)}, null, null, null, null);
        if (cursor != null)
            cursor.moveToFirst();

        HolePlay holePlay = new HolePlay(cursor.getInt(0), cursor.getInt(1), cursor.getInt(2), cursor.getInt(3) );

        cursor.close();
        db.close();
        return holePlay;
    }

    public List<HolePlay> getHolePlaysList()
    {
        SQLiteDatabase db = this.getReadableDatabase();

        String selectQuery = "select * from " + HolePlayTable.TABLE_NAME;
        Cursor cursor = db.rawQuery(selectQuery, null);
        List<HolePlay> toReturn = new ArrayList<>();
        // looping through all rows and adding to list
        if(cursor.moveToFirst()){
            do{
                HolePlay holePlay = new HolePlay(cursor.getInt(0),
                        cursor.getInt(1),
                        cursor.getInt(2),
                        cursor.getInt(3));
                toReturn.add(holePlay);
            }while(cursor.moveToNext());
        }
        cursor.close();
        db.close();
        return toReturn;
    }

    public Coordinates getCoordinates(Integer id)
    {
        SQLiteDatabase db = this.getReadableDatabase();

        DateFormat format = new SimpleDateFormat("YYYY-MM-DD HH:MM:SS.SSS");

        Cursor cursor = db.query(CoordinatesTable.TABLE_NAME, CoordinatesTable.COORDINATES_TABLE_COLUMNS, CoordinatesTable.COLUMN_ID + "=?",
                new String[] { String.valueOf(id) }, null, null, null, null);
        if (cursor != null)
            cursor.moveToFirst();

        Coordinates coordinates = null;
        try {
            coordinates = new Coordinates(cursor.getInt(0), cursor.getDouble(1), cursor.getDouble(2),
                    format.parse(cursor.getString(3)), cursor.getInt(4) );
        } catch (ParseException e) {
            e.printStackTrace();
        }

        cursor.close();
        db.close();
        return coordinates;
    }

    public List<Coordinates> getCoordinatesList()
    {
        SQLiteDatabase db = this.getReadableDatabase();
        DateFormat format = new SimpleDateFormat("YYYY-MM-DD HH:MM:SS.SSS");

        String selectQuery = "select * from " + CoordinatesTable.TABLE_NAME;
        Cursor cursor = db.rawQuery(selectQuery, null);
        List<Coordinates> toReturn = new ArrayList<>();
        // looping through all rows and adding to list
        if(cursor.moveToFirst()){
            Coordinates coordinates = null;
            do{
                try {
                    coordinates = new Coordinates(cursor.getInt(0),
                            cursor.getDouble(1),
                            cursor.getDouble(2),
                            format.parse(cursor.getString(3)),
                            cursor.getInt(4));
                toReturn.add(coordinates);
                } catch (ParseException e) {
                    e.printStackTrace();
                }
            }while(cursor.moveToNext());
        }
        cursor.close();
        db.close();
        return toReturn;
    }

/*--todo--*/
//    public int Update_Contact(Contact contact) {
//        SQLiteDatabase db = this.getWritableDatabase();
//
//        ContentValues values = new ContentValues();
//        values.put(KEY_NAME, contact.getName());
//        values.put(KEY_PH_NO, contact.getPhoneNumber());
//        values.put(KEY_EMAIL, contact.getEmail());
//
//        return db.update(TABLE_CONTACTS, values, KEY_ID + " = ?",
//                new String[] { String.valueOf(contact.getID()) });
//    }
//
//    public void Delete_Contact(int id) {
//        SQLiteDatabase db = this.getWritableDatabase();
//        db.delete(TABLE_CONTACTS, KEY_ID + " = ?",
//                new String[] { String.valueOf(id) });
//        db.close();
//    }
//
//    public int Get_Total_Contacts() {
//        String countQuery = "SELECT  * FROM " + TABLE_CONTACTS;
//        SQLiteDatabase db = this.getReadableDatabase();
//        Cursor cursor = db.rawQuery(countQuery, null);
//        cursor.close();
//
//        return cursor.getCount();
//    }







}
