package shivamjindal.remindthere;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.support.annotation.IntegerRes;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.LocationServices;

import java.util.List;

import shivamjindal.remindthere.models.TasksContainer;

/**
 * Created by shivam on 8/4/17.
 */

class OuterRVAdapter extends RecyclerView.Adapter<OuterRVAdapter.OuterAdapterViewHolder>
        implements GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener, ResultCallback<Status> {

    private List<TasksContainer> taskList;
    private Context context;
    private DatabaseAdapter databaseAdapter;
    private List<Integer> dateReminderIds;
    private List<Integer> locationReminderIds;

    private GoogleApiClient googleApiClient;

    OuterRVAdapter(List<TasksContainer> taskList, Context context) {
        this.taskList = taskList;

        if (googleApiClient == null) {
            googleApiClient = new GoogleApiClient.Builder(context)
                    .addApi(LocationServices.API)
                    .addConnectionCallbacks(this)
                    .addOnConnectionFailedListener(this)
                    .build();
        }
        connetApiClient();
    }

    private void connetApiClient() {
        if(googleApiClient!=null && !googleApiClient.isConnected()){
            googleApiClient.connect();
            //Toast.makeText(context,"Google Api Connected",Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public OuterAdapterViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.single_row_outer_recyclerview, parent, false);
        context = parent.getContext();
        databaseAdapter = new DatabaseAdapter(parent.getContext());

        dateReminderIds = databaseAdapter.getDateReminderIds();
        locationReminderIds = databaseAdapter.getLocationReminderIds();
        return new OuterAdapterViewHolder(v);
    }

    @Override
    public void onBindViewHolder(final OuterAdapterViewHolder holder, final int position) {
        if (taskList.get(position).getTitle() == null)
            holder.taskTitle.setVisibility(View.GONE);
        else {
            holder.taskTitle.setVisibility(View.VISIBLE);
            holder.taskTitle.setText(taskList.get(position).getTitle());
        }

        if (dateReminderIds.contains(taskList.get(position).getCategoryId())) {
            holder.dateReminderLayout.setVisibility(View.VISIBLE);
            holder.dateReminderText.setText(databaseAdapter.getDateReminder(taskList.get(position).getCategoryId()));
        } else {
            holder.dateReminderLayout.setVisibility(View.GONE);
        }

        if (locationReminderIds.contains(taskList.get(position).getCategoryId())) {
            holder.locationReminderLayout.setVisibility(View.VISIBLE);
            holder.locationReminderText.setText(databaseAdapter.getLocationReminder(taskList.get(position).getCategoryId()));
        } else {
            holder.locationReminderLayout.setVisibility(View.GONE);
        }

        final InnerRVAdapter innerRVAdapter = new InnerRVAdapter(taskList.get(position));
        holder.innerRecyclerView.setAdapter(innerRVAdapter);
        LinearLayoutManager llm = new LinearLayoutManager(context);
        holder.innerRecyclerView.setLayoutManager(llm);


        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, EditItemActivity.class);
                intent.putExtra("CATEGORY_ID", taskList.get(holder.getAdapterPosition()).getCategoryId());
                context.startActivity(intent);
            }
        });


        holder.dateReminderDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new AlertDialog.Builder(context)
                        .setMessage("Are you sure that you want to delete the reminder?")
                        .setPositiveButton("DELETE", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int whichButton) {
                                Intent intent = new Intent(context, AlarmReceiver.class);

                                intent.putExtra("TITLE_TEXT", taskList.get(holder.getAdapterPosition()).getTitle());
                                intent.putExtra("CATEGORY_ID", taskList.get(holder.getAdapterPosition()).getCategoryId());
                                PendingIntent pendingIntent = PendingIntent
                                        .getBroadcast(
                                                context,
                                                taskList.get(holder.getAdapterPosition()).getCategoryId(),
                                                intent,
                                                PendingIntent.FLAG_UPDATE_CURRENT);

                                AlarmManager alarmManager = (AlarmManager) (context.getSystemService(Context.ALARM_SERVICE));
                                pendingIntent.cancel();
                                alarmManager.cancel(pendingIntent);

                                Constants.deleteTimeReminderFromDB(context, taskList.get(holder.getAdapterPosition()).getCategoryId());
                                Constants.refreshFragment(context);

                                Toast.makeText(context, "Reminder Deleted Successfully!", Toast.LENGTH_SHORT).show();
                            }
                        })
                        .setNegativeButton("CANCEL", null)
                        .show();
            }
        });


        holder.locationReminderDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new AlertDialog.Builder(context)
                        .setMessage("Are you sure that you want to delete the reminder?")
                        .setPositiveButton("DELETE", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int whichButton) {
                                Intent intent = new Intent(context, GeofenceTransitionsIntentService.class);
                                PendingIntent gpi = PendingIntent.getService(
                                        context, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);

                                if(googleApiClient.isConnected()) {
                                    LocationServices.GeofencingApi.removeGeofences(googleApiClient, gpi);
                                }

                                Constants.deleteLocationReminderFromDB(context, taskList.get(holder.getAdapterPosition()).getCategoryId());
                                Constants.refreshFragment(context);
                            }
                        })
                        .setNegativeButton("CANCEL", null)
                        .show();
            }
        });
    }


    @Override
    public int getItemCount() {
        if (taskList == null) {
            return 0;
        } else {
            return taskList.size();
        }
    }


    void removeItem(int swipedPosition) {
        DatabaseAdapter databaseAdapter = new DatabaseAdapter(context);
        databaseAdapter.deleteItem(taskList.get(swipedPosition).getCategoryId());
        taskList.remove(swipedPosition);
        notifyItemRemoved(swipedPosition);
    }

    @Override
    public void onConnected(@Nullable Bundle bundle) {

    }

    @Override
    public void onConnectionSuspended(int i) {
       googleApiClient.connect();
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }

    @Override
    public void onResult(@NonNull Status status) {

    }


    class OuterAdapterViewHolder extends RecyclerView.ViewHolder {
        TextView taskTitle;
        RecyclerView innerRecyclerView;
        LinearLayout dateReminderLayout, locationReminderLayout;
        TextView dateReminderText, locationReminderText;
        ImageView dateReminderDelete, locationReminderDelete;

        OuterAdapterViewHolder(View itemView) {
            super(itemView);
            taskTitle = (TextView) itemView.findViewById(R.id.title);
            innerRecyclerView = (RecyclerView) itemView.findViewById(R.id.inner_recycler_view);
            dateReminderLayout = (LinearLayout) itemView.findViewById(R.id.date_reminder_layout);
            locationReminderLayout = (LinearLayout) itemView.findViewById(R.id.location_reminder_layout);
            dateReminderText = (TextView) itemView.findViewById(R.id.date_reminder_text);
            locationReminderText = (TextView) itemView.findViewById(R.id.location_reminder_text);
            dateReminderDelete = (ImageView) itemView.findViewById(R.id.date_reminder_delete);
            locationReminderDelete = (ImageView) itemView.findViewById(R.id.location_reminder_delete);
        }
    }
}
