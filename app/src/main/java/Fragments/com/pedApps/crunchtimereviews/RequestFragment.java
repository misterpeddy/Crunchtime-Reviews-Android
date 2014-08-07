package com.pedApps.crunchtimereviews;

/**
 * Created by Pedram on 7/28/2014.
 */

import android.content.Context;
import android.database.DataSetObserver;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.SpinnerAdapter;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.w3c.dom.Text;
import org.xmlpull.v1.XmlPullParserException;

import java.io.IOException;
import java.util.ArrayList;


public class RequestFragment extends Fragment implements AdapterView.OnItemSelectedListener {
    public static String URL_GET_COURSES_ALL = AppBrain.CRUNCHTIME_ROOT_FOLDER_URL +  "DB_Request.php?database=crunchtime_reviews&query=SELECT%20DISTINCT%20course%20FROM%20professors";
    public static String URL_GET_TEACHERS_FOR = AppBrain.CRUNCHTIME_ROOT_FOLDER_URL +  "DB_Request.php?database=crunchtime_reviews&query=SELECT%20DISTINCT%20name%20FROM%20professors%20WHERE%20course%20=%20";
    public RequestFragment() {
    }

    public static RequestFragment newInstance (String... arguments) {
        RequestFragment fragment = new RequestFragment();
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
        View rootView = inflater.inflate(R.layout.fragment_request, container, false);
        return rootView;
    }

    @Override
    public void onActivityCreated(Bundle args) {
        super.onActivityCreated(args);
        setUpCourseSpinner();
        linkEditTextAndButton();
    }

    public void linkEditTextAndButton() {
        //Setting disabledBackground to the teachers spinner
        Spinner teachers = (Spinner) getActivity().findViewById(R.id.teachers);

        final EditText sessionInfo = (EditText) getActivity().findViewById(R.id.session_purpose);
        sessionInfo.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                sessionInfo.clearFocus();
                InputMethodManager imm = (InputMethodManager)getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(sessionInfo.getWindowToken(), 0);
                return true;
            }
        });
    }

    public void setUpCourseSpinner() {
        //Get Courses
        JSONCoursesListRetriever jsonCoursesListRetriever = new JSONCoursesListRetriever(null);
        jsonCoursesListRetriever.execute(URL_GET_COURSES_ALL);

        //Setup blank teacherlist
        Spinner teachers = (Spinner) getActivity().findViewById(R.id.teachers);
        final TeachersSpinnerAdapter spinnerAdapter = new TeachersSpinnerAdapter(getActivity(), null);
        teachers.setAdapter(spinnerAdapter);
        teachers.setEnabled(false);

    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {
    }



    public class JSONCoursesListRetriever extends JSONDataRetriever {
        public JSONCoursesListRetriever(ArrayList<String> fields) {
            super(fields);
        }

        @Override
        protected void onPostExecute(JSONArray jArray) {
            ArrayList<JSONObject> dataList = new ArrayList<JSONObject>();
            try {
                JSONObject json_data = new JSONObject();
                json_data.put("course", "Courses");
                dataList.add(json_data);
                for (int i = 0; i < jArray.length(); i++) {
                    json_data = jArray.getJSONObject(i);
                    dataList.add(json_data);
                }
            } catch (Exception e) {
                Log.e("log_tag", "Error getting fields " + e.toString());
            }

            Spinner courses = (Spinner) getActivity().findViewById(R.id.courses);
            final CoursesSpinnerAdapter spinnerAdapter = new CoursesSpinnerAdapter(getActivity(), dataList);
            courses.setAdapter(spinnerAdapter);

            //Link Courses and Teachers
            courses.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                @Override
                public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                    if (spinnerAdapter.selectionMade) {
                        JSONObject courseData = spinnerAdapter.data.get(position);
                        try {
                            String courseName = "'" + courseData.get("course") + "'";
                            new JSONTeachersListRetriever(null).execute(URL_GET_TEACHERS_FOR + courseName);
                            spinnerAdapter.notifyDataSetChanged();
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                    spinnerAdapter.selectionMade = true;
                }

                @Override
                public void onNothingSelected(AdapterView<?> parent) {
                }
            });

        }
    }


    public class JSONTeachersListRetriever extends JSONDataRetriever {
        public JSONTeachersListRetriever(ArrayList<String> fields) {
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
            } catch (Exception e) {
                Log.e("log_tag", "Error getting fields " + e.toString());
            }
            Spinner teachers = (Spinner) getActivity().findViewById(R.id.teachers);
            final TeachersSpinnerAdapter spinnerAdapter = new TeachersSpinnerAdapter(getActivity(), dataList);
            spinnerAdapter.selectionMade = true;
            teachers.setAdapter(spinnerAdapter);
            teachers.setEnabled(true);
            //Setting enableddBackground to the teachers spinner

        }
    }


}
