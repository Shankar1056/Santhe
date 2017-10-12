package bigappcompany.com.santhe.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.TextPaint;
import android.text.TextWatcher;
import android.text.method.LinkMovementMethod;
import android.text.style.ClickableSpan;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import bigappcompany.com.santhe.R;
import bigappcompany.com.santhe.interfaces.SmsListener;
import bigappcompany.com.santhe.model.GenerateOtpPojo;
import bigappcompany.com.santhe.model.RegistrationPojo;
import bigappcompany.com.santhe.network.DownlodableCallback;
import bigappcompany.com.santhe.network.RetrofitDataProvider;
import bigappcompany.com.santhe.other.SmsReceiver;
import bigappcompany.com.santhe.utility.NetworkUtil;
import bigappcompany.com.santhe.utility.SantheUtility;
import bigappcompany.com.santhe.utility.SharedPreferencesUtility;
import uk.co.chrisjenx.calligraphy.CalligraphyConfig;
import uk.co.chrisjenx.calligraphy.CalligraphyContextWrapper;

public class VerifyOtpActivity extends AppCompatActivity {
	
	private TextView tvStatement;
	private Button btnSubmit;
	private EditText etOtp;
	private RetrofitDataProvider retrofitDataProvider;
	private String phNumber = "", UserId = "", otp;
	
	@Override
	protected void onCreate(@Nullable Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		Window window = this.getWindow();
		window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
		window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
			window.setStatusBarColor(ContextCompat.getColor(this, R.color.colorGreen));
		}
		
		setContentView(R.layout.verify_otp_activity);
		
		retrofitDataProvider = new RetrofitDataProvider();
		Bundle bundle = getIntent().getExtras();
		if (bundle != null) {
			
			phNumber = bundle.getString("PhNumber");
			UserId = bundle.getString("UserId");
			otp = bundle.getString("otp");
		}
		
		CalligraphyConfig.initDefault(new CalligraphyConfig.Builder()
		    .setDefaultFontPath("fonts/Pangram-Regular.otf")
		    .setFontAttrId(R.attr.fontPath)
		    .build()
		);
		initViews();
		eventListener();
		ediTextAddListener();
		SmsReceiver.bindListener(new SmsListener() {
			@Override
			public void messageReceived(String messageText) {
				etOtp.setText(messageText);
			}
		});
		
	}
	
	private void ediTextAddListener() {
		etOtp.addTextChangedListener(new TextWatcher() {
			@Override
			public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
				
			}
			
			@Override
			public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
				if (charSequence.length() == 5) {
					InputMethodManager inputManager = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
					inputManager.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
				}
			}
			
			@Override
			public void afterTextChanged(Editable editable) {
				
			}
		});
	}
	
	private void eventListener() {
		
		
		SpannableString ss = new SpannableString("We just sent you the OTP please enter \n OTP below or Resend");
		ClickableSpan clickableSpan = new ClickableSpan() {
			@Override
			public void onClick(View textView) {
				try {
					hitResend();
				} catch (Exception e) {
					e.printStackTrace();
					SantheUtility.dismissProgressDialog();
				}
				
			}
			
			@Override
			public void updateDrawState(TextPaint ds) {
				super.updateDrawState(ds);
				ds.setUnderlineText(false);
				ds.setColor(getResources().getColor(R.color.colorOrange));
			}
		};
		ss.setSpan(clickableSpan, 53, 59, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
		tvStatement.setText(ss);
		tvStatement.setMovementMethod(LinkMovementMethod.getInstance());
		tvStatement.setBackgroundColor(getResources().getColor(R.color.colorTransparent));
		tvStatement.setHighlightColor(getResources().getColor(R.color.colorTransparent));
		
		btnSubmit.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View view) {
				
				try {
					if (etOtp.getText().toString().trim().length() == 0) {
						SantheUtility.displayMessageAlert(getResources().getString(R.string.otp),
						    VerifyOtpActivity.this);
						return;
					}
					if (etOtp.getText().toString().trim().equals(otp)) {
						SantheUtility.showProgress(VerifyOtpActivity.this, getResources().getString(R.string.pleaseWait));
						hitApi();
					} else {
						Toast.makeText(VerifyOtpActivity.this, "Enter Correct otp", Toast.LENGTH_SHORT).show();
						return;
					}
					
				} catch (Exception e) {
					e.printStackTrace();
					SantheUtility.dismissProgressDialog();
				}
				
			}
		});
		
	}
	
	private void hitApi() {
		if (NetworkUtil.getConnectivityStatusBoolen(VerifyOtpActivity.this)) {
			
			
			retrofitDataProvider.getOtpVerify(phNumber, etOtp.getText().toString(), new DownlodableCallback<RegistrationPojo>() {
				@Override
				public void onSuccess(RegistrationPojo result) {
					
					SantheUtility.dismissProgressDialog();
					
					if (result.getStatus().contentEquals("0")) {
						SharedPreferencesUtility.setAuthKey(VerifyOtpActivity.this, result.getData().getUserId());
						SharedPreferencesUtility.setProfileName(VerifyOtpActivity.this, result.getData().getName());
						SharedPreferencesUtility.setProfilePicture(VerifyOtpActivity.this, result.getData().getProfile());
						SharedPreferencesUtility.setUserPhone(VerifyOtpActivity.this, phNumber);
						SharedPreferencesUtility.setIsNormalLogin(getApplicationContext(), false);
						
						if (result.getData().getUserStatus().contentEquals("1")) {
							
							SharedPreferencesUtility.setIsNormalLogin(getApplicationContext(), false);
							Intent intent = new Intent(VerifyOtpActivity.this, PersonaliseActivity.class);
							intent.putExtra("PhNumber", phNumber);
							intent.putExtra("UserId", UserId);
							startActivity(intent);
							finish();
							
						} else {
							
							Intent intent = new Intent(VerifyOtpActivity.this, RegistrationActivity.class);
							intent.putExtra("PhNumber", phNumber);
							intent.putExtra("UserId", UserId);
							startActivity(intent);
							finish();
						}
						
					} else {
						
						SantheUtility.displayMessageAlert(result.getMessage(), VerifyOtpActivity.this);
						
					}
					
				}
				
				@Override
				public void onFailure(String error) {
					
					SantheUtility.displayMessageAlert(error, getApplicationContext());
					SantheUtility.dismissProgressDialog();
					
				}
			});
		} else {
			SantheUtility.displayMessageAlert(getResources().getString(R.string.network), VerifyOtpActivity.this);
			
		}
		
	}
	
	private void hitResend() {
		
		if (NetworkUtil.getConnectivityStatusBoolen(VerifyOtpActivity.this)) {
			
			SantheUtility.showProgress(VerifyOtpActivity.this, getResources().getString(R.string.pleaseWait));
			
			retrofitDataProvider.getMobileRegister(phNumber, new DownlodableCallback<GenerateOtpPojo>() {
				@Override
				public void onSuccess(GenerateOtpPojo result) {
					
					SantheUtility.dismissProgressDialog();
					if (result.getStatus().contains("0")) {
						
						Toast.makeText(getApplicationContext(), result.getMessage(), Toast.LENGTH_LONG).show();
					} else {
						
						Toast.makeText(getApplicationContext(), result.getMessage(), Toast.LENGTH_LONG).show();
					}
					
				}
				
				@Override
				public void onFailure(String error) {
					SantheUtility.dismissProgressDialog();
					SantheUtility.displayMessageAlert(getResources().getString(R.string.something), VerifyOtpActivity.this);
				}
				
				
			});
		} else {
			SantheUtility.displayMessageAlert(getResources().getString(R.string.network), VerifyOtpActivity.this);
			
		}
		
	}
	
	private void initViews() {
		tvStatement = (TextView) findViewById(R.id.tvStatement);
		btnSubmit = (Button) findViewById(R.id.btnSubmit);
		etOtp = (EditText) findViewById(R.id.etOtp);
	}
	
	@Override
	protected void attachBaseContext(Context newBase) {
		super.attachBaseContext(CalligraphyContextWrapper.wrap(newBase));
	}
}
