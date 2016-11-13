package com.androiddev.julianeiler.dailyfoto.fragment;

import android.app.NotificationManager;
import android.content.Context;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.app.NotificationCompat;
import android.support.v4.content.FileProvider;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import com.androiddev.julianeiler.dailyfoto.R;
import com.androiddev.julianeiler.dailyfoto.activity.MainActivity;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

import static android.content.Context.NOTIFICATION_SERVICE;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link HomeFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link HomeFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class HomeFragment extends Fragment implements View.OnClickListener{
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    private static final String TAG = "HomeFragment";
    String folder_main = "DailyFoto";
    private static final int REQUEST_TAKE_PHOTO = 1;
    final private int MY_PERMISSIONS_REQUEST_WRITE_EXTERNAL_STORAGE = 0;
    Button btnShow, btnClear, btnTakePicture, btnDebug, btnDebug2;
    NotificationManager manager;
    String mCurrentPhotoPath;
    ImageView img_view;
    Uri photoURI;

    private OnFragmentInteractionListener mListener;

    public HomeFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment HomeFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static HomeFragment newInstance(String param1, String param2) {
        HomeFragment fragment = new HomeFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_home, container, false);

        if (v.findViewById(R.id.fragment_container) != null) {

            // However, if we're being restored from a previous state,
            // then we don't need to do anything and should return or else
            // we could end up with overlapping fragments.
            if (savedInstanceState != null) {
                return v;
            }

            // Create a new Fragment to be placed in the activity layout
            ShowImageFragment imageFragment = new ShowImageFragment();
            GalleryFragment galleryFragment = new GalleryFragment();

            // In case this activity was started with special instructions from an
            // Intent, pass the Intent's extras to the fragment as arguments
            imageFragment.setArguments(getActivity().getIntent().getExtras());

            // Add the fragment to the 'fragment_container' FrameLayout
            getActivity().getSupportFragmentManager().beginTransaction()
                    .add(R.id.fragment_container, imageFragment).commit();
        }

        Log.d(TAG, "checks done");

        manager = (NotificationManager) getActivity().getSystemService(NOTIFICATION_SERVICE);
        initialise(v);
        return v;
    }

    // TODO: Rename method, update argument and hook method into UI event
    public void onButtonPressed(Uri uri) {
        if (mListener != null) {
            mListener.onFragmentInteraction(uri);
        }
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
//        if (context instanceof OnFragmentInteractionListener) {
//            mListener = (OnFragmentInteractionListener) context;
//        } else {
//            throw new RuntimeException(context.toString()
//                    + " must implement OnFragmentInteractionListener");
//        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        void onFragmentInteraction(Uri uri);
    }

    private void initialise(View v) {
        Log.d(TAG, "initialize");
        btnShow = (Button) v.findViewById(R.id.btnShowNotification);
        btnClear = (Button) v.findViewById(R.id.btnClearNotification);
        btnTakePicture = (Button) v.findViewById(R.id.btnTakePicture);
        btnDebug = (Button) v.findViewById(R.id.btnDebug);
        btnDebug2 = (Button) v.findViewById(R.id.btnDebug2);

        btnShow.setOnClickListener((View.OnClickListener) this);
        btnClear.setOnClickListener((View.OnClickListener) this);
        btnTakePicture.setOnClickListener((View.OnClickListener) this);
        btnDebug.setOnClickListener((View.OnClickListener) this);
        btnDebug2.setOnClickListener((View.OnClickListener) this);
    }

        private void dispatchTakePictureIntent() {
        Log.d(TAG, "dispatchTakePictureIntent");
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        // Ensure that there's a camera activity to handle the intent
        if (takePictureIntent.resolveActivity(getActivity().getPackageManager()) != null) {
            // Create the File where the photo should go
            File photoFile = null;
            try {
                photoFile = createImageFile();
            } catch (IOException ex) {
                // Error occurred while creating the File
                Log.d(TAG, "create ImageFile failed");
            }
            // Continue only if the File was successfully created
            if (photoFile != null) {
                Log.d(TAG, "we have a photo file");
                photoURI = FileProvider.getUriForFile(getActivity(),
                        "com.example.android.fileprovider",
                        photoFile);
                Log.d(TAG, "disp: " + photoURI.getPath());
                takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI);
                startActivityForResult(takePictureIntent, REQUEST_TAKE_PHOTO);

                // add picture to gallery
                galleryAddPic();
            }
        }
    }

    private void galleryAddPic() {
        Log.d(TAG, "galleryAddPic");
        Intent mediaScanIntent = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
        File f = new File(mCurrentPhotoPath);
        Uri contentUri = Uri.fromFile(f);
        mediaScanIntent.setData(contentUri);
        getActivity().sendBroadcast(mediaScanIntent);
    }

    private File createImageFile() throws IOException {
        Log.d(TAG, "createImageFile");

        // Create an image file name
        // String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String timeStamp = new SimpleDateFormat("yyyy_MM_dd").format(new Date());
        String imageFileName = "Image_" + timeStamp + "_";

        //File storageDir = new File(Environment.getExternalStorageDirectory(), folder_main);
        File storageDir = getActivity().getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        Log.d(TAG, "storageDir:" + storageDir);

        File image = new File(storageDir, imageFileName + ".jpg");


        // in case I want temporary file (adds unique suffix)

        /*File image = File.createTempFile(
                imageFileName,  *//* prefix *//*
                ".jpg",         *//* suffix *//*
                storageDir      *//* directory *//*
        );*/

        // Save a file: path for use with ACTION_VIEW intents
        mCurrentPhotoPath = "file:" + image.getAbsolutePath();
        return image;
    }

    /*
    Handle Buttons
     */
    @Override
    public void onClick(View v) {
        switch (v.getId())
        {
            case R.id.btnShowNotification:
                Log.d(TAG, "btnShowNotification");
                Toast.makeText(getActivity(), "Button Clicked: btnShowNotification", Toast.LENGTH_SHORT).show();

                NotificationCompat.Builder mBuilder =
                        new NotificationCompat.Builder(getActivity())
                                .setLargeIcon(BitmapFactory.decodeResource(getResources(), R.drawable.selfie_icon1))
                                .setSmallIcon(R.drawable.selfie1)
                                .setContentTitle("Take a fucking selfie")
                                .setContentText("NOW!");
                NotificationManager mNotificationManager =
                        (NotificationManager) getActivity().getSystemService(NOTIFICATION_SERVICE);
                // mId allows you to update the notification later on.
                mNotificationManager.notify(11, mBuilder.build());
                break;

            case R.id.btnClearNotification:
                Log.d(TAG, "btnClearNotification");
                Toast.makeText(getActivity(), "Button Clicked: btnClearNotification", Toast.LENGTH_SHORT).show();
                manager.cancel(11);
                break;

            case R.id.btnTakePicture:
                Log.d(TAG, "btnTakePicture");
                Toast.makeText(getActivity(), "Button Clicked: btnTakePicture", Toast.LENGTH_SHORT).show();

                dispatchTakePictureIntent();
                //btnTakePicture.setText("Retake Picture");
                break;

            case R.id.btnDebug:
                Log.d(TAG, "btnDebug");
                Toast.makeText(getActivity(), "Button Clicked: btnDebug", Toast.LENGTH_SHORT).show();

//                File folder = getExternalFilesDir(Environment.DIRECTORY_PICTURES);
//                allFiles = folder.listFiles();
//                new SingleMediaScanner(MainActivity.this, allFiles[0]);
                if (v.findViewById(R.id.fragment_container) != null) {

                    // Create a new Fragment to be placed in the activity layout
                    GalleryFragment galleryFragment = new GalleryFragment();
                    galleryFragment.setArguments(getActivity().getIntent().getExtras());

                    FragmentTransaction transaction = getActivity().getSupportFragmentManager().beginTransaction();
                    transaction.replace(R.id.fragment_container, galleryFragment);
                    transaction.addToBackStack(null);
                    transaction.commit();
                }
                break;

            case R.id.btnDebug2:
                Log.d(TAG, "btnDebug2");
                Toast.makeText(getActivity(), "Button Clicked: btnDebug2", Toast.LENGTH_SHORT).show();

                if (v.findViewById(R.id.fragment_container) != null) {

                    // Create a new Fragment to be placed in the activity layout
                    ShowImageFragment imageFragment = new ShowImageFragment();

                    FragmentTransaction transaction = getActivity().getSupportFragmentManager().beginTransaction();
                    transaction.replace(R.id.fragment_container, imageFragment);
                    transaction.addToBackStack(null);
                    transaction.commit();
                }
                break;
        }
    }

}
