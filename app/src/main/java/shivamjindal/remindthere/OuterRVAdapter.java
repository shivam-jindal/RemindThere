package shivamjindal.remindthere;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

/**
 * Created by shivam on 8/4/17.
 */

public class OuterRVAdapter extends RecyclerView.Adapter<OuterRVAdapter.OuterAdapterViewHolder> {
    @Override
    public OuterAdapterViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return null;
    }

    @Override
    public void onBindViewHolder(OuterAdapterViewHolder holder, int position) {

    }

    @Override
    public int getItemCount() {
        return 0;
    }

    class OuterAdapterViewHolder extends RecyclerView.ViewHolder {
        TextView taskTitle;
        RecyclerView outerRecyclerView;

        public OuterAdapterViewHolder(View itemView) {
            super(itemView);
            taskTitle = (TextView) itemView.findViewById(R.id.title);
            outerRecyclerView = (RecyclerView) itemView.findViewById(R.id.outer_recycler_view);
        }
    }
}
