package com.androiddev.julianeiler.dailyfoto;

import android.app.Activity;
import android.support.v4.app.Fragment;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;

import java.io.File;
import java.util.ArrayList;

/**
 * Activity to Display single image
 *
 * Created by Julian Eiler on 18.10.2016.
 */

public class ShowImageFragment extends Fragment {
    private static final String TAG = "ShowImageFragment";
    ArrayList<String> f = new ArrayList<>();// list of file paths
    File[] listFile;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.display_single_image, container, false);

        getFromSdcard();
        ImageView imageview = (ImageView) v.findViewById(R.id.PhoneImageView);

        Bitmap myBitmap = BitmapFactory.decodeFile(f.get(1));

        //File imgFile = new  File("/storage/emulated/0/Android/data/com.androiddev.julianeiler.dailyfoto/files/Pictures/Image_2016_11_12_.jpg");
        //Bitmap myBitmap = BitmapFactory.decodeFile(imgFile.getAbsolutePath());
        int nh = (int) ( myBitmap.getHeight() * (512.0 / myBitmap.getWidth()) );
        Bitmap scaled = Bitmap.createScaledBitmap(myBitmap, 512, nh, true);

        imageview.setImageBitmap(scaled);

        return v;
    }


    public void getFromSdcard()
    {
        File file = getActivity().getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        //File file= new File(android.os.Environment.getExternalStorageDirectory(),"MapleBear");
        Log.d(TAG, "file:" + file);

        assert file != null;
        if (file.isDirectory())
        {
            Log.d(TAG, "file is dir");
            listFile = file.listFiles();

            for (File aListFile : listFile) {
                Log.d(TAG, "add stuff");
                f.add(aListFile.getAbsolutePath());
            }
        }
    }
}
