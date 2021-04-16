package com.example.socialqs.adapters;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.coordinatorlayout.widget.CoordinatorLayout;
import androidx.recyclerview.widget.RecyclerView;

import com.example.socialqs.R;
import com.example.socialqs.activities.home.VideoRepliesActivity;
import com.example.socialqs.models.NotificationModel;
import com.example.socialqs.utils.networking.NetworkHandler;
import com.google.android.material.snackbar.Snackbar;

import java.util.List;

/**
 *  List of notification items
 */
public class NotificationAdapter extends RecyclerView.Adapter<NotificationAdapter.NotificationViewHolder> {

    private List<NotificationModel> notificationList;
    private NotificationModel deletedItem;
    private int deletedItemPos;
    private CoordinatorLayout coordinatorLayout;
    private OnDataChangeListener onDataChangeListener;
    private Context context;

    public NotificationAdapter(List<NotificationModel> notificationList, CoordinatorLayout coordinatorLayout, Context context) {
        this.notificationList = notificationList;
        this.coordinatorLayout = coordinatorLayout;
        this.context = context;
    }

    @NonNull
    @Override
    public NotificationAdapter.NotificationViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new NotificationAdapter.NotificationViewHolder(LayoutInflater.from(parent.getContext()).inflate(
                R.layout.item_notification, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull NotificationViewHolder holder, int position) {
        holder.setData(notificationList.get(position), position);
    }

    @Override
    public int getItemCount() {
        return notificationList.size();
    }

    //update layout immediately of change
    public interface OnDataChangeListener{ void onDataChanged(int size);}

    public void setOnDataChangeListener(OnDataChangeListener onDataChangeListener){
        this. onDataChangeListener = onDataChangeListener;
    }

    //delete individual notification
    public void deleteNotification(int position){
        deletedItem = notificationList.get(position);
        deletedItemPos = position;
        notificationList.remove(position);
        notifyItemRemoved(position);
        showUndoSnackbar();
        updateActivityLayout();
    }

    //show delete notification snackbar at bottom of screen
    private void showUndoSnackbar() {
        Snackbar snackbar = Snackbar.make(coordinatorLayout, R.string.delete_notification_text, Snackbar.LENGTH_LONG);

        //Undo delete
        snackbar.setAction(R.string.undo_delete, v -> {
            notificationList.add(deletedItemPos, deletedItem);
            notifyItemInserted(deletedItemPos);
            updateActivityLayout();
        });

        //Ignore snackbar = delete notification permanently
        snackbar.addCallback(new Snackbar.Callback() {
             @Override
             public void onDismissed(Snackbar transientBottomBar, int event) {
             super.onDismissed(transientBottomBar, event);

             //if snackbar is ignored, delete completely
             if(event == Snackbar.Callback.DISMISS_EVENT_TIMEOUT) {
                 String notificationID = deletedItem.getNotificationID();
                 NetworkHandler.getInstance().deleteNotification(notificationID, (object, message) -> { });
             }
             }
         });

        snackbar.show();
    }

    //Update layout list
    private void updateActivityLayout(){
        if(onDataChangeListener != null){
            onDataChangeListener.onDataChanged(notificationList.size());
        }
    }


    /**
     *  Set up individual notification item data
     */
    public class NotificationViewHolder extends RecyclerView.ViewHolder {

        private TextView title, message, messageLength, date;
        private LinearLayout notification;

        private final static int MAX_LINES_COLLAPSED = 3;
        private boolean isCollapsed = true;

        public NotificationViewHolder(@NonNull View v) {
            super(v);
            title = v.findViewById(R.id.notification_title);
            message = v.findViewById(R.id.notification_message);
            messageLength = v.findViewById(R.id.notification_message_length);
            date = v.findViewById(R.id.notification_time);
            notification = v.findViewById(R.id.notification_section);
        }

        void setData(NotificationModel notificationItem, int position) {
            String notificationID = notificationItem.getNotificationID();
            String questionID = notificationItem.getQuestionID();
            title.setText(notificationItem.getNotificationTitle());
            message.setText(notificationItem.getNotificationMessage());
            date.setText(notificationItem.getTime());

            //Hide read more option if all text is seen
            message.post(() -> {
                if(message.getLineCount() < MAX_LINES_COLLAPSED){
                    messageLength.setVisibility(View.INVISIBLE);
                }
            });

            //Read More/Read Less On Click
            messageLength.setOnClickListener(v -> {
                if(isCollapsed){
                    message.setMaxLines(Integer.MAX_VALUE);
                    messageLength.setText(R.string.read_less);
                }else{
                    message.setMaxLines(MAX_LINES_COLLAPSED);
                    messageLength.setText(R.string.read_more);
                }
                isCollapsed  = !isCollapsed;
            });

            //Show answer
            notification.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent myIntent = new Intent(context, VideoRepliesActivity.class);
                    myIntent.putExtra("video_id", questionID);

                    //remove notification
                    NetworkHandler.getInstance().deleteNotification(notificationID, (object, message) -> { });

                    context.startActivity(myIntent);
                }
            });
        }

    }

}
