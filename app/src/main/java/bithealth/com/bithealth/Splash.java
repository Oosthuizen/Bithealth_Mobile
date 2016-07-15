package bithealth.com.bithealth;

import android.content.Intent;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ProgressBar;

public class Splash extends AppCompatActivity
{

    private final int SPLASH_DISPLAY_LENGTH = 3000;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        //Remove type bar
        this.requestWindowFeature(Window.FEATURE_NO_TITLE);
        //Remove notification bar
        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        //set content view AFTER ABOVE sequence (to avoid crash)
        this.setContentView(R.layout.activity_splash);

        ProgressBar spinner = (ProgressBar) findViewById(R.id.progBar);
        spinner.getIndeterminateDrawable().setColorFilter(getResources().getColor(R.color.colorPrimaryDark), android.graphics.PorterDuff.Mode.MULTIPLY);

        new Handler().postDelayed(new Runnable()
        {
            @Override
            public void run()
            {
                startActivity(new Intent(Splash.this, Login.class));
                finish();
            }
        }, SPLASH_DISPLAY_LENGTH);
    }

}
