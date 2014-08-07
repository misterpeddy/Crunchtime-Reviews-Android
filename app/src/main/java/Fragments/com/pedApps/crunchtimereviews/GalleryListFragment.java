package com.pedApps.crunchtimereviews;

import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ExpandableListView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;


public class GalleryListFragment extends GalleryFragment {
    public static final String ARG_COURSE_NAME = "course";
    public static final String URL_GET_ALL_VIDEOS_FOR = AppBrain.CRUNCHTIME_ROOT_FOLDER_URL +  "DB_Request.php?database=crunchtime_reviews&query=SELECT%20unit,number,title,id%20FROM%20";
    public Bundle videoInfo;
    public ExpandableListAdapter listAdapter;

    public GalleryListFragment() {
    }


    public static GalleryListFragment newInstance(String courseName) {
        GalleryListFragment fragment = new GalleryListFragment();

        Bundle args = new Bundle();
        args.putString(ARG_COURSE_NAME, courseName);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_gallery_list, container, false);
        return rootView;
    }

    @Override
    public void onActivityCreated(Bundle args) {
        super.onActivityCreated(args);
        TextView textView = (TextView) getActivity().findViewById(R.id.course_name);
        textView.setText(getArguments().getString(ARG_COURSE_NAME));

        getJSON();
    }

    public void getJSON() {
        JSONDataRetriever jsonFromUrl = new JSONAllVideosRetriever(null);
        String url = URL_GET_ALL_VIDEOS_FOR + getDBStyleCourseName(getArguments().getString(ARG_COURSE_NAME));
        jsonFromUrl.execute(url);
    }

    public class JSONAllVideosRetriever extends JSONDataRetriever {
        public JSONAllVideosRetriever(ArrayList<String> fields) {
            super(fields);
        }

        @Override
        protected void onPostExecute(JSONArray jArray) {
            ArrayList<JSONObject> dataList = new ArrayList<JSONObject>();
            try {
                for (int i = 0; i < jArray.length(); i++) {
                    JSONObject json_data = jArray.getJSONObject(i);
                    dataList.add(json_data);
                }
            }
            catch (Exception e) {
                Log.e("log_tag", "Error getting fields " + e.toString());
            }

            ExpandableListView videoList = (ExpandableListView) getActivity().findViewById(R.id.videoList);
            listAdapter = new ExpandableListAdapter(getActivity(), dataList);
            videoList.setAdapter(listAdapter);

            videoList.setOnChildClickListener(new ExpandableListView.OnChildClickListener() {
                @Override
                public boolean onChildClick(ExpandableListView parent, View v, int groupPosition, int childPosition, long id) {
                    Toast.makeText(
                            getActivity(),
                            listAdapter._listDataHeader.get(groupPosition)
                                    + " : "
                                    + listAdapter._listDataChild.get(
                                    listAdapter._listDataHeader.get(groupPosition)).get(
                                    childPosition), Toast.LENGTH_SHORT)
                            .show();


                    String course = getArguments().getString(ARG_COURSE_NAME);
                    String title = (String) listAdapter.getChild(groupPosition, childPosition);
                    String video_id = listAdapter.getChildId(groupPosition, childPosition) + "";

                    Bundle args = new Bundle();
                    args.putString("course", course);
                    args.putString("title", title);
                    args.putString("id", video_id);
                    GalleryListFragment.this.videoInfo = args;

                    getDescriptAndUrl(args);

                    return false;
                }
            });


        }
    }

    public void getDescriptAndUrl(Bundle args) {
        String courseName = getDBStyleCourseName((String)args.get("course"));
        String id = (String) args.get("id");
        String url = "http://108.18.98.201/crunchtime_reviews/DB_Request.php?database=crunchtime_reviews&query=SELECT%20DISTINCT%20description,video_url%20FROM%20"+ courseName +"%20WHERE%20id=" + id;
        JSONVideoInfoRetriever jsonVideoInfoRetriever = new JSONVideoInfoRetriever(null);
        jsonVideoInfoRetriever.execute(url);
    }


    public class JSONVideoInfoRetriever extends JSONDataRetriever {
        AlertDialog alertDialog;

        public JSONVideoInfoRetriever(ArrayList<String> fields) {
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
            text.setText("Loading Video");
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

            ArrayList<JSONObject> dataList = new ArrayList<JSONObject>();
            try {
                for (int i = 0; i < jArray.length(); i++) {
                    JSONObject json_data = jArray.getJSONObject(i);
                    dataList.add(json_data);
                }
            }
            catch (Exception e) {
                Log.e("log_tag", "Error getting fields " + e.toString());
            }
            String description, url;

            try {
                description = (String) dataList.get(0).get("description");
                url = parseURL((String) dataList.get(0).get("video_url"));
                GalleryListFragment.this.videoInfo.putString("url", url);
                GalleryListFragment.this.videoInfo.putString("description", description);

                Intent intent = new Intent(getActivity(), VideoPlayerActivity.class);
                intent.putExtras(GalleryListFragment.this.videoInfo);
                startActivity(intent);

            } catch (JSONException e) {
                Log.e("JSON", "Could not get fields video_url and description");
                Toast.makeText(getActivity(), "CrunchError 404 - Could not load video", Toast.LENGTH_LONG);
            }

        }


    }







    public String parseURL (String rawURL) {
        return rawURL.substring(rawURL.indexOf('=')+1);
    }



    public String getDBStyleCourseName(String courseName) {
        return courseName.substring(0,4) + "_" + courseName.substring(4) + "_" + "videos";
    }

}
