package com.aplication.appweather;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;

import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;


import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;


public class MainActivity extends AppCompatActivity {

    TextView textViewInfo;
    EditText editTextCity;
    String city;
    String temperaturaCity="",feelsLikeCity="",temperaturaMaxCity="",temperaturaMinCity="",umiditateCity="",main="",description="";

    public  void onWeather(View view) throws UnsupportedEncodingException {
        city=editTextCity.getText().toString();
        //dispare fereastra de la edit text cand apasam butonul
        InputMethodManager mrg=(InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
        mrg.hideSoftInputFromWindow(editTextCity.getWindowToken(),0);
        String encodeCityName= URLEncoder.encode(city,"UTF-8");

       setInfoWeather(encodeCityName);
    }

    public void setInfoWeather(String city)  {
        DownloadTask downloadTask=new DownloadTask();
        downloadTask.execute("http://openweathermap.org/data/2.5/weather?q="+city+"&appid=439d4b804bc8187953eb36d2a8c26a02");

    }

    public class DownloadTask extends AsyncTask<String,Void,String>
    {

        @Override
        protected String doInBackground(String... urls) {

            String weatherInfo="";
            HttpURLConnection urlConnection=null;
            try {
                URL url=new URL(urls[0]);
                urlConnection=(HttpURLConnection) url.openConnection();
                InputStream inputStream=urlConnection.getInputStream();
                InputStreamReader reader=new InputStreamReader(inputStream);

                int data=reader.read();

                while(data!=-1)
                {
                    char c=(char)data;
                    weatherInfo+=c;
                    data=reader.read();
                }
                return weatherInfo;

            } catch (Exception e) {
                e.printStackTrace();
               // Toast.makeText(MainActivity.this, "Could not find weather!", Toast.LENGTH_SHORT).show();
            }
            return null;
        }

        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);
            //Log.i("Weather",result);
            String rezultInfoTempCity="error,re-try",t1,statisticiInfoCity="try more,error,re-try";
            try {
                JSONObject jsonObject=new JSONObject(result);
                //temp
                String weatherCityTemp=jsonObject.getString("main");
                JSONObject objectTemp=new JSONObject(weatherCityTemp);
                temperaturaCity=objectTemp.getString("temp");
                feelsLikeCity=objectTemp.getString("feels_like");
                temperaturaMinCity=objectTemp.getString("temp_min");
                temperaturaMaxCity=objectTemp.getString("temp_max");
                umiditateCity=objectTemp.getString("humidity");
                Log.i("Temperatura",temperaturaCity);
                Log.i("Feel_like",feelsLikeCity);
                Log.i("TemperaturaMinCity",temperaturaMinCity);
                Log.i("TemperaturaMaxCity",temperaturaMaxCity);
                Log.i("UmiditateCity",umiditateCity);

                if(temperaturaCity!=""&&feelsLikeCity!=""&&temperaturaMinCity!=""&&temperaturaMaxCity!=""&&umiditateCity!="") {
                 rezultInfoTempCity="But now let's talk about temperatures :\n" +
                         "General temperature: "+temperaturaCity+"°C,\n"+
                         "Feels like: "+feelsLikeCity+"°C,\n"+
                         "The minimum temperature: "+temperaturaMinCity+"°C,\n"+
                         "The maximum temperature: "+temperaturaMaxCity+"°C,\n"+
                         "Humidity: "+umiditateCity+"%!";
                }else
                {
                    Toast.makeText(MainActivity.this, "Could not find temperatures!", Toast.LENGTH_SHORT).show();
                }
                //weather
                String weatherCityInfo=jsonObject.getString("weather");

               JSONArray arr=new JSONArray(weatherCityInfo);
                for(int i=0;i<arr.length();++i)
                {
                    JSONObject jsonParte=arr.getJSONObject(i);
                    main=jsonParte.getString("main");
                    description=jsonParte.getString("description");

                    if(main!=""&&description!="")
                    {
                        statisticiInfoCity="If we look out the window we will notice "+main.toLowerCase() +" and overall at this time they are " +description +" !";

                    }else
                    {
                        Toast.makeText(MainActivity.this, "Could not find sta temperatures!", Toast.LENGTH_SHORT).show();
                    }
                    Log.i("Main",main);
                    Log.i("Description",description);
                }
            } catch (JSONException e) {
                e.printStackTrace();
                Toast.makeText(MainActivity.this, "Could not find sta temperatures!", Toast.LENGTH_SHORT).show();
            }


            t1="Hi,I show you the weather in the city: "+city+"! ";
            textViewInfo.setText(t1+statisticiInfoCity+rezultInfoTempCity);
        }
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        textViewInfo=(TextView) findViewById(R.id.textViewInfo);
        editTextCity=(EditText)findViewById(R.id.editTextTextPersonName);


    }
}