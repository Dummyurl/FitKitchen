package com.explicate.fitkitchen.user;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RelativeLayout;

import com.explicate.fitkitchen.R;
import com.explicate.fitkitchen.application.App;
import com.explicate.fitkitchen.utility.GetData;
import com.explicate.fitkitchen.utility.MyPreferences;
import com.explicate.fitkitchen.utility.URLListner;
import com.explicate.fitkitchen.utility.Utility;
import com.explicate.fitkitchen.utility.Validate;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Mahesh Nikam on 16/01/2017.
 */

public class SocialRegistrationActivity extends AppCompatActivity implements View.OnClickListener {

    private static final String TAG = SocialRegistrationActivity.class.getSimpleName();

    private RelativeLayout relativeLayout;
    private EditText user_phone,user_name,user_email;
    private Button registration;
    private String userPhone,socialName,socialEmail,socialId,socialType;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_social_registration);
        setUpViews();

    }

    private void setUpViews() {

        relativeLayout = (RelativeLayout)findViewById(R.id.phoneNoActivity_RL);

        user_phone = (EditText)findViewById(R.id.social_phone_no_edt);
        user_email = (EditText)findViewById(R.id.social_email_edt);
        user_name = (EditText)findViewById(R.id.social_name_edt);

        registration = (Button)findViewById(R.id.social_registeration_btn);
        registration.setOnClickListener(this);

        socialId = getIntent().getStringExtra("social_id");
        socialName = getIntent().getStringExtra("social_name");
        socialEmail = getIntent().getStringExtra("social_email");
        socialType = getIntent().getStringExtra("social_type");

        user_name.setText(socialName);
        user_email.setText(socialEmail);

       // Toast.makeText(SocialRegistrationActivity.this,"\n"+socialId+"\n"+socialType,Toast.LENGTH_LONG).show();

    }

    @Override
    public void onClick(View v) {
        switch (v.getId())
        {
            case R.id.social_registeration_btn:

                userPhone = user_phone.getText().toString().trim();

                if( userPhone.equals(""))
                {
                    user_phone.setError(getString(R.string.please_enter_your_phone_no));
                }
                else if(!Validate.isValidPhone(userPhone))
                {
                    user_phone.setError(getString(R.string.please_enter_valid_phone_no));
                }
                else
                {
                    if (!Utility.isConnected(this))
                    {
                        Utility.showSnackbar(relativeLayout, getString(R.string.connection_failed));
                    }
                    else {

                        setUserData();//....send user data to server....
                    }

                }
                break;
        }
    }

    private void setUserData() {

        Utility.showProgressDialog(this,"Please wait..");
        Utility.progressDialog.show();

        List<NameValuePair> params=new ArrayList<>();

        params.add(new BasicNameValuePair("user_name",socialName));
        params.add(new BasicNameValuePair("user_email",socialEmail));
        params.add(new BasicNameValuePair("user_mobile",userPhone));
        params.add(new BasicNameValuePair("social_id",socialId));
        params.add(new BasicNameValuePair("social_type",socialType));

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

                        JSONArray arr = jsonObject.getJSONArray("user");
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
                        Log.e(TAG,"Id"+id);

                        SharedPreferences sharedPreferences = getSharedPreferences(MyPreferences.My_PREFRENCES, Context.MODE_PRIVATE);
                        SharedPreferences.Editor editor = sharedPreferences.edit();

                        editor.putString(MyPreferences.USER_ID,id);
                        editor.putString(MyPreferences.USER_NAME,username);
                        editor.putString(MyPreferences.USER_EMAIL,email1);
                        editor.putString(MyPreferences.USER_PHONE,phone);
                        editor.putString(MyPreferences.USER_IMAGE,image);

                        editor.commit();

                        if(!App.getUserId().equalsIgnoreCase(""))
                        {
                            //GOTO MainActivity

                            finish();
                            Utility.showSnackbar(relativeLayout,getString(R.string.success));
                            Utility.getIntent(SocialRegistrationActivity.this,MainActivity.class);
                        }
                        else
                        {
                            Utility.showSnackbar(relativeLayout,"Failed to store this session");
                        }

                    }
                    else {
                        Utility.showSnackbar(relativeLayout,getString(R.string.please_enter_valid_details));
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
        getData.execute(URLListner.URL+URLListner.SOCIAL_LOGIN);

    }
}
