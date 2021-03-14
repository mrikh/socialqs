package com.example.socialqs.adapters;

import android.animation.LayoutTransition;
import android.app.Notification;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.coordinatorlayout.widget.CoordinatorLayout;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.socialqs.R;
import com.example.socialqs.activities.create.CreateActivity;
import com.example.socialqs.activities.home.notifications.NotificationActivity;
import com.example.socialqs.models.NotificationModel;
import com.example.socialqs.models.VideoRepliesModel;
import com.example.socialqs.utils.Utilities;
import com.example.socialqs.utils.helperInterfaces.NetworkingClosure;
import com.example.socialqs.utils.networking.NetworkHandler;
import com.google.android.material.snackbar.Snackbar;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

public class NotificationAdapter extends RecyclerView.Adapter<NotificationAdapter.NotificationViewHolder> {

    private List<NotificationModel> notificationList;
    private NotificationModel deletedItem;
    private int deletedItemPos;
    private CoordinatorLayout coordinatorLayout;

    public NotificationAdapter(List<NotificationModel> notificationList, CoordinatorLayout coordinatorLayout) {
        this.notificationList = notificationList;
        this.coordinatorLayout = coordinatorLayout;
    }

    @NonNull
    @Override
    public NotificationAdapter.NotificationViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new NotificationAdapter.NotificationViewHolder(LayoutInflater.from(parent.getContext()).inflate(
                R.layout.item_notification, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull NotificationViewHolder holder, int position) {
        holder.setData(notificationList.get(position));
    }

    @Override
    public int getItemCount() {
        return notificationList.size();
    }

    public void deleteNotification(int position){
        deletedItem = notificationList.get(position);
        deletedItemPos = position;
        notificationList.remove(position);
        notifyItemRemoved(position);
        showUndoSnackbar();
    }



    private void showUndoSnackbar() {
        Snackbar snackbar = Snackbar.make(coordinatorLayout, R.string.delete_notification_text,
                Snackbar.LENGTH_LONG);

        snackbar.setAction(R.string.undo_delete, v -> {
            notificationList.add(deletedItemPos, deletedItem);
            notifyItemInserted(deletedItemPos);
        });

        snackbar.addCallback(new Snackbar.Callback() {
             @Override
             public void onDismissed(Snackbar transientBottomBar, int event) {
             super.onDismissed(transientBottomBar, event);

             String notificationID = deletedItem.getNotificationID();

             //TODO DELETE NOTIFICATION COMPLETELY
             NetworkHandler.getInstance().deleteNotification(notificationID, new NetworkingClosure() {
                 @Override
                 public void completion(JSONObject object, String message) {
//                     try {
//
//
//                     } catch (JSONException e) {
//                         e.printStackTrace();
//                     }
                 }
             });
             }
         });

        snackbar.show();
    }

    public class NotificationViewHolder extends RecyclerView.ViewHolder {

        private TextView title, message, messageLength, date;

        private final static int MAX_LINES_COLLAPSED = 3;
        private boolean isCollapsed = true;

        public NotificationViewHolder(@NonNull View v) {
            super(v);
            title = v.findViewById(R.id.notification_title);
            message = v.findViewById(R.id.notification_message);
            messageLength = v.findViewById(R.id.notification_message_length);
            date = v.findViewById(R.id.notification_time);
        }

        void setData(NotificationModel notificationItem) {
            title.setText(notificationItem.getNotificationTitle());
            message.setText(notificationItem.getNotificationMessage());
            date.setText(notificationItem.getTime());

            //Hide read more option if all text is see
            message.post(() -> {
                if(message.getLineCount() < MAX_LINES_COLLAPSED){
                    messageLength.setVisibility(View.INVISIBLE);
                }
            });

            //Read More/Read Less On Click
            messageLength.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if(isCollapsed){
                        message.setMaxLines(Integer.MAX_VALUE);
                        messageLength.setText(R.string.read_less);
                    }else{
                        message.setMaxLines(MAX_LINES_COLLAPSED);
                        messageLength.setText(R.string.read_more);
                    }
                    isCollapsed  = !isCollapsed;
                }
            });
        }

    }

}
