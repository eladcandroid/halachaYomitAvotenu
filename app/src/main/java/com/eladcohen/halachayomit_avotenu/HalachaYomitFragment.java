package com.eladcohen.halachayomit_avotenu;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView;
import android.widget.ImageView;
import android.widget.TextView;

/**
 * Created by elad on 4/28/15.
 */
public class HalachaYomitFragment extends Fragment {
    public TextView tvTitle;
    public WebView wvContent;
    public ImageView ivImage;
    public HalachaYomitFragment() {

    }
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_halachayomit, container, false);
//        TextView dummyTextView = (TextView) rootView.findViewById(R.id.section_label);
//        dummyTextView.setText(Integer.toString(getArguments().getInt(ARG_SECTION_NUMBER)));
        setRetainInstance(true);
        return rootView;
    }
    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        // TODO Auto-generated method stub
        super.onViewCreated(view, savedInstanceState);

            tvTitle = (TextView) getView().findViewById(R.id.tvTitle);
            wvContent = (WebView) getView().findViewById(R.id.wvContent);
            ivImage = (ImageView) getView().findViewById(R.id.ivImage);
            setRetainInstance(true);
        }
}
