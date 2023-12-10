package com.example.crawler.util;

import android.app.Activity;
import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkCapabilities;
import android.util.Log;

import java.util.Arrays;

public class util {
    private util(){}
    public static boolean isNetworkAvailable(Activity activity)
    {
        Context context = activity.getApplicationContext();
        ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        if(cm==null)
            return false;
        NetworkCapabilities capabilities = cm.getNetworkCapabilities(cm.getActiveNetwork());
        if(capabilities == null)
            return false;
        return capabilities.hasCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET);
    }
    public static int ChangeTimeToSec(String str)
    {
        String[] AllTime=str.trim().split("\\s+");//month day year
        AllTime[1]=AllTime[1].substring(0,AllTime[1].length()-1);
//        for(String temp: Arrays.asList(AllTime))
//            Log.i("Test",temp);
        int month=0;
        String[] MonthString={"Jan","Feb","Mar","Apr","May","Jun","Jul","Aug","Sep","Oct","Nov","Dec"};
        for(int i=0;i<MonthString.length;++i)
            if(MonthString[i]==AllTime[0]) {
                month = i + 1;
                break;
            }
        int secTime=Integer.parseInt(AllTime[1])+(month*31)+(Integer.parseInt(AllTime[2])-2023)*365;
        return secTime;
    }
}
