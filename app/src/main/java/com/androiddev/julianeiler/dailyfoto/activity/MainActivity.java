package com.androiddev.julianeiler.dailyfoto.activity;

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
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.app.NotificationCompat;
import android.support.v4.content.FileProvider;
import android.support.v4.view.GravityCompat;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import com.androiddev.julianeiler.dailyfoto.fragment.GalleryFragment;
import com.androiddev.julianeiler.dailyfoto.R;
import com.androiddev.julianeiler.dailyfoto.fragment.ShowImageFragment;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;


import android.os.Handler;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.v4.widget.DrawerLayout;
import android.widget.TextView;


import  com.androiddev.julianeiler.dailyfoto.fragment.HomeFragment;
import  com.androiddev.julianeiler.dailyfoto.fragment.MoviesFragment;
import  com.androiddev.julianeiler.dailyfoto.fragment.NotificationsFragment;
import  com.androiddev.julianeiler.dailyfoto.fragment.PhotosFragment;
import  com.androiddev.julianeiler.dailyfoto.fragment.SettingsFragment;
import  com.androiddev.julianeiler.dailyfoto.other.CircleTransform;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;

public class MainActivity extends AppCompatActivity implements View.OnClickListener{
    // Good tutorial for toolbar
    // https://guides.codepath.com/android/Using-the-App-ToolBar

    private NavigationView navigationView;
    private DrawerLayout drawer;
    private View navHeader;
    private ImageView imgNavHeaderBg, imgProfile;
    private TextView txtName, txtWebsite;
    private Toolbar toolbar;
    private FloatingActionButton fab;

    // urls to load navigation header background image
    // and profile image
    private static final String urlNavHeaderBg = "http://api.androidhive.info/images/nav-menu-header-bg.jpg";
    private static final String urlProfileImg = "https://lh3.googleusercontent.com/eCtE_G34M9ygdkmOpYvCag1vBARCmZwnVS6rS5t4JLzJ6QgQSBquM0nuTsCpLhYbKljoyS-txg";

    // index to identify current nav menu item
    public static int navItemIndex = 0;

    // tags used to attach the fragments
    private static final String TAG_HOME = "home";
    private static final String TAG_PHOTOS = "photos";
    private static final String TAG_MOVIES = "movies";
    private static final String TAG_NOTIFICATIONS = "notifications";
    private static final String TAG_SETTINGS = "settings";
    public static String CURRENT_TAG = TAG_HOME;

    // toolbar titles respected to selected nav menu item
    private String[] activityTitles;

    // flag to load home fragment when user presses back key
    private boolean shouldLoadHomeFragOnBackPress = true;
    private Handler mHandler;

    private static final String TAG = "MainActivity";
    String folder_main = "DailyFoto";
//    private static final int REQUEST_TAKE_PHOTO = 1;
//    final private int MY_PERMISSIONS_REQUEST_WRITE_EXTERNAL_STORAGE = 0;
//    Button btnShow, btnClear, btnTakePicture, btnDebug, btnDebug2;
//    NotificationManager manager;
//    String mCurrentPhotoPath;
//    ImageView img_view;
//    Uri photoURI;
    Context context = this;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Log.d(TAG, "onCreate");
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main2);

        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        //getSupportActionBar().setDisplayShowTitleEnabled(false);

        // Method to Display an Icon in the toolbar
//        getSupportActionBar().setDisplayShowHomeEnabled(true);
//        getSupportActionBar().setLogo(R.mipmap.ic_launcher);
//        getSupportActionBar().setDisplayUseLogoEnabled(true);

        mHandler = new Handler();

        drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        navigationView = (NavigationView) findViewById(R.id.nav_view);
        fab = (FloatingActionButton) findViewById(R.id.fab);

        // Navigation view header
        navHeader = navigationView.getHeaderView(0);
        txtName = (TextView) navHeader.findViewById(R.id.name);
        txtWebsite = (TextView) navHeader.findViewById(R.id.website);
        imgNavHeaderBg = (ImageView) navHeader.findViewById(R.id.img_header_bg);
        imgProfile = (ImageView) navHeader.findViewById(R.id.img_profile);

        // load toolbar titles from string resources
        activityTitles = getResources().getStringArray(R.array.nav_item_activity_titles);


        // load nav menu header data
        loadNavHeader();

        // initializing navigation menu
        setUpNavigationView();

        if (savedInstanceState == null) {
            navItemIndex = 0;
            CURRENT_TAG = TAG_HOME;
            loadHomeFragment();
        }

        initialise();
//        checkFolder();
//
//        Toolbar myToolbar = (Toolbar) findViewById(R.id.my_toolbar);
//        setSupportActionBar(myToolbar);
//
//        if (findViewById(R.id.fragment_container) != null) {
//
//            // However, if we're being restored from a previous state,
//            // then we don't need to do anything and should return or else
//            // we could end up with overlapping fragments.
//            if (savedInstanceState != null) {
//                return;
//            }
//
//            // Create a new Fragment to be placed in the activity layout
//            ShowImageFragment imageFragment = new ShowImageFragment();
//            GalleryFragment galleryFragment = new GalleryFragment();
//
//            // In case this activity was started with special instructions from an
//            // Intent, pass the Intent's extras to the fragment as arguments
//            imageFragment.setArguments(getIntent().getExtras());
//
//            // Add the fragment to the 'fragment_container' FrameLayout
//            getSupportFragmentManager().beginTransaction()
//                    .add(R.id.fragment_container, imageFragment).commit();
//        }
//
//        Log.d(TAG, "checks done");
//
//        manager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
//        Log.d(TAG, "onClickListeners Created");
    }

    private void loadNavHeader() {
        // name, website
        txtName.setText("Ravi Tamada");
        txtWebsite.setText("www.androidhive.info");

        // loading header background image
        Glide.with(this).load(urlNavHeaderBg)
                .crossFade()
                .diskCacheStrategy(DiskCacheStrategy.ALL)
                .into(imgNavHeaderBg);

        // Loading profile image
        Glide.with(this).load(urlProfileImg)
                .crossFade()
                .thumbnail(0.5f)
                .bitmapTransform(new CircleTransform(this))
                .diskCacheStrategy(DiskCacheStrategy.ALL)
                .into(imgProfile);

        // showing dot next to notifications label
        navigationView.getMenu().getItem(3).setActionView(R.layout.menu_dot);
    }

    /***
     * Returns respected fragment that user
     * selected from navigation menu
     */
    private void loadHomeFragment() {
        // selecting appropriate nav menu item
        selectNavMenu();

        // set toolbar title
        setToolbarTitle();

        // if user select the current navigation menu again, don't do anything
        // just close the navigation drawer
        if (getSupportFragmentManager().findFragmentByTag(CURRENT_TAG) != null) {
            drawer.closeDrawers();

            // show or hide the fab button
            toggleFab();
            return;
        }

        // Sometimes, when fragment has huge data, screen seems hanging
        // when switching between navigation menus
        // So using runnable, the fragment is loaded with cross fade effect
        // This effect can be seen in GMail app
        Runnable mPendingRunnable = new Runnable() {
            @Override
            public void run() {
                // update the main content by replacing fragments
                Fragment fragment = getHomeFragment();
                FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
                fragmentTransaction.setCustomAnimations(android.R.anim.fade_in,
                        android.R.anim.fade_out);
                fragmentTransaction.replace(R.id.frame, fragment, CURRENT_TAG);
                fragmentTransaction.commitAllowingStateLoss();
            }
        };

        // If mPendingRunnable is not null, then add to the message queue
        if (mPendingRunnable != null) {
            mHandler.post(mPendingRunnable);
        }

        // show or hide the fab button
        toggleFab();

        //Closing drawer on item click
        drawer.closeDrawers();

        // refresh toolbar menu
        invalidateOptionsMenu();
    }

    private Fragment getHomeFragment() {
        switch (navItemIndex) {
            case 0:
                // home
                HomeFragment homeFragment = new HomeFragment();
                return homeFragment;
            case 1:
                // photos
                PhotosFragment photosFragment = new PhotosFragment();
                return photosFragment;
            case 2:
                // movies fragment
                MoviesFragment moviesFragment = new MoviesFragment();
                return moviesFragment;
            case 3:
                // notifications fragment
                NotificationsFragment notificationsFragment = new NotificationsFragment();
                return notificationsFragment;

            case 4:
                // settings fragment
                SettingsFragment settingsFragment = new SettingsFragment();
                return settingsFragment;
            default:
                return new HomeFragment();
        }
    }

    private void setToolbarTitle() {
        getSupportActionBar().setTitle(activityTitles[navItemIndex]);
    }

    private void selectNavMenu() {
        navigationView.getMenu().getItem(navItemIndex).setChecked(true);
    }

    private void setUpNavigationView() {
        //Setting Navigation View Item Selected Listener to handle the item click of the navigation menu
        navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {

            // This method will trigger on item Click of navigation menu
            @Override
            public boolean onNavigationItemSelected(MenuItem menuItem) {

                //Check to see which item was being clicked and perform appropriate action
                switch (menuItem.getItemId()) {
                    //Replacing the main content with ContentFragment Which is our Inbox View;
                    case R.id.nav_home:
                        navItemIndex = 0;
                        CURRENT_TAG = TAG_HOME;
                        break;
                    case R.id.nav_photos:
                        navItemIndex = 1;
                        CURRENT_TAG = TAG_PHOTOS;
                        break;
                    case R.id.nav_movies:
                        navItemIndex = 2;
                        CURRENT_TAG = TAG_MOVIES;
                        break;
                    case R.id.nav_notifications:
                        navItemIndex = 3;
                        CURRENT_TAG = TAG_NOTIFICATIONS;
                        break;
                    case R.id.nav_settings:
                        navItemIndex = 4;
                        CURRENT_TAG = TAG_SETTINGS;
                        break;
                    case R.id.nav_about_us:
                        // launch new intent instead of loading fragment
                        startActivity(new Intent(MainActivity.this, AboutUsActivity.class));
                        drawer.closeDrawers();
                        return true;
                    case R.id.nav_privacy_policy:
                        // launch new intent instead of loading fragment
                        startActivity(new Intent(MainActivity.this, PrivacyPolicyActivity.class));
                        drawer.closeDrawers();
                        return true;
                    default:
                        navItemIndex = 0;
                }

                //Checking if the item is in checked state or not, if not make it in checked state
                if (menuItem.isChecked()) {
                    menuItem.setChecked(false);
                } else {
                    menuItem.setChecked(true);
                }
                menuItem.setChecked(true);

                loadHomeFragment();

                return true;
            }
        });


        ActionBarDrawerToggle actionBarDrawerToggle = new ActionBarDrawerToggle(this, drawer, toolbar, R.string.openDrawer, R.string.closeDrawer) {

            @Override
            public void onDrawerClosed(View drawerView) {
                // Code here will be triggered once the drawer closes as we dont want anything to happen so we leave this blank
                super.onDrawerClosed(drawerView);
            }

            @Override
            public void onDrawerOpened(View drawerView) {
                // Code here will be triggered once the drawer open as we dont want anything to happen so we leave this blank
                super.onDrawerOpened(drawerView);
            }
        };

        //Setting the actionbarToggle to drawer layout
        drawer.setDrawerListener(actionBarDrawerToggle);

        //calling sync state is necessary or else your hamburger icon wont show up
        actionBarDrawerToggle.syncState();
    }

    @Override
    public void onBackPressed() {
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawers();
            return;
        }

        // This code loads home fragment when back key is pressed
        // when user is in other fragment than home
        if (shouldLoadHomeFragOnBackPress) {
            // checking if user is on other navigation menu
            // rather than home
            if (navItemIndex != 0) {
                navItemIndex = 0;
                CURRENT_TAG = TAG_HOME;
                loadHomeFragment();
                return;
            }
        }

        super.onBackPressed();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.

        // show menu only when home fragment is selected
        if (navItemIndex == 0) {
            getMenuInflater().inflate(R.menu.main, menu);
        }

        // when fragment is notifications, load the menu created for notifications
        if (navItemIndex == 3) {
            getMenuInflater().inflate(R.menu.notifications, menu);
        }
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_logout) {
            Toast.makeText(getApplicationContext(), "Logout user!", Toast.LENGTH_LONG).show();
            return true;
        }

        // user is in notifications fragment
        // and selected 'Mark all as Read'
        if (id == R.id.action_mark_all_read) {
            Toast.makeText(getApplicationContext(), "All notifications marked as read!", Toast.LENGTH_LONG).show();
        }

        // user is in notifications fragment
        // and selected 'Clear All'
        if (id == R.id.action_clear_notifications) {
            Toast.makeText(getApplicationContext(), "Clear all notifications!", Toast.LENGTH_LONG).show();
        }

        return super.onOptionsItemSelected(item);
    }

    // show or hide the fab
    private void toggleFab() {
        if (navItemIndex == 0)
            fab.show();
        else
            fab.hide();
    }

    @Override
    public void onResume() {
        super.onResume();  // Always call the superclass method first
    }

    @Override
    public void onStart() {
        super.onResume();  // Always call the superclass method first
        //checkPermissions();
    }

    @Override
    public void onPause() {
        super.onPause();  // Always call the superclass method first
    }

//    @Override
//    public boolean onCreateOptionsMenu(Menu menu) {
//        // Inflate the menu; this adds items to the action bar if it is present.
//        getMenuInflater().inflate(R.menu.main_menu, menu);
//        return true;
//    }


//    private void dispatchTakePictureIntent() {
//        Log.d(TAG, "dispatchTakePictureIntent");
//        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
//        // Ensure that there's a camera activity to handle the intent
//        if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
//            // Create the File where the photo should go
//            File photoFile = null;
//            try {
//                photoFile = createImageFile();
//            } catch (IOException ex) {
//                // Error occurred while creating the File
//                Log.d(TAG, "create ImageFile failed");
//            }
//            // Continue only if the File was successfully created
//            if (photoFile != null) {
//                Log.d(TAG, "we have a photo file");
//                photoURI = FileProvider.getUriForFile(this,
//                        "com.example.android.fileprovider",
//                        photoFile);
//                Log.d(TAG, "disp: " + photoURI.getPath());
//                takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI);
//                startActivityForResult(takePictureIntent, REQUEST_TAKE_PHOTO);
//
//                // add picture to gallery
//                galleryAddPic();
//            }
//        }
//    }
//
//    private File createImageFile() throws IOException {
//        Log.d(TAG, "createImageFile");
//
//        // Create an image file name
//        // String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
//        String timeStamp = new SimpleDateFormat("yyyy_MM_dd").format(new Date());
//        String imageFileName = "Image_" + timeStamp + "_";
//
//        //File storageDir = new File(Environment.getExternalStorageDirectory(), folder_main);
//        File storageDir = getExternalFilesDir(Environment.DIRECTORY_PICTURES);
//        Log.d(TAG, "storageDir:" + storageDir);
//
//        File image = new File(storageDir, imageFileName + ".jpg");
//
//
//        // in case I want temporary file (adds unique suffix)
//
//        /*File image = File.createTempFile(
//                imageFileName,  *//* prefix *//*
//                ".jpg",         *//* suffix *//*
//                storageDir      *//* directory *//*
//        );*/
//
//        // Save a file: path for use with ACTION_VIEW intents
//        mCurrentPhotoPath = "file:" + image.getAbsolutePath();
//        return image;
//    }
//
//
//    private void checkFolder() {
//        File f = new File(Environment.getExternalStorageDirectory(), folder_main);
//        if (!f.exists()) {
//            f.mkdirs();
//        }
//    }

//    private void checkPermissions() {
//        //Log.d(TAG, "checkPermissions");
//        // Assume thisActivity is the current activity
//        ActivityCompat.requestPermissions(MainActivity.this,
//                new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},
//                MY_PERMISSIONS_REQUEST_WRITE_EXTERNAL_STORAGE);
//
//        if (!Environment.getExternalStorageDirectory().canWrite()){
//            //btnTakePicture.setBackgroundColor(Color.RED);
//            btnTakePicture.setEnabled(false);
//            btnTakePicture.setText(R.string.grant_storage_permission);
//        }
//        else{
//            btnTakePicture.setEnabled(true);
//            btnTakePicture.setText(R.string.take_picture);
//        }
//    }
//
//    @Override
//    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
//        switch (requestCode) {
//            case MY_PERMISSIONS_REQUEST_WRITE_EXTERNAL_STORAGE: {
//
//                // If request is cancelled, the result arrays are empty.
//                if (grantResults.length > 0
//                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
//                        Log.d(TAG, "permission was granted");
//                    // permission was granted, yay! Do the
//                    // contacts-related task you need to do.
//                } else {
//
//                    // permission denied, boo! Disable the
//                    // functionality that depends on this permission.
//                    Toast.makeText(MainActivity.this, "Permission denied to read your External storage", Toast.LENGTH_SHORT).show();
//                    Toast.makeText(MainActivity.this, "This app does not work without these permissions", Toast.LENGTH_SHORT).show();
//
//
//                    btnTakePicture.setEnabled(false);
//                    btnTakePicture.setText(R.string.grant_storage_permission);
//                }
//            }
//
//            // other 'case' lines to check for other
//            // permissions this app might request
//        }
//    }
//
//
//
//
//
//    public class SingleMediaScanner implements MediaScannerConnection.MediaScannerConnectionClient {
//
//        private MediaScannerConnection mMs;
//        private File mFile;
//
//        public SingleMediaScanner(Context context, File f) {
//            mFile = f;
//            mMs = new MediaScannerConnection(context, this);
//            mMs.connect();
//        }
//
//        public void onMediaScannerConnected() {
//            mMs.scanFile(mFile.getAbsolutePath(), null);
//        }
//
//        public void onScanCompleted(String path, Uri uri) {
//            Intent intent = new Intent(Intent.ACTION_VIEW);
//            intent.setData(uri);
//            startActivity(intent);
//            mMs.disconnect();
//        }
//
//    }
//
//
//    @Override
//    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
//        Log.d(TAG, "onActivityResult");
//        if (requestCode == REQUEST_TAKE_PHOTO && resultCode == RESULT_OK) {
//            //Bundle extras = data.getExtras();
//            //Bitmap imageBitmap = (Bitmap) extras.get("data");
//            //mImageView.setImageBitmap(imageBitmap);
//
//            String yourFilePath = context.getFilesDir() + "/" + "Pictures/JPEG_20161018_.jpg";
//            Log.d(TAG, "context_dir: " + context.getFilesDir());
//            File imgFile = new File( yourFilePath );
//
//            //File imgFile = new  File("Android/data/com.androiddev.julianeiler.dailyfoto/files/Pictures/JPEG_20161018_.jpg");
//            if(imgFile.exists()){
//                Log.d(TAG, "imageFileexist");
//                Bitmap myBitmap = BitmapFactory.decodeFile(imgFile.getAbsolutePath());
//                //mImageView.setImageBitmap(myBitmap);
//
//            }
//
//            Log.d(TAG, "res: " + photoURI.getPath());
//
//            Bitmap myImg = BitmapFactory.decodeFile("Android/data/com.androiddev.julianeiler.dailyfoto/files/Pictures/JPEG_20161018_.jpg");
//            //mImageView.setImageBitmap(myImg);
//        }
//
//        Log.d(TAG, "onActivityResult...done");
//    }
//
//
//    private String saveToInternalStorage(Bitmap bitmapImage){
//        Log.d(TAG, "saveToInternalStorage");
//        ContextWrapper cw = new ContextWrapper(getApplicationContext());
//        // path to /data/data/yourapp/app_data/imageDir
//        File directory = cw.getDir("imageDir", Context.MODE_PRIVATE);
//        // Create imageDir
//        File mypath=new File(directory,"profile.jpg");
//
//        FileOutputStream fos = null;
//        try {
//            fos = new FileOutputStream(mypath);
//            // Use the compress method on the BitMap object to write image to the OutputStream
//            bitmapImage.compress(Bitmap.CompressFormat.PNG, 100, fos);
//        } catch (Exception e) {
//            e.printStackTrace();
//        } finally {
//            try {
//                assert fos != null;
//                fos.close();
//            } catch (IOException e) {
//                e.printStackTrace();
//            }
//        }
//        return directory.getAbsolutePath();
//    }
//
//    private void displayPicture() {
//        String yourFilePath = context.getFilesDir() + "/" + "Pictures/JPEG_20161106_.jpg";
//        Log.d(TAG, "context_dir: " + context.getFilesDir());
//        File imgFile = new File( yourFilePath );
//
//        //File imgFile = new  File("Android/data/com.androiddev.julianeiler.dailyfoto/files/Pictures/JPEG_20161018_.jpg");
//        if(imgFile.exists()){
//            Log.d(TAG, "imageFileexist");
//            int resID = getResources().getIdentifier(yourFilePath, null, getPackageName());
//            //mGridView.setImageResource(resID);
//
//            //Bitmap myBitmap = BitmapFactory.decodeFile(imgFile.getAbsolutePath());
//            //mImageView.setImageBitmap(myBitmap);
//
//        }
//
//        Bitmap myImg = BitmapFactory.decodeFile("Android/data/com.androiddev.julianeiler.dailyfoto/files/Pictures/JPEG_20161018_.jpg");
//        // mImageView.setImageBitmap(myImg);
//
//    }
//
//
//    private void galleryAddPic() {
//        Log.d(TAG, "galleryAddPic");
//        Intent mediaScanIntent = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
//        File f = new File(mCurrentPhotoPath);
//        Uri contentUri = Uri.fromFile(f);
//        mediaScanIntent.setData(contentUri);
//        this.sendBroadcast(mediaScanIntent);
//    }
//
//    /*
//    Initialize all elements in XML
//     */
    private void initialise() {
        Log.d(TAG, "initialize");
//        btnShow = (Button) findViewById(R.id.btnShowNotification);
//        btnClear = (Button) findViewById(R.id.btnClearNotification);
//        btnTakePicture = (Button) findViewById(R.id.btnTakePicture);
//        btnDebug = (Button) findViewById(R.id.btnDebug);
//        btnDebug2 = (Button) findViewById(R.id.btnDebug2);
//
//        btnShow.setOnClickListener(this);
//        btnClear.setOnClickListener(this);
//        btnTakePicture.setOnClickListener(this);
//        btnDebug.setOnClickListener(this);
//        btnDebug2.setOnClickListener(this);


        fab.setOnClickListener(this);
    }
//
//    /*
//    Handle Buttons
//     */
    @Override
    public void onClick(View v) {
        switch (v.getId())
        {
            case R.id.fab:
                Snackbar.make(v, "Replace with your own action", Snackbar.LENGTH_LONG)
                            .setAction("Action", null).show();

            break;


//            case R.id.btnShowNotification:
//                Log.d(TAG, "btnShowNotification");
//                Toast.makeText(MainActivity.this, "Button Clicked: btnShowNotification", Toast.LENGTH_SHORT).show();
//
//                NotificationCompat.Builder mBuilder =
//                        new NotificationCompat.Builder(MainActivity.this)
//                                .setLargeIcon(BitmapFactory.decodeResource(getResources(), R.drawable.selfie_icon1))
//                                .setSmallIcon(R.drawable.selfie1)
//                                .setContentTitle("Take a fucking selfie")
//                                .setContentText("NOW!");
//                NotificationManager mNotificationManager =
//                        (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
//                // mId allows you to update the notification later on.
//                mNotificationManager.notify(11, mBuilder.build());
//                break;
//
//            case R.id.btnClearNotification:
//                Log.d(TAG, "btnClearNotification");
//                Toast.makeText(MainActivity.this, "Button Clicked: btnClearNotification", Toast.LENGTH_SHORT).show();
//                manager.cancel(11);
//                break;
//
//            case R.id.btnTakePicture:
//                Log.d(TAG, "btnTakePicture");
//                Toast.makeText(MainActivity.this, "Button Clicked: btnTakePicture", Toast.LENGTH_SHORT).show();
//
//                dispatchTakePictureIntent();
//                //btnTakePicture.setText("Retake Picture");
//                break;
//
//            case R.id.btnDebug:
//                Log.d(TAG, "btnDebug");
//                Toast.makeText(MainActivity.this, "Button Clicked: btnDebug", Toast.LENGTH_SHORT).show();
//
////                File folder = getExternalFilesDir(Environment.DIRECTORY_PICTURES);
////                allFiles = folder.listFiles();
////                new SingleMediaScanner(MainActivity.this, allFiles[0]);
//                if (findViewById(R.id.fragment_container) != null) {
//
//                    // Create a new Fragment to be placed in the activity layout
//                    GalleryFragment galleryFragment = new GalleryFragment();
//                    galleryFragment.setArguments(getIntent().getExtras());
//
//                    FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
//                    transaction.replace(R.id.fragment_container, galleryFragment);
//                    transaction.addToBackStack(null);
//                    transaction.commit();
//                }
//                break;
//
//            case R.id.btnDebug2:
//                Log.d(TAG, "btnDebug2");
//                Toast.makeText(MainActivity.this, "Button Clicked: btnDebug2", Toast.LENGTH_SHORT).show();
//
//                if (findViewById(R.id.fragment_container) != null) {
//
//                    // Create a new Fragment to be placed in the activity layout
//                    ShowImageFragment imageFragment = new ShowImageFragment();
//
//                    FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
//                    transaction.replace(R.id.fragment_container, imageFragment);
//                    transaction.addToBackStack(null);
//                    transaction.commit();
//                }
//                break;
        }
    }

//    @Override
//    public boolean onOptionsItemSelected(MenuItem item) {
//        switch (item.getItemId()) {
//            case R.id.action_settings:
//                Toast.makeText(MainActivity.this, "Button Clicked: action_settings", Toast.LENGTH_SHORT).show();
//
//                //img_view.setImageResource(R.drawable.selfie);
//                return true;
//
//            case R.id.action_favorite:
//                Toast.makeText(MainActivity.this, "Button Clicked: action_favorite", Toast.LENGTH_SHORT).show();
//
//                // as a favorite...
//                return true;
//
//            case R.id.action_search:
//                Toast.makeText(MainActivity.this, "Button Clicked: action_search", Toast.LENGTH_SHORT).show();
//
//                // as a favorite...
//                return true;
//
//            default:
//                // If we got here, the user's action was not recognized.
//                // Invoke the superclass to handle it.
//                return super.onOptionsItemSelected(item);
//
//        }
//    }

}