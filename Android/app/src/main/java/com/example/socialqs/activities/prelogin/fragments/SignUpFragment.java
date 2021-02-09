package com.example.socialqs.activities.prelogin.fragments;

import android.content.Intent;
import android.graphics.Paint;
import android.graphics.Typeface;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import android.text.Spannable;
import android.text.SpannableStringBuilder;
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

public class SignUpFragment extends Fragment {

    private InputValidator validator;
    private ProgressBar progressBar;

    public SignUpFragment() {
        // Required empty public constructor
    }

    public static SignUpFragment newInstance(String param1, String param2) {
        SignUpFragment fragment = new SignUpFragment();
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
        ((PreLoginActivity)getActivity()).setActionBarTitle(getString(R.string.sign_up), "#ffffff", R.color.black);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_sign_up, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        TextInputEditText emailField = view.findViewById(R.id.editTextSignUpEmailTextField);
        emailField.addTextChangedListener(new ErrorRemoveInterface() {
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) { emailField.setError(null); }
        });

        TextInputEditText passwordField = view.findViewById(R.id.editTextSignUpPasswordTextField);
        passwordField.addTextChangedListener(new ErrorRemoveInterface() {
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) { passwordField.setError(null); }
        });

        TextInputEditText nameField = view.findViewById(R.id.editTextName);
        nameField.addTextChangedListener(new ErrorRemoveInterface() {
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) { nameField.setError(null); }
        });

        TextView terms = view.findViewById(R.id.termsTextView);
        terms.setTypeface(null, Typeface.BOLD);
        terms.setPaintFlags(Paint.UNDERLINE_TEXT_FLAG);
        terms.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {

            }
        });

        TextView loginTextView = view.findViewById(R.id.loginTextView);
        loginTextView.setTypeface(null, Typeface.BOLD);
        loginTextView.setPaintFlags(Paint.UNDERLINE_TEXT_FLAG);
        loginTextView.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                FragmentManager manager = getActivity().getSupportFragmentManager();
                manager.beginTransaction().replace(R.id.preLoginFragmentContainer, LoginFragment.class, null)
                        .setReorderingAllowed(true)
                        .commit();
            }
        });

        ProgressBar progressBar = view.findViewById(R.id.progress);
        Sprite doubleBounce = new DoubleBounce();
        progressBar.setIndeterminateDrawable(doubleBounce);
        progressBar.setVisibility(View.INVISIBLE);

        Button signUp = view.findViewById(R.id.signUpButton);
        signUp.setOnClickListener(new View.OnClickListener(){
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

                String name = nameField.getText().toString().trim();
                if (!validator.isValidName(name)){
                    //error
                    nameField.setError(getString(R.string.name_error));
                    return;
                }

                progressBar.setVisibility(View.VISIBLE);
                NetworkHandler.getInstance().signUp(email, password, name, new NetworkingClosure() {
                    @Override
                    public void completion(JSONObject object, String message) {
                        System.out.println("===========================");
                        progressBar.setVisibility(View.INVISIBLE);
                        if (object == null){
                            //this will only happen if api fails
                            Toast.makeText(getActivity(), (message == null) ? getText(R.string.something_wrong): message, Toast.LENGTH_LONG).show();
                        }else{
                            try {
//                                UserModel current = new UserModel(object.getInt("id"), object.getString("name"), object.getString("email"));
//                                UserModel.currentUser = current;
//                                Utilities.getInstance().saveJsonObject(object, getApplicationContext());
//                                //go to landing
//                                Intent myIntent = new Intent(SignUpActivity.this, LandingSearchActivity.class);
//                                myIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
//                                startActivity(myIntent);
                            }catch (Exception e){
                                Toast.makeText(getActivity(), e.getMessage(), Toast.LENGTH_LONG).show();
                            }
                        }
                    }
                });
            }
        });
    }
}