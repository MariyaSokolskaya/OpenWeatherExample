package com.samsung.itschool.openweatherexample;

import androidx.appcompat.app.AppCompatActivity;

import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

import okhttp3.HttpUrl;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class MainActivity extends AppCompatActivity {

    Button weatherButton;
    EditText cityText;
    TextView tempText, windText, pressText, discribText;

    //константы сетевого соединения
    private final String ADDRESS = "http://api.openweathermap.org/data/2.5/weather";
    private final String API_KEY = "ea69b3d4f0a4a724d2c03268919c83a1";

    //переменные для работы сети
    OkHttpClient weatherClient;
    Request weatherRequest;
    //Response weatherResponse;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        weatherButton   =   findViewById(R.id.weather);
        cityText        =   findViewById(R.id.city);
        tempText        =   findViewById(R.id.temper);
        windText        =   findViewById(R.id.wind);
        pressText       =   findViewById(R.id.pressure);
        discribText     =   findViewById(R.id.discrib);

        weatherButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String city = cityText.getText().toString();
                //создать поток
                WeatherTask weatherTask = new WeatherTask();
                weatherTask.execute(city);
            }
        });
    }

    class WeatherTask extends AsyncTask<String, Void, Response>{

        @Override
        protected Response doInBackground(String... cities) {
            weatherClient = new OkHttpClient();

            //этап 1 - формирование строки запроса
            HttpUrl.Builder httpBuilder = HttpUrl.parse(ADDRESS).newBuilder();
            httpBuilder.addQueryParameter("q", cities[0]);
            httpBuilder.addQueryParameter("appid", API_KEY);
            httpBuilder.addQueryParameter("units", "metric");
            HttpUrl httpUrl = httpBuilder.build();

            //этап 2 - формирование запроса
            weatherRequest = new Request.Builder().url(httpUrl).build();

            //этап 3 - отправка запроса
            try {
                Response weatherResponse = weatherClient.newCall(weatherRequest).execute();
                return weatherResponse;
            } catch (IOException e) {
                e.printStackTrace();
            }

            return null;
        }

        @Override
        protected void onPostExecute(Response response) {
            super.onPostExecute(response);
            if(response != null) {
                try {
                    //String rez = response.body().string();
                    JSONObject jsonObject = new JSONObject(response.body().string());
                    JSONObject main = jsonObject.getJSONObject("main");
                    tempText.setText(Double.toString(main.getDouble("temp")));
                    JSONObject wind = jsonObject.getJSONObject("wind");
                    windText.setText(wind.toString());
                    pressText.setText(Integer.toString(main.getInt("pressure")));
                    JSONArray weather = jsonObject.getJSONArray("weather");
                    discribText.setText(weather.getJSONObject(0).getString("description"));
                }catch (JSONException e){
                    e.printStackTrace();
                }catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }
}
