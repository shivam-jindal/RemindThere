package shivamjindal.remindthere;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;


/**
 * Created by shivam on 8/4/17.
 */

public class MainFragment extends Fragment {

    static RecyclerView outerRecyclerView;
    OuterRVAdapter outerRVAdapter;
    StaggeredGridLayoutManager staggeredGridLayoutManager;
    LinearLayoutManager linearLayoutManager;


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_main, container, false);
        outerRecyclerView = (RecyclerView) view.findViewById(R.id.outer_recycler_view);
        outerRecyclerView.setHasFixedSize(true);

        DatabaseAdapter databaseAdapter = new DatabaseAdapter(getContext());
        outerRVAdapter = new OuterRVAdapter(databaseAdapter.getAllTasks());
        outerRecyclerView.setAdapter(outerRVAdapter);

        staggeredGridLayoutManager = new StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL);
        linearLayoutManager = new LinearLayoutManager(getContext());
        outerRecyclerView.setLayoutManager(linearLayoutManager);
        return view;
    }
}
