package com.eladcohen.halachayomit_avotenu;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.atomic.AtomicInteger;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.graphics.Color;
import android.os.AsyncTask;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.app.ActionBar;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.app.FragmentPagerAdapter;
import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.gcm.GoogleCloudMessaging;

import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;


public class MainActivity extends ActionBarActivity implements ActionBar.TabListener {
    JSONParser jParser = new JSONParser();
    // Progress Dialog
    private ProgressDialog pDialog;
    // JSON Node names
    private static final String TAG_SUCCESS = "success";
    private static final String TAG_HALACHOT = "halachot";

    private static final String TAG_ID = "id";
    private static final String TAG_TITLE = "title";
    private static final String TAG_CONTENT = "content";
    private static final String TAG_IMAGE = "image";

    public static final String PREFS_NAME = "halachaYomitPrefs";
    private static final String HALACHAYOMIT_JSON_PREF = "halachaYomitJsonPref";
    private static final String HALACHOT_JSON_PREF = "halachotJsonPref";

    private static String jsonUrl = "";
    private static ListView lvHalachot;
    private static TextView tvTitle;
    private static WebView wvContent;

    private static HalachaYomitFragment halachaYomitFragment;
    private static HalachotFragment halachotFragment;
    private static ShutFragment shutFragment;

    private static int currentPosition = 0;

    private enum Activities {
        about,contact;
    }
//    private ImageView mImageView;

    // products JSONArray
    JSONArray halachot = null;

    /**
     * The {@link android.support.v4.view.PagerAdapter} that will provide
     * fragments for each of the sections. We use a
     * {@link FragmentPagerAdapter} derivative, which will keep every
     * loaded fragment in memory. If this becomes too memory intensive, it
     * may be best to switch to a
     * {@link android.support.v4.app.FragmentStatePagerAdapter}.
     */
    SectionsPagerAdapter mSectionsPagerAdapter;
    ArrayList<HashMap<String, String>> halachotList;
    /**
     * The {@link ViewPager} that will host the section contents.
     */
    static ViewPager mViewPager;

    public static final String EXTRA_MESSAGE = "message";
    public static final String PROPERTY_REG_ID = "registration_id";
    private static final String PROPERTY_APP_VERSION = "appVersion";
    private final static int PLAY_SERVICES_RESOLUTION_REQUEST = 9000;

    /**
     * Tag used on log messages.
     */
    static final String TAG = "GCMDemo";

    TextView mDisplay;
    GoogleCloudMessaging gcm;
    AtomicInteger msgId = new AtomicInteger();
    SharedPreferences prefs;
    Context context;

    String regid;
    /**
     * Substitute you own sender ID here. This is the project number you got
     * from the API Console, as described in "Getting Started."
     */
    String SENDER_ID = "434714569811";
    /**
     * Sends the registration ID to your server over HTTP, so it can use GCM/HTTP
     * or CCS to send messages to your app. Not needed for this demo since the
     * device sends upstream messages to a server that echoes back the message
     * using the 'from' address in the message.
     */
    private void sendRegistrationIdToBackend() {
        ServerUtilities.register(context, "", "", regid);
    }
    /**
     * Stores the registration ID and app versionCode in the application's
     * {@code SharedPreferences}.
     *
     * @param context application's context.
     * @param regId registration ID
     */
    private void storeRegistrationId(Context context, String regId) {
        final SharedPreferences prefs = getGCMPreferences(context);
        int appVersion = getAppVersion(context);
        Log.i(TAG, "Saving regId on app version " + appVersion);
        SharedPreferences.Editor editor = prefs.edit();
        editor.putString(PROPERTY_REG_ID, regId);
        editor.putInt(PROPERTY_APP_VERSION, appVersion);
        editor.commit();
    }
    /**
     * Registers the application with GCM servers asynchronously.
     * <p>
     * Stores the registration ID and app versionCode in the application's
     * shared preferences.
     */
    private void registerInBackground() {
        new AsyncTask<Void, Void, String>() {

            @Override
            protected String doInBackground(Void... params) {
                String msg = "";
                try {
                    if (gcm == null) {
                        gcm = GoogleCloudMessaging.getInstance(context);
                    }
                    regid = gcm.register(SENDER_ID);
                    msg = "Device registered, registration ID=" + regid;

                    // You should send the registration ID to your server over HTTP,
                    // so it can use GCM/HTTP or CCS to send messages to your app.
                    // The request to your server should be authenticated if your app
                    // is using accounts.
                    sendRegistrationIdToBackend();

                    // For this demo: we don't need to send it because the device
                    // will send upstream messages to a server that echo back the
                    // message using the 'from' address in the message.

                    // Persist the registration ID - no need to register again.
                    storeRegistrationId(context, regid);
                } catch (IOException ex) {
                    msg = "Error :" + ex.getMessage();
                    // If there is an error, don't just keep trying to register.
                    // Require the user to click a button again, or perform
                    // exponential back-off.
                }
                return msg;
            }

            @Override
            protected void onPostExecute(String msg) {
//                mDisplay.append(msg + "\n");
            }
        }.execute(null, null, null);
    }
    /**
     * @return Application's version code from the {@code PackageManager}.
     */
    private static int getAppVersion(Context context) {
        try {
            PackageInfo packageInfo = context.getPackageManager()
                    .getPackageInfo(context.getPackageName(), 0);
            return packageInfo.versionCode;
        } catch (PackageManager.NameNotFoundException e) {
            // should never happen
            throw new RuntimeException("Could not get package name: " + e);
        }
    }
    /**
     * Gets the current registration ID for application on GCM service.
     * <p>
     * If result is empty, the app needs to register.
     *
     * @return registration ID, or empty string if there is no existing
     *         registration ID.
     */
    private String getRegistrationId(Context context) {
        final SharedPreferences prefs = getGCMPreferences(context);
        String registrationId = prefs.getString(PROPERTY_REG_ID, "");
        if (registrationId.isEmpty()) {
            Log.i(TAG, "Registration not found.");
            return "";
        }
        // Check if app was updated; if so, it must clear the registration ID
        // since the existing registration ID is not guaranteed to work with
        // the new app version.
        int registeredVersion = prefs.getInt(PROPERTY_APP_VERSION, Integer.MIN_VALUE);
        int currentVersion = getAppVersion(context);
        if (registeredVersion != currentVersion) {
            Log.i(TAG, "App version changed.");
            return "";
        }
        return registrationId;
    }
    /**
     * @return Application's {@code SharedPreferences}.
     */
    private SharedPreferences getGCMPreferences(Context context) {
        // This sample app persists the registration ID in shared preferences, but
        // how you store the registration ID in your app is up to you.
        return getSharedPreferences(MainActivity.class.getSimpleName(),
                Context.MODE_PRIVATE);
    }
    /**
     * Check the device to make sure it has the Google Play Services APK. If 
     * it doesn't, display a dialog that allows users to download the APK from 
     * the Google Play Store or enable it in the device's system settings. 
     */
    private boolean checkPlayServices() {
        int resultCode = GooglePlayServicesUtil.isGooglePlayServicesAvailable(this);
        if (resultCode != ConnectionResult.SUCCESS) {
            if (GooglePlayServicesUtil.isUserRecoverableError(resultCode)) {
                GooglePlayServicesUtil.getErrorDialog(resultCode, this,
                        PLAY_SERVICES_RESOLUTION_REQUEST).show();
            } else {
                Log.i("ELAD", "This device is not supported.");
                finish();
            }
            return false;
        }
        return true;
    }
    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        new LoadDataTask(currentPosition).execute();
    }
    @Override
    protected void onResume() {
        super.onResume();
        context = getApplicationContext();
        if (checkPlayServices()) {
            gcm = GoogleCloudMessaging.getInstance(this);
            regid = getRegistrationId(context);

            if (regid!=null && regid.length()<=0) {
                registerInBackground();
            }
        } else {
            Log.i(TAG, "No valid Google Play Services APK found.");
        }
//        new LoadDataTask(currentPosition).execute();
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        currentPosition = 0;

        // Set up the action bar.
        final ActionBar actionBar = getSupportActionBar();
        actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_TABS);
//        actionBar.setBackgroundDrawable(new ColorDrawable(Color.RED));

        // Create the adapter that will return a fragment for each of the three
        // primary sections of the activity.
        mSectionsPagerAdapter = new SectionsPagerAdapter(getSupportFragmentManager());

        // Set up the ViewPager with the sections adapter.
        mViewPager = (ViewPager) findViewById(R.id.pager);
        mViewPager.setAdapter(mSectionsPagerAdapter);
        // When swiping between different sections, select the corresponding
        // tab. We can also use ActionBar.Tab#select() to do this if we have
        // a reference to the Tab.

//        if (savedInstanceState == null)
//            new LoadDataTask(currentPosition).execute();
//        else if (savedInstanceState.getInt("currentPosition")!=0)
            new LoadDataTask(currentPosition).execute();

        mViewPager.setOnPageChangeListener(new ViewPager.SimpleOnPageChangeListener() {
            @Override
            public void onPageSelected(int position) {
                if (position == 0 || position == 1) {
                    new LoadDataTask(position).execute();
                }
                else if (position == 2) {

                }

//                lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
//
//                    @Override
//                    public void onItemClick(AdapterView<?> parent, View view,
//                                            int position, long id) {
//                        // getting values from selected ListItem
////                        String pid = ((TextView) view.findViewById(R.id.pid)).getText()
////                                .toString();
//
//                        // Starting new intent
////                        Intent in = new Intent(getApplicationContext(),
////                                EditProductActivity.class);
//                        // sending pid to next activity
////                        in.putExtra(TAG_ID, 1);
////
////                        // starting new activity and expecting some response back
////                        startActivityForResult(in, 100);
//                    }
//                });
//                }
//                else if (position == 1) {
//
//                    new LoadDataTask(position).execute();
//                }
                currentPosition = position;
                invalidateOptionsMenu();
                actionBar.setSelectedNavigationItem(position);
            }
        });

        // For each of the sections in the app, add a tab to the action bar.
        for (int i = 0; i < mSectionsPagerAdapter.getCount(); i++) {
            // Create a tab with text corresponding to the page title defined by
            // the adapter. Also specify this Activity object, which implements
            // the TabListener interface, as the callback (listener) for when
            // this tab is selected.
            actionBar.addTab(
                    actionBar.newTab()
                            .setText(mSectionsPagerAdapter.getPageTitle(i))
                            .setTabListener(this));
        }
//        mViewPager.setCurrentItem(1);
//        mViewPager.refreshDrawableState();
//        mViewPager.setCurrentItem(0);
//        mViewPager.refreshDrawableState();
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        MenuItem refreshItem = (MenuItem) menu.findItem(R.id.action_refresh);
        if (currentPosition == 2)
            refreshItem.setVisible(false);
        else
            refreshItem.setVisible(true);

        invalidateOptionsMenu();

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
//        if (id == R.id.action_settings) {
//            return true;
//        }
        if (id == R.id.action_refresh) {
            new LoadDataTask(currentPosition).execute();
        }
        else if (id == R.id.action_about) {
            loadActivity("about");
        }
        else if (id == R.id.action_contact) {
            loadActivity("contact");
        }

        return super.onOptionsItemSelected(item);
    }

    public void loadActivity(String activityName) {
        Intent intent = null;
        Activities activities  = Activities.valueOf(activityName);
        switch (activities){
            case about:
                intent = new Intent(this, About.class);
                break;
            case contact:
                intent = new Intent(this, FormActivity.class);
                break;
        }
        if (intent != null) startActivity(intent);
    }

    @Override
    public void onTabSelected(ActionBar.Tab tab, FragmentTransaction fragmentTransaction) {
        // When the given tab is selected, switch to the corresponding page in
        // the ViewPager.
        mViewPager.setCurrentItem(tab.getPosition());
    }

    @Override
    public void onTabUnselected(ActionBar.Tab tab, FragmentTransaction fragmentTransaction) {
    }

    @Override
    public void onTabReselected(ActionBar.Tab tab, FragmentTransaction fragmentTransaction) {
    }

    /**
     * A {@link FragmentPagerAdapter} that returns a fragment corresponding to
     * one of the sections/tabs/pages.
     */
    public class SectionsPagerAdapter extends FragmentPagerAdapter {

        public SectionsPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            Fragment fragment = null;
            // getItem is called to instantiate the fragment for the given page.
            // Return a PlaceholderFragment (defined as a static inner class below).
            switch (position) {
                case 0:
                    halachaYomitFragment = new HalachaYomitFragment();
                    fragment = halachaYomitFragment;
                    break;
//                    Bundle args = new Bundle();
//                    args.putInt(fragment.ARG_SECTION_NUMBER, position + 1);
//                    fragment.setArguments(args);
                case 1:
//                default:
                    halachotFragment = new HalachotFragment();
                    fragment = halachotFragment;
//                    Bundle args = new Bundle();
//                    args.putInt(fragment.ARG_SECTION_NUMBER, position + 1);
//                    fragment.setArguments(args);
                    break;
                case 2:
                    shutFragment = new ShutFragment();
                    fragment = shutFragment;

//                return PlaceholderFragment.newInstance(position + 1);
            }
            return fragment;
        }

        @Override
        public int getCount() {
            return 3;
        }

        @Override
        public CharSequence getPageTitle(int position) {
            Locale l = Locale.getDefault();
            switch (position) {
                case 0:
                    return getString(R.string.title_section1).toUpperCase(l);
                case 1:
                    return getString(R.string.title_section2).toUpperCase(l);
                case 2:
                    return getString(R.string.title_section3).toUpperCase(l);
            }
            return null;
        }
    }

//    /**
//     * A placeholder fragment containing a simple view.
//     */
//    public static class PlaceholderFragment extends Fragment {
//        /**
//         * The fragment argument representing the section number for this
//         * fragment.
//         */
//        private static final String ARG_SECTION_NUMBER = "section_number";
//
//        /**
//         * Returns a new instance of this fragment for the given section
//         * number.
//         */
//        public static PlaceholderFragment newInstance(int sectionNumber) {
//            PlaceholderFragment fragment = new PlaceholderFragment();
//            Bundle args = new Bundle();
//            args.putInt(ARG_SECTION_NUMBER, sectionNumber);
//            fragment.setArguments(args);
//            return fragment;
//        }
//
//        public PlaceholderFragment() {
//        }
//
//        @Override
//        public View onCreateView(LayoutInflater inflater, ViewGroup container,
//                                 Bundle savedInstanceState) {
//            View rootView = null;
//            if (getArguments().getInt(ARG_SECTION_NUMBER)==1) {
//                rootView = inflater.inflate(R.layout.fragment_halachayomit, container, false);
//                tvTitle = (TextView) rootView.findViewById(R.id.tvTitle);
//                wvContent = (WebView) rootView.findViewById(R.id.wvContent);
//            }
//            else if (getArguments().getInt(ARG_SECTION_NUMBER)==2) {
//                rootView = inflater.inflate(R.layout.fragment_halachot, container, false);
//                lvHalachot = (ListView) rootView.findViewById(R.id.lvHalachot);
//            }
//            else {
//                Log.d("ELAD 1: ",getArguments().getInt(ARG_SECTION_NUMBER) + "");
//                rootView = inflater.inflate(R.layout.fragment_halachayomit, container, false);
//            }
//            return rootView;
//        }
//    }

    /**
     * Background Async Task to Load all product by making HTTP Request
     * */
    class LoadDataTask extends AsyncTask<String, String, String> {
        String contentTest = "";
        int mPosition = 0;
        public LoadDataTask(int position) {
            super();
            mPosition = position;
        }
        /**
         * Before starting background thread Show Progress Dialog
         * */
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            pDialog = new ProgressDialog(MainActivity.this);
            if (mPosition == 0)
                pDialog.setMessage(getResources().getString(R.string.loading_halacha_yomit));
            else if (mPosition == 1)
                pDialog.setMessage(getResources().getString(R.string.loading_halachot));
            pDialog.setIndeterminate(false);
            pDialog.setCancelable(false);
            pDialog.show();
        }

        /**
         * getting All products from url
         * */
        protected String doInBackground(String... args) {
            // Building Parameters
            List<NameValuePair> params = new ArrayList<NameValuePair>();
            // getting JSON string from URL
            if (mPosition == 0)
            {
                jsonUrl = getResources().getString(R.string.halachayomit_json_url);
            }
            else
            {
                jsonUrl = getResources().getString(R.string.halachot_json_url);
            }
            JSONObject json = null;
            try {
                json = jParser.makeHttpRequest(jsonUrl, "GET", params);
            } catch (Exception e) {
                e.printStackTrace();
            }

            if (json == null) {
                try {
                    if (mPosition == 0)
                        json = new JSONObject(getHalachaYomitJsonStrPref());
                    else
                        json = new JSONObject(getHalachotJsonStrPref());
                } catch (JSONException e) {

                }
            }
            // Check your log cat for JSON reponse
//            Log.d("All Halachot: ", json.toString());

            if (json == null) {
                return null;
            }

            try {
                // Checking for SUCCESS TAG
                int success = json.getInt(TAG_SUCCESS);

                if (success == 1) {
                    if (mPosition == 0)
                        setHalachaYomitJsonStrPref(json.toString());
                    else
                        setHalachotJsonStrPref(json.toString());
                    // products found
                    // Getting Array of Products
                    halachot = json.getJSONArray(TAG_HALACHOT);
                    halachotList = new ArrayList<HashMap<String, String>>();

                    // looping through All Products
                    for (int i = 0; i < halachot.length(); i++) {
                        JSONObject c = halachot.getJSONObject(i);

                        // Storing each json item in variable
                        String id = c.getString(TAG_ID);
                        String title = c.getString(TAG_TITLE);
                        String content = c.getString(TAG_CONTENT);
                        String image = c.getString(TAG_IMAGE);

                        content = content.replaceAll("img src=\"fileman","img src=\"http://www.mogo.co.il/api/halachayomit/fileman");

                        // creating new HashMap
                        HashMap<String, String> map = new HashMap<String, String>();

                        // adding each child node to HashMap key => value
                        map.put(TAG_ID, id);
                        map.put(TAG_TITLE, title);
                        map.put(TAG_CONTENT, content);
                        map.put(TAG_IMAGE, image);

                        // adding HashList to ArrayList
                        halachotList.add(map);
                    }
                } else {
                    // no products found

                }
            } catch (JSONException e) {
                e.printStackTrace();
            }

            return null;
        }
        /**
         * After completing background task Dismiss the progress dialog
         * **/
        @SuppressLint("JavascriptInterface")
         protected void onPostExecute(String file_url) {
            // dismiss the dialog after getting all products
            pDialog.dismiss();
            // updating UI from Background Thread
            if (halachotList==null || halachotList.isEmpty())
            {
                Toast toast = Toast.makeText(getApplicationContext(), R.string.empty_list_error,Toast.LENGTH_LONG);

                toast.setGravity(Gravity.CENTER_HORIZONTAL | Gravity.CENTER_VERTICAL, 0, 0);
                toast.setDuration(Toast.LENGTH_LONG);
//                toast.setView(lay);
                toast.show();
            }
            else {
                if (mPosition == 0) {
                    String title = halachotList.get(0).get(TAG_TITLE);
//                TextView tvTitle = (TextView) findViewById(R.id.tvTitle);
                    halachaYomitFragment.tvTitle.setText(title);
                    String image = halachotList.get(0).get(TAG_IMAGE);

                    if (image == null || image.isEmpty()) {
                        String content = halachotList.get(0).get(TAG_CONTENT);
//                WebView wvContent = (WebView) findViewById(R.id.wvContent);
                        halachaYomitFragment.wvContent.setVisibility(View.VISIBLE);
                        halachaYomitFragment.wvContent.getSettings().setJavaScriptEnabled(true);
                        halachaYomitFragment.wvContent.loadDataWithBaseURL("", ((MyApplication) getApplication()).getAlignRightCss() + content, "text/html", "UTF-8", "");
                        halachaYomitFragment.wvContent.setBackgroundColor(Color.TRANSPARENT);
                        halachaYomitFragment.wvContent.getSettings().setLayoutAlgorithm(WebSettings.LayoutAlgorithm.SINGLE_COLUMN);
//                    halachaYomitFragment.wvContent.getSettings().setUseWideViewPort(true);

                    } else {

                        halachaYomitFragment.ivImage.setVisibility(View.VISIBLE);
//                mImageView = halachaYomitFragment.ivImage;
                        new DownloadImage(halachaYomitFragment.ivImage, null)
                                .execute(image);
                    }

                }
//                mViewPager.refreshDrawableState();
                else {
                    SimpleAdapter adapter = new SimpleAdapter(
                            MainActivity.this, halachotList,
                            R.layout.list_item, new String[]{TAG_ID,
                            TAG_TITLE, TAG_CONTENT, TAG_IMAGE},
                            new int[]{R.id.id, R.id.title, R.id.content, R.id.image});
                    // updating listview
                    halachotFragment.lvHalachot.setAdapter(adapter);
                    adapter.notifyDataSetChanged();
//                adapter.notifyDataSetChanged();

                }
            }

        }

    }


    public String getPreferenceValue(String key)
    {
        SharedPreferences sp = getSharedPreferences(PREFS_NAME,0);
        String str = sp.getString(key,"");
        return str;
    }

    public void writeToPreference(String key, String val)
    {
        SharedPreferences.Editor editor = getSharedPreferences(PREFS_NAME,0).edit();
        editor.putString(key, val);
        editor.commit();
    }
    public String getHalachaYomitJsonStrPref(){
        return getPreferenceValue(HALACHAYOMIT_JSON_PREF);
    }
    public void setHalachaYomitJsonStrPref(String jsonStr){
        writeToPreference(HALACHAYOMIT_JSON_PREF,jsonStr);
    }
    public String getHalachotJsonStrPref(){
        return getPreferenceValue(HALACHOT_JSON_PREF);
    }
    public void setHalachotJsonStrPref(String jsonStr){
        writeToPreference(HALACHOT_JSON_PREF,jsonStr);
    }
}
