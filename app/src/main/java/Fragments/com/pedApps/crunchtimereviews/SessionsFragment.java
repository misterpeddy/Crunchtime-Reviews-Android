package com.pedApps.crunchtimereviews;

/**
 * Created by Pedram on 7/28/2014.
 */

import android.app.AlertDialog;
import android.content.Context;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;


public class SessionsFragment extends Fragment {
    public static String URL_GET_SESSIONS_ALL = AppBrain.CRUNCHTIME_ROOT_FOLDER_URL +  "DB_Request.php?database=crunchtime_reviews&query=SELECT%20*%20FROM%20sessions";
    public static final String SESSION_ATTEND_FRAGMENT_NAME = "SessionAttend";
    public SessionsFragment() {
    }

    public static SessionsFragment newInstance (String... arguments) {
        SessionsFragment fragment = new SessionsFragment();
        Bundle args = new Bundle();

        if (arguments != null) {
            for (String arg : arguments) {
                String key = arg.substring(0, arg.indexOf(':'));
                String value = arg.substring(arg.indexOf(':') + 1);
                args.putString(key, value);
            }
            fragment.setArguments(args);
        }
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        getJSON();

        View rootView = inflater.inflate(R.layout.fragment_session, container, false);
        return rootView;
    }

    public void getJSON() {
        JSONDataRetriever jsonFromUrl = new JSONSessionListRetriever(null);
        jsonFromUrl.execute(URL_GET_SESSIONS_ALL);
    }



    public class JSONSessionListRetriever extends JSONDataRetriever {
        AlertDialog alertDialog;
        public JSONSessionListRetriever(ArrayList<String> fields) {
            super(fields);
        }

        @Override
        protected void onPreExecute() {
            setUpProgressDialog();
        }

        public void setUpProgressDialog() {
            Context mContext = getActivity();
            LayoutInflater inflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            View layout = inflater.inflate(R.layout.progress_dialog,
                    (ViewGroup) getActivity().findViewById(R.id.layout_root));

            TextView text = (TextView) layout.findViewById(R.id.text);
            text.setText("Loading Sessions");
            ImageView image = (ImageView) layout.findViewById(R.id.image);
            image.setImageResource(R.drawable.loader);

            AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
            builder.setView(layout);
            alertDialog = builder.create();
            alertDialog.show();
        }

        @Override
        protected void onPostExecute(JSONArray jArray) {
            if (alertDialog.isShowing()) {
                alertDialog.dismiss();
            }

            final ArrayList<JSONObject> dataList = new ArrayList<JSONObject>();
            try {
                for (int i = 0; i < jArray.length(); i++) {
                        JSONObject json_data = jArray.getJSONObject(i);
                        dataList.add(json_data);
                }
            }
            catch (Exception e) {
                Log.e("log_tag", "Error getting fields " + e.toString());
            }


            final ListView sessionList = (ListView) getActivity().findViewById(R.id.sessions);
            SessionListViewAdapter sessionListViewAdapter = new SessionListViewAdapter(getActivity(), dataList);
            sessionList.setAdapter(sessionListViewAdapter);
            sessionList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    JSONObject jsonObject = ((SessionListViewAdapter)sessionList.getAdapter()).jsonObject;
                    Bundle passOn = new Bundle();
                    Item sessionAttendItem = new Item(SessionsFragment.SESSION_ATTEND_FRAGMENT_NAME, R.id.abs__icon);
                    try {
                        passOn.putString(SessionAttendFragment.COURSE_NAME, (String) jsonObject.get("Course"));
                    } catch (JSONException e) {
                        Log.e("JSON", "failed");
                    }
                    ((FragmentSample)getActivity()).onSubMenuItemClicked(0, sessionAttendItem, passOn);

                }
            });

        }
    }



}
