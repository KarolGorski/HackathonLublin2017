package com.mojzesze.hackathonlublin2017;

import android.app.Activity;
import android.content.Intent;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import org.json.JSONObject;


import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

public class WeatherActivity extends Activity {

    float temperature;
    float rain;
    String temp;
    String rainString;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_weather);

        new URLLoader().execute("http://212.182.4.252/data.php?s=16");
        String URLmpk = "http://www.mpk.lublin.pl/index.php?s=rozklady";
    }

    public void goToRoute(View v) {
        Intent intent = new Intent(WeatherActivity.this, FindRoute.class);
        startActivity(intent);
    }
    public void goToWeb(View v){
        Intent intent = new Intent(WeatherActivity.this, WebActivity.class);
        startActivity(intent);
    }


    private class URLLoader extends AsyncTask<String, Void, String> {


        @Override
        protected String doInBackground(String... urls) {
            HttpURLConnection urlConnection;
            try {
                urlConnection = (HttpURLConnection) ((new URL(urls[0]).openConnection()));
                urlConnection.setRequestMethod("GET");
                urlConnection.setDoInput(true);
                urlConnection.connect();

                InputStream stream = urlConnection.getInputStream();

                BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(urlConnection.getInputStream(), "UTF-8"));
                StringBuilder sb = new StringBuilder();
                String inputLine;
                while((inputLine = bufferedReader.readLine()) != null){
                    sb.append(inputLine);
                }

                JSONObject json = new JSONObject(sb.toString());

                temp = json.getString("temperatureInt");
                rainString = json.getString("rainCumInt");

                Log.d("xd", temp + " " + rainString);

                temperature = Float.valueOf(temp);
                rain = Float.valueOf(rainString);


                stream.close();

            } catch (Exception e) {
                e.printStackTrace();
            }


            return null;
        }

        @Override
        protected void onPostExecute(String s) {
            TextView tempElement = (TextView) findViewById(R.id.tempView);
            TextView rainElement = (TextView) findViewById(R.id.rainView);
            TextView notification = (TextView) findViewById(R.id.notify);
            Button buttonBike = (Button) findViewById(R.id.bikeButton);
            Button buttonMPK = (Button) findViewById(R.id.webButton);

            tempElement.setText( temperature + " C");
            rainElement.setText( rain + " mm");

            Log.d("xdd", temperature + " " + rain);

            if(rain >0 || temperature < 10){
                notification.setText("Pogoda nie dopisuje, zalecamy jazdę autobusem");
                buttonBike.setText("Mimo to chcę pojechać rowerem.");

            } else {
                notification.setText("Pogoda idealna na rower");

                buttonMPK.setText("Mimo dobrej pogody chcę zobaczyć rozkład MPK Lublin");

            }
        }
    }




}