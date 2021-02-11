package com.example.socialqs.activities.prelogin.fragments;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.example.socialqs.R;
import com.example.socialqs.activities.prelogin.PreLoginActivity;
import com.example.socialqs.utils.InputValidator;
import com.example.socialqs.utils.helperInterfaces.ErrorRemoveInterface;
import com.google.android.material.textfield.TextInputEditText;

public class ResetPasswodFragment extends Fragment {

    private InputValidator validator;
    private String email;
    private boolean isResetPassword;

    public ResetPasswodFragment() {
        // Required empty public constructor
    }

    public static ResetPasswodFragment newInstance() {
        ResetPasswodFragment fragment = new ResetPasswodFragment();
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        validator = new InputValidator();
        Bundle bundle = this.getArguments();
        if (bundle != null){
            this.isResetPassword = bundle.getBoolean("isResetPassword");
            this.email = bundle.getString("email");
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        ((PreLoginActivity)getActivity()).updateActionBarBack(true);
        ((PreLoginActivity)getActivity()).setActionBarTitle(getText(R.string.update_password).toString(), "#ffffff", R.color.black);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_reset_passwod, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        TextInputEditText oldPassField = view.findViewById(R.id.oldPasswordEditTextField);
        oldPassField.addTextChangedListener(new ErrorRemoveInterface() {
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) { oldPassField.setError(null); }
        });

        TextInputEditText newPassField = view.findViewById(R.id.newPasswordEditTextField);
        newPassField.addTextChangedListener(new ErrorRemoveInterface() {
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) { newPassField.setError(null); }
        });

        TextInputEditText confirmPassField = view.findViewById(R.id.confirmNewPasswordEditTextField);
        confirmPassField.addTextChangedListener(new ErrorRemoveInterface() {
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) { confirmPassField.setError(null); }
        });

        Button forgotPassConfirmButton = view.findViewById(R.id.forgotPassConfirmButton);
        forgotPassConfirmButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String oldPass = oldPassField.getText().toString().trim();
                String newPass = newPassField.getText().toString().trim();
                String confirmPass = confirmPassField.getText().toString().trim();

                if (!isResetPassword) {
                    if (!validator.isValidPassword(oldPass)) {
                        //error
                        oldPassField.setError(getString(R.string.invalid_email));
                        return;
                    }
                }

                if (!validator.isValidPassword(newPass)){
                    //error
                    newPassField.setError(getString(R.string.invalid_email));
                    return;
                }

                if (!validator.isValidPassword(confirmPass)){
                    //error
                    confirmPassField.setError(getString(R.string.invalid_email));
                    return;
                }

                if (!newPass.equalsIgnoreCase(confirmPass)){
                    newPassField.setError(getString(R.string.password_not_equal));
                    return;
                }

                //TODO: Update password api
                updateProgress(View.VISIBLE);
                
            }
        });
    }

    private void updateProgress(int visibility){
        ((PreLoginActivity) getActivity()).updateProgress(visibility);
    }
}