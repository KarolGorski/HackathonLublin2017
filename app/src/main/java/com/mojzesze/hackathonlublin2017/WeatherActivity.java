package com.mojzesze.hackathonlublin2017;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.TextView;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;

public class WeatherActivity extends AppCompatActivity {

    float temperature = 0;
    float rain = 0;



    public WeatherActivity() throws IOException {
        URL temperatureURL = new URL("http://212.182.4.252/data.php?s=16");
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



    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_weather);

        TextView tempElement = (TextView) findViewById(R.id.textView2);
        TextView rainElement = (TextView) findViewById(R.id.textView4);

        tempElement.setText((int) temperature + " C");
        rainElement.setText((int) rain + " mm");

    }


}
