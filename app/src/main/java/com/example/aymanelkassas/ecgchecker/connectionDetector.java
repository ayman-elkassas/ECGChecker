package com.example.aymanelkassas.ecgchecker;

import android.app.Service;
import android.content.Context;
import android.content.ServiceConnection;
import android.net.ConnectivityManager;
import android.net.Network;
import android.net.NetworkInfo;

/**
 * Created by Ayman Elkassas on 3/16/2018.
 */

public class connectionDetector {

    Context context;
    ConnectivityManager connectivity;

    public connectionDetector(Context context) {
        this.context = context;
    }

    public boolean isConnected()
    {
        //TODO:GET Connectivity object
        connectivity= (ConnectivityManager) context.getSystemService(Service.CONNECTIVITY_SERVICE);
        if(connectivity!=null)
        {
            //TODO:GET Info of connection
            NetworkInfo info=connectivity.getActiveNetworkInfo();
            if(info!=null)
            {
                if(info.getState()==NetworkInfo.State.CONNECTED)
                {
                    return true;
                }
            }
        }

        return false;
    }
}
