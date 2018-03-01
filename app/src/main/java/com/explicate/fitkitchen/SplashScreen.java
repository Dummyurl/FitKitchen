package com.explicate.fitkitchen;

import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.Window;
import android.view.WindowManager;

import com.explicate.fitkitchen.application.App;
import com.explicate.fitkitchen.user.AddExtrasActivity;
import com.explicate.fitkitchen.user.LoginActivity;
import com.explicate.fitkitchen.user.MainActivity;
import com.explicate.fitkitchen.utility.Utility;

/**
 * Created by Mahesh Nikam on 12/01/2017.
 */

public class SplashScreen extends AppCompatActivity {

    private static final String TAG = SplashScreen.class.getSimpleName();

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.splash_screen);

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {

                if(!App.getUserId().equalsIgnoreCase(""))
                {
                    //GOTO MainActivity
                    finish();
                    Utility.getIntent(SplashScreen.this,MainActivity.class);
                }
                else
                {
                    finish();
                    Utility.getIntent(SplashScreen.this,LoginActivity.class);
                }
            }
        },3000);
    }
}
