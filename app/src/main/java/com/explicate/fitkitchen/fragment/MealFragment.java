package com.explicate.fitkitchen.fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import com.explicate.fitkitchen.R;
import com.explicate.fitkitchen.adapter.MealAdapter;
import com.explicate.fitkitchen.model.MealModel;
import com.explicate.fitkitchen.user.MealDetailActivity;
import com.explicate.fitkitchen.utility.GetData;
import com.explicate.fitkitchen.utility.GridSpacingItemDecoration;
import com.explicate.fitkitchen.utility.RecyclerItemClickListener;
import com.explicate.fitkitchen.utility.URLListner;
import com.explicate.fitkitchen.utility.Utility;

import org.apache.http.NameValuePair;
import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * Created by Mahesh Nikam on 18/01/2017.
 */

public class MealFragment extends Fragment {

    private ArrayList<MealModel> mealModelList = new ArrayList<>();
    private MealAdapter adapter;
    private LinearLayout err_container;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.master_recycler_view,container,false);

        err_container = (LinearLayout)rootView.findViewById(R.id.plan_unavailable_container);
        err_container.setVisibility(View.GONE);

        final RecyclerView list =(RecyclerView)rootView.findViewById(R.id.master_recycler_view);
        list.setLayoutManager(new GridLayoutManager(getActivity(),2));
        list.setPadding(list.getPaddingLeft(), list.getPaddingTop(),list.getPaddingRight(), list.getPaddingBottom());
        int spanCount = 2; // 3 columns
        int spacing = 10; // 50px
        boolean includeEdge = true;
        list.addItemDecoration(new GridSpacingItemDecoration(spanCount, spacing, includeEdge));
        adapter = new MealAdapter(getActivity(), mealModelList);
        list.setAdapter(adapter);
        list.addOnItemTouchListener(new RecyclerItemClickListener(getActivity(), new RecyclerItemClickListener.OnItemClickListener() {
            @Override
            public void onItemClick(final View view, int position) {

                HashMap<String,String> map = new HashMap<String, String>();

                map.put("meal_title",String.valueOf(mealModelList.get(position).getMealShortName()));
                map.put("meal_price",String.valueOf(mealModelList.get(position).getMealPrice()));
                map.put("meal_id",String.valueOf(mealModelList.get(position).getMealId()));

                //Utility.showToast(getContext(), String.valueOf(mealModelList.get(position).getMealShortName()));
                Utility.getIntent(getActivity(), MealDetailActivity.class,map);
            }
        }));

        getMealData();
        return rootView;

    }


    //.........Get meal data.......
    private void getMealData() {

        Utility.showProgressDialog(getActivity(),"Please wait...");
       // Utility.progressDialog.show();


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

                    Log.e("MealFragment=","Status:"+success);


                    if(success==1)
                    {

                        JSONArray arr = jsonObject.getJSONArray("meal_plan");
                        for(int i=0;i<arr.length();i++)
                        {

                            JSONObject json = arr.getJSONObject(i);
                            MealModel mealModel = new MealModel();

                            mealModel.setMealId(json.getString("meal_id"));
                            mealModel.setMealShortName(json.getString("meal_short_name"));
                            mealModel.setMealDescription(json.getString("meal_description"));
                            mealModel.setImageUrl(json.getString("meal_image"));
                            mealModel.setMealPrice(json.getString("meal_price"));

                            mealModelList.add(mealModel);
                        }

                        adapter.notifyDataSetChanged();
                    }
                    if(success!=1)
                    {
                        err_container.setVisibility(View.VISIBLE);
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
                    Utility.showToast(getActivity(),getString(R.string.failed));
                }


            }
        });
        getData.execute(URLListner.URL+URLListner.MEAL_PLAN);
    }

}
