package com.juniorenriquegonzalez.whatsapp.config;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

public class ConfiguracionFirebase {
    private static DatabaseReference database;
    private static FirebaseAuth auth;
    private static StorageReference storage;

    //Retorna la instancia de FirebaseDatabase

    public static DatabaseReference getFirebaseDatabase() {

        if ( database == null ) {
            database = FirebaseDatabase.getInstance().getReference();
        }
        return database;
    }

    //Retorna la Instancia de FirebaseAuth

    public static FirebaseAuth getFirebaseAutenticacion() {

        if ( auth == null ) {
            auth = FirebaseAuth.getInstance();
        }

        return auth;

    }

    public static StorageReference getFirebaseStorage() {
         if ( storage == null )  {

             storage = FirebaseStorage.getInstance().getReference();
         }

         return storage;
    }

}
