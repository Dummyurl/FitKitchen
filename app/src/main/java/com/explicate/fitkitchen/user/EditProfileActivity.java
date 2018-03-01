package com.explicate.fitkitchen.user;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.annotation.Nullable;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RelativeLayout;

import com.explicate.fitkitchen.R;
import com.explicate.fitkitchen.application.App;
import com.explicate.fitkitchen.utility.AndroidMultiPartEntity;
import com.explicate.fitkitchen.utility.URLListner;
import com.explicate.fitkitchen.utility.Utility;
import com.explicate.fitkitchen.utility.Validate;
import com.squareup.picasso.Picasso;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.mime.content.FileBody;
import org.apache.http.entity.mime.content.StringBody;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

import de.hdodenhof.circleimageview.CircleImageView;

/**
 * Created by Mahesh Nikam on 16/01/2017.
 */

public class EditProfileActivity extends AppCompatActivity implements View.OnClickListener {

    private static final String TAG = EditProfileActivity.class.getSimpleName();

    private RelativeLayout relativeLayout;
    private Toolbar toolbar;
    private CircleImageView profile_picture;
    private EditText name,phone,email;
    private Button save;
    private String userName,userMobile,userEmail;

    private String userChoosenTask;
    private Uri fileUri;
    private static final int REQUEST_CAMERA = 100;
    private static final int SELECT_FILE = 200;
    private File destination;
    private float totalSize = 0;
    private int status;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_profile);
        setUpViews();
    }

    private void setUpViews() {

        relativeLayout = (RelativeLayout)findViewById(R.id.userEditProfileActivity_RL);

        toolbar = (Toolbar)findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setTitle(getString(R.string.user_update_profile));

        profile_picture = (CircleImageView)findViewById(R.id.edit_profile_picture_civ);
        profile_picture.setOnClickListener(this);

        name = (EditText)findViewById(R.id.edit_profile_name);
        phone  = (EditText)findViewById(R.id.edit_profile_phone);
        email = (EditText)findViewById(R.id.edit_profile_email);

        save = (Button)findViewById(R.id.save_profile_btn);
        save.setOnClickListener(this);

        setPrefferenceData();
    }

    //...get all user details from shared prefferences and set to the fields...
    private void setPrefferenceData() {

        Picasso.with(this)
                .load(URLListner.IMAGE_PATH+ App.getUserImage())
               /* .resize(100,100)*/
                .fit()
                .centerCrop()
                .placeholder(R.drawable.placeholder)
                .into(profile_picture);

        name.setText(App.getUserName());
        int position = name.length();
        name.setSelection(position); //..Set cursor position..

        phone.setText(App.getUserPhone());
        email.setText(App.getUserEmail());
    }
    @Override
    public void onClick(View view) {

        switch (view.getId())
        {
            //.......Open gallery/camera.......
            case R.id.edit_profile_picture_civ:

                selectImage();

                break;

            //.......save user details......
            case R.id.save_profile_btn:

                Utility.hideKeyboard(view,this);

                userName = name.getText().toString().trim();
                userEmail = email.getText().toString().trim();
                userMobile = phone.getText().toString().trim();

                if(userName.equals(""))
                {
                    name.setError(getString(R.string.please_enter_your_name));
                }
                else if( userEmail.equals(""))
                {
                    email.setError(getString(R.string.please_enter_your_email_id));
                }
                else if(!Validate.isValidEmail(userEmail))
                {
                    email.setError(getString(R.string.please_enter_valid_email_address));
                }
                else if( userMobile.equals(""))
                {
                    phone.setError(getString(R.string.please_enter_your_phone_no));
                }
                else if(!Validate.isValidPhone(userMobile))
                {
                    phone.setError(getString(R.string.please_enter_valid_phone_no));
                }
                else
                {
                    if (!Utility.isConnected(this))
                    {
                        Utility.showSnackbar(relativeLayout, getString(R.string.connection_failed));
                    }
                    else
                    {
                         UploadFileToServer uploadFileToServer = new UploadFileToServer();
                        uploadFileToServer.execute();
                    }
                    //Utility.showSnackbar(relativeLayout,getString(R.string.success));
                }

                break;
        }
    }

    //................OPEN SELECT IMAGE ALERT BOX...........
    private void selectImage() {

        final CharSequence[] items = { "Take Photo", "Choose from Gallery", "Cancel" };
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Add Photo!");

        builder.setItems(items, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int item) {

                boolean result = Utility.checkPermission(EditProfileActivity.this);

                if (items[item].equals("Take Photo")) {

                    userChoosenTask = "Take Photo";
                    if (result)
                        cameraIntent();

                } else if (items[item].equals("Choose from Gallery")) {

                    userChoosenTask = "Choose from Library";
                    if (result)

                        galleryIntent();

                } else if (items[item].equals("Cancel")) {

                    dialog.dismiss();
                }
            }
        });

        builder.show();
    }

    /**************IMAGE SELECTION************/

    //............PERMISSION OF READ EXTERNAL STORAGE FOR MARSHMALLO..............
    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        switch (requestCode) {
            case Utility.MY_PERMISSIONS_REQUEST_READ_EXTERNAL_STORAGE:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                    if(userChoosenTask.equals("Take Photo"))
                        cameraIntent();
                    else if(userChoosenTask.equals("Choose from Gallery"))
                        galleryIntent();
                } else {
                    //code for deny
                }
                break;
        }
    }

    //.................CHOOSE IMAGE FROM GALLERY..............
    private void galleryIntent() {

        Intent intent = new Intent(
                Intent.ACTION_PICK,
                MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        intent.setType("image/*");
        startActivityForResult(Intent.createChooser(intent, "Select File"), SELECT_FILE);


    }

    //.................TAKE IMAGE FROM CAMERA..............
    private void cameraIntent() {
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        fileUri = Utility.getOutputMediaFileUri();
        intent.putExtra(MediaStore.EXTRA_OUTPUT, fileUri);
        startActivityForResult(intent, REQUEST_CAMERA);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == RESULT_OK) {

            if (requestCode == REQUEST_CAMERA) {

                //Uri selectedImageUri = data.getData();

                Picasso.with(this)
                        .load(fileUri)
                        /*.resize(100,100)*/
                        .fit()
                        .centerCrop()
                        .placeholder(R.drawable.placeholder)
                        .into(profile_picture);

                ByteArrayOutputStream bytes = new ByteArrayOutputStream();


                try {
                    Bitmap bitmap = MediaStore.Images.Media.getBitmap(getApplicationContext().getContentResolver(),fileUri);
                    bitmap.compress(Bitmap.CompressFormat.JPEG, 90, bytes);
                } catch (IOException e) {
                    e.printStackTrace();
                }
                //Bitmap thumbnail = (Bitmap) data.getExtras().get("data");

                // External sdcard location
                File mediaStorageDir = new File(
                        Environment
                                .getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES),
                        getString(R.string.app_name)); //Create folder fitkitchen in SD-Card

                // Create the storage directory if it does not exist
                if (!mediaStorageDir.exists()) {


                    if (!mediaStorageDir.mkdirs()) {


                    }
                }
                String title=String.valueOf(System.currentTimeMillis());
                destination = new File(mediaStorageDir.getPath(),
                        title + ".jpg");

                FileOutputStream fo;
                try {
                    destination.createNewFile();
                    fo = new FileOutputStream(destination);
                    fo.write(bytes.toByteArray());
                    fo.close();
                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                }



            } else if (requestCode == SELECT_FILE) {

                Uri selectedImageUri = data.getData();

                Picasso.with(this)
                        .load(selectedImageUri)
                        /*.resize(100,100)*/
                        .fit()
                        .centerCrop()
                        .placeholder(R.drawable.placeholder)
                        .into(profile_picture);


                ByteArrayOutputStream bytes = new ByteArrayOutputStream();


                try {
                    Bitmap bitmap = MediaStore.Images.Media.getBitmap(getApplicationContext().getContentResolver(),selectedImageUri);
                    bitmap.compress(Bitmap.CompressFormat.JPEG, 90, bytes);

                } catch (IOException e) {
                    e.printStackTrace();
                }

                // External sdcard location
                File mediaStorageDir = new File(
                        Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES),
                        getString(R.string.app_name));//Create folder fitkitchen in SD-Card

                // Create the storage directory if it does not exist
                if (!mediaStorageDir.exists()) {

                    if (!mediaStorageDir.mkdirs()) {


                    }

                }

                String title=String.valueOf(System.currentTimeMillis());
                destination = new File(mediaStorageDir.getPath(),
                        title + ".jpg");

                FileOutputStream fo;
                try {
                    destination.createNewFile();
                    fo = new FileOutputStream(destination);
                    fo.write(bytes.toByteArray());
                    fo.close();
                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                }

            }
        }
    }

    //.........UPLOAD FILE TO SERVER.........
    private class UploadFileToServer extends AsyncTask<Void, Integer, String> {

        ProgressDialog mProgressDialog = new ProgressDialog(EditProfileActivity.this);

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            // setting progress bar to zero
            mProgressDialog.setMessage("Please wait...");
            mProgressDialog.setIndeterminate(false);
            mProgressDialog.setMax(100);
            mProgressDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
            mProgressDialog.setCancelable(true);
            mProgressDialog.setCanceledOnTouchOutside(false);
            mProgressDialog.show();

        }

        @Override
        protected void onProgressUpdate(Integer... progress) {
            // Making progress bar visible
            mProgressDialog.setProgress(progress[0]);
        }


        @Override
        protected String doInBackground(Void... params) {
            return uploadFile();
        }

        @SuppressWarnings("deprecation")
        private String uploadFile() {

            String responseString="";

            HttpClient httpclient = new DefaultHttpClient();
            // HttpPost httppost = new HttpPost("");


            HttpPost httppost = new HttpPost(URLListner.URL+URLListner.UPDATE_USER_PROFILE);
            try {


                AndroidMultiPartEntity entity = new AndroidMultiPartEntity(
                        new AndroidMultiPartEntity.ProgressListener() {

                            @Override
                            public void transferred(long num) {

                                publishProgress((int) ((num / (float) totalSize) * 100));

                            }
                        });


                String path="";
                try
                {
                    path = destination.getPath();
                }catch (Exception e)
                {
                    e.printStackTrace();
                }

                if(!path.equalsIgnoreCase("")) {

                    entity.addPart("user_image", new FileBody(destination));
                }

                // Extra parameters if you want to pass to server
                try {

                    entity.addPart("user_id",new StringBody(App.getUserId()));
                    entity.addPart("firstname",new StringBody(userName));
                    entity.addPart("user_email",new StringBody(userEmail));
                    entity.addPart("user_mobile",new StringBody(userMobile));


                } catch (Exception e) {
                    e.printStackTrace();
                }



                totalSize = entity.getContentLength();
                httppost.setEntity(entity);

                // Making server call
                HttpResponse response = null;
                try {
                    response = httpclient.execute(httppost);

                    String responseStr = EntityUtils.toString(response.getEntity());

                    Log.e("data",responseStr);

                    if(!responseStr.equalsIgnoreCase("")){

                        JSONObject json=new JSONObject(responseStr);

                        //  {"status":1,"message":"Data inserted !!","inserted_id":[{"event_id":"17"}]}

                        status=json.getInt("success");

                        if(status==1){

                            Log.e("Success","File Uploaded");

                        }else{

                        }

                    }


                } catch (IOException e) {
                    e.printStackTrace();
                }
                HttpEntity r_entity = response.getEntity();

                int statusCode = response.getStatusLine().getStatusCode();
                if (statusCode == 200) {
                    // Server response
                    responseString = "Successfully uploaded..";

                } else {
                    responseString = "Error occurred! Http Status Code: "
                            + statusCode;
                }

            } catch (Exception e) {
                responseString = e.toString();
            }
            return responseString;

        }

        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);
            if(status == 1)
            {
                Log.e("Response from server: ", result);
                Utility.showToast(EditProfileActivity.this,"Records submitted");
                finish();
                Utility.getIntent(EditProfileActivity.this,UserProfileActivity.class);
            }
            else{
                Log.e("Status: ",""+status);
            }


            if(mProgressDialog.isShowing()){
                mProgressDialog.dismiss();
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
