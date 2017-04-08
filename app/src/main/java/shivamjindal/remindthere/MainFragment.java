package shivamjindal.remindthere;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

/**
 * Created by shivam on 8/4/17.
 */

public class MainFragment extends Fragment {

    RecyclerView outerRecyclerView;
    OuterRVAdapter outerRVAdapter;


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_main, container, false);
        outerRecyclerView = (RecyclerView) view.findViewById(R.id.outer_recycler_view);
        outerRecyclerView.setHasFixedSize(true);



        outerRVAdapter = new OuterRVAdapter( list );
        outerRecyclerView.setAdapter(outerRVAdapter);
        LinearLayoutManager llm = new LinearLayoutManager(getContext());
        outerRecyclerView.setLayoutManager(llm);
        return view;
    }
}
