package bigappcompany.com.santhe.activity;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.telephony.TelephonyManager;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

import bigappcompany.com.santhe.R;
import bigappcompany.com.santhe.adapter.AgricultureLocationAdapter;
import bigappcompany.com.santhe.interfaces.ClickListener;
import bigappcompany.com.santhe.interfaces.ClickOnReadmore;
import bigappcompany.com.santhe.model.LocationOfficeDetails;
import bigappcompany.com.santhe.model.LocationOfficePojo;
import bigappcompany.com.santhe.network.DownlodableCallback;
import bigappcompany.com.santhe.network.RetrofitDataProvider;
import bigappcompany.com.santhe.other.RecyclerTouchListener;
import bigappcompany.com.santhe.utility.NetworkUtil;
import bigappcompany.com.santhe.utility.SantheUtility;
import uk.co.chrisjenx.calligraphy.CalligraphyConfig;
import uk.co.chrisjenx.calligraphy.CalligraphyContextWrapper;


public class AgricultureLocationActivity extends AppCompatActivity implements ClickOnReadmore {


    private RecyclerView rcvType;
    private RelativeLayout rv_click;
    private String cat_id = "", city_id = "";
    private TextView tvLoaction;
    private TextView tvCall;
    private List<LocationOfficeDetails> resultvalue = new ArrayList<>();
    private int clickedPosition = 0;
    private String phNumbertoCall = "";

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Window window = this.getWindow();
        window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            window.setStatusBarColor(ContextCompat.getColor(this, R.color.colorGreen));
        }

        Bundle bundle = getIntent().getExtras();
        if (bundle != null) {
            cat_id = bundle.getString("category_id");
            city_id = bundle.getString("citi_id");
        }
        setContentView(R.layout.agriculture_place);
        CalligraphyConfig.initDefault(new CalligraphyConfig.Builder()
                .setDefaultFontPath("fonts/Pangram-Regular.otf")
                .setFontAttrId(R.attr.fontPath)
                .build()
        );

        initViews();
        eventListerns();


        try {

            if (NetworkUtil.getConnectivityStatusBoolen(AgricultureLocationActivity.this)) {
                hitApi();
            } else {
                SantheUtility.displayMessageAlert(getResources().getString(R.string.network), AgricultureLocationActivity.this);
            }
        } catch (Exception e) {
            e.printStackTrace();
            SantheUtility.dismissProgressDialog();
        }
    }

    private void hitApi() {
        SantheUtility.showProgress(AgricultureLocationActivity.this, getResources().getString(R.string.pleaseWait));

        RetrofitDataProvider retrofitDataProvider = new RetrofitDataProvider();

        retrofitDataProvider.getLocationDetails(city_id, cat_id, new DownlodableCallback<LocationOfficePojo>() {
            @Override
            public void onSuccess(LocationOfficePojo result) {
                SantheUtility.dismissProgressDialog();
                if (result.getStatus().contains("true")) {

                    resultvalue.clear();
                    resultvalue = result.getData();
                    RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(AgricultureLocationActivity.this);
                    rcvType.setLayoutManager(mLayoutManager);

                    if (resultvalue.size() > 0) {
                        AgricultureLocationAdapter adapter = new AgricultureLocationAdapter(getApplicationContext(), resultvalue, AgricultureLocationActivity.this);
                        rcvType.setAdapter(adapter);

                    }

                } else {

                    SantheUtility.displayMessageAlert(getResources().getString(R.string.NoData), AgricultureLocationActivity.this);


                }
            }

            @Override
            public void onFailure(String error) {

                SantheUtility.dismissProgressDialog();
                Toast.makeText(getApplicationContext(), getResources().getString(R.string.something), Toast.LENGTH_SHORT).show();


            }
        });
    }


    private void eventListerns() {

        rv_click.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                finish();
            }
        });

        rcvType.addOnItemTouchListener(new RecyclerTouchListener(AgricultureLocationActivity.this, rcvType, new ClickListener() {
            @Override
            public void onClick(View view, final int position) {

                tvLoaction = view.findViewById(R.id.tvLoaction);
                tvCall = view.findViewById(R.id.tvCall);

                tvCall.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {

                        clickedPosition = position;

                        if (resultvalue.size() > 0) {
                            if (((TelephonyManager) getSystemService(Context.TELEPHONY_SERVICE)).getPhoneType()
                                    == TelephonyManager.PHONE_TYPE_NONE) {
                                Toast.makeText(getApplicationContext(), "Device does not support call", Toast.LENGTH_SHORT).show();

                            } else {
                                Intent myIntent = new Intent(Intent.ACTION_VIEW);
                                myIntent.setData(Uri.parse("tel:" + resultvalue.get(position).getOffice_phone()));
                                startActivity(myIntent);

                            }
                        }
                    }

                });


            }

            @Override
            public void onLongClick(View view, int position) {

            }
        }));

    }

    private void popupforcategory(int position) {

        final Dialog dialog = new Dialog(AgricultureLocationActivity.this);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.popup_address);
        dialog.setTitle(null);
        Button submitButton = dialog.findViewById(R.id.btnOk);
        TextView tvAddress = dialog.findViewById(R.id.tvAddress);
        tvAddress.setText(resultvalue.get(position).getOffice_address());

        submitButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                dialog.dismiss();

            }
        });

        dialog.show();
    }

    private void initViews() {
        rcvType = (RecyclerView) findViewById(R.id.rcvType);
        rv_click = (RelativeLayout) findViewById(R.id.rv_click);
    }

    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(CalligraphyContextWrapper.wrap(newBase));
    }

    @Override
    public void clickeOnOread(int position) {

        popupforcategory(position);
    }
}
