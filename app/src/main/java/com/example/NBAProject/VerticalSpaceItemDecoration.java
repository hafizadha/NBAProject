package com.example.NBAProject;

import android.graphics.Rect;
import android.view.View;

// Not RELEVANT TO TOPIC
// Only to add spaces between layouts in a RecyclerView
import androidx.recyclerview.widget.RecyclerView;


//This class is purely for decoration purposes ( creating spaces between items in a Recycler View )
//None of the basic features can be found here
public class VerticalSpaceItemDecoration extends RecyclerView.ItemDecoration {

    private final int verticalSpaceHeight;

    //Received the magnitude of space (in dp)
    public VerticalSpaceItemDecoration(int verticalSpaceHeight) {
        this.verticalSpaceHeight = verticalSpaceHeight;
    }
    @Override
    public void getItemOffsets(Rect outRect, View view, RecyclerView parent, RecyclerView.State state) {
        super.getItemOffsets(outRect, view, parent, state);

        // Add spacing to the bottom of each item
        outRect.bottom = verticalSpaceHeight;
        // Add pacing to the top of the first item
        if(parent.getChildAdapterPosition(view) == 0){
            outRect.top = verticalSpaceHeight;
        }
    }
}

