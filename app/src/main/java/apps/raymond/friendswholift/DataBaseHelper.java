package apps.raymond.friendswholift;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import apps.raymond.friendswholift.LiftObject.LiftObject;

public class DataBaseHelper extends SQLiteOpenHelper {

    private static final String DATABASE_NAME = "Lifts_db.db";
    private static final int DATABASE_VERSION = 1;
    private static final String TABLE_NAME = "lifts_table";
    private static final String COL_ID = "_ID";
    private static final String COL_TYPE = "type";
    private static final String COL_WEIGHT = "weight";

    public DataBaseHelper(Context context){
        super(context,DATABASE_NAME,null,DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db){
        Log.d ("DBCREATION","Create table in database.");
        String CREATE_TABLE_LIFTS = "CREATE TABLE "
                + TABLE_NAME + " (" + COL_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
                + COL_TYPE + " TEXT, " + COL_WEIGHT + " REAL)";
        db.execSQL(CREATE_TABLE_LIFTS);
    }

    public long insertWeight(String type, double weight){
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();

        contentValues.put(COL_TYPE,type);
        contentValues.put(COL_WEIGHT, weight);
        long id = db.insert(DATABASE_NAME, null, contentValues);
        db.close();

        return id;
    }

    public LiftObject getLift(long id){

        SQLiteDatabase db = this.getReadableDatabase();

        Cursor cursor = db.query(TABLE_NAME,new String[]{COL_ID,COL_TYPE,COL_WEIGHT},COL_ID + "=?",
                new String[]{String.valueOf(id)},null,null,null,null);

        if (cursor == null){
            cursor.moveToFirst();
        }
        LiftObject lift = new LiftObject(cursor.getInt(cursor.getColumnIndex(COL_ID)),
                cursor.getString(cursor.getColumnIndex(COL_TYPE)),
                cursor.getDouble(cursor.getColumnIndex(COL_WEIGHT)));
        return lift;
    }

    //This method is only called when the db version is incremented.
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion){
        }
}