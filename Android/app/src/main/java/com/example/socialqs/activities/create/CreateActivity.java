package com.example.socialqs.activities.create;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import android.Manifest;
import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.style.ForegroundColorSpan;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.ProgressBar;

import com.amazonaws.mobileconnectors.s3.transferutility.TransferListener;
import com.amazonaws.mobileconnectors.s3.transferutility.TransferState;
import com.example.socialqs.R;
import com.example.socialqs.models.CategoryModel;
import com.example.socialqs.models.QuestionModel;
import com.example.socialqs.utils.Utilities;
import com.example.socialqs.utils.helperInterfaces.NetworkingClosure;
import com.example.socialqs.utils.networking.NetworkHandler;
import com.github.ybq.android.spinkit.sprite.Sprite;
import com.github.ybq.android.spinkit.style.DoubleBounce;

import org.json.JSONObject;

import java.util.ArrayList;

public class CreateActivity extends AppCompatActivity {

    public ArrayList<CategoryModel> categories;
    private ProgressBar progressBar;
    public QuestionModel question = new QuestionModel();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create);

        getSupportActionBar().hide();
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        Bundle bundle = getIntent().getExtras();
        if (bundle != null){
            this.categories = bundle.getParcelableArrayList("categories");
        }

        findViewById(R.id.createBackground).setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                InputMethodManager imm = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(v.getWindowToken(), 0);
                return true;
            }
        });

        progressBar = findViewById(R.id.progress);
        Sprite doubleBounce = new DoubleBounce();
        progressBar.setIndeterminateDrawable(doubleBounce);
        progressBar.setVisibility(View.INVISIBLE);
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }

    public void setActionBarTitle(String title, String color, int titleColorId) {
        getSupportActionBar().setBackgroundDrawable(new ColorDrawable(Color.parseColor(color)));
        getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_back_arrow);

        if (title == null) {
            return;
        }

        Spannable text = new SpannableString(title);
        text.setSpan(new ForegroundColorSpan(getResources().getColor(titleColorId)), 0, text.length(), Spannable.SPAN_INCLUSIVE_INCLUSIVE);
        getSupportActionBar().setTitle(text);
    }

    public void updateActionBarBack(boolean show){
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(show);
        }
    }

    public void uploadAction(String filename, String filePath){

        try {
            updateProgress(View.VISIBLE);
            Utilities.getInstance().uploadFile(filename, "file:" + filePath, this, new TransferListener() {
                @Override
                public void onStateChanged(int id, TransferState state) {
                    if (state == TransferState.IN_PROGRESS){
                        updateProgress(View.VISIBLE);
                    }else if (state == TransferState.COMPLETED) {

                        JSONObject params = new JSONObject();
                        try {
                            params.put("title", question.getqTitle());
                            params.put("category", question.getqCategory().id);
                            params.put("videoUrl", Utilities.getInstance().s3UrlString(filename));
                            NetworkHandler.getInstance().createQuestion(params, new NetworkingClosure() {
                                @Override
                                public void completion(JSONObject object, String message) {
                                    updateProgress(View.INVISIBLE);
                                    if (object == null){
                                        Utilities.getInstance().createSingleActionAlert(message, "Okay", CreateActivity.this, null).show();
                                    }else{
                                        Intent newIntent = new Intent("CreatedQuestionIntent");
                                        LocalBroadcastManager.getInstance(CreateActivity.this).sendBroadcast(newIntent);
                                        Utilities.getInstance().createSingleActionAlert("Successfully created the question", "Okay", CreateActivity.this, new DialogInterface.OnClickListener() {
                                            @Override
                                            public void onClick(DialogInterface dialog, int which) {
                                                CreateActivity.this.finish();
                                            }
                                        }).show();
                                    }
                                }
                            });
                        } catch (Exception e) {
                            updateProgress(View.INVISIBLE);
                            Utilities.getInstance().createSingleActionAlert(e.getLocalizedMessage(), "Okay", CreateActivity.this, null).show();
                        }
                    }else{
                        updateProgress(View.INVISIBLE);
                    }
                }

                @Override
                public void onProgressChanged(int id, long bytesCurrent, long bytesTotal) {

                }

                @Override
                public void onError(int id, Exception ex) {
                    updateProgress(View.INVISIBLE);
                    Utilities.getInstance().createSingleActionAlert(ex.getLocalizedMessage(), "Okay", CreateActivity.this, null).show();
                }
            });
        } catch (Exception e) {
            Utilities.getInstance().createSingleActionAlert(e.getLocalizedMessage(), "Okay", this, null).show();
        }
    }

    public void updateProgress(int visibility){
        progressBar.setVisibility(visibility);
    }
}