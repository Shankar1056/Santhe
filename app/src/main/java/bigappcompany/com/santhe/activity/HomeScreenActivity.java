package bigappcompany.com.santhe.activity;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Resources;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.graphics.drawable.DrawerArrowDrawable;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;

import java.util.ArrayList;

import bigappcompany.com.santhe.R;
import bigappcompany.com.santhe.adapter.MenuListAdapter;
import bigappcompany.com.santhe.fragment.BuyHomeFragment;
import bigappcompany.com.santhe.fragment.DiscussionHomeFragment;
import bigappcompany.com.santhe.fragment.InstitutionFragment;
import bigappcompany.com.santhe.fragment.ProfileFragment;
import bigappcompany.com.santhe.fragment.SellHomeFragment;
import bigappcompany.com.santhe.fragment.UpdateFragment;
import bigappcompany.com.santhe.fragment.UsefulInformationFragment;
import bigappcompany.com.santhe.model.MenuListPojo;
import bigappcompany.com.santhe.utility.SharedPreferencesUtility;
import uk.co.chrisjenx.calligraphy.CalligraphyConfig;
import uk.co.chrisjenx.calligraphy.CalligraphyContextWrapper;


public class HomeScreenActivity extends AppCompatActivity {

    public int pos = 0;
    int listPosition = 0;
    private DrawerLayout drawer;
    private RelativeLayout _profile;
    private Resources resources;
    private DrawerArrowDrawable drawerArrowDrawable;
    private float offset;
    private boolean flipped;
    private Fragment fragment;
    private FragmentTransaction fragmentTransaction;
    private ListView List;
    private ArrayList<MenuListPojo> _menuListData;
    private TextView _tvProfileName, _tvEmail;
    private RelativeLayout rlProfile, rlHome;
    private TextView tvProfileName;
    private ImageView ivProfilePicture;
    private boolean doubleBackToExitPressedOnce = false;
    private boolean checkBackpressed = true;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


        Window window = this.getWindow();
        window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {

            window.setStatusBarColor(ContextCompat.getColor(this, R.color.colorGreen));
        }

        setContentView(R.layout.home_screen_activity);

        CalligraphyConfig.initDefault(new CalligraphyConfig.Builder()
                .setDefaultFontPath("fonts/Pangram-Medium.otf")
                .setFontAttrId(R.attr.fontPath)
                .build()
        );
        initWigdets();
        eventListers();
        prepareList();
        setUpSlideMenu();
        setUpDrawerLisener();
        callHome();

    }

    @Override
    protected void onResume() {
        super.onResume();

        callUpdaeDetails();



    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        outState.putString("WORKAROUND_FOR_BUG_19917_KEY", "WORKAROUND_FOR_BUG_19917_VALUE");
        super.onSaveInstanceState(outState);
    }

    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(CalligraphyContextWrapper.wrap(newBase));
    }

    private void prepareList() {

        _menuListData = new ArrayList<MenuListPojo>();

        MenuListPojo ojtpojo = new MenuListPojo();
        ojtpojo.setItem("Profile");
        ojtpojo.setSelectedImg(R.drawable.green_profile);
        ojtpojo.setDeselectedImg(R.drawable.profile);
        ojtpojo.setIsSelected(false);
        _menuListData.add(ojtpojo);

        ojtpojo = new MenuListPojo();
        ojtpojo.setItem("My Updates");
        ojtpojo.setSelectedImg(R.drawable.green_update);
        ojtpojo.setDeselectedImg(R.drawable.update);
        ojtpojo.setIsSelected(false);
        _menuListData.add(ojtpojo);

        ojtpojo = new MenuListPojo();
        ojtpojo.setItem("Dealer Information");
        ojtpojo.setSelectedImg(R.drawable.select_dealer);
        ojtpojo.setDeselectedImg(R.drawable.deselect_dealer);
        ojtpojo.setIsSelected(false);
        _menuListData.add(ojtpojo);

        ojtpojo = new MenuListPojo();
        ojtpojo.setItem("Institution Information");
        ojtpojo.setSelectedImg(R.drawable.select_institute);
        ojtpojo.setDeselectedImg(R.drawable.deselect_institute);
        ojtpojo.setIsSelected(false);
        _menuListData.add(ojtpojo);

        ojtpojo = new MenuListPojo();
        ojtpojo.setItem("Log out");
        ojtpojo.setSelectedImg(R.drawable.logout_green);
        ojtpojo.setDeselectedImg(R.drawable.logout);
        ojtpojo.setIsSelected(false);
        _menuListData.add(ojtpojo);

        MenuListAdapter value = new MenuListAdapter(this, _menuListData);
        List.setAdapter(value);

    }

    private void setMenuArray(int pos) {

        MenuListPojo _Mydata;

        for (int size = 0; size < _menuListData.size(); size++) {
            _Mydata = new MenuListPojo();
            _Mydata = _menuListData.get(size);
            if (pos == size) {
                _Mydata.setIsSelected(true);
            } else {
                _Mydata.setIsSelected(false);
            }
            _menuListData.set(size, _Mydata);
        }
        List.invalidateViews();
    }

    private void eventListers() {

        List.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, final int position, long id) {
                listPosition = position;
                switchFragments(position);

            }
        });

        rlProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                DrawerMenuStatus();
                fragmentTransaction = getSupportFragmentManager().beginTransaction();
                fragment = new ProfileFragment();
                fragmentTransaction.replace(R.id.content_frame, fragment);
                fragmentTransaction.commit();

            }
        });

        rlHome.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                callHome();


            }
        });


    }

    private void callHome() {

        drawer.closeDrawers();
        fragmentTransaction = getSupportFragmentManager().beginTransaction();
        fragment = new BuyHomeFragment();
        fragmentTransaction.replace(R.id.content_frame, fragment);
        fragmentTransaction.commit();
    }


    private void switchFragments(int position) {

        if (position == 0) {
            DrawerMenuStatus();
            callFrgments(0);
        } else if (position == 1) {
            DrawerMenuStatus();
            callFrgments(1);
        } else if (position == 2) {
            DrawerMenuStatus();
            callFrgments(2);
        } else if (position == 3) {
            callFrgments(3);
        } else if (position == 4) {
            callFrgments(4);
        }
    }


    private void initWigdets() {

        List = (ListView) findViewById(android.R.id.list);
        _profile = (RelativeLayout) findViewById(R.id.rl_profile);
        rlProfile = (RelativeLayout) findViewById(R.id.rlProfile);
        ivProfilePicture = (ImageView) findViewById(R.id.ivProfilePicture);
        tvProfileName = (TextView) findViewById(R.id.tvProfileName);
        rlHome = (RelativeLayout) findViewById(R.id.rlHome);




        Log.d("profileImage", "image" + SharedPreferencesUtility.getProfilePicture(this));


    }

    private void setUpSlideMenu() {

        drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        resources = getResources();
        drawerArrowDrawable = new DrawerArrowDrawable(this);

    }


    private void setUpDrawerLisener() {
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

    private void DrawerMenuStatus() {

        if (drawer.isDrawerVisible(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            drawer.openDrawer(GravityCompat.START);
        }
    }

    public void callBuyFragment() {
        fragmentTransaction = getSupportFragmentManager().beginTransaction();
        fragment = new BuyHomeFragment();
        fragmentTransaction.replace(R.id.content_frame, fragment);
        fragmentTransaction.commit();
        checkBackpressed = true;
    }

    public void callSellFragment() {
        fragmentTransaction = getSupportFragmentManager().beginTransaction();
        fragment = new SellHomeFragment();
        fragmentTransaction.replace(R.id.content_frame, fragment);
        fragmentTransaction.commit();
        checkBackpressed = true;
    }

    public void callDiscussFragment() {
        fragmentTransaction = getSupportFragmentManager().beginTransaction();
        fragment = new DiscussionHomeFragment();
        fragmentTransaction.replace(R.id.content_frame, fragment);
        fragmentTransaction.commit();
        checkBackpressed = true;
    }

    public void callFrgments(int which) {
        pos = which;
        setMenuArray(which);

        drawer.closeDrawers();
        switch (which) {
            case 0:
                fragmentTransaction = getSupportFragmentManager().beginTransaction();
                fragment = new ProfileFragment();
                fragmentTransaction.replace(R.id.content_frame, fragment, "profile");
                fragmentTransaction.commit();
                checkBackpressed = false;
                break;
            case 1:

                fragmentTransaction = getSupportFragmentManager().beginTransaction();
                fragment = new UpdateFragment();
                fragmentTransaction.replace(R.id.content_frame, fragment, "myUpdates");
                fragmentTransaction.commit();
                checkBackpressed = false;
                break;

            case 2:
                fragmentTransaction = getSupportFragmentManager().beginTransaction();
                fragment = new UsefulInformationFragment();
                fragmentTransaction.replace(R.id.content_frame, fragment, "usefullinfo");
                fragmentTransaction.commit();
                checkBackpressed = false;
                break;
            case 3:
                fragmentTransaction = getSupportFragmentManager().beginTransaction();
                fragment = new InstitutionFragment();
                fragmentTransaction.replace(R.id.content_frame, fragment, "institiute");
                fragmentTransaction.commit();
                checkBackpressed = false;
                break;
            case 4:
                android.app.AlertDialog.Builder alertDialogBuilder = new android.app.AlertDialog.Builder(HomeScreenActivity.this);
                alertDialogBuilder.setMessage("Are You Sure You Want to Logout?").setCancelable(false).setPositiveButton("Yes",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {

                                SharedPreferencesUtility.setAuthKey(getApplicationContext(), "");
                                SharedPreferencesUtility.setIsNormalLogin(getApplicationContext(), true);
                                Intent intent = new Intent(HomeScreenActivity.this, MobileActivity.class);
                                startActivity(intent);
                                finish();


                            }
                        }).setNegativeButton("No", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.cancel();
                    }
                });
                android.app.AlertDialog alertDialog = alertDialogBuilder.create();
                alertDialog.show();

                break;
            default:
                break;
        }
    }

    @Override

    public void onBackPressed() {
        //Checking for fragment count on backstack


        if (!checkBackpressed) {

            callBuyFragment();

        } else {
            if (getSupportFragmentManager().getBackStackEntryCount() > 0) {
                getSupportFragmentManager().popBackStack();
            } else if (!doubleBackToExitPressedOnce) {
                this.doubleBackToExitPressedOnce = true;
                Toast.makeText(this, "Please click BACK again to exit.", Toast.LENGTH_SHORT).show();

                new Handler().postDelayed(new Runnable() {

                    @Override
                    public void run() {
                        doubleBackToExitPressedOnce = false;
                    }
                }, 2000);
            } else {
                super.onBackPressed();
                return;
            }
        }
    }

    public void callUpdaeDetails() {

        StringBuilder sb = new StringBuilder(SharedPreferencesUtility.getProfileName(this));
        sb.setCharAt(0, Character.toUpperCase(sb.charAt(0)));
        tvProfileName.setText(sb);

        Glide.with(this).load(SharedPreferencesUtility.getProfilePicture(this))
                .apply(RequestOptions.circleCropTransform()).into(ivProfilePicture);
    }
}
