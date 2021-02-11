package com.example.socialqs.activities.prelogin.fragments;

import android.graphics.Typeface;
import android.opengl.Visibility;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.example.socialqs.R;
import com.example.socialqs.activities.prelogin.PreLoginActivity;
import com.example.socialqs.utils.InputValidator;
import com.example.socialqs.utils.helperInterfaces.ErrorRemoveInterface;
import com.example.socialqs.utils.helperInterfaces.NetworkingClosure;
import com.example.socialqs.utils.networking.NetworkHandler;
import com.google.android.material.textfield.TextInputEditText;

import org.json.JSONException;
import org.json.JSONObject;

import javax.xml.validation.Validator;

public class ForgotPasswordFragment extends Fragment {

    private InputValidator validator;

    public ForgotPasswordFragment() {
        // Required empty public constructor
    }

    public static ForgotPasswordFragment newInstance() {
        ForgotPasswordFragment fragment = new ForgotPasswordFragment();
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        validator = new InputValidator();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_forgot_password, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        TextView forgotPassword = view.findViewById(R.id.forgotPasswordTitle);
        forgotPassword.setTypeface(null, Typeface.BOLD);

        TextInputEditText emailField = view.findViewById(R.id.forgotPassEmailTextField);
        emailField.addTextChangedListener(new ErrorRemoveInterface() {
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) { emailField.setError(null); }
        });

        Button confirmButton = view.findViewById(R.id.forgotConfirmButton);
        confirmButton.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                String email = emailField.getText().toString().trim();
                if (!validator.isValidEmail(email)){
                    //error
                    emailField.setError(getString(R.string.invalid_email));
                    return;
                }

                updateProgress(View.VISIBLE);
                NetworkHandler.getInstance().forgotPassword(email, new NetworkingClosure() {
                    @Override
                    public void completion(JSONObject object, String message) {
                        if (object == null){
                            Toast.makeText(getActivity(), (message == null) ? getText(R.string.something_wrong): message, Toast.LENGTH_LONG).show();
                        }else{
                            FragmentManager manager = getActivity().getSupportFragmentManager();

                            Bundle arguments = new Bundle();
                            arguments.putBoolean("isForgot", true);
                            arguments.putString("email", email);

                            manager.beginTransaction()
                                    .setCustomAnimations(R.anim.slide_in, R.anim.fade_out, R.anim.fade_in, R.anim.slide_out)
                                    .replace(R.id.preLoginFragmentContainer, VerifyEmailFragment.class, arguments)
                                    .setReorderingAllowed(true)
                                    .addToBackStack(null)
                                    .commit();
                        }
                    }
                });
            }
        });
    }

    private void updateProgress(int visibility){
        ((PreLoginActivity) getActivity()).updateProgress(visibility);
    }
}