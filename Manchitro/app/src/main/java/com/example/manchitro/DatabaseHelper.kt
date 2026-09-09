package com.example.manchitro

import android.content.ContentValues
import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import com.example.manchitro.json.PlaceDataItem

class DatabaseHelper(context: Context) : SQLiteOpenHelper(context, DATABASE_NAME, null, DATABASE_VERSION) {

    companion object {
        private const val DATABASE_NAME = "places.db"
        private const val DATABASE_VERSION = 1
        private const val TABLE_NAME = "places"
        private const val COLUMN_ID = "id"
        private const val COLUMN_TITLE = "title"
        private const val COLUMN_LAT = "latitude"
        private const val COLUMN_LON = "longitude"
        private const val COLUMN_IMAGE = "image"
    }

    override fun onCreate(db: SQLiteDatabase?) {
        val createTable = ("CREATE TABLE $TABLE_NAME ($COLUMN_ID INTEGER PRIMARY KEY, "
                + "$COLUMN_TITLE TEXT, $COLUMN_LAT REAL, $COLUMN_LON REAL, $COLUMN_IMAGE TEXT)")
        db?.execSQL(createTable)
    }

    override fun onUpgrade(db: SQLiteDatabase?, oldVersion: Int, newVersion: Int) {
        db?.execSQL("DROP TABLE IF EXISTS $TABLE_NAME")
        onCreate(db)
    }

    fun addPlace(place: PlaceDataItem): Long {
        val db = this.writableDatabase
        val contentValues = ContentValues()
        contentValues.put(COLUMN_ID, place.id)
        contentValues.put(COLUMN_TITLE, place.title)
        contentValues.put(COLUMN_LAT, place.lat)
        contentValues.put(COLUMN_LON, place.lon)
        contentValues.put(COLUMN_IMAGE, place.image)
        val success = db.insert(TABLE_NAME, null, contentValues)
        db.close()
        return success
    }

    fun updatePlace(place: PlaceDataItem): Int {
        val db = this.writableDatabase
        val contentValues = ContentValues()
        contentValues.put(COLUMN_TITLE, place.title)
        contentValues.put(COLUMN_LAT, place.lat)
        contentValues.put(COLUMN_LON, place.lon)
        contentValues.put(COLUMN_IMAGE, place.image)
        val success = db.update(TABLE_NAME, contentValues, "$COLUMN_ID = ?", arrayOf(place.id.toString()))
        db.close()
        return success
    }

    fun getAllPlaces(): List<PlaceDataItem> {
        val placeList: ArrayList<PlaceDataItem> = ArrayList()
        val selectQuery = "SELECT * FROM $TABLE_NAME"
        val db = this.readableDatabase
        val cursor = db.rawQuery(selectQuery, null)
        if (cursor.moveToFirst()) {
            do {
                val place = PlaceDataItem(
                    cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_ID)),
                    cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_TITLE)),
                    cursor.getDouble(cursor.getColumnIndexOrThrow(COLUMN_LAT)),
                    cursor.getDouble(cursor.getColumnIndexOrThrow(COLUMN_LON)),
                    cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_IMAGE))
                )
                placeList.add(place)
            } while (cursor.moveToNext())
        }
        cursor.close()
        return placeList
    }

    fun clearPlaces() {
        val db = this.writableDatabase
        db.delete(TABLE_NAME, null, null)
        db.close()
    }
}
