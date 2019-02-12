package com.iitbhilai.bus;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.util.Log;

public class ServiceManager {
    Context context;
    NetworkInfo networkInfo = null;

    public ServiceManager( Context base) {
        context = base;
    }

    public boolean isNetworkAvailable() {
        ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        try {
            networkInfo = cm.getActiveNetworkInfo();
        } catch (NullPointerException e){
            Log.v("Service Manager: ", ""+e);
        }
        return networkInfo != null && networkInfo.isConnected();
    }
}
