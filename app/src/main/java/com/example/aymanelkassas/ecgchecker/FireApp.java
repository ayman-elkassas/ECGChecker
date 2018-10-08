package com.example.aymanelkassas.ecgchecker;

import android.app.Application;

import com.example.aymanelkassas.ecgchecker.networking.NearByApi;
import com.firebase.client.Firebase;
import com.google.firebase.FirebaseApp;
import com.google.firebase.database.FirebaseDatabase;
import com.onesignal.OneSignal;
//import com.onesignal.OneSignal;

/**
 * Created by Ayman Elkassas on 2/21/2018.
 */

public class FireApp extends Application {

    //TODO: Thats a noSql database

    NearByApi nearByApi = null;
    static MyApplication app;

    @Override
    public void onCreate() {
        super.onCreate();

//         OneSignal Initialization
        OneSignal.startInit(this)
                .inFocusDisplaying(OneSignal.OSInFocusDisplayOption.Notification)
                .unsubscribeWhenNotificationsAreDisabled(true)
                .init();

        //TODO: old version
//        Firebase.setAndroidContext(this);
//        //TODO: New version
        if(!FirebaseApp.getApps(this).isEmpty())
        {
            FirebaseDatabase.getInstance().setPersistenceEnabled(true);
        }

    }
}
