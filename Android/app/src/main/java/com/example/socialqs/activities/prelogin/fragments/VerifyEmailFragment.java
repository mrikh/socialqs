package com.example.socialqs.activities.prelogin.fragments;

import android.graphics.Paint;
import android.graphics.Typeface;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.res.ResourcesCompat;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.chaos.view.PinView;
import com.example.socialqs.R;
import com.example.socialqs.activities.prelogin.PreLoginActivity;
import com.example.socialqs.models.UserModel;
import com.example.socialqs.utils.helperInterfaces.NetworkingClosure;
import com.example.socialqs.utils.networking.NetworkHandler;
import com.github.ybq.android.spinkit.sprite.Sprite;
import com.github.ybq.android.spinkit.style.DoubleBounce;

import org.json.JSONObject;

public class VerifyEmailFragment extends Fragment {

    private ProgressBar progressBar;

    public VerifyEmailFragment() {
        // Required empty public constructor
    }

    public static VerifyEmailFragment newInstance(String param1, String param2) {
        VerifyEmailFragment fragment = new VerifyEmailFragment();
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public void onResume() {
        super.onResume();
        ((PreLoginActivity)getActivity()).updateActionBarBack(true);
        ((PreLoginActivity)getActivity()).setActionBarTitle(getString(R.string.verify_email), "#ffffff", R.color.black);
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_verify_email, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        progressBar = view.findViewById(R.id.progress);
        Sprite doubleBounce = new DoubleBounce();
        progressBar.setIndeterminateDrawable(doubleBounce);
        progressBar.setVisibility(View.INVISIBLE);

        TextView laterTextView = view.findViewById(R.id.skipTextView);
        laterTextView.setTypeface(null, Typeface.BOLD);
        laterTextView.setPaintFlags(Paint.UNDERLINE_TEXT_FLAG);

        laterTextView.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                //TODO: Go to landing screen
            }
        });

        TextView resend = view.findViewById(R.id.resendTextView);
        resend.setTypeface(null, Typeface.BOLD);
        resend.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                //resend api
                progressBar.setVisibility(View.VISIBLE);
                NetworkHandler.getInstance().resendVerification(new NetworkingClosure() {
                    @Override
                    public void completion(JSONObject object, String message) {
                        progressBar.setVisibility(View.INVISIBLE);
                        if (object == null){
                            Toast.makeText(getActivity(), (message == null) ? getText(R.string.something_wrong): message, Toast.LENGTH_LONG).show();
                        }else{
                            Toast.makeText(getActivity(), message, Toast.LENGTH_LONG).show();
                        }
                    }
                });
            }
        });

        PinView otpView = view.findViewById(R.id.otp_view);
        otpView.setTextColor(ResourcesCompat.getColor(getResources(), R.color.black, getActivity().getTheme()));
        otpView.setLineColor(ResourcesCompat.getColor(getResources(), R.color.black, getActivity().getTheme()));
        otpView.setItemCount(4);
        otpView.setCursorColor(ResourcesCompat.getColor(getResources(), R.color.black, getActivity().getTheme()));



        Button confirm = view.findViewById(R.id.verifyConfirmButton);
        confirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                progressBar.setVisibility(View.VISIBLE);
                NetworkHandler.getInstance().verifyEmail(otpView.getText().toString(), new NetworkingClosure() {
                    @Override
                    public void completion(JSONObject object, String message) {
                        progressBar.setVisibility(View.INVISIBLE);
                        if (object == null){
                            Toast.makeText(getActivity(), (message == null) ? getText(R.string.something_wrong): message, Toast.LENGTH_LONG).show();
                        }else{
                            try{
                                UserModel.current.emailVerified = true;
                                UserModel.current.saveToDefaults(getActivity());
                                Toast.makeText(getActivity(), message, Toast.LENGTH_LONG).show();
                                //TODO: Go to landing screen
                            }catch(Exception e){
                                Toast.makeText(getActivity(), e.getMessage(), Toast.LENGTH_LONG).show();
                            }
                        }
                    }
                });
            }
        });
    }
}