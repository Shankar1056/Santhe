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
import java.util.ArrayList;
import java.util.List;

import bigappcompany.com.santhe.R;
import bigappcompany.com.santhe.network.DownlodableCallback;
import bigappcompany.com.santhe.network.RetrofitDataProvider;
import bigappcompany.com.santhe.utility.SantheUtility;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import uk.co.chrisjenx.calligraphy.CalligraphyConfig;
import uk.co.chrisjenx.calligraphy.CalligraphyContextWrapper;


public class MultipleImageActivity extends AppCompatActivity {

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
        if (bundle != null) {
            phNumber = bundle.getString("PhNumber");
            UserId = bundle.getString("UserId");
        }

        CalligraphyConfig.initDefault(new CalligraphyConfig.Builder()
                .setDefaultFontPath("fonts/Pangram-Regular.otf")
                .setFontAttrId(R.attr.fontPath)
                .build()
        );
        ActivityCompat.requestPermissions(MultipleImageActivity.this,
                new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, 1);

        initViews();
        eventListerns();
        register();
    }

    private void register() {

//        http://bigappcompany.in/demos/farm_app/post_product_for_selling

        RequestBody requestEmail = RequestBody.create(MediaType.parse("multipart/form-data"), etvEmail.getText().toString());
        RequestBody requestName = RequestBody.create(MediaType.parse("multipart/form-data"), etvName.getText().toString());
        RequestBody requestUserId = RequestBody.create(MediaType.parse("multipart/form-data"), UserId);

        List<MultipartBody.Part> profileImage = new ArrayList<MultipartBody.Part>();


//        for (int i = 0; i < 3; i++) {
//            File file = new File(coverImages.get(i).path);
//            RequestBody requestBody = RequestBody.create(MediaType.parse("image/*"), file);
//            MultipartBody.Part filePart =
//                    MultipartBody.Part.createFormData("img]", file.getName(), requestBody);
//            photos.add(filePart);
//        }


//        MultipartBody.Part profileImage = null;


        if (tempUri != null) {
            File file = new File(getRealPathFromURI(tempUri));
            Log.i("Register", "Nombre del archivo " + file.getName());
            // create RequestBody instance from file
            RequestBody requestFile =
                    RequestBody.create(MediaType.parse("multipart/form-data"), file);
            // MultipartBody.Part is used to send also the actual file name
//            profileImage = MultipartBody.Part.createFormData("profile_img", file.getName(), requestFile);
            profileImage.add(MultipartBody.Part.createFormData("profile_img[]", file.getName(), requestFile));
            profileImage.add(MultipartBody.Part.createFormData("profile_img[]", file.getName(), requestFile));
            profileImage.add(MultipartBody.Part.createFormData("profile_img[]", file.getName(), requestFile));
            profileImage.add(MultipartBody.Part.createFormData("profile_img[]", file.getName(), requestFile));

            Log.i("Pj", "Nombre del archivo " + profileImage.size());


        }


        retrofitDataProvider.MultipleImage(profileImage,
                new DownlodableCallback<ResponseBody>() {
                    @Override
                    public void onSuccess(ResponseBody result) {
                        Log.e("RESPONSE", result.toString());
//                        if (result.getStatus().contentEquals("0")) {
//                            Log.d("PJ", "sucess" + result.getMessage());
//                            Intent intent = new Intent(MultipleImageActivity.this, MultipleImageActivity.class);
//                            startActivity(intent);
//                            finish();
//
//                        } else {
//                            Log.d("PJ", "failure" + result.getStatus());
//                        }


                    }

                    @Override
                    public void onFailure(String error) {

                        SantheUtility.displayMessageAlert(error, getApplicationContext());

                    }
                });

    }

    private void eventListerns() {


        btnVerify.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (loginValidation()) {

                    register();
                }
            }
        });

        ivselectPicture.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {


                Intent pickIntent = new Intent();
                pickIntent.setType("image/*");
                pickIntent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true);
                pickIntent.setAction(Intent.ACTION_GET_CONTENT);

                Intent takePhotoIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                String pickTitle = "Take or select a photo";
                Intent chooserIntent = Intent.createChooser(pickIntent, pickTitle);

                chooserIntent.putExtra(Intent.EXTRA_INITIAL_INTENTS, new Intent[]{takePhotoIntent});
                startActivityForResult(chooserIntent, REQUEST_CODE_PICTURE);


//				Intent pickIntent = new Intent(Intent.ACTION_PICK_ACTIVITY);
//				Intent gallIntent=new Intent(Intent.ACTION_GET_CONTENT);
//				gallIntent.setType("image/*");
//				Intent camIntent = new Intent("android.media.action.IMAGE_CAPTURE");
//				pickIntent.putExtra(Intent.EXTRA_INTENT, camIntent);
//				pickIntent.putExtra(Intent.EXTRA_INTENT, gallIntent);
//				pickIntent.putExtra(Intent.EXTRA_TITLE, "Select Source");
//				startActivityForResult(pickIntent, REQUEST_CODE_PICTURE);

            }
        });


    }


    private Boolean loginValidation() {

        if (etvName.getText().toString().length() <= 0) {
            SantheUtility.displayMessageAlert(getResources().getString(R.string.nameValid), this);
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
                    Toast.makeText(MultipleImageActivity.this, "Permission denied to read your External storage", Toast.LENGTH_SHORT).show();
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

//				ivProfile.setImageBitmap(bitmap);
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
        etvPhNumber.setText(phNumber);
        etvPhNumber.setEnabled(false);
    }

    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(CalligraphyContextWrapper.wrap(newBase));
    }
}
