package com.example.a10016322.weatherapp;

import android.app.Activity;
import android.content.Context;
import android.net.Uri;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.List;


//BK



public class MainActivity extends AppCompatActivity {

    TextView temp, loki, curDate, con, quote;
    String currentDate, curCondition, input, cityState, info;
    boolean bk;
    ListView list;
    EditText editText;
    Button button, backup;
    ImageView imageView;
    ArrayList<JSONObject> forecasts;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        temp = (TextView)(findViewById(R.id.temp_id));
        loki = (TextView)(findViewById(R.id.location_id));
        curDate = (TextView)(findViewById(R.id.currentDate_id));
        con = (TextView)(findViewById(R.id.curCondition_id));
        quote = (TextView)(findViewById(R.id.quote_id));
        list = (ListView)(findViewById(R.id.listView_id));
        editText = (EditText) (findViewById(R.id.editText_id));
        button = (Button) (findViewById(R.id.button_id));
        backup = (Button) (findViewById(R.id.backup_id));
        imageView = (ImageView)(findViewById(R.id.imageView_id));
        forecasts = new ArrayList<JSONObject>();

        WeatherThread DownloadWeather = new WeatherThread();
        DownloadWeather.execute();

        CustomAdapter myAdapter = new CustomAdapter(this, R.layout.listviewlayout, forecasts);
        list.setAdapter(myAdapter);
        editText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {}

            @Override
            public void afterTextChanged(Editable s) {
                cityState = s.toString();
            }
        });

        backup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
            }
        });

        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (cityState!=null) {
                    WeatherThread myThread = new WeatherThread();
                    myThread.execute(cityState);
                }
            }
        });
    }


    public class CustomAdapter extends ArrayAdapter {
        List<JSONObject> list;
        Context mainContext;

        public CustomAdapter(Context context, int resource, ArrayList<JSONObject> objects) {
            super(context, resource, objects);
            list = objects; //this way we can use objects from list in main method
            mainContext = context;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            LayoutInflater inflater = (LayoutInflater)mainContext.getSystemService(Activity.LAYOUT_INFLATER_SERVICE);
            //use this b/c setContentView only works in main activity
            //without ContentView, xml file will not show
            View layoutView = inflater.inflate(R.layout.listviewlayout,null);
            TextView day = (TextView)layoutView.findViewById(R.id.day_id);
            TextView low = (TextView)layoutView.findViewById(R.id.low_id);
            TextView high = (TextView)layoutView.findViewById(R.id.high_id);
            try
            {
                day.setText(forecasts.get(position).get("day").toString());
                high.setText(forecasts.get(position).get("high").toString());
                low.setText(forecasts.get(position).get("low").toString());
            }catch(Exception e){}
            return layoutView;
        }
    }


    public class WeatherThread extends AsyncTask<String, Void, Void> //needs to use String as one of the parameters
    {
        String loc;
        JSONObject json;
        String temperature;
        //@Override
        protected Void doInBackground(String... params) {

            try {
                if(params[0]!=null)
                {
                    String newURL = "select * from weather.forecast where woeid in (select woeid from geo.places(1) where text=\""+params[0]+"\")";
                    String newerURL = Uri.encode(newURL);
                    URL url = new URL("https://query.yahooapis.com/v1/public/yql?q="+newerURL+"&format=json&env=store%3A%2F%2Fdatatables.org%2Falltableswithkeys");
                    Log.d("tag", url.toString());
                    //URLConnection urlConnection = url.openConnection();
                    URLConnection urlConnection = null;
                    Log.d("tag", "ok1");
                    try{
                        urlConnection = url.openConnection();
                        Log.d("tag", "ok2");
                    }catch(Exception e){
                        Log.d("Exception", e.toString());
                    }
                    try{
                        InputStream inputStream = urlConnection.getInputStream();
                        Log.d("tag", inputStream.toString());
                        BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));
                        info = reader.readLine();
                        //reader.close();
                        Log.d("tag", info);
                    }catch(Exception e){
                        Log.d("Exception", e.toString());
                    }
                    //InputStream inputStream = urlConnection.getInputStream();
                    //Log.d("tag", "ok2");
                    //BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));
                    //Log.d("tag", "ok3");
                    //info = reader.readLine();
                    //Log.d("tag", "ok4");
                    //Log.d("tag", info);

                    if (info.contains("null"))
                    {
                        info = "{\"query\":{\"count\":1,\"created\":\"2016-12-14T13:15:56Z\",\"lang\":\"en-US\",\"results\":{\"channel\":{\"units\":{\"distance\":\"mi\",\"pressure\":\"in\",\"speed\":\"mph\",\"temperature\":\"F\"},\"title\":\"Yahoo! Weather - South Brunswick, NJ, US\",\"link\":\"http://us.rd.yahoo.com/dailynews/rss/weather/Country__Country/*https://weather.yahoo.com/country/state/city-12761289/\",\"description\":\"Yahoo! Weather for South Brunswick, NJ, US\",\"language\":\"en-us\",\"lastBuildDate\":\"Wed, 14 Dec 2016 08:15 AM EST\",\"ttl\":\"60\",\"location\":{\"city\":\"South Brunswick\",\"country\":\"United States\",\"region\":\" NJ\"},\"wind\":{\"chill\":\"25\",\"direction\":\"300\",\"speed\":\"11\"},\"atmosphere\":{\"humidity\":\"83\",\"pressure\":\"1011.0\",\"rising\":\"0\",\"visibility\":\"16.1\"},\"astronomy\":{\"sunrise\":\"7:14 am\",\"sunset\":\"4:33 pm\"},\"image\":{\"title\":\"Yahoo! Weather\",\"width\":\"142\",\"height\":\"18\",\"link\":\"http://weather.yahoo.com\",\"url\":\"http://l.yimg.com/a/i/brand/purplelogo//uh/us/news-wea.gif\"},\"item\":{\"title\":\"Conditions for South Brunswick, NJ, US at 07:00 AM EST\",\"lat\":\"40.389568\",\"long\":\"-74.539688\",\"link\":\"http://us.rd.yahoo.com/dailynews/rss/weather/Country__Country/*https://weather.yahoo.com/country/state/city-12761289/\",\"pubDate\":\"Wed, 14 Dec 2016 07:00 AM EST\",\"condition\":{\"code\":\"31\",\"date\":\"Wed, 14 Dec 2016 07:00 AM EST\",\"temp\":\"31\",\"text\":\"Clear\"},\"forecast\":[{\"code\":\"30\",\"date\":\"14 Dec 2016\",\"day\":\"Wed\",\"high\":\"40\",\"low\":\"30\",\"text\":\"Partly Cloudy\"},{\"code\":\"30\",\"date\":\"15 Dec 2016\",\"day\":\"Thu\",\"high\":\"29\",\"low\":\"20\",\"text\":\"Partly Cloudy\"},{\"code\":\"30\",\"date\":\"16 Dec 2016\",\"day\":\"Fri\",\"high\":\"28\",\"low\":\"18\",\"text\":\"Partly Cloudy\"},{\"code\":\"5\",\"date\":\"17 Dec 2016\",\"day\":\"Sat\",\"high\":\"47\",\"low\":\"24\",\"text\":\"Rain And Snow\"},{\"code\":\"39\",\"date\":\"18 Dec 2016\",\"day\":\"Sun\",\"high\":\"56\",\"low\":\"32\",\"text\":\"Scattered Showers\"},{\"code\":\"28\",\"date\":\"19 Dec 2016\",\"day\":\"Mon\",\"high\":\"31\",\"low\":\"23\",\"text\":\"Mostly Cloudy\"},{\"code\":\"28\",\"date\":\"20 Dec 2016\",\"day\":\"Tue\",\"high\":\"31\",\"low\":\"20\",\"text\":\"Mostly Cloudy\"},{\"code\":\"28\",\"date\":\"21 Dec 2016\",\"day\":\"Wed\",\"high\":\"40\",\"low\":\"21\",\"text\":\"Mostly Cloudy\"},{\"code\":\"28\",\"date\":\"22 Dec 2016\",\"day\":\"Thu\",\"high\":\"45\",\"low\":\"31\",\"text\":\"Mostly Cloudy\"},{\"code\":\"12\",\"date\":\"23 Dec 2016\",\"day\":\"Fri\",\"high\":\"43\",\"low\":\"35\",\"text\":\"Rain\"}],\"description\":\"<![CDATA[<img src=\\\"http://l.yimg.com/a/i/us/we/52/31.gif\\\"/>\\n<BR />\\n<b>Current Conditions:</b>\\n<BR />Clear\\n<BR />\\n<BR />\\n<b>Forecast:</b>\\n<BR /> Wed - Partly Cloudy. High: 40Low: 30\\n<BR /> Thu - Partly Cloudy. High: 29Low: 20\\n<BR /> Fri - Partly Cloudy. High: 28Low: 18\\n<BR /> Sat - Rain And Snow. High: 47Low: 24\\n<BR /> Sun - Scattered Showers. High: 56Low: 32\\n<BR />\\n<BR />\\n<a href=\\\"http://us.rd.yahoo.com/dailynews/rss/weather/Country__Country/*https://weather.yahoo.com/country/state/city-12761289/\\\">Full Forecast at Yahoo! Weather</a>\\n<BR />\\n<BR />\\n(provided by <a href=\\\"http://www.weather.com\\\" >The Weather Channel</a>)\\n<BR />\\n]]>\",\"guid\":{\"isPermaLink\":\"false\"}}}}}}";
                        Log.d("Tag", info);
                    }
                }
                else info = "{\"query\":{\"count\":1,\"created\":\"2016-12-14T13:15:56Z\",\"lang\":\"en-US\",\"results\":{\"channel\":{\"units\":{\"distance\":\"mi\",\"pressure\":\"in\",\"speed\":\"mph\",\"temperature\":\"F\"},\"title\":\"Yahoo! Weather - South Brunswick, NJ, US\",\"link\":\"http://us.rd.yahoo.com/dailynews/rss/weather/Country__Country/*https://weather.yahoo.com/country/state/city-12761289/\",\"description\":\"Yahoo! Weather for South Brunswick, NJ, US\",\"language\":\"en-us\",\"lastBuildDate\":\"Wed, 14 Dec 2016 08:15 AM EST\",\"ttl\":\"60\",\"location\":{\"city\":\"South Brunswick\",\"country\":\"United States\",\"region\":\" NJ\"},\"wind\":{\"chill\":\"25\",\"direction\":\"300\",\"speed\":\"11\"},\"atmosphere\":{\"humidity\":\"83\",\"pressure\":\"1011.0\",\"rising\":\"0\",\"visibility\":\"16.1\"},\"astronomy\":{\"sunrise\":\"7:14 am\",\"sunset\":\"4:33 pm\"},\"image\":{\"title\":\"Yahoo! Weather\",\"width\":\"142\",\"height\":\"18\",\"link\":\"http://weather.yahoo.com\",\"url\":\"http://l.yimg.com/a/i/brand/purplelogo//uh/us/news-wea.gif\"},\"item\":{\"title\":\"Conditions for South Brunswick, NJ, US at 07:00 AM EST\",\"lat\":\"40.389568\",\"long\":\"-74.539688\",\"link\":\"http://us.rd.yahoo.com/dailynews/rss/weather/Country__Country/*https://weather.yahoo.com/country/state/city-12761289/\",\"pubDate\":\"Wed, 14 Dec 2016 07:00 AM EST\",\"condition\":{\"code\":\"31\",\"date\":\"Wed, 14 Dec 2016 07:00 AM EST\",\"temp\":\"31\",\"text\":\"Clear\"},\"forecast\":[{\"code\":\"30\",\"date\":\"14 Dec 2016\",\"day\":\"Wed\",\"high\":\"40\",\"low\":\"30\",\"text\":\"Partly Cloudy\"},{\"code\":\"30\",\"date\":\"15 Dec 2016\",\"day\":\"Thu\",\"high\":\"29\",\"low\":\"20\",\"text\":\"Partly Cloudy\"},{\"code\":\"30\",\"date\":\"16 Dec 2016\",\"day\":\"Fri\",\"high\":\"28\",\"low\":\"18\",\"text\":\"Partly Cloudy\"},{\"code\":\"5\",\"date\":\"17 Dec 2016\",\"day\":\"Sat\",\"high\":\"47\",\"low\":\"24\",\"text\":\"Rain And Snow\"},{\"code\":\"39\",\"date\":\"18 Dec 2016\",\"day\":\"Sun\",\"high\":\"56\",\"low\":\"32\",\"text\":\"Scattered Showers\"},{\"code\":\"28\",\"date\":\"19 Dec 2016\",\"day\":\"Mon\",\"high\":\"31\",\"low\":\"23\",\"text\":\"Mostly Cloudy\"},{\"code\":\"28\",\"date\":\"20 Dec 2016\",\"day\":\"Tue\",\"high\":\"31\",\"low\":\"20\",\"text\":\"Mostly Cloudy\"},{\"code\":\"28\",\"date\":\"21 Dec 2016\",\"day\":\"Wed\",\"high\":\"40\",\"low\":\"21\",\"text\":\"Mostly Cloudy\"},{\"code\":\"28\",\"date\":\"22 Dec 2016\",\"day\":\"Thu\",\"high\":\"45\",\"low\":\"31\",\"text\":\"Mostly Cloudy\"},{\"code\":\"12\",\"date\":\"23 Dec 2016\",\"day\":\"Fri\",\"high\":\"43\",\"low\":\"35\",\"text\":\"Rain\"}],\"description\":\"<![CDATA[<img src=\\\"http://l.yimg.com/a/i/us/we/52/31.gif\\\"/>\\n<BR />\\n<b>Current Conditions:</b>\\n<BR />Clear\\n<BR />\\n<BR />\\n<b>Forecast:</b>\\n<BR /> Wed - Partly Cloudy. High: 40Low: 30\\n<BR /> Thu - Partly Cloudy. High: 29Low: 20\\n<BR /> Fri - Partly Cloudy. High: 28Low: 18\\n<BR /> Sat - Rain And Snow. High: 47Low: 24\\n<BR /> Sun - Scattered Showers. High: 56Low: 32\\n<BR />\\n<BR />\\n<a href=\\\"http://us.rd.yahoo.com/dailynews/rss/weather/Country__Country/*https://weather.yahoo.com/country/state/city-12761289/\\\">Full Forecast at Yahoo! Weather</a>\\n<BR />\\n<BR />\\n(provided by <a href=\\\"http://www.weather.com\\\" >The Weather Channel</a>)\\n<BR />\\n]]>\",\"guid\":{\"isPermaLink\":\"false\"}}}}}}";

                json = new JSONObject(info);
                JSONObject query = json.getJSONObject("query");
                JSONObject results = query.getJSONObject("results");
                JSONObject channel = results.getJSONObject("channel");
                JSONObject item = channel.getJSONObject("item");
                JSONObject condition = item.getJSONObject("condition");
                temperature = condition.get("temp").toString();

                JSONObject location = channel.getJSONObject("location");

                JSONArray forecast = item.getJSONArray("forecast");
                currentDate = forecast.getJSONObject(0).get("date").toString();
                curCondition = forecast.getJSONObject(0).get("text").toString();
                Log.d("halp", currentDate);
                Log.d("halp", curCondition);


                for (int i=1; i<6; i++) //to do 5 days, do i<5
                {
                    forecasts.add(forecast.getJSONObject(i));
                }


                loc = location.get("city").toString() + " ," + location.get("region").toString();


                //temperature = json.getJSONObject("object").getJSONObject("query").getJSONObject("results").getJSONObject("channel").getJSONObject("item").getJSONObject("condition").getJSONObject("item").get("temp").toString();
            }catch(Exception e){}
            return null;



        }

        //@Override
        protected void onProgressUpdate(Void... values) {
            super.onProgressUpdate(values);
        }

        // @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            temp.setText(temperature+(char) 0x00B0+ "F"); //shows up null when you start because i added F
            loki.setText(loc);
            curDate.setText(currentDate);
            con.setText(curCondition);
            try
            {
                if(curCondition.toLowerCase().contains("thunder"))
                {
                    imageView.setImageResource(R.drawable.thunder);
                    quote.setText("You are the thunder and I am the lightning!");
                }
                else if(curCondition.toLowerCase().contains("snow"))
                {
                    imageView.setImageResource(R.drawable.snow);
                    quote.setText("Let it snow, let it snow, let it snow!");
                }
                else if(curCondition.toLowerCase().contains("sun"))
                {
                    imageView.setImageResource(R.drawable.sun);
                    quote.setText("I'm walkin' on sunshine!");
                }
                else if(curCondition.toLowerCase().contains("rain")|| curCondition.contains("drizzle"))
                {
                    imageView.setImageResource(R.drawable.rain);
                    quote.setText("Rain, rain, go away. Come again another day.");
                }
                else if(curCondition.toLowerCase().contains("wind"))
                {
                    imageView.setImageResource(R.drawable.wind);
                    quote.setText("Can you paint with all the colors of the wind?");
                }
                else if(curCondition.toLowerCase().contains("cloud"))
                {
                    imageView.setImageResource(R.drawable.cloud);
                    quote.setText("I'm falling from cloud 9");
                }
                else if(curCondition.toLowerCase().contains("clear"))
                {
                    imageView.setImageResource(R.drawable.clear);
                    quote.setText("Blue as the sky, somber and lonely...");
                }
                else if(curCondition.toLowerCase().contains("fog"))
                {
                    imageView.setImageResource(R.drawable.fog);
                    quote.setText("That's when the fog rolls in.");
                }
                else quote.setText("The weather in unclear.");
            }catch (Exception e){}

        }
    }
}
