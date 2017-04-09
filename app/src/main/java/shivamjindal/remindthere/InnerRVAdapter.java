package shivamjindal.remindthere;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.TextView;

import shivamjindal.remindthere.models.TasksContainer;

/**
 * Created by shivam on 9/4/17.
 */

public class InnerRVAdapter extends RecyclerView.Adapter<InnerRVAdapter.InnerAdapterViewHolder> {

    private Context context;
    private TasksContainer taskList;


    public InnerRVAdapter(TasksContainer taskList){
        this.taskList = taskList;
    }

    @Override
    public InnerAdapterViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.single_row_inner_recyclerview, parent, false);
        context = parent.getContext();
        return new InnerRVAdapter.InnerAdapterViewHolder(v);
    }

    @Override
    public void onBindViewHolder(InnerAdapterViewHolder holder, int position) {
        holder.taskName.setText(taskList.getTasks().get(position).getTaskName());

        if(taskList.getTasks().get(position).isCheckVisible()){
            holder.checkBox.setVisibility(View.VISIBLE);
            holder.checkBox.setChecked(taskList.getTasks().get(position).isChecked());
        }else{
            holder.checkBox.setVisibility(View.GONE);
        }
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
