package com.example.socialqs.activities.create.fragments;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.fragment.NavHostFragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.socialqs.R;
import com.example.socialqs.activities.create.CreateActivity;
import com.example.socialqs.adapters.RecyclerViewAdapter;
import com.example.socialqs.constant.Constant;
import com.example.socialqs.models.UserModel;
import com.example.socialqs.utils.FilePath;
import com.example.socialqs.utils.StorageUtil;
import com.example.socialqs.utils.Utilities;
import com.example.socialqs.utils.helperInterfaces.VideoUploadCompletion;

import java.io.File;
import java.util.ArrayList;

public class VideoList extends Fragment implements RecyclerViewAdapter.ItemClickListener {

    private RecyclerView recyclerView;
    private RecyclerViewAdapter recyclerViewAdapter;
    private Uri fileUri;

    private ArrayList<File> allMediaList = new ArrayList<>();

    private File storage;
    private String[] storagePaths;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_video_list, container, false);
        Context context = getContext();
        
        storagePaths = StorageUtil.getStorageDirectories(getContext());

        for (String path : storagePaths) {
            storage = new File(path);
            load_Directory_Files(storage);
        }

        if (ContextCompat.checkSelfPermission(getActivity(),
                Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            requestPermissions(new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, Constant.FILE_ACCESS_PERMISSION);
        }

        recyclerView = view.findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(context));

        recyclerView.setHasFixedSize(true);
        recyclerView.setItemViewCacheSize(20);
        recyclerView.setDrawingCacheEnabled(true);
        recyclerView.setDrawingCacheQuality(View.DRAWING_CACHE_QUALITY_HIGH);
        recyclerView.setNestedScrollingEnabled(false);

        recyclerViewAdapter = new RecyclerViewAdapter(context, allMediaList);
        recyclerView.setAdapter(recyclerViewAdapter);
        recyclerViewAdapter.setClickListener(new RecyclerViewAdapter.ItemClickListener() {
            @Override
            public void onItemClick(View view, int position, Context context) {
                String path = Uri.fromFile(allMediaList.get(position)).getPath();
                ((CreateActivity)getActivity()).uploadAction(Utilities.getInstance().getFileName(), path);
            }
        });

        return view;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == Constant.FILE_ACCESS_PERMISSION) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                accessFile();
            } else {
                //TODO: pop the current fragment and go back to previous fragment
                //FragmentManager manager = getActivity().getSupportFragmentManager();
                //manager.popBackStack();
            }
        }
    }

    private void accessFile() {
        storagePaths = StorageUtil.getStorageDirectories(getContext());

        for (String path : storagePaths) {
            storage = new File(path);
            load_Directory_Files(storage);
        }

        recyclerViewAdapter.notifyDataSetChanged();
    }

    @Override
    public void onStop() {
        super.onStop();
        allMediaList.clear();
    }

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == Constant.FILE_ACCESS_PERMISSION) {
            if (resultCode == Activity.RESULT_OK) {
                fileUri = data.getData();
                getActivity().getContentResolver().takePersistableUriPermission(fileUri, Intent.FLAG_GRANT_WRITE_URI_PERMISSION
                        | Intent.FLAG_GRANT_READ_URI_PERMISSION);
            }
        }
    }

    @Override
    public void onItemClick(View view, int position, Context context) {

    }

    public void load_Directory_Files(File directory) {
        File[] fileList = directory.listFiles();
        if(fileList != null && fileList.length > 0) {
            for (int i = 0; i<fileList.length; i++) {
                if(fileList[i].isDirectory()) {
                    load_Directory_Files(fileList[i]);
                }
                else {
                    String name = fileList[i].getName().toLowerCase();
                    for(String extension : com.example.socialqs.constant.Constant.videoExtensions) {
                        if(name.endsWith(extension)) {
                            allMediaList.add(fileList[i]);
                            break;
                        }
                    }
                }
            }
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        updateActionBar();
    }

    private void updateActionBar(){
        ((CreateActivity) getActivity()).getSupportActionBar().show();
        ((CreateActivity)getActivity()).setActionBarTitle("Select Source", "#ffffff", R.color.black);
        ((CreateActivity)getActivity()).updateActionBarBack(true);
    }
}

