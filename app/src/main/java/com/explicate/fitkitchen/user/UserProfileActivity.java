package com.explicate.fitkitchen.user;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.explicate.fitkitchen.R;
import com.explicate.fitkitchen.application.App;
import com.explicate.fitkitchen.utility.GetData;
import com.explicate.fitkitchen.utility.MyPreferences;
import com.explicate.fitkitchen.utility.URLListner;
import com.explicate.fitkitchen.utility.Utility;
import com.squareup.picasso.Picasso;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

/**
 * Created by Mahesh Nikam on 12/01/2017.
 */

public class UserProfileActivity extends Fragment implements View.OnClickListener {

    private static final String TAG = UserProfileActivity.class.getSimpleName();

    private RelativeLayout relativeLayout;
    private Toolbar toolbar;
    private CircleImageView profile_picture;
    private TextView user_name,user_email,user_phone,my_orders,settings;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.activity_user_profile,container,false);

        setUpViews(rootView);
        setHasOptionsMenu(true);

        return rootView;
    }


    private void setUpViews(View rootView) {

        relativeLayout = (RelativeLayout)rootView.findViewById(R.id.userProfileActivity_RL);

        profile_picture = (CircleImageView)rootView.findViewById(R.id.profile_picture_civ);

        user_name = (TextView)rootView.findViewById(R.id.profile_name_tv);
        user_phone = (TextView)rootView.findViewById(R.id.profile_phone_tv);
        user_email = (TextView)rootView.findViewById(R.id.profile_email_tv);

        my_orders = (TextView)rootView.findViewById(R.id.myorders_tv);
        my_orders.setOnClickListener(this);

        settings = (TextView)rootView.findViewById(R.id.settings_tv);
        settings.setOnClickListener(this);


        setPrefferenceData();
    }

    //...get all user details from shared prefferences and set to the fields...
    private void setPrefferenceData() {

        Picasso.with(getActivity())
                .load(URLListner.IMAGE_PATH+App.getUserImage())
                .fit()
                .centerCrop()
                .placeholder(R.drawable.placeholder)
                .into(profile_picture);

        user_name.setText(App.getUserName());
        user_phone.setText(App.getUserPhone());
        user_email.setText(App.getUserEmail());
    }

    @Override
    public void onStart() {
        super.onStart();
        getUserDetails();
    }

    //.....Get user data from server and store into preffrences....
    private void getUserDetails() {

            Utility.showProgressDialog(getActivity(),"Please wait..");
            Utility.progressDialog.show();

            List<NameValuePair> params=new ArrayList<>();


            params.add(new BasicNameValuePair("user_id",App.getUserId()));

            GetData getData = new GetData(params);
            getData.setResultListner(new GetData.ResultListner() {

                int success=0;
                private String id,username,phone,email1,image,mobileVerify,emailVerify,otp,socialType,socialId;
                @Override
                public void success(JSONObject jsonObject) {

                    if(Utility.progressDialog.isShowing())
                    {
                        Utility.progressDialog.dismiss();
                    }

                    try{

                        success=jsonObject.getInt("success");

                        Log.e(TAG,"Status:"+success);

                        if(success==1)
                        {

                            JSONArray arr = jsonObject.getJSONArray("info");
                            for(int i=0;i<arr.length();i++)
                            {

                                JSONObject json = arr.getJSONObject(i);

                                id = json.getString("user_id");
                                username = json.getString("firstname");
                                email1 = json.getString("user_email");
                                phone = json.getString("user_mobile");
                                image = json.getString("user_image");
                                mobileVerify = json.getString("user_mobile_verify");
                                emailVerify = json.getString("user_email_verify");
                                otp = json.getString("user_otp");
                                socialType = json.getString("user_social_type");
                                socialId = json.getString("user_social_id");

                            }

                            SharedPreferences sharedPreferences = getActivity().getSharedPreferences(MyPreferences.My_PREFRENCES, Context.MODE_PRIVATE);
                            SharedPreferences.Editor editor = sharedPreferences.edit();

                            editor.putString(MyPreferences.USER_ID,id);
                            editor.putString(MyPreferences.USER_NAME,username);
                            editor.putString(MyPreferences.USER_EMAIL,email1);
                            editor.putString(MyPreferences.USER_PHONE,phone);
                            editor.putString(MyPreferences.USER_IMAGE,image);

                            editor.commit();

                            if(!App.getUserId().equalsIgnoreCase(""))
                            {
                                setPrefferenceData();
                                //Log.e(TAG,"Image path"+URLListner.IMAGE_PATH+App.getUserImage());
                                //Utility.showSnackbar(relativeLayout,getString(R.string.success));
                            }
                            else
                            {
                                Utility.showSnackbar(relativeLayout,"Failed to store user details");
                            }

                        }
                        if(success!=1)
                        {
                            Utility.showSnackbar(relativeLayout,getString(R.string.failed));
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
                        Utility.showSnackbar(relativeLayout,getString(R.string.failed));
                    }


                }
            });
            getData.execute(URLListner.URL+URLListner.USER_PROFILE_DETAILS);



    }

    @Override
    public void onClick(View v) {

        switch (v.getId())
        {
            case R.id.myorders_tv:

                //Utility.showToast(UserProfileActivity.this,"My orders");
                Utility.getIntent(getActivity(),MyOrdersActivity.class);

                break;
        }

    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {

       getActivity().getMenuInflater().inflate(R.menu.menu_profile,menu);


    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId())
        {
            //....Goto edit profile activity
            case R.id.action_edit:

                Utility.getIntent(getActivity(),EditProfileActivity.class);

                return true;
        }
        return false;
    }


}
