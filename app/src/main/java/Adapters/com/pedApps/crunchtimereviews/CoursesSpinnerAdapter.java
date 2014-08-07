package com.pedApps.crunchtimereviews;

import android.app.Activity;
import android.content.Context;
import android.graphics.drawable.Drawable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.SpinnerAdapter;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;
import org.xmlpull.v1.XmlPullParserException;

import java.io.IOException;
import java.util.ArrayList;

/**
 * Created by Pedram on 7/30/2014.
 */
public class CoursesSpinnerAdapter  extends BaseAdapter implements SpinnerAdapter {
    ArrayList<JSONObject> data = new ArrayList<JSONObject>();
    Context context;
    public boolean selectionMade = false;

    public CoursesSpinnerAdapter(Context context, ArrayList<JSONObject> data) {
        this.data = data;
        this.context = context;
    }
    @Override
    public int getCount() {
        return data.size();
    }

    @Override
    public View getDropDownView(int position, View convertView, ViewGroup parent) {

        if (convertView == null) {
            LayoutInflater infalInflater = (LayoutInflater) this.context
                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = infalInflater.inflate(R.layout.spinner_item, null);
        }


        JSONObject jsonObject = data.get(position);
        TextView textView = ((TextView) convertView.findViewById(R.id.spinner_item));
        try {
            textView.setText((String) jsonObject.get("course"));
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return textView;
    }


    @Override
    public Object getItem(int position) {
        return data.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
//        if (!selectionMade) {
//            TextView tv = new TextView(context);
//            tv.setText("Courses");
//            return tv;
//        }
        return getDropDownView(position, convertView, parent);
    }

    @Override
    public boolean isEnabled(int position) {
        return position != 0 ;// Don't allow the 'nothing selected' item to be picked.
    }
    @Override
    public boolean areAllItemsEnabled() {
        return false;
    }
}
