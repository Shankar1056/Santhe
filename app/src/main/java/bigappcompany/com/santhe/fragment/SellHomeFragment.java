package bigappcompany.com.santhe.fragment;

import android.Manifest;
import android.app.Dialog;
import android.content.ClipData;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.provider.Settings;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AlertDialog;
import android.support.v7.graphics.drawable.DrawerArrowDrawable;
import android.support.v7.widget.AppCompatCheckBox;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.maps.model.LatLng;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import bigappcompany.com.santhe.R;
import bigappcompany.com.santhe.activity.HomeScreenActivity;
import bigappcompany.com.santhe.activity.MapActivity;
import bigappcompany.com.santhe.adapter.AutoTextAdaptor;
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

import static android.app.Activity.RESULT_OK;
import static android.content.pm.PackageManager.PERMISSION_GRANTED;

public class SellHomeFragment extends Fragment implements LocationListener {

    private static final int REQUEST_CODE_PICTURE = 1;
    private static final int PICK_IMAGE_MULTIPLE = 2;
    private static final int LOCATION_DATA = 3;
    private static final int RC_PERM = 1024;
    List<MultipartBody.Part> profileImage = new ArrayList<MultipartBody.Part>();
    private View rootView;
    private DrawerArrowDrawable drawerArrowDrawable;
    private float offset;
    private boolean flipped;
    private DrawerLayout drawer;
    private RelativeLayout rvToggle, rvDiscussion, rvSell, rvBuy, rvSelectPic, rvSearch;
    private Spinner sCategory, sUnit;
    private AutoCompleteTextView sItemName;

    private String[] SunitItem = {"Select Unit", "litre", "ton", "kg", "grams", "Piece"};
    private String[] SItemNameItem = {"Beans /Green Beans", "Bitter gourd", "Bottle gourd", "Brinjal", "Cabbage", "Capsicum", "Carrot", "Cauliflower", "Coriander leaves", "Cucumber", "Curryleaves", "Drum stick", "Garlic", "German Turnip", "Ginger", "Green chilly",
            "Lady's finger", "Lemon", "Onion", "Peas", "Potato", "Radish", "Pumpkin", "Raw plantain", "Snake gourd", "Tomato", "Coconut", "Beetroot", "Ivy Gourd", "Apple",
            "Banana", "Chikku", "Custard Apple", "Grapes", "Guava", "Muskmelon/Cantalope", "Mango", "Moosambi", "Orange", "Pomogranate", "Papaya", "Pineapple", "Watermelon", "Jackfruit", "Raw Mango", "Palm Fruit", "Black Pepper", "Cardamom", "Cinnamon",
            "Cloves", "Cumin Seeds", "Asafoetida", "Fenugreek", "Bay Leaf", "Raisins", "Tamarind", "Mustard", "Poppy Seeds", "Flax Seeds", "Tea Powder", "Coffee", "Arhar", "Green Gram", "Black gram", "Bengal gram", "Horse gram", "Peas", "Coconut", "Groundnut", "Cashewnut", "Almond", "Rice", "Poha/Beaten Rice", "Puffed Rice", "wheat", "Beaten wheat", "Ragi", "Jowar", "Corn", "Barley", "Oats", "Tapioca Root / Cassava", "Salt", "Sugar", "Jaggery"
    };

    private ImageView ivDropDown;
    private ArrayList<String> imagesPathList;
    private Bitmap bitmap;
    private Uri tempUri;
    private ArrayList<DataCategory> dataPersonalise = new ArrayList<DataCategory>();
    private ArrayList<DataCategory> itemNameList = new ArrayList<DataCategory>();
    private DataCategory dataCategory;
    private String selectedCategory = "", selectedItemName = "";
    private LocationManager locationManager;
    private LatLng mCurrentLocation;
    private TextView tvLocation, etCategory;
    private RetrofitDataProvider retrofitDataProvider;
    private Button btnSell;
    private String unitSelectedText = "";
    private AppCompatCheckBox cbNegotiable;
    private String cbNegotiableSelected = "no";
    private EditText etPrice, etWeightOfItem;
    private String idToSend = "";
    private RequestBody requestFile;
    private String imageEncoded;
    private ListView listView;
    private ImageView ivSelectedPicture;
    private String mCurrentPhotoPath;
    private boolean isCamera;


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        rootView = inflater.inflate(R.layout.sell_home_fragment, null);
        retrofitDataProvider = new RetrofitDataProvider();
        initViews();
        eventListners();
        SetUpSlideMenu();
        SetUpDrawerLisener();
        ActivityCompat.requestPermissions(getActivity(),
                new String[]{Manifest.permission.CAMERA, Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.READ_EXTERNAL_STORAGE}, 1);
        if (NetworkUtil.getConnectivityStatusBoolen(getActivity())) {
            locationUpdate();
        } else {
            SantheUtility.displayMessageAlert(getResources().getString(R.string.network), getActivity());
        }


        try {

            if (NetworkUtil.getConnectivityStatusBoolen(getActivity())) {
                hitCategory();
            } else {
                SantheUtility.displayMessageAlert(getResources().getString(R.string.network), getActivity());
            }

        } catch (Exception e) {
            e.printStackTrace();
            SantheUtility.dismissProgressDialog();
            Log.d("PJ", e.getMessage());
        }


        return rootView;
    }

    private void getLocation() {

        if (mCurrentLocation != null) {
            try {
                Geocoder geo = new Geocoder(getActivity(), Locale.getDefault());
                List<Address> addresses = geo.getFromLocation(
                        mCurrentLocation.latitude, mCurrentLocation.longitude, 1
                );

                if (addresses.size() > 0) {
                    Address address = addresses.get(0);

                    ArrayList<String> addressFragments = new ArrayList<>();
                    for (int i = 0; i < address.getMaxAddressLineIndex(); i++) {
                        addressFragments.add(address.getAddressLine(i));

                    }
                    String addres = TextUtils.join(System.getProperty("line.separator"),
                            addressFragments);
                    String newString = addres.replace("\n", " ");
                    newString = address.getAddressLine(0);


                    Log.d("Latitude", "status" + newString);

                    tvLocation.setText(newString);

                    SharedPreferencesUtility.setlatitude(getActivity(), "" + mCurrentLocation.latitude);
                    SharedPreferencesUtility.setlongitude(getActivity(), "" + mCurrentLocation.longitude);


                } else {
                    Toast.makeText(getActivity(), R.string.no_location, Toast.LENGTH_SHORT).show();
                }
            } catch (Exception e) {
                Log.e("Exception", e.getMessage(), e);
            }

        } else {
            Toast.makeText(getActivity(), R.string.please_choose_location, Toast.LENGTH_SHORT).show();
        }
    }

    public void locationUpdate() {
        // check permission and request if not granted
        if (ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_FINE_LOCATION)
                != PERMISSION_GRANTED &&
                ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_COARSE_LOCATION)
                        != PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(getActivity(),
                    new String[]{
                            Manifest.permission.ACCESS_FINE_LOCATION,
                            Manifest.permission.ACCESS_COARSE_LOCATION
                    },
                    RC_PERM
            );
            return;

        }

        locationManager = (LocationManager) getActivity().getSystemService(Context.LOCATION_SERVICE);

        if (!locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER)) {
            // notify user
            AlertDialog.Builder dialog = new AlertDialog.Builder(getActivity());
            dialog.setMessage(getString(R.string.gps_network_not_enabled));
            dialog.setPositiveButton(getString(R.string.open_location_settings), new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface paramDialogInterface, int paramInt) {

                    Intent myIntent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                    startActivity(myIntent);

                }
            });
            dialog.setNegativeButton(getString(R.string.cancel), null);
            dialog.show();
        }

        // GPS location request
        LocationManager locationManager = (LocationManager) getActivity().getSystemService(Context.LOCATION_SERVICE);
        locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 5000, 100, this);

        // Can we at least get network location?
        Location location = locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
        if (location == null) {
            // If cannot get GPS go for Network Provider's Location service
            locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 5000, 100, this);
        }
    }

    private void hitSellApi() {


        RequestBody quantity = RequestBody.create(MediaType.parse("multipart/form-data"), unitSelectedText);
        RequestBody productName = RequestBody.create(MediaType.parse("multipart/form-data"), sItemName.getText().toString());
        RequestBody price = RequestBody.create(MediaType.parse("multipart/form-data"), etPrice.getText().toString());
        RequestBody category = RequestBody.create(MediaType.parse("multipart/form-data"), idToSend);
        RequestBody totalQuantity = RequestBody.create(MediaType.parse("multipart/form-data"), etWeightOfItem.getText().toString());
        RequestBody lat = RequestBody.create(MediaType.parse("multipart/form-data"), SharedPreferencesUtility.getlatitude(getActivity()));
        RequestBody longitude = RequestBody.create(MediaType.parse("multipart/form-data"), SharedPreferencesUtility.getlongitude(getActivity()));
        RequestBody negotiable = RequestBody.create(MediaType.parse("multipart/form-data"), cbNegotiableSelected);
        RequestBody userId = RequestBody.create(MediaType.parse("multipart/form-data"), SharedPreferencesUtility.getAuthkey(getActivity()));

        if (isAllFilled()) {

            SantheUtility.showProgress(getActivity(), getResources().getString(R.string.pleaseWait));

            retrofitDataProvider.sellProduct(profileImage, productName, quantity, userId, negotiable,
                    price, category, totalQuantity, longitude, lat, new DownlodableCallback<MobileRegisterPojo>() {
                        @Override
                        public void onSuccess(MobileRegisterPojo result) {

                            SantheUtility.dismissProgressDialog();
                            if (result.getStatus().contains("0")) {
                                ((HomeScreenActivity) getActivity()).callFrgments(1);
                            } else {
                                Toast.makeText(getActivity(), "" + result.getMessage(), Toast.LENGTH_SHORT).show();
                            }

                        }

                        @Override
                        public void onFailure(String error) {

                            Toast.makeText(getActivity(), getResources().getString(R.string.something), Toast.LENGTH_SHORT).show();
                            SantheUtility.dismissProgressDialog();

                        }
                    });
        }
    }

    private boolean isAllFilled() {

        if (etCategory.getText().toString().trim().length() <= 0) {

            Toast.makeText(getActivity(), getResources().getString(R.string.selectCategory), Toast.LENGTH_SHORT).show();
            return false;
        } else if (sUnit.getSelectedItem().toString().contains("Select Unit") || unitSelectedText.length() < 0) {
            Toast.makeText(getActivity(), getResources().getString(R.string.selectUnit), Toast.LENGTH_SHORT).show();
            return false;

        } else if (etPrice.getText().toString().trim().length() <= 0) {
            Toast.makeText(getActivity(), getResources().getString(R.string.selectItemPrice), Toast.LENGTH_SHORT).show();
            return false;

        } else if (etWeightOfItem.getText().toString().trim().length() <= 0) {
            Toast.makeText(getActivity(), getResources().getString(R.string.selectWeight), Toast.LENGTH_SHORT).show();
            return false;
        }

        return true;

    }

    private void hitCategory() {
        SantheUtility.showProgress(getActivity(), getResources().getString(R.string.pleaseWait));
        retrofitDataProvider.getCategories(new DownlodableCallback<PersonalisePojo>() {
            @Override
            public void onSuccess(PersonalisePojo result) {

                SantheUtility.dismissProgressDialog();

                if (result.getStatus().contentEquals("true")) {

                    dataPersonalise.clear();
                    dataPersonalise = result.getData();

                } else {

                    Toast.makeText(getActivity(), result.getMessage(), Toast.LENGTH_SHORT).show();
                }

            }

            @Override
            public void onFailure(String error) {
                Toast.makeText(getActivity(), getResources().getString(R.string.something), Toast.LENGTH_SHORT).show();
                SantheUtility.dismissProgressDialog();

            }
        });
    }


    private void spinnerClick() {

        ArrayAdapter<String> sUnitData = new ArrayAdapter<String>(getActivity(), R.layout.list_spinner, SunitItem);
        sUnit.setAdapter(sUnitData);

        itemNameList.clear();

        for (int i = 0; i < SItemNameItem.length; i++) {

            DataCategory dataCategory = new DataCategory();
            dataCategory.setCat_name(SItemNameItem[i]);
            dataCategory.setCat_name(SItemNameItem[i]);
            itemNameList.add(dataCategory);

        }

        AutoTextAdaptor adapter = new AutoTextAdaptor(getActivity(), itemNameList);
        sItemName.setAdapter(adapter);

    }


    private void eventListners() {

        cbNegotiable.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (cbNegotiable.isChecked()) {
                    cbNegotiableSelected = "yes";
                } else {

                    cbNegotiableSelected = "no";

                }
            }
        });

        btnSell.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    if (NetworkUtil.getConnectivityStatusBoolen(getActivity())) {
                        hitSellApi();
                    } else {
                        SantheUtility.displayMessageAlert(getResources().getString(R.string.network), getActivity());
                    }

                } catch (Exception e) {
                    e.printStackTrace();
                    Log.d("PJ", "e" + e.getMessage());
                }
            }
        });

        sItemName.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {

                hidesoftKey();
                TextView customerName = view.findViewById(R.id.tvAutoText);
                selectedItemName = customerName.getText().toString();
                sItemName.setText(selectedItemName);
            }
        });

        etCategory.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                popupforcategory();
            }
        });


        rvToggle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DrawerMenuStatus();
            }
        });

        rvSell.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                ((HomeScreenActivity) getActivity()).callSellFragment();
            }
        });
        rvBuy.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                ((HomeScreenActivity) getActivity()).callBuyFragment();
            }
        });

        rvDiscussion.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                ((HomeScreenActivity) getActivity()).callDiscussFragment();
            }
        });


        rvSearch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (NetworkUtil.getConnectivityStatusBoolen(getActivity())) {
                    Intent intent = new Intent(getActivity(), MapActivity.class);
                    startActivityForResult(intent, LOCATION_DATA);
                } else {
                    SantheUtility.displayMessageAlert(getResources().getString(R.string.network), getActivity());
                }

            }
        });

        sUnit.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {

                unitSelectedText = sUnit.getSelectedItem().toString();
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });


        rvSelectPic.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (ContextCompat.checkSelfPermission(getActivity(), Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED || ContextCompat.checkSelfPermission(getActivity(), Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                    ActivityCompat.requestPermissions(getActivity(), new String[]{Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE}, 110);
                } else {
                    takePicture();
                }


            }
        });


    }

    private void takePicture() {

        Intent pickIntent = new Intent();
        pickIntent.setType("image/*");
        pickIntent.setAction(Intent.ACTION_GET_CONTENT);
        pickIntent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true);
        Intent takePhotoIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        String pickTitle = "Take or select a photo";
        Intent chooserIntent = Intent.createChooser(pickIntent, pickTitle);
        chooserIntent.putExtra(Intent.EXTRA_INITIAL_INTENTS, new Intent[]{takePhotoIntent});
        startActivityForResult(chooserIntent, REQUEST_CODE_PICTURE);

//        if (takePhotoIntent.resolveActivity(getActivity().getPackageManager()) != null) {
//            // Create the File where the photo should go
//            File photoFile = null;
//            try {
//                photoFile = createImageFile();
//            } catch (IOException ex) {
//                // Error occurred while creating the File
//                Log.i("somthing", "IOException");
//            }
//            // Continue only if the File was successfully created
//            if (photoFile != null) {
//                takePhotoIntent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(photoFile));
//                Intent chooserIntent = Intent.createChooser(pickIntent, pickTitle);
//                chooserIntent.putExtra(Intent.EXTRA_INITIAL_INTENTS, new Intent[]{takePhotoIntent});
//                startActivityForResult(chooserIntent, REQUEST_CODE_PICTURE);
//
//            }
//        }
    }

    private void popupforcategory() {

        final Dialog dialog = new Dialog(getActivity(), android.R.style.Theme_Translucent_NoTitleBar);

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
                etCategory.setText(selectedCategory);
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
        listView.setAdapter(new ArrayAdapter<String>(getActivity(), android.R.layout.simple_list_item_1, items));
    }

    private void hidesoftKey() {

        InputMethodManager inputManager = (InputMethodManager)
                getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
        inputManager.hideSoftInputFromWindow(getActivity().getCurrentFocus().getWindowToken(),
                InputMethodManager.HIDE_NOT_ALWAYS);
    }


    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == 5) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // Now user should be able to use camera
//                Intent takePicture = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
//                startActivityForResult(takePicture, 0);
//
//                Intent pickPhoto = new Intent(Intent.ACTION_PICK,
//                        android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
//                startActivityForResult(pickPhoto, 1);


            } else {
                // Your app will not have this permission. Turn off all functions
                // that require this permission or it will force close like your
                // original question
            }
        }
    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == LOCATION_DATA && resultCode == RESULT_OK) {

            String returnString = data.getStringExtra("Address");
            tvLocation.setText(returnString);


            SharedPreferencesUtility.setlatitude(getActivity(), data.getStringExtra("lat"));
            SharedPreferencesUtility.setlongitude(getActivity(), data.getStringExtra("long"));

            Log.d("selectedImage", "lat" + tvLocation.getText().toString());
            Log.d("selectedImage", "lat" + SharedPreferencesUtility.getlatitude(getActivity()));
            Log.d("selectedImage", "long" + SharedPreferencesUtility.getlongitude(getActivity()));

        } else if (requestCode == REQUEST_CODE_PICTURE && resultCode == RESULT_OK) {

            ClipData clipData = data.getClipData();

            if (clipData == null) {
                final String action = data.getAction();

                if (action == null) {
                    isCamera = false;
                    InputStream inputStream = null;
                    try {
                        inputStream = getActivity().getContentResolver().openInputStream(data.getData());
                    } catch (FileNotFoundException e) {
                        e.printStackTrace();
                    }
                    bitmap = BitmapFactory.decodeStream(inputStream);

                } else {

                    bitmap = (Bitmap) data.getExtras().get("data");
                }


                ivSelectedPicture.setImageBitmap(bitmap);
                tempUri = getImageUri(getActivity(), bitmap);
                File finalFile = new File(getRealPathFromURI(tempUri));
                profileImage.clear();
                RequestBody requestFile =
                        RequestBody.create(MediaType.parse("multipart/form-data"), finalFile);

                profileImage.add(MultipartBody.Part.createFormData("prod_img[]", finalFile.getName(), requestFile));

                Log.d("selectedImage", "selectedImage" + profileImage.size());


            } else {

                profileImage.clear();
                Log.d("clipdate", "second" + data.getClipData().getItemCount());
                System.out.println("++count" + data.getClipData().getItemCount());
                Uri selectedImage = null;
                Bitmap bitmap = null;

                ivSelectedPicture.setImageBitmap(bitmap);

                for (int i = 0; i < data.getClipData().getItemCount(); i++) {
                    selectedImage = data.getClipData().getItemAt(i).getUri();
                    //        Uri selectedImage1 = data.getData();
                    try {
                        bitmap = MediaStore.Images.Media.getBitmap(getActivity().getContentResolver(), selectedImage);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    tempUri = getImageUri(getActivity(), bitmap);

                    ivSelectedPicture.setImageBitmap(bitmap);

                    if (tempUri != null) {
                        File file = new File(getRealPathFromURI(tempUri));

                        Log.i("clipdate", "Nombre del archivo " + file.getName());
                        requestFile =
                                RequestBody.create(MediaType.parse("multipart/form-data"), file);
                        profileImage.add(MultipartBody.Part.createFormData("prod_img[]", file.getName(), requestFile));

                    }
                }
            }

        }

    }


    protected void getIdOfMember() {

        for (int i = 0; i < dataPersonalise.size(); i++) {

            if (dataPersonalise.get(i).getCat_name().contains(selectedCategory)) {
                idToSend = dataPersonalise.get(i).getCat_id();
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

        String[] projection = {MediaStore.Images.Media.DATA};
        Cursor cursor = getActivity().getContentResolver().query(uri, projection, null, null, null);
        if (cursor == null) return null;
        int column_index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
        cursor.moveToFirst();
        String s = cursor.getString(column_index);
        cursor.close();
        return s;

    }

    private void DrawerMenuStatus() {
        if (drawer.isDrawerVisible(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            drawer.openDrawer(GravityCompat.START);
        }
    }

    private void initViews() {
        rvToggle = rootView.findViewById(R.id.rv_click);
        rvDiscussion = rootView.findViewById(R.id.rvDiscussion);
        rvSell = rootView.findViewById(R.id.rvSell);
        rvBuy = rootView.findViewById(R.id.rvBuy);
        rvSelectPic = rootView.findViewById(R.id.rvSelectPic);
        rvSearch = rootView.findViewById(R.id.rvSearch);
        etWeightOfItem = rootView.findViewById(R.id.etWeightOfItem);
        etCategory = rootView.findViewById(R.id.etCategory);
        sItemName = rootView.findViewById(R.id.autoItemName);
        sUnit = rootView.findViewById(R.id.sUnit);
        tvLocation = rootView.findViewById(R.id.tvLoc);
        btnSell = rootView.findViewById(R.id.btnSell);
        cbNegotiable = rootView.findViewById(R.id.cbNegotiable);
        etPrice = rootView.findViewById(R.id.etPrice);
        etWeightOfItem = rootView.findViewById(R.id.etWeightOfItem);

        ivSelectedPicture = rootView.findViewById(R.id.ivSelectedPicture);
        tvLocation.setText(getResources().getString(R.string.Fetching));

        spinnerClick();
//        SpannableStringBuilder sb = new SpannableStringBuilder("Enter Weight of item [optional]");
//        CharacterStyle cs = new ForegroundColorSpan(getResources().getColor(R.color.colorOrange));
//        CharacterStyle cs1 = new ForegroundColorSpan(getResources().getColor(R.color.colorText));
//        sb.setSpan(cs, 21, 31, 0);
//        sb.setSpan(cs1, 0, 20, 0);
//        sb.setSpan(new AbsoluteSizeSpan(30), 21, 31, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);//resize size
//        etWeightOfItem.setHint(sb);
    }

    private void SetUpSlideMenu() {

        drawer = getActivity().findViewById(R.id.drawer_layout);
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

    @Override
    public void onLocationChanged(Location location) {
        Log.d("Latitude", "status" + location.getLatitude() + location.getLongitude());

        mCurrentLocation = new LatLng(location.getLatitude(), location.getLongitude());
        if (isAdded()) {
            getLocation();
        }
    }

    @Override
    public void onStatusChanged(String s, int i, Bundle bundle) {
        Log.d("Latitude", "status");
    }

    @Override
    public void onProviderEnabled(String s) {
        Log.d("Latitude", "disable");
    }

    @Override
    public void onProviderDisabled(String s) {
        Log.d("Latitude", "enabled");

    }
}
