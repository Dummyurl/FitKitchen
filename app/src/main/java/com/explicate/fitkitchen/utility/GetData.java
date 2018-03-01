package com.explicate.fitkitchen.utility;

import android.os.AsyncTask;


import org.apache.http.NameValuePair;
import org.json.JSONObject;

import java.util.List;

/**
 * Created by Mahesh on 11/01/17.
 */
public class GetData extends AsyncTask<String,Void,Boolean> {

    private JSONParser jsonParser=new JSONParser();
    private int status=0;
    private List<NameValuePair> paramsList;
    ResultListner resultListner;
    JSONObject json;


    public GetData(List<NameValuePair> paramsList){

        this.paramsList=paramsList;

    }

    public void setResultListner(ResultListner listner){

        this.resultListner=listner;
    }


    @Override
    protected void onPreExecute() {
        super.onPreExecute();
    }

    @Override
    protected Boolean doInBackground(String... params) {

        String URL=params[0];

        try {
            // getting JSON string from URL
            json = jsonParser.makeHttpRequest(URL, "POST", paramsList);

            return true;

        } catch (Exception e) {

            e.printStackTrace();

            return false;
        }


    }

    @Override
    protected void onPostExecute(Boolean s) {
        super.onPostExecute(s);

        if(s){

            resultListner.success(json);

        }else{

            resultListner.fail();
        }
    }



    public interface ResultListner{

        public void success(JSONObject jsonObject);
        public void fail();
    }



}
