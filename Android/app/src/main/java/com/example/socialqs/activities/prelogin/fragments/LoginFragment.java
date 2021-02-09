package com.example.socialqs.activities.prelogin.fragments;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.example.socialqs.R;
import com.example.socialqs.activities.prelogin.PreLoginActivity;
import com.example.socialqs.utils.InputValidator;
import com.example.socialqs.utils.helperInterfaces.ErrorRemoveInterface;
import com.example.socialqs.utils.helperInterfaces.NetworkingClosure;
import com.example.socialqs.utils.networking.NetworkHandler;
import com.github.ybq.android.spinkit.sprite.Sprite;
import com.github.ybq.android.spinkit.style.DoubleBounce;
import com.google.android.material.textfield.TextInputEditText;

import org.json.JSONObject;

public class LoginFragment extends Fragment {

    private InputValidator validator;
    private ProgressBar progressBar;

    public LoginFragment() {}

    public static LoginFragment newInstance() {
        LoginFragment fragment = new LoginFragment();
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        validator = new InputValidator();
    }

    @Override
    public void onResume() {
        super.onResume();
        ((PreLoginActivity)getActivity()).setActionBarTitle(getString(R.string.app_name), "#000000", R.color.white);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        TextInputEditText emailField = view.findViewById(R.id.editTextEmailTextField);
        emailField.addTextChangedListener(new ErrorRemoveInterface() {
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) { emailField.setError(null); }
        });

        TextInputEditText passwordField = view.findViewById(R.id.editTextPasswordTextField);
        passwordField.addTextChangedListener(new ErrorRemoveInterface() {
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) { passwordField.setError(null); }
        });

        TextView signUp = view.findViewById(R.id.signUpTextView);
        signUp.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                //Load sign up fragment
                FragmentManager manager = getActivity().getSupportFragmentManager();
                manager.beginTransaction().replace(R.id.preLoginFragmentContainer, SignUpFragment.class, null)
                        .setReorderingAllowed(true)
                        .commit();
            }
        });

        TextView exploreTextView = view.findViewById(R.id.exploreTextView);
        exploreTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //go to landing
            }
        });

        progressBar = view.findViewById(R.id.progress);
        Sprite doubleBounce = new DoubleBounce();
        progressBar.setIndeterminateDrawable(doubleBounce);
        progressBar.setVisibility(View.INVISIBLE);

        Button loginButton = view.findViewById(R.id.loginButton);
        loginButton.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                String email = emailField.getText().toString().trim();
                if (!validator.isValidEmail(email)){
                    //error
                    emailField.setError(getString(R.string.invalid_email));
                    return;
                }

                String password = passwordField.getText().toString().trim();
                if (!validator.isValidPassword(password)){
                    //error
                    passwordField.setError(getString(R.string.password_short));
                    return;
                }

                progressBar.setVisibility(View.VISIBLE);
                NetworkHandler.getInstance().login(email, password, new NetworkingClosure() {
                    @Override
                    public void completion(JSONObject object, String message) {
                        System.out.println("===========================");
                        progressBar.setVisibility(View.INVISIBLE);
                        if (object == null){
                            //this will only happen if api fails
                            Toast.makeText(getActivity(), (message == null) ? getText(R.string.something_wrong): message, Toast.LENGTH_LONG).show();
                        }else{
                            try {
                                System.out.println(object);
//                                UserModel current = new UserModel(object.getInt("id"), object.getString("name"), object.getString("email"));
//                                UserModel.currentUser = current;
//                                Utilities.getInstance().saveJsonObject(object, getApplicationContext());

                            }catch (Exception e){
                                Toast.makeText(getActivity(), e.getMessage(), Toast.LENGTH_LONG).show();
                            }
                        }
                    }
                });
            }
        });
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_login, container, false);
    }
}