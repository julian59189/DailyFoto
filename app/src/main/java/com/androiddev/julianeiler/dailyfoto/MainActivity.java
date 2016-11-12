package com.androiddev.julianeiler.dailyfoto;


import android.Manifest;
import android.app.NotificationManager;
import android.content.Context;
import android.content.ContextWrapper;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.MediaScannerConnection;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.NotificationCompat;
import android.support.v4.content.FileProvider;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.Toast;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class MainActivity extends AppCompatActivity implements View.OnClickListener{

    private static final String TAG = "MainActivity";
    String folder_main = "DailyFoto";
    private static final int REQUEST_TAKE_PHOTO = 1;
    final private int MY_PERMISSIONS_REQUEST_WRITE_EXTERNAL_STORAGE = 0;
    GridView mGridView;
    Button btnShow, btnClear, btnTakePicture, btnDebug, btnDebug2;
    NotificationManager manager;
    String mCurrentPhotoPath;
    ImageView img_view;
    Uri photoURI;
    Context context = this;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Log.d(TAG, "onCreate");
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        initialise();
        checkFolder();

        Toolbar myToolbar = (Toolbar) findViewById(R.id.my_toolbar);
        setSupportActionBar(myToolbar);
        //myToolbar.setLogo(R.drawable.selfie);


        Log.d(TAG, "checks done");

        manager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);


        Log.d(TAG, "onClickListeners Created");
    }

    @Override
    public void onResume() {
        super.onResume();  // Always call the superclass method first
        checkPermissions();
    }

    @Override
    public void onPause() {
        super.onPause();  // Always call the superclass method first
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main_menu, menu);
        return true;
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_settings:
                // User chose the "Settings" item, show the app settings UI...
                img_view.setImageResource(R.drawable.selfie);
                return true;

            case R.id.action_favorite:
                // User chose the "Favorite" action, mark the current item
                // as a favorite...
                return true;

            default:
                // If we got here, the user's action was not recognized.
                // Invoke the superclass to handle it.
                return super.onOptionsItemSelected(item);

        }
    }

    private void dispatchTakePictureIntent() {
        Log.d(TAG, "dispatchTakePictureIntent");
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        // Ensure that there's a camera activity to handle the intent
        if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
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
                photoURI = FileProvider.getUriForFile(this,
                        "com.example.android.fileprovider",
                        photoFile);
                Log.d(TAG, "disp: " + photoURI.getPath());
                takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI);
                startActivityForResult(takePictureIntent, REQUEST_TAKE_PHOTO);

                // add picture to gallery
                galleryAddPic();

                //startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE);
            }
        }
    }

    private File createImageFile() throws IOException {
        Log.d(TAG, "createImageFile");

        // Create an image file name
        // String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String timeStamp = new SimpleDateFormat("yyyyMMdd").format(new Date());
        String imageFileName = "JPEG_" + timeStamp + "_";

        //File storageDir = new File(Environment.getExternalStorageDirectory(), folder_main);
        File storageDir = getExternalFilesDir(Environment.DIRECTORY_PICTURES);

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


    private void checkFolder() {
        File f = new File(Environment.getExternalStorageDirectory(), folder_main);
        if (!f.exists()) {
            f.mkdirs();
        }
    }


    private void checkPermissions() {
        //Log.d(TAG, "checkPermissions");
        // Assume thisActivity is the current activity
        ActivityCompat.requestPermissions(MainActivity.this,
                new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},
                MY_PERMISSIONS_REQUEST_WRITE_EXTERNAL_STORAGE);

        if (!Environment.getExternalStorageDirectory().canWrite()){
            //btnTakePicture.setBackgroundColor(Color.RED);
            btnTakePicture.setEnabled(false);
            btnTakePicture.setText(R.string.grant_storage_permission);
        }
        else{
            btnTakePicture.setEnabled(true);
            btnTakePicture.setText(R.string.take_picture);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String permissions[], int[] grantResults) {
        switch (requestCode) {
            case MY_PERMISSIONS_REQUEST_WRITE_EXTERNAL_STORAGE: {

                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                        Log.d(TAG, "permission was granted");
                    // permission was granted, yay! Do the
                    // contacts-related task you need to do.
                } else {

                    // permission denied, boo! Disable the
                    // functionality that depends on this permission.
                    Toast.makeText(MainActivity.this, "Permission denied to read your External storage", Toast.LENGTH_SHORT).show();
                    Toast.makeText(MainActivity.this, "This app does not work without these permissions", Toast.LENGTH_SHORT).show();


                    btnTakePicture.setEnabled(false);
                    btnTakePicture.setText(R.string.grant_storage_permission);
                }
            }

            // other 'case' lines to check for other
            // permissions this app might request
        }
    }



    private void initialise() {
        Log.d(TAG, "initialize");
        btnShow = (Button) findViewById(R.id.btnShowNotification);
        btnClear = (Button) findViewById(R.id.btnClearNotification);
        btnTakePicture = (Button) findViewById(R.id.btnTakePicture);
        btnDebug = (Button) findViewById(R.id.btnDebug);
        btnDebug2 = (Button) findViewById(R.id.btnDebug2);
        img_view = (ImageView) findViewById(R.id.mImageView);

        btnShow.setOnClickListener(this);
        btnClear.setOnClickListener(this);
        btnTakePicture.setOnClickListener(this);
        btnDebug.setOnClickListener(this);
        btnDebug2.setOnClickListener(this);
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
                Toast.makeText(MainActivity.this, "Button Clicked: btnShowNotification", Toast.LENGTH_SHORT).show();

                NotificationCompat.Builder mBuilder =
                        new NotificationCompat.Builder(MainActivity.this)
                                .setLargeIcon(BitmapFactory.decodeResource(getResources(), R.drawable.selfie_icon1))
                                .setSmallIcon(R.drawable.selfie1)
                                .setContentTitle("Take a fucking selfie")
                                .setContentText("NOW!");
                NotificationManager mNotificationManager =
                        (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
                // mId allows you to update the notification later on.
                mNotificationManager.notify(11, mBuilder.build());
                break;

            case R.id.btnClearNotification:
                Log.d(TAG, "btnClearNotification");
                Toast.makeText(MainActivity.this, "Button Clicked: btnClearNotification", Toast.LENGTH_SHORT).show();

                manager.cancel(11);
                break;

            case R.id.btnTakePicture:
                Log.d(TAG, "btnTakePicture");
                Toast.makeText(MainActivity.this, "Button Clicked: btnTakePicture", Toast.LENGTH_SHORT).show();

                dispatchTakePictureIntent();

                //btnTakePicture.setText("Retake Picture");
                break;

            case R.id.btnDebug:
                Log.d(TAG, "btnDebug");
                Toast.makeText(MainActivity.this, "Button Clicked: btnDebug", Toast.LENGTH_SHORT).show();

//                File folder = getExternalFilesDir(Environment.DIRECTORY_PICTURES);
//                allFiles = folder.listFiles();
//                new SingleMediaScanner(MainActivity.this, allFiles[0]);
                Intent i_mult = new Intent(getApplicationContext(), AndroidCustomGalleryActivity.class);
                startActivity(i_mult);
                break;

            case R.id.btnDebug2:
                Log.d(TAG, "btnDebug2");
                Toast.makeText(MainActivity.this, "Button Clicked: btnDebug2", Toast.LENGTH_SHORT).show();

                Intent i_single = new Intent(getApplicationContext(),AndroidDiplayImage.class);
                startActivity(i_single);
                break;
        }
    }

    public class SingleMediaScanner implements MediaScannerConnection.MediaScannerConnectionClient {

        private MediaScannerConnection mMs;
        private File mFile;

        public SingleMediaScanner(Context context, File f) {
            mFile = f;
            mMs = new MediaScannerConnection(context, this);
            mMs.connect();
        }

        public void onMediaScannerConnected() {
            mMs.scanFile(mFile.getAbsolutePath(), null);
        }

        public void onScanCompleted(String path, Uri uri) {
            Intent intent = new Intent(Intent.ACTION_VIEW);
            intent.setData(uri);
            startActivity(intent);
            mMs.disconnect();
        }

    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        Log.d(TAG, "onActivityResult");
        if (requestCode == REQUEST_TAKE_PHOTO && resultCode == RESULT_OK) {
            //Bundle extras = data.getExtras();
            //Bitmap imageBitmap = (Bitmap) extras.get("data");
            //mImageView.setImageBitmap(imageBitmap);

            String yourFilePath = context.getFilesDir() + "/" + "Pictures/JPEG_20161018_.jpg";
            Log.d(TAG, "context_dir: " + context.getFilesDir());
            File imgFile = new File( yourFilePath );

            //File imgFile = new  File("Android/data/com.androiddev.julianeiler.dailyfoto/files/Pictures/JPEG_20161018_.jpg");
            if(imgFile.exists()){
                Log.d(TAG, "imageFileexist");
                Bitmap myBitmap = BitmapFactory.decodeFile(imgFile.getAbsolutePath());
                //mImageView.setImageBitmap(myBitmap);

            }

            Log.d(TAG, "res: " + photoURI.getPath());

            Bitmap myImg = BitmapFactory.decodeFile("Android/data/com.androiddev.julianeiler.dailyfoto/files/Pictures/JPEG_20161018_.jpg");
            //mImageView.setImageBitmap(myImg);
        }

        Log.d(TAG, "onActivityResult...done");
    }


    private String saveToInternalStorage(Bitmap bitmapImage){
        Log.d(TAG, "saveToInternalStorage");
        ContextWrapper cw = new ContextWrapper(getApplicationContext());
        // path to /data/data/yourapp/app_data/imageDir
        File directory = cw.getDir("imageDir", Context.MODE_PRIVATE);
        // Create imageDir
        File mypath=new File(directory,"profile.jpg");

        FileOutputStream fos = null;
        try {
            fos = new FileOutputStream(mypath);
            // Use the compress method on the BitMap object to write image to the OutputStream
            bitmapImage.compress(Bitmap.CompressFormat.PNG, 100, fos);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                assert fos != null;
                fos.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return directory.getAbsolutePath();
    }

    private void displayPicture() {
        String yourFilePath = context.getFilesDir() + "/" + "Pictures/JPEG_20161106_.jpg";
        Log.d(TAG, "context_dir: " + context.getFilesDir());
        File imgFile = new File( yourFilePath );



        //File imgFile = new  File("Android/data/com.androiddev.julianeiler.dailyfoto/files/Pictures/JPEG_20161018_.jpg");
        if(imgFile.exists()){
            Log.d(TAG, "imageFileexist");


            int resID = getResources().getIdentifier(yourFilePath, null, getPackageName());
            //mGridView.setImageResource(resID);

            //Bitmap myBitmap = BitmapFactory.decodeFile(imgFile.getAbsolutePath());
            //mImageView.setImageBitmap(myBitmap);

        }

        Bitmap myImg = BitmapFactory.decodeFile("Android/data/com.androiddev.julianeiler.dailyfoto/files/Pictures/JPEG_20161018_.jpg");
        // mImageView.setImageBitmap(myImg);

    }


    private void galleryAddPic() {
        Log.d(TAG, "galleryAddPic");
        Intent mediaScanIntent = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
        File f = new File(mCurrentPhotoPath);
        Uri contentUri = Uri.fromFile(f);
        mediaScanIntent.setData(contentUri);
        this.sendBroadcast(mediaScanIntent);
    }

}