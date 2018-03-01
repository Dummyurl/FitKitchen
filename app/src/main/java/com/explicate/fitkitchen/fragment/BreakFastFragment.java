package com.explicate.fitkitchen.fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.explicate.fitkitchen.R;
import com.explicate.fitkitchen.adapter.MealSubItemAdapter;
import com.explicate.fitkitchen.model.MealSubItemModel;
import com.explicate.fitkitchen.utility.RecyclerItemClickListener;
import com.explicate.fitkitchen.utility.Utility;

import java.util.ArrayList;

/**
 * Created by Mahesh Nikam on 31/01/2017.
 */

public class BreakFastFragment extends Fragment {

    private MealSubItemAdapter mealSubItemAdapter;
    private ArrayList<MealSubItemModel> mealSubItemModelArrayList = new ArrayList<>();
    private String type;

    public BreakFastFragment()
    {
        super();
    }

    public BreakFastFragment(ArrayList<MealSubItemModel> list,String type)
    {
        this.mealSubItemModelArrayList = list;
        this.type=type;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.master_recycler_view,container,false);

        RecyclerView list =(RecyclerView)rootView.findViewById(R.id.master_recycler_view);
        list.setLayoutManager(new LinearLayoutManager(getActivity()));
        list.setPadding(list.getPaddingLeft(), list.getPaddingTop(),list.getPaddingRight(), list.getPaddingBottom());

        final ArrayList<MealSubItemModel> updatedList = new ArrayList<>();

        if(!mealSubItemModelArrayList.isEmpty())
        {

            for(MealSubItemModel mealSubItemModel:mealSubItemModelArrayList)
            {
                if(mealSubItemModel.getSubMealCategaory().equalsIgnoreCase(type))
                {
                    updatedList.add(mealSubItemModel);
                }
            }

        }

        mealSubItemAdapter = new MealSubItemAdapter(getActivity(),updatedList,"");
        list.setAdapter(mealSubItemAdapter);
        list.addOnItemTouchListener(new RecyclerItemClickListener(getActivity(), new RecyclerItemClickListener.OnItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {


                Utility.showToast(getContext(),updatedList.get(position).getSubmealName());

            }
        }));


        return rootView;
    }

}
