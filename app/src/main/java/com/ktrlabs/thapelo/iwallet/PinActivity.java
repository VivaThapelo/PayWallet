package com.ktrlabs.thapelo.iwallet;

import android.content.Intent;
import android.os.Build;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class PinActivity extends AppCompatActivity {
    EditText pin_first, pin_second,pin_third,pin_forth;
    String from;
    FirebaseDatabase database;
    DatabaseReference myRef;
    String remotePin;
    TextView PinPageText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pin);

        PinPageText = (TextView) findViewById(R.id.pin_page_text);

        hideSoftKeyboard();

        pin_first = (EditText) findViewById(R.id.pin_first);
        pin_second = (EditText) findViewById(R.id.pin_second);
        pin_third = (EditText) findViewById(R.id.pin_third);
        pin_forth = (EditText) findViewById(R.id.pin_forth);

       // pin_first.requestFocus();

      /*  pin_first.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                pin_second.requestFocus();
            }
        });
        pin_second.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                pin_third.requestFocus();
            }
        });
        pin_third.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                pin_forth.requestFocus();
            }
        });
        pin_forth.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                if (getIntent().getStringExtra("from").equals("login")) {
                    verifyPin();
                } else if (getIntent().getStringExtra("from").equals("registration") ) {
                    setPin();
                } else if (getIntent().getStringExtra("from").equals("splash")) {
                    verifyPin();
                } else  if (getIntent().getStringExtra("from").equals("payment")) {
                    verifyPin();
                }
            }
        }); */

        database = FirebaseDatabase.getInstance();
        myRef = database.getReference("users");

        if (getIntent().getStringExtra("from").equals("login")) {
            PinPageText.setText("ENTER PIN");
        } else if(getIntent().getStringExtra("from").equals("registration")) {
            PinPageText.setText("CREATE PIN");
        } else if(getIntent().getStringExtra("from").equals("splash")){
            PinPageText.setText("ENTER PIN");
        } else if (getIntent().getStringExtra("from").equals("payment")) {
            PinPageText.setText("ENTER PIN");
        }

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


    public void pinCode(View view) {
        String key = ((Button) view).getText().toString();
        Integer[] numbers = {0,1,2,3,4,5,6,7,8,9};


            if(pin_first.getText().toString().isEmpty() || pin_first.getText().toString()=="") {
                Log.d("PinPage","Clicked button:"+ key);
                pin_first.setText(key);
                pin_second.requestFocus();
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                    pin_second.setShowSoftInputOnFocus(false);
                }
            } else
            if (pin_second.getText().toString().isEmpty() || pin_second.getText().toString()=="") {
                pin_second.setText(key);
                pin_third.requestFocus();
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                    pin_third.setShowSoftInputOnFocus(false);
                }
            } else
            if (pin_third.getText().toString().isEmpty() || pin_third.getText().toString()=="") {
                pin_third.setText(key);
                pin_forth.requestFocus();
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                    pin_forth.setShowSoftInputOnFocus(false);
                }
            } else
            if (pin_forth.getText().toString().isEmpty() || pin_forth.getText().toString()==""){
                pin_forth.setText(key);
                if (getIntent().getStringExtra("from").equals("login")) {
                    verifyPin();
                } else if (getIntent().getStringExtra("from").equals("registration") ) {
                    setPin();
                } else if (getIntent().getStringExtra("from").equals("splash")) {
                    verifyPin();
                } else  if (getIntent().getStringExtra("from").equals("payment")) {
                    verifyPin();
                }
            }


    }

    public void clearCode(View view) {
        Log.v("PinPage","This is not a number");
        pin_first.getText().clear();
        pin_second.getText().clear();
        pin_third.getText().clear();
        pin_forth.getText().clear();
        pin_first.requestFocus();
       // pin_first.setShowSoftInputOnFocus(false);
    }

    String pin1,pinCode1,pin2;
    public void setPin() {
        PinPageText.setText("Confirm PIN");
        if (pinCode1==null) {
            pin1 = pin_first.getText().toString() + pin_second.getText().toString() + pin_third.getText().toString() + pin_forth.getText().toString();
            pinCode1 = new String(pin1);
            pin_first.getText().clear();
            pin_second.getText().clear();
            pin_third.getText().clear();
            pin_forth.getText().clear();
            pin_first.requestFocus();
         //   pin_first.setShowSoftInputOnFocus(false);
        }
        if (pinCode1!=null){
            pin2 = pin_first.getText().toString() + pin_second.getText().toString() + pin_third.getText().toString() + pin_forth.getText().toString();
            if (pinCode1.equals(pin2)) {
                myRef.child(new BankAi().getStokedKey(getApplicationContext(), "Cellphone")).child("Pin").setValue(pin1);
                Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                startActivity(intent);
                finish();
            }
        }

    }

    public boolean verifyPin(){
        String pin = new String(pin_first.getText().toString() + pin_second.getText().toString() + pin_third.getText().toString() + pin_forth.getText().toString());
        myRef.child(new BankAi().getStokedKey(getApplicationContext(), "Cellphone")).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
           //     Log.d("remotePIN", "Value is: " + remotePin.toString());
             //   remotePin =  dataSnapshot.getValue(String.class);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.w("PinActivity-Firebase", "Failed to read value.", databaseError.toException());
            }
        });
        remotePin = "4161";
        if (pin.equals(remotePin)) {
            Log.d("Pin",pin);
            Log.d("remotePin",remotePin);
            if (!(getIntent().getStringExtra("from").equals("payment"))) {
                Intent intent = new Intent(this, MainActivity.class);
                startActivity(intent);
            }
            finish();
            return true;
        } else {
            pin_first.getText().clear();
            pin_second.getText().clear();
            pin_third.getText().clear();
            pin_forth.getText().clear();
            Toast.makeText(getApplicationContext(), "Pin Failed, Try Again", Toast.LENGTH_LONG).show();
            pin_first.requestFocus();
          //  pin_first.setShowSoftInputOnFocus(false);
            return false;
        }
    }

    public void hideSoftKeyboard() {
        if(getCurrentFocus()!=null) {
            InputMethodManager inputMethodManager = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
            inputMethodManager.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), 0);
            getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);
        }
    }
}
