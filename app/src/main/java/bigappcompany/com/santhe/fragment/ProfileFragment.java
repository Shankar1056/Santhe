package bigappcompany.com.santhe.fragment;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.graphics.drawable.DrawerArrowDrawable;
import android.support.v7.widget.CardView;
import android.text.InputType;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.InputStream;

import bigappcompany.com.santhe.R;
import bigappcompany.com.santhe.activity.HomeScreenActivity;
import bigappcompany.com.santhe.activity.PersonaliseActivity;
import bigappcompany.com.santhe.model.MobileRegisterPojo;
import bigappcompany.com.santhe.model.ProfilePojo;
import bigappcompany.com.santhe.network.DownlodableCallback;
import bigappcompany.com.santhe.network.RetrofitDataProvider;
import bigappcompany.com.santhe.utility.NetworkUtil;
import bigappcompany.com.santhe.utility.SantheUtility;
import bigappcompany.com.santhe.utility.SharedPreferencesUtility;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;


public class ProfileFragment extends Fragment {

    private static final int REQUEST_CODE_PICTURE = 1;
    private View rootView;
    private DrawerArrowDrawable drawerArrowDrawable;
    private float offset;
    private boolean flipped;
    private DrawerLayout drawer;
    private RelativeLayout rvToggle;
    private ImageView ivProfileImage, ivBackground, ivEdit, ivProfileEdit;
    private EditText etvName, etvEmail;
    private CardView cvPersonalise;
    private TextView tvName, tvEmail, tvNumber, tvCategories;
    private RetrofitDataProvider retrofitDataProvider;
    private boolean editBoolean = true;
    private Uri tempUri;
    private String oldimage = "";
    private RelativeLayout rvCategory;
    private Uri picUri;
    private Bitmap bitmap;


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        rootView = inflater.inflate(R.layout.profile_fragment, null);
        initViews();
        eventListners();
        SetUpSlideMenu();
        SetUpDrawerLisener();
        retrofitDataProvider = new RetrofitDataProvider();

        if (NetworkUtil.getConnectivityStatusBoolen(getActivity())) {
            hitApi();
        } else {
            SantheUtility.displayMessageAlert(getResources().getString(R.string.network), getActivity());
        }

        return rootView;
    }

    @Override
    public void onResume() {
        super.onResume();
    }

    private void hitApi() {


        try {
            SantheUtility.showProgress(getActivity(), getResources().getString(R.string.pleaseWait));

            retrofitDataProvider.getProfileDetails(SharedPreferencesUtility.getAuthkey(getActivity()),
                    new DownlodableCallback<ProfilePojo>() {
                        @Override
                        public void onSuccess(ProfilePojo result) {

                            SantheUtility.dismissProgressDialog();

                            if (result.getStatus().contains("true")) {

                                Glide.with(getActivity()).load(result.getData().getProfileImage())
                                        .apply(RequestOptions.circleCropTransform()).into(ivProfileImage);
                                Glide.with(getActivity()).load(result.getData().getProfileImage())
                                        .into(ivBackground);

                                SharedPreferencesUtility.setProfileName(getActivity(), result.getData().getName());
                                SharedPreferencesUtility.setProfilePicture(getActivity(), result.getData().getProfileImage());

                                StringBuilder sb = new StringBuilder(result.getData().getName());
                                sb.setCharAt(0, Character.toUpperCase(sb.charAt(0)));
                                tvName.setText(sb);

                                tvEmail.setText(result.getData().getEmail());
                                tvNumber.setText(result.getData().getMobile());
                                tvCategories.setText(result.getData().getUser_categories());
                                etvName.setText(result.getData().getName());
                                etvEmail.setText(result.getData().getEmail());
                                oldimage = result.getData().getImage_name();

                                ((HomeScreenActivity) getActivity()).callUpdaeDetails();

                            } else {

                                Toast.makeText(getActivity(), getResources().getString(R.string.something), Toast.LENGTH_SHORT).show();

                            }

                        }

                        @Override
                        public void onFailure(String error) {

                            Toast.makeText(getActivity(), getResources().getString(R.string.something), Toast.LENGTH_SHORT).show();
                            SantheUtility.dismissProgressDialog();
                        }
                    });
        } catch (Exception e) {

            SantheUtility.dismissProgressDialog();


        }

    }

    private void eventListners() {

        rvToggle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                ((HomeScreenActivity) getActivity()).callBuyFragment();
            }
        });

        rvCategory.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent intent = new Intent(getActivity(), PersonaliseActivity.class);
                startActivity(intent);

            }
        });

        ivProfileEdit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (ContextCompat.checkSelfPermission(getActivity(), Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED || ContextCompat.checkSelfPermission(getActivity(), Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                    ActivityCompat.requestPermissions(getActivity(), new String[]{Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE}, 110);
                } else {
                    takePicture();
                }


            }
        });

        ivEdit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (!editBoolean) {

                    if (etvName.getText().toString().length() > 1) {
                        if (NetworkUtil.getConnectivityStatusBoolen(getActivity())) {

                            hideKeypad();
                            hitEditApi();

                        } else {
                            SantheUtility.displayMessageAlert(getResources().getString(R.string.network), getActivity());
                        }
                    } else {
                        SantheUtility.displayMessageAlert(getResources().getString(R.string.nameValid), getActivity());
                    }
                }

                setEditOrProfile();


            }
        });

    }

    private void takePicture() {

//        Intent pickIntent = new Intent();
//        pickIntent.setType("image/*");
//        pickIntent.setAction(Intent.ACTION_GET_CONTENT);
//
//        Intent takePhotoIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
//        String pickTitle = "Take or select a photo";
//        Intent chooserIntent = Intent.createChooser(pickIntent, pickTitle);
//        chooserIntent.putExtra(MediaStore.EXTRA_OUTPUT, new Intent[]{takePhotoIntent});
////        chooserIntent.putExtra(Intent.EXTRA_INITIAL_INTENTS, new Intent[]{takePhotoIntent});
//        chooserIntent.putExtra(Intent.EXTRA_INITIAL_INTENTS, new Intent[]{takePhotoIntent});

        Intent pickIntent = new Intent();
        pickIntent.setType("image/*");
        pickIntent.setAction(Intent.ACTION_GET_CONTENT);
        pickIntent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true);
        Intent takePhotoIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        String pickTitle = "Take or select a photo";
        Intent chooserIntent = Intent.createChooser(pickIntent, pickTitle);
        chooserIntent.putExtra(Intent.EXTRA_INITIAL_INTENTS, new Intent[]{takePhotoIntent});
        startActivityForResult(chooserIntent, REQUEST_CODE_PICTURE);


//        File file = getOutputMediaFile(1);
//        picUri = Uri.fromFile(file); // create
//        chooserIntent.putExtra(MediaStore.EXTRA_OUTPUT, picUri);
//
//        startActivityForResult(chooserIntent, REQUEST_CODE_PICTURE);
    }

    private void setEditOrProfile() {

        if (editBoolean) {

            ivProfileEdit.setVisibility(View.VISIBLE);
            etvName.setVisibility(View.VISIBLE);
            etvEmail.setVisibility(View.VISIBLE);
            tvName.setVisibility(View.INVISIBLE);
            tvEmail.setVisibility(View.INVISIBLE);
            ivEdit.setImageResource(R.drawable.done);
            editBoolean = !editBoolean;

        } else {

            ivProfileEdit.setVisibility(View.INVISIBLE);
            etvName.setVisibility(View.INVISIBLE);
            etvEmail.setVisibility(View.INVISIBLE);
            tvName.setVisibility(View.VISIBLE);
            tvEmail.setVisibility(View.VISIBLE);
            ivEdit.setImageResource(R.drawable.edit);


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
//                InputStream inputStream = getActivity().getContentResolver().openInputStream(data.getData());
//                Bitmap bitmap = BitmapFactory.decodeStream(inputStream);

                final String action = data.getAction();

                if (action == null) {

                    InputStream inputStream = null;
                    try {
                        inputStream = getActivity().getContentResolver().openInputStream(data.getData());
                    } catch (FileNotFoundException e) {
                        e.printStackTrace();
                    }
                    bitmap = BitmapFactory.decodeStream(inputStream);

                } else {
//                    isCamera = action.equals(MediaStore.ACTION_IMAGE_CAPTURE);
                    bitmap = (Bitmap) data.getExtras().get("data");
                }


//                Bitmap bitmap = (Bitmap) data.getExtras().get("data");

                tempUri = getImageUri(getActivity(), bitmap);

                File finalFile = new File(getRealPathFromURI(tempUri));

                Glide.with(this).load(tempUri).apply(RequestOptions.circleCropTransform()).into(ivProfileImage);
                Glide.with(this).load(tempUri).into(ivBackground);

                Log.d("selectedImage", "selectedImage" + finalFile.getAbsolutePath());

            } catch (Exception e) {
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


    private void hitEditApi() {

        RequestBody requestEmail = RequestBody.create(MediaType.parse("multipart/form-data"), etvEmail.getText().toString());
        RequestBody requestName = RequestBody.create(MediaType.parse("multipart/form-data"), etvName.getText().toString());
        RequestBody requestOldimage = RequestBody.create(MediaType.parse("multipart/form-data"), oldimage);
        RequestBody requestUserId = RequestBody.create(MediaType.parse("multipart/form-data"), SharedPreferencesUtility.getAuthkey(getActivity()));


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
        SantheUtility.showProgress(getActivity(), getResources().getString(R.string.pleaseWait));

        retrofitDataProvider.editCustomer(requestName, requestEmail, requestUserId, profileImage, requestOldimage,
                new DownlodableCallback<MobileRegisterPojo>() {
                    @Override
                    public void onSuccess(MobileRegisterPojo result) {
                        SantheUtility.dismissProgressDialog();


                        if (result.getStatus().contentEquals("0")) {
                            setEditOrProfile();
                            editBoolean = !editBoolean;
//                            tvName.setText(etvName.getText().toString());
//                            tvEmail.setText(etvEmail.getText().toString());

                            if (NetworkUtil.getConnectivityStatusBoolen(getActivity())) {
                                hitApi();
                            } else {
                                SantheUtility.displayMessageAlert(getResources().getString(R.string.network), getActivity());
                            }

                        } else {
                            Toast.makeText(getActivity(), "" + result.getMessage(), Toast.LENGTH_SHORT).show();
                        }


                    }

                    @Override
                    public void onFailure(String error) {

                        Toast.makeText(getActivity(), getResources().getString(R.string.something), Toast.LENGTH_SHORT).show();
                        SantheUtility.dismissProgressDialog();
                        editBoolean = true;

                    }
                });

    }

    private void hideKeypad() {

        InputMethodManager inputManager = (InputMethodManager) getActivity().getSystemService(getActivity().INPUT_METHOD_SERVICE);
        inputManager.hideSoftInputFromWindow(getActivity().getCurrentFocus().getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
    }

    public String getRealPathFromURI(Uri uri) {
        Cursor cursor = getActivity().getContentResolver().query(uri, null, null, null, null);
        cursor.moveToFirst();
        int idx = cursor.getColumnIndex(MediaStore.Images.ImageColumns.DATA);
        return cursor.getString(idx);
    }

    private void initViews() {

        rvToggle = rootView.findViewById(R.id.rv_click);
        ivProfileImage = rootView.findViewById(R.id.ivProfileImage);
        ivBackground = rootView.findViewById(R.id.ivBackground);
        tvName = rootView.findViewById(R.id.tvName);
        tvEmail = rootView.findViewById(R.id.tvEmail);
        tvNumber = rootView.findViewById(R.id.tvNumber);
        tvCategories = rootView.findViewById(R.id.tvCategories);
        ivEdit = rootView.findViewById(R.id.ivEdit);
        ivProfileEdit = rootView.findViewById(R.id.ivProfileEdit);
        etvName = rootView.findViewById(R.id.etvName);
        etvEmail = rootView.findViewById(R.id.etvEmail);
        cvPersonalise = rootView.findViewById(R.id.cvPersonalise);
        rvCategory = rootView.findViewById(R.id.rvCategory);
        tvName.setInputType(InputType.TYPE_TEXT_FLAG_CAP_SENTENCES);

    }

    private void SetUpSlideMenu() {
        if (isAdded()) {

            drawer = getActivity().findViewById(R.id.drawer_layout);
        }
        drawerArrowDrawable = new DrawerArrowDrawable(getActivity());
    }

    private void SetUpDrawerLisener() {
        drawer.setDrawerListener(new DrawerLayout.SimpleDrawerListener() {
            @Override
            public void onDrawerSlide(View drawerView, float slideOffset) {
                offset = slideOffset;

                if (slideOffset >= .995) {
                    flipped = true;

                } else if (slideOffset <= .005) {
                    flipped = false;

                }

            }
        });
    }

}
