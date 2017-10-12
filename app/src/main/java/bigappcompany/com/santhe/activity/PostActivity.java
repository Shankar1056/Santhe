package bigappcompany.com.santhe.activity;

import android.Manifest;
import android.app.Dialog;
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
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.util.ArrayList;

import bigappcompany.com.santhe.R;
import bigappcompany.com.santhe.model.DataCategory;
import bigappcompany.com.santhe.model.MobileRegisterPojo;
import bigappcompany.com.santhe.model.PersonalisePojo;
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

/**
 * @author Samuel Robert <sam@spotsoon.com>
 * @created on 01 Aug 2017 at 3:08 PM
 */

public class PostActivity extends AppCompatActivity {

    private static final int CAMERA_REQUEST = 0;
    private TextView tvName, tvPost;
    private ImageView ivProfile;
    private EditText etDescription;
    private RelativeLayout rvCamera, rvGallery, rv_click;
    private int _Camera = 0, _Gallery = 1;
    private Uri fileUri;
    private String picturePath = "";
    private RetrofitDataProvider retrofitDataProvider;
    private Uri tempUri;
    private TextView etAddcaption;
    private ImageView ivImage;
    private ArrayList<DataCategory> dataPersonalise = new ArrayList<DataCategory>();
    private String selectedCategory;
    private String idToSend = "";
    private ListView listView;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Window window = this.getWindow();
        window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            window.setStatusBarColor(ContextCompat.getColor(this, R.color.colorGreen));
        }
        setContentView(R.layout.post_activity);

        retrofitDataProvider = new RetrofitDataProvider();

        CalligraphyConfig.initDefault(new CalligraphyConfig.Builder()
                .setDefaultFontPath("fonts/Pangram-Regular.otf")
                .setFontAttrId(R.attr.fontPath)
                .build()
        );
        initViews();
        eventListerns();

        if (NetworkUtil.getConnectivityStatusBoolen(PostActivity.this)) {
            hitCategory();
        } else {
            SantheUtility.displayMessageAlert(getResources().getString(R.string.network), PostActivity.this);

        }


        ActivityCompat.requestPermissions(PostActivity.this,
                new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, 1);
    }

    private void eventListerns() {

        rvCamera.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {


                if (ContextCompat.checkSelfPermission(PostActivity.this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED || ContextCompat.checkSelfPermission(PostActivity.this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                    ActivityCompat.requestPermissions(PostActivity.this, new String[]{Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE}, 110);
                } else {
                    CallCamera();
                }


            }
        });
        rvGallery.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (ContextCompat.checkSelfPermission(PostActivity.this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED || ContextCompat.checkSelfPermission(PostActivity.this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                    ActivityCompat.requestPermissions(PostActivity.this, new String[]{Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE}, 110);
                } else {
                    CallGallery();
                }
               


            }
        });
        rv_click.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                finish();
            }
        });

        tvPost.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (idToSend.length() > 0) {

                    try {
                        if (NetworkUtil.getConnectivityStatusBoolen(PostActivity.this)) {
                            hitApi();
                        } else {
                            SantheUtility.displayMessageAlert(getResources().getString(R.string.network), PostActivity.this);

                        }

                    } catch (Exception e) {

                        e.printStackTrace();
                    }
                } else {
                    Toast.makeText(getApplicationContext(), "Please select category", Toast.LENGTH_SHORT).show();
                }

            }
        });


        etAddcaption.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                popupforcategory();
            }
        });

    }

    protected void getIdOfMember() {

        for (int i = 0; i < dataPersonalise.size(); i++) {
            if (dataPersonalise.get(i).getCat_name().contains(selectedCategory) == true) {
                idToSend = dataPersonalise.get(i).getCat_id();
                Log.d("Test", "_idToSend" + idToSend);
            }
        }
    }

    private void hitCategory() {

        SantheUtility.showProgress(PostActivity.this, getResources().getString(R.string.pleaseWait));

        retrofitDataProvider.getCategories(new DownlodableCallback<PersonalisePojo>() {
            @Override
            public void onSuccess(PersonalisePojo result) {

                SantheUtility.dismissProgressDialog();

                if (result.getStatus().contentEquals("true")) {

                    dataPersonalise.clear();
                    dataPersonalise = result.getData();

                } else {
                    SantheUtility.displayMessageAlert(getResources().getString(R.string.NoData), PostActivity.this);
                }

            }

            @Override
            public void onFailure(String error) {

                SantheUtility.dismissProgressDialog();
                SantheUtility.displayMessageAlert(getResources().getString(R.string.something), PostActivity.this);

            }
        });
    }

    private void popupforcategory() {

//        final Dialog dialog = new Dialog(PostActivity.this);
        final Dialog dialog = new Dialog(PostActivity.this, android.R.style.Theme_Translucent_NoTitleBar);

//        final Dialog dialog = new Dialog(new ContextThemeWrapper(PostActivity.this,  R.style.full_screen_dialog));

        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.popup_categoty);
        dialog.setTitle(null);

        RelativeLayout submitButton = dialog.findViewById(R.id.rv_click);
        listView = dialog.findViewById(R.id.listView);
        setupCategoryList();

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {

                selectedCategory = dataPersonalise.get(position).getCat_name();
                etAddcaption.setText(selectedCategory);
                getIdOfMember();
                dialog.dismiss();
            }
        });


        submitButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                dialog.dismiss();

            }
        });


        dialog.show();
    }

    private void setupCategoryList() {

        String[] items = new String[dataPersonalise.size()];
        for (int i = 0; i < dataPersonalise.size(); i++) {

            items[i] = dataPersonalise.get(i).getCat_name();
        }
        listView.setAdapter(new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, items));
    }

    private void hitApi() {


        RequestBody requestPostDesc = RequestBody.create(MediaType.parse("multipart/form-data"), etDescription.getText().toString());
        RequestBody requestCategory = RequestBody.create(MediaType.parse("multipart/form-data"),
                idToSend);
        RequestBody requestUserId = RequestBody.create(MediaType.parse("multipart/form-data"),
                SharedPreferencesUtility.getAuthkey(PostActivity.this));

        MultipartBody.Part profileImage = null;

        if (tempUri != null) {
            File file = new File(getRealPathFromURI(tempUri));
            Log.i("Register", "Nombre del archivo " + file.getName());

            // create RequestBody instance from file
            RequestBody requestFile =
                    RequestBody.create(MediaType.parse("multipart/form-data"), file);
            // MultipartBody.Part is used to send also the actual file name
            profileImage = MultipartBody.Part.createFormData("post_img", file.getName(), requestFile);


        }

        SantheUtility.showProgress(PostActivity.this, getResources().getString(R.string.pleaseWait));
        retrofitDataProvider.postDetails(requestUserId, requestCategory, requestPostDesc, profileImage, new DownlodableCallback<MobileRegisterPojo>() {
            @Override
            public void onSuccess(MobileRegisterPojo result) {

                SantheUtility.dismissProgressDialog();
                if (result.getStatus().contains("0")) {
                    finish();
                } else {

                    SantheUtility.displayMessageAlert(getResources().getString(R.string.something), PostActivity.this);

                }

            }

            @Override
            public void onFailure(String error) {

                SantheUtility.dismissProgressDialog();
                SantheUtility.displayMessageAlert(getResources().getString(R.string.something), PostActivity.this);

            }
        });

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
                    Toast.makeText(PostActivity.this, "Permission denied to read your External storage", Toast.LENGTH_SHORT).show();
                }
                return;
            }
            // add other cases for more permissions
        }
    }


    private void CallGallery() {
        Intent GalleryIntent = new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        GalleryIntent.setType("image/*");
        startActivityForResult(GalleryIntent, _Gallery);
    }

    public String getRealPathFromURI(Uri uri) {
        Cursor cursor = getContentResolver().query(uri, null, null, null, null);
        cursor.moveToFirst();
        int idx = cursor.getColumnIndex(MediaStore.Images.ImageColumns.DATA);
        return cursor.getString(idx);
    }

    private void CallCamera() {

        if (!SantheUtility.isDeviceSupportCamera(this)) {
            SantheUtility.displayMessageAlert("Divece does not support camera.", this);
            Toast.makeText(getApplicationContext(), "Image is selected", Toast.LENGTH_SHORT).show();

        } else {
            captureImage();
        }
    }

    private void captureImage() {

//        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
//        fileUri = SantheUtility.getOutputMediaFileUri(1);
////        fileUri = FileProvider.getUriForFile(PostActivity.this, BuildConfig.APPLICATION_ID + ".provider",File);
//        if (fileUri != null) {
//            intent.putExtra(MediaStore.EXTRA_OUTPUT, fileUri);
//            startActivityForResult(intent, _Camera);
//        } else {
//            Toast.makeText(getApplicationContext(), "Device does not support", Toast.LENGTH_SHORT).show();
//
//        }
        Intent cameraIntent = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
        startActivityForResult(cameraIntent, CAMERA_REQUEST);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == CAMERA_REQUEST) {

            if (data == null) {
                return;
            } else {
                gotImageFromCamera(resultCode, data);
            }

            SantheUtility.dismissProgressDialog();
        } else if (requestCode == _Gallery && resultCode == RESULT_OK && null != data) {
            gotImageFromGallory(data);
            Glide.with(PostActivity.this).load(picturePath)
                    .into(ivImage);
            SantheUtility.dismissProgressDialog();
        } else if (requestCode == _Gallery && resultCode == RESULT_CANCELED) {
            Toast.makeText(getApplicationContext(), "Failed to load image", Toast.LENGTH_SHORT).show();
        }
    }

    private void gotImageFromCamera(int resultCode, Intent data) {
        if (resultCode == RESULT_OK) {

            if (data == null) {
                return;
            }
            try {
//                InputStream inputStream = getActivity().getContentResolver().openInputStream(data.getData());
//                Bitmap bitmap = BitmapFactory.decodeStream(inputStream);
                Bitmap bitmap = (Bitmap) data.getExtras().get("data");

                tempUri = getImageUri(PostActivity.this, bitmap);

                Glide.with(PostActivity.this).load(tempUri)
                        .into(ivImage);

            } catch (Exception e) {
                e.printStackTrace();
            }
        } else if (resultCode == RESULT_CANCELED) {
            Toast.makeText(getApplicationContext(), "Canceled image.", Toast.LENGTH_SHORT).show();

        } else {
            Toast.makeText(getApplicationContext(), "Failed to capture image", Toast.LENGTH_SHORT).show();

        }
    }

    private Bitmap previewCapturedImage() {
        try {
            BitmapFactory.Options options = new BitmapFactory.Options();
            options.inSampleSize = 8;
            picturePath = fileUri.getPath();
            final Bitmap bitmap = BitmapFactory.decodeFile(fileUri.getPath(), options);

            tempUri = getImageUri(getApplicationContext(), bitmap);


            return bitmap;
        } catch (NullPointerException e) {

            Toast.makeText(getApplicationContext(), "Failed to capture image.", Toast.LENGTH_SHORT).show();
            SantheUtility.dismissProgressDialog();
            e.printStackTrace();
        }
        return null;
    }

    public Uri getImageUri(Context inContext, Bitmap inImage) {
        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        inImage.compress(Bitmap.CompressFormat.JPEG, 100, bytes);
        String path = MediaStore.Images.Media.insertImage(inContext.getContentResolver(), inImage, "Title", null);
        return Uri.parse(path);
    }

    private void gotImageFromGallory(Intent data) {
        Uri selectedImage = data.getData();
        String[] filePathColumn = {MediaStore.Images.Media.DATA};

        Cursor cursor = this.getContentResolver().query(selectedImage, filePathColumn, null, null, null);
        if (cursor == null) {
            Toast.makeText(getApplicationContext(), "Failed to capture image.", Toast.LENGTH_SHORT).show();
        } else {
            cursor.moveToFirst();
            int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
            picturePath = cursor.getString(columnIndex);

            tempUri = getImageUri(getApplicationContext(), BitmapFactory.decodeFile(picturePath));

            cursor.close();

        }
    }

    private void initViews() {
        tvName = (TextView) findViewById(R.id.tvName);
        tvPost = (TextView) findViewById(R.id.tvPost);
        ivProfile = (ImageView) findViewById(R.id.ivProfile);
        etDescription = (EditText) findViewById(R.id.etDescription);
        rvCamera = (RelativeLayout) findViewById(R.id.rvCamera);
        rvGallery = (RelativeLayout) findViewById(R.id.rvGallery);
        rv_click = (RelativeLayout) findViewById(R.id.rv_click);
        ivImage = (ImageView) findViewById(R.id.ivImage);
        etAddcaption = (TextView) findViewById(R.id.etAddcaption);

        tvName.setText(SharedPreferencesUtility.getProfileName(this));
        Glide.with(this).load(SharedPreferencesUtility.getProfilePicture(this)).apply(RequestOptions.circleCropTransform()).into(ivProfile);
    }

    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(CalligraphyContextWrapper.wrap(newBase));
    }
}
