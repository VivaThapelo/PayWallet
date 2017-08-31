package com.ktrlabs.thapelo.iwallet;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.Paint;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.provider.ContactsContract;
import android.support.annotation.LayoutRes;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.internal.BottomNavigationItemView;
import android.support.design.internal.BottomNavigationMenuView;
import android.support.design.widget.BottomNavigationView;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.util.Base64;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.anthonycr.grant.PermissionsManager;
import com.anthonycr.grant.PermissionsResultAction;
import com.google.zxing.BarcodeFormat;
import com.google.zxing.MultiFormatWriter;
import com.google.zxing.ResultPoint;
import com.google.zxing.WriterException;
import com.google.zxing.client.android.BeepManager;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.integration.android.IntentIntegrator;
import com.journeyapps.barcodescanner.BarcodeCallback;
import com.journeyapps.barcodescanner.BarcodeResult;
import com.journeyapps.barcodescanner.CaptureManager;
import com.journeyapps.barcodescanner.DecoratedBarcodeView;
import com.mikhaellopez.circularimageview.CircularImageView;
import com.wang.avi.AVLoadingIndicatorView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.lang.reflect.Field;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import mehdi.sakout.dynamicbox.DynamicBox;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener, DatePickerDialog.OnDateSetListener {


    View viewb,views, viewh, viewr,viewc = null;
    TextView viewTitle;
    MenuItem userMenuItem = null;
    MenuItem scanMenuItem = null;
    DecoratedBarcodeView cameraPreview;
    private CaptureManager capture;
    public final static int QRcodeWidth = 200;
    ImageView qrView = null;
    MenuItem balance = null;
    TransactionsAdapter transactionsAdapter = null;
    ListView transactionsList;
    ArrayList<String> stringArrayList = new ArrayList<>();
    TextView tvi = null;
    TextView codeView = null;
    public static final int MY_PERMISSIONS_REQUEST_LOCATION = 99;
    NumberFormat format = NumberFormat.getCurrencyInstance();
    DecimalFormatSymbols dfs = new DecimalFormatSymbols();
    private BeepManager beepManager;
    private String lastText;
    FloatingActionButton fab;
    Integer scanState = 1;
    AVLoadingIndicatorView avi;
    LinearLayout paymentLayout;
    public String acc;

    private BottomNavigationView.OnNavigationItemSelectedListener mOnNavigationItemSelectedListener
            = new BottomNavigationView.OnNavigationItemSelectedListener() {

        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item) {
            switch (item.getItemId()) {
                case R.id.navigation_bills:
                    viewToggle(viewb);
                    return true;
                case R.id.navigation_send:
                    viewToggle(views);
                    return true;
                case R.id.navigation_home:
                    viewToggle(viewh);
                    return true;
                case R.id.navigation_receive:
                    viewToggle(viewr);
                    return true;
                case R.id.navigation_shopping:
                    viewToggle(viewc);
                    return true;
            }
            return false;
        }

    };

    @Override
    public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {

    }

    public class getQrCode extends AsyncTask<String, String, Bitmap> {
        @Override
        protected Bitmap doInBackground(String... params) {
            Bitmap bitmap = null;
            try {
                Log.d("data", params[0]);
                bitmap = getQr(params[0]);
            } catch (WriterException e) {
                e.printStackTrace();
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

    Bitmap getQr(String Value) throws WriterException {
        BitMatrix bitMatrix;
        try {
            bitMatrix = new MultiFormatWriter().encode(
                    Value,
                    BarcodeFormat.DATA_MATRIX.QR_CODE,
                    QRcodeWidth, QRcodeWidth, null
            );

        } catch (IllegalArgumentException Illegalargumentexception) {

            return null;
        }
        int bitMatrixWidth = bitMatrix.getWidth();

        int bitMatrixHeight = bitMatrix.getHeight();

        int[] pixels = new int[bitMatrixWidth * bitMatrixHeight];

        for (int y = 0; y < bitMatrixHeight; y++) {
            int offset = y * bitMatrixWidth;

            for (int x = 0; x < bitMatrixWidth; x++) {

                pixels[offset + x] = bitMatrix.get(x, y) ?
                        getResources().getColor(R.color.black) : getResources().getColor(R.color.white);
            }
        }
        Bitmap bitmap = Bitmap.createBitmap(bitMatrixWidth, bitMatrixHeight, Bitmap.Config.ARGB_4444);

        bitmap.setPixels(pixels, 0, QRcodeWidth, 0, 0, bitMatrixWidth, bitMatrixHeight);
        new BankAi().storeKey(getApplicationContext(), "bitmap", bitmapToString(bitmap));
        return bitmap;
    }

    public final static String bitmapToString(Bitmap in) {
        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        in.compress(Bitmap.CompressFormat.PNG, 100, bytes);
        return Base64.encodeToString(bytes.toByteArray(), Base64.DEFAULT);
    }

    public final static Bitmap stringToBitmap(String in) {
        byte[] bytes = Base64.decode(in, Base64.DEFAULT);
        return BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
    }

    public void viewToggle(View view) {
        if (view == views) {
            views.setVisibility(View.VISIBLE);
            //   userMenuItem.setVisible(true);
            //userMenuItem.setTitle("Send a friend");
            // hide others
            viewr.setVisibility(View.GONE);
            viewh.setVisibility(View.GONE);
            viewb.setVisibility(View.GONE);
            viewc.setVisibility(View.GONE);
        } else if (view == viewr) {
            viewr.setVisibility(View.VISIBLE);
            //userMenuItem.setTitle("Ask A Friend");
            // hide others
            // userMenuItem.setVisible(true);
            views.setVisibility(View.GONE);
            viewh.setVisibility(View.GONE);
            viewc.setVisibility(View.GONE);
            viewb.setVisibility(View.GONE);
            //((ViewGroup) views.getParent()).removeView(views);
            // ((ViewGroup) viewh.getParent()).removeView(viewh);
        } else if(view  == viewc) {
            viewc.setVisibility(View.VISIBLE);
            views.setVisibility(View.GONE);
            viewb.setVisibility(View.GONE);
            viewh.setVisibility(View.GONE);
            viewr.setVisibility(View.GONE);
        }else if(view  == viewb){
                viewb.setVisibility(View.VISIBLE);
                views.setVisibility(View.GONE);
                viewc.setVisibility(View.GONE);
                viewh.setVisibility(View.GONE);
                viewr.setVisibility(View.GONE);
        } else {
            viewh.setVisibility(View.VISIBLE);
            //hide others
            // userMenuItem.setVisible(false);
            views.setVisibility(View.GONE);
            viewr.setVisibility(View.GONE);
            viewb.setVisibility(View.GONE);
            viewc.setVisibility(View.GONE);
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_transactions);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        PermissionsManager.getInstance().requestAllManifestPermissionsIfNecessary(this,
                new PermissionsResultAction() {
                    @Override
                    public void onGranted() {
                        // Proceed with initialization
                    }

                    @Override
                    public void onDenied(String permission) {
                        //Notify the user that you need all of the permissions
                        Toast.makeText(MainActivity.this,
                                "Sorry, we need those Permissions",
                                Toast.LENGTH_LONG).show();
                    }
                });

        if ((getResources().getConfiguration().screenLayout &
                Configuration.SCREENLAYOUT_SIZE_MASK) ==
                Configuration.SCREENLAYOUT_SIZE_SMALL) {
            // on a large screen device ...
            ((LinearLayout) findViewById(R.id.keyboard)).setVisibility(View.GONE);
        } else {
            hideSoftKeyboard();
        }

       // final DatePickerDialog datePickerDialog = new DatePickerDialog(MainActivity.this, MainActivity.this, 2017, 5, 22);

       // ((ImageButton) findViewById(R.id.datepicker)).setOnClickListener(new View.OnClickListener() {
       //     @Override
        //    public void onClick(View v) {
       //         datePickerDialog.show();
     //       }
      //  });


        new BankAi().context = getApplicationContext();

        dfs.setCurrencySymbol("R");
        //dfs.setGroupingSeparator('.');
        dfs.setMonetaryDecimalSeparator('.');
        ((DecimalFormat) format).setDecimalFormatSymbols(dfs);

        viewb = (View) findViewById(R.id.bills_transactions);
        views = (View) findViewById(R.id.send_transactions);
        viewh = (View) findViewById(R.id.home_transactions);
        viewr = (View) findViewById(R.id.receive_transactions);
        viewc = (View) findViewById(R.id.shopping_transactions);
       // viewTitle = (TextView) findViewById(R.id.viewTitle);
        cameraPreview = (DecoratedBarcodeView) findViewById(R.id.camera_preview);
        //cameraPreview.decodeContinuous(callback);
        beepManager = new BeepManager(this);
        cameraPreview.setStatusText("");

        if (new BankAi().getStokedKey(getApplicationContext(), "idNumber") != "failed" && new BankAi().getStokedKey(getApplicationContext(), "password") != "failed") {
            Log.d("Details refresh happens", new BankAi().getStokedKey(getApplicationContext(), "idNumber"));
            new BankLoginAsync().execute();
        }

        ((ImageButton) findViewById(R.id.receive_clear)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ((TextView) findViewById(R.id.receive_amount)).setText("0");
            }
        });

        new getTransactionList().execute();
        new refreshAccountDetails().execute();

        transactionsAdapter = new TransactionsAdapter(getApplicationContext(), R.layout.transactions_listview_item, stringArrayList);

        transactionsList = (ListView) findViewById(R.id.transactions_listview);
        DynamicBox transactionSpinner = new DynamicBox(this,transactionsList);
        transactionSpinner.showLoadingLayout();
        transactionsList.setAdapter(transactionsAdapter);
        transactionSpinner.hideAll();

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
                        Log.d("data",jObj.toString());
                        String RecipientDetails = jObj.toString();
                        new getQrCode().execute(RecipientDetails);
                    }
                }
                findViewById(R.id.receive_edit).setVisibility(View.GONE);
            }
        });


        capture = new CaptureManager(this, cameraPreview);
        capture.initializeFromIntent(getIntent(), savedInstanceState);
        cameraPreview.decodeSingle(callback);


        TextView tView = (TextView) findViewById(R.id.account_number);
        tView.setText((new BankAi().getStokedKey(getApplicationContext(), "AccountNumber")));


        final BottomNavigationView navigation = (BottomNavigationView) findViewById(R.id.navigation);
        navigation.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);
        BottomNavigationViewHelper.disableShiftMode(navigation);
        navigation.setSelectedItemId(R.id.navigation_bills);

        fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                navigation.setSelectedItemId(R.id.navigation_send);
                // onNavigationItemSelected((MenuItem) findViewById(R.id.nav_send));
            }
        });

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
                String RecipientDetails = jObj.toString();
                new getQrCode().execute(RecipientDetails);
        }


        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);


        View headerView = navigationView.getHeaderView(0);
        View rootView = navigationView.getRootView();
        TextView accountHolderName = (TextView) headerView.findViewById(R.id.accountHolderName);
        accountHolderName.setText(new BankAi().getStokedKey(getApplicationContext(), "AccountHolderName"));

        TextView accountNumbers = (TextView) headerView.findViewById(R.id.accountNumber);
        accountNumbers.setText((new BankAi().getStokedKey(getApplicationContext(), "AccountNumber")));
    }

    public class SMS extends AsyncTask<String[], String, String> {
        @Override
        protected String doInBackground(String[]... params) {
            try {
                new BankAi().SendSms(params[0][0], params[0][1]);
            } catch (IOException e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            
        }
    }

    private BarcodeCallback callback = new BarcodeCallback() {
        @Override
        public void barcodeResult(final BarcodeResult result) {
            Log.v("callback", " It got here");
            if (result != null) {
                if (result.getText() == null) {
                    Toast.makeText(getApplicationContext(), "Cancelled", Toast.LENGTH_LONG).show();
                } else {
                    lastText = result.getText();
                    beepManager.playBeepSoundAndVibrate();
                    //paymentLayout.setVisibility(View.VISIBLE);
                    fab.setImageResource(R.drawable.ic_clear_black_24dp);
                    cameraPreview.pause();
                    Toast.makeText(getApplicationContext(), "Scanned: " + result.getText(), Toast.LENGTH_LONG).show();
                    Intent intent = new Intent();
                    intent.setClass(getApplicationContext(),PaymentActivity.class);
                    intent.putExtra("result",result.getText());
                    startActivity(intent);
                     new MainActivity.getTransactionList().execute();
                    new MainActivity.refreshAccountDetails().execute();
                    new MainActivity.BankLoginAsync().execute();

                /*  button.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            Log.d("first click"," got here");
                            Random random = new Random();

                            final int code = random.nextInt((99999 - 10000) + 1) + 10000;

                            final TextInputLayout textInputLayout = (TextInputLayout) findViewById(R.id.send_code_layout);
                            //textInputLayout.setVisibility(View.VISIBLE);
                            //new BankAi().storeKey(getApplicationContext(),"code",Integer.toString(code));
                            /*    String[] sms = {new BankAi().getStokedKey(getApplicationContext(),"ContactNumber1"),Integer.toString(code)};
                                new  SMS().execute(sms);

                        }
                    });*/
                }
            } else {
                Log.v("callback", " No results found");
            }
        }

        @Override
        public void possibleResultPoints(List<ResultPoint> resultPoints) {

        }


    };


    public class refreshAccountDetails extends AsyncTask<String, String, String> {
        @Override
        protected String doInBackground(String... params) {
            try {
                new BankAi().getAndStoreAccountDetails(getApplicationContext(), new BankAi().getStokedKey(getApplicationContext(), "x-auth"));
            } catch (IOException e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();

        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            ((TextView) findViewById(R.id.balance)).setText(format.format(Float.parseFloat(new BankAi().getStokedKey(getApplicationContext(), "AvailableBalance"))) );

        }
    }


    public class getTransactionList extends AsyncTask<String, String, ArrayList> {
        @Override
        protected ArrayList<JSONObject> doInBackground(String... params) {
            JSONArray jArray = null;
            try {
                jArray = new BankAi().TransactionList(new BankAi().getStokedKey(getApplicationContext(), "x-auth"), new BankAi().getStokedKey(getApplicationContext(), "AccountNumber"));
            } catch (IOException e) {
                e.printStackTrace();
            }
            ArrayList<JSONObject> items = new ArrayList<JSONObject>();
            if (jArray == null) {
                return items;
            } else {
                for (int i = 0; i < jArray.length(); i++) {
                    JSONObject json_data = null;
                    try {
                        json_data = jArray.getJSONObject(i);
                        JSONObject receiver = json_data.getJSONObject("Receiver");
                        JSONObject sender = json_data.getJSONObject("Sender");
                        String sndName = sender.getString("Name");
                        String rcvName = receiver.getString("Name");
                        String date = json_data.getString("Timestamp");
                        String amount = json_data.getString("Amount");
                        String fee = json_data.getString("Fee");
                        String desc = json_data.getString("Desc");
                        Float amt = Float.parseFloat(amount);
                        String name = null;
                        if (amt < 0) {
                            name = rcvName;
                        } else {
                            name = sndName;
                        }
                        //int id=json_data.getInt("id");
                        JSONObject jObj = new JSONObject();
                        jObj.put("name", name);
                        jObj.put("date", date);
                        jObj.put("amount", amount);
                        jObj.put("fee", fee);
                        jObj.put("type",desc);
                        items.add(jObj);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            }

            return items;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            
        }

        @Override
        protected void onPostExecute(ArrayList strings) {
            super.onPostExecute(strings);
            //stringArrayList.addAll(strings);
            transactionsAdapter.clear();
            transactionsAdapter.addAll(strings);
            if (strings.isEmpty()) {
                LinearLayout linearLayout = (LinearLayout) findViewById(R.id.home_transactions);
                TextView textView = new TextView(getApplicationContext());
                textView.setTextColor(Color.GRAY);
                textView.setTextSize(14);
                textView.setPadding(18, 18, 18, 18);
                textView.setText("No Transactions ( or Network Connection Error )");
                linearLayout.addView(textView);
                
            }
        }
    }

    //code
    @Override
    public void onActivityResult(int reqCode, int resultCode, Intent data) {
        super.onActivityResult(reqCode, resultCode, data);
        String cNumber = null;
        final int PICK_CONTACT = 1;

        switch (reqCode) {
            case (PICK_CONTACT):
                if (resultCode == Activity.RESULT_OK) {

                    Uri contactData = data.getData();
                    Cursor c = managedQuery(contactData, null, null, null, null);
                    if (c.moveToFirst()) {


                        String id = c.getString(c.getColumnIndexOrThrow(ContactsContract.Contacts._ID));

                        String hasPhone = c.getString(c.getColumnIndex(ContactsContract.Contacts.HAS_PHONE_NUMBER));

                        if (hasPhone.equalsIgnoreCase("1")) {
                            Cursor phones = getContentResolver().query(
                                    ContactsContract.CommonDataKinds.Phone.CONTENT_URI, null,
                                    ContactsContract.CommonDataKinds.Phone.CONTACT_ID + " = " + id,
                                    null, null);
                            phones.moveToFirst();
                            cNumber = phones.getString(phones.getColumnIndex("data1"));
                            Log.d("number is:", cNumber);
                        }
                        String name = c.getString(c.getColumnIndex(ContactsContract.Contacts.DISPLAY_NAME));
                        JSONObject jsonObject = new JSONObject();
                        try {
                            jsonObject.put("cellPhoneID",cNumber.replaceAll("\\s",""));
                            jsonObject.put("amount",0);
                            jsonObject.put("fee","");
                            jsonObject.put("type","Transfer");
                            jsonObject.put("accountNumber", "");
                            new getAccWithID().execute(jsonObject);
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                }
                break;
        }
    }

    public class getAccWithID extends AsyncTask<JSONObject,String,JSONObject> {

        @Override
        protected JSONObject doInBackground(JSONObject... params) {
            String resp = null;
            try {
                resp = new BankAi().getAccountWithID(params[0].getString("cellPhoneID"), new BankAi().getStokedKey(getApplicationContext(),"x-auth"));
                params[0].put("accountNumber",resp);
            } catch (IOException e) {
                e.printStackTrace();
            } catch (JSONException e) {
                e.printStackTrace();
            }
            return params[0];
        }

        @Override
        protected void onPostExecute(JSONObject s) {
            super.onPostExecute(s);
            Log.d("atad",s.toString());
            Intent intent = new Intent();
            intent.setClass(getApplicationContext(),PaymentActivity.class);
            intent.putExtra("result",s.toString());
            startActivity(intent);
        }
    }

    public class TransactionsAdapter extends ArrayAdapter<JSONObject> {
        public TransactionsAdapter(@NonNull Context context, @LayoutRes int resource, ArrayList objects) {
            super(context, resource, objects);
        }

        public class ViewHolder {
            CircularImageView propic;
            TextView fee;
            TextView type;
            TextView name;
            TextView amount;
            TextView date;
        }

        @NonNull
        @Override
        public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
            //return super.getView(position, convertView, parent);
            Log.d("from atrray list", getItem(position).toString());
            JSONObject jsonObject = getItem(position);
            ViewHolder holder;
            if (convertView == null) {
                LayoutInflater vi = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                convertView = vi.inflate(R.layout.transactions_listview_item, null);
                holder = new ViewHolder();
                holder.fee = (TextView) convertView.findViewById(R.id.fee);
                holder.type = (TextView) convertView.findViewById(R.id.type);
                holder.propic = (CircularImageView) convertView.findViewById(R.id.profile_image);
                holder.name = (TextView) convertView.findViewById(R.id.name);
                holder.date = (TextView) convertView.findViewById(R.id.date);
                holder.amount = (TextView) convertView.findViewById(R.id.amount);

                convertView.setTag(holder);
            } else {
                holder = (ViewHolder) convertView.getTag();
            }
            try {
                Integer inc = position;
                if (holder.propic != null) {
                   // holder.propic.setBorderColor(myColorRand(inc) );
                }
               // convertView.setBackgroundColor(myColorRand(inc));
                if (holder.name != null) {
                    if (jsonObject.getString("name").length()==0) {
                        holder.name.setText("Bank");
                    } else {
                        holder.name.setText(jsonObject.getString("name"));
                    }
                }
                if (holder.date != null) {
                    holder.date.setText(getDateTime(jsonObject.getLong("date")));
                }
                if (holder.amount != null) {
                    if (Double.parseDouble(jsonObject.getString("amount"))<0) {
                        holder.amount.setTextColor(Color.RED);
                    } else {
                        holder.amount.setTextColor(Color.GREEN);
                    }
                    holder.amount.setText( format.format(Float.parseFloat(jsonObject.getString("amount"))) );
                }
                if (holder.fee != null) {
                    holder.fee.setText(format.format(Float.parseFloat(jsonObject.getString("fee"))) + " fee");
                }
                if (holder.type != null) {
                    holder.type.setText(jsonObject.getString("type"));
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }

            return convertView;
        }
    }

    public Integer myColorRand(int inc) {
        String[] colors = {"#3c4fb5","#89c148","#00b9d1","#fee83a","#cc2d2d","#6537b5","#3e4fb5"};
        if (inc<=5) {
            inc++;
        } else {
            inc=0;
        }
        return Color.parseColor(colors[inc]);
    }

    public class BankLoginAsync extends AsyncTask<String, String, String> {
        @Override
        protected String doInBackground(String... params) {
            String idNumber = new BankAi().getStokedKey(getApplicationContext(), "idNumber");
            String password = new BankAi().getStokedKey(getApplicationContext(), "password");
            Log.d("login-idNumber", idNumber);
            Log.d("login-Password", password);
            new BankAi().bankLogin(getApplicationContext(), idNumber, password);
            return null;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            transactionsAdapter.notifyDataSetChanged();
            balance.setTitle(format.format(Float.parseFloat(new BankAi().getStokedKey(getApplicationContext(), "AvailableBalance"))) );
            
        }
    }

    public void getContact(View view) {
        Intent intent = new Intent(Intent.ACTION_PICK, ContactsContract.Contacts.CONTENT_URI);
        startActivityForResult(intent, 1);
    }

    private String getDateTime(long timeStampStr) {

        try {
            String date = new SimpleDateFormat("hh:mm a - dd MMMM yyyy").format(new Date(timeStampStr * 1000));
            return date;
        } catch (Exception ex) {
            ex.printStackTrace();
            return Long.toString(timeStampStr);
        }
    }
    
    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.transactions, menu);
        //userMenuItem = (MenuItem) menu.findItem(R.id.add_account);
        balance = menu.findItem(R.id.balance);
        if (new BankAi().getStokedKey(getApplicationContext(), "AvailableBalance") != "failed") {
            balance.setTitle(format.format(Float.parseFloat(new BankAi().getStokedKey(getApplicationContext(), "AvailableBalance"))));
        }
            return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
      //  if (id == R.id.action_settings) {
     //       return true;
      //  }

        if (id == R.id.deposit) {
            Intent intent = new Intent(this, Deposit.class);
            startActivity(intent);
        }

        if (id == R.id.balance) {
            Intent intent = new Intent(this, Account.class);
            startActivity(intent);
        }

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_home) {
            // Handle the camera action
            viewToggle(viewh);
      /*  } else if (id == R.id.nav_send) {
            viewToggle(views);
        } else if (id == R.id.nav_receive) {
            viewToggle(viewr); */
        } else if (id == R.id.nav_signout) {
            clear();
            Intent intent  = new Intent();
            intent.setClass(getApplicationContext(),LoginActivity.class);
            startActivity(intent);
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    public void clear()
    {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        //SharedPreferences prefs; // here you get your prefrences by either of two methods
        SharedPreferences.Editor editor = prefs.edit();
        editor.clear();
        editor.commit();
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        capture.onSaveInstanceState(outState);
    }

    @Override
    protected void onResume() {
        super.onResume();
        capture.onResume();
        if (new BankAi().getStokedKey(getApplicationContext(),"idNumber")!="failed" && new BankAi().getStokedKey(getApplicationContext(),"password")!="failed") {
            Log.d("From Resume","it will try to login");
            new BankLoginAsync().execute();
            new refreshAccountDetails().execute();
        }
        LocalBroadcastManager.getInstance(getApplicationContext()).registerReceiver(mMessageReceiver,
                new IntentFilter("myFunction"));
       /*try {
            timer = new Timer();
            timerTask = new TimerTask() {
                @Override
                public void run() {
                    //Download file here and refresh
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            new getTransactionList().execute("");
                            //transactionsAdapter.notifyDataSetChanged();
                            //transactionsList.notifyAll();
                            balance.setText("Balance: " + format.format(Float.parseFloat(new BankAi().getStokedKey(getApplicationContext(), "AvailableBalance"))) );
                        }
                    });
                }
            };
            timer.schedule(timerTask, 1000, 1000);
        } catch (IllegalStateException e){
            android.util.Log.i("Damn", "resume error");
        } */
    }


    private BroadcastReceiver mMessageReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            // Extract data included in the Intent
            //String from = intent.getStringExtra("from");
           // String message = intent.getStringExtra("message");
            //alert data here
            //Toast.makeText(getApplicationContext(),from+": "+message,Toast.LENGTH_LONG).show();
            new getTransactionList().execute();
            new refreshAccountDetails().execute();
        }
    };

    @Override
    public void onStop() {
        super.onStop();
        LocalBroadcastManager.getInstance(getApplicationContext()).unregisterReceiver(mMessageReceiver);

    }


    @Override
    protected void onPause() {
        super.onPause();
        capture.onPause();
        LocalBroadcastManager.getInstance(getApplicationContext()).unregisterReceiver(mMessageReceiver);
        //timer.cancel();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        capture.onDestroy();
        //timer.cancel();
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        return cameraPreview.onKeyDown(keyCode, event) || super.onKeyDown(keyCode, event);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String permissions[], @NonNull int[] grantResults) {
        capture.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }

    public void scanToolbar(View view) {
        new IntentIntegrator(this).initiateScan();
        viewToggle(view);
    }

    public static boolean KeyisInteger(String s) {
        try {
            Integer.parseInt(s);
        } catch(NumberFormatException e) {
            return false;
        } catch(NullPointerException e) {
            return false;
        }
        // only got here if we didn't return false
        return true;
    }

    public void goToGeoPay(View view)
    {
        Intent intent = new Intent(this, GeoPay.class);
        startActivity(intent);
    }

    public void goToSetAmount(View view) {
         findViewById(R.id.receive_edit).setVisibility(View.VISIBLE);
    }

    public void goToQrPay(View view)
    {
        Intent intent = new Intent(this, QuickPayActivity.class);
        startActivity(intent);
    }

    public void goToBasket(View view)
    {
        Intent intent = new Intent(this, Basket.class);
        startActivity(intent);
    }

    public void hideSoftKeyboard() {
        if(getCurrentFocus()!=null) {
            InputMethodManager inputMethodManager = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
            inputMethodManager.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), 0);
            getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);
        }
    }

    public void triggerScan(View view) {
        cameraPreview.decodeSingle(callback);
    }

    public static class BottomNavigationViewHelper {
        public static void disableShiftMode(BottomNavigationView view) {
            BottomNavigationMenuView menuView = (BottomNavigationMenuView) view.getChildAt(0);
            try {
                Field shiftingMode = menuView.getClass().getDeclaredField("mShiftingMode");
                shiftingMode.setAccessible(true);
                shiftingMode.setBoolean(menuView, false);
                shiftingMode.setAccessible(false);
                for (int i = 0; i < menuView.getChildCount(); i++) {
                    BottomNavigationItemView item = (BottomNavigationItemView) menuView.getChildAt(i);
                    //noinspection RestrictedApi
                    item.setShiftingMode(false);
                    // set once again checked value, so view will be updated
                    //noinspection RestrictedApi
                    item.setChecked(item.getItemData().isChecked());
                }
            } catch (NoSuchFieldException e) {
                Log.e("BNVHelper", "Unable to get shift mode field", e);
            } catch (IllegalAccessException e) {
                Log.e("BNVHelper", "Unable to change value of shift mode", e);
            }
        }
    }

}

