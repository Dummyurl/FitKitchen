package com.explicate.fitkitchen.user;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.Signature;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.explicate.fitkitchen.R;
import com.explicate.fitkitchen.application.App;
import com.explicate.fitkitchen.utility.GetData;
import com.explicate.fitkitchen.utility.MyPreferences;
import com.explicate.fitkitchen.utility.URLListner;
import com.explicate.fitkitchen.utility.Utility;
import com.explicate.fitkitchen.utility.Validate;
import com.facebook.AccessToken;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.FacebookSdk;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.facebook.login.LoginManager;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;
import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.auth.api.signin.GoogleSignInResult;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

/**
 * Created by Mahesh Nikam on 12/01/2017.
 */

public class LoginActivity  extends AppCompatActivity implements View.OnClickListener, GoogleApiClient.OnConnectionFailedListener {

    private static final String TAG = LoginActivity.class.getSimpleName();

    private RelativeLayout relativeLayout;
    private EditText user_name,user_password;
    private Button login,fb_login,google_login,signup;
    private LoginButton facebook_login_btn;
    private CallbackManager callbackManager;
    private SignInButton google_login_btn;
    private GoogleApiClient mGoogleApiClient;
    private TextView forgot_password;
    private String userName,userPassword,social_id,social_name,social_email,social_image,social_type,login_by;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        FacebookSdk.sdkInitialize(getApplicationContext());
        setContentView(R.layout.activity_login);

        setUpViews();
       // printHashKey();

        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestEmail()
                .build();

        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .enableAutoManage(this, this)
                .addApi(Auth.GOOGLE_SIGN_IN_API, gso)
                .build();
    }

    private void setUpViews() {

        relativeLayout = (RelativeLayout)findViewById(R.id.loginActivity_RL);

        user_name = (EditText)findViewById(R.id.input_user_name_edt);
        user_password = (EditText)findViewById(R.id.input_user_password_edt);

        login = (Button)findViewById(R.id.login_btn);
        login.setOnClickListener(this);

        signup = (Button)findViewById(R.id.signup_link_btn);
        signup.setOnClickListener(this);

        fb_login = (Button)findViewById(R.id.fb_login);
        fb_login.setOnClickListener(this);

        google_login = (Button)findViewById(R.id.google_login);
        google_login.setOnClickListener(this);

        facebook_login_btn = (LoginButton)findViewById(R.id.fb_login_btn);
        facebook_login_btn.setOnClickListener(this);

        google_login_btn = (SignInButton)findViewById(R.id.google_login_btn);
        google_login_btn.setOnClickListener(this);

        forgot_password = (TextView)findViewById(R.id.forgot_password_link);
        forgot_password.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {

        switch (v.getId())
        {
            //.......Login.......
            case R.id.login_btn:
                //Create new user
                Utility.hideKeyboard(v,this);

                userName = user_name.getText().toString().trim();
                userPassword = user_password.getText().toString().trim();

                if(userName.equals(""))
                {
                    user_name.setError(getString(R.string.please_enter_your_email_phone));
                }
                else if(userPassword.equals(""))
                {
                    user_password.setError(getString(R.string.please_enter_password));
                }
                else if(!Validate.isValidPassword(userPassword))
                {
                    if(userPassword.length()<6)
                    {
                        user_password.setError(getString(R.string.password_should_be_6_character_in_length));
                    }
                    else if(userPassword.length()>15)
                    {
                        user_password.setError(getString(R.string.password_is_too_long));
                    }
                    else
                    {
                        user_password.setError(getString(R.string.password_should_contain));
                    }
                }
                else
                {
                    if (!Utility.isConnected(this))
                    {
                        Utility.showSnackbar(relativeLayout, getString(R.string.connection_failed));
                    }
                    else {

                        getUserData();//....check user data....
                    }

                }


                break;

            //Custom button
            case R.id.fb_login:

                if (!Utility.isConnected(this))
                {
                    Utility.showSnackbar(relativeLayout,getString(R.string.connection_failed));
                }
                else
                {
                    facebook_login_btn.performClick();
                }
                break;

            //Facebook login button
            case R.id.fb_login_btn:

                social_type= "facebook";
                facebook();

                break;

            //Custom button
            case R.id.google_login:

                social_type= "google";
                googleLogin();

                break;

            //Google signIn button
            case R.id.google_login_btn:

                if (!Utility.isConnected(this))
                {
                    Utility.showSnackbar(relativeLayout,getString(R.string.connection_failed));
                }
                else
                {
                    google_login.performClick();
                }

                break;

            case R.id.forgot_password_link:
                //....Goto forgot password page....
                Utility.getIntent(LoginActivity.this,ForgotPasswordActivity.class);
                break;

            case R.id.signup_link_btn:
                //....Goto signup page....
                Utility.getIntent(LoginActivity.this,SignUpActivity.class);
                break;

        }

    }

    //.........Get user data.......
    private void getUserData() {

        Utility.showProgressDialog(this,"Please wait..");
        Utility.progressDialog.show();

        List<NameValuePair> params=new ArrayList<>();


        params.add(new BasicNameValuePair("user_email",userName));
        params.add(new BasicNameValuePair("user_password",userPassword));

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
                            //GOTO BOOKING PAGE
                            finish();
                            Utility.showSnackbar(relativeLayout,getString(R.string.success));
                            clearFields();//.....call clear field...
                            Utility.getIntent(LoginActivity.this,MainActivity.class);
                        }
                        else
                        {
                            Utility.showSnackbar(relativeLayout,"Failed to store this session");
                        }

                    }
                    if(success!=1)
                    {
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
        getData.execute(URLListner.URL+URLListner.CUSTOM_LOGIN);

    }


    //........Clear all fields......
    private void clearFields() {

        user_name.setText("");
        user_password.setText("");
    }

    //.............Facebook Login................
    private void facebook() {

        callbackManager = CallbackManager.Factory.create();
        facebook_login_btn.setReadPermissions(Arrays.asList("public_profile, email"));
        facebook_login_btn.registerCallback(callbackManager, new FacebookCallback<LoginResult>() {

            @Override
            public void onSuccess(final LoginResult loginResult) {
                Log.e("At","Success");

                GraphRequest request = GraphRequest.newMeRequest(
                        AccessToken.getCurrentAccessToken(),
                        new GraphRequest.GraphJSONObjectCallback() {

                            @Override
                            public void onCompleted(
                                    JSONObject object,
                                    GraphResponse response) {
                                // Application code

                                JSONObject c = response.getJSONObject();
                                Log.e("Data",c.toString());


                                try {

                                    social_id = c.getString("id");
                                    social_name = c.getString("name");
                                    social_email = c.getString("email");
                                    social_image = "https://graph.facebook.com/" +social_id+ "/picture?type=large";
                                    //social_type = "2";

                                    Log.e("Image",social_image);

                                } catch (JSONException e) {
                                    e.printStackTrace();

                                }
                                login_by = "Facebook";

                                LoginManager.getInstance().logOut();
                                 //getSocialData();//...call AsyncTask...
                                sendSocialDetails();

                                //Log.e("Social Register","Reg");
                            }
                        });
                Bundle parameters = new Bundle();
                parameters.putString("fields", "id,name,email,gender");
                request.setParameters(parameters);
                request.executeAsync();


            }

            @Override
            public void onCancel() {
                // App code
                Utility.showSnackbar(relativeLayout,"Login cancelled by user!");
            }

            @Override
            public void onError(FacebookException exception) {
                // App code
                Utility.showSnackbar(relativeLayout,"Login unsuccessful!");
            }
        });

    }


    //.............Google Login..................
    private void googleLogin() {
        Intent signInIntent = Auth.GoogleSignInApi.getSignInIntent(mGoogleApiClient);
        startActivityForResult(signInIntent, 007);
    }

    //..........Google Login result handle.......
    private void handleSignInResult(GoogleSignInResult result) {

        if (result.isSuccess()) {
            // Signed in successfully, show authenticated UI.
            GoogleSignInAccount acct = result.getSignInAccount();


            social_id = acct.getId();
            social_name = acct.getDisplayName();
            //String personPhotoUrl = acct.getPhotoUrl().toString();
            social_email = acct.getEmail();
            login_by = "google";

            Log.e("googleResult", "Id: "+ social_id +"Name: " + social_name + ", email: " + social_email);



            Auth.GoogleSignInApi.signOut(mGoogleApiClient).setResultCallback(
                    new ResultCallback<Status>() {
                        @Override
                        public void onResult(Status status) {
                        }
                    });

           // SocialRegister();//...Call Asynch Task...
            sendSocialDetails();


        } else {
            // Signed out, show unauthenticated UI.
            Utility.showSnackbar(relativeLayout,"Google login failed");
        }
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }

    //...Send all social details on MobileNoActivity...
    private void sendSocialDetails() {

        HashMap<String,String> map = new HashMap<>();

        map.put("social_id",social_id);
        map.put("social_name",social_name);
        map.put("social_email",social_email);
        map.put("social_type",social_type);

        finish();
        Utility.getIntent(LoginActivity.this,SocialRegistrationActivity.class,map);
    }


    //Calculate hashkey
    private void printHashKey() {

        try {
            PackageInfo info = getPackageManager().getPackageInfo("com.explicate.fitkitchen", PackageManager.GET_SIGNATURES);//Paste your package name -"com.bfurns"
            for (Signature signature : info.signatures) {
                MessageDigest md = MessageDigest.getInstance("SHA");
                md.update(signature.toByteArray());
                String sign= Base64.encodeToString(md.digest(), Base64.DEFAULT);
                Log.e("MY KEY HASH:", sign);
                Toast.makeText(getApplicationContext(),sign,         Toast.LENGTH_LONG).show();
            }
        } catch (PackageManager.NameNotFoundException e) {
        } catch (NoSuchAlgorithmException e) {
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(social_type.equalsIgnoreCase("facebook"))
        {
            callbackManager.onActivityResult(requestCode,resultCode,data);
            Log.e("At","result");

        }
        else if(social_type.equalsIgnoreCase("google"))
        {
            if (requestCode == 007) {
                GoogleSignInResult result = Auth.GoogleSignInApi.getSignInResultFromIntent(data);
                handleSignInResult(result);
            }
        }

    }


}
