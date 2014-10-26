package com.anuraagy.myweather;

import android.app.Activity;
import android.app.ActionBar;
import android.app.AlertDialog;
import android.app.Fragment;
import android.graphics.Color;
import android.media.Image;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.Html;
import android.text.Layout;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.format.Time;
import android.text.style.ForegroundColorSpan;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.*;
import android.content.*;
import android.os.Build;

import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.StatusLine;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;


public class MyActivity extends Activity {
    private String s;
    private RelativeLayout leay;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my);
        ActionBar actionBar = getActionBar();
        actionBar.hide();
        getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_HIDE_NAVIGATION);
        SharedPreferences sharedPref = getSharedPreferences("loc.xml",MODE_PRIVATE);
//        SharedPreferences.Editor edit=sharedPref.edit();
//        edit.clear();
//        edit.apply();
        String myvalue = sharedPref.getString("loc","" );
        Log.i("Value",myvalue);
        if(myvalue.equals(""))
        {
            if (savedInstanceState == null) {
                getFragmentManager().beginTransaction()
                        .add(R.id.container, new InformationFragment())
                        .commit();
            }
        }else {
            if (savedInstanceState == null) {
                getFragmentManager().beginTransaction()
                        .add(R.id.container, new PlaceholderFragment())
                        .commit();
            }

        }
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.my, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        if (id == R.id.action_settings) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    /**
     * A placeholder fragment containing a simple view.
     */

    public static class InformationFragment extends Fragment {
        private EditText input;
        private TextView tv;
        private Button submit;
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                                 Bundle savedInstanceState) {
            View rootView = inflater.inflate(R.layout.information, container, false);
            getActivity().getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_HIDE_NAVIGATION);

            return rootView;
        }
        public void onViewCreated(View view, Bundle savedInstanceState) {
            input = (EditText)getActivity().findViewById(R.id.editText);
            submit = (Button)getActivity().findViewById(R.id.button);
            tv = (TextView)getActivity().findViewById(R.id.textView);
            submit.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    String s = input.getText().toString().trim();
                    s = s.replace(" ", "");
                    if(s != "")
                    {

                        SharedPreferences sharedPref = getActivity().getSharedPreferences("loc.xml",MODE_PRIVATE);
                        SharedPreferences.Editor editor = sharedPref.edit();
                        Log.i("Text Inputed",s);
                        editor.putString("loc",s);
                        editor.apply();
                        getActivity().finish();
                        startActivity(getActivity().getIntent());

                    } else {
                        tv.setText("Please Enter A Valid Address");
                    }
                }
            });
        }

        }
    public static class PlaceholderFragment extends Fragment {
        private JSONObject myObject,mainObject,nameObject;
        private String myweather;
        private JSONArray weatherName;
        private RelativeLayout leay;

        private String nameW,nameWeird;
        private JSONArray array;
        private ImageView myImage;
        private TextView titleText;
        private String hello,max_temp,min_temp;
        public PlaceholderFragment() {
        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                                 Bundle savedInstanceState) {
            View rootView = inflater.inflate(R.layout.fragment_my, container, false);
            SharedPreferences sharedPref = getActivity().getSharedPreferences("loc.xml", MODE_PRIVATE);
            String sweg = sharedPref.getString("loc", "address");

            Log.i("Sweg",sweg);
             leay = (RelativeLayout)rootView.findViewById(R.id.layout);
            RequestTask task = new RequestTask();
            task.execute(new String[]{"http://api.openweathermap.org/data/2.5/weather?q=" + sweg + "&APPID=970bf0e4978dae293b065f8f2830ba58"});

            return rootView;
        }

        public void onViewCreated(View view, Bundle savedInstanceState) {
            ImageButton myb = (ImageButton)getActivity().findViewById(R.id.imageButton);
            myb.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    SharedPreferences sharedPref = getActivity().getSharedPreferences("loc.xml", MODE_PRIVATE);
                    SharedPreferences.Editor edit=sharedPref.edit();
                    edit.clear();
                    edit.apply();
                    getActivity().finish();
                    startActivity(getActivity().getIntent());
                    getActivity().getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_HIDE_NAVIGATION);

                }
            });

        }
        private void showRightAlert()
        {
            AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
            builder.setMessage("Please Enter a Valid City or Zipcode!")
                    .setCancelable(false)
                    .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            SharedPreferences sharedPref = getActivity().getSharedPreferences("loc.xml",MODE_PRIVATE);
                            SharedPreferences.Editor edit=sharedPref.edit();
                            edit.clear();
                            edit.apply();
                            getActivity().finish();
                            startActivity(getActivity().getIntent());
                            getActivity().getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_HIDE_NAVIGATION);

                        }
                    });
            AlertDialog alert = builder.create();
            alert.show();
        }
        public class RequestTask extends AsyncTask<String, String, String> {
            private TextView myView;
            private String s;
            @Override
            protected String doInBackground(String... uri) {
                HttpClient httpclient = new DefaultHttpClient();
                HttpResponse response;
                String responseString = null;
                try {
                    response = httpclient.execute(new HttpGet(uri[0]));
                    StatusLine statusLine = response.getStatusLine();
                    if (statusLine.getStatusCode() == HttpStatus.SC_OK) {
                        ByteArrayOutputStream out = new ByteArrayOutputStream();
                        response.getEntity().writeTo(out);
                        out.close();
                        responseString = out.toString();
                    } else {
                        //Closes the connection.
                        response.getEntity().getContent().close();
                        throw new IOException(statusLine.getReasonPhrase());
                    }
                } catch (ClientProtocolException e) {
                    //TODO Handle problems..
                } catch (IOException e) {
                    //TODO Handle problems..
                }

//                Log.i("", responseString);
                return responseString;
            }

            @Override
            protected void onPostExecute(String result) {
//                Log.i("","hello");
                super.onPostExecute(result);


                try {


                        JSONObject weather = new JSONObject(result);

                        myweather = weather.getJSONObject("main").getString("temp");
                        weatherName = weather.getJSONArray("weather");
                        nameW = weatherName.getJSONObject(0).getString("main");
                        nameWeird =  weatherName.getJSONObject(0).getString("id");
//                       Log.i("Weather",myweather);
//                     nameObject = myObject.getJSONObject("weather");
//                     weatherName = nameObject.getString("main");
//                     mainObject = myObject.getJSONObject("main");
//                    Log.i("Wather Name",weatherName);
//                    Log.i("mainObject",mainObject.toString());
//                     hello = mainObject.getString("temp");
//                     max_temp = mainObject.getString("temp_max");
//                     min_temp = mainObject.getString("temp_min");


                } catch(JSONException jsonException){

                } catch(NullPointerException n) {

                    SharedPreferences sharedPref = getActivity().getSharedPreferences("loc.xml",MODE_PRIVATE);
                    SharedPreferences.Editor edit=sharedPref.edit();
                    edit.clear();
                    edit.apply();



                }

                try {
                    double f = Double.parseDouble(myweather);
//                double max = Double.parseDouble(max_temp);
//                double min = Double.parseDouble(min_temp);
//
//                double realMax = (max - 273)* 1.8 + 32;
//                double realMin = (min - 273)* 1.8 + 32;
                    int realTemp = (int) Math.round((f - 273) * 1.8 + 32);

//                int myMax = (int)realMax;
//                int myMin = (int)realMin;

//
//


                    Time t = new Time();
                    t.setToNow();
                    int hour = t.hour;
                    myView = (TextView) getActivity().findViewById(R.id.textView);
                    titleText = (TextView) getActivity().findViewById(R.id.textView3);
                    String fe = realTemp + "" + (char) 0x00B0 + "F";


                    myView.setText(fe);
                    String smash = titleText.getText().toString();
                    int hello = nameW.length();
                    String swag = smash.replace("sunny", nameW.toLowerCase());
                    String fo = nameW.toLowerCase();
                    int start = swag.indexOf(fo);
                    Spannable myword = new SpannableString(swag);
                    Log.i("hour", hour + "");
                    myword.setSpan(new ForegroundColorSpan(0xFF2ecc71), start, start+hello,Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                            titleText.setText(myword);


                    myImage = (ImageView) getActivity().findViewById(R.id.imageView);
                    try {
//                        String swggg = nameWeird;
//                        int id1 = Integer.parseInt(nameWeird);
                        int id1 = Integer.parseInt(nameWeird);
                        Log.i("Weather ID", id1+ "");

                        if(id1<300 && id1 > 199){
                            myImage.setImageResource(R.drawable.thunderstorm);
                            myImage.setX(10);
                            myImage.setY(-30);
                        }else
                        if(id1>299 && id1 < 322){
                            myImage.setImageResource(R.drawable.drizzle);
                            myImage.setX(10);
                            myImage.setY(-30);
                        }
                        else
                        if(id1>499 && id1 < 532){
                            myImage.setImageResource(R.drawable.rain);
                            myImage.setX(10);
                            myImage.setY(-30);
                        }
                        else
                        if(id1>599 && id1 < 623){
                            if(id1 == 611 || id1 == 615 || id1 == 612 || id1 == 616)
                            {
                                myImage.setImageResource(R.drawable.sleet);
                            } else {
                                myImage.setImageResource(R.drawable.snow);
                            }
                            myImage.setX(10);
                            myImage.setY(-30);
                        }else if(id1==701 || id1 == 741){
                            myImage.setImageResource(R.drawable.fog);
                            myImage.setX(-40);
                            myImage.setY(-70);
                        }
                        else if(id1==721){
                            myImage.setImageResource(R.drawable.haze);
                            myImage.setX(-40);
                            myImage.setY(-70);
                        }
                        else if(id1 == 800){
                            if(hour > 18 || hour < 6)
                            {
                                myImage.setImageResource(R.drawable.clearnight);

                            } else {
                                myImage.setImageResource(R.drawable.clearsky);
                            }
                            myImage.setX(-40);
                            myImage.setY(-70);
                        }else if(id1 > 800 && id1 < 803)
                        {
                            myImage.setImageResource(R.drawable.brokenclouds);
                            myImage.setX(10);
                            myImage.setY(-30);

                        }
                        else if(id1 == 804)
                        {
                            myImage.setImageResource(R.drawable.overcastclouds);
                            myImage.setX(10);
                            myImage.setY(-30);

                        }else if(id1 > 950 && id1 < 962)
                        {
                            myImage.setImageResource(R.drawable.freshbreeze);
                            myImage.setX(-40);
                            myImage.setY(-70);

                        }else{
                            myImage.setImageResource(R.drawable.calm);
                            myImage.setX(-40);
                            myImage.setY(-70);

                        }


//                        String swggg = "showerdrizzle";
//                        swggg = swggg.replace(" ", "");
//                        Context context = myImage.getContext();
//                        int id = context.getResources().getIdentifier(swggg, "drawable", context.getPackageName());
//                        myImage.setImageResource(R.drawable.showerdrizzle);
//                        if(swggg.contains("rain") || swggg.contains("drizzle") || swggg.contains("rain") ) {
//                            myImage.setX(10);
//                            myImage.setY(-30);
//                        }
//                        } else {
//                            myImage.setX(-70);
//                            myImage.setY(-70);
//                        }
                    } catch (NullPointerException n){
                        myImage.setImageResource(R.drawable.calm);
                    }
                    leay.setVisibility(View.VISIBLE);

//                String weather = myWeather + "";
                } catch (NullPointerException n){
                    showRightAlert();
                }
            }
        }
    }
}
