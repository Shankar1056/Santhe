package bigappcompany.com.santhe.activity;

import android.Manifest;
import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Bundle;
import android.provider.Settings;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.graphics.drawable.DrawerArrowDrawable;
import android.support.v7.widget.AppCompatCheckBox;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.Window;
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

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import bigappcompany.com.santhe.R;
import bigappcompany.com.santhe.adapter.AutoTextAdaptor;
import bigappcompany.com.santhe.model.DataCategory;
import bigappcompany.com.santhe.model.MobileRegisterPojo;
import bigappcompany.com.santhe.model.PersonalisePojo;
import bigappcompany.com.santhe.model.ProduuctDetailDataPojo;
import bigappcompany.com.santhe.network.DownlodableCallback;
import bigappcompany.com.santhe.network.RetrofitDataProvider;
import bigappcompany.com.santhe.utility.NetworkUtil;
import bigappcompany.com.santhe.utility.SantheUtility;
import bigappcompany.com.santhe.utility.SharedPreferencesUtility;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;

import static android.content.pm.PackageManager.PERMISSION_GRANTED;


public class EditProductActivity extends Activity implements LocationListener {

    private static final int REQUEST_CODE_PICTURE = 1;
    private static final int PICK_IMAGE_MULTIPLE = 2;
    private static final int LOCATION_DATA = 3;
    private static final int RC_PERM = 1024;
    List<MultipartBody.Part> profileImage = new ArrayList<MultipartBody.Part>();

    private DrawerArrowDrawable drawerArrowDrawable;
    private RelativeLayout rvToggle, rvSearch;
    private Spinner sCategory, sUnit;
    private AutoCompleteTextView sItemName;
    private String[] SItemNameItem = {"Wheat", "Rye", "Barley", "barley malt", "Bran", "Bulgar", "Couscous", "Farina", "Kamut"};
    private String[] SunitItem = {"Select Unit", "litre", "ton", "kg", "grams"};


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
    private ProduuctDetailDataPojo produuctDetailDataPojo = new ProduuctDetailDataPojo();
    private String currentLat = "", currentLong = "";
    private ListView listView;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.edit_productdetail);

        retrofitDataProvider = new RetrofitDataProvider();
        initViews();
        eventListners();
        produuctDetailDataPojo = (ProduuctDetailDataPojo) getIntent().getExtras().getSerializable("productDetail");

        ActivityCompat.requestPermissions(this,
                new String[]{Manifest.permission.CAMERA, Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.READ_EXTERNAL_STORAGE}, 1);

        locationUpdate();


        try {
            if (NetworkUtil.getConnectivityStatusBoolen(this)) {
                hitCategory();
            } else {
                SantheUtility.displayMessageAlert(getResources().getString(R.string.network), this);
            }

        } catch (Exception e) {
            e.printStackTrace();
            SantheUtility.dismissProgressDialog();

        }


    }


    private void getLocation() {

        if (mCurrentLocation != null) {
            try {
                Geocoder geo = new Geocoder(this, Locale.getDefault());
                List<Address> addresses = geo.getFromLocation(
                        mCurrentLocation.latitude, mCurrentLocation.longitude, 1
                );

                Log.e("Test", "logitute :" + mCurrentLocation.longitude + "" + mCurrentLocation.latitude);

                if (addresses.size() > 0) {
                    Address address = addresses.get(0);

                    ArrayList<String> addressFragments = new ArrayList<>();
                    for (int i = 0; i < address.getMaxAddressLineIndex(); i++) {
                        addressFragments.add(address.getAddressLine(i));

                    }
                    String addres = TextUtils.join(System.getProperty("line.separator"),
                            addressFragments);
                    String newString = addres.replace("\n", " ");


                    tvLocation.setText(getAdress(mCurrentLocation));

//                    currentLat = String.valueOf(mCurrentLocation.latitude);
//
//                    currentLong = String.valueOf(mCurrentLocation.longitude);

                    Log.e("Test", "logitute :" + newString);


                } else {
                    Toast.makeText(this, R.string.no_location, Toast.LENGTH_SHORT).show();
                }
            } catch (Exception e) {
                Log.e("Exception", e.getMessage(), e);
            }

        } else {
            Toast.makeText(this, R.string.please_choose_location, Toast.LENGTH_SHORT).show();
        }
    }

    public void locationUpdate() {
        // check permission and request if not granted
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                != PERMISSION_GRANTED &&
                ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION)
                        != PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this,
                    new String[]{
                            Manifest.permission.ACCESS_FINE_LOCATION,
                            Manifest.permission.ACCESS_COARSE_LOCATION
                    },
                    RC_PERM
            );
            return;

        }

        locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);

        if (!locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER)) {
            // notify user
            AlertDialog.Builder dialog = new AlertDialog.Builder(this);
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
        LocationManager locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 5000, 100, this);

        // Can we at least get network location?
        Location location = locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
        if (location == null) {
            // If cannot get GPS go for Network Provider's Location service
            locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 5000, 100, this);
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == LOCATION_DATA && resultCode == RESULT_OK) {

            String returnString = data.getStringExtra("Address");
            tvLocation.setText(returnString);
            currentLat = "";
            currentLong = "";
            currentLat = data.getStringExtra("lat");
            currentLong = data.getStringExtra("long");
            Log.d("selectedImage", "lat from maps" + data.getStringExtra("lat") + "long" + data.getStringExtra("long"));

            Log.d("selectedImage", "lat from maps" + currentLat + "long" + currentLong);
        }
    }

    private void hitSellApi() {

        SantheUtility.showProgress(EditProductActivity.this, getResources().getString(R.string.pleaseWait));
        Log.d("selectedImage", "lat from maps" + currentLat + "long" + currentLong);

        String user_id = SharedPreferencesUtility.getAuthkey(EditProductActivity.this);
        retrofitDataProvider.UpdateProductForPost(produuctDetailDataPojo.getProductId(), sItemName.getText().toString(),
                unitSelectedText, user_id, cbNegotiableSelected, etPrice.getText().toString(), idToSend,
                etWeightOfItem.getText().toString(), currentLong, currentLat, new DownlodableCallback<MobileRegisterPojo>() {
                    @Override
                    public void onSuccess(MobileRegisterPojo result) {
                        SantheUtility.dismissProgressDialog();
                        if (result.getStatus().contains("0")) {

                            Intent intent = new Intent(EditProductActivity.this, DetailsActivity.class);
                            intent.putExtra("productId", produuctDetailDataPojo.getProductId());
                            intent.putExtra("From", "MyUpdateFragment");
                            startActivity(intent);
                            finish();


                        } else {
                            SantheUtility.dismissProgressDialog();
                            SantheUtility.displayMessageAlert(result.getMessage(), EditProductActivity.this);

                        }

                    }

                    @Override
                    public void onFailure(String error) {

                        SantheUtility.dismissProgressDialog();
                        SantheUtility.displayMessageAlert(getResources().getString(R.string.something), EditProductActivity.this);

                    }
                });
    }

    private void hitCategory() {

        SantheUtility.showProgress(this, getResources().getString(R.string.pleaseWait));

        retrofitDataProvider.getCategories(new DownlodableCallback<PersonalisePojo>() {
            @Override
            public void onSuccess(PersonalisePojo result) {

                SantheUtility.dismissProgressDialog();

                if (result.getStatus().contentEquals("true")) {

                    dataPersonalise.clear();
                    dataPersonalise = result.getData();


//                    AutoTextAdaptor adapter = new AutoTextAdaptor(EditProductActivity.this, dataPersonalise);
//                    etCategory.setAdapter(adapter);

                    setValues();

                } else {

                    SantheUtility.displayMessageAlert(result.getMessage(), EditProductActivity.this);
                }

            }

            @Override
            public void onFailure(String error) {

                SantheUtility.dismissProgressDialog();
                SantheUtility.displayMessageAlert(getResources().getString(R.string.something), EditProductActivity.this);

            }
        });
    }

    private void setValues() {

        sItemName.setText(produuctDetailDataPojo.getProductName());
        etPrice.setText(produuctDetailDataPojo.getPricePerKg());

        etWeightOfItem.setText(produuctDetailDataPojo.getTotalQuantity());
        idToSend = produuctDetailDataPojo.getCategory_id();
        unitSelectedText = produuctDetailDataPojo.getQuantity();
        cbNegotiableSelected = produuctDetailDataPojo.getNegotiable();
        etCategory.setText(produuctDetailDataPojo.getCategory());

        if (produuctDetailDataPojo.getNegotiable().contains("yes")) {
            cbNegotiable.setEnabled(true);

        } else {
            cbNegotiable.setEnabled(false);
        }

        sUnit.setSelection(((ArrayAdapter<String>) sUnit.getAdapter()).getPosition(produuctDetailDataPojo.getQuantity()));

        currentLat = produuctDetailDataPojo.getLatitude();
        currentLong = produuctDetailDataPojo.getLongitude();

        mCurrentLocation = new LatLng(Double.parseDouble(currentLat), Double.parseDouble(currentLong));

//        Geocoder geo = new Geocoder(this, Locale.getDefault());
//        List<Address> addresses = null;
//        try {
//            addresses = geo.getFromLocation(
//                    Double.parseDouble(currentLat), Double.parseDouble(currentLong), 1
//            );
//
//
//            Log.d("Test","newString"+Double.parseDouble(currentLat)+Double.parseDouble(currentLong));
//
//            if (addresses.size() > 0) {
//
//                Address address = addresses.get(0);
//
//                ArrayList<String> addressFragments = new ArrayList<>();
//                for (int i = 0; i < address.getMaxAddressLineIndex(); i++) {
//                    addressFragments.add(address.getAddressLine(i));
//                }
//                String addres = TextUtils.join(System.getProperty("line.separator"),
//                        addressFragments);
//                String newString = addres.replace("\n", " ");
//
//                Log.d("Test","newString"+newString);
//
//
//
//                tvLocation.setText(newString);
//            }
//
//        } catch (IOException e) {
//            e.printStackTrace();
//            Log.d("Test","exception"+e.getMessage());
//        }
        getLocation();


    }

    private String getAdress(LatLng finalLocation) {

        Geocoder geocoder;
        List<Address> addresses = null;
        geocoder = new Geocoder(this, Locale.getDefault());

        try {
            addresses = geocoder.getFromLocation(finalLocation.latitude, finalLocation.longitude, 1); // Here 1 represent max location result to returned, by documents it recommended 1 to 5
        } catch (IOException e) {
            e.printStackTrace();
        }

        String address = addresses.get(0).getAddressLine(0); // If any additional address line present than only, check with max available address lines by getMaxAddressLineIndex()
        String city = addresses.get(0).getLocality();
        String state = addresses.get(0).getLocality();
        String country = addresses.get(0).getCountryName();
        String postalCode = addresses.get(0).getPostalCode();
        String knownName = addresses.get(0).getFeatureName();

        return state;
    }


    private void spinnerClick() {


        ArrayAdapter<String> sUnitData = new ArrayAdapter<String>(this, R.layout.list_spinner, SunitItem);
        sUnit.setAdapter(sUnitData);


        itemNameList.clear();

        for (int i = 0; i < SItemNameItem.length; i++) {

            DataCategory dataCategory = new DataCategory();
            dataCategory.setCat_name(SItemNameItem[i]);
            dataCategory.setCat_name(SItemNameItem[i]);
            itemNameList.add(dataCategory);

        }

        AutoTextAdaptor adapter = new AutoTextAdaptor(this, itemNameList);
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

                    if (NetworkUtil.getConnectivityStatusBoolen(EditProductActivity.this)) {
                        hitSellApi();
                    } else {
                        SantheUtility.displayMessageAlert(getResources().getString(R.string.network), EditProductActivity.this);
                    }

                } catch (Exception e) {
                    e.printStackTrace();
                    SantheUtility.dismissProgressDialog();
                    Log.d("PJ", "e" + e.getMessage());
                }
            }
        });

        sItemName.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {

                TextView customerName = view.findViewById(R.id.tvAutoText);
                selectedItemName = customerName.getText().toString();
                sItemName.setText(selectedItemName);
            }
        });

//        etCategory.setOnItemClickListener(new AdapterView.OnItemClickListener() {
//            @Override
//            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
//
//                TextView customerName = view.findViewById(R.id.tvAutoText);
//                selectedCategory = customerName.getText().toString();
//                etCategory.setText(selectedCategory);
//                getIdOfMember();
//
//            }
//        });

        etCategory.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                popupforcategory();
            }
        });

        rvToggle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                finish();

            }
        });


        rvSearch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent intent = new Intent(EditProductActivity.this, MapActivity.class);
                startActivityForResult(intent, LOCATION_DATA);
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

    }


    private void popupforcategory() {

        final Dialog dialog = new Dialog(this, android.R.style.Theme_Translucent_NoTitleBar);

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
        listView.setAdapter(new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, items));
    }

    protected void getIdOfMember() {

        for (int i = 0; i < dataPersonalise.size(); i++) {

            if (dataPersonalise.get(i).getCat_name().contains(selectedCategory) == true) {

                idToSend = dataPersonalise.get(i).getCat_id();


            }

        }

    }


    private void initViews() {
        rvToggle = findViewById(R.id.rv_click);
        rvSearch = findViewById(R.id.rvSearch);
        etWeightOfItem = findViewById(R.id.etWeightOfItem);
        etCategory = findViewById(R.id.etCategory);
        sItemName = findViewById(R.id.autoItemName);
        sUnit = findViewById(R.id.sUnit);
        tvLocation = findViewById(R.id.tvLoc);
        btnSell = findViewById(R.id.btnSell);
        cbNegotiable = findViewById(R.id.cbNegotiable);
        etPrice = findViewById(R.id.etPrice);
        etWeightOfItem = findViewById(R.id.etWeightOfItem);

        spinnerClick();
//        SpannableStringBuilder sb = new SpannableStringBuilder("Enter Weight of item [optional]");
//        CharacterStyle cs = new ForegroundColorSpan(getResources().getColor(R.color.colorOrange));
//        CharacterStyle cs1 = new ForegroundColorSpan(getResources().getColor(R.color.colorText));
//        sb.setSpan(cs, 21, 31, 0);
//        sb.setSpan(cs1, 0, 20, 0);
//        sb.setSpan(new AbsoluteSizeSpan(30), 21, 31, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);//resize size
//        etWeightOfItem.setHint(sb);
    }


    @Override
    public void onLocationChanged(Location location) {

        Log.d("Latitude", "status" + location.getLatitude() + location.getLongitude());

        mCurrentLocation = new LatLng(location.getLatitude(), location.getLongitude());

        Log.d("selectedImage", "previous" + mCurrentLocation.latitude + mCurrentLocation.longitude);

//        getLocation();
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
