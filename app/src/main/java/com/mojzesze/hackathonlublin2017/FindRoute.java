package com.mojzesze.hackathonlublin2017;

import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserFactory;

import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

public class FindRoute extends AppCompatActivity {

    private List<BikesPlace> bikesPlaces = new ArrayList<>();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_find_route);

        new XmlParser().execute("https://nextbike.net/maps/nextbike-official.xml");
    }

    private class XmlParser extends AsyncTask<String, Void, String> {


        @Override
        protected String doInBackground(String... urls) {
            HttpURLConnection urlConnection;
            try {
                urlConnection = (HttpURLConnection) ((new URL(urls[0]).openConnection()));
                urlConnection.setRequestMethod("GET");
                urlConnection.setDoInput(true);
                urlConnection.connect();

                InputStream stream = urlConnection.getInputStream();

                XmlPullParserFactory xmlFactoryObject = XmlPullParserFactory.newInstance();
                XmlPullParser xmlParser = xmlFactoryObject.newPullParser();
                xmlParser.setFeature(XmlPullParser.FEATURE_PROCESS_NAMESPACES, false);
                xmlParser.setInput(stream, null);

                parseXml(xmlParser);

                stream.close();

            } catch (Exception e) {
                e.printStackTrace();
            }


            return null;
        }

        private void parseXml(XmlPullParser myParser) {
            int event;
            try {
                myParser.next();
                event = myParser.getEventType();
                while (event != XmlPullParser.END_DOCUMENT) {
                    String name = myParser.getName();
                    if ("city".equals(name) && event==XmlPullParser.START_TAG) {
                        if("Lublin".equals(myParser.getAttributeValue(null, "name"))) {
                            do {
                                myParser.next();
                                if("place".equals(myParser.getName()) && myParser.getEventType()==XmlPullParser.START_TAG) {
                                    bikesPlaces.add(new BikesPlace(Double.valueOf(myParser.getAttributeValue(null, "lat")),
                                            Double.valueOf(myParser.getAttributeValue(null, "lng")),
                                            myParser.getAttributeValue(null, "name"),
                                            Integer.valueOf(myParser.getAttributeValue(null, "bikes")),
                                            Integer.valueOf(myParser.getAttributeValue(null, "free_racks"))
                                    ));

                                }

                            } while(!"city".equals(myParser.getName()));
                        }
                    }
                    event = myParser.next();
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
}
