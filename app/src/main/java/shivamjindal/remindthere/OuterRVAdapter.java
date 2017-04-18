package shivamjindal.remindthere;

import android.content.Context;
import android.content.Intent;
import android.provider.ContactsContract;
import android.support.annotation.IntegerRes;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.List;

import shivamjindal.remindthere.models.TasksContainer;

/**
 * Created by shivam on 8/4/17.
 */

class OuterRVAdapter extends RecyclerView.Adapter<OuterRVAdapter.OuterAdapterViewHolder> {

    private List<TasksContainer> taskList;
    private Context context;
    private DatabaseAdapter databaseAdapter;
    private List<Integer> dateReminderIds;
    private List<Integer> locationReminderIds;

    OuterRVAdapter(List<TasksContainer> taskList) {
        this.taskList = taskList;
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

            }
        });


        holder.locationReminderDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

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
