package com.explicate.fitkitchen.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.explicate.fitkitchen.R;
import com.explicate.fitkitchen.model.CartModel;
import com.explicate.fitkitchen.model.MealSubItemModel;
import com.explicate.fitkitchen.utility.URLListner;
import com.explicate.fitkitchen.utility.Utility;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

/**
 * Created by Mahesh Nikam on 01/02/2017.
 */

public class MealSubItemAdapter extends RecyclerView.Adapter<MealSubItemAdapter.CustomViewHolder>  {


    private ArrayList<MealSubItemModel> slist;
    Context mycontext;
    private String type;
    private int price=0;

    public MealSubItemAdapter(Context context, ArrayList<MealSubItemModel> object, String type) {

        this.slist = object;
        this.mycontext = context;
        this.type = type;
    }


    @Override
    public CustomViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {

         View v = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.meal_sub_item, null);

        CustomViewHolder mh = new CustomViewHolder(v);


        return mh;
    }

    @Override
    public void onBindViewHolder(final CustomViewHolder holder, final int position) {

        final MealSubItemModel item=slist.get(position);

        holder.sub_meal_title.setText(item.getSubmealName());
        holder.sub_meal_price.setText(item.getSubmealPrice());

        //...Hide price...
        if(type.equalsIgnoreCase("extra"))
        {
            holder.price_container.setVisibility(View.VISIBLE);
        }
        else
        {
            holder.price_container.setVisibility(View.GONE);
        }

        if(!item.getSubimageUrl().equalsIgnoreCase("null")){

            Picasso.with(mycontext)
                    .load(URLListner.SUB_MEAL_ITEM_IMAGE_PATH+item.getSubimageUrl())
                    .placeholder(R.drawable.meal_placeholder)
                    .fit()
                    .centerCrop()
                    .into(holder.sub_meal_image);
        }else{

            //  Picasso.with(mycontext).load(R.drawable.placeholder).resize(40,40).into(holder.left_img);
        }

        //...Hide checkbox...
        if(type.equalsIgnoreCase("extra"))
        {
            holder.sub_meal_checkbox.setVisibility(View.VISIBLE);
        }
        else
        {
            holder.sub_meal_checkbox.setVisibility(View.GONE);
        }
    }

    @Override
    public int getItemCount() {

        return (null != slist ? slist.size() : 0);
    }

    public class CustomViewHolder extends RecyclerView.ViewHolder {

        protected TextView sub_meal_title,sub_meal_price;
        protected ImageView sub_meal_image;
        protected LinearLayout price_container;
        protected CheckBox sub_meal_checkbox;

        public CustomViewHolder(View itemView) {
            super(itemView);

            this.sub_meal_title = (TextView)itemView.findViewById(R.id.tv_submeal_title);
            this.sub_meal_image = (ImageView)itemView.findViewById(R.id.iv_submeal_image);
            this.sub_meal_price = (TextView)itemView.findViewById(R.id.tv_submeal_price);
            this.price_container = (LinearLayout) itemView.findViewById(R.id.price_container);
            this.sub_meal_checkbox = (CheckBox)itemView.findViewById(R.id.cb_submeal);
        }
    }
}





