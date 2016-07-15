package bithealth.com.bithealth;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.AsyncTask;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.util.SparseArray;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.vision.CameraSource;
import com.google.android.gms.vision.Detector;
import com.google.android.gms.vision.barcode.Barcode;
import com.google.android.gms.vision.barcode.BarcodeDetector;

import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.URL;
import java.net.URLConnection;
import java.util.StringTokenizer;

public class ScanQR extends AppCompatActivity
{

    private SurfaceView cameraView;
    private TextView barcodeInfo;
    private BarcodeDetector barcodeDetector;
    private CameraSource cameraSource;
    boolean count = false;

    // http://code.tutsplus.com/tutorials/reading-qr-codes-using-the-mobile-vision-api--cms-24680
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        count = false;
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_scan_qr);

        cameraView = (SurfaceView) findViewById(R.id.camera_view);
        barcodeInfo = (TextView) findViewById(R.id.code_info);

        barcodeDetector = new BarcodeDetector.Builder(this).setBarcodeFormats(Barcode.QR_CODE).build();

        cameraSource = new CameraSource.Builder(this, barcodeDetector).setRequestedPreviewSize(640, 480).build(); //change camera size

        cameraView.getHolder().addCallback(new SurfaceHolder.Callback()
        {
            @Override
            public void surfaceCreated(SurfaceHolder holder)
            {
                try
                {
                    final int REQUEST_CODE_ASK_PERMISSIONS = 123;
                    int hasWriteContactsPermission = checkSelfPermission(Manifest.permission.CAMERA);
                    if (hasWriteContactsPermission != PackageManager.PERMISSION_GRANTED)
                    {
                        requestPermissions(new String[] {Manifest.permission.CAMERA},
                                REQUEST_CODE_ASK_PERMISSIONS);
                        return;
                    }
                    cameraSource.start(cameraView.getHolder());
                }
                catch (IOException ie)
                {
                    Log.e("CAMERA SOURCE", ie.getMessage());
                }
            }

            @Override
            public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
            }

            @Override
            public void surfaceDestroyed(SurfaceHolder holder)
            {
                cameraSource.stop();
            }
        });

        barcodeDetector.setProcessor(new Detector.Processor<Barcode>() {
            @Override
            public void release() {
            }

            @Override
            public void receiveDetections(Detector.Detections<Barcode> detections)
            {
                if(count == false)
                {
                    final SparseArray<Barcode> barcodes = detections.getDetectedItems();
                    //String qrcode =  "";
                    if (barcodes.size() != 0)
                    {
                        barcodeInfo.post(new Runnable()
                        {    // Use the post method of the TextView
                            public void run()
                            {
                                barcodeInfo.setText(barcodes.valueAt(0).displayValue);
                                //qrcode=barcodes.valueAt(0).displayValue;
                            }
                        });
                        addQR add = new addQR(barcodeInfo.getText().toString());
                        add.execute();
                        count = true;
                        Intent i = new Intent(ScanQR.this, Dashboard.class);
                        startActivity(i);
                    }
                }
            }
        });
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
                finish();
                Toast.makeText(ScanQR.this, "Activity Added", Toast.LENGTH_LONG).show();
            } else
            {
                /*mPasswordView.setError(getString(R.string.error_incorrect_password));
                mPasswordView.requestFocus();*/
                finish();
                Toast.makeText(ScanQR.this, "Activity Add Unsuccessful", Toast.LENGTH_LONG).show();
            }
        }

        @Override
        protected void onPreExecute()
        {
        }

    }
}