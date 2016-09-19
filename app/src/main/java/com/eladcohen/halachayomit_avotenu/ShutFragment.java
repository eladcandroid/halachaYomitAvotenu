package com.eladcohen.halachayomit_avotenu;

import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import com.eladcohen.halachayomit_avotenu.ServerUtilities;
import android.widget.ListView;
import android.widget.Toast;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by elad on 4/28/15.
 */
public class ShutFragment extends Fragment {
    private boolean errFlag = false;
    public Button btnShutSend;
    public EditText etShutName, etShutEmail, etShutQuestion;
    public ShutFragment() {

    }
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_shut, container, false);
//        TextView dummyTextView = (TextView) rootView.findViewById(R.id.section_label);
//        dummyTextView.setText(Integer.toString(getArguments().getInt(ARG_SECTION_NUMBER)));
        return rootView;
    }
    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        // TODO Auto-generated method stub
        super.onViewCreated(view, savedInstanceState);
        etShutEmail = (EditText) getView().findViewById(R.id.etShutEmail);
        etShutName = (EditText) getView().findViewById(R.id.etShutName);
        etShutQuestion = (EditText) getView().findViewById(R.id.etShutQuestion);

        btnShutSend = (Button) getView().findViewById(R.id.btnShutSend);
        btnShutSend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View arg0) {
                errFlag = false;
                final String shutEmail = etShutEmail.getText().toString();
                if(shutEmail.trim().equals(""))
                {
                    etShutEmail.setError(getResources().getString(R.string.err_shut_email));
                    errFlag = true;
                }
                else if (!CommonUtilities.isEmailValid(shutEmail)){
                    etShutEmail.setError(getResources().getString(R.string.err_shut_email_struct));
                    errFlag = true;
                }
                final String shutName = etShutName.getText().toString();
//                if(shutName.trim().equals(""))
//                {
//                    etShutName.setError( "חובה להכניס שם" );
//                }
                final String shutQuestion = etShutQuestion.getText().toString();
                if(shutQuestion.trim().equals(""))
                {
                    etShutQuestion.setError(getResources().getString(R.string.err_shut_question));
                    errFlag = true;
                }
                if (errFlag) {
                    Toast.makeText(getActivity(),getResources().getString(R.string.msg_shut_sent_err),Toast.LENGTH_SHORT).show();
                }
                else {
                    new AsyncTask<Void, Integer, Boolean>() {
                        @Override
                        protected Boolean doInBackground(Void... arg0) {
                            ServerUtilities.shutAsk(getActivity(), shutName, shutEmail, shutQuestion);
                            return true;
                        }

                        protected void onPostExecute(Boolean result) {
                            etShutEmail.setText("");
                            etShutName.setText("");
                            etShutQuestion.setText("");
                            Toast.makeText(getActivity(), getResources().getString(R.string.msg_shut_sent), Toast.LENGTH_LONG).show();
                        }
                    }.execute();
                }

            }
        });
    }


}
