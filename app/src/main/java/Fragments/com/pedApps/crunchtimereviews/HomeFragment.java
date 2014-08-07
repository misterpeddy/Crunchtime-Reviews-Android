package com.pedApps.crunchtimereviews;

/**
 * Created by Pedram on 7/28/2014.
 */

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;


public class HomeFragment extends Fragment {

    public HomeFragment() {
    }

    public static HomeFragment newInstance (String... arguments) {
        HomeFragment fragment = new HomeFragment();
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
        View rootView = inflater.inflate(R.layout.fragment_home, container, false);
        return rootView;
    }

}
