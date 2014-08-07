package com.pedApps.crunchtimereviews;

/**
 * Created by Pedram on 7/28/2014.
 */

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.Toast;


public class GalleryFragment extends Fragment {

    public static final String GALLERY_LIST_FRAGMENT_NAME = "GalleryList";
    public static final String COURSE_NAME = "CourseName";

    public GalleryFragment() {
    }

    public static GalleryFragment newInstance (String... arguments) {
        GalleryFragment fragment = new GalleryFragment();
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
        View rootView = inflater.inflate(R.layout.fragment_gallery, container, false);
        return rootView;
    }

    @Override
    public void onActivityCreated(Bundle args) {
        super.onActivityCreated(args);
    }

    public void goToCourse(View view) {
        String courseName = getActivity().getResources().getResourceEntryName(view.getId());

//        GalleryListFragment galleryListFragment = GalleryListFragment.newInstance(courseName);
//
//        FragmentTransaction transaction = getFragmentManager().beginTransaction();
//
//        transaction.replace(android.R.id.content, galleryListFragment);
//        transaction.addToBackStack(null);
//
//        transaction.commit();

        Bundle passOn = new Bundle();
        Item GalleryListItem = new Item(GalleryFragment.GALLERY_LIST_FRAGMENT_NAME, R.id.abs__icon);
        passOn.putString(COURSE_NAME, courseName);
        ((FragmentSample)getActivity()).onSubMenuItemClicked(0, GalleryListItem, passOn);
    }




}
