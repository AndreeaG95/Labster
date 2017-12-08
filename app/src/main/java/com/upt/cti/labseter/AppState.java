package com.upt.cti.labseter;

import com.google.firebase.database.DatabaseReference;

/**
 * Created by andreeagb on 12/8/2017.
 * This class is used for passing instances between activities.
 */

public class AppState {

    private static AppState singletonObject;
    private static String userId;

    public static synchronized AppState get() {
        if (singletonObject == null) {
            singletonObject = new AppState();
        }
        return singletonObject;
    }

    // reference to Firebase used for reading and writing data
    private DatabaseReference databaseReference;

    public DatabaseReference getDatabaseReference() {
        return databaseReference;
    }

    public void setDatabaseReference(DatabaseReference databaseReference) {
        this.databaseReference = databaseReference;
    }

    public void setUserId(String id){
        this.userId = id;
    }
}
