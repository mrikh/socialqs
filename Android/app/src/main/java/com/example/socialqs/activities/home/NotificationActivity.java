package com.example.socialqs.activities.home;

import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.style.ForegroundColorSpan;
import android.util.AttributeSet;
import android.view.MenuItem;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.socialqs.R;
import com.example.socialqs.adapters.NotificationAdapter;
import com.example.socialqs.adapters.VideoRepliesAdapter;
import com.example.socialqs.models.NotificationModel;
import com.example.socialqs.models.VideoRepliesModel;
import com.example.socialqs.utils.Utilities;
import com.example.socialqs.utils.helperInterfaces.NetworkingClosure;
import com.example.socialqs.utils.networking.NetworkHandler;
import com.github.ybq.android.spinkit.sprite.Sprite;
import com.github.ybq.android.spinkit.style.DoubleBounce;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class NotificationActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private ProgressBar progressBar;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_notification);

        updateActionBar();

        recyclerView = findViewById(R.id.notification_recycler);

        progressBar = findViewById(R.id.progress);
        Sprite doubleBounce = new DoubleBounce();
        progressBar.setIndeterminateDrawable(doubleBounce);
        progressBar.setVisibility(View.VISIBLE);

        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setHasFixedSize(true);

        List<NotificationModel> notificationList = new ArrayList<>();

        NotificationModel n1 = new NotificationModel();
        n1.getNotificationTitle();
        n1.getNotificationMessage();
        notificationList.add(n1);

        recyclerView.setAdapter(new NotificationAdapter(getApplicationContext(), notificationList));

//        NetworkHandler.getInstance().notificationListing(new NetworkingClosure() {
//            @Override
//            public void completion(JSONObject object, String message) {
//                progressBar.setVisibility(View.INVISIBLE);
//                if (object == null) {
//                    Utilities.getInstance().createSingleActionAlert((message == null) ? getText(R.string.something_wrong) : message, getText(R.string.okay), getApplicationContext(), null).show();
//                    return;
//                }
//
//                try {
//                    JSONArray arr = object.getJSONArray("result");
//                    for (int i = 0; i < arr.length(); i++) {
//                        NotificationModel item = new NotificationModel(arr.getJSONObject(i));
//                        notificationList.add(item);
//                    }
//
//                    if(notificationList.size() == 0){
////                        noNotificationLayout.setVisibility(View.VISIBLE);
//                    }else {
//                        recyclerView.setAdapter(new NotificationAdapter(getApplicationContext(), notificationList));
//                    }
//
//                } catch (Exception e) {
//                    e.printStackTrace();
//                }
//            }
//        });

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item){
        if (item.getItemId() == android.R.id.home) {
            finish();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void updateActionBar(){
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setBackgroundDrawable(new ColorDrawable(Color.parseColor("#ffffff")));
        getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_back_arrow);

        Spannable text = new SpannableString(getSupportActionBar().getTitle());
        text.setSpan(new ForegroundColorSpan(getResources().getColor(R.color.black)), 0, text.length(), Spannable.SPAN_INCLUSIVE_INCLUSIVE);
        getSupportActionBar().setTitle(text);
    }

}
