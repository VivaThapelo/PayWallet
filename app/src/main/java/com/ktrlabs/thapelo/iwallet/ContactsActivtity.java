package com.ktrlabs.thapelo.iwallet;

import android.content.Context;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.widget.CursorAdapter;
import android.support.v4.widget.SimpleCursorAdapter;
import android.support.v4.widget.TextViewCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.TextView;

import com.mikhaellopez.circularimageview.CircularImageView;

import java.util.concurrent.ExecutionException;

public class ContactsActivtity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_contacts_activtity);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        Cursor cur = getContacts();

        ListView lv = (ListView) findViewById(R.id.contactsListview);

        ContactsAdapter adapter = new ContactsAdapter(getApplicationContext(),cur);
        lv.setAdapter(adapter);
    }

    public class ContactsAdapter extends CursorAdapter {
        public ContactsAdapter(Context context, Cursor c) {
            super(context, c);
        }


        public class ViewHolder {
            CircularImageView circularImageView;
            TextView names;
            TextView number;
        }

        @Override
        public View newView(Context context, Cursor cursor, ViewGroup parent) {
            View view = null;
            ViewHolder viewHolder;
            Object cItem =  (cursor.getPosition());

            if (view==null) {
                LayoutInflater vi = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                view = vi.inflate(R.layout.contacts_listview_item, null);
                viewHolder = new ViewHolder();
                viewHolder.circularImageView = (CircularImageView) view.findViewById(R.id.contact_image);
                viewHolder.names = (TextView) view.findViewById(R.id.contact_names);
                viewHolder.number = (TextView) view.findViewById(R.id.contact_account);
                view.setTag(viewHolder);
            } else {
                viewHolder = (ViewHolder) view.getTag();
            }

            try {
                if (viewHolder.circularImageView!=null) {
                    // viewHolder.circularImageView.setImageBitmap(null);
                }
                if(viewHolder.names!=null) {
                    viewHolder.names.setText(cursor.getColumnIndex(ContactsContract.Data.DISPLAY_NAME));
                }
                if (viewHolder.number!=null) {
                    viewHolder.number.setText(ContactsContract.Data.CONTACT_ID);
                }

            } catch (Exception e) {
                e.printStackTrace();
            }
            return view;
        }

        @Override
        public void bindView(View view, Context context, Cursor cursor) {

        }
    }

    private Cursor getContacts() {
        // Run query
        Uri uri = ContactsContract.Contacts.CONTENT_URI;

        String[] projection = new String[]{ ContactsContract.Contacts._ID, ContactsContract.Contacts.DISPLAY_NAME };
        String selection = null;
        String[] selectionArgs = null;
        String sortOrder = ContactsContract.Contacts.DISPLAY_NAME +
                " COLLATE LOCALIZED ASC";
        return managedQuery(uri, projection, selection, selectionArgs, sortOrder);
    }

    public class getContactList extends AsyncTask<String,String,Cursor> {

        @Override
        protected Cursor doInBackground(String... params) {
            return getContacts();
        }
    }

}
