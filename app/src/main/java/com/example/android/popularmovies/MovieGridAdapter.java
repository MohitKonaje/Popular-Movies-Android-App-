package com.example.android.popularmovies;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import com.squareup.picasso.Picasso;

/**
 * Created by mk3s on 9/19/2017.
 */

public class MovieGridAdapter extends RecyclerView.Adapter<MovieGridAdapter.ItemViewHolder>  {

    Context context;
    MovieDetails[] mDataItems;
    private final movieGridAdapterOnClickHandler mClickHandler;


    MovieGridAdapter(Context c, movieGridAdapterOnClickHandler handler){
        this.context = c;
        mClickHandler = handler;
    }


    public interface movieGridAdapterOnClickHandler {
        void onClick(MovieDetails selected_movie);
    }



    public class ItemViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{

       //create and set views to be hold
        ImageView mPosterImage;
        ItemViewHolder (View view){
            super(view);
            mPosterImage = view.findViewById(R.id.main_poster_image);
            view.setOnClickListener(this);

        }

        //handle onclick interface
        @Override
        public void onClick(View v) {
            v.getId();
            int adapterPosition = getAdapterPosition();
            mClickHandler.onClick(mDataItems[adapterPosition]);
        }
    }


    @Override
    public ItemViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        Context context = parent.getContext();
        //set layout to load holder objects
        int layoutId = R.layout.poster_grid_items_layout;
        LayoutInflater inflater = LayoutInflater.from(context);
        View view =  inflater.inflate(layoutId,parent,false);
        return new ItemViewHolder(view);

    }

    @Override
    public int getItemCount() {

        if(mDataItems == null)
        {
        return 0;
        }
        else{
        return mDataItems.length;
        }
    }

    @Override
    public void onBindViewHolder(ItemViewHolder holder, int position) {


        String positionString=  String.valueOf(position);
        Log.d("Adding poster", positionString);
       // load image using picasso api
        Picasso.with(context).load(mDataItems[position].image).into(holder.mPosterImage);

    }
    //method to set data
    public void setData(MovieDetails[] movies_data){
        mDataItems =movies_data;

        //reload data
        notifyDataSetChanged();

    }
}
