package com.sizzling.apps.fakesms;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.ActivityNotFoundException;
import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.provider.Telephony;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TimePicker;
import android.widget.Toast;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.InterstitialAd;
import com.google.android.gms.ads.NativeExpressAdView;

import java.util.Calendar;

public class FakesmsActivity extends AppCompatActivity {
    final int SELECT_PHONE_NUMBER = 10;
    /**
     * Called when the activity is first created.
     */
    boolean adShown = false;
    String phoneNumber;
    String message;
    String time;
    String folderName;
    String defaultSmsApp;
    Button timePickBtn, datePickBtn;
    TimePicker tp;
    DatePicker dp;
    EditText tnum;
    private String array_spinner[];
    private AdView mAdView;
    NativeExpressAdView nativeAdView;
    static boolean testingMode = false;
    InterstitialAd mInterstitialAd;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            defaultSmsApp = Telephony.Sms.getDefaultSmsPackage(FakesmsActivity.this);
        }
        AdRequest adRequest;
        mAdView = (AdView) findViewById(R.id.bannerAd);
        if (testingMode) {
            adRequest= new AdRequest.Builder().addTestDevice("55757F6B6D6116FAC42122EC92E5A58C").build();
        } else {
            adRequest= new AdRequest.Builder().build();
        }

        mAdView.loadAd(adRequest);
        nativeAdView = (NativeExpressAdView)findViewById(R.id.nativeAdView);
        AdRequest request;
        if (testingMode) {
            request= new AdRequest.Builder().addTestDevice("55757F6B6D6116FAC42122EC92E5A58C").build();
        } else {
            request= new AdRequest.Builder().build();
        }
        nativeAdView.loadAd(request);


        mInterstitialAd = new InterstitialAd(this);
        if(testingMode){
            mInterstitialAd.setAdUnitId("ca-app-pub-3940256099942544/1033173712"); // Test Ad Id
        } else{
            mInterstitialAd.setAdUnitId("ca-app-pub-6557142062270167/9949615949");
        }
        requestNewInterstitial();
        // Here come all the options that you wish to show depending on the
        // size of the array.
        array_spinner = new String[5];
        array_spinner[0] = "Inbox";
        array_spinner[1] = "Sent";
        array_spinner[2] = "Draft";
        array_spinner[3] = "Outbox";
        array_spinner[4] = "Failed";
        final Spinner s = (Spinner) findViewById(R.id.spinner1);
        ArrayAdapter adapter = new ArrayAdapter(this,
                android.R.layout.simple_spinner_dropdown_item, array_spinner);
//        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        s.setAdapter(adapter);
        timePickBtn = findViewById(R.id.timePickBtn);
        datePickBtn = findViewById(R.id.datePickBtn);
        Calendar cal = Calendar.getInstance();
        String hh = "";
        String am = "";
        if (cal.get(Calendar.HOUR_OF_DAY) > 12) {
            hh = (cal.get(Calendar.HOUR_OF_DAY) - 12) + "";
            am = "PM";
        } else {
            hh = (cal.get(Calendar.HOUR_OF_DAY)) + "";
            am = "AM";
        }
        if(hh.length()==1)
            hh = "0"+hh;
        String mm = cal.get(Calendar.MINUTE)+"";
        if(mm.length()==1)
            mm="0"+mm;
        timePickBtn.setText(hh + " : " + mm + " " + am);
        final EditText tbody;


        Button save, reset;

        tnum = (EditText) findViewById(R.id.tnum);
        tbody = (EditText) findViewById(R.id.tbody);
        save = (Button) findViewById(R.id.save);
        reset = (Button) findViewById(R.id.reset);


        datePickBtn.setText((cal.get(Calendar.DAY_OF_MONTH) < 10 ? "0" + cal.get(Calendar.DAY_OF_MONTH) : cal.get(Calendar.DAY_OF_MONTH)) + "-" + ((cal.get(Calendar.MONTH) + 1) < 10 ? "0" + (cal.get(Calendar.MONTH) + 1) : (cal.get(Calendar.MONTH) + 1)) + "" + "-" + cal.get(Calendar.YEAR) + "");
        save.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                phoneNumber = tnum.getText().toString();
                message = tbody.getText().toString();
                if (phoneNumber.equals("")) {
                    Toast.makeText(FakesmsActivity.this, "Please Provide Sender's Information", Toast.LENGTH_SHORT).show();
                    tnum.requestFocus();
                    return;
                }
                if (message.equals("")) {
                    Toast.makeText(FakesmsActivity.this, "Please Provide a Message", Toast.LENGTH_SHORT).show();
                    tbody.requestFocus();
                    return;
                }
                Calendar cal = Calendar.getInstance();
                if (dp != null)
                    cal.set(dp.getYear(), dp.getMonth(), dp.getDayOfMonth());
                if (tp != null) {
                    cal.set(Calendar.HOUR_OF_DAY, tp.getCurrentHour());
                    cal.set(Calendar.MINUTE, tp.getCurrentMinute());
                    cal.set(Calendar.SECOND, 0);
                }
                time = cal.getTimeInMillis() + "";
                folderName = array_spinner[s.getSelectedItemPosition()].toLowerCase();
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
                    final String myPackageName = getPackageName();
                    if (!Telephony.Sms.getDefaultSmsPackage(FakesmsActivity.this).equals(myPackageName)) {

                        Intent intent = new Intent(Telephony.Sms.Intents.ACTION_CHANGE_DEFAULT);
                        intent.putExtra(Telephony.Sms.Intents.EXTRA_PACKAGE_NAME, myPackageName);
                        startActivityForResult(intent, 1);
                    } else {
                        saveSms();
                    }
                } else {
                    saveSms();
                }
            }
        });

        reset.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                tnum.setText("");
                tbody.setText("");
            }
        });


    }
    public void requestNewInterstitial() {
        AdRequest adRequest = null;
        if(testingMode){
            adRequest= new AdRequest.Builder()
                    .addTestDevice("55757F6B6D6116FAC42122EC92E5A58C")
                    .build();
        } else {
            adRequest = new AdRequest.Builder()
                    .build();
        }
        mInterstitialAd.loadAd(adRequest);
    }

    public boolean saveSms() {
        boolean ret = false;
        try {
            ContentValues values = new ContentValues();
            values.put("address", phoneNumber);
            values.put("body", message);
            values.put("read", "0"); //"0" for have not read sms and "1" for have read sms
            values.put("date", time);

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
                Uri uri = Telephony.Sms.Sent.CONTENT_URI;
                if (folderName.equals("inbox")) {
                    uri = Telephony.Sms.Inbox.CONTENT_URI;
                }
                if (getContentResolver().insert(uri, values) != null) {
                    Toast.makeText(getBaseContext(), "Successfully Faked!",
                            Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(getBaseContext(), "Unsuccesful!",
                            Toast.LENGTH_SHORT).show();
                }
            } else {
                if (getContentResolver().insert(Uri.parse("content://sms/" + folderName), values) != null) {
                    Toast.makeText(getBaseContext(), "Successfully Faked!",
                            Toast.LENGTH_SHORT).show();
                    //for a bug
                    getContentResolver().delete(Uri.parse("content://sms/conversations/-1"), null, null);
                } else
                    Toast.makeText(getBaseContext(), "Unsuccesful!",
                            Toast.LENGTH_SHORT).show();
            }

            ret = true;
        } catch (Exception ex) {
            ex.printStackTrace();
            ret = false;
        }
        if(mInterstitialAd!=null && mInterstitialAd.isLoaded())
            mInterstitialAd.show();
        else
            requestNewInterstitial();
        return ret;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {


        if (requestCode == 1) {
            if (resultCode == RESULT_OK) {

                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
                    final String myPackageName = getPackageName();
                    if (Telephony.Sms.getDefaultSmsPackage(this).equals(myPackageName)) {

                        //Write to the default sms app
                        saveSms();
                        Intent intent = new Intent(Telephony.Sms.Intents.ACTION_CHANGE_DEFAULT);
                        intent.putExtra(Telephony.Sms.Intents.EXTRA_PACKAGE_NAME, defaultSmsApp);
                        startActivityForResult(intent, 2);
                    }
                }
            }
        }
        if (requestCode == SELECT_PHONE_NUMBER && resultCode == RESULT_OK) {
            // Get the URI and query the content provider for the phone number
            Uri contactUri = data.getData();
            String[] projection = new String[]{ContactsContract.CommonDataKinds.Phone.NUMBER};
            Cursor cursor = getContentResolver().query(contactUri, projection,
                    null, null, null);

            // If the cursor returned is valid, get the phone number
            if (cursor != null && cursor.moveToFirst()) {
                int numberIndex = cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER);
                String number = cursor.getString(numberIndex);
                tnum.setText(number);
            }

            cursor.close();
        }
    }


    public void showTimePicker(final View view) {
        if(!adShown) {
            if(mInterstitialAd!=null && mInterstitialAd.isLoaded()) {
                mInterstitialAd.show();
                return;
            }
            else {
                requestNewInterstitial();
            }
        }
        adShown=false;
        Calendar currentTime = Calendar.getInstance();
        int hour = currentTime.get(Calendar.HOUR_OF_DAY);
        int minute = currentTime.get(Calendar.MINUTE);

        TimePickerDialog.OnTimeSetListener onTimeSetListener = new TimePickerDialog.OnTimeSetListener() {
            @Override
            public void onTimeSet(TimePicker timePicker, int i, int i1) {
                tp = timePicker;
                String hh = "";
                String am = "";
                if (tp.getCurrentHour() > 12) {
                    hh = (tp.getCurrentHour() - 12) + "";
                    am = "PM";
                } else {
                    hh = tp.getCurrentHour() + "";
                    am = "AM";
                }
                if(hh.length()==1)
                    hh="0"+hh;
                timePickBtn.setText(hh + " : " + (tp.getCurrentMinute() < 10 ? "0" : "") + tp.getCurrentMinute() + " " + am);
            }
        };
        TimePickerDialog timePickerDialog = new TimePickerDialog(this, onTimeSetListener, hour, minute, false);
        timePickerDialog.setTitle("Pick SMS Time");
        timePickerDialog.show();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.navigation, menu);

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_rate) {
            rateApp();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    void rateApp() {
        Uri uri = Uri.parse("market://details?id=" + getPackageName());
        Intent myAppLinkToMarket = new Intent(Intent.ACTION_VIEW, uri);
        try {
            startActivity(myAppLinkToMarket);
        } catch (ActivityNotFoundException e) {
            Toast.makeText(this, " unable to find market app", Toast.LENGTH_LONG).show();
        }


    }

    void showMoreApps() {
        Uri uri = Uri.parse("market://search?q=pub:sizzlingapps");
        Intent myAppLinkToMarket = new Intent(Intent.ACTION_VIEW, uri);
        try {
            startActivity(myAppLinkToMarket);
        } catch (ActivityNotFoundException e) {
            Toast.makeText(this, " unable to find market app", Toast.LENGTH_LONG).show();
        }


    }

    public void showDatePicker(final View view) {
        if(!adShown) {
            if(mInterstitialAd!=null && mInterstitialAd.isLoaded()) {
                mInterstitialAd.show();
                return;
            }
            else {
                requestNewInterstitial();
            }
        }
        adShown=false;
        // TODO Auto-generated method stub
        //To show current date in the datepicker
        Calendar mcurrentDate = Calendar.getInstance();
        int mYear = mcurrentDate.get(Calendar.YEAR);
        int mMonth = mcurrentDate.get(Calendar.MONTH);
        int mDay = mcurrentDate.get(Calendar.DAY_OF_MONTH);

        DatePickerDialog mDatePicker;
        mDatePicker = new DatePickerDialog(this, new DatePickerDialog.OnDateSetListener() {
            public void onDateSet(DatePicker datepicker, int selectedyear, int selectedmonth, int selectedday) {
                dp = datepicker;
                ((Button) view).setText((dp.getDayOfMonth() < 10 ? "0" + dp.getDayOfMonth() : dp.getDayOfMonth()) + "-" + ((dp.getMonth() + 1) < 10 ? "0" + (dp.getMonth() + 1) : (dp.getMonth() + 1)) + "" + "-" + dp.getYear() + "");
            }
        }, mYear, mMonth, mDay);
        mDatePicker.setTitle("Pick SMS Date");
        mDatePicker.show();

    }

    public void pickContactClicked(View view) {
        Intent i = new Intent(Intent.ACTION_PICK);
        i.setType(ContactsContract.CommonDataKinds.Phone.CONTENT_TYPE);
        startActivityForResult(i, SELECT_PHONE_NUMBER);
    }
}