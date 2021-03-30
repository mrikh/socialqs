package com.example.socialqs.activities.profile;

import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.Fragment;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;
import androidx.navigation.Navigation;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.example.socialqs.R;
import com.example.socialqs.activities.home.MainMenuActivity;
import com.example.socialqs.activities.prelogin.PreLoginActivity;
import com.example.socialqs.models.UserModel;
import com.example.socialqs.utils.Utilities;

public class SettingsFragment extends Fragment {

    ImageView disable, enable, back;
    ConstraintLayout toggler;
    LinearLayout logOut;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v =  inflater.inflate(R.layout.fragment_settings, container, false);

        ((MainMenuActivity)getActivity()).setActionBarTitle("Settings", "#ffffff", R.color.black);
        ((MainMenuActivity)getActivity()).updateActionBarBack(true);
        ((AppCompatActivity)getActivity()).getSupportActionBar().show();

        disable = v.findViewById(R.id.notificationDisabled);
        enable = v.findViewById(R.id.notificationEnabled);

        toggler = v.findViewById(R.id.toggleButton);
        logOut = v.findViewById(R.id.logOutLayout);

        //default
        enable.setVisibility(View.INVISIBLE);
        disable.setVisibility(View.VISIBLE);

        return v;
    }

    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

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

        logOut.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
                builder.setTitle("Success!");
                builder.setMessage(R.string.logout_message);
                builder.setNegativeButton("No", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });
                builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Utilities.getInstance().logout(getActivity().getApplicationContext());
                        UserModel.current = null;
                        Intent login = new Intent(getContext(), PreLoginActivity.class);
                        login.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                        startActivity(login);
                        /*  1. Log out (Takes you to login page)
                            2. Press back (Exits app)
                            3. Open app again (Opens main menu)
                            4. Clear task manager (Exits app)
                            5. Open app again (Opens login page)
                            */
                    }
                });

                AlertDialog alertDialog = builder.create();
                alertDialog.show();
            }
        });
    }
}