package shivamjindal.remindthere;

import android.content.Context;
import android.graphics.Paint;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Handler;

import shivamjindal.remindthere.models.Task;
import shivamjindal.remindthere.models.TasksContainer;

/**
 * Created by shivam on 9/4/17.
 */

class InnerRVAdapter extends RecyclerView.Adapter<InnerRVAdapter.InnerAdapterViewHolder> {

    private Context context;
    private TasksContainer taskList;

    InnerRVAdapter(TasksContainer tasksList) {
        List<Task> tasks = new ArrayList<>();
        TasksContainer nt = new TasksContainer();
        for (int i = 0; i < tasksList.getTasks().size(); i++) {
            if (!tasksList.getTasks().get(i).getTaskName().equals(""))
                tasks.add(tasksList.getTasks().get(i));
        }
        nt.setTasks(tasks);
        nt.setTitle(tasksList.getTitle());
        nt.setCategoryId(tasksList.getCategoryId());
        this.taskList = nt;

    }

    @Override
    public InnerAdapterViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.single_row_inner_recyclerview, parent, false);
        context = parent.getContext();
        return new InnerRVAdapter.InnerAdapterViewHolder(v);
    }

    @Override
    public void onBindViewHolder(final InnerAdapterViewHolder holder, int position) {
            holder.taskName.setText(taskList.getTasks().get(position).getTaskName());

            if (taskList.getTasks().get(position).isCheckVisible()) {
                holder.checkBox.setVisibility(View.VISIBLE);
                if(taskList.getTasks().get(position).isChecked()){
                holder.checkBox.setChecked(true);
                holder.taskName.setPaintFlags(Paint.STRIKE_THRU_TEXT_FLAG);
                }
                else{
                    holder.checkBox.setChecked(false);
                    holder.taskName.setPaintFlags(0);
                }
            } else {
                holder.checkBox.setVisibility(View.GONE);
            }

            holder.checkBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                    DatabaseAdapter databaseAdapter = new DatabaseAdapter(context);
                    databaseAdapter.changeCheck(taskList.getTasks().get(holder.getAdapterPosition()).getTaskName(), isChecked);
                    taskList.getTasks().get(holder.getAdapterPosition()).setChecked(isChecked);
                    android.os.Handler handler = new android.os.Handler();
                    final Runnable r = new Runnable() {
                        @Override
                        public void run() {
                            notifyItemChanged(holder.getAdapterPosition());
                        }
                    };
                    handler.post(r);
                }
            });
    }

    @Override
    public int getItemCount() {
        if (taskList.getTasks() == null) {
            return 0;
        } else {
              return taskList.getTasks().size();
        }
    }

    class InnerAdapterViewHolder extends RecyclerView.ViewHolder {
        CheckBox checkBox;
        TextView taskName;

        InnerAdapterViewHolder(View itemView) {
            super(itemView);
            checkBox = (CheckBox) itemView.findViewById(R.id.checkbox_task);
            taskName = (TextView) itemView.findViewById(R.id.task_text);
        }
    }
}
