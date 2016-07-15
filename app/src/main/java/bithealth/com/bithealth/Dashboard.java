package bithealth.com.bithealth;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.github.clans.fab.FloatingActionButton;
import com.github.clans.fab.FloatingActionMenu;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.ExecutionException;

public class Dashboard extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    private List<Activity> movieList = new ArrayList<>();
    private RecyclerView recyclerView;
    private ActivityAdapter mAdapter;

    FloatingActionMenu materialDesignFAM;
    FloatingActionButton floatingActionButton1, floatingActionButton2, floatingActionButton3;

    int count;
    String[] type = new String[200];
    String[] distance = new String[200];
    String[] date =new String[200];

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dashboard);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        final DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        ImageView progbar = (ImageView) findViewById(R.id.progBar);

        materialDesignFAM = (FloatingActionMenu) findViewById(R.id.material_design_android_floating_action_menu);
        floatingActionButton1 = (FloatingActionButton) findViewById(R.id.material_design_floating_action_menu_item1);
        floatingActionButton2 = (FloatingActionButton) findViewById(R.id.material_design_floating_action_menu_item2);
        floatingActionButton3 = (FloatingActionButton) findViewById(R.id.material_design_floating_action_menu_item3);


        floatingActionButton1.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Intent i = new Intent(Dashboard.this, ScanQR.class);
                startActivity(i);
            }
        });
        floatingActionButton2.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Intent i = new Intent(Dashboard.this, ScanNFC.class);
                startActivity(i);

            }
        });

        floatingActionButton3.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Intent i = new Intent(Dashboard.this, WaterIntake.class);
                startActivity(i);
            }
        });

        recyclerView = (RecyclerView) findViewById(R.id.recycler_view);

        mAdapter = new ActivityAdapter(movieList);
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getApplicationContext());
        recyclerView.setLayoutManager(mLayoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.addItemDecoration(new DividerItemDecoration(this, LinearLayoutManager.VERTICAL));
        recyclerView.setAdapter(mAdapter);
        prepareData();

        GetEventInfo getEvent = new GetEventInfo();
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

            String username, name, surname, waterIntake;
            username = hashMap1.get("UserName");
            name = hashMap1.get("Name");
            surname = hashMap1.get("SurName");
            waterIntake = hashMap1.get("WaterIn");

            SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(this);
            SharedPreferences.Editor editor = preferences.edit();
            editor.putString("WaterIn",waterIntake);
            editor.apply();

            NavigationView navView = (NavigationView) findViewById(R.id.nav_view);
            navView.setNavigationItemSelectedListener(this);
            View header=navView.getHeaderView(0);

            TextView txtname = (TextView)header.findViewById(R.id.txtnavname);
            txtname.setText(name + " " + surname);
            TextView txtuname = (TextView)header.findViewById(R.id.txtnavuname);
            txtuname.setText(username);
            /*ImageView imgview = (ImageView) header.findViewById(R.id.imgnavlogo);
            imgview.setImageResource(R.drawable.profile_img);*/


        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        } catch (JSONException e) {
            e.printStackTrace();
        }

        GetActivities getActivities = new GetActivities();
        getActivities.execute();
        JSONObject jsonO = null;
        try
        {
            jsonO = getActivities.get();
            HashMap<String,String> hashMapActivity = new HashMap<>();
            Iterator<String> iter = jsonO.keys();
            while (iter.hasNext())
            {
                String key = iter.next();
                String value = jsonO.getString(key);
                hashMapActivity.put(key,value);
            }
            String test = hashMapActivity.get("GetAllActivitysResult").toString();
            JSONArray array = new JSONArray(test);
            for(int i=0; i<array.length(); i++)
            {
                JSONObject jsonObj  = array.getJSONObject(i);
                HashMap<String,String> hashMapActivity1 = new HashMap<>();
                Iterator<String> iter1 = jsonObj.keys();
                while (iter1.hasNext()) {
                    String key1 = iter1.next();
                    String value1 = jsonObj.getString(key1);
                    hashMapActivity1.put(key1,value1);
                }
                type[i] = hashMapActivity1.get("Type");
                distance[i] = hashMapActivity1.get("Discription");
                date[i] = hashMapActivity1.get("Date");
                count++;
                Log.d("test", type[i]);
            }

        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        } catch (JSONException e) {
            e.printStackTrace();
        }

        prepareData();
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    private void prepareData()
    {
        for(int i=0; i<count; i++)
        {
            Activity activity = new Activity(type[i],date[i], distance[i]);
            movieList.add(activity);
        }

        mAdapter.notifyDataSetChanged();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.dashboard, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        /*if (id == R.id.action_settings) {
            return true;
        }*/

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_scanQR)
        {
            Intent i = new Intent(Dashboard.this, ScanQR.class);
            startActivity(i);
        } else if (id == R.id.nav_nfc) {

        } else if (id == R.id.nav_waterIntake)
        {
            Intent i = new Intent(Dashboard.this, WaterIntake.class);
            startActivity(i);
        }
        else if (id == R.id.nav_UserProgress)
        {
            Intent i = new Intent(Dashboard.this, UserInfo.class);
            startActivity(i);
        }
        /*else if (id == R.id.nav_share) {

        } else if (id == R.id.nav_send) {

        }*/

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    public static class GetEventInfo extends AsyncTask<Void, Void, JSONObject> {

        public GetEventInfo() {

        }

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

    public static class GetActivities extends AsyncTask<Void, Void, JSONObject> {

        public GetActivities() {

        }

        @Override
        protected JSONObject doInBackground(Void... params) {
            try {
                URL url = new URL("http://discotestcloud.cloudapp.net/Service1.svc/GetAllActivitys");
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
