package com.ktrlabs.thapelo.iwallet;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
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

import com.google.zxing.WriterException;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.math.BigInteger;
import java.text.DecimalFormatSymbols;
import java.text.NumberFormat;

import static com.ktrlabs.thapelo.iwallet.QuickReference.bitmapToString;

public class ReceiveActivity extends AppCompatActivity {
    NumberFormat format = NumberFormat.getCurrencyInstance();
    DecimalFormatSymbols dfs = new DecimalFormatSymbols();
    QuickReference quickReference = new QuickReference();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_receive);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        final NumberFormat format = NumberFormat.getCurrencyInstance();
        DecimalFormatSymbols dfs = new DecimalFormatSymbols();
        QuickReference quickReference = new QuickReference();

        quickReference.context = getApplicationContext();
        quickReference.resources = getResources();

        JSONObject data = new JSONObject();
        String accountNumber = new BankAi().getStokedKey(getApplicationContext(), "AccountNumber");
        String cellphone = new BankAi().getStokedKey(getApplicationContext(), "Cellphone");
        if (accountNumber != "failed") {
            JSONObject jObj = new JSONObject();
            try {

                jObj.put("cellPhoneID", new BankAi().getStokedKey(getApplicationContext(), "PhoneNumber"));
                jObj.put("accountNumber", accountNumber);
                jObj.put("amount", "");
                jObj.put("fee", "");
                jObj.put("type", "Transfer");
                // items.add(jObj);
            } catch (JSONException e) {
                e.printStackTrace();
            }
            Log.d("data", jObj.toString());
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
            Log.d("data4", jObj.toString());
            String RecipientDetails = jObj.toString();
            if (new BankAi().getStokedKey(this,"bitmapReceive")!="failed") {
                ((ImageView) findViewById(R.id.qr_receive)).setImageBitmap(stringToBitmap(new BankAi().getStokedKey(this,"bitmapReceive")));
            } else {
                new getQrCode(true,"bitmapReceive").execute(RecipientDetails);
            }
            if (new BankAi().getStokedKey(this,"barcodeReceive")!="failed") {
                ((ImageView) findViewById(R.id.barq_receive)).setImageBitmap(stringToBitmap(new BankAi().getStokedKey(this,"barcodeReceive")));
            } {
                new getBarCode(true,"barcodeReceive").execute(bigInteger);
            }
        }


        final TextView rcvAmtView = (TextView) findViewById(R.id.receive_amount);
        Button rcvAmtBtn = (Button) findViewById(R.id.receive_button);
        rcvAmtBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (rcvAmtView != null || rcvAmtView.getText() != null) {
                    String amt = rcvAmtView.getText().toString();
                    rcvAmtView.clearFocus();
                    ((TextView) findViewById(R.id.receive_value)).setText(format.format(Float.parseFloat(amt)));
                    JSONObject data = new JSONObject();
                    String accountNumber = new BankAi().getStokedKey(getApplicationContext(), "AccountNumber");
                    String cellphone = new BankAi().getStokedKey(getApplicationContext(), "Cellphone");
                    JSONObject jObj = new JSONObject();
                    if (accountNumber != "failed") {
                        try{
                            jObj.put("cellPhoneID", new BankAi().getStokedKey(getApplicationContext(), "PhoneNumber"));
                            jObj.put("accountNumber", accountNumber);
                            jObj.put("amount", amt);
                            jObj.put("fee", "");
                            jObj.put("type","Transfer");
                            // items.add(jObj);
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                        Log.d("data11",jObj.toString());
                        String RecipientDetails = jObj.toString();
                        new getQrCode(false,"Receive").execute(RecipientDetails);
                    }
                }
                findViewById(R.id.receive_edit).setVisibility(View.GONE);
            }
        });

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }


    public void goToSetAmount(View view) {
        findViewById(R.id.receive_edit).setVisibility(View.VISIBLE);
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
            Bitmap bitmap = null;
            try {
                Log.d("data99", params[0]);
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
            ((ImageView) findViewById(R.id.qr_receive)).setImageBitmap(s);

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
            ((ImageView) findViewById(R.id.barq_receive)).setImageBitmap(s);
        }
    }

}
