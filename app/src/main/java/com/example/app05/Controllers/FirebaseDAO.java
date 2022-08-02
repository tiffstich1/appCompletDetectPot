package com.example.app05.Controllers;

import com.google.firebase.auth.FirebaseAuth;

public class FirebaseDAO {


    public String getUserUID()
    {
        FirebaseAuth mAuth = FirebaseAuth.getInstance();
        return mAuth.getCurrentUser().getUid();

    }
}
