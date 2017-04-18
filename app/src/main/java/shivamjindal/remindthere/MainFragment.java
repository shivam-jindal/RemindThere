package shivamjindal.remindthere;

import android.appwidget.AppWidgetProvider;
import android.content.DialogInterface;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.RectF;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.support.v7.widget.helper.ItemTouchHelper;
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
        outerRVAdapter = new OuterRVAdapter(databaseAdapter.getAllTasks(), getContext());
        outerRecyclerView.setAdapter(outerRVAdapter);

        staggeredGridLayoutManager = new StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL);
        linearLayoutManager = new LinearLayoutManager(getContext());
        outerRecyclerView.setLayoutManager(linearLayoutManager);
        initSwipe();
        return view;
    }


    private void initSwipe() {
        //ItemTouchHelper.SimpleCallback simpleItemTouchCallback = new ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT | ItemTouchHelper.RIGHT)
        ItemTouchHelper.SimpleCallback simpleItemTouchCallback = new ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT | ItemTouchHelper.RIGHT ) {

            @Override
            public boolean onMove(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, RecyclerView.ViewHolder target) {
                return false;
            }

            /**
             * When a notification is swiped add it to pending items
             * and if it is a bookmarked item then show a dialog for confirmation
             * @param viewHolder view holder object which is swiped
             * @param direction direction in which object is swiped
             */
            @Override
            public void onSwiped(RecyclerView.ViewHolder viewHolder, int direction) {
                final int swipedPosition = viewHolder.getAdapterPosition();
                final OuterRVAdapter adapter = (OuterRVAdapter) outerRecyclerView.getAdapter();
                //adapter.setUndoOn(true);
                //boolean undoOn = adapter.isUndoOn();
                new AlertDialog.Builder(getContext())
                        .setMessage("Are you sure that you want to delete this item?")
                        .setPositiveButton("DELETE", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int whichButton) {
                                adapter.removeItem(swipedPosition);
                            }
                        })
                        .setNegativeButton(android.R.string.no, new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int whichButton) {
                                adapter.notifyItemChanged(swipedPosition);
                            }
                        })
                        .setOnCancelListener(new DialogInterface.OnCancelListener() {
                            @Override
                            public void onCancel(DialogInterface dialogInterface) {
                                adapter.notifyItemChanged(swipedPosition);
                            }
                        }).show();
            }


            @Override
            public int getSwipeDirs(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder) {

                //make sure that no notification is expanded and action mode is not active
                //while performing swipe delete

                int position = viewHolder.getAdapterPosition();
                OuterRVAdapter adapter = (OuterRVAdapter) recyclerView.getAdapter();
                return super.getSwipeDirs(recyclerView, viewHolder);

            }

            /**
             * When the item is swiped draw red color and delete icon on its background
             */
            @Override
            public void onChildDraw(Canvas c, RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, float dX, float dY, int actionState, boolean isCurrentlyActive) {
                Bitmap icon;
                RectF background;
                RectF icon_dest;
                Paint p = new Paint();

                if (actionState == ItemTouchHelper.ACTION_STATE_SWIPE) {
                    View itemView = viewHolder.itemView;
                    float height = itemView.getHeight();

                    float width = height / 3;
                    p.setColor(ContextCompat.getColor(getContext(), R.color.delete_card_background));
                    Drawable deleteIcon = ContextCompat.getDrawable(getContext(), R.drawable.ic_multiselect_delete);
                    //convert drawable type icon into bitmap
                    icon = Constants.drawableToBitmap(deleteIcon);

                    if (dX > 0) {
                        float temp = dX;
                        if(temp > itemView.getWidth())
                            temp = itemView.getWidth();
                        if(temp < itemView.getLeft())
                            temp += itemView.getLeft();

                        background = new RectF((float) itemView.getLeft(),
                                (float) itemView.getTop(),
                                temp,
                                (float) itemView.getBottom());

                        icon_dest = new RectF((float) itemView.getLeft() + width,
                                (float) itemView.getTop() + width,
                                (float) itemView.getLeft() + 2 * width,
                                (float) itemView.getBottom() - width);
                    } else {
                        float temp2 = -dX;
                        if( temp2 > itemView.getWidth())
                            temp2 = itemView.getWidth();

                        background = new RectF((float) itemView.getRight() - temp2,
                                (float) itemView.getTop(),
                                (float) itemView.getRight(),
                                (float) itemView.getBottom());

                        icon_dest = new RectF((float) itemView.getRight() - 2 * width,
                                (float) itemView.getTop() + width,
                                (float) itemView.getRight() - width,
                                (float) itemView.getBottom() - width);
                    }
                    c.drawRect(background, p);
                    c.drawBitmap(icon, null, icon_dest, p);
                }
                super.onChildDraw(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive);
            }
        };

        ItemTouchHelper itemTouchHelper = new ItemTouchHelper(simpleItemTouchCallback);
        itemTouchHelper.attachToRecyclerView(outerRecyclerView);
    }
}
