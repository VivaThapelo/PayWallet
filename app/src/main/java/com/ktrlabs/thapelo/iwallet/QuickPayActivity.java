package com.ktrlabs.thapelo.iwallet;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.MultiFormatWriter;
import com.google.zxing.WriterException;
import com.google.zxing.common.BitMatrix;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.UnsupportedEncodingException;
import java.math.BigInteger;
import java.security.Timestamp;
import java.sql.Time;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.text.NumberFormat;
import java.util.Arrays;
import java.util.Calendar;

import static com.ktrlabs.thapelo.iwallet.QuickReference.bitmapToString;

public class QuickPayActivity extends AppCompatActivity {
    public final static int QRcodeWidth = 250;
    private int seconds;
    NumberFormat format = NumberFormat.getCurrencyInstance();
    DecimalFormatSymbols dfs = new DecimalFormatSymbols();
    QuickReference quickReference = new QuickReference();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_qr_pay);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        quickReference.context = getApplicationContext();
        quickReference.resources = getResources();

        dfs.setCurrencySymbol("R");
        //dfs.setGroupingSeparator('.');
        dfs.setMonetaryDecimalSeparator('.');
        ((DecimalFormat) format).setDecimalFormatSymbols(dfs);

        final TextView rcvAmtView = (TextView) findViewById(R.id.payment_amount);
        Button rcvAmtBtn = (Button) findViewById(R.id.payment_button);
        rcvAmtBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (rcvAmtView != null || rcvAmtView.getText() != null) {
                    String amt = rcvAmtView.getText().toString();
                    rcvAmtView.clearFocus();
                    ((TextView) findViewById(R.id.payment_value)).setText(format.format(Float.parseFloat(amt)));
                    JSONObject data = new JSONObject();
                    String accountNumber = new BankAi().getStokedKey(getApplicationContext(), "AccountNumber");
                    String cellphone = new BankAi().getStokedKey(getApplicationContext(), "Cellphone");
                    JSONObject jObj = new JSONObject();
                    if (accountNumber != "failed") {
                        try{
                            jObj.put("pid","quickpay");
                            jObj.put("cellPhoneID", new BankAi().getStokedKey(getApplicationContext(), "PhoneNumber"));
                            jObj.put("accountNumber", accountNumber);
                            jObj.put("amount", amt);
                            jObj.put("fee", "");
                            jObj.put("type","Transfer");
                            jObj.put("xauth",new BankAi().getStokedKey(getApplicationContext(), "x-auth"));
                            jObj.put("type", "Payment");
                            // items.add(jObj);
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                        Log.d("data",jObj.toString());
                        String RecipientDetails = jObj.toString();
                        new getQrCode(false,"Payment").execute(RecipientDetails);
                    }
                }
                findViewById(R.id.payment_edit).setVisibility(View.GONE);
            }
        });

        JSONObject data = new JSONObject();
        String accountNumber = new BankAi().getStokedKey(getApplicationContext(), "AccountNumber");
        String cellphone = new BankAi().getStokedKey(getApplicationContext(), "Cellphone");
        if (accountNumber != "failed") {
            JSONObject jObj = new JSONObject();
            try {
                jObj.put("pid","quickpay");
                jObj.put("cellPhoneID", new BankAi().getStokedKey(getApplicationContext(), "PhoneNumber"));
                jObj.put("accountNumber", accountNumber);
                jObj.put("amount", "");
                jObj.put("fee", "");
                jObj.put("xauth",new BankAi().getStokedKey(getApplicationContext(), "x-auth"));
                jObj.put("type", "Payment");
                Calendar c = Calendar.getInstance();
                seconds = c.get(Calendar.SECOND);
                // payment must go through within 30 seconds @ (till = seconds - 30) verification
               // jObj.put("security", seconds);
                // items.add(jObj);
            } catch (JSONException e) {
                e.printStackTrace();
            }
            Log.d("data", jObj.toString());
            String RecipientDetails = jObj.toString();
            String bigInteger = null;
            try {
                Log.d("xauthTest",new BankAi().getStokedKey(getApplicationContext(), "x-auth"));
                bigInteger = new BigInteger((new BankAi().getStokedKey(getApplicationContext(), "x-auth")).getBytes("UTF-8")).toString(32);
            //convert back
                Log.d("newTEST",bigInteger);
                String textBack = new String(new BigInteger(bigInteger,32).toByteArray());
                Log.d("xauthTESTconvert","And back = " + textBack);
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            }
            if (new BankAi().getStokedKey(this,"bitmapPayment")!="failed") {
                ((ImageView) findViewById(R.id.qr_payment)).setImageBitmap(stringToBitmap(new BankAi().getStokedKey(this,"bitmapPayment")));
            } else {
                new getQrCode(true,"bitmapPayment").execute(RecipientDetails);
            }
           if (new BankAi().getStokedKey(this,"barcodePayment")!="failed") {
               ((ImageView) findViewById(R.id.barq_payment)).setImageBitmap(stringToBitmap(new BankAi().getStokedKey(this,"barcodePayment")));
          } {
              new getBarCode(true,"barcodePayment").execute(bigInteger);
          }

        }

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }

    public void goToSetAmount(View view) {
        findViewById(R.id.payment_edit).setVisibility(View.VISIBLE);
    }
    public final static Bitmap stringToBitmap(String in) {
        byte[] bytes = Base64.decode(in, Base64.DEFAULT);
        return BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
    }


    public class getQrCode extends AsyncTask<String, String, Bitmap> {
        private Bitmap bitmap = null;
        public Boolean save = true;
        public String type;

        public getQrCode(Boolean save,String type) {
            this.save = save;
            this.type = type;
        }

        @Override
        protected Bitmap doInBackground(String... params) {

            try {
                Log.d("data", params[0]);
                bitmap = quickReference.getQr(params[0]);

            } catch (WriterException e) {
                e.printStackTrace();
            }
            if (save==true) {
                new BankAi().storeKey(getApplicationContext(), type, bitmapToString(bitmap));
            }
            return bitmap;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected void onPostExecute(Bitmap s) {
            super.onPostExecute(s);
            ((ImageView) findViewById(R.id.qr_payment)).setImageBitmap(s);
        }
    }

    public class getBarCode extends AsyncTask<String, String, Bitmap> {
        private Bitmap bitmap = null;
        public Boolean save = true;
        public String type;
        public String param;

        public getBarCode(Boolean save,String type) {
            this.save = save;
            this.type = type;
        }
        @Override
        protected Bitmap doInBackground(String... params) {
            Bitmap bitmap = null;
            param = params[0];
            try {
                Log.d("dataBarCode", params[0]);
                bitmap = new QuickReference().getBar(params[0]);
            } catch (WriterException e) {
                e.printStackTrace();
            }
            if (save==true) {
                new BankAi().storeKey(getApplicationContext(), type, bitmapToString(bitmap));
            }
            return bitmap;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();

        }

        @Override
        protected void onPostExecute(Bitmap s) {
            super.onPostExecute(s);
            ((ImageView) findViewById(R.id.barq_payment)).setImageBitmap(s);
        }
    }

}
