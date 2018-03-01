package com.explicate.fitkitchen.user;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RelativeLayout;

import com.explicate.fitkitchen.R;
import com.explicate.fitkitchen.utility.GetData;
import com.explicate.fitkitchen.utility.URLListner;
import com.explicate.fitkitchen.utility.Utility;
import com.explicate.fitkitchen.utility.Validate;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Mahesh Nikam on 12/01/2017.
 */

public class ForgotPasswordActivity extends AppCompatActivity implements View.OnClickListener {

    private static final String TAG = ForgotPasswordActivity.class.getSimpleName();

    private RelativeLayout relativeLayout;
    private EditText email;
    private Button forgot_password;
    private String userEmail;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_forgot_password);
        setUpViews();
    }

    private void setUpViews() {

        relativeLayout = (RelativeLayout)findViewById(R.id.forgotPassActivity_RL);

        email = (EditText)findViewById(R.id.forgot_pass_email_edt);
        forgot_password = (Button)findViewById(R.id.forgot_pass_btn);
        forgot_password.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId())
        {
            case R.id.forgot_pass_btn:
                userEmail = email.getText().toString().trim();
                Utility.hideKeyboard(v,this);

                if( userEmail.equals(""))
                {
                    email.setError(getString(R.string.please_enter_your_email_id));
                }
                else if(!Validate.isValidEmail(userEmail))
                {
                    email.setError(getString(R.string.please_enter_valid_email_address));
                }
                else
                {
                    if (!Utility.isConnected(this))
                    {
                        Utility.showSnackbar(relativeLayout, getString(R.string.connection_failed));
                    }
                    else {

                        setForgotPassword();//....send password on users email....
                    }

                }

                break;
        }
    }

    //.........Send password on users email.......
    private void setForgotPassword() {

        Utility.showProgressDialog(this,"Please wait..");
        Utility.progressDialog.show();

        List<NameValuePair> params=new ArrayList<>();

        params.add(new BasicNameValuePair("user_email",userEmail));

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
                    Log.e(TAG,"Error:"+success);

                    if(success==1)
                    {
                        Utility.showSnackbar(relativeLayout,getString(R.string.reset_password_link_send_on_email));

                        Snackbar snackbar = Snackbar
                                .make(relativeLayout, R.string.reset_password_link_send_on_email, Snackbar.LENGTH_INDEFINITE)
                                .setAction("OK", new View.OnClickListener() {
                                    @Override
                                    public void onClick(View view) {
                                       Utility.getIntent(ForgotPasswordActivity.this,LoginActivity.class);
                                    }
                                });

                        snackbar.show();
                    }
                    else {
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
        getData.execute(URLListner.URL+URLListner.FORGOT_PASSWORD);

        clearFields();//.....clear all field...
    }

    //....Clear all fields...
    private void clearFields() {

        email.setText("");
    }
}
