package com.ktrlabs.thapelo.iwallet;

import android.content.Context;
import android.preference.PreferenceManager;
import android.util.Log;
import android.widget.Toast;

import com.google.firebase.iid.FirebaseInstanceId;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

import okhttp3.FormBody;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;

/**
 * Created by thapelo on 3/23/17.
 */

public class BankAi {

    Context context;

    public BankAi() {
        super();
    }

    public boolean bankRegistration(Context activity,String[] info) {
        try {
            Log.d("BankAi line 40","password="+ info[5]);
            String[] details = createAccount(info);
            storeKey(activity,"info",info.toString());
            Log.d("BankAi line 42","password="+ details[1]);
            bankLogin(activity,details[0],details[1]);
            return true;
        } catch (IOException e) {
            e.printStackTrace();
        }

        return false;
    }

    public boolean bankLogin(Context context, String idNumber,String password) {
        try {
            String account = getAuthRequest(idNumber,password)[0];
            Log.d("details",account + " " + password);
            String auth = getLoginRequest(account,password);
            setPushToken(context,auth);
            getAndStoreAccountDetails(context,auth);
            getAndStoreAccountDetails2(context,auth);
            this.storeKey(context,"idNumber",idNumber);
            this.storeKey(context,"password",password);
            this.storeKey(context,"account",account);
            this.storeKey(context,"x-auth",auth);
            return true;
        } catch (IOException e) {
            e.printStackTrace();
        }
        return false;
    }

    public String[] createAccount(String[]... data) throws IOException {
        Log.v("stuff", data.toString());
        okhttp3.OkHttpClient httpClient = new okhttp3.OkHttpClient();
        httpClient.retryOnConnectionFailure();
        okhttp3.RequestBody requestBody = new MultipartBody.Builder()
                .setType(MultipartBody.FORM)
                .addFormDataPart("AccountHolderEmailAddress",data[0][0])
                .addFormDataPart("AccountHolderGivenName",data[0][1])
                .addFormDataPart("AccountHolderFamilyName",data[0][2])
                .addFormDataPart("AccountHolderDateOfBirth","15-08-1985")
                .addFormDataPart("AccountHolderIdentificationNumber",data[0][3])
                .addFormDataPart("AccountHolderContactNumber1",data[0][4])
                .addFormDataPart("AccountHolderAddressLine1","Gauteng")
                .addFormDataPart("AccountType","credit")
                .addFormDataPart("Password",data[0][5])
                .build();

        okhttp3.Request request = new okhttp3.Request.Builder()
                .url("https://creditable.bankai.co/account")
                //.method("POST", okhttp3.RequestBody.create(null, new byte[0]))
                .addHeader("cache-control","no-cache")
                .addHeader("content-type","multipart/form-data; boundary=---011000010111000001101001")
                .post(requestBody)
                .build();


        okhttp3.Response httpResponse = httpClient.newCall(request).execute();

        String response = httpResponse.body().string();
        Log.d(request.toString(),response);



        String[] res = {data[0][3],data[0][5]};

        return res;
    }

    public String getAccountWithID(String ID,String auth) throws IOException, JSONException {
        Log.v("stuff", ID);
        okhttp3.OkHttpClient httpClient = new okhttp3.OkHttpClient();
        httpClient.retryOnConnectionFailure();
        okhttp3.RequestBody requestBody = new MultipartBody.Builder()
                .addFormDataPart("x-auth-token",auth)
                .setType(MultipartBody.FORM)
                .build();

        okhttp3.Request request = new okhttp3.Request.Builder()
                .url("https://creditable.bankai.co/account/" + ID)
                //.method("POST", okhttp3.RequestBody.create(null, new byte[0]))
                .addHeader("cache-control","no-cache")
                .addHeader("x-auth-token",auth)
                .addHeader("content-type","multipart/form-data; boundary=---011000010111000001101001")
                .post(requestBody)
                .build();


        okhttp3.Response httpResponse = httpClient.newCall(request).execute();
        String response = httpResponse.body().string();
        JSONObject jsonObject = new JSONObject(response);
        String account = jsonObject.getString("response");

        return account;
    }

    public String[] getAuthRequest(String walletID,String passWord) throws IOException {
        Log.d(walletID,passWord);
        okhttp3.OkHttpClient httpClient = new okhttp3.OkHttpClient();
        okhttp3.RequestBody requestBody = new MultipartBody.Builder()
                .setType(MultipartBody.FORM)
                .addFormDataPart("UserIdentificationNumber",walletID)
                .addFormDataPart("Password",passWord)
                .build();

        okhttp3.Request request = new okhttp3.Request.Builder()
                .url("https://creditable.bankai.co/auth/account")
                //.method("POST", okhttp3.RequestBody.create(null, new byte[0]))
                .addHeader("cache-control","no-cache")
                .addHeader("content-type","multipart/form-data; boundary=---011000010111000001101001")
                .post(requestBody)
                .build();


        okhttp3.Response httpResponse = httpClient.newCall(request).execute();

        String response = httpResponse.body().string();
        Log.d(request.toString(),response);
        String jString = null;
        try {
            JSONObject jsonObject = new JSONObject(response);
            jString = jsonObject.get("response").toString();
        } catch (JSONException e) {
            e.printStackTrace();
        }
        String[] res = {jString,passWord};
        return res;
    }

    public void setPushToken(Context cont,String auth) throws IOException {

        String android_id = FirebaseInstanceId.getInstance().getToken();
        Log.d("FCM", "Refreshed token: " + android_id);


        okhttp3.OkHttpClient httpClient = new okhttp3.OkHttpClient();
        okhttp3.RequestBody requestBody = new MultipartBody.Builder()
                .setType(MultipartBody.FORM)
                .addFormDataPart("PushToken", android_id)
                .addFormDataPart("Platform","android")
                .build();

        okhttp3.Request request = new okhttp3.Request.Builder()
                .url("https://creditable.bankai.co/accountPushToken")
                //.method("POST", okhttp3.RequestBody.create(null, new byte[0]))
                .addHeader("x-auth-token",auth)
                .addHeader("cache-control","no-cache")
                .addHeader("content-type","multipart/form-data; boundary=---011000010111000001101001")
                .post(requestBody)
                .build();


        okhttp3.Response httpResponse = httpClient.newCall(request).execute();

        String response = httpResponse.body().string();
        Log.d(request.toString(),response);
        String jString = null;
        try {
            JSONObject jsonObject = new JSONObject(response);
            jString = jsonObject.get("response").toString();
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public String getLoginRequest(String authresponse,String Password) throws IOException {
        Log.d("responza",authresponse + " " + Password);
        okhttp3.OkHttpClient httpClient = new okhttp3.OkHttpClient();
        okhttp3.RequestBody requestBody = new MultipartBody.Builder()
                .setType(MultipartBody.FORM)
                .addFormDataPart("User",authresponse)
                .addFormDataPart("Password",Password)
                .build();

        okhttp3.Request request = new okhttp3.Request.Builder()
                .url("https://creditable.bankai.co/auth/login")
                //.method("POST", okhttp3.RequestBody.create(null, new byte[0]))
                .addHeader("cache-control","no-cache")
                .post(requestBody)
                .build();


        okhttp3.Response httpResponse = httpClient.newCall(request).execute();

        String response = httpResponse.body().string();
        Log.d(request.toString(),response);
        String jString = null;
        try {

                JSONObject jsonObject = new JSONObject(response);
            if (jsonObject.get("response").toString()==null) {
                Toast.makeText(context,jsonObject.get("error").toString(),Toast.LENGTH_LONG).show();
                return null;
            }
                jString = jsonObject.get("response").toString();


        } catch (JSONException e) {
            e.printStackTrace();
        }
        return jString;
    }

    public boolean getAndStoreAccountDetails(Context context,String token) throws IOException {

        okhttp3.OkHttpClient httpClient = new okhttp3.OkHttpClient();

        okhttp3.Request request = new okhttp3.Request.Builder()
                .url("https://creditable.bankai.co/account")
                .addHeader("cache-control","no-cache")
                .addHeader("x-auth-token",token)
                .get()
                .build();


        okhttp3.Response httpResponse = httpClient.newCall(request).execute();

        String response = httpResponse.body().string();
        Log.d(request.toString(),response);

        String AccountNumber,AccountHolderName,AvailableBalance = null;
        try {
            JSONObject jsonObject = new JSONObject(response);
            if (jsonObject.getJSONArray("response")==null) {
                return false;
            }
            JSONArray jsonArray = jsonObject.getJSONArray("response");
            JSONObject jsonObjects = new JSONObject(jsonArray.get(0).toString());

            AvailableBalance = jsonObjects.getString("AvailableBalance");
            storeKey(context,"AvailableBalance", AvailableBalance);

            AccountHolderName = jsonObjects.getString("AccountHolderName");
            storeKey(context,"AccountHolderName", AccountHolderName);

            AccountNumber = jsonObjects.getString("AccountNumber");
            storeKey(context,"AccountNumber", AccountNumber);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return true;
    }

    public boolean getAndStoreAccountDetails2(Context context,String token) throws IOException {

        okhttp3.OkHttpClient httpClient = new okhttp3.OkHttpClient();

        okhttp3.Request request = new okhttp3.Request.Builder()
                .url("https://creditable.bankai.co/accountHolder")
                .addHeader("cache-control","no-cache")
                .addHeader("x-auth-token",token)
                .get()
                .build();


        okhttp3.Response httpResponse = httpClient.newCall(request).execute();

        String response = httpResponse.body().string();
        Log.d(request.toString(),response);

        String ContactNumber1,EmailAddress = null;
        try {
            JSONObject jsonObject = new JSONObject(response);
            JSONObject jsonObjects = jsonObject.getJSONObject("response");

            ContactNumber1= jsonObjects.getString("ContactNumber1");
            storeKey(context,"ContactNumber1", ContactNumber1);

            EmailAddress= jsonObjects.getString("EmailAddress");
            storeKey(context,"EmailAddress", EmailAddress);

        } catch (JSONException e) {
            e.printStackTrace();
        }

        return true;
    }

    public String getName(String xauth, String accountNumber) throws IOException {
        okhttp3.OkHttpClient httpClient = new okhttp3.OkHttpClient();

        okhttp3.Request request = new okhttp3.Request.Builder()
                .url("https://creditable.bankai.co/accountByNumber/" + accountNumber)
                .addHeader("cache-control","no-cache")
                .addHeader("x-auth-token",xauth)
                .get()
                .build();

        okhttp3.Response httpResponse = httpClient.newCall(request).execute();

        String response = httpResponse.body().string();
        Log.d(request.toString(),response);
        JSONObject jsonObject = null;
        String jString = null;
        try {
            jsonObject = new JSONObject(response);
            jsonObject = jsonObject.getJSONObject("response");
            jString = jsonObject.getString("AccountHolderName");
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return jString;
    }

    public int storeKey(Context context, String name, String value) {
        Log.v("Store start", " it really gets here");
        if (PreferenceManager.getDefaultSharedPreferences(context).edit().putString(name, value).commit()) {
            Log.v(name + "Stored true "," Yep it stored. Value="+value);
            return 1;
        }
        Log.v(name + "Stored false"," NO No No it didn't store. Value="+value);
        return 0;
    }

    public String getStokedKey(Context context, String name) {
        Log.d("Getting Stored ", " it really gets here");
        String token = PreferenceManager.getDefaultSharedPreferences(context).getString(name,"failed");
        Log.d(name,token);
        return token;
    }

    public boolean SendSms (String phone_number,String message) throws IOException {
        Log.d("phone number", phone_number);
        okhttp3.OkHttpClient httpClient = new okhttp3.OkHttpClient();
        RequestBody requestBody = new FormBody.Builder()
                .add("api_key","41106c42")
                .add("api_secret","24e6606ea92a108b")
                .add("to",phone_number)
                .add("from","iwallet")
                .add("text",message)
                .build();

        okhttp3.Request request = new okhttp3.Request.Builder()
                .url("https://rest.nexmo.com/sms/json")
                .addHeader("cache-control","no-cache")
                .post(requestBody)
                .build();


        okhttp3.Response httpResponse = httpClient.newCall(request).execute();
        String response = httpResponse.body().string();
        Log.d(request.toString(),response);

        return true;
    }

    public String CreditTransfer(String cell, String account,String amount, String type, String myaccount,String auth, String[] location) throws IOException {
        okhttp3.OkHttpClient httpClient = new okhttp3.OkHttpClient();
        httpClient.retryOnConnectionFailure();
        String lon,lat;
        if (location!=null)  {
            lon = location[0];
            lat = location[1];
        } else {
            lon =  "0.0";
            lat = "0.0";
        }
        okhttp3.RequestBody requestBody = new MultipartBody.Builder()
                .setType(MultipartBody.FORM)
                .addFormDataPart("SenderDetails",myaccount+"@")
                .addFormDataPart("RecipientDetails",account+"@")
                .addFormDataPart("Amount",amount)
                .addFormDataPart("Lon",lon)
                .addFormDataPart("Lat",lat)
                .addFormDataPart("Desc",type)
                .build();
        okhttp3.Request request = new okhttp3.Request.Builder()
                .url("https://creditable.bankai.co/transaction/credit")
                .addHeader("cache-control","no-cache")
                .addHeader("content-type","multipart/form-data; boundary=---011000010111000001101001")
                .addHeader("x-auth-token",auth)
                .post(requestBody)
                .build();

        okhttp3.Response httpResponse = httpClient.newCall(request).execute();
        String response = httpResponse.body().string();
        return response;
    }

    public JSONArray TransactionList(String xauth, String accnum) throws IOException {
        Log.d("x-auth",xauth);
        Log.d("accn",accnum);
        okhttp3.OkHttpClient httpClient = new okhttp3.OkHttpClient();
        httpClient.retryOnConnectionFailure();

        okhttp3.Request request = new okhttp3.Request.Builder()
                .url("https://creditable.bankai.co/transaction/list/100/0")
                .addHeader("cache-control","no-cache")
                .addHeader("content-type","multipart/form-data; boundary=---011000010111000001101001")
                .addHeader("x-auth-accountnumber",accnum)
                .addHeader("x-auth-token",xauth)
                .get()
                .build();

        okhttp3.Response httpResponse = httpClient.newCall(request).execute();

        String response = httpResponse.body().string();
        Log.d("BankListResponse",response);
        String jString = null;
        JSONArray jsonArray = null;
        try {
            JSONObject jsonObject = new JSONObject(response);
            if(jsonObject.has("response")) {
                jsonArray = jsonObject.getJSONArray("response");
            } else {
                jsonArray = null;
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return jsonArray;
    }


}
