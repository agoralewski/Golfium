package pl.goralewski.artur.golfium.dataManagement.tables;

import android.database.sqlite.SQLiteDatabase;

public interface Table {

    void onCreate(SQLiteDatabase database);
    void onUpgrade(SQLiteDatabase database, int oldVersion, int newVersion);

}
