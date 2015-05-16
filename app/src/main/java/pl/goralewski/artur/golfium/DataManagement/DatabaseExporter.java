package pl.goralewski.artur.golfium.dataManagement;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Environment;
import android.util.Log;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;

/**
 * Created by Artur Góralewski on 10/05/2015.
 */
public class DatabaseExporter {

    private static final String DATASUBDIRECTORY = "/golfium";
    private static final String DEBUG_TAG = "DatabaseExporter";
    private SQLiteDatabase db;
    private MyXMLBuilder myXMLBuilder;
//    private MyUtility mu = new MyUtility();

    public DatabaseExporter(SQLiteDatabase db) {
        this.db = db;
    }

    public void export(String dbName, String exportFileName) throws IOException {
        Log.i(DEBUG_TAG, "exporting database - " + dbName + " exportFileName=" + exportFileName);

        this.myXMLBuilder = new MyXMLBuilder();
        this.myXMLBuilder.start(dbName);

        // get the tables
        String sql = "select * from sqlite_master";
        Cursor cursor = this.db.rawQuery(sql, new String[0]);
        Log.d(DEBUG_TAG, "select * from sqlite_master, cur size " + cursor.getCount());
        if (cursor.moveToFirst()) {
            do {
                String tableName = cursor.getString(cursor.getColumnIndex("name"));
                Log.d(DEBUG_TAG, "table name " + tableName);

                // skip metadata, sequence, and uidx (unique indexes)
                if (!tableName.equals("android_metadata") && !tableName.equals("sqlite_sequence")
                        && !tableName.startsWith("uidx")) {
                    this.exportTable(tableName);
                }
            } while (cursor.moveToNext());
        }
        String output = this.myXMLBuilder.end();
        this.writeToFile(output, exportFileName + ".txt");
        Log.i(DEBUG_TAG, "exporting database complete");
    }

    private void exportTable(final String tableName) throws IOException {
        Log.d(DEBUG_TAG, "exporting table - " + tableName);
        this.myXMLBuilder.openTable(tableName);
        String sql = "select * from " + tableName;
        Cursor c = this.db.rawQuery(sql, new String[0]);
        if (c.moveToFirst()) {
            int cols = c.getColumnCount();
            do {
                this.myXMLBuilder.openRow();
                for (int i = 0; i < cols; i++) {
                    this.myXMLBuilder.addColumn(c.getColumnName(i), c.getString(i));
                }
                this.myXMLBuilder.closeRow();
            } while (c.moveToNext());
        }
        c.close();
        this.myXMLBuilder.closeTable();
    }

    private void writeToFile(String xmlString, String exportFileName) throws IOException {
        File dir = new File(Environment.getExternalStorageDirectory(), DATASUBDIRECTORY);
        if (!dir.exists()) {
            dir.mkdirs();
        }
        File file = new File(dir, exportFileName);
        file.createNewFile();

        ByteBuffer buff = ByteBuffer.wrap(xmlString.getBytes());
        FileChannel channel = new FileOutputStream(file).getChannel();
        try {
            channel.write(buff);
        } finally {
            if (channel != null)
                channel.close();
        }
    }

    class MyXMLBuilder {
        private static final String OPEN_XML_STANZA = "";
        private static final String CLOSE_WITH_TICK = "'>\n";
        private static final String DB_OPEN = "<database name='";
        private static final String DB_CLOSE = "</database>";
        private static final String TABLE_OPEN = "<table name='";
        private static final String TABLE_CLOSE = "</table>";
        private static final String ROW_OPEN = "\n";
        private static final String ROW_CLOSE = "";
        private static final String COL_OPEN = "<col name='";
        private static final String COL_CLOSE = "|";

        private final StringBuilder sb;

        public MyXMLBuilder() /*throws IOException*/ {
            this.sb = new StringBuilder();
        }

        void start(String dbName) {
            this.sb.append(OPEN_XML_STANZA);
            this.sb.append(DB_OPEN);
            this.sb.append(dbName);
            this.sb.append(CLOSE_WITH_TICK);
        }

        String end() /*throws IOException*/ {
            this.sb.append(DB_CLOSE);
            return this.sb.toString();
        }

        void openTable(String tableName) {
            this.sb.append(TABLE_OPEN );
            this.sb.append(tableName );
            this.sb.append(CLOSE_WITH_TICK);
        }

        void closeTable() {
            this.sb.append(TABLE_CLOSE);
        }

        void openRow() {
            this.sb.append(ROW_OPEN);
        }

        void closeRow() {
            this.sb.append(ROW_CLOSE);
        }

        void addColumn(final String name, final String val) /*throws IOException*/ {
         this.sb.append(COL_OPEN);
         this.sb.append(name);
         this.sb.append( "' value=" );
         this.sb.append(val);
         this.sb.append(COL_CLOSE);
        }
    }
}
