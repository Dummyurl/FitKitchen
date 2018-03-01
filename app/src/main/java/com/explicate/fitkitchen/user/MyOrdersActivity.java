package com.explicate.fitkitchen.user;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;

import com.explicate.fitkitchen.R;
import com.explicate.fitkitchen.adapter.MyOrdersAdapter;
import com.explicate.fitkitchen.application.App;
import com.explicate.fitkitchen.model.MyOrdersModel;
import com.explicate.fitkitchen.utility.GetData;
import com.explicate.fitkitchen.utility.RecyclerItemClickListener;
import com.explicate.fitkitchen.utility.URLListner;
import com.explicate.fitkitchen.utility.Utility;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Mahesh Nikam on 20/01/2017.
 */

public class MyOrdersActivity extends AppCompatActivity {

    private static final String TAG = MyOrdersActivity.class.getSimpleName();

    private ArrayList<MyOrdersModel> myOrdersList = new ArrayList<>();
    private MyOrdersAdapter myOrdersAdapter;
    private Toolbar toolbar;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_myorders);
        setUpViews();
    }

    private void setUpViews() {

        toolbar = (Toolbar)findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setTitle("My orders");

        final RecyclerView list = (RecyclerView) findViewById(R.id.master_recycler_view);
        list.setLayoutManager(new LinearLayoutManager(this));
        list.setPadding(list.getPaddingLeft(), list.getPaddingTop(), list.getPaddingRight(), list.getPaddingBottom());
       myOrdersAdapter = new MyOrdersAdapter(MyOrdersActivity.this,myOrdersList);
        list.setAdapter(myOrdersAdapter);
        list.addOnItemTouchListener(new RecyclerItemClickListener(this, new RecyclerItemClickListener.OnItemClickListener() {
            @Override
            public void onItemClick(final View view, int position) {

                Utility.showToast(MyOrdersActivity.this,""+position);
            }

        }));

        getMyOrdersData();
    }

    //.........Get My orders data data.......
    private void getMyOrdersData() {

        Utility.showProgressDialog(this,"Please wait...");
        Utility.progressDialog.show();


        List<NameValuePair> params=new ArrayList<>();

       params.add(new BasicNameValuePair("user_id", App.getUserId()));


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

                    Log.e("MealFragment=","Status:"+success);


                    if(success==1)
                    {

                        JSONArray arr = jsonObject.getJSONArray("order");
                        for(int i=0;i<arr.length();i++)
                        {

                            JSONObject json = arr.getJSONObject(i);
                            MyOrdersModel myOrdersModel = new MyOrdersModel();

                            myOrdersModel.setOrderId(json.getString("order_id"));
                            myOrdersModel.setOrderTitle(json.getString("meal_short_name"));
                            myOrdersModel.setOrderAmount(json.getString("amount"));
                            myOrdersModel.setOrderImage(json.getString("meal_image"));

                            myOrdersList.add(myOrdersModel);
                        }

                        myOrdersAdapter.notifyDataSetChanged();
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
                    Utility.showToast(MyOrdersActivity.this,getString(R.string.failed));
                }


            }
        });
        getData.execute(URLListner.URL+URLListner.MY_ORDERS);
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
