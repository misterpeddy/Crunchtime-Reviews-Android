package com.pedApps.crunchtimereviews;

import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import net.simonvt.menudrawer.MenuDrawer;

import org.json.JSONException;
import org.json.JSONObject;


public class SessionAttendFragment extends Fragment {

    public static final String COURSE_NAME = "Course";
    public static final String SESSION_ID = "Session ID";
    public static final String DATE = "Date";
    public static final String TIME = "Time";
    public static final String LOCATION = "Location";

    public SessionAttendFragment() {
    }


    public static SessionAttendFragment newInstance(Bundle args) {
        SessionAttendFragment fragment = new SessionAttendFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_attend, container, false);
        return rootView;
    }


}
