package com.explicate.fitkitchen.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.explicate.fitkitchen.R;
import com.explicate.fitkitchen.model.MealModel;
import com.explicate.fitkitchen.utility.URLListner;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

/**
 * Created by Mahesh on 18/01/2017.
 */

public class MealAdapter extends RecyclerView.Adapter<MealAdapter.CustomViewHolder> {


    private ArrayList<MealModel> slist;
    Context mycontext;


    public MealAdapter(Context context, ArrayList<MealModel> object) {

        this.slist = object;
        this.mycontext = context;
    }


    @Override
    public CustomViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {

        View v = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.meal_item, null);
        CustomViewHolder mh = new CustomViewHolder(v);

        return mh;
    }

    @Override
    public void onBindViewHolder(final CustomViewHolder holder, final int position) {

        MealModel item=slist.get(position);

        holder.meal_title.setText(item.getMealShortName());
        holder.meal_price.setText(item.getMealPrice());
        //holder.review_count.setText(item.getRewiewCount());

        if(!item.getImageUrl().equalsIgnoreCase("null")){

            Picasso.with(mycontext)
                    .load(URLListner.MEAL_IMAGE_PATH+item.getImageUrl())
                    .placeholder(R.drawable.meal_placeholder)
                    .fit()
                    .centerCrop()
                    .into(holder.meal_img);
        }else{

            //  Picasso.with(mycontext).load(R.drawable.placeholder).resize(40,40).into(holder.left_img);
        }

    }

    @Override
    public int getItemCount() {

        return (null != slist ? slist.size() : 0);
    }

    public class CustomViewHolder extends RecyclerView.ViewHolder {


        protected TextView meal_title,review_count,meal_price;

        protected ImageView meal_img;


        public CustomViewHolder(View v) {
            super(v);

            this.meal_title=(TextView)v.findViewById(R.id.tv_meal_title);
            this.review_count =(TextView)v.findViewById(R.id.tv_review_count);
            this.meal_price =(TextView)v.findViewById(R.id.tv_price);

            this.meal_img=(ImageView)v.findViewById(R.id.iv_meal_image);

        }

    }
}
