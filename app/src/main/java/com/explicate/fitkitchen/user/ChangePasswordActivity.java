package com.explicate.fitkitchen.user;

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
import com.explicate.fitkitchen.utility.URLListner;
import com.explicate.fitkitchen.utility.Utility;
import com.explicate.fitkitchen.utility.Validate;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Mahesh Nikam on 16/01/2017.
 */

public class ChangePasswordActivity extends AppCompatActivity implements View.OnClickListener {

    private static final String TAG = ChangePasswordActivity.class.getSimpleName();

    private RelativeLayout relativeLayout;
    private EditText old_pass,new_pass,confirm_pass;
    private Button change_pass_btn;
    private String oldPassword,newPassword,confirmPassword,userId;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_change_password);

        setUpViews();
    }

    private void setUpViews() {

        relativeLayout = (RelativeLayout)findViewById(R.id.changePassActivity_RL);

        old_pass = (EditText)findViewById(R.id.old_pass_edt);
        new_pass = (EditText)findViewById(R.id.new_pass_edt);
        confirm_pass = (EditText)findViewById(R.id.confirm_pass_edt);

        change_pass_btn = (Button)findViewById(R.id.change_pass_btn);
        change_pass_btn.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId())
        {
            case R.id.change_pass_btn:
                Utility.hideKeyboard(v,this);

                oldPassword = old_pass.getText().toString().trim();
                newPassword = new_pass.getText().toString().trim();
                confirmPassword = confirm_pass.getText().toString().trim();
                userId = App.getUserId();

                if( oldPassword.equals(""))
                {
                    old_pass.setError(getString(R.string.please_enter_old_password));
                }
                else if( newPassword.equals(""))
                {
                    new_pass.setError(getString(R.string.please_enter_new_password));
                }
                else if(!Validate.isValidPassword(newPassword))
                {
                    if(newPassword.length()<6)
                    {
                        new_pass.setError(getString(R.string.password_should_be_6_character_in_length));
                    }
                    else if(newPassword.length()>15)
                    {
                        new_pass.setError(getString(R.string.password_is_too_long));
                    }
                    else
                    {
                        new_pass.setError(getString(R.string.password_should_contain));
                    }
                }
                else if (!confirmPassword.equals(newPassword) || confirmPassword.equals(""))
                {
                    confirm_pass.setError(getString(R.string.please_confirm_the_password));
                }
                else
                {
                    if (!Utility.isConnected(this))
                    {
                        Utility.showSnackbar(relativeLayout, getString(R.string.connection_failed));
                    }
                    else
                    {
                        resetUserPassword();
                    }

                }

                break;
        }
    }

    //.........Reset user password.......
    private void resetUserPassword() {

        Utility.showProgressDialog(this,"Please wait..");
        Utility.progressDialog.show();

        List<NameValuePair> params=new ArrayList<>();


        params.add(new BasicNameValuePair("user_id",userId));
        params.add(new BasicNameValuePair("user_password",oldPassword));
        params.add(new BasicNameValuePair("newpassword",newPassword));
        params.add(new BasicNameValuePair("confpassword",confirmPassword));

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
                        Utility.showSnackbar(relativeLayout,getString(R.string.reset_password_successfully));
                    }else {

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
        getData.execute(URLListner.URL+URLListner.CHANGE_PASSWORD);

        clearFields();//.....call clear field...
    }

    //....Clear all fields...
    private void clearFields() {

        old_pass.setText("");
        new_pass.setText("");
        confirm_pass.setText("");
    }
}
