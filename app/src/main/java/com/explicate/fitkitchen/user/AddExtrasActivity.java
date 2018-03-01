package com.explicate.fitkitchen.user;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.explicate.fitkitchen.R;
import com.explicate.fitkitchen.adapter.ExtrasItemAdapter;
import com.explicate.fitkitchen.model.CartModel;
import com.explicate.fitkitchen.model.ExtrasItemModel;
import com.explicate.fitkitchen.model.MealSubItemModel;
import com.explicate.fitkitchen.utility.GetData;
import com.explicate.fitkitchen.utility.RecyclerItemClickListener;
import com.explicate.fitkitchen.utility.URLListner;
import com.explicate.fitkitchen.utility.Utility;
import com.squareup.picasso.Picasso;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Mahesh Nikam on 01/02/2017.
 */

public class AddExtrasActivity extends AppCompatActivity implements View.OnClickListener {

    private static final String TAG = AddExtrasActivity.class.getSimpleName();

    private Toolbar toolbar;
    private RecyclerView extra_item,extra_sub_item;
    private ArrayList<ExtrasItemModel> extrasItemModelArrayList = new ArrayList<>();
    private ExtrasItemAdapter extrasItemAdapter;

    private ArrayList<MealSubItemModel> mealSubItemModelArrayList = new ArrayList<>();
    private extrasSubItemAdapter extrasSubItemAdapter;

    private ArrayList<CartModel> cartModelArrayList = new ArrayList<>();

    private String extraItemId,extraItemName;
    private Button apply;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_extras);
        cartModelArrayList.clear();
        setUpViews();
    }

    private void setUpViews() {

        toolbar = (Toolbar)findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setTitle("Extras");

        extra_item = (RecyclerView)findViewById(R.id.rv_extra_items);
        extra_item.setLayoutManager(new LinearLayoutManager(this));
        extra_item.addOnItemTouchListener(new RecyclerItemClickListener(this, new RecyclerItemClickListener.OnItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {

                extra_sub_item.setVisibility(View.VISIBLE);

                extraItemId = extrasItemModelArrayList.get(position).getExtrasItemId();
                extraItemName = extrasItemModelArrayList.get(position).getExtrasItemName();

                mealSubItemModelArrayList.clear();
                getExtraSubItems();
            }
        }));

        extra_sub_item = (RecyclerView)findViewById(R.id.rv_extra_sub_item);
        extra_sub_item.setLayoutManager(new LinearLayoutManager(this));
        extra_sub_item.setVisibility(View.GONE);

        getExtraItemsData();

        apply = (Button)findViewById(R.id.btn_apply);
        apply.setOnClickListener(this);

    }

    //.......Code for get extra main items data...........
    private void getExtraItemsData() {

        extrasItemModelArrayList.clear();

        Utility.showProgressDialog(AddExtrasActivity.this,"Please wait...");
        Utility.progressDialog.show();


        List<NameValuePair> params=new ArrayList<>();


        GetData getData = new GetData(params);
        getData.setResultListner(new GetData.ResultListner() {

            int success=0;
            @Override
            public void success(JSONObject jsonObject) {

                if(Utility.progressDialog.isShowing())
                {
                    Utility.progressDialog.dismiss();
                }

                try{

                    success=jsonObject.getInt("success");


                    if(success==1)
                    {

                        JSONArray arr = jsonObject.getJSONArray("extra");
                        for(int i=0;i<arr.length();i++)
                        {

                            JSONObject json = arr.getJSONObject(i);
                            ExtrasItemModel extrasItemModel = new ExtrasItemModel();

                            extrasItemModel.setExtrasItemId(json.getString("extra_id"));
                            extrasItemModel.setExtrasItemName(json.getString("extra_name"));
                            extrasItemModel.setExtrasItemImageUrl(json.getString("extra_image"));

                          extrasItemModelArrayList.add(extrasItemModel);
                        }


                        extrasItemAdapter = new ExtrasItemAdapter(AddExtrasActivity.this,extrasItemModelArrayList);
                        extra_item.setAdapter(extrasItemAdapter);

                        extrasItemAdapter.notifyDataSetChanged();
                    }
                    if(success!=1)
                    {
                        //Utility.showSnackbar(relativeLayout,getString(R.string.failed));
                    }

                }catch (Exception e){

                    e.printStackTrace();
                }
            }

            @Override
            public void fail() {

                if(Utility.progressDialog.isShowing())
                {
                    Utility.progressDialog.dismiss();
                }

                if(success!=1)
                {
                    Utility.showToast(AddExtrasActivity.this,getString(R.string.failed));
                }


            }
        });
        getData.execute(URLListner.URL+URLListner.EXTRA_ITEM);
    }

    //.......Code for get extra sub items data...........
    private void getExtraSubItems() {

        Utility.showProgressDialog(AddExtrasActivity.this,"Please wait...");
        Utility.progressDialog.show();


        List<NameValuePair> params=new ArrayList<>();

        params.add(new BasicNameValuePair("extra_id",extraItemId));

        GetData getData = new GetData(params);
        getData.setResultListner(new GetData.ResultListner() {

            int success=0;
            @Override
            public void success(JSONObject jsonObject) {

                if(Utility.progressDialog.isShowing())
                {
                    Utility.progressDialog.dismiss();
                }

                try{

                    success=jsonObject.getInt("success");


                    if(success==1)
                    {

                        JSONArray arr = jsonObject.getJSONArray("extra_dish");
                        for(int i=0;i<arr.length();i++)
                        {

                            JSONObject json = arr.getJSONObject(i);
                            MealSubItemModel mealSubItemModel = new MealSubItemModel();

                            mealSubItemModel.setSubmealId(json.getString("extra_dish_id"));
                            mealSubItemModel.setSubmealName(json.getString("extra_dish_name"));
                            mealSubItemModel.setSubimageUrl(json.getString("extra_dish_image"));
                            mealSubItemModel.setSubmealPrice(json.getString("extra_dish_price"));

                            mealSubItemModelArrayList.add(mealSubItemModel);

                        }


                        extrasSubItemAdapter = new extrasSubItemAdapter(AddExtrasActivity.this,mealSubItemModelArrayList,"extra");
                        extra_sub_item.setAdapter(extrasSubItemAdapter);


                        extrasSubItemAdapter.notifyDataSetChanged();
                    }
                    if(success!=1)
                    {
                        //Utility.showSnackbar(relativeLayout,getString(R.string.failed));
                    }

                }catch (Exception e){

                    e.printStackTrace();
                }
            }

            @Override
            public void fail() {

                if(Utility.progressDialog.isShowing())
                {
                    Utility.progressDialog.dismiss();
                }

                if(success!=1)
                {
                    Utility.showToast(AddExtrasActivity.this,getString(R.string.failed));
                }


            }
        });
        getData.execute(URLListner.URL+URLListner.EXTRA_ITEM_DISH);

    }


    @Override
    public void onClick(View v) {

        switch (v.getId()) {
            //...........Code for on click button apply..........
            case R.id.btn_apply:

                Intent intent = new Intent();
                Bundle bundle = new Bundle();
                bundle.putSerializable("CART_DATA", cartModelArrayList);
                intent.putExtras(bundle);
                setResult(005, intent);
                finish();
                break;
        }
    }

    //...............Meal Sub item adapter.........
    public class extrasSubItemAdapter extends RecyclerView.Adapter<extrasSubItemAdapter.CustomViewHolder>  {


        private ArrayList<MealSubItemModel> slist;
        Context mycontext;
        private String type,subItemName;
        private int price=0;

        public extrasSubItemAdapter(Context context, ArrayList<MealSubItemModel> object, String type) {

            this.slist = object;
            this.mycontext = context;
            this.type = type;
        }


        @Override
        public  CustomViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {

            View v;

            if(type.equalsIgnoreCase("extra"))
            {
                v = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.meal_sub_item, viewGroup,false);

            }
            else
            {
                v = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.meal_sub_item, null);
            }
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
                        .load(URLListner.EXTRA_DISH_IMAGE_PATH+item.getSubimageUrl())
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
                holder.sub_meal_checkbox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                    @Override
                    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {

                        CartModel cartModel = new CartModel();
                        if(isChecked==true)
                        {
                            boolean ispresent =false;
                            if(!cartModelArrayList.isEmpty())
                            {
                                for(CartModel model:cartModelArrayList)
                                {
                                    if(model.getExtraDishName().equalsIgnoreCase(item.getSubmealName()))
                                    {
                                        ispresent=true;
                                    }
                                }
                                if(!ispresent)
                                {
                                    cartModel.setExtraMainName(extraItemName);
                                    cartModel.setExtraMainId(extraItemId);
                                    cartModel.setExtraDishName(item.getSubmealName());
                                    cartModel.setExtraDishPrice(item.getSubmealPrice());
                                    cartModel.setExtraDishImage(item.getSubimageUrl());

                                    cartModelArrayList.add(cartModel);
                                }
                            }
                            else {
                                cartModel.setExtraMainName(extraItemName);
                                cartModel.setExtraMainId(extraItemId);
                                cartModel.setExtraDishName(item.getSubmealName());
                                cartModel.setExtraDishPrice(item.getSubmealPrice());
                                cartModel.setExtraDishImage(item.getSubimageUrl());

                                cartModelArrayList.add(cartModel);

                            }
                        }
                        else
                        {
                            /*cartModel.setExtraMainName(extraItemName);
                            cartModel.setExtraMainId(extraItemId);
                            cartModel.setExtraDishName(item.getSubmealName());
                            cartModel.setExtraDishPrice(item.getSubmealPrice());*/

                            cartModelArrayList.remove(cartModel);
                        }

                    }
                });
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
            protected CheckBox sub_meal_checkbox;
            protected LinearLayout price_container;

            public CustomViewHolder(View itemView) {
                super(itemView);

                this.sub_meal_title = (TextView)itemView.findViewById(R.id.tv_submeal_title);
                this.sub_meal_image = (ImageView)itemView.findViewById(R.id.iv_submeal_image);
                this.sub_meal_price = (TextView)itemView.findViewById(R.id.tv_submeal_price);
                this.sub_meal_checkbox = (CheckBox)itemView.findViewById(R.id.cb_submeal);
                this.price_container = (LinearLayout) itemView.findViewById(R.id.price_container);

            }
        }
    }


    //..........Code for Press Back arrow..........
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId())
        {
            case android.R.id.home:

                onBackPressed();

                return true;
        }
        return false;
    }
}
