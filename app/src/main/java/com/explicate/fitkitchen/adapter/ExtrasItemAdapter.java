package com.explicate.fitkitchen.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.explicate.fitkitchen.R;
import com.explicate.fitkitchen.model.ExtrasItemModel;
import com.explicate.fitkitchen.utility.URLListner;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

/**
 * Created by Mahesh Nikam on 01/02/2017.
 */

public class ExtrasItemAdapter extends RecyclerView.Adapter<ExtrasItemAdapter.CustomViewHolder> {


    private ArrayList<ExtrasItemModel> slist;
    Context mycontext;


    public ExtrasItemAdapter(Context context, ArrayList<ExtrasItemModel> object) {

        this.slist = object;
        this.mycontext = context;
    }


    @Override
    public ExtrasItemAdapter.CustomViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {

        View v = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.extras_item,null);
        ExtrasItemAdapter.CustomViewHolder mh = new ExtrasItemAdapter.CustomViewHolder(v);

        return mh;
    }

    @Override
    public void onBindViewHolder(final ExtrasItemAdapter.CustomViewHolder holder, final int position) {

        ExtrasItemModel item=slist.get(position);

        holder.extras_item_title.setText(item.getExtrasItemName());
        //holder.review_count.setText(item.getRewiewCount());

        if(!item.getExtrasItemImageUrl().equalsIgnoreCase("null")){

            Picasso.with(mycontext)
                    .load(URLListner.EXTRA_ITEM_IMAGE_PATH+item.getExtrasItemImageUrl())
                    .fit()
                    .centerCrop()
                    .into(holder.extras_item_image);
        }else{

            //  Picasso.with(mycontext).load(R.drawable.placeholder).resize(40,40).into(holder.left_img);
        }

    }

    @Override
    public int getItemCount() {

        return (null != slist ? slist.size() : 0);
    }

    public class CustomViewHolder extends RecyclerView.ViewHolder {

        protected TextView extras_item_title;
        protected ImageView extras_item_image;

        public CustomViewHolder(View itemView) {
            super(itemView);

            this.extras_item_title = (TextView)itemView.findViewById(R.id.tv_extras_title);
            this.extras_item_image = (ImageView)itemView.findViewById(R.id.iv_extras_image);
        }
    }
}
