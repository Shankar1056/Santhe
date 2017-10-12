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
import java.util.ArrayList;

import bigappcompany.com.santhe.R;
import bigappcompany.com.santhe.model.DataCategory;
import bigappcompany.com.santhe.model.DiscussionDataIPojo;
import bigappcompany.com.santhe.model.MobileRegisterPojo;
import bigappcompany.com.santhe.model.PersonalisePojo;
import bigappcompany.com.santhe.network.DownlodableCallback;
import bigappcompany.com.santhe.network.RetrofitDataProvider;
import bigappcompany.com.santhe.utility.NetworkUtil;
import bigappcompany.com.santhe.utility.SantheUtility;
import bigappcompany.com.santhe.utility.SharedPreferencesUtility;
import uk.co.chrisjenx.calligraphy.CalligraphyConfig;
import uk.co.chrisjenx.calligraphy.CalligraphyContextWrapper;


public class PostEditActivity extends AppCompatActivity {

    private TextView tvName, tvPost, etAddcaption;
    private ImageView ivProfile;
    private EditText etDescription;
    private RelativeLayout rv_click;
    private int _Camera = 0, _Gallery = 1;
    private Uri fileUri;
    private String picturePath = "";
    private RetrofitDataProvider retrofitDataProvider;
    private Uri tempUri;

    private ImageView ivImage;
    private ArrayList<DataCategory> dataPersonalise = new ArrayList<DataCategory>();
    private String selectedCategory;
    private String idToSend = "";
    private DiscussionDataIPojo dataOfDetails = new DiscussionDataIPojo();
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
        setContentView(R.layout.post_edit_activity);

        retrofitDataProvider = new RetrofitDataProvider();

        if (getIntent().getExtras().getSerializable("postDetail") != null) {

            dataOfDetails = (DiscussionDataIPojo) getIntent().getExtras().getSerializable("postDetail");

        }

        CalligraphyConfig.initDefault(new CalligraphyConfig.Builder()
                .setDefaultFontPath("fonts/Pangram-Regular.otf")
                .setFontAttrId(R.attr.fontPath)
                .build()
        );
        initViews();
        eventListerns();


        if (NetworkUtil.getConnectivityStatusBoolen(PostEditActivity.this)) {
            hitCategory();
        } else {
            SantheUtility.displayMessageAlert(getResources().getString(R.string.network), PostEditActivity.this);

        }


        ActivityCompat.requestPermissions(PostEditActivity.this,
                new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, 1);
    }

    private void setValues() {


        etAddcaption.setText(dataOfDetails.getCategoryName());
        etDescription.setText(dataOfDetails.getDescription());
        idToSend = dataOfDetails.getCategory_id();


    }

    private void eventListerns() {

        rv_click.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                finish();
            }
        });

        tvPost.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                try {
                    if (NetworkUtil.getConnectivityStatusBoolen(PostEditActivity.this)) {
                        hitApi();
                    } else {
                        SantheUtility.displayMessageAlert(getResources().getString(R.string.network), PostEditActivity.this);
                    }

                } catch (Exception e) {
                    e.printStackTrace();

                }

            }
        });

//        etAddcaption.setOnItemClickListener(new AdapterView.OnItemClickListener() {
//            @Override
//            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
//
//                TextView customerName = view.findViewById(R.id.tvAutoText);
//                selectedCategory = customerName.getText().toString();
//                etAddcaption.setText(selectedCategory);
//                getIdOfMember(selectedCategory);
//
//
//            }
//        });
        etAddcaption.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                popupforcategory();
            }
        });

    }

    private void popupforcategory() {

//        final Dialog dialog = new Dialog(PostActivity.this);
        final Dialog dialog = new Dialog(PostEditActivity.this, android.R.style.Theme_Translucent_NoTitleBar);

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
                getIdOfMember(selectedCategory);
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

    protected String getIdOfMember(String selectedCategory) {

        for (int i = 0; i < dataPersonalise.size(); i++) {

            if (dataPersonalise.get(i).getCat_name().contains(this.selectedCategory) == true) {
                idToSend = dataPersonalise.get(i).getCat_id();
            }

        }

        return idToSend;
    }

    private void hitCategory() {

        retrofitDataProvider.getCategories(new DownlodableCallback<PersonalisePojo>() {
            @Override
            public void onSuccess(PersonalisePojo result) {

                SantheUtility.dismissProgressDialog();

                if (result.getStatus().contentEquals("true")) {




                    dataPersonalise.clear();
                    dataPersonalise = result.getData();
                    setValues();

//                    AutoTextAdaptor adapter = new AutoTextAdaptor(getApplicationContext(), dataPersonalise);
//                    etAddcaption.setAdapter(adapter);


                } else {
                    SantheUtility.dismissProgressDialog();
                    Toast.makeText(getApplicationContext(), result.getMessage(), Toast.LENGTH_SHORT).show();

                }

            }

            @Override
            public void onFailure(String error) {
                SantheUtility.dismissProgressDialog();
                Toast.makeText(getApplicationContext(), getResources().getString(R.string.something), Toast.LENGTH_SHORT).show();

            }
        });
    }

    private void hitApi() {
        SantheUtility.showProgress(PostEditActivity.this, getResources().getString(R.string.pleaseWait));
        retrofitDataProvider.editPostDiscussion(dataOfDetails.getDpId(), SharedPreferencesUtility.getAuthkey(PostEditActivity.this),
                idToSend, etDescription.getText().toString(), new DownlodableCallback<MobileRegisterPojo>() {
                    @Override
                    public void onSuccess(MobileRegisterPojo result) {

                        SantheUtility.dismissProgressDialog();


                        if (result.getStatus().contains("0")) {

                            finish();
                        } else {
                            SantheUtility.dismissProgressDialog();
                            SantheUtility.displayMessageAlert(getResources().getString(R.string.something), PostEditActivity.this);

                        }
                    }

                    @Override
                    public void onFailure(String error) {

                        SantheUtility.dismissProgressDialog();
                        SantheUtility.displayMessageAlert(getResources().getString(R.string.something), PostEditActivity.this);
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
                    Toast.makeText(PostEditActivity.this, "Permission denied to read your External storage", Toast.LENGTH_SHORT).show();
                }
                return;
            }
            // add other cases for more permissions
        }
    }


    private void CallGallery() {
        Intent GalleryIntent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
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
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        fileUri = SantheUtility.getOutputMediaFileUri(1);
        if (fileUri != null) {
            intent.putExtra(MediaStore.EXTRA_OUTPUT, fileUri);
            startActivityForResult(intent, _Camera);
        } else {
            Toast.makeText(getApplicationContext(), "Device does not support", Toast.LENGTH_SHORT).show();

        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == _Camera) {
            gotImageFromCamera(resultCode);
            Glide.with(PostEditActivity.this).load(picturePath)
                    .into(ivImage);
            SantheUtility.dismissProgressDialog();
        } else if (requestCode == _Gallery && resultCode == RESULT_OK && null != data) {
            gotImageFromGallory(data);
            Glide.with(PostEditActivity.this).load(picturePath)
                    .into(ivImage);
            SantheUtility.dismissProgressDialog();
        } else if (requestCode == _Gallery && resultCode == RESULT_CANCELED) {
            Toast.makeText(getApplicationContext(), "Failed to load image", Toast.LENGTH_SHORT).show();
        }
    }

    private void gotImageFromCamera(int resultCode) {
        if (resultCode == RESULT_OK) {
            Bitmap bitmap = previewCapturedImage();
            if (bitmap != null) {

                SantheUtility.dismissProgressDialog();

            } else {
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
