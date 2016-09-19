package com.eladcohen.halachayomit_avotenu;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;

/**
 * Created by elad on 4/28/15.
 */
public class HalachotFragment extends Fragment {
    public ListView lvHalachot;
    public HalachotFragment() {

    }
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_halachot, container, false);
//        TextView dummyTextView = (TextView) rootView.findViewById(R.id.section_label);
//        dummyTextView.setText(Integer.toString(getArguments().getInt(ARG_SECTION_NUMBER)));
        return rootView;
    }
    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        // TODO Auto-generated method stub
        super.onViewCreated(view, savedInstanceState);
        final Context context = getActivity();
        lvHalachot = (ListView) getView().findViewById(R.id.lvHalachot);
        lvHalachot.setOnItemClickListener(new AdapterView.OnItemClickListener() {

                    @Override
                    public void onItemClick(AdapterView<?> parent, View view,
                                            int position, long id) {
                        // getting values from selected ListItem
                        String halachaTitle = ((TextView) view.findViewById(R.id.title)).getText()
                                .toString();
                        String halachaContent = ((TextView) view.findViewById(R.id.content)).getText()
                                .toString();
                        String halachaImage = ((TextView) view.findViewById(R.id.image)).getText()
                                .toString();
//                        Log.d("ELAD", halachaId);
//                        Toast.makeText(context,halachaId,Toast.LENGTH_LONG).show();

                        AlertDialog.Builder alert = new AlertDialog.Builder(context);
//                        alert.setTitle(halachaTitle);

                        LayoutInflater factory = LayoutInflater.from(context);
                        final View alertView = factory.inflate(R.layout.alert_imageview, null);
                        alert.setView(alertView);
                        ProgressBar pbAlert = (ProgressBar) alertView.findViewById(R.id.pbAlert);

                        if (halachaImage==null || halachaImage.isEmpty()) {
                            WebView wvAlert = (WebView) alertView.findViewById(R.id.wvAlert);
                            wvAlert.getSettings().setJavaScriptEnabled(true);
                            wvAlert.loadDataWithBaseURL("", ((MyApplication) getActivity().getApplication()).getAlignRightCss() + halachaContent, "text/html", "UTF-8", null);
                            wvAlert.setBackgroundColor(Color.TRANSPARENT);
                            pbAlert.setVisibility(View.GONE);
                            wvAlert.setVisibility(View.VISIBLE);
//                            alert.setView(wvAlert);
                        }
                        else {

//                            ImageView iv = new ImageView(context);
//                            iv.setVisibility(View.VISIBLE);
//                            iv.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
                            ImageView ivAlert = (ImageView) alertView.findViewById(R.id.ivAlert);
                            new DownloadImage(ivAlert, pbAlert)
                                    .execute(halachaImage);
//                            alert.setView(iv);
                        }

//                        wv.loadUrl("http:\\www.google.com");
//                        wv.setWebViewClient(new WebViewClient() {
//                            @Override
//                            public boolean shouldOverrideUrlLoading(WebView view, String url) {
//                                view.loadUrl(url);
//
//                                return true;
//                            }
//                        });


                        alert.setNegativeButton("סגור", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int id) {
                                dialog.dismiss();
                            }
                        });
                        alert.show();

                        // Starting new intent
//                        Intent in = new Intent(getApplicationContext(),
//                                EditProductActivity.class);
                        // sending pid to next activity
//                        in.putExtra(TAG_ID, 1);
//
//                        // starting new activity and expecting some response back
//                        startActivityForResult(in, 100);
                    }
                });
    }
}
