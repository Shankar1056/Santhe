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
import android.text.SpannableString;
import android.text.SpannableStringBuilder;
import android.text.Spanned;
import android.text.style.ClickableSpan;
import android.util.Log;
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
import bigappcompany.com.santhe.adapter.InstitutionsAdapter;
import bigappcompany.com.santhe.interfaces.ClickListener;
import bigappcompany.com.santhe.interfaces.ClickOnReadmore;
import bigappcompany.com.santhe.model.InstitutionDataItem;
import bigappcompany.com.santhe.model.InstitutionsPojo;
import bigappcompany.com.santhe.network.DownlodableCallback;
import bigappcompany.com.santhe.network.RetrofitDataProvider;
import bigappcompany.com.santhe.other.RecyclerTouchListener;
import bigappcompany.com.santhe.utility.NetworkUtil;
import bigappcompany.com.santhe.utility.SantheUtility;
import uk.co.chrisjenx.calligraphy.CalligraphyConfig;
import uk.co.chrisjenx.calligraphy.CalligraphyContextWrapper;


public class InstitutionDetailsActivity extends AppCompatActivity implements ClickOnReadmore {


    private RecyclerView rcvType;
    private RelativeLayout rv_click;
    private String cat_id = "", city_id = "";
    private TextView tvLoaction;
    private TextView tvCall;
    private List<InstitutionDataItem> resultvalue = new ArrayList<>();
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

//            cat_id = bundle.getString("category_id");
            city_id = bundle.getString("citi_id");


        }

        setContentView(R.layout.institution_details_activity);

        CalligraphyConfig.initDefault(new CalligraphyConfig.Builder()
                .setDefaultFontPath("fonts/Pangram-Regular.otf")
                .setFontAttrId(R.attr.fontPath)
                .build()
        );

        initViews();
        eventListerns();


        try {

            if (NetworkUtil.getConnectivityStatusBoolen(InstitutionDetailsActivity.this)) {
                hitApi();
            } else {
                SantheUtility.displayMessageAlert(getResources().getString(R.string.network), InstitutionDetailsActivity.this);
            }
        } catch (Exception e) {
            e.printStackTrace();
            SantheUtility.dismissProgressDialog();
        }
    }

    private void hitApi() {

        SantheUtility.showProgress(InstitutionDetailsActivity.this, getResources().getString(R.string.pleaseWait));

        RetrofitDataProvider retrofitDataProvider = new RetrofitDataProvider();

        retrofitDataProvider.getInstitutionDetails(city_id, new DownlodableCallback<InstitutionsPojo>() {
            @Override
            public void onSuccess(InstitutionsPojo result) {
                SantheUtility.dismissProgressDialog();
                if (result.getStatus().contains("true")) {

                    resultvalue.clear();
                    resultvalue = result.getData();
                    RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(InstitutionDetailsActivity.this);
                    rcvType.setLayoutManager(mLayoutManager);

                    if (resultvalue.size() > 0) {
                        InstitutionsAdapter adapter = new InstitutionsAdapter(getApplicationContext(), resultvalue, InstitutionDetailsActivity.this);
                        rcvType.setAdapter(adapter);


                    }

                } else {

                    SantheUtility.displayMessageAlert(getResources().getString(R.string.NoData), InstitutionDetailsActivity.this);


                }
            }

            @Override
            public void onFailure(String error) {

                SantheUtility.dismissProgressDialog();
                Toast.makeText(getApplicationContext(), getResources().getString(R.string.something), Toast.LENGTH_SHORT).show();


            }
        });
    }


    private SpannableStringBuilder addClickablePart(String str) {
        SpannableStringBuilder ssb = new SpannableStringBuilder(str);

        int idx1 = str.indexOf("[");
        int idx2 = 0;
        while (idx1 != -1) {
            idx2 = str.indexOf("]", idx1) + 1;

            final String clickString = str.substring(idx1, idx2);
            Log.d("PJJJJJJ", "clickString" + clickString);
            phNumbertoCall = clickString;
            ssb.setSpan(new ClickableSpan() {

                @Override
                public void onClick(View widget) {
                    Toast.makeText(getApplicationContext(), clickString,
                            Toast.LENGTH_SHORT).show();


                    Log.d("PJJJJJJ", "clickString" + clickString);

                }
            }, idx1, idx2, 0);
            idx1 = str.indexOf("[", idx2);
        }

        return ssb;
    }


    private void eventListerns() {

        rv_click.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                finish();
            }
        });

        rcvType.addOnItemTouchListener(new RecyclerTouchListener(InstitutionDetailsActivity.this, rcvType, new ClickListener() {
            @Override
            public void onClick(View view, final int position) {

                tvLoaction = view.findViewById(R.id.tvLoaction);
                tvCall = view.findViewById(R.id.tvCall);

                tvCall.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {

                        clickedPosition = position;

//                        textClick();

                        if (resultvalue.size() > 0) {
                            if (((TelephonyManager) getSystemService(Context.TELEPHONY_SERVICE)).getPhoneType()
                                    == TelephonyManager.PHONE_TYPE_NONE) {
                                Toast.makeText(getApplicationContext(), "Device does not support call", Toast.LENGTH_SHORT).show();

                            } else {
                                Intent myIntent = new Intent(Intent.ACTION_VIEW);
                                myIntent.setData(Uri.parse("tel:" + resultvalue.get(position).getCollegePhone()));
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

    private void checkSpannable(int start, int end, final String value) {

        SpannableString ss = new SpannableString(resultvalue.get(clickedPosition).getCollegePhone());
        ClickableSpan clickableSpan = new ClickableSpan() {
            @Override
            public void onClick(View textView) {

                phNumbertoCall = value;
                Log.d("PJJJJJJ", "phNumbertoCall" + phNumbertoCall);

            }
        };
        ss.setSpan(clickableSpan, start, end, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        tvCall.setText(ss);
    }

    private void popupforcategory(int position) {

        final Dialog dialog = new Dialog(InstitutionDetailsActivity.this);
//        final Dialog dialog = new Dialog(AgricultureLocationActivity.this, android.R.style.Theme_Translucent_NoTitleBar);

//        final Dialog dialog = new Dialog(new ContextThemeWrapper(PostActivity.this,  R.style.full_screen_dialog));

        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.popup_address);
        dialog.setTitle(null);

        Button submitButton = dialog.findViewById(R.id.btnOk);
        TextView tvAddress = dialog.findViewById(R.id.tvAddress);
        tvAddress.setText(resultvalue.get(position).getCollegeAddress());

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
