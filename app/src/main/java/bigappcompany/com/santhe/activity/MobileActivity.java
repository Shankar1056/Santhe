package bigappcompany.com.santhe.activity;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import bigappcompany.com.santhe.R;
import bigappcompany.com.santhe.model.GenerateOtpPojo;
import bigappcompany.com.santhe.network.DownlodableCallback;
import bigappcompany.com.santhe.network.RetrofitDataProvider;
import bigappcompany.com.santhe.utility.NetworkUtil;
import bigappcompany.com.santhe.utility.SantheUtility;
import bigappcompany.com.santhe.utility.SharedPreferencesUtility;
import uk.co.chrisjenx.calligraphy.CalligraphyConfig;
import uk.co.chrisjenx.calligraphy.CalligraphyContextWrapper;


public class MobileActivity extends AppCompatActivity {
	
	private static final int PERMISSIONS_REQUEST_RECEIVE_SMS = 0;
	private Button btnVerify;
	private EditText tvPhNumber;
	private RetrofitDataProvider RetrofitDataProvider;
	
	@Override
	protected void onCreate(@Nullable Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		Window window = this.getWindow();
		window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
		window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
			window.setStatusBarColor(ContextCompat.getColor(this, R.color.colorGreen));
		}
		setContentView(R.layout.mobile_no_activity);
		
		CalligraphyConfig.initDefault(new CalligraphyConfig.Builder()
		    .setDefaultFontPath("fonts/Pangram-Regular.otf")
		    .setFontAttrId(R.attr.fontPath)
		    .build()
		);
		RetrofitDataProvider = new RetrofitDataProvider();
		initViews();
		eventListerns();
		edittextChangeText();
		requestPermissions(Manifest.permission.RECEIVE_SMS, PERMISSIONS_REQUEST_RECEIVE_SMS);
		
	}
	
	private void edittextChangeText() {
		tvPhNumber.addTextChangedListener(new TextWatcher() {
			@Override
			public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
				
			}
			
			@Override
			public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
				
				if (charSequence.length() == 10) {
					InputMethodManager inputManager = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
					inputManager.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
				}
			}
			
			@Override
			public void afterTextChanged(Editable editable) {
				
			}
		});
	}
	
	private void eventListerns() {
		
		btnVerify.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View view) {
				if (tvPhNumber.getText().toString().trim().length() < 10) {
					SantheUtility.displayMessageAlert(getResources().getString(R.string.phoneNumberValid), MobileActivity.this);
				} else {
					
					try {
						
						if (NetworkUtil.getConnectivityStatusBoolen(MobileActivity.this)) {
							hitApi();
						} else {
							SantheUtility.displayMessageAlert(getResources().getString(R.string.network), MobileActivity.this);
							
						}
					} catch (Exception e) {
						e.printStackTrace();
						SantheUtility.dismissProgressDialog();
					}
				}
			}
		});
		
	}
	
	private void requestPermissions(String permission, int requestCode) {
		// Here, thisActivity is the current activity
		if (ContextCompat.checkSelfPermission(this, permission)
		    != PackageManager.PERMISSION_GRANTED) {
			
			// Should we show an explanation?
			if (ActivityCompat.shouldShowRequestPermissionRationale(this, permission)) {
				
				// Show an explanation to the user *asynchronously* -- don't block
				// this thread waiting for the user's response! After the user
				// sees the explanation, try again to request the permission.
				Toast.makeText(this, "Granting permission is necessary!", Toast.LENGTH_LONG).show();
				
			} else {
				
				// No explanation needed, we can request the permission.
				
				ActivityCompat.requestPermissions(this,
				    new String[]{permission},
				    requestCode);
				
				// requestCode is an
				// app-defined int constant. The callback method gets the
				// result of the request.
			}
		}
	}
	
	@Override
	public void onRequestPermissionsResult(int requestCode,
	                                       String permissions[], int[] grantResults) {
		switch (requestCode) {
			case PERMISSIONS_REQUEST_RECEIVE_SMS: {
				// If request is cancelled, the result arrays are empty.
				if (grantResults.length > 0
				    && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
					
					// permission was granted, yay! Do the
					// contacts-related task you need to do.

//                    NotificationUtil.getInstance().show(this, NotificationUtil.CONTENT_TYPE.INFO,
//                            getResources().getString(R.string.app_name),
//                            "Permission granted!");
					Toast.makeText(this, "Permission granted!", Toast.LENGTH_LONG).show();
					
				} else {
					
					// permission denied, boo! Disable the
					// functionality that depends on this permission.

//                    NotificationUtil.getInstance().show(this, NotificationUtil.CONTENT_TYPE.ERROR,
//                            getResources().getString(R.string.app_name),
//                            "Permission denied! App will not function correctly");
					Toast.makeText(this, "Permission denied! App will not function correctly", Toast.LENGTH_LONG).show();
				}
				return;
			}
			
			// other 'case' lines to check for other
			// permissions this app might request
		}
	}
	
	private void hitApi() {
		SantheUtility.showProgress(MobileActivity.this, getResources().getString(R.string.pleaseWait));
		
		RetrofitDataProvider.getMobileRegister(tvPhNumber.getText().toString(), new DownlodableCallback<GenerateOtpPojo>() {
			@Override
			public void onSuccess(GenerateOtpPojo result) {
				
				SantheUtility.dismissProgressDialog();
				
				if (result.getStatus().contains("0")) {
					
					SharedPreferencesUtility.setAuthKey(MobileActivity.this, result.getData().getUser_id());
					SharedPreferencesUtility.setProfileName(MobileActivity.this, result.getData().getName());
					
					Intent intent = new Intent(MobileActivity.this, VerifyOtpActivity.class);
					intent.putExtra("PhNumber", tvPhNumber.getText().toString());
					intent.putExtra("UserId", result.getData().getUser_id());
					intent.putExtra("otp", result.getOtp());
					startActivity(intent);
					finish();
					
				} else {
					Toast.makeText(getApplicationContext(), result.getMessage(), Toast.LENGTH_LONG).show();
					
				}
			}
			
			@Override
			public void onFailure(String error) {
				
				SantheUtility.dismissProgressDialog();
				Toast.makeText(getApplicationContext(), getResources().getString(R.string.something
				), Toast.LENGTH_LONG).show();
				
			}
			
			
		});
		
	}
	
	
	private void initViews() {
		btnVerify = (Button) findViewById(R.id.btnVerify);
		tvPhNumber = (EditText) findViewById(R.id.tvPhNumber);
	}
	
	@Override
	protected void attachBaseContext(Context newBase) {
		super.attachBaseContext(CalligraphyContextWrapper.wrap(newBase));
		
	}
}
