package com.pedApps.crunchtimereviews;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.SpinnerAdapter;
import android.widget.TextView;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

/**
 * Created by Pedram on 7/30/2014.
 */
public class TeachersSpinnerAdapter extends BaseAdapter implements SpinnerAdapter {
    ArrayList<JSONObject> data = new ArrayList<JSONObject>();
    Context context;
    public boolean selectionMade = false;

    public TeachersSpinnerAdapter(Context context, ArrayList<JSONObject> data) {
        this.data = data;
        this.context = context;
        if (data == null) {
            JSONObject jsonObject = new JSONObject();
            try {
                jsonObject.putOpt("name","Professors");
            } catch (JSONException e) {
                e.printStackTrace();
            }
            ArrayList<JSONObject> jsonObjects = new ArrayList<JSONObject>();
            jsonObjects.add(jsonObject);
            this.data =  jsonObjects;
        }
    }
    @Override
    public int getCount() {
        return data.size();
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
//            tv.setText("Professors");
//            return tv;
//        }
        return getDropDownView(position, convertView, parent);
    }

    @Override
    public View getDropDownView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            LayoutInflater infalInflater = (LayoutInflater) this.context
                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            if (this.selectionMade)
                convertView = infalInflater.inflate(R.layout.spinner_item, null);
            else
                convertView = infalInflater.inflate(R.layout.spinner_item_disabled, null);
        }

        TextView textView = ((TextView) convertView.findViewById(R.id.spinner_item));

        JSONObject jsonObject = data.get(position);
        try {
            textView.setText((String) jsonObject.get("name"));
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return textView;
    }


}
