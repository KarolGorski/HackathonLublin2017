package com.mojzesze.hackathonlublin2017;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;

public class WeatherActivity extends AppCompatActivity {

    float temperature = 0;
    float rain = 0;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_weather);

        URL temperatureURL = null;
        try {
            temperatureURL = new URL("http://212.182.4.252/data.php?s=16");

        BufferedReader in = new BufferedReader(
                new InputStreamReader(temperatureURL.openStream()));
        String inputLine;
        while ((inputLine = in.readLine()) != null){
            if(inputLine.contains("temperatureInt:")){
                String[] splittedLine = inputLine.split(" ");

                String temp = splittedLine[1].substring(0, 5);

                temperature = Float.parseFloat(temp);
            }
            if(inputLine.contains("rainCumInt:")) {
                String[] splittedLine = inputLine.split(" ");

                String rainCumInt = splittedLine[1].substring(0, 4);

                rain = Float.parseFloat(rainCumInt);
            }
        }
        in.close();

        TextView tempElement = (TextView) findViewById(R.id.textView2);
        TextView rainElement = (TextView) findViewById(R.id.textView4);

        Log.d("xd", temperature+" "+rain);

        tempElement.setText((int) temperature + " C");
        rainElement.setText((int) rain + " mm");
        } catch (Exception e) {
            e.printStackTrace();
            Log.d("xd", "error");
        }

    }


}
