package com.ktrlabs.thapelo.iwallet;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import java.net.URL;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.provider.ContactsContract;
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
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.Switch;
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
import java.net.HttpURLConnection;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.concurrent.ExecutionException;

import io.realm.OrderedRealmCollection;
import io.realm.Realm;
import io.realm.RealmBaseAdapter;
import io.realm.RealmConfiguration;
import io.realm.RealmResults;
import io.realm.Sort;
import mehdi.sakout.dynamicbox.DynamicBox;

import static com.ktrlabs.thapelo.iwallet.QuickReference.bitmapToString;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener, DatePickerDialog.OnDateSetListener {

    Boolean internet = true;
    AsyncTask sum;
    Menu optionsMenu;
    View viewb,views, viewh, viewr,viewc = null;
    DecoratedBarcodeView cameraPreview;
    private CaptureManager capture;
    public final static int QRcodeWidth = 250;
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
    private String daytextSet = "";

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

        Realm.init(this);
        //Realm realm = Realm.getDefaultInstance();
        RealmConfiguration config = new RealmConfiguration
                .Builder()
                .deleteRealmIfMigrationNeeded()
                .build();
        if (!hasFlash()) {
            findViewById(R.id.flash_switch).setEnabled(false);
        }

        ((Switch)findViewById(R.id.flash_switch)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (((Switch)findViewById(R.id.flash_switch)).isChecked()) {
                    cameraPreview.setTorchOn();
                } else {
                    cameraPreview.setTorchOff();
                }
            }
        });

        Realm realm = Realm.getInstance(config);

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

        ((TextView) findViewById(R.id.balamt)).setText(format.format(Float.parseFloat(new BankAi().getStokedKey(getApplicationContext(), "AvailableBalance"))));


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

                if (true) {
                    new BankLoginAsync().execute();
                } else {
                    Toast.makeText(this, "No Internet Connection. Please check the connection and try again.", Toast.LENGTH_LONG).show();
                }
        }

        ((ImageButton) findViewById(R.id.receive_clear)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ((TextView) findViewById(R.id.receive_amount)).setText("0");
            }
        });

            if (true) {
                 new getTransactionList().execute();
                new refreshAccountDetails().execute();
            } else {
                Toast.makeText(this, "No Internet Connection. Please check the connection and try again.", Toast.LENGTH_SHORT).show();
            }


        RealmResults<Transaction> realmResults= realm.where(Transaction.class).findAllSortedAsync("id", Sort.DESCENDING);

        transactionsAdapter = new TransactionsAdapter(realmResults);

        transactionsList = (ListView) findViewById(R.id.transactions_listview);
        DynamicBox transactionSpinner = new DynamicBox(this,transactionsList);
        transactionSpinner.showLoadingLayout();
        transactionsList.setAdapter(transactionsAdapter);
        transactionSpinner.hideAll();


        capture = new CaptureManager(this, cameraPreview);
        capture.initializeFromIntent(getIntent(), savedInstanceState);
        cameraPreview.decodeSingle(callback);


        //TextView tView = (TextView) findViewById(R.id.account_number);
        //tView.setText((new BankAi().getStokedKey(getApplicationContext(), "AccountNumber")));


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
        ((TextView) findViewById(R.id.balacc)).setText((new BankAi().getStokedKey(getApplicationContext(), "AccountNumber")));
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

                    // new MainActivity.getTransactionList().execute();
                 //   new MainActivity.refreshAccountDetails().execute();
                 //   new MainActivity.BankLoginAsync().execute();

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
            TextView balance = (TextView) findViewById(R.id.balance);
            TextView balamt = (TextView) findViewById(R.id.balamt);
            if (balance!=null && balamt!=null) {
                if (new BankAi().getStokedKey(getApplicationContext(), "AvailableBalance") != "failed") {
                    balance.setText(format.format(Float.parseFloat(new BankAi().getStokedKey(getApplicationContext(), "AvailableBalance"))));
                    balamt.setText(format.format(Float.parseFloat(new BankAi().getStokedKey(getApplicationContext(), "AvailableBalance"))));
                } else {
                    ((TextView) findViewById(R.id.balance)).setText("Failed");
                    ((TextView) findViewById(R.id.balamt)).setText("Failed");
                }
            }
        }
    }


    public class getTransactionList extends AsyncTask<String, String, RealmResults> {
        @Override
        protected RealmResults doInBackground(String... params) {
            JSONArray jArray = null;
            RealmResults<Transaction> realmResults = null;
            try {
                jArray = new BankAi().TransactionList(new BankAi().getStokedKey(getApplicationContext(), "x-auth"), new BankAi().getStokedKey(getApplicationContext(), "AccountNumber"));
            } catch (IOException e) {
                e.printStackTrace();
            }
            ArrayList<JSONObject> items = new ArrayList<JSONObject>();
            if (jArray == null) {
                return null;
            } else {
                for (int i = 0; i < jArray.length(); i++) {
                    JSONObject json_data = null;
                    try {
                        json_data = jArray.getJSONObject(i);
                        String id = json_data.getString("ID");
                        JSONObject receiver = json_data.getJSONObject("Receiver");
                        JSONObject sender = json_data.getJSONObject("Sender");
                        String sndName = sender.getString("Name");
                        String rcvName = receiver.getString("Name");
                        String date = json_data.getString("Timestamp");
                        String amount = json_data.getString("Amount");
                        String fee = json_data.getString("Fee");
                        String desc = json_data.getString("Desc");
                        String status = json_data.getString("Status");
                        Float amt = Float.parseFloat(amount);
                        String name = null;
                        if (amt < 0) {
                            name = rcvName;
                        } else {
                            name = sndName;
                        }
                        //int id=json_data.getInt("id");
                        JSONObject jObj = new JSONObject();
                        jObj.put("id",id);
                        jObj.put("name", name);
                        jObj.put("date", date);
                        jObj.put("amount", amount);
                        jObj.put("fee", fee);
                        jObj.put("type",desc);
                        jObj.put("status",status);
                        RealmConfiguration config = new RealmConfiguration
                                .Builder()
                                .deleteRealmIfMigrationNeeded()
                                .build();
                        Realm realm = Realm.getInstance(config);
                        try {
                            // Work with Realm
                            realm.beginTransaction();
                            realm.createOrUpdateObjectFromJson(Transaction.class,jObj);
                            realm.commitTransaction();

                        } finally {
                            realm.close();
                        }
                        items.add(jObj);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            }

            return realmResults;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            
        }

        @Override
        protected void onPostExecute(RealmResults strings) {
            super.onPostExecute(strings);
            //stringArrayList.addAll(strings);
            RealmConfiguration config = new RealmConfiguration
                    .Builder()
                    .deleteRealmIfMigrationNeeded()
                    .build();
            Realm realm = Realm.getInstance(config);
            try {
                // Work with Realm
               realm.beginTransaction();
                RealmResults realmResults = realm.where(Transaction.class).findAllSorted("id",Sort.DESCENDING);
                //transactionsAdapter.updateData(realmResults);
                if (realmResults.isEmpty()) {
                    LinearLayout linearLayout = (LinearLayout) findViewById(R.id.home_transactions);
                    TextView textView = new TextView(getApplicationContext());
                    textView.setTextColor(Color.GRAY);
                    textView.setTextSize(14);
                    textView.setPadding(18, 18, 18, 18);
                    textView.setText("No Transactions ( or Network Connection Error )");
                    linearLayout.addView(textView);

                }
                realm.commitTransaction();

            } finally {
                realm.close();
            }
          // transactionsAdapter.updateData(strings);

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

                                if (true) {
                                    new getAccWithID().execute(jsonObject);
                                } else {
                                    Toast.makeText(this, "No Internet Connection. Please check the connection and try again.", Toast.LENGTH_SHORT).show();
                                }

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

    public class TransactionsAdapter extends RealmBaseAdapter<Transaction> implements ListAdapter {
        public TransactionsAdapter(@Nullable OrderedRealmCollection<Transaction> data) {
            super(data);
        }

        public class ViewHolder {
            CircularImageView propic;
            TextView fee;
            TextView type;
            TextView name;
            TextView amount;
            TextView time;
            TextView status;
            TextView dayText;
            TextView no_transact;
        }

        @NonNull
        @Override
        public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
            //return super.getView(position, convertView, parent);
            Log.d("from atrray list", getItem(position).toString());
            Transaction jsonObject = getItem(position);
            ViewHolder holder;
            if (convertView == null) {
                LayoutInflater vi = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                convertView = vi.inflate(R.layout.transactions_listview_item, null);
                holder = new ViewHolder();
                holder.fee = (TextView) convertView.findViewById(R.id.fee);
                holder.type = (TextView) convertView.findViewById(R.id.type);
                holder.propic = (CircularImageView) convertView.findViewById(R.id.profile_image);
                holder.name = (TextView) convertView.findViewById(R.id.name);
                holder.time = (TextView) convertView.findViewById(R.id.time);
                holder.amount = (TextView) convertView.findViewById(R.id.amount);
                holder.status = (TextView) convertView.findViewById(R.id.status);
                holder.dayText = (TextView) convertView.findViewById(R.id.day_text);
                holder.no_transact = (TextView) convertView.findViewById(R.id.no_transactions);


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
                    if (jsonObject.getName().length()==0) {
                        holder.name.setText("iWallet Merchant");
                    } else {
                        holder.name.setText(jsonObject.getName());
                    }
                }
                if (holder.time != null) {
                    holder.time.setText(getTime( jsonObject.getDate() ));
                }
                if (holder.dayText!=null) {
                    String formattedDate = new SimpleDateFormat("dd MMM yyyy").format(Calendar.getInstance().getTime());
                    String yesterdayDate =  Integer.parseInt(formattedDate.substring(0,2))-1 + formattedDate.substring(2);
                    if (jsonObject.getDate()!=null) {
                        if (!daytextSet.contentEquals("") && daytextSet.contentEquals(getDate(jsonObject.getDate()))) {
                            daytextSet = getDate(jsonObject.getDate());
                            holder.dayText.setVisibility(View.GONE);
                        } else {
                            if (formattedDate.contentEquals(getDate(jsonObject.getDate()))) {
                                holder.dayText.setText("Today");
                            } else if (yesterdayDate.contentEquals(getDate(jsonObject.getDate()))){
                                holder.dayText.setText("Yesterday");
                            } else {
                                holder.dayText.setText(getDate(jsonObject.getDate()));
                            }
                            holder.dayText.setVisibility(View.VISIBLE);
                            daytextSet = getDate(jsonObject.getDate());
                        }
                    }
                }
             /*    if (holder.no_transact!=null) {
                   // conditions for showing transaction days
                    Log.d("Which day?",daytextSet);
                    if (daytextSet == "" || daytextSet == "todayagain"){
                        if (daytextSet=="") {
                            holder.dayText.setVisibility(View.VISIBLE);
                            holder.dayText.setText("Today");
                        }
                        // If today
                        if (System.currentTimeMillis() - jsonObject.getDate() < 86400000) {
                            holder.no_transact.setVisibility(View.GONE);
                            daytextSet = "todayagain";
                        } else {
                            // if yesterday
                            if (daytextSet=="todayagain") {
                                holder.no_transact.setVisibility(View.GONE);
                            } else {
                                holder.no_transact.setVisibility(View.VISIBLE);
                            }
                            daytextSet = "today";
                        }
                    } else if ( (daytextSet=="today" || daytextSet == "yesterdayagain") && daytextSet!="" && daytextSet!="todayagain") {
                        if (daytextSet=="today") {
                            holder.dayText.setText("Yesterday");
                            holder.dayText.setVisibility(View.VISIBLE);
                        }
                        if ( System.currentTimeMillis() - jsonObject.getDate() > 86400000 && System.currentTimeMillis() - jsonObject.getDate() < (2*86400000) ) {
                            holder.no_transact.setVisibility(View.GONE);
                            daytextSet ="yesterdayagain";
                        } else {
                            holder.no_transact.setVisibility(View.VISIBLE);
                            daytextSet = "yesterday";
                        }
                    } else if ( jsonObject.getName()!=null && daytextSet=="yesterday" && daytextSet!="" && daytextSet!="today" && daytextSet!="todayagain" && daytextSet!="yesterdayagain" ) {
                            holder.dayText.setText("All");
                            holder.dayText.setVisibility(View.VISIBLE);
                            holder.no_transact.setVisibility(View.GONE);
                            daytextSet = "All";
                    }
                } */
                if (holder.amount != null) {
                    if (Double.parseDouble(jsonObject.getAmount())<0) {
                        holder.amount.setTextColor(Color.RED);
                    } else {
                        holder.amount.setTextColor(Color.GREEN);
                    }
                    holder.amount.setText( format.format(Float.parseFloat( jsonObject.getAmount() ) ));
                }
                if (holder.fee != null) {
                    holder.fee.setText(format.format(Float.parseFloat( jsonObject.getFee() ) ) + " fee");
                }
                if (holder.type != null) {
                    holder.type.setText(jsonObject.getType() );
                }

                if (holder.status != null) {
                    holder.status.setText(jsonObject.getStatus().substring(0,1).toUpperCase() + jsonObject.getStatus().substring(1));
                }
            } catch (Exception e) {
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
            if (balance==null) {
                // Do nothing
            } else if (new BankAi().getStokedKey(getApplicationContext(), "AvailableBalance")=="failed") {
                balance.setTitle("Failed");
            }else {
                balance.setTitle(format.format(Float.parseFloat(  new BankAi().getStokedKey(getApplicationContext(), "AvailableBalance")  ) ));
            }
        }
    }

    public void getContact(View view) {
        Intent intent = new Intent(Intent.ACTION_PICK, ContactsContract.Contacts.CONTENT_URI);
        startActivityForResult(intent, 1);
    }

    private String getDateTime(long timeStampStr) {

        try {
            String date = new SimpleDateFormat("HH:mm dd.MM.yyyy").format(new Date(timeStampStr * 1000));
            return date;
        } catch (Exception ex) {
            ex.printStackTrace();
            return Long.toString(timeStampStr);
        }
    }

    private String getTime(long timeStampStr) {

        try {
            String date = new SimpleDateFormat("HH:mm").format(new Date(timeStampStr * 1000));
            return date;
        } catch (Exception ex) {
            ex.printStackTrace();
            return Long.toString(timeStampStr);
        }
    }

    private String getDate(long timeStampStr) {

        try {
            String date = new SimpleDateFormat("dd MMM yyyy").format(new Date(timeStampStr * 1000));
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
        optionsMenu = menu;
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
            Intent intent = new Intent(this, Deposit.class);
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

    public class hasActiveInternetConnection extends AsyncTask<String,String,Boolean>{

        @Override
        protected Boolean doInBackground(String... params) {
            if (isNetworkAvailable()) {
                try {
                    HttpURLConnection urlc = (HttpURLConnection) (new URL("http://clients3.google.com/generate_204").openConnection());
                    urlc.setRequestProperty("User-Agent", "Test");
                    urlc.setRequestProperty("Connection", "close");
                    urlc.setConnectTimeout(1500);
                    urlc.connect();
                    return (urlc.getResponseCode() == 204 && urlc.getContentLength() == 0);
                } catch (IOException e) {
                    Log.e("Internet?", "Error checking internet connection", e);
                }
            } else {
                Log.d("Network/Internet?", "No network available!");
            }
            return false;
        }

        @Override
        protected void onPostExecute(Boolean aBoolean) {
            super.onPostExecute(aBoolean);
            internet = aBoolean;
        }
    }

    private boolean isNetworkAvailable() {
        ConnectivityManager connectivityManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null;
    }


    @Override
    protected void onResume() {
        super.onResume();
        capture.onResume();
      if (new BankAi().getStokedKey(getApplicationContext(),"idNumber")!="failed" && new BankAi().getStokedKey(getApplicationContext(),"password")!="failed") {
            Log.d("From Resume","it will try to login");
                if (true) {
                   // new BankLoginAsync().execute();
                  //  new refreshAccountDetails().execute();
                } else {
                    Toast.makeText(this, "No Internet Connection. Please check the connection and try again.", Toast.LENGTH_LONG).show();
                }

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
                if (true) {
                    new getTransactionList().execute();
                    new refreshAccountDetails().execute();
                }
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

    public void gotoProfile(View view) {
        Intent intent = new Intent(this, Account.class);
        startActivity(intent);
    }

    public void goToGeoPay(View view)
    {
        Intent intent = new Intent(this, EasyPayActivity.class);
        startActivity(intent);
    }

    public void goToWithdraw(View view)
    {
        Intent intent = new Intent(this, WithdrawActivity.class);
        startActivity(intent);
    }

    public void goToTransfer(View view)
    {
        Intent intent = new Intent(this, TransferActivity.class);
        startActivity(intent);
    }

    public void goToContacts(View view)
    {
        Intent intent = new Intent(this, ContactsActivtity.class);
        startActivity(intent);
    }

    public void goToQrPay(View view)
    {
        Intent intent = new Intent(this, QuickPayActivity.class);
        startActivity(intent);
    }

    public void goToQrReceive(View view)
    {
        Intent intent = new Intent(this, ReceiveActivity.class);
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

    private boolean hasFlash() {
        return getApplicationContext().getPackageManager()
                .hasSystemFeature(PackageManager.FEATURE_CAMERA_FLASH);
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

