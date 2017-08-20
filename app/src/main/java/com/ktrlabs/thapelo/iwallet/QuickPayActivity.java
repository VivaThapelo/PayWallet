package com.ktrlabs.thapelo.iwallet;

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
import android.widget.ImageView;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.MultiFormatWriter;
import com.google.zxing.WriterException;
import com.google.zxing.common.BitMatrix;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.security.Timestamp;
import java.sql.Time;
import java.util.Arrays;
import java.util.Calendar;

public class QuickPayActivity extends AppCompatActivity {
    public final static int QRcodeWidth = 250;
    private int seconds;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_qr_pay);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

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
                jObj.put("type", "Payment");
                Calendar c = Calendar.getInstance();
                seconds = c.get(Calendar.SECOND);
                // payment must go through within 30 seconds @ (till = seconds - 30) verification
                jObj.put("security", seconds);
                // items.add(jObj);
            } catch (JSONException e) {
                e.printStackTrace();
            }
            Log.d("data", jObj.toString());
            String RecipientDetails = jObj.toString();
            new getQrCode().execute(RecipientDetails);
            new getBarCode().execute("618704559321");
        }

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
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

    public class getBarCode extends AsyncTask<String, String, Bitmap> {
        @Override
        protected Bitmap doInBackground(String... params) {
            Bitmap bitmap = null;
            try {
                Log.d("data", params[0]);
                bitmap = getBar(params[0]);
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
            ((ImageView) findViewById(R.id.barq_receive)).setImageBitmap(s);
        }
    }

    private Bitmap getBar(String data) throws WriterException {
        int width = 300;
        int height = 100;
        MultiFormatWriter writer = new MultiFormatWriter();
        String finalData = Uri.encode(data);

        // Use 1 as the height of the matrix as this is a 1D Barcode.
        BitMatrix bm = writer.encode(finalData, BarcodeFormat.CODE_128, width, 1);
        int bmWidth = bm.getWidth();

        Bitmap imageBitmap = Bitmap.createBitmap(bmWidth, height, Bitmap.Config.ARGB_8888);

        for (int i = 0; i < bmWidth; i++) {
            // Paint columns of width 1
            int[] column = new int[height];
            Arrays.fill(column, bm.get(i, 0) ? Color.BLACK : Color.WHITE);
            imageBitmap.setPixels(column, 0, 1, i, 0, 1, height);
        }

        return imageBitmap;
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
}
