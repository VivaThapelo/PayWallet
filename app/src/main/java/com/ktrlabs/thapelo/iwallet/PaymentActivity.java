package com.ktrlabs.thapelo.iwallet;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.location.LocationManager;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.text.NumberFormat;

public class PaymentActivity extends AppCompatActivity {

    TextView tvi;
    MainActivity MA = new MainActivity();
    NumberFormat format = NumberFormat.getCurrencyInstance();
    DecimalFormatSymbols dfs = new DecimalFormatSymbols();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_payment);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        tvi = (TextView) findViewById(R.id.send_account_number);


        dfs.setCurrencySymbol("R");
        //dfs.setGroupingSeparator('.');
        dfs.setMonetaryDecimalSeparator('.');
        ((DecimalFormat) format).setDecimalFormatSymbols(dfs);

        TextWatcher inputTextWatcher = new TextWatcher() {
            public void afterTextChanged(Editable s) {
                try {
                    ((TextView) findViewById(R.id.amount)).setText(format.format(Double.parseDouble(s.toString())));
                    ((TextView) findViewById(R.id.fee)).setText(format.format(Double.parseDouble(s.toString())/40) + " fee");
                } catch (Exception e) {
                    ((TextView) findViewById(R.id.amount)).setText(format.format(0));
                    ((TextView) findViewById(R.id.fee)).setText(format.format(0) + " fee");
                    e.printStackTrace();
                }

            }
            public void beforeTextChanged(CharSequence s, int start, int count, int after){
            }
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }
        };

        ((EditText) findViewById(R.id.send_amount)).addTextChangedListener(inputTextWatcher);

        ((ImageButton) findViewById(R.id.send_clear)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ((TextView) findViewById(R.id.send_amount)).setText("");
            }
        });

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        final String result = getIntent().getStringExtra("result");

        // Transfer credit
        final TextView sendAm = (TextView) findViewById(R.id.send_amount);
        if (result.split("@").length == 4) {
            sendAm.setText(result.split("@")[3]);
            sendAm.setEnabled(false);
        }
        TextView tv = (TextView) findViewById(R.id.send_account_number);
        //tv.setText(result.split("@")[1]);
        // sendAm.setVisibility(View.VISIBLE);
        // new getQRbar().execute("send", result);

        final Button button = (Button) findViewById(R.id.send_button);
        button.setVisibility(View.VISIBLE);

        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //DynamicBox sendSpinner = new DynamicBox(getApplicationContext(),R.layout.scanpay_page);
                //sendSpinner.showLoadingLayout();
                String amount = ((TextView) findViewById(R.id.send_amount)).getText().toString();
                //String cell
                String resultt = null;
                try {
                    JSONObject jsonObject = new JSONObject(result);
                    jsonObject.put("amount",amount);
                    resultt = jsonObject.toString();
                    Log.d("resultt",jsonObject.toString());
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                String bal = new BankAi().getStokedKey(getApplicationContext(), "AvailableBalance");
                Log.d("Balance: "+bal,"Amount: "+amount);
                if (Math.round(Float.parseFloat(bal)) > Float.parseFloat(amount)) {
                    AsyncTask<String, String, String> er = new sendMoney().execute(resultt);
                    if (er.getStatus() == AsyncTask.Status.FINISHED) {
                        // new MainActivity.getTransactionList().execute();
                        //   new MainActivity.refreshAccountDetails().execute();
                    }
                    sendAm.setText(null);
                    sendAm.setEnabled(true);
                } else {
                    Toast toast = Toast.makeText(getApplicationContext(), "You don't have enough balance", Toast.LENGTH_LONG);
                    toast.show();
                }

            }
        });
    }

    public void pinCode(View view) {
        String key = ((Button)view).getText().toString();
        EditText sen_amount = (EditText) findViewById(R.id.send_amount);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            sen_amount.setShowSoftInputOnFocus(false);
        }
        // if (KeyisInteger(key)) {
        if(sen_amount != null) {
            Log.d("PinPage","Clicked button:"+ key);
            sen_amount.append(key);
        }
    }

    public class sendMoney extends AsyncTask<String, String, String> {
        @Override
        protected String doInBackground(String... params) {
            JSONObject jsonObject = null;
            String toCell = null,account=null,amount = null,type=null;
            try {
                jsonObject = new JSONObject(params[0]);
                toCell = jsonObject.getString("cellPhoneID");
                account = jsonObject.getString("accountNumber");
                type = jsonObject.getString("type");
                amount = jsonObject.getString("amount");
            } catch (JSONException e) {
                e.printStackTrace();
            }
            String fromCell = new BankAi().getStokedKey(getApplicationContext(), "ContactNumber1");

            String res = null;
            String[] coords = null;
            LocationManager lm = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
            // if (ActivityCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            //   ActivityCompat.requestPermissions(MainActivity.this,new String[]{Manifest.permission.ACCESS_FINE_LOCATION},MY_PERMISSIONS_REQUEST_LOCATION);
            //    Location location = lm.getLastKnownLocation(LocationManager.GPS_PROVIDER);
            //    String lon =  Double.toString(location.getLongitude());
            //     String lat = Double.toString(location.getLatitude());
            //    coords = new String[]{lon, lat};
            //   return null;
            //return TODO;
            // } else {
            coords = null;
            // }

            try {
                res = new BankAi().CreditTransfer(toCell, account, amount, type, new BankAi().getStokedKey(getApplicationContext(), "AccountNumber"),new BankAi().getStokedKey(getApplicationContext(), "x-auth"),coords);
                new  BankAi().SendSms(toCell,"Hello " +  new BankAi().getName(new BankAi().getStokedKey(getApplicationContext(), "x-auth"),account) +", this is to confirm " +  new BankAi().getStokedKey(getApplicationContext(),"AccountHolderName") +" just sent you R"+ amount + " on iWallet");
                new  BankAi().SendSms(fromCell,"Hello "+ new BankAi().getStokedKey(getApplicationContext(),"AccountHolderName") + ", this is to confirm that you just sent R"+ amount +" to "+new BankAi().getName(new BankAi().getStokedKey(getApplicationContext(), "x-auth"),account)+" on iWallet");
            } catch (IOException e) {
                e.printStackTrace();
            }
            return res;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            tvi.setText("Transaction Loading");
            tvi.setVisibility(View.VISIBLE);
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            if (s != null ) {
                Toast.makeText(getApplicationContext(),s,Toast.LENGTH_LONG);
                tvi.setTextColor(Color.GREEN);
                Log.d("Credit transfer",s);
                tvi.setText("Transaction Successful");
                tvi.setTextSize(40);
                ((LinearLayout) findViewById(R.id.payment)).setVisibility(View.INVISIBLE);
                Intent intent = new Intent();
                intent.setClass(getApplicationContext(),SuccessActivity.class);
                startActivity(intent);
                //new MainActivity.BankLoginAsync().execute("");
            } else {
                tvi.setTextColor(Color.RED);
                Log.d("Credit transfer",s);
                tvi.setText("Error: Transaction Failed");
            }

        }
    }

}
