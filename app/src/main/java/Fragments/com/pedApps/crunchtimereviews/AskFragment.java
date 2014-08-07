
package com.pedApps.crunchtimereviews;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;


import org.json.JSONArray;
import org.json.JSONObject;


import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class AskFragment extends Fragment implements Button.OnClickListener {

    // Activity result key for camera
    static final int REQUEST_TAKE_PHOTO = 11111;
    public final static String URL_GET_COURSES_ALL = AppBrain.CRUNCHTIME_ROOT_FOLDER_URL + "DB_Request.php?database=crunchtime_reviews&query=SELECT%20DISTINCT%20course%20FROM%20professors";

    private ImageView mThumbnailImageView;
    private Button takePictureButton;
    private Button sendButton;
    private TextView explanation, forward, selectedCourse;
    private Spinner courses;
    private EditText uvaId;
    private LinearLayout inputs;
    private HttpURLConnection connection;

    private String imagePath;
    private View layout;

    /**
     * Default empty constructor.
     */
    public AskFragment() {
        super();
    }


    public static AskFragment newInstance(Bundle args) {
        AskFragment fragment = new AskFragment();
        return fragment;
    }

    /**
     * OnCreateView fragment override
     *
     * @param inflater
     * @param container
     * @param savedInstanceState
     * @return
     */
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = null;
        view = inflater.inflate(R.layout.fragment_ask, container, false);
        this.layout = view;

        mThumbnailImageView = (ImageView) view.findViewById(R.id.imageViewThumbnail);
        takePictureButton = (Button) view.findViewById(R.id.takePhotoButton);
        explanation = (TextView) view.findViewById(R.id.explanation);
        courses = (Spinner) view.findViewById(R.id.courses);
        sendButton = (Button) view.findViewById(R.id.sendButton);
        uvaId = (EditText) view.findViewById(R.id.uva_id);
        forward = (TextView) view.findViewById(R.id.forward);
        selectedCourse = (TextView) view.findViewById(R.id.course_selected);
        inputs = (LinearLayout) view.findViewById(R.id.inputs);


        setUpCourseSpinner();
        // Set OnItemClickListener so we can be notified on button clicks
        takePictureButton.setOnClickListener(this);
        sendButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                PostPicture postPicture = new PostPicture(null);
                postPicture.execute(imagePath);
            }
        });

        return view;
    }

    public int providedID() {
        //200: OK
        //400: bad
        //0: none
        String id = uvaId.getText().toString();

        Pattern pattern = Pattern.compile("[^a-z0-9 ]", Pattern.CASE_INSENSITIVE);
        Matcher matcher = pattern.matcher(id);
        boolean badChars = matcher.find();

        if (id == null) {
            return 0;
        }
        if (id.length() < 5 || badChars){
            return 400;
        }
        return 200;
    }


    class PostPicture extends AsyncTask<String, Void, JSONArray> {
        AlertDialog alertDialog;

        public PostPicture() {
        }

        public PostPicture(ArrayList<String> fields) {
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
            text.setText("Uploading Picture");
            ImageView image = (ImageView) layout.findViewById(R.id.image);
            image.setImageResource(R.drawable.loader);

            AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
            builder.setView(layout);
            alertDialog = builder.create();
            alertDialog.show();
        }

        @Override
        protected JSONArray doInBackground(String... paths) {
            Log.d("POST", "about to post");
            HttpURLConnection connection = null;
            DataOutputStream outputStream = null;
            DataInputStream inputStream = null;
            String pathToOurFile = paths[0];
            String urlServer = AppBrain.CRUNCHTIME_ROOT_FOLDER_URL + AppBrain.IMAGE_UPLOAD_RELATIVE_URL;
            String lineEnd = "\r\n";
            String twoHyphens = "--";
            String boundary =  "*****";

            int bytesRead, bytesAvailable, bufferSize;
            byte[] buffer;
            int maxBufferSize = 1*1024*1024;

            try
            {
                Log.d("POST", "inside Try");
                FileInputStream fileInputStream = new FileInputStream(new File(pathToOurFile) );


                URL url = new URL(urlServer);
                connection = (HttpURLConnection) url.openConnection();


                // Allow Inputs &amp; Outputs.
                connection.setDoInput(true);
                connection.setDoOutput(true);
                connection.setUseCaches(false);

                // Set HTTP method to POST.
                connection.setRequestMethod("POST");

                connection.setRequestProperty("Connection", "Keep-Alive");
                connection.setRequestProperty("Content-Type", "multipart/form-data;boundary="+boundary);

                outputStream = new DataOutputStream( connection.getOutputStream() );
                outputStream.writeBytes(twoHyphens + boundary + lineEnd);
                outputStream.writeBytes("Content-Disposition: form-data; name=\"uploadedfile\";filename=\"" + pathToOurFile +"\"" + lineEnd);
                outputStream.writeBytes(lineEnd);

                bytesAvailable = fileInputStream.available();
                bufferSize = Math.min(bytesAvailable, maxBufferSize);
                buffer = new byte[bufferSize];

                // Read file
                bytesRead = fileInputStream.read(buffer, 0, bufferSize);

                while (bytesRead > 0)
                {
                    outputStream.write(buffer, 0, bufferSize);
                    bytesAvailable = fileInputStream.available();
                    bufferSize = Math.min(bytesAvailable, maxBufferSize);
                    bytesRead = fileInputStream.read(buffer, 0, bufferSize);
                }

                outputStream.writeBytes(lineEnd);
                outputStream.writeBytes(twoHyphens + boundary + twoHyphens + lineEnd);

                // Responses from the server (code and message)
                int serverResponseCode = connection.getResponseCode();
                String serverResponseMessage = connection.getResponseMessage();
                Log.d("POST", "responseCode: " + serverResponseCode);
                Log.d("POST", "responseMessage: " + serverResponseMessage);
                fileInputStream.close();
                outputStream.flush();
                outputStream.close();
            }
            catch (Exception ex)
            {
                Log.d("POST", "error during writing");
            }
            return null;
        }

        @Override
        protected void onPostExecute(JSONArray jArray) {
            if (alertDialog.isShowing()) {
                alertDialog.dismiss();
            }
            forward.setText(getString(R.string.ask_forward));
            Toast.makeText(getActivity(), "Image is uploaded", Toast.LENGTH_SHORT).show();
        }
    }

    /**
     * Start the camera by dispatching a camera intent.
     */
    protected void dispatchTakePictureIntent() {

        // Check if there is a camera.
        Context context = getActivity();
        PackageManager packageManager = context.getPackageManager();
        if (!packageManager.hasSystemFeature(PackageManager.FEATURE_CAMERA)) {
            Toast.makeText(getActivity(), "This device does not have a camera.", Toast.LENGTH_SHORT)
                    .show();
            return;
        }

        // Camera exists? Then proceed...
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);

        // Ensure that there's a camera activity to handle the intent
        AppBrain activity = (AppBrain) getActivity();
        if (takePictureIntent.resolveActivity(activity.getPackageManager()) != null) {
            // Create the File where the photo should go.
            // If you don't do this, you may get a crash in some devices.
            File photoFile = null;
            try {
                photoFile = createImageFile();
            } catch (IOException ex) {
                // Error occurred while creating the File
                Toast toast = Toast.makeText(activity, "There was a problem saving the photo...", Toast.LENGTH_SHORT);
                toast.show();
            }
            // Continue only if the File was successfully created
            if (photoFile != null) {
                Uri fileUri = Uri.fromFile(photoFile);
                activity.setCapturedImageURI(fileUri);
                activity.setCurrentPhotoPath(fileUri.getPath());
                takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT,
                        activity.getCapturedImageURI());
                startActivityForResult(takePictureIntent, REQUEST_TAKE_PHOTO);
            }
        }
    }

    /**
     * The activity returns with the photo.
     *
     * @param requestCode
     * @param resultCode
     * @param data
     */
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQUEST_TAKE_PHOTO && resultCode == Activity.RESULT_OK) {
            addPhotoToGallery();
            AppBrain activity = (AppBrain) getActivity();
            doNewLayout();


            // Show the full sized image.
            //setFullImageFromFilePath(activity.getCurrentPhotoPath(), mImageView);
            setFullImageFromFilePath(activity.getCurrentPhotoPath(), mThumbnailImageView);
        } else {
            Toast.makeText(getActivity(), "Image Capture Failed", Toast.LENGTH_SHORT)
                    .show();
        }
    }


    public void doNewLayout() {
        takePictureButton.setVisibility(View.GONE);
        explanation.setVisibility(View.GONE);
        uvaId.setVisibility(View.GONE);
        courses.setVisibility(View.GONE);
        inputs.setVisibility(View.GONE);
        mThumbnailImageView.getLayoutParams().height = 500;
        mThumbnailImageView.getLayoutParams().width = 500;
        sendButton.setVisibility(View.VISIBLE);
        forward.setVisibility(View.VISIBLE);

    }

    public void setUpCourseSpinner() {
        //Get Courses
        JSONCoursesListRetriever jsonCoursesListRetriever = new JSONCoursesListRetriever(null);
        jsonCoursesListRetriever.execute(URL_GET_COURSES_ALL);
    }

    /**
     * Creates the image file to which the image must be saved.
     *
     * @return
     * @throws java.io.IOException
     */
    protected File createImageFile() throws IOException {
        // Create an image file name
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String imageFileName =
                ((TextView)courses.getSelectedView()).getText().toString()+ "_" +
                    uvaId.getText().toString() + "_" +
                        timeStamp + "_";
        File storageDir = Environment.getExternalStoragePublicDirectory(
                Environment.DIRECTORY_PICTURES);
        File image = File.createTempFile(
                imageFileName,  /* prefix */
                ".jpg",         /* suffix */
                storageDir      /* directory */
        );

        // Save a file: path for use with ACTION_VIEW intents
        AppBrain activity = (AppBrain) getActivity();
        activity.setCurrentPhotoPath("file:" + image.getAbsolutePath());
        return image;
    }

    /**
     * Add the picture to the photo gallery.
     * Must be called on all camera images or they will
     * disappear once taken.
     */
    protected void addPhotoToGallery() {
        Intent mediaScanIntent = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
        AppBrain activity = (AppBrain) getActivity();
        File f = new File(activity.getCurrentPhotoPath());
        Uri contentUri = Uri.fromFile(f);
        mediaScanIntent.setData(contentUri);
        this.getActivity().sendBroadcast(mediaScanIntent);
    }

    /**
     * Deal with button clicks.
     *
     * @param v
     */
    @Override
    public void onClick(View v) {
        int courseSelectedPosition = courses.getSelectedItemPosition();
        if (courseSelectedPosition > 0) {
            if (providedID() == 200) {
                dispatchTakePictureIntent();
            }
            else if (providedID() == 400) {
                Toast.makeText(getActivity(), "Questions without legitimate UVa ID's will not be considered", Toast.LENGTH_LONG).show();
            }
            else {
                Toast.makeText(getActivity(), "Please provide UVa ID", Toast.LENGTH_SHORT).show();
            }
        }
        else {
            Toast.makeText(getActivity(), "Please select a course", Toast.LENGTH_SHORT).show();
        }

    }

    /**
     * Scale the photo down and fit it to our image views.
     * <p/>
     * "Drastically increases performance" to set images using this technique.
     * Read more:http://developer.android.com/training/camera/photobasics.html
     */
    private void setFullImageFromFilePath(String imagePath, ImageView imageView) {
        this.imagePath = imagePath;
        Log.d("image path", imagePath);
        // Get the dimensions of the View
        int targetW = imageView.getWidth();
        int targetH = imageView.getHeight();

        // Get the dimensions of the bitmap
        BitmapFactory.Options bmOptions = new BitmapFactory.Options();
        bmOptions.inJustDecodeBounds = true;
        BitmapFactory.decodeFile(imagePath, bmOptions);
        int photoW = bmOptions.outWidth;
        int photoH = bmOptions.outHeight;

        // Determine how much to scale down the image
        int scaleFactor = Math.min(photoW / targetW, photoH / targetH);

        // Decode the image file into a Bitmap sized to fill the View
        bmOptions.inJustDecodeBounds = false;
        bmOptions.inSampleSize = scaleFactor;
        bmOptions.inPurgeable = true;

        Bitmap bitmap = BitmapFactory.decodeFile(imagePath, bmOptions);
        imageView.setImageBitmap(bitmap);
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
            sendButton.setEnabled(true);
        }
    }

}