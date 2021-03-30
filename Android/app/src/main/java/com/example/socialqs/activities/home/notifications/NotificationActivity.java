package com.example.socialqs.activities.home.notifications;

import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.style.ForegroundColorSpan;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.ProgressBar;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.coordinatorlayout.widget.CoordinatorLayout;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.socialqs.R;
import com.example.socialqs.adapters.NotificationAdapter;
import com.example.socialqs.models.NotificationModel;
import com.example.socialqs.utils.Utilities;
import com.example.socialqs.utils.helperInterfaces.NetworkingClosure;
import com.example.socialqs.utils.networking.NetworkHandler;
import com.github.ybq.android.spinkit.style.DoubleBounce;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class NotificationActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private ProgressBar progressBar;
    private NotificationAdapter adapter;
    private CoordinatorLayout coordinatorLayout;
    private LinearLayout noNotificationsLayout;
    private List<NotificationModel> notificationList;
    private Menu menu;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_notification);

        coordinatorLayout = findViewById(R.id.notification_layout);
        noNotificationsLayout = findViewById(R.id.no_notifications_layout);

        recyclerView = findViewById(R.id.notification_recycler);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setHasFixedSize(true);

        progressBar = findViewById(R.id.progress);
        progressBar.setIndeterminateDrawable( new DoubleBounce());
        progressBar.setVisibility(View.VISIBLE);

    }

    @Override
    protected void onResume() {
        super.onResume();
        notificationList = new ArrayList<>();

        NetworkHandler.getInstance().notificationListing(new NetworkingClosure() {
            @Override
            public void completion(JSONObject object, String message) {
                progressBar.setVisibility(View.INVISIBLE);
                if (object == null) {
                    Utilities.getInstance().createSingleActionAlert((message == null) ? getText(R.string.something_wrong) : message, getText(R.string.okay), getApplicationContext(), null).show();
                    return;
                }

                try {
                    JSONArray arr = object.getJSONArray("result");
                    for (int i = 0; i < arr.length(); i++) {
                        NotificationModel item = new NotificationModel(arr.getJSONObject(i));
                        notificationList.add(item);
                    }

                    if(notificationList.size() == 0){
                        noNotificationsLayout.setVisibility(View.VISIBLE);
                        menu.getItem(0).setVisible(false);
                    }else {
                        adapter = new NotificationAdapter(notificationList, coordinatorLayout, NotificationActivity.this);
                        adapter.setOnDataChangeListener(size -> {
                            if(size == 0){
                                noNotificationsLayout.setVisibility(View.VISIBLE);
                                menu.getItem(0).setVisible(false);
                            }else{
                                noNotificationsLayout.setVisibility(View.INVISIBLE);
                                menu.getItem(0).setVisible(true);
                            }
                        });

                        recyclerView.setAdapter(adapter);
                        ItemTouchHelper itemTouchHelper = new ItemTouchHelper(new SwipeToDelete(adapter, getApplicationContext()));
                        itemTouchHelper.attachToRecyclerView(recyclerView);
                    }

                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        this.menu = menu;
        getMenuInflater().inflate(R.menu.notification_menu, menu);
        Drawable deleteAll = menu.getItem(0).getIcon();
        deleteAll.setColorFilter(getResources().getColor(R.color.black), PorterDuff.Mode.SRC_ATOP);
        updateActionBar();

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            finish();
            return true;
        }

        if (item.getItemId() == R.id.btn_delete_all) {
            //Delete Confirmation
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setMessage(R.string.delete_all_notification_warning).setTitle(R.string.delete_notifications)
                    .setCancelable(false)
                    //Delete
                    .setPositiveButton(R.string.title_delete, (dialog, which) -> {
                        NetworkHandler.getInstance().deleteAllNotifications((object, message) -> {
                        });
                        noNotificationsLayout.setVisibility(View.VISIBLE);
                        item.getIcon().setAlpha(0);
                    })
                    //Cancel
                    .setNegativeButton(R.string.cancel, (dialog, which) -> dialog.cancel());
            builder.create().show();
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
