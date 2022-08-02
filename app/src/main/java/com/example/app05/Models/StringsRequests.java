package com.example.app05.Models;

public class StringsRequests {

    public static final String LOCATION_ID = "id";
    public static final String LATITUDE="latitude";
    public static final String LOGITUDE ="longitude";
    public static final String ID_USER ="idUser";
    public static final String DATE ="date";
    public static final String POTEAU ="poteau";
    public static final String SECTION ="section";
    public static final String RANG ="rang";
    public static final String COMPOSANT ="composant";
    public static final String TABLE_NAME="location";

    public static final String CREATE_TABLE_LOCATION =
            " CREATE TABLE "+ TABLE_NAME + " ( " +
                    LOCATION_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                    POTEAU + " TEXTE, " +
                    DATE + " TEXTE, " +
                    ID_USER + " TEXTE, " +
                    LOGITUDE + " TEXTE, " +
                    LATITUDE + " TEXTE, " +
                    SECTION + " TEXTE, " +
                    RANG + " TEXTE, " +
                    COMPOSANT + " TEXTE); " ;

    public static final String DROP_TABLE_LOCATION =
            " DROP TABLE IF EXISTS " + TABLE_NAME + ";" ;
}
