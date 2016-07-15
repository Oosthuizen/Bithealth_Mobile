package bithealth.com.bithealth;

import android.os.AsyncTask;
import android.support.design.widget.NavigationView;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.HashMap;
import java.util.Iterator;
import java.util.concurrent.ExecutionException;

public class UserInfo extends AppCompatActivity
{
    String totalPoints = "";
    String bmi = "";
    String weight = "";
    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_info);

        GetUserInfo getEvent = new GetUserInfo();
        getEvent.execute();

        JSONObject json = null;
        try
        {
            json = getEvent.get();
            HashMap<String,String> hashMap = new HashMap<>();
            Iterator<String> iter = json.keys();
            while (iter.hasNext()) {
                String key = iter.next();
                String value = json.getString(key);
                hashMap.put(key,value);
            }
            String test = hashMap.get("GetUserResult").toString();

            JSONObject jsonObj = new JSONObject(test);
            HashMap<String,String> hashMap1 = new HashMap<>();
            Iterator<String> iter1 = jsonObj.keys();
            while (iter1.hasNext())
            {
                String key1 = iter1.next();
                String value1 = jsonObj.getString(key1);
                hashMap1.put(key1,value1);
            }

            bmi = hashMap1.get("BMI");
            totalPoints = hashMap1.get("Points");
            weight = hashMap1.get("wight");

            TextView txtBmi = (TextView) findViewById(R.id.txtCurrentBMI);
            txtBmi.setText(bmi);

            TextView txtTotal = (TextView) findViewById(R.id.txtTotalPoints);
            txtTotal.setText(totalPoints);

            TextView txtWeight = (TextView) findViewById(R.id.txtWeight);
            txtWeight.setText(weight);


        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        } catch (JSONException e) {
            e.printStackTrace();
        }

        ImageView progBar = (ImageView) findViewById(R.id.progBar);

        Double d = Double.parseDouble(bmi);
        if(d < 15.5)
        {
            progBar.setImageResource(R.drawable.img1);
        }
        else if(d >= 15.5 && d < 16.5)
        {
            progBar.setImageResource(R.drawable.img3);
        }
        else if(d >= 16.5 && d < 17.5)
        {
            progBar.setImageResource(R.drawable.img5);
        }
        else if(d >= 17.5 && d < 18.5)
        {
            progBar.setImageResource(R.drawable.img7);
        }
        else if(d >= 18.5 && d < 19.5)
        {
            progBar.setImageResource(R.drawable.img9);
        }
        else if(d >= 19.5 && d < 20.5)
        {
            progBar.setImageResource(R.drawable.img11);
        }
        else if(d >= 20.5 && d < 21.5)
        {
            progBar.setImageResource(R.drawable.img13);
        }

        else if(d >= 21.5 && d < 22.5)
        {
            progBar.setImageResource(R.drawable.img15);
        }

        else if(d >= 22.5 && d < 23.5)
        {
            progBar.setImageResource(R.drawable.img17);
        }
        else if(d >= 23.5 && d < 24.5)
        {
            progBar.setImageResource(R.drawable.img19);
        }
        else if(d >= 25.5 && d < 26.5)
        {
            progBar.setImageResource(R.drawable.img22);
        }
        else if(d >= 27.5 && d < 28.5)
        {
            progBar.setImageResource(R.drawable.img24);
        }
        else if(d >= 29.5 && d < 30.5)
        {
            progBar.setImageResource(R.drawable.img27);
        }
        else if(d >= 30.5)
        {
            progBar.setImageResource(R.drawable.img29);
        }

    }

    public static class GetUserInfo extends AsyncTask<Void, Void, JSONObject>
    {
        @Override
        protected JSONObject doInBackground(Void... params) {
            try {
                URL url = new URL("http://discotestcloud.cloudapp.net/Service1.svc/GetUser/4");
                HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                connection.setRequestMethod("GET");
                connection.setRequestProperty("Content-Type", "application/json");
                connection.setConnectTimeout(5000);
                connection.setReadTimeout(5000);
                int responseCode = connection.getResponseCode();
                Log.d("Service", String.valueOf(responseCode));
                BufferedReader br = new BufferedReader(new InputStreamReader(connection.getInputStream()));
                String line = "";
                StringBuilder responseOutput = new StringBuilder();
                while((line = br.readLine()) != null ) {
                    responseOutput.append(line);
                }
                br.close();
                JSONObject Temp = new JSONObject(responseOutput.toString());
                Log.d("Service", Temp.toString());
                return Temp;
            } catch (Exception e) {
                Log.d("ERROR", "Service did not connect");
                System.out.println(e);
                return null;
            }
        }

        @Override
        protected void onPostExecute(JSONObject success) {
            if(success.isNull("count")){
                Log.d("onPOST","problems for days");
            }
        }

        @Override
        protected void onCancelled()
        {

        }

    }
}
