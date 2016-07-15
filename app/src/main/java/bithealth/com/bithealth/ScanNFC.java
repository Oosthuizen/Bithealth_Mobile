package bithealth.com.bithealth;

import android.content.Intent;
import android.nfc.NfcAdapter;
import android.nfc.Tag;
import android.nfc.tech.MifareClassic;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.URL;
import java.net.URLConnection;
import java.util.StringTokenizer;
import java.util.Timer;
import java.util.TimerTask;

public class ScanNFC extends AppCompatActivity {

    private NfcAdapter nfcAdapter;
    TextView textViewInfo, textViewTagInfo;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_scan_nfc);
        textViewInfo = (TextView)findViewById(R.id.info);
        textViewTagInfo = (TextView)findViewById(R.id.taginfo);

        nfcAdapter = NfcAdapter.getDefaultAdapter(this);
        if(nfcAdapter == null){
            Toast.makeText(this,
                    "NFC NOT supported on this device!",
                    Toast.LENGTH_LONG).show();
            finish();
        }else if(!nfcAdapter.isEnabled()){
            Toast.makeText(this,
                    "NFC NOT Enabled!",
                    Toast.LENGTH_LONG).show();
            finish();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();

        Intent intent = getIntent();
        String action = intent.getAction();

        if (NfcAdapter.ACTION_TECH_DISCOVERED.equals(action))
        {
            Toast.makeText(this, "onResume() - ACTION_TECH_DISCOVERED", Toast.LENGTH_SHORT).show();

            Tag tag = intent.getParcelableExtra(NfcAdapter.EXTRA_TAG);
            if(tag == null){
                textViewInfo.setText("tag == null");
            }else
            {
                String tagInfo = tag.toString() + "\n";

                tagInfo += "\nTag Id: \n";
                byte[] tagId = tag.getId();
                tagInfo += "length = " + tagId.length +"\n";
                for(int i=0; i<tagId.length; i++){
                    tagInfo += Integer.toHexString(tagId[i] & 0xFF) + " ";
                }
                tagInfo += "\n";

                String[] techList = tag.getTechList();
                tagInfo += "\nTech List\n";
                tagInfo += "length = " + techList.length +"\n";
                for(int i=0; i<techList.length; i++){
                    tagInfo += techList[i] + "\n ";
                }

                textViewInfo.setText(tagInfo);

                //Only android.nfc.tech.MifareClassic specified in nfc_tech_filter.xml,
                //so must be MifareClassic
                readMifareClassic(tag);
            }
        }else
        {
            //Toast.makeText(this, "onResume() : " + action, Toast.LENGTH_SHORT).show();

            addQR add = new addQR("Cycling;91.2;12-July-2016");
            add.execute();
        }
    }

    public void readMifareClassic(Tag tag){
        MifareClassic mifareClassicTag = MifareClassic.get(tag);

        String typeInfoString = "--- MifareClassic tag ---\n";
        int type = mifareClassicTag.getType();
        switch(type){
            case MifareClassic.TYPE_PLUS:
                typeInfoString += "MifareClassic.TYPE_PLUS\n";
                break;
            case MifareClassic.TYPE_PRO:
                typeInfoString += "MifareClassic.TYPE_PRO\n";
                break;
            case MifareClassic.TYPE_CLASSIC:
                typeInfoString += "MifareClassic.TYPE_CLASSIC\n";
                break;
            case MifareClassic.TYPE_UNKNOWN:
                typeInfoString += "MifareClassic.TYPE_UNKNOWN\n";
                break;
            default:
                typeInfoString += "unknown...!\n";
        }

        int size = mifareClassicTag.getSize();
        switch(size){
            case MifareClassic.SIZE_1K:
                typeInfoString += "MifareClassic.SIZE_1K\n";
                break;
            case MifareClassic.SIZE_2K:
                typeInfoString += "MifareClassic.SIZE_2K\n";
                break;
            case MifareClassic.SIZE_4K:
                typeInfoString += "MifareClassic.SIZE_4K\n";
                break;
            case MifareClassic.SIZE_MINI:
                typeInfoString += "MifareClassic.SIZE_MINI\n";
                break;
            default:
                typeInfoString += "unknown size...!\n";
        }

        int blockCount = mifareClassicTag.getBlockCount();
        typeInfoString += "BlockCount \t= " + blockCount + "\n";
        int sectorCount = mifareClassicTag.getSectorCount();
        typeInfoString += "SectorCount \t= " + sectorCount + "\n";

        textViewTagInfo.setText(typeInfoString);
    }

    public class addQR extends AsyncTask<Void,Void,Boolean>
    {
        private String QR;

        addQR(String QR)
        {
            this.QR = QR;

        }

        @Override
        protected Boolean doInBackground(Void... params)
        {
            JSONObject jsonObject = new JSONObject();

            StringTokenizer st2 = new StringTokenizer(QR, ";");
            String[] temp = new String[50];
            int i = 0;
            while (st2.hasMoreElements())
            {
                temp[i] = st2.nextElement().toString();
                i++;
            }
            try
            {
           /*     Log.d("hello", temp[0]);
                Log.d("hello", temp[1]);
                Log.d("hello", temp[2]);*/
                URL url = new URL("http://discotestcloud.cloudapp.net/Service1.svc/PostActivity/" + temp[0] + "/" + temp[1] + "/" + temp[2] + "/4");
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

        @Override
        protected void onPostExecute(final Boolean success)
        {
            if (success)
            {
                //finish();
                Toast.makeText(ScanNFC.this, "Activity Added", Toast.LENGTH_LONG).show();
                Intent i = new Intent(ScanNFC.this, Dashboard.class);
                startActivity(i);
            } else
            {
                /*mPasswordView.setError(getString(R.string.error_incorrect_password));
                mPasswordView.requestFocus();*/
                finish();
                Toast.makeText(ScanNFC.this, "Activity Add Unsuccessful", Toast.LENGTH_LONG).show();
            }
        }

        @Override
        protected void onPreExecute()
        {
        }

    }
}