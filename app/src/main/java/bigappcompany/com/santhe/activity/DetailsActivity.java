package bigappcompany.com.santhe.activity;

import android.Manifest;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Parcelable;
import android.provider.Settings;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.telephony.TelephonyManager;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PolylineOptions;
import com.viewpagerindicator.CirclePageIndicator;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.NavigableMap;
import java.util.TreeMap;

import bigappcompany.com.santhe.R;
import bigappcompany.com.santhe.model.MobileRegisterPojo;
import bigappcompany.com.santhe.model.ProductDetailPojo;
import bigappcompany.com.santhe.model.ProduuctDetailDataPojo;
import bigappcompany.com.santhe.network.DownlodableCallback;
import bigappcompany.com.santhe.network.RetrofitDataProvider;
import bigappcompany.com.santhe.utility.NetworkUtil;
import bigappcompany.com.santhe.utility.SantheUtility;
import uk.co.chrisjenx.calligraphy.CalligraphyConfig;
import uk.co.chrisjenx.calligraphy.CalligraphyContextWrapper;

import static android.content.pm.PackageManager.PERMISSION_GRANTED;

/**
 * @author Samuel Robert <sam@spotsoon.com>
 * @created on 01 Aug 2017 at 3:08 PM
 */

public class DetailsActivity extends AppCompatActivity implements OnMapReadyCallback, LocationListener {

    static final LatLng HAMBURG = new LatLng(53.558, 9.927);
    static final LatLng KIEL = new LatLng(53.551, 9.993);
    private static final int RC_PERM = 1024;
    private static final NavigableMap<Long, String> suffixes = new TreeMap<>();

    static {
        suffixes.put(1_000L, "k");
        suffixes.put(1_000_000L, "M");
        suffixes.put(1_000_000_000L, "B");
        suffixes.put(1_000_000_000_000L, "T");
        suffixes.put(1_000_000_000_000_000L, "Q");
        suffixes.put(1_000_000_000_000_000_000L, "E");
    }

    private CirclePageIndicator indicator;
    private ViewPager viewPager;
    private GoogleMap maps;
    private FloatingActionButton btnFab;
    private View mapView;
    private RelativeLayout rvClick;
    private LatLng mCurrentLocation;
    private String mycurrentLocationAddress = "";
    private String productId = "";
    private RetrofitDataProvider retrofitDataProvider;
    private double currentLat = 0, currentLong = 0;
    private ProductDetailPojo productDetailPojo = new ProductDetailPojo();
    private TextView tvCountImage, tvCategory, tvPostedOn, tvAmount, tvDistance, tvtotalWeight;
    private double distance = 0.0f;
    private ImageView ivDelete, ivEdit;
    private TextView tvNegotiable, tvHeading;
    private Marker currentLoc;
    private Marker FinalLoc;


    public static String format(long value) {

        if (value == Long.MIN_VALUE) return format(Long.MIN_VALUE + 1);
        if (value < 0) return "-" + format(-value);
        if (value < 1000) return Long.toString(value); //deal with easy case

        Map.Entry<Long, String> e = suffixes.floorEntry(value);
        Long divideBy = e.getKey();
        String suffix = e.getValue();

        long truncated = value / (divideBy / 10); //the number part of the output times 10
        boolean hasDecimal = truncated < 100 && (truncated / 10d) != (truncated / 10);
        return hasDecimal ? (truncated / 10d) + suffix : (truncated / 10) + suffix;
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Window window = this.getWindow();
        window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            window.setStatusBarColor(ContextCompat.getColor(this, R.color.colorGreen));
        }

        Bundle bundle = new Bundle();
        bundle = getIntent().getExtras();
        setContentView(R.layout.type_details);

        initViews();
        if (bundle != null) {

            if (bundle.containsKey("From")) {

                if (bundle.getString("From").contains("MyUpdateFragment")) {
                    ivDelete.setVisibility(View.VISIBLE);
                    ivEdit.setVisibility(View.VISIBLE);
                } else {
                    ivDelete.setVisibility(View.GONE);
                    ivEdit.setVisibility(View.GONE);
                }
            }
            productId = getIntent().getExtras().getString("productId");
        }


        retrofitDataProvider = new RetrofitDataProvider();

        eventListerns();


        CalligraphyConfig.initDefault(new CalligraphyConfig.Builder()
                .setDefaultFontPath("fonts/Pangram-Regular.otf")
                .setFontAttrId(R.attr.fontPath)
                .build()
        );

        Log.d("Test", "currentLat" + currentLat + "" + currentLong);
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    MapView mv = new MapView(getApplicationContext());
                    mv.onCreate(null);
                    mv.onPause();
                    mv.onDestroy();
                    mv.onLowMemory();
                } catch (Exception ignored) {

                }
            }
        }).start();


//        new Handler().postDelayed(new Runnable() {
//
//            @Override
//            public void run() {
//                if (!isFinishing()) {

                    SupportMapFragment maps = (SupportMapFragment) getSupportFragmentManager()
                            .findFragmentById(R.id.map);
                    maps.getMapAsync(DetailsActivity.this);
                    mapView = maps.getView();

//                }
//            }
//        }, 1000);


    }

    @Override
    protected void onResume() {
        super.onResume();

        if (NetworkUtil.getConnectivityStatusBoolen(DetailsActivity.this)) {

            hitApi();
        } else {
            SantheUtility.displayMessageAlert(getResources().getString(R.string.network), DetailsActivity.this);

        }


    }

    private void hitApi() {

        try {

            SantheUtility.showProgress(DetailsActivity.this, getResources().getString(R.string.pleaseWait));
            retrofitDataProvider.getProductDetail(productId, new DownlodableCallback<ProductDetailPojo>() {
                @Override
                public void onSuccess(ProductDetailPojo result) {

                    SantheUtility.dismissProgressDialog();

                    if (result.getStatus().contains("0")) {

                        productDetailPojo = result;
                        ProduuctDetailDataPojo datapojo = new ProduuctDetailDataPojo();
                        datapojo = result.getData();

                        if (datapojo.getImage().size() > 0) {

                            if (datapojo.getImage().size() <= 3) {
                                if (datapojo.getImage().size() == 1) {
                                    indicator.setVisibility(View.GONE);
                                    tvCountImage.setText("" + datapojo.getImage().size() + " image");

                                } else {
                                    indicator.setVisibility(View.VISIBLE);
                                    tvCountImage.setText("" + datapojo.getImage().size() + " images");
                                }

                            } else {

                                tvCountImage.setText("3" + " images");
                            }
                        }
                        tvCategory.setText(datapojo.getProductName());
                        tvPostedOn.setText(datapojo.getPostedDate());
                        tvHeading.setText("Posted by :" + " " + datapojo.getPostedBy());

                        if (datapojo.getNegotiable().contains("yes")) {

                            tvNegotiable.setText("Negotiable");
                        } else {

                            tvNegotiable.setText("Non-Negotiable");
                        }

                        try {
                            tvtotalWeight.setText(format(Long.parseLong(datapojo.getTotalQuantity().replaceAll(",", ""))));
                        } catch (Exception e) {
                            e.printStackTrace();
                        }

                        tvAmount.setText("Rs " + datapojo.getPricePerKg() + "/- per " + datapojo.getQuantity());

                        if (datapojo.getImage().size() > 0) {

                            final ProduuctDetailDataPojo Datapojo = datapojo;
                            new Handler().postDelayed(new Runnable() {

                                @Override
                                public void run() {

                                    ImageSliderAdapter homeViewPagerAdapter = new ImageSliderAdapter(DetailsActivity.this, Datapojo.getImage());
                                    viewPager.setAdapter(homeViewPagerAdapter);
                                    indicator.setViewPager(viewPager);
                                }
                            }, 1000);


                        }
                        if (mapView != null) {

                            new Handler().postDelayed(new Runnable() {
                                public void run() {

                                    if (currentLat > 0.0 || currentLat
                                            > 0.0) {
//
                                        setMapsDirection();
                                    }


                                }
                            }, 500);

                        }

                    } else {

                        SantheUtility.displayMessageAlert(getResources().getString(R.string.something), DetailsActivity.this);
                    }


                }

                @Override
                public void onFailure(String error) {

                    SantheUtility.displayMessageAlert(getResources().getString(R.string.something), DetailsActivity.this);

                    SantheUtility.dismissProgressDialog();

                }
            });
        } catch (Exception e) {

            e.printStackTrace();
            SantheUtility.dismissProgressDialog();

            Log.d("Test", "e." + e.getMessage());
        }
    }

    private void setMapsDirection() {


        maps.clear();

        if (productDetailPojo != null) {

            if (productDetailPojo.getStatus().contains("0")) {

                Log.d("Test", "valuesOFLatLong" + productDetailPojo.getData().getLatitude() + productDetailPojo.getData().getLongitude());


                if (productDetailPojo.getData().getLatitude().length() > 0 && productDetailPojo.getData().getLongitude().length() > 0) {

                    LatLng FinalLocation = new LatLng(Double.parseDouble(productDetailPojo.getData().getLatitude()), Double.parseDouble(productDetailPojo.getData().getLongitude()));

                    LatLng Currentlocation = new LatLng(currentLat, currentLong);

                    Log.d("Test", "currentLat" + currentLat + "" + currentLong);
                    Log.d("Test", "finalLat" + FinalLocation.longitude + "finalLog" + FinalLocation.latitude);


                    currentLoc = maps.addMarker(new MarkerOptions().position(Currentlocation)
                            .title("current location")
                    );

//                maps.moveCamera(CameraUpdateFactory.newLatLng(Currentlocation));
//                maps.animateCamera(CameraUpdateFactory.zoomTo(10), 2000, null);

                    FinalLoc = maps.addMarker(new MarkerOptions()
                            .position(FinalLocation)
                            .title("farmer location")
                    );
                    maps.moveCamera(CameraUpdateFactory.newLatLng(FinalLocation));
                    maps.animateCamera(CameraUpdateFactory.zoomTo(10), 2000, null);

                    PolylineOptions line =
                            new PolylineOptions().add(Currentlocation, FinalLocation)
                                    .width(5).color(Color.RED);

                    maps.addPolyline(line);


                    distance = distance(currentLat, currentLong, FinalLocation.latitude, FinalLocation.longitude);

                    try {

                        tvDistance.setText(String.format("%.2f", distance) + "km");

                    } catch (Exception e) {
                        e.printStackTrace();
                        Log.d("Test", "currentLat" + "message" + e.getMessage());
                        SantheUtility.dismissProgressDialog();

                    }
                } else {

                    Toast.makeText(getApplicationContext(), getResources().getString(R.string.noLocation), Toast.LENGTH_SHORT).show();


                }

                Log.d("Test", "currentLat" + "distance" + distance);

                SantheUtility.dismissProgressDialog();
            } else {

                Toast.makeText(getApplicationContext(), getResources().getString(R.string.something), Toast.LENGTH_SHORT).show();
                SantheUtility.dismissProgressDialog();
            }
        } else {

            Toast.makeText(getApplicationContext(), getResources().getString(R.string.something), Toast.LENGTH_SHORT).show();
            SantheUtility.dismissProgressDialog();
        }

        SantheUtility.dismissProgressDialog();

        mapView.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                switch (event.getAction()) {
                    case MotionEvent.ACTION_MOVE:
                        mapView.getParent().requestDisallowInterceptTouchEvent(true);
                        break;
                    case MotionEvent.ACTION_UP:
                    case MotionEvent.ACTION_CANCEL:
                        mapView.getParent().requestDisallowInterceptTouchEvent(false);
                        break;
                }
                return mapView.onTouchEvent(event);
            }
        });

        mapView.invalidate();

        SantheUtility.dismissProgressDialog();

    }

    private double distance(double lat1, double lon1, double lat2, double lon2) {
        double theta = lon1 - lon2;
        double dist = Math.sin(deg2rad(lat1))
                * Math.sin(deg2rad(lat2))
                + Math.cos(deg2rad(lat1))
                * Math.cos(deg2rad(lat2))
                * Math.cos(deg2rad(theta));
        dist = Math.acos(dist);
        dist = rad2deg(dist);
        dist = dist * 60 * 1.1515;
        return (dist);
    }

    private double deg2rad(double deg) {
        return (deg * Math.PI / 180.0);
    }

    private double rad2deg(double rad) {
        return (rad * 180.0 / Math.PI);
    }



    private void eventListerns() {


        rvClick.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        btnFab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {


                if (((TelephonyManager) getSystemService(Context.TELEPHONY_SERVICE)).getPhoneType()
                        == TelephonyManager.PHONE_TYPE_NONE) {
                    Toast.makeText(getApplicationContext(), "Device does not support call", Toast.LENGTH_SHORT).show();

                } else {

                    Intent myIntent = new Intent(Intent.ACTION_VIEW);
                    myIntent.setData(Uri.parse("tel:" + productDetailPojo.getData().getUserMobile()));
                    startActivity(myIntent);

                }
            }
        });

        ivDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                android.app.AlertDialog.Builder alertDialogBuilder = new android.app.AlertDialog.Builder(DetailsActivity.this);
                alertDialogBuilder.setMessage("You really want to Delete ?").setCancelable(false).setPositiveButton("Yes",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {

                                if (NetworkUtil.getConnectivityStatusBoolen(DetailsActivity.this)) {
                                    HitDeleteApi();
                                } else {
                                    SantheUtility.displayMessageAlert(getResources().getString(R.string.network), DetailsActivity.this);

                                }

                            }
                        }).setNegativeButton("No", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.cancel();
                    }
                });
                android.app.AlertDialog alertDialog = alertDialogBuilder.create();
                alertDialog.show();


            }
        });
        ivEdit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

//                Toast.makeText(getApplicationContext(), "done", Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(DetailsActivity.this, EditProductActivity.class);
                intent.putExtra("productDetail", productDetailPojo.getData());
                startActivity(intent);
                finish();


            }
        });
    }

    private void HitDeleteApi() {

        try {

            SantheUtility.showProgress(DetailsActivity.this, getResources().getString(R.string.pleaseWait));

            retrofitDataProvider.deleteProductItem(productId, new DownlodableCallback<MobileRegisterPojo>() {
                @Override
                public void onSuccess(MobileRegisterPojo result) {

                    SantheUtility.dismissProgressDialog();

                    if (result.getStatus().contains("0")) {
                        Log.d("Rakess", result.getStatus());

                        finish();
                    } else {

                        SantheUtility.displayMessageAlert(getResources().getString(R.string.unable), DetailsActivity.this);
                    }

                }

                @Override
                public void onFailure(String error) {

                    SantheUtility.displayMessageAlert(getResources().getString(R.string.something), DetailsActivity.this);
                    SantheUtility.dismissProgressDialog();
                }
            });
        } catch (Exception e) {

            e.printStackTrace();
            SantheUtility.dismissProgressDialog();

        }

    }

    private void initViews() {

        indicator = (CirclePageIndicator) findViewById(R.id.indicator);
        rvClick = (RelativeLayout) findViewById(R.id.rvClick);
        viewPager = (ViewPager) findViewById(R.id.viewPager);
        btnFab = (FloatingActionButton) findViewById(R.id.btnFab);
        btnFab.setBackgroundTintList(ColorStateList.valueOf(getResources().getColor(R.color.colorOrange)));
        tvCountImage = (TextView) findViewById(R.id.tvImageCount);
        tvCategory = (TextView) findViewById(R.id.tvCategory);
        tvPostedOn = (TextView) findViewById(R.id.tvPostedOn);
        tvAmount = (TextView) findViewById(R.id.tvAmount);
        tvDistance = (TextView) findViewById(R.id.tvDistance);
        tvtotalWeight = (TextView) findViewById(R.id.tvtotalWeight);
        ivDelete = (ImageView) findViewById(R.id.ivDelete);
        ivEdit = (ImageView) findViewById(R.id.ivEdit);
        tvNegotiable = (TextView) findViewById(R.id.tvNegotiable);
        tvHeading = (TextView) findViewById(R.id.tvHeading);

    }

    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(CalligraphyContextWrapper.wrap(newBase));
    }

    public void onMapReady(GoogleMap googleMap) {
        maps = googleMap;


        locationUpdate();

        if (mapView != null &&
                mapView.findViewById(Integer.parseInt("1")) != null) {

            View toolbar = ((View) mapView.findViewById(Integer.parseInt("1")).
                    getParent()).findViewById(Integer.parseInt("4"));

            RelativeLayout.LayoutParams rlp = (RelativeLayout.LayoutParams) toolbar.getLayoutParams();

            rlp.addRule(RelativeLayout.ALIGN_PARENT_TOP, 0);
            rlp.addRule(RelativeLayout.ALIGN_PARENT_TOP, RelativeLayout.TRUE);
            rlp.setMargins(0, 0, 30, 30);


        }
    }

    private void getLocation() {

        if (mCurrentLocation != null) {
            try {
                Geocoder geo = new Geocoder(DetailsActivity.this, Locale.getDefault());
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

                    Log.d("Latitude", "status" + newString);

                    mycurrentLocationAddress = newString;

                } else {
                    Toast.makeText(getApplicationContext(), R.string.no_location, Toast.LENGTH_SHORT).show();
                }
            } catch (Exception e) {
                Log.e("Exception", e.getMessage(), e);
            }

        } else {
            Toast.makeText(getApplicationContext(), R.string.please_choose_location, Toast.LENGTH_SHORT).show();
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
                    }, RC_PERM

            );
            return;

        }
        maps.getUiSettings().setMyLocationButtonEnabled(false);
        LocationManager lm = (LocationManager) getSystemService(Context.LOCATION_SERVICE);

        if (!lm.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
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
        locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 5000, 100, this);

        // Can we at least get network location?
        Location location = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
        if (location == null) {
            // If cannot get GPS go for Network Provider's Location service
            locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 5000, 100, this);
        }
    }

    @Override
    public void onLocationChanged(Location location) {

        mCurrentLocation = new LatLng(location.getLatitude(), location.getLongitude());

        Log.d("Test", "I am Caling location get");
        currentLat = location.getLatitude();
        currentLong = location.getLongitude();
        getLocation();

        if (maps != null) {
            try {
                if (currentLat > 0.0 || currentLat
                        > 0.0) {
                    setMapsDirection();
                }
            } catch (Exception e) {

                e.printStackTrace();
            }

        }


    }

    @Override
    public void onStatusChanged(String s, int i, Bundle bundle) {

    }

    @Override
    public void onProviderEnabled(String s) {

    }

    @Override
    public void onProviderDisabled(String s) {

    }

    public class ImageSliderAdapter extends PagerAdapter {

        private Context context;
        private LayoutInflater _Inflater;
        private List<String> images;

        public ImageSliderAdapter(Context context, List<String> images) {
            this.context = context;
            this._Inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            this.images = images;
        }

        @Override
        public int getCount() {

            if (images.size() > 4) {

                return 3;
            } else {
                return images.size();
            }
        }

        @Override
        public Object instantiateItem(ViewGroup container, int position) {

            View pager = null;
            pager = _Inflater.inflate(R.layout.displayimage, null);
            ImageView ivMusicImage = pager.findViewById(R.id.ivMusicImage);

            Glide.with(DetailsActivity.this).load(images.get(position))
                    .into(ivMusicImage);

            container.addView(pager, 0);
            return pager;
        }

        @Override
        public void destroyItem(ViewGroup container, int position, Object object) {
            container.removeView((View) object);
        }

        @Override
        public boolean isViewFromObject(View view, Object object) {
            return view == object;
        }

        @Override
        public void restoreState(Parcelable arg0, ClassLoader arg1) {
        }

        @Override
        public Parcelable saveState() {
            return null;
        }
    }

}
