package com.example.app05.Controllers;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;

public abstract class DAOBase {

    protected final static int version = 5;
    protected final static String NOM = "location.db";
    protected SQLiteDatabase mDb= null;
    protected DatabaseHandler mdatabaseHandel =null;

    public DAOBase(Context context)
    {
        mdatabaseHandel = new DatabaseHandler(context,NOM,null,version);
    }

    public SQLiteDatabase open()
    {
        mDb = mdatabaseHandel.getWritableDatabase();
        return mDb;
    }

    public void close()
    {
        mDb.close();
    }
}
