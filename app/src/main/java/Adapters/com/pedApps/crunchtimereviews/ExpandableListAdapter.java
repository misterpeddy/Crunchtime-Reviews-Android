package com.pedApps.crunchtimereviews;

/**
 * Created by Pedram on 7/31/2014.
 */

import android.util.Log;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;

import android.content.Context;
import android.graphics.Typeface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.TextView;

import org.json.JSONException;
import org.json.JSONObject;

public class ExpandableListAdapter extends BaseExpandableListAdapter {

    public Context _context;
    public String selected_url;
    public List<String> _listDataHeader = new ArrayList<String>(); // header titles
    // child data in format of header title, child title
    public HashMap<String, List<String>> _listDataChild = new HashMap<String, List<String>>();
    public HashMap<String, String> ids = new HashMap<String, String>();

    public ExpandableListAdapter(Context context, ArrayList<JSONObject> JSONData) {
        this._context = context;

        String unit, title, id;
        //Converting JSON data into a map and list
        for (JSONObject jsonObject : JSONData) {
            Log.d("json:", jsonObject.toString());
            try {
                    unit = "Unit " + (String)jsonObject.get("unit");
                    title = "Section " + jsonObject.get("number") + " - "  + jsonObject.get("title");
                    id = (String) jsonObject.get("id");
                    ids.put(unit+"."+title, id);

                if (!_listDataHeader.contains(unit)) _listDataHeader.add(unit);
                if (_listDataChild.containsKey(unit)) {
                    _listDataChild.get(unit).add(title);
                    Collections.sort(_listDataChild.get(unit));
                }
                else {
                    ArrayList<String> temp = new ArrayList<String>();
                    temp.add(title);
                    _listDataChild.put(unit, temp);
                }

            } catch (JSONException e) {
                Log.d("Error", "Could not read unit, section, id");
            }
        }
        Collections.sort(_listDataHeader);
    }

    @Override
    public Object getChild(int groupPosition, int childPosititon) {
        return this._listDataChild.get(this._listDataHeader.get(groupPosition))
                .get(childPosititon);
    }

    @Override
    public long getChildId(int groupPosition, int childPosition) {
        String key = _listDataHeader.get(groupPosition) + "." + this._listDataChild.get(this._listDataHeader.get(groupPosition)).get(childPosition);
        return Integer.parseInt(ids.get(key));
    }

    @Override
    public View getChildView(int groupPosition, final int childPosition,
                             boolean isLastChild, View convertView, ViewGroup parent) {

        final String childText = (String) getChild(groupPosition, childPosition);

        if (convertView == null) {
            LayoutInflater infalInflater = (LayoutInflater) this._context
                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = infalInflater.inflate(R.layout.gallery_section, null);
        }

        TextView txtListChild = (TextView) convertView
                .findViewById(R.id.section_header);

        txtListChild.setText(childText);
        return convertView;
    }

    @Override
    public int getChildrenCount(int groupPosition) {
        return this._listDataChild.get(this._listDataHeader.get(groupPosition))
                .size();
    }

    @Override
    public Object getGroup(int groupPosition) {
        return this._listDataHeader.get(groupPosition);
    }

    @Override
    public int getGroupCount() {
        return this._listDataHeader.size();
    }

    @Override
    public long getGroupId(int groupPosition) {
        return groupPosition;
    }

    @Override
    public View getGroupView(int groupPosition, boolean isExpanded,
                             View convertView, ViewGroup parent) {
        String headerTitle = (String) getGroup(groupPosition);
        if (convertView == null) {
            LayoutInflater infalInflater = (LayoutInflater) this._context
                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = infalInflater.inflate(R.layout.gallery_unit, null);
        }

        TextView lblListHeader = (TextView) convertView
                .findViewById(R.id.unit_header);
        lblListHeader.setTypeface(null, Typeface.ITALIC);
        lblListHeader.setText(headerTitle);

        return convertView;
    }

    @Override
    public boolean hasStableIds() {
        return false;
    }

    @Override
    public boolean isChildSelectable(int groupPosition, int childPosition) {
        return true;
    }
}