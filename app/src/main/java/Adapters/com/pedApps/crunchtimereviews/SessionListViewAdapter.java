package com.pedApps.crunchtimereviews;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.TextView;

import org.json.JSONObject;

import java.util.ArrayList;

/**
 * Created by Pedram on 7/28/2014.
 */
public class SessionListViewAdapter extends BaseAdapter {
    private ArrayList<JSONObject> data;
    private LayoutInflater inflater;
    private Context context;
    public JSONObject jsonObject;

    public SessionListViewAdapter(Context context, ArrayList<JSONObject> data) {
        this.context = context;
        this.data = data;
    }

    @Override
    public int getCount() {
        return data.size();
    }

    @Override
    public Object getItem(int index) {
        return index;
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    public View getView(int position, View view, ViewGroup parent) {
        View v = view;
        if (v == null) {
            inflater = (LayoutInflater) context
                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            v = inflater.inflate(R.layout.session_row, null);
        }

        TextView material = (TextView) v.findViewById(R.id.material);
        TextView location = (TextView) v.findViewById(R.id.location);
        TextView date = (TextView) v.findViewById(R.id.date);
        TextView time = (TextView) v.findViewById(R.id.time);
        Button course = (Button) v.findViewById(R.id.course);

        jsonObject = data.get(position);

        try {
            material.setText("Sections " + jsonObject.get("material"));
            location.setText((String) jsonObject.get("location"));
            date.setText((String) jsonObject.get("date"));
            time.setText((String) jsonObject.get("time"));
            course.setText(getCourseName((String) jsonObject.get("course")));

        }catch (Exception e) {

        }

        return v;
    }

    //*will BREAK when course prefixes are not 4 letters
    public String getCourseName(String course) {
        return course.substring(0,4) + " " + course.substring(4);
    }

}
