package com.explicate.fitkitchen.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.explicate.fitkitchen.R;
import com.explicate.fitkitchen.model.MyOrdersModel;
import com.explicate.fitkitchen.utility.URLListner;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

/**
 * Created by Mahesh Nikam on 23/01/2017.
 */

public class MyOrdersAdapter extends RecyclerView.Adapter<MyOrdersAdapter.CustomViewHolder> {


    private ArrayList<MyOrdersModel> slist;
    Context mycontext;


    public MyOrdersAdapter(Context context, ArrayList<MyOrdersModel> object) {

        this.slist = object;
        this.mycontext = context;
    }


    @Override
    public MyOrdersAdapter.CustomViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {

        View v = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.myorder_item, null);
        MyOrdersAdapter.CustomViewHolder mh = new MyOrdersAdapter.CustomViewHolder(v);

        return mh;
    }


    @Override
    public void onBindViewHolder(final MyOrdersAdapter.CustomViewHolder holder, final int position) {

        MyOrdersModel item=slist.get(position);

        holder.order_title.setText(item.getOrderTitle());
        holder.order_amount.setText(item.getOrderAmount());
        //holder.review_count.setText(item.getRewiewCount());

        if(!item.getOrderImage().equalsIgnoreCase("null")){

            Picasso.with(mycontext)
                    .load(URLListner.MEAL_IMAGE_PATH+item.getOrderImage())
                    .placeholder(R.drawable.meal_placeholder)
                    .fit()
                    .centerCrop()
                    .into(holder.order_img);
        }else{

            //  Picasso.with(mycontext).load(R.drawable.placeholder).resize(40,40).into(holder.left_img);
        }
    }

    @Override
    public int getItemCount() {

        return (null != slist ? slist.size() : 0);
    }

    public class CustomViewHolder extends RecyclerView.ViewHolder {


        protected TextView order_title,order_amount;

        protected ImageView order_img;


        public CustomViewHolder(View v) {
            super(v);

            this.order_title=(TextView)v.findViewById(R.id.tv_myorder_title);
            this.order_amount =(TextView)v.findViewById(R.id.tv_myorder_amt);

            this.order_img=(ImageView)v.findViewById(R.id.iv_myorder_image);

        }
    }
}
