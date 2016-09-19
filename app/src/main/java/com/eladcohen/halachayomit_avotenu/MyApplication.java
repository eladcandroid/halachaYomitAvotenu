package com.eladcohen.halachayomit_avotenu;

import android.app.Application;
import android.content.Context;
import android.view.View;
import android.widget.ProgressBar;

/**
 * Created by elad on 2/22/15.
 */
public class MyApplication extends Application {

    private static ProgressBar searchProgressBar;
    private static ProgressBar piyutimProgressBar;
    private static ProgressBar traditionsProgressBar;
    private static ProgressBar performancesProgressBar;

    private static final String ALIGN_RIGHT_CSS = "<style>*{text-align:right;direction:rtl;}</style><body>";

    private static Context context;

    public void onCreate(){
        super.onCreate();
        MyApplication.context = getApplicationContext();
    }

    public String getAlignRightCss(){
        return ALIGN_RIGHT_CSS;
    }
    public static Context getAppContext() {
        return MyApplication.context;
    }

    public static void setSearchProgressBar(ProgressBar pb)
    {
        searchProgressBar=pb;
    }

    public static ProgressBar getSearchProgressBar()
    {
        return searchProgressBar;
    }

    public static void hideSearchProgressBar()
    {
        searchProgressBar.setVisibility(View.INVISIBLE);
    }

    public static void showSearchProgressBar()
    {
        searchProgressBar.setVisibility(View.VISIBLE);
    }

    public static void setPiyutimProgressBar(ProgressBar pb)
    {
        piyutimProgressBar=pb;
    }

    public static ProgressBar getPiyutimProgressBar()
    {
        return piyutimProgressBar;
    }

    public static void hidePiyutimProgressBar()
    {
        piyutimProgressBar.setVisibility(View.INVISIBLE);
    }

    public static void showTraditionsProgressBar()
    {
        traditionsProgressBar.setVisibility(View.VISIBLE);
    }

    public static void setTraditionsProgressBar(ProgressBar pb)
    {
        traditionsProgressBar=pb;
    }

    public static ProgressBar getTraditionsProgressBar()
    {
        return traditionsProgressBar;
    }

    public static void hideTraditionsProgressBar()
    {
        traditionsProgressBar.setVisibility(View.INVISIBLE);
    }

    public static void showpPerformancesProgressBar()
    {
        performancesProgressBar.setVisibility(View.VISIBLE);
    }

    public static void setPerformancesProgressBar(ProgressBar pb)
    {
        performancesProgressBar=pb;
    }

    public static ProgressBar getPerformancesProgressBar()
    {
        return performancesProgressBar;
    }

    public static void hidePerformancesProgressBar()
    {
        performancesProgressBar.setVisibility(View.INVISIBLE);
    }

    public static void showPerformancesProgressBar()
    {
        performancesProgressBar.setVisibility(View.VISIBLE);
    }

}