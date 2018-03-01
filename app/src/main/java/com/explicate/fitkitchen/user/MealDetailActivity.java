package com.explicate.fitkitchen.user;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.Button;
import android.widget.ExpandableListAdapter;
import android.widget.ExpandableListView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.explicate.fitkitchen.R;
import com.explicate.fitkitchen.application.App;
import com.explicate.fitkitchen.fragment.BreakFastFragment;
import com.explicate.fitkitchen.model.CartModel;
import com.explicate.fitkitchen.model.MealSubItemModel;
import com.explicate.fitkitchen.utility.GetData;
import com.explicate.fitkitchen.utility.URLListner;
import com.explicate.fitkitchen.utility.Utility;
import com.paytm.pgsdk.PaytmMerchant;
import com.paytm.pgsdk.PaytmOrder;
import com.paytm.pgsdk.PaytmPGService;
import com.paytm.pgsdk.PaytmPaymentTransactionCallback;
import com.payu.india.Model.PaymentParams;
import com.payu.india.Model.PayuConfig;
import com.payu.india.Model.PayuHashes;
import com.payu.india.Payu.Payu;
import com.payu.india.Payu.PayuConstants;
import com.payu.payuui.Activity.PayUBaseActivity;
import com.squareup.picasso.Picasso;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

/**
 * Created by Mahesh Nikam on 31/01/2017.
 */

public class MealDetailActivity extends AppCompatActivity implements View.OnClickListener {

    private static final String TAG = MealDetailActivity.class.getSimpleName();

    private Toolbar toolbar;
    private TabLayout tabLayout;
    private ViewPager viewPager;
    private String title,price,meal_id;
    private TextView total_price;
    private Button add_extras,proceed;
    private FloatingActionButton fab_cart;
    private ImageView close_cart;
    private String orderId="0",transactionId,transactionAmt,transactionStatus,cardBin;
    private int extraDishPrice=0;
    private PaymentParams mPaymentParams;
    private LinkedHashMap<String, ArrayList<CartModel>> expandableListDetailGlobal;
    private ExpandableListView expandableListView;
    private ExpandableListAdapter expandableListAdapter;
    private List<String> expandableListTitleGlobal;
    private ArrayList<MealSubItemModel> subMealDetailslist = new ArrayList<MealSubItemModel>();


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Payu.setInstance(this);
        setContentView(R.layout.activity_meal_detail);
        getMealDetailData();
        expandableListDetailGlobal = new LinkedHashMap<String, ArrayList<CartModel>>();
        expandableListTitleGlobal = new ArrayList<String>();
        setUpViews();
        initOrderId();
    }

    private void setUpViews() {

        title = getIntent().getStringExtra("meal_title"); //...Get selected meal title...
        price = getIntent().getStringExtra("meal_price");

        toolbar = (Toolbar)findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setTitle(title);

        add_extras = (Button)findViewById(R.id.btn_add_extras);
        add_extras.setOnClickListener(this);

        fab_cart = (FloatingActionButton)findViewById(R.id.fab_show_cart);
        fab_cart.setOnClickListener(this);
    }

    @Override
    public void onStart() {
        super.onStart();
        initOrderId();
    }

    @Override
    public void onClick(final View v) {

        switch (v.getId())
        {
            case R.id.btn_add_extras:

                if(!expandableListDetailGlobal.isEmpty())
                {
                    extraDishPrice=0;
                    expandableListTitleGlobal.clear();
                    expandableListDetailGlobal.clear();
                }
                Intent getData = new Intent(MealDetailActivity.this,AddExtrasActivity.class);
                startActivityForResult(getData,005);

                break;

            //......Code on click fab cart.........
            case R.id.fab_show_cart:

                showCart();
                
                break;

            //....Code for proceed button(Payment/PayUBiz gateway)...
            case R.id.btn_proceed:

                final CharSequence[] options=new CharSequence[2];
                options[0]="Card/NetBanking/Wallet Payment";
                options[1]="Paytm Wallet";

                AlertDialog.Builder ad=new AlertDialog.Builder(MealDetailActivity.this);
                ad.setTitle("Payment Options");
                ad.setSingleChoiceItems(options, 0, null);
                ad.setPositiveButton("Pay", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int id) {

                        dialog.dismiss();
                        int selectedPosition = ((AlertDialog) dialog).getListView().getCheckedItemPosition();

                        switch (selectedPosition) {

                            case 0:

                                // Utility.showToast(getActivity(),"Card");
                                goToPayu();

                                break;

                            case 1:

                                //.....Start paytm transaction...
                                onStartTransaction(v);
                                //Utility.getIntent(getContext(), PaytmMerchantActivity.class);

                                break;
                        }


                    }
                });

                ad.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                        dialog.dismiss();

                    }
                });

                ad.show();
                break;
        }
    }


    //........Code for show cart...........
    private void showCart() {

        final Dialog dialog = new Dialog(this,R.style.MaterialDialogSheet);

        dialog.setContentView(R.layout.cart_details_dialog);
        dialog.setCancelable(true);
        dialog.getWindow().setLayout(LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT);
        dialog.getWindow ().setGravity(Gravity.BOTTOM);

        //....Code for set Plan name and its price...
        TextView plan_name = (TextView)dialog.findViewById(R.id.tv_plan_name);
        TextView plan_price = (TextView)dialog.findViewById(R.id.tv_plan_price);
        total_price = (TextView)dialog.findViewById(R.id.tv_total_price);

        plan_name.setText(title);
        plan_price.setText(price);

        //Set meal price to total price
       total_price.setText(Integer.toString(Integer.parseInt(price)+extraDishPrice));


        //...Code for close dialog...
        close_cart = (ImageView) dialog.findViewById(R.id.iv_close_cart);
        close_cart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });

        //....Expandable list view..
        expandableListView = (ExpandableListView)dialog.findViewById(R.id.elv_cart_extras_data);
        //expandableListAdapter = new CustomExpandableListAdapter(this);
        expandableListAdapter = new CustomExpandableListAdapter(this,expandableListTitleGlobal,expandableListDetailGlobal);
        expandableListView.setAdapter(expandableListAdapter);


        for(int i=0;i<expandableListTitleGlobal.size();i++)
        {
            expandableListView.expandGroup(i);
        }


        //....Proceed button...
        proceed = (Button)dialog.findViewById(R.id.btn_proceed);
        proceed.setOnClickListener(this);

        dialog.show();
    }


    //.................Code for Custom Expanded List Adapter...........
    public class CustomExpandableListAdapter extends BaseExpandableListAdapter {

        private Context context;
        //private List<String> expandableListTitle;
        //private HashMap<String, ArrayList<CartModel>> expandableListDetail;

        public CustomExpandableListAdapter(Context context)
        {

        }

        public CustomExpandableListAdapter(Context context, List<String> expandableListTitle, HashMap<String, ArrayList<CartModel>> expandableListDetail) {

            this.context = context;
           // this.expandableListTitle = expandableListTitle;
            //this.expandableListDetail = expandableListDetail;
        }

        @Override
        public void notifyDataSetChanged() {

            notifyDataSetInvalidated();
        }

        @Override
        public Object getChild(int listPosition, int expandedListPosition) {

            return expandableListDetailGlobal.get(expandableListTitleGlobal.get(listPosition)).get(expandedListPosition);
        }

        @Override
        public long getChildId(int listPosition, int expandedListPosition) {
            return expandedListPosition;
        }

        @Override
        public View getChildView(final int listPosition, final int expandedListPosition,
                                 final boolean isLastChild, View convertView, final ViewGroup parent) {

            //final ArrayList<CartModel> item= (ArrayList<CartModel>) getChild(listPosition, expandedListPosition);
            final CartModel item= (CartModel) getChild(listPosition, expandedListPosition);

            if (convertView == null) {
                LayoutInflater mInflater = (LayoutInflater) this.context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                convertView = mInflater.inflate(R.layout.cart_extras_dish, null);
            }


            final ImageView dishImage = (ImageView)convertView.findViewById(R.id.iv_cart_extras_dish_image);
            ImageView deletedish = (ImageView)convertView.findViewById(R.id.iv_delete_cart_item);
            TextView dishName = (TextView)convertView.findViewById(R.id.tv_cart_extras_dish_title);
            final TextView dishprice = (TextView)convertView.findViewById(R.id.tv_cart_extras_dish_price);

            deletedish.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {


                    if(expandableListDetailGlobal.get(expandableListTitleGlobal.get(listPosition)).size()==1)
                    {
                        //expandableListTitle.remove(listPosition);
                        expandableListDetailGlobal.get(expandableListTitleGlobal.get(listPosition)).remove(expandedListPosition);
                        expandableListTitleGlobal.remove(listPosition);
                        notifyDataSetChanged();
                    }
                    else
                    {
                        //expandableListDetail.get(expandableListTitle.get(listPosition)).remove(expandedListPosition);
                        expandableListDetailGlobal.get(expandableListTitleGlobal.get(listPosition)).remove(expandedListPosition);
                        notifyDataSetChanged();
                    }

                    //..........also delete price of dish and set to total........
                    extraDishPrice -= Integer.parseInt(item.getExtraDishPrice());
                    total_price.setText(Integer.toString(Integer.parseInt(price)+extraDishPrice));
                    notifyDataSetChanged();
                }
            });


            dishName.setText(item.getExtraDishName());
            dishprice.setText(item.getExtraDishPrice());

            Picasso.with(context)
                    .load(URLListner.EXTRA_DISH_IMAGE_PATH+item.getExtraDishImage())
                    .fit()
                    .centerCrop()
                    .into(dishImage);

            return convertView;
        }

        @Override
        public int getChildrenCount(int listPosition) {
            return expandableListDetailGlobal.get(expandableListTitleGlobal.get(listPosition)).size();
        }



        @Override
        public Object getGroup(int listPosition) {
            return expandableListTitleGlobal.get(listPosition);
        }

        @Override
        public int getGroupCount() {
            return expandableListTitleGlobal.size();
        }

        @Override
        public long getGroupId(int listPosition) {
            return listPosition;
        }

        @Override
        public View getGroupView(int listPosition, boolean isExpanded,
                                 View convertView, ViewGroup parent) {
            String listTitle = (String) getGroup(listPosition);

            if (convertView == null) {
                LayoutInflater layoutInflater = (LayoutInflater) this.context.
                        getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                convertView = layoutInflater.inflate(R.layout.cart_extras_title, null);
            }
            TextView cart_item_name = (TextView) convertView
                    .findViewById(R.id.tv_cart_extras_title);
            cart_item_name.setTypeface(null, Typeface.BOLD);
            cart_item_name.setText(listTitle);
            return convertView;
        }

        @Override
        public boolean hasStableIds() {
            return true;
        }

        @Override
        public boolean isChildSelectable(int listPosition, int expandedListPosition) {
            return true;
        }


    }

    //.......Code for get meal detail items data...........
    private void getMealDetailData() {

        if(!subMealDetailslist.isEmpty())
        {
            subMealDetailslist.clear();
        }

        meal_id = getIntent().getStringExtra("meal_id");// Get meal id

        Utility.showProgressDialog(MealDetailActivity.this,"Please wait...");
        Utility.progressDialog.show();


        List<NameValuePair> params=new ArrayList<>();

        params.add(new BasicNameValuePair("meal_id",meal_id));


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

                    if(success==1)
                    {

                        JSONArray arr = jsonObject.getJSONArray("meal_dish");

                        for(int i=0;i<arr.length();i++)
                        {

                            JSONObject json = arr.getJSONObject(i);
                           MealSubItemModel mealSubItemModel = new MealSubItemModel();

                            mealSubItemModel.setSubmealId(json.getString("item_id"));
                            mealSubItemModel.setCategoryId(json.getString("dish_id"));
                            mealSubItemModel.setSubMealCategaory(json.getString("item_name"));
                            mealSubItemModel.setSubmealName(json.getString("dish_name"));
                            mealSubItemModel.setSubmealDescription(json.getString("dish_desc"));
                            mealSubItemModel.setSubimageUrl(json.getString("dish_image"));

                           subMealDetailslist.add(mealSubItemModel);

                        }

                        viewPager = (ViewPager)findViewById(R.id.viewpager);
                        setupViewPager(viewPager);


                        tabLayout = (TabLayout)findViewById(R.id.tabs);
                        tabLayout.setupWithViewPager(viewPager);

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
                    Utility.showToast(MealDetailActivity.this,getString(R.string.failed));
                }


            }
        });
        getData.execute(URLListner.URL+URLListner.GET_MEAL_DISH);
    }


    //.......Code for view pager fragments....
    private void setupViewPager(ViewPager viewPager) {

        ViewPagerAdapter adapter = new ViewPagerAdapter(getSupportFragmentManager());

            adapter.addFragment(new BreakFastFragment(subMealDetailslist,"Snaks"),"Snacks");
            adapter.addFragment(new BreakFastFragment(subMealDetailslist,"BreakFast"),"Break Fast");
            adapter.addFragment(new BreakFastFragment(subMealDetailslist,"Lunch"),"Lunch");
            adapter.addFragment(new BreakFastFragment(subMealDetailslist,"Dinner"),"Dinner");

        viewPager.setAdapter(adapter);
    }

    //.........ViewPager Adapter.......
    class ViewPagerAdapter extends FragmentPagerAdapter {

        private final List<Fragment> fragmentList = new ArrayList<>();
        private final List<String> fragmentTitleList = new ArrayList<>();

        public ViewPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            return fragmentList.get(position);
        }

        @Override
        public int getCount() {
            return fragmentList.size();
        }

        public void addFragment(Fragment fragment,String title)
        {
            fragmentList.add(fragment);
            fragmentTitleList.add(title);
        }

        @Override
        public CharSequence getPageTitle(int position) {
            return fragmentTitleList.get(position);
        }
    }

    //..................CODE FOR PAYTM PAYMENT........
    //...Calculate random merchant id.......
    private void initOrderId() {
        Random r = new Random(System.currentTimeMillis());
        orderId = "ORDER" + (1 + r.nextInt(2)) * 10000
                + r.nextInt(10000);
        // EditText orderIdEditText = (EditText)findViewById(R.id.order_id);
        // orderIdEditText.setText(orderId);
    }

    //.....Start Paytm Trasaction....
    public void onStartTransaction(View view) {
        PaytmPGService Service = PaytmPGService.getStagingService();
        Map<String, String> paramMap = new HashMap<String, String>();

        // these are mandatory parameters

        paramMap.put("ORDER_ID", orderId);
        paramMap.put("MID", getString(R.string.sample_merchant_id_staging));
        paramMap.put("CUST_ID", App.getUserId());
        paramMap.put("CHANNEL_ID", getString(R.string.sample_channel_id));
        paramMap.put("INDUSTRY_TYPE_ID", getString(R.string.sample_industry_type_id));
        paramMap.put("WEBSITE", getString(R.string.sample_website));
        paramMap.put("TXN_AMOUNT", getString(R.string.sample_transaction_amount));
        paramMap.put("THEME", getString(R.string.sample_theme));
        paramMap.put("EMAIL", App.getUserEmail());
        paramMap.put("MOBILE_NO", App.getUserPhone());
        PaytmOrder Order = new PaytmOrder(paramMap);

        PaytmMerchant Merchant = new PaytmMerchant(
                "https://pguat.paytm.com/paytmchecksum/paytmCheckSumGenerator.jsp",
                "https://pguat.paytm.com/paytmchecksum/paytmCheckSumVerify.jsp");

        Service.initialize(Order, Merchant, null);

        Service.startPaymentTransaction(this, true, true,
                new PaytmPaymentTransactionCallback() {
                    @Override
                    public void someUIErrorOccurred(String inErrorMessage) {
                        // Some UI Error Occurred in Payment Gateway Activity.
                        // // This may be due to initialization of views in
                        // Payment Gateway Activity or may be due to //
                        // initialization of webview. // Error Message details
                        // the error occurred.
                    }

                    @Override
                    public void onTransactionSuccess(Bundle inResponse) {
                        // After successful transaction this method gets called.
                        // // Response bundle contains the merchant response
                        // parameters.
                        com.paytm.pgsdk.Log.d("LOG", "Payment Transaction is successful " + inResponse);
                        Toast.makeText(MealDetailActivity.this, "Payment Transaction is successful ", Toast.LENGTH_LONG).show();

                        transactionId= inResponse.getString("TXNID");
                        transactionAmt = inResponse.getString("TXNAMOUNT");
                        transactionStatus = inResponse.getString("STATUS");

                        setOrder();
                    }

                    @Override
                    public void onTransactionFailure(String inErrorMessage,
                                                     Bundle inResponse) {
                        // This method gets called if transaction failed. //
                        // Here in this case transaction is completed, but with
                        // a failure. // Error Message describes the reason for
                        // failure. // Response bundle contains the merchant
                        // response parameters.
                        com.paytm.pgsdk.Log.e("LOG", "Payment Transaction Failed " + inErrorMessage);
                        Toast.makeText(MealDetailActivity.this, "Payment Transaction Failed ", Toast.LENGTH_LONG).show();
                    }

                    @Override
                    public void networkNotAvailable() { // If network is not
                        // available, then this
                        // method gets called.
                    }

                    @Override
                    public void clientAuthenticationFailed(String inErrorMessage) {
                        // This method gets called if client authentication
                        // failed. // Failure may be due to following reasons //
                        // 1. Server error or downtime. // 2. Server unable to
                        // generate checksum or checksum response is not in
                        // proper format. // 3. Server failed to authenticate
                        // that client. That is value of payt_STATUS is 2. //
                        // Error Message describes the reason for failure.
                    }

                    @Override
                    public void onErrorLoadingWebPage(int iniErrorCode,
                                                      String inErrorMessage, String inFailingUrl) {

                    }

                    // had to be added: NOTE
                    @Override
                    public void onBackPressedCancelTransaction() {
                        // TODO Auto-generated method stub
                    }

                });
    }

    //.........Set order.......
    private void setOrder() {

        Utility.showProgressDialog(this,"Please wait...");
        Utility.progressDialog.show();


        List<NameValuePair> params=new ArrayList<>();

        params.add(new BasicNameValuePair("user_id",App.getUserId()));
        params.add(new BasicNameValuePair("tran_id",transactionId));
        params.add(new BasicNameValuePair("amount",transactionAmt));
        params.add(new BasicNameValuePair("order_status",transactionStatus));

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

                           /* mealModel.setMealId(json.getString("order_id"));
                            mealModel.setMealShortName(json.getString("meal_short_name"));
                            mealModel.setMealDescription(json.getString("meal_description"));
                            mealModel.setImageUrl(json.getString("meal_image"));
                            mealModel.setMealPrice(json.getString("meal_price"));*/

                        }

                        Log.e("MealFragment=",jsonObject.getString("message"));
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
                    Utility.showToast(MealDetailActivity.this,getString(R.string.failed));
                }


            }
        });
        getData.execute(URLListner.URL+URLListner.SET_ORDER);
    }


    //............CODE FOR PAYU-BIZ PAYMENT......

    private void goToPayu() {

        mPaymentParams=new PaymentParams();

        mPaymentParams.setKey("19rABa");
        mPaymentParams.setAmount("1.0");
        mPaymentParams.setProductInfo("myproduct");
        mPaymentParams.setFirstName("mahesh");
        mPaymentParams.setEmail("maheshbhople@gmail.com");
        mPaymentParams.setTxnId(String.valueOf(System.currentTimeMillis()));
        mPaymentParams.setSurl("https://payu.herokuapp.com/success");
        mPaymentParams.setFurl("https://payu.herokuapp.com/failure");
        mPaymentParams.setUserCredentials("gtKFFx:payutest@payu.in");
        mPaymentParams.setUdf1("udf1");
        mPaymentParams.setUdf2("udf2");
        mPaymentParams.setUdf3("udf3");
        mPaymentParams.setUdf4("udf4");
        mPaymentParams.setUdf5("udf5");


        generateHashFromServer(mPaymentParams);

    }

    public void generateHashFromServer(PaymentParams mPaymentParams){
        // nextButton.setEnabled(false); // lets not allow the user to click the button again and again.
        // lets create the post params

        StringBuffer postParamsBuffer = new StringBuffer();
        postParamsBuffer.append(concatParams(PayuConstants.KEY, mPaymentParams.getKey()));
        postParamsBuffer.append(concatParams(PayuConstants.AMOUNT, mPaymentParams.getAmount()));
        postParamsBuffer.append(concatParams(PayuConstants.TXNID, mPaymentParams.getTxnId()));
        postParamsBuffer.append(concatParams(PayuConstants.EMAIL, null == mPaymentParams.getEmail() ? "" : mPaymentParams.getEmail()));
        postParamsBuffer.append(concatParams(PayuConstants.PRODUCT_INFO, mPaymentParams.getProductInfo()));
        postParamsBuffer.append(concatParams(PayuConstants.FIRST_NAME, null == mPaymentParams.getFirstName() ? "" : mPaymentParams.getFirstName()));
        postParamsBuffer.append(concatParams(PayuConstants.UDF1, mPaymentParams.getUdf1() == null ? "" : mPaymentParams.getUdf1()));
        postParamsBuffer.append(concatParams(PayuConstants.UDF2, mPaymentParams.getUdf2() == null ? "" : mPaymentParams.getUdf2()));
        postParamsBuffer.append(concatParams(PayuConstants.UDF3, mPaymentParams.getUdf3() == null ? "" : mPaymentParams.getUdf3()));
        postParamsBuffer.append(concatParams(PayuConstants.UDF4, mPaymentParams.getUdf4() == null ? "" : mPaymentParams.getUdf4()));
        postParamsBuffer.append(concatParams(PayuConstants.UDF5, mPaymentParams.getUdf5() == null ? "" : mPaymentParams.getUdf5()));
        postParamsBuffer.append(concatParams(PayuConstants.USER_CREDENTIALS, mPaymentParams.getUserCredentials() == null ? PayuConstants.DEFAULT : mPaymentParams.getUserCredentials()));

        // for offer_key
        if(null != mPaymentParams.getOfferKey())
            postParamsBuffer.append(concatParams(PayuConstants.OFFER_KEY, mPaymentParams.getOfferKey()));
        // for check_isDomestic
        if(null != cardBin)
            postParamsBuffer.append(concatParams("card_bin", cardBin));

        String postParams = postParamsBuffer.charAt(postParamsBuffer.length() - 1) == '&' ? postParamsBuffer.substring(0, postParamsBuffer.length() - 1).toString() : postParamsBuffer.toString();
        // make api call
        GetHashesFromServerTask getHashesFromServerTask = new GetHashesFromServerTask();
        getHashesFromServerTask.execute(postParams);
    }

    protected String concatParams(String key, String value) {
        return key + "=" + value + "&";
    }

    class GetHashesFromServerTask extends AsyncTask<String, String, PayuHashes> {

        @Override
        protected PayuHashes doInBackground(String ... postParams) {
            PayuHashes payuHashes = new PayuHashes();
            try {
//                URL url = new URL(PayuConstants.MOBILE_TEST_FETCH_DATA_URL);
//                        URL url = new URL("http://10.100.81.49:80/merchant/postservice?form=2");;


                URL url = new URL("http://getsetrecharge.com/cart/getHashes");


                //  URL url = new URL("https://payu.herokuapp.com/get_hash");

                // get the payuConfig first
                String postParam = postParams[0];

                Log.e("Post", postParam);

                byte[] postParamsByte = postParam.getBytes("UTF-8");

                HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                conn.setRequestMethod("POST");
                conn.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
                conn.setRequestProperty("Content-Length", String.valueOf(postParamsByte.length));
                conn.setDoOutput(true);
                conn.getOutputStream().write(postParamsByte);

                InputStream responseInputStream = conn.getInputStream();
                StringBuffer responseStringBuffer = new StringBuffer();
                byte[] byteContainer = new byte[1024];
                for (int i; (i = responseInputStream.read(byteContainer)) != -1; ) {
                    responseStringBuffer.append(new String(byteContainer, 0, i));
                }

                JSONObject response = new JSONObject(responseStringBuffer.toString());

                Log.e("Response",response.toString());

                Iterator<String> payuHashIterator = response.keys();
                while(payuHashIterator.hasNext()){
                    String key = payuHashIterator.next();
                    switch (key){
                        case "payment_hash":
                            payuHashes.setPaymentHash(response.getString(key));
                            break;
                        case "get_merchant_ibibo_codes_hash": //
                            payuHashes.setMerchantIbiboCodesHash(response.getString(key));
                            break;
                        case "vas_for_mobile_sdk_hash":
                            payuHashes.setVasForMobileSdkHash(response.getString(key));
                            break;
                        case "payment_related_details_for_mobile_sdk_hash":
                            payuHashes.setPaymentRelatedDetailsForMobileSdkHash(response.getString(key));
                            break;
                        case "delete_user_card_hash":
                            payuHashes.setDeleteCardHash(response.getString(key));
                            break;
                        case "get_user_cards_hash":
                            payuHashes.setStoredCardsHash(response.getString(key));
                            break;
                        case "edit_user_card_hash":
                            payuHashes.setEditCardHash(response.getString(key));
                            break;
                        case "save_user_card_hash":
                            payuHashes.setSaveCardHash(response.getString(key));
                            break;
                        case "check_offer_status_hash":
                            payuHashes.setCheckOfferStatusHash(response.getString(key));
                            break;
                        case "check_isDomestic_hash":
                            payuHashes.setCheckIsDomesticHash(response.getString(key));
                            break;
                        default:
                            break;
                    }
                }

            } catch (MalformedURLException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            } catch (JSONException e) {
                e.printStackTrace();
            }
            return payuHashes;
        }

        @Override
        protected void onPostExecute(PayuHashes payuHashes) {
            super.onPostExecute(payuHashes);
            //  nextButton.setEnabled(true);
            launchSdkUI(payuHashes);
        }
    }

    public void launchSdkUI(PayuHashes payuHashes){
        // let me add the other params which i might use from other activity

        PayuConfig payuConfig = new PayuConfig();

        // mPaymentParams.setHash(payuHashes.getPaymentHash());

        payuConfig.setEnvironment(PayuConstants.PRODUCTION_ENV);

        Intent intent = new Intent(MealDetailActivity.this, PayUBaseActivity.class);

        intent.putExtra(PayuConstants.PAYU_CONFIG, payuConfig);
        intent.putExtra(PayuConstants.PAYMENT_PARAMS, mPaymentParams);
        //intent.putExtra(PayuConstants.SMS_PERMISSION, true);
        intent.putExtra(PayuConstants.STORE_ONE_CLICK_HASH, 0);
        intent.putExtra(PayuConstants.PAYU_HASHES, payuHashes);
        startActivityForResult(intent, PayuConstants.PAYU_REQUEST_CODE);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, final Intent data) {
        if (requestCode == PayuConstants.PAYU_REQUEST_CODE) {
            if (data != null) {
                new AlertDialog.Builder(this)
                        .setCancelable(false)
                        .setMessage("Payment Successful")
                        // .setMessage("Payu's Data : " + data.getStringExtra("payu_response") + "\n\n\n Merchant's Data: " + data.getStringExtra("result"))
                        .setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int whichButton) {

                            }
                        }).show();

            } else {
                Toast.makeText(MealDetailActivity.this, "Could not receive data", Toast.LENGTH_LONG).show();
            }
        }


        //............code for get cart data............
        if(requestCode == 005)
        {

            if(data!=null)
            {

                Bundle bundle = data.getExtras();
                ArrayList<CartModel> cartDataList = (ArrayList<CartModel>) bundle.getSerializable("CART_DATA");

                for(CartModel cartModel:cartDataList)
                {
                    ArrayList<CartModel> list = new ArrayList<>();
                    for(CartModel cartModel1:cartDataList)
                    {

                        if(cartModel1.getExtraMainName().equalsIgnoreCase(cartModel.getExtraMainName()))
                        {
                            list.add(cartModel1);
                        }

                    }
                    extraDishPrice += Integer.parseInt(cartModel.getExtraDishPrice());
                    expandableListDetailGlobal.put(cartModel.getExtraMainName(),list);
                }

            }
            fab_cart.setImageResource(R.drawable.loaded_cart);
            ArrayList<String> dummy = new ArrayList<String>(expandableListDetailGlobal.keySet());
            expandableListTitleGlobal = dummy;
            showCart();
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
