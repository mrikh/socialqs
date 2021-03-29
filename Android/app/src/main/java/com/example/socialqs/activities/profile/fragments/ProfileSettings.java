package com.example.socialqs.activities.profile.fragments;

import android.content.DialogInterface;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.example.socialqs.R;

public class ProfileSettings extends Fragment {

    ImageView disable, enable, back;
    ConstraintLayout toggler;
    LinearLayout updatePassword, logOut;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v =  inflater.inflate(R.layout.fragment_profile_settings, container, false);

        disable = v.findViewById(R.id.notificationDisabled);
        enable = v.findViewById(R.id.notificationEnabled);
        back = v.findViewById(R.id.previousFragment);

        toggler = v.findViewById(R.id.toggleButton);
        //blockList = v.findViewById(R.id.blockListLayout);
        updatePassword = v.findViewById(R.id.updatePasswordLayout);
        logOut = v.findViewById(R.id.logOutLayout);

        //default
        enable.setVisibility(View.INVISIBLE);
        disable.setVisibility(View.VISIBLE);

        //blockedUsers.add("Test User");
        return v;
    }

    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getActivity().getSupportFragmentManager().popBackStack();
            }
        });

        toggler.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //TODO: Toggle Notification
                if (disable.getVisibility() == View.VISIBLE) {
                    disable.setVisibility(View.INVISIBLE);
                    enable.setVisibility(View.VISIBLE);
                } else {
                    enable.setVisibility(View.INVISIBLE);
                    disable.setVisibility(View.VISIBLE);
                }
            }
        });

//        blockList.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                if(blockedUsers.size() > 0) {
//                    Navigation.findNavController(v).navigate(R.id.action_profileSettings_to_blockList);
//                } else {
//                    Navigation.findNavController(v).navigate(R.id.action_profileSettings_to_emptyBlockList);
//                }
//            }
//        });

        updatePassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Navigation.findNavController(v).navigate(R.id.action_profileSettings_to_resetPasswodFragment);
                //TODO: update password
            }
        });

        logOut.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
                builder.setTitle("Success!");
                builder.setMessage("This will log you out of the application.");
                builder.setNegativeButton("No", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });
                builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        //TODO: Log out of the application
                        getActivity().finish();
                    }
                });

                AlertDialog alertDialog = builder.create();
                alertDialog.show();
            }
        });
    }
}