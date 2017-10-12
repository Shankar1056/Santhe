package bigappcompany.com.santhe.activity;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.InputStream;

import bigappcompany.com.santhe.R;
import bigappcompany.com.santhe.model.RegistrationPojo;
import bigappcompany.com.santhe.network.DownlodableCallback;
import bigappcompany.com.santhe.network.RetrofitDataProvider;
import bigappcompany.com.santhe.utility.NetworkUtil;
import bigappcompany.com.santhe.utility.SantheUtility;
import bigappcompany.com.santhe.utility.SharedPreferencesUtility;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import uk.co.chrisjenx.calligraphy.CalligraphyConfig;
import uk.co.chrisjenx.calligraphy.CalligraphyContextWrapper;


public class RegistrationActivity extends AppCompatActivity {
	
	private static final int REQUEST_CODE_PICTURE = 1;
	private Button btnVerify;
	private ImageView ivselectPicture, ivProfile;
	private EditText etvEmail, etvPhNumber, etvName;
	private String phNumber = "", UserId = "";
	private Uri tempUri;
	private RetrofitDataProvider retrofitDataProvider;
	
	@Override
	protected void onCreate(@Nullable Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		Window window = this.getWindow();
		window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
		window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
			window.setStatusBarColor(ContextCompat.getColor(this, R.color.colorGreen));
		}
		
		setContentView(R.layout.registration_activity);
		retrofitDataProvider = new RetrofitDataProvider();
		Bundle bundle = getIntent().getExtras();
		initViews();
		
		if (bundle != null) {
			
			phNumber = bundle.getString("PhNumber");
			UserId = bundle.getString("UserId");
			etvPhNumber.setText(phNumber);
			etvPhNumber.setEnabled(false);
			
		}
		
		CalligraphyConfig.initDefault(new CalligraphyConfig.Builder()
		    .setDefaultFontPath("fonts/Pangram-Regular.otf")
		    .setFontAttrId(R.attr.fontPath)
		    .build()
		);
		ActivityCompat.requestPermissions(RegistrationActivity.this,
		    new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, 1);
		
		
		eventListerns();
		
	}
	
	
	private void register() {
		
		
		RequestBody requestEmail = RequestBody.create(MediaType.parse("multipart/form-data"), etvEmail.getText().toString());
		RequestBody requestName = RequestBody.create(MediaType.parse("multipart/form-data"), etvName.getText().toString());
		RequestBody requestUserId = RequestBody.create(MediaType.parse("multipart/form-data"), UserId);
		
		
		MultipartBody.Part profileImage = null;
		
		if (tempUri != null) {
			File file = new File(getRealPathFromURI(tempUri));
			Log.i("Register", "Nombre del archivo " + file.getName());
			// create RequestBody instance from file
			RequestBody requestFile =
			    RequestBody.create(MediaType.parse("multipart/form-data"), file);
			// MultipartBody.Part is used to send also the actual file name
			profileImage = MultipartBody.Part.createFormData("profile_img", file.getName(), requestFile);
		}
		SantheUtility.showProgress(RegistrationActivity.this, getResources().getString(R.string.pleaseWait));
		retrofitDataProvider.registerCustomer(requestName, requestEmail, requestUserId, profileImage,
		    new DownlodableCallback<RegistrationPojo>() {
			    @Override
			    public void onSuccess(RegistrationPojo result) {
				    SantheUtility.dismissProgressDialog();
				    
				    if (result.getStatus().contentEquals("0")) {
					    
					    SharedPreferencesUtility.setProfilePicture(RegistrationActivity.this, result.getData().getProfileImage());
					    SharedPreferencesUtility.setProfileName(RegistrationActivity.this, result.getData().getName());
					    SharedPreferencesUtility.setIssignup(getApplicationContext(), false);
					    Intent intent = new Intent(RegistrationActivity.this, PersonaliseActivity.class);
					    startActivity(intent);
					    finish();
					    
				    } else {
					    
					    SantheUtility.displayMessageAlert(result.getMessage(), getApplicationContext());
				    }
				    
				    
			    }
			    
			    @Override
			    public void onFailure(String error) {
				    
				    SantheUtility.displayMessageAlert(error, getApplicationContext());
				    
				    SantheUtility.dismissProgressDialog();
				    
				    
			    }
		    });
		
	}
	
	private void eventListerns() {
		
		
		btnVerify.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View view) {
				
				try {
					
					
					if (loginValidation()) {
						
						if (NetworkUtil.getConnectivityStatusBoolen(RegistrationActivity.this)) {
							register();
						} else {
							SantheUtility.displayMessageAlert(getResources().getString(R.string.network), RegistrationActivity.this);
							
						}
						
						
					}
					
				} catch (Exception e) {
					e.printStackTrace();
					
					SantheUtility.dismissProgressDialog();
				}
				
			}
		});
		
		ivselectPicture.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View view) {
				
				
				Intent pickIntent = new Intent();
				pickIntent.setType("image/*");
				pickIntent.setAction(Intent.ACTION_GET_CONTENT);
				
				Intent takePhotoIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
				String pickTitle = "Take or select a photo";
				Intent chooserIntent = Intent.createChooser(pickIntent, pickTitle);
				
				chooserIntent.putExtra(Intent.EXTRA_INITIAL_INTENTS, new Intent[]{takePhotoIntent});
				startActivityForResult(chooserIntent, REQUEST_CODE_PICTURE);
				
				
			}
		});
		
		
	}
	
	
	private Boolean loginValidation() {
		
		if (etvName.getText().toString().trim().length() <= 0) {
			SantheUtility.displayMessageAlert(getResources().getString(R.string.nameValid), this);
			return false;
		}
		else if (etvEmail.getText().toString().trim().length() <= 0) {
			SantheUtility.displayMessageAlert(getResources().getString(R.string.emaiId), this);
			etvEmail.setText("");
			return false;
		}
		else if (!SantheUtility.isValidEmail1(etvEmail.getText().toString().trim())) {
			SantheUtility.displayMessageAlert(getResources().getString(R.string.validemaiId), this);
			return false;
		} else {
			return true;
		}
	}
	
	@Override
	public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
		switch (requestCode) {
			case 1: {
				// If request is cancelled, the result arrays are empty.
				if (grantResults.length > 0
				    && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
					// permission granted and now can proceed
//                    mymethod(); //a sample method called
					
				} else {
					
					// permission denied, boo! Disable the
					// functionality that depends on this permission.
					Toast.makeText(RegistrationActivity.this, "Permission denied to read your External storage", Toast.LENGTH_SHORT).show();
				}
				return;
			}
			// add other cases for more permissions
		}
	}
	
	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		if (requestCode == REQUEST_CODE_PICTURE && resultCode == Activity.RESULT_OK) {
			if (data == null) {
				return;
			}
			try {
				InputStream inputStream = getContentResolver().openInputStream(data.getData());
				Bitmap bitmap = BitmapFactory.decodeStream(inputStream);
				
				
				tempUri = getImageUri(getApplicationContext(), bitmap);
				
				File finalFile = new File(getRealPathFromURI(tempUri));
				
				Glide.with(this).load(tempUri).apply(RequestOptions.circleCropTransform()).into(ivProfile);
				
				Log.d("selectedImage", "selectedImage" + tempUri);
				
				Log.d("selectedImage", "selectedImage" + finalFile.getAbsolutePath());
				
				
			} catch (FileNotFoundException e) {
				e.printStackTrace();
			}
		}
	}
	
	public Uri getImageUri(Context inContext, Bitmap inImage) {
		ByteArrayOutputStream bytes = new ByteArrayOutputStream();
		inImage.compress(Bitmap.CompressFormat.JPEG, 100, bytes);
		String path = MediaStore.Images.Media.insertImage(inContext.getContentResolver(), inImage, "Title", null);
		return Uri.parse(path);
	}
	
	public String getRealPathFromURI(Uri uri) {
		Cursor cursor = getContentResolver().query(uri, null, null, null, null);
		cursor.moveToFirst();
		int idx = cursor.getColumnIndex(MediaStore.Images.ImageColumns.DATA);
		return cursor.getString(idx);
	}
	
	private void initViews() {
		
		btnVerify = (Button) findViewById(R.id.btnVerify);
		ivselectPicture = (ImageView) findViewById(R.id.ivselectPicture);
		ivProfile = (ImageView) findViewById(R.id.ivProfile);
		etvEmail = (EditText) findViewById(R.id.etvEmail);
		etvName = (EditText) findViewById(R.id.etvName);
		etvPhNumber = (EditText) findViewById(R.id.etvPhNumber);
		
	}
	
	@Override
	protected void attachBaseContext(Context newBase) {
		super.attachBaseContext(CalligraphyContextWrapper.wrap(newBase));
	}
}
