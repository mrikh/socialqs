package com.example.socialqs.activities.prelogin.fragments;

import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import android.telecom.Call;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.example.socialqs.R;
import com.example.socialqs.activities.prelogin.PreLoginActivity;
import com.example.socialqs.models.UserModel;
import com.example.socialqs.utils.InputValidator;
import com.example.socialqs.utils.Utilities;
import com.example.socialqs.utils.helperInterfaces.ErrorRemoveInterface;
import com.example.socialqs.utils.helperInterfaces.NetworkingClosure;
import com.example.socialqs.utils.networking.NetworkHandler;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.facebook.Profile;
import com.facebook.ProfileTracker;
import com.facebook.login.LoginManager;
import com.facebook.login.LoginResult;
import com.github.ybq.android.spinkit.sprite.Sprite;
import com.github.ybq.android.spinkit.style.DoubleBounce;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputEditText;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Arrays;

public class LoginFragment extends Fragment {

    private InputValidator validator;
    private CallbackManager callbackManager;
    private GoogleSignInClient mGoogleSignInClient;
    private ProfileTracker profileTracker;
    //needed for google
    private static int RC_SIGN_IN = 100;

    public LoginFragment() {}

    public static LoginFragment newInstance() {
        LoginFragment fragment = new LoginFragment();
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        validator = new InputValidator();
        callbackManager = CallbackManager.Factory.create();

        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestEmail()
                .build();
        mGoogleSignInClient = GoogleSignIn.getClient(getActivity(), gso);

        LoginManager.getInstance().registerCallback(
                callbackManager,
                new FacebookCallback<LoginResult>() {
                    @Override
                    public void onSuccess(LoginResult loginResult) {
                        //get data. Since profile has no email
                        GraphRequest request = GraphRequest.newMeRequest(loginResult.getAccessToken(), new GraphRequest.GraphJSONObjectCallback() {
                            @Override
                            public void onCompleted(JSONObject object, GraphResponse response) {
                                updateProgress(View.INVISIBLE);
                                try {
                                    JSONObject params = new JSONObject();
                                    JSONObject responseObject = response.getJSONObject();

                                    String name = responseObject.getString("name");
                                    Profile profile = Profile.getCurrentProfile();
                                    String id = profile.getId();
                                    params.put("name", name);
                                    params.put("socialId", id);

                                    Uri profilePhotoUri = Profile.getCurrentProfile().getProfilePictureUri(1024, 1024);
                                    if (profilePhotoUri != null){
                                        params.put("profilePhoto", profilePhotoUri.toString());
                                    }

                                    //now we may not receive email, in which case we need to go to sign up
                                    if (!responseObject.has("email")){
                                        Utilities.getInstance().createSingleActionAlert(getText(R.string.fb_no_email), getText(R.string.okay), getActivity(), new DialogInterface.OnClickListener() {
                                            @Override
                                            public void onClick(DialogInterface dialog, int which) {
                                                dialog.dismiss();
                                                goToSignUp(params);
                                            }
                                        }).show();
                                    }else{
                                        String email = responseObject.getString("email");
                                        params.put("email", email);
                                        beginLogin(params);
                                    }

                                } catch (Exception e) {
                                    Toast.makeText(getActivity(), e.getMessage(), Toast.LENGTH_LONG).show();
                                }
                            }
                        });
                        updateProgress(View.VISIBLE);
                        request.executeAsync();
                    }

                    @Override
                    public void onCancel() {
                        Toast.makeText(getActivity(), getText(R.string.login_cancelled), Toast.LENGTH_LONG).show();
                    }

                    @Override
                    public void onError(FacebookException exception) {
                        Toast.makeText(getActivity(), exception.getMessage(), Toast.LENGTH_LONG).show();
                    }
                }
        );
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == RC_SIGN_IN) {
            // The Task returned from this call is always completed, no need to attach
            // a listener.
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
            handleSignInResult(task);
        }else {
            callbackManager.onActivityResult(requestCode, resultCode, data);
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        ((PreLoginActivity)getActivity()).updateActionBarBack(false);
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
                goToSignUp(null);
            }
        });

        TextView forgotTextView = view.findViewById(R.id.forgotPasswordTextView);
        forgotTextView.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                //Load sign up fragment
                FragmentManager manager = getActivity().getSupportFragmentManager();
                manager.beginTransaction()
                        .setCustomAnimations(R.anim.slide_in, R.anim.fade_out, R.anim.fade_in, R.anim.slide_out)
                        .replace(R.id.preLoginFragmentContainer, ForgotPasswordFragment.class, null)
                        .setReorderingAllowed(true)
                        .addToBackStack(null)
                        .commit();
            }
        });

        TextView exploreTextView = view.findViewById(R.id.exploreTextView);
        exploreTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //TODO: go to landing
            }
        });

        ImageView facebookButton = view.findViewById(R.id.facebookButton);
        facebookButton.setOnClickListener(new View.OnClickListener(){

            @Override
            public void onClick(View v) {
                LoginManager.getInstance().logInWithReadPermissions(
                        getActivity(), Arrays.asList("email", "public_profile")
                );
            }
        });

        ImageView googleButton = view.findViewById(R.id.googleButton);
        googleButton.setOnClickListener(new View.OnClickListener(){

            @Override
            public void onClick(View v) {
                Intent signInIntent = mGoogleSignInClient.getSignInIntent();
                startActivityForResult(signInIntent, RC_SIGN_IN);
            }
        });

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

                try{
                    JSONObject object = new JSONObject();
                    object.put("email", email);
                    object.put("password", password);
                    beginLogin(object);
                } catch (JSONException e) {
                    Toast.makeText(getActivity(), e.getMessage(), Toast.LENGTH_LONG).show();
                }
            }
        });
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_login, container, false);
    }

    private void beginLogin(JSONObject object){

        //get push token and add if it exists
        Bundle extras = getActivity().getIntent().getExtras();
        if (extras != null){
            String push = extras.getString("pushToken");
            if (push != null){
                try {
                    object.put("pushToken", push);
                }catch(Exception e){
                    System.out.println(e.getMessage());
                }
            }
        }

        updateProgress(View.VISIBLE);
        NetworkHandler.getInstance().login(object, new NetworkingClosure() {
            @Override
            public void completion(JSONObject object, String message) {
                updateProgress(View.INVISIBLE);
                if (object == null){
                    //this will only happen if api fails
                    Toast.makeText(getActivity(), (message == null) ? getText(R.string.something_wrong): message, Toast.LENGTH_LONG).show();
                }else{
                    try {
                        JSONObject finalObject = object.getJSONObject("user");
                        finalObject.put("token", object.getString("token"));

                        UserModel currentUser = new UserModel(finalObject);
                        UserModel.current = currentUser;
                        UserModel.current.saveToDefaults(getActivity().getApplicationContext());

                        //TODO: Open landing
                        Toast.makeText(getActivity(), "Success! Remove message and navigate to landing :P", Toast.LENGTH_LONG).show();
                    }catch (Exception e){
                        Toast.makeText(getActivity(), e.getMessage(), Toast.LENGTH_LONG).show();
                    }
                }
            }
        });
    }

    private void goToSignUp(JSONObject params){
        FragmentManager manager = getActivity().getSupportFragmentManager();

        Bundle arguments = new Bundle();

        if (params != null) {
            arguments.putString("params", params.toString());
        }

        manager.beginTransaction()
                .setCustomAnimations(R.anim.slide_in, R.anim.fade_out, R.anim.fade_in, R.anim.slide_out)
                .replace(R.id.preLoginFragmentContainer, SignUpFragment.class, arguments)
                .setReorderingAllowed(true)
                .commit();
    }

    private void handleSignInResult(Task<GoogleSignInAccount> completedTask) {
        try {
            GoogleSignInAccount acct = completedTask.getResult(ApiException.class);
            // Signed in successfully, show authenticated UI.
            if (acct != null) {
                String personDisplayName = acct.getDisplayName();
                String personEmail = acct.getEmail();
                String id = acct.getId();
                Uri personPhoto = acct.getPhotoUrl();
                JSONObject object = new JSONObject();
                object.put("name", personDisplayName);
                object.put("email", personEmail);
                object.put("socialId", id);
                if (personPhoto != null){
                    object.put("profilePhoto", personPhoto.toString());
                }
                beginLogin(object);
            }else{
                Toast.makeText(getActivity(), getString(R.string.something_wrong), Toast.LENGTH_LONG).show();
            }
        } catch (Exception e) {
            // The ApiException status code indicates the detailed failure reason.
            // Please refer to the GoogleSignInStatusCodes class reference for more information.
            Toast.makeText(getActivity(), e.getMessage(), Toast.LENGTH_LONG).show();
        }
    }

    private void updateProgress(int visibility){
        ((PreLoginActivity) getActivity()).updateProgress(visibility);
    }
}