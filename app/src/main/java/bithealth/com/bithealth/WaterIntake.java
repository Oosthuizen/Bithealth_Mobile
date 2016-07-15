package bithealth.com.bithealth;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.PorterDuff;
import android.os.AsyncTask;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;

public class WaterIntake extends AppCompatActivity {
    String waterIntake;
    String total;
    TextView txtCurrentIntake;
    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_water_intake);

        final EditText txtWater = (EditText) findViewById(R.id.txtWaterIntake);
        txtWater.setHintTextColor(getResources().getColor(R.color.colorPrimaryDark));
        txtWater.getBackground().mutate().setColorFilter(getResources().getColor(R.color.colorPrimaryText), PorterDuff.Mode.SRC_ATOP);

        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(this);
        String waterIn = preferences.getString("WaterIn", "");
        if(!waterIn.equalsIgnoreCase(""))
        {
            waterIntake = waterIn;  /* Edit the value here*/
            txtCurrentIntake = (TextView) findViewById(R.id.txtCurrentIntake);
            txtCurrentIntake.setText(waterIntake);
        }


        Button btnWater = (Button) findViewById(R.id.btnWaterIntake);

        btnWater.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v)
            {
                if(!(txtWater.toString().trim().equals("")))
                {
                    int temp;
                    temp = Integer.parseInt(txtWater.getText().toString()) + Integer.parseInt(waterIntake);
                    total = Integer.toString(temp);
                    addWater add = new addWater(total);
                    add.execute();

                    SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(WaterIntake.this);
                    SharedPreferences.Editor editor = preferences.edit();
                    editor.putString("WaterIn",total);
                    editor.apply();
                }
                else
                {
                    Toast.makeText(WaterIntake.this, "No Value Entered", Toast.LENGTH_LONG).show();
                }
            }
        });
    }

    public class addWater extends AsyncTask<Void,Void,Boolean>
    {
        private String total;

        addWater(String total)
        {
            this.total = total;

        }

        @Override
        protected Boolean doInBackground(Void... params)
        {
            try
            {
                JSONObject jsonObject = new JSONObject();

                try
                {
                    URL url = new URL("http://discotestcloud.cloudapp.net/Service1.svc/UpdateUser/4/Mario/DaSilva/password/23/M/22.9/0.86666/1.70/77/MDaSilva/900/"+ total +"");
                    URLConnection connection = url.openConnection();
                    connection.setDoOutput(true);
                    connection.setRequestProperty("Content-Type", "application/json");
                    connection.setConnectTimeout(5000);
                    connection.setReadTimeout(5000);

                    OutputStreamWriter out = new OutputStreamWriter(connection.getOutputStream());

                    out.write(jsonObject.toString());
                    out.close();

                    BufferedReader in = new BufferedReader(new InputStreamReader(connection.getInputStream()));

                    in.close();
                    return true;

                }
                catch (Exception e)
                {
                    Log.d("ERROR", "Service did not connect");
                    System.out.println(e);
                    return false;
                }
            }
            catch (Exception e)
            {
                e.printStackTrace();
            }
            return false;
        }

        @Override
        protected void onPostExecute(final Boolean success)
        {
            if (success)
            {
                finish();
                Toast.makeText(WaterIntake.this, "Water Intake Added", Toast.LENGTH_LONG).show();
            } else
            {
                /*mPasswordView.setError(getString(R.string.error_incorrect_password));
                mPasswordView.requestFocus();*/
                finish();
                Toast.makeText(WaterIntake.this, "Water Intake Unsuccessful", Toast.LENGTH_LONG).show();
            }
        }

        @Override
        protected void onPreExecute()
        {
        }

    }
}
