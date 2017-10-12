package bigappcompany.com.santhe.activity;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.content.ContextCompat;
import android.view.Window;
import android.view.WindowManager;

import org.json.JSONException;

import bigappcompany.com.santhe.R;
import bigappcompany.com.santhe.utility.SharedPreferencesUtility;
import uk.co.chrisjenx.calligraphy.CalligraphyConfig;
import uk.co.chrisjenx.calligraphy.CalligraphyContextWrapper;

public class SplashScreenActivity extends Activity {

    private int SplashTime = 2500;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Window window = this.getWindow();

        window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            window.setStatusBarColor(ContextCompat.getColor(this, R.color.colorGreen));
        }
        setContentView(R.layout.splashscreen_activity);
        setSplashScreen();
        CalligraphyConfig.initDefault(new CalligraphyConfig.Builder()
                .setDefaultFontPath("fonts/Pangram-Bold.otf")
                .setFontAttrId(R.attr.fontPath)
                .build()
        );
    }

    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(CalligraphyContextWrapper.wrap(newBase));
    }

    private void setSplashScreen() {
        new Handler().postDelayed(new Runnable() {

            @Override
            public void run() {
                try {
                    AfterSplashScreen();
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }, SplashTime);

    }

    private void AfterSplashScreen() throws JSONException {
        super.onResume();

        if (SharedPreferencesUtility.getFirstTime(this)) {
            super.onResume();
            SharedPreferencesUtility.setFirstTime(this, false);
            startActivity(new Intent(SplashScreenActivity.this, FirstTimeDetailActivity.class));
            finish();

        } else {

            // check for logout m make true

            if (SharedPreferencesUtility.getIsNormalLogin(getApplicationContext()) == true) {
                super.onResume();
                startActivity(new Intent(SplashScreenActivity.this, MobileActivity.class));
                SplashScreenActivity.this.finish();

            }
            else if (SharedPreferencesUtility.getIssignup(getApplicationContext()) == true) {
                super.onResume();
                Intent intent = new Intent(SplashScreenActivity.this, RegistrationActivity.class);
                intent.putExtra("PhNumber", SharedPreferencesUtility.getUserPhone(SplashScreenActivity.this));
                intent.putExtra("UserId", SharedPreferencesUtility.getAuthkey(SplashScreenActivity.this));
                startActivity(intent);
                SplashScreenActivity.this.finish();
            }
            else {
                super.onResume();
                startActivity(new Intent(SplashScreenActivity.this, HomeScreenActivity.class));
                SplashScreenActivity.this.finish();
            }

        }
    }


}
