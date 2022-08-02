package com.example.app05.Controllers;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.util.Log;

import com.example.app05.Models.Location;
import com.example.app05.Models.StringsRequests;

import java.util.ArrayList;

public class LocationDAO extends DAOBase {

    public LocationDAO(Context context) {
        super(context);
    }

    public void insertLocation(Location location)
    {
        ContentValues cv = new ContentValues();
        cv.put(StringsRequests.LOCATION_ID, location.getId());
        cv.put(StringsRequests.POTEAU, location.getPoteau());
        cv.put(StringsRequests.DATE, location.getDate());
        cv.put(StringsRequests.ID_USER, location.getIdUser());
        cv.put(StringsRequests.LOGITUDE, location.getLongitude());
        cv.put(StringsRequests.LATITUDE, location.getLatitude());
        cv.put(StringsRequests.SECTION, location.getSection());
        cv.put(StringsRequests.RANG, location.getRang());
        cv.put(StringsRequests.COMPOSANT, location.getComposant());

        mDb = open();
        mDb.insert(StringsRequests.TABLE_NAME, null, cv);
        Log.d("test2", "Insertion effectue");
        mDb.close();
    }

    public Location selectLocationById(String id)
    {
        mDb = open();
        String queryString = " SELECT * FROM  "+StringsRequests.TABLE_NAME+ " WHERE " + StringsRequests.LOCATION_ID+ " =?" ;
        Cursor cursor = mDb.rawQuery(queryString, new String[]{id}) ;
        Location location = new Location();

        while (cursor.moveToNext() )
        {
            location.setId(cursor.getString(0));
            location.setPoteau(cursor.getString(1));
            location.setDate(cursor.getString(2));
            location.setIdUser(cursor.getString(3));
            location.setLongitude(cursor.getDouble(4));
            location.setLatitude(cursor.getDouble(5));
            location.setSection(cursor.getString(6));
            location.setRang(cursor.getInt(7));
            location.setComposant(cursor.getString(8));
            break;
        }
        mDb.close();
        return location;

    }

    public ArrayList<Location> selectAllLocation()
    {
        ArrayList<Location> arrayList = new ArrayList<>();
        mDb = open();
        String queryString = " SELECT * FROM " + StringsRequests.TABLE_NAME ;
        Cursor cursor = mDb.rawQuery(queryString, new String[]{}) ;



        while(cursor.moveToNext())
        {
            Location location = new Location();
            location.setId(cursor.getString(0));
            location.setPoteau(cursor.getString(1));
            location.setDate(cursor.getString(2));
            location.setIdUser(cursor.getString(3));
            location.setLongitude(cursor.getDouble(4));
            location.setLatitude(cursor.getDouble(5));
            location.setSection(cursor.getString(6));
            location.setRang(cursor.getInt(7));
            location.setComposant(cursor.getString(8));

            arrayList.add(location) ;
        }
        mDb.close();
        return arrayList;
    }

    public void deleteLocation(String id)
    {
        mDb = open() ;
        mDb.delete(StringsRequests.TABLE_NAME, StringsRequests.LOCATION_ID + " =?", new String[]{id}) ;
        mDb.close();
    }
}
