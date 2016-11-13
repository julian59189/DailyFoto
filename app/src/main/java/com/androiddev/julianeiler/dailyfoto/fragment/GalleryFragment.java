package com.androiddev.julianeiler.dailyfoto.fragment;

import android.app.Activity;
import android.support.v4.app.Fragment;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;

import com.androiddev.julianeiler.dailyfoto.R;

import java.io.File;
import java.util.ArrayList;

/**
 * Gallery Intent
 *
 * Created by Julian Eiler on 18.10.2016.
 */

public class GalleryFragment extends Fragment {
    private static final String TAG = "CustomGalleryActivity";
    private int count;
    private Bitmap[] thumbnails;
    private boolean[] thumbnailsselection;
    private String[] arrPath;
    private ImageAdapter imageAdapter;
    ArrayList<String> f = new ArrayList<>();// list of file paths
    File[] listFile;



    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.display_gallery, container, false);

        getFromSdcard();
        GridView imagegrid = (GridView) v.findViewById(R.id.PhoneImageGrid);
        imageAdapter = new ImageAdapter();
        imagegrid.setAdapter(imageAdapter);

        return v;
    }


    public void getFromSdcard()
    {
        File file = getActivity().getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        Log.d(TAG, "storageDir external:" + file);
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

    public class ImageAdapter extends BaseAdapter {
        private LayoutInflater mInflater;

        ImageAdapter() {
            mInflater = (LayoutInflater) getActivity().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        }

        public int getCount() {
            return f.size();
        }

        public Object getItem(int position) {
            return position;
        }

        public long getItemId(int position) {
            return position;
        }

        public View getView(int position, View convertView, ViewGroup parent) {
            ViewHolder holder;
            if (convertView == null) {
                holder = new ViewHolder();
                convertView = mInflater.inflate(
                        R.layout.gallery_item, null);
                holder.imageview = (ImageView) convertView.findViewById(R.id.thumbImage);

                convertView.setTag(holder);
            }
            else {
                holder = (ViewHolder) convertView.getTag();
            }

            Bitmap myBitmap = BitmapFactory.decodeFile(f.get(position));
            int nh = (int) ( myBitmap.getHeight() * (512.0 / myBitmap.getWidth()) );
            Bitmap scaled = Bitmap.createScaledBitmap(myBitmap, 512, nh, true);
            holder.imageview.setImageBitmap(scaled);
            return convertView;
        }
    }
    class ViewHolder {
        ImageView imageview;
    }
}