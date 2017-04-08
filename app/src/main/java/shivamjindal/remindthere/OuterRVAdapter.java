package shivamjindal.remindthere;

import android.content.Context;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.List;

import shivamjindal.remindthere.models.TasksContainer;

/**
 * Created by shivam on 8/4/17.
 */

class OuterRVAdapter extends RecyclerView.Adapter<OuterRVAdapter.OuterAdapterViewHolder> {

    private List<TasksContainer> taskList;
    private Context context;

    OuterRVAdapter(List<TasksContainer> taskList) {
        this.taskList = taskList;
    }

    @Override
    public OuterAdapterViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.single_row_outer_recyclerview, parent, false);
        context = parent.getContext();
        return new OuterAdapterViewHolder(v);
    }

    @Override
    public void onBindViewHolder(OuterAdapterViewHolder holder, int position) {
        if (taskList.get(position).getTitle() == null)
            holder.taskTitle.setVisibility(View.GONE);
        else {
            holder.taskTitle.setVisibility(View.VISIBLE);
            holder.taskTitle.setText(taskList.get(position).getTitle());
        }

        InnerRVAdapter innerRVAdapter = new InnerRVAdapter(taskList.get(position));
        holder.innerRecyclerView.setAdapter(innerRVAdapter);
        LinearLayoutManager llm = new LinearLayoutManager(context);
        holder.innerRecyclerView.setLayoutManager(llm);
    }

    @Override
    public int getItemCount() {
        if (taskList == null) {
            return 0;
        } else {
            return taskList.size();
        }
    }

    class OuterAdapterViewHolder extends RecyclerView.ViewHolder {
        TextView taskTitle;
        RecyclerView innerRecyclerView;

        OuterAdapterViewHolder(View itemView) {
            super(itemView);
            taskTitle = (TextView) itemView.findViewById(R.id.title);
            innerRecyclerView = (RecyclerView) itemView.findViewById(R.id.inner_recycler_view);
        }
    }
}
