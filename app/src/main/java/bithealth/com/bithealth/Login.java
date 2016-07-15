package bithealth.com.bithealth;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.PorterDuff;
import android.graphics.Typeface;
import android.os.AsyncTask;
import android.os.Handler;
import android.os.StrictMode;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.method.PasswordTransformationMethod;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.URL;
import java.net.URLConnection;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class Login extends AppCompatActivity {

    Connection con;
    String un,pass,db,ip;
    EditText password;
    EditText username;
    String _username, _password;
    ProgressBar spinner;
    Button btnLogin;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        this.requestWindowFeature(Window.FEATURE_NO_TITLE);
        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_login);

        password = (EditText) findViewById(R.id.txtPassword);
        password.setTypeface(Typeface.DEFAULT);
        password.setTransformationMethod(new PasswordTransformationMethod());
        password.setHintTextColor(getResources().getColor(R.color.colorPrimaryDark));
        password.getBackground().mutate().setColorFilter(getResources().getColor(R.color.colorPrimaryText), PorterDuff.Mode.SRC_ATOP);

        username = (EditText) findViewById(R.id.txtUsername);
        username.setHintTextColor(getResources().getColor(R.color.colorPrimaryDark));
        username.getBackground().mutate().setColorFilter(getResources().getColor(R.color.colorPrimaryText), PorterDuff.Mode.SRC_ATOP);

        spinner = (ProgressBar) findViewById(R.id.pBar);
        spinner.getIndeterminateDrawable().setColorFilter(getResources().getColor(R.color.colorPrimaryDark), android.graphics.PorterDuff.Mode.MULTIPLY);
        spinner.setVisibility(View.GONE);

        /*Button btnSignUp = (Button)findViewById(R.id.btnSignUp);*/
        btnLogin = (Button)findViewById(R.id.btnLogin);

        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v)
            {
                _username = username.getText().toString();
                _password = password.getText().toString();

                if(_username.trim().equals("")) {
                    Toast.makeText(Login.this, "Please enter your Username", Toast.LENGTH_LONG).show();
                   // z = "Please enter your Username";
                }
                else if(_password.equals("")) {
                    Toast.makeText(Login.this, "Please enter your Password", Toast.LENGTH_LONG).show();
                    //z = "Please enter your Password";
                }
                else
                {
                    spinner.setVisibility(View.VISIBLE);
                    btnLogin.setVisibility(View.INVISIBLE);
                    new Handler().postDelayed(new Runnable()
                    {
                        @Override
                        public void run()
                        {
                            Intent i = new Intent(Login.this, Dashboard.class);
                            startActivity(i);
                        }
                    }, 2000);

                }


            }
        });


    }
}
