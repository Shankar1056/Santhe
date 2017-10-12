package bigappcompany.com.santhe.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;

import java.util.ArrayList;

import bigappcompany.com.santhe.R;
import bigappcompany.com.santhe.adapter.PersonalisedAdapter;
import bigappcompany.com.santhe.interfaces.ClickListener;
import bigappcompany.com.santhe.model.DataCategory;
import bigappcompany.com.santhe.model.PersonalisePojo;
import bigappcompany.com.santhe.model.PersonalizedFormingPojo;
import bigappcompany.com.santhe.network.DownlodableCallback;
import bigappcompany.com.santhe.network.RetrofitDataProvider;
import bigappcompany.com.santhe.other.RecyclerTouchListener;
import bigappcompany.com.santhe.utility.NetworkUtil;
import bigappcompany.com.santhe.utility.SantheUtility;
import bigappcompany.com.santhe.utility.SharedPreferencesUtility;
import uk.co.chrisjenx.calligraphy.CalligraphyConfig;
import uk.co.chrisjenx.calligraphy.CalligraphyContextWrapper;

public class PersonaliseActivity extends AppCompatActivity {

    private ImageView ivVegetable, ivFruit, ivSeeds, ivGrains, ivFlower, ivMedical, ivFertilizer, ivFish, ivMilk;
    private TextView tvStart, tvSkip;
    private int count = 0;
    private boolean isVegetables = false, isFruit = false, isSeeds = false, isGrains = false, isFlower = false,
            isMedical = false, isFertilizer = false, isFish = false, isMilk = false;
    private RetrofitDataProvider retrofitDataProvider;
    private RecyclerView rcvPersonalise;
    private ArrayList<DataCategory> dataPersonalise = new ArrayList<DataCategory>();
    private ImageView ivImageType;
    private ArrayList<String> seleted = new ArrayList<String>();

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Window window = this.getWindow();
        window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            window.setStatusBarColor(ContextCompat.getColor(this, R.color.colorWhite));
        }
        setContentView(R.layout.personalise_activity);
        retrofitDataProvider = new RetrofitDataProvider();

        CalligraphyConfig.initDefault(new CalligraphyConfig.Builder()
                .setDefaultFontPath("fonts/Pangram-Regular.otf")
                .setFontAttrId(R.attr.fontPath)
                .build()
        );


        initViews();
        eventListerns();


        try {
            if (NetworkUtil.getConnectivityStatusBoolen(PersonaliseActivity.this)) {
                hitApi();
            } else {
                SantheUtility.displayMessageAlert(getResources().getString(R.string.network), PersonaliseActivity.this);

            }

        } catch (Exception e) {
            e.printStackTrace();
            SantheUtility.dismissProgressDialog();
        }

    }


    private void hitApi() {

        SantheUtility.showProgress(PersonaliseActivity.this, getResources().getString(R.string.pleaseWait));

        retrofitDataProvider.getCategories(new DownlodableCallback<PersonalisePojo>() {
            @Override
            public void onSuccess(PersonalisePojo result) {

                SantheUtility.dismissProgressDialog();

                if (result.getStatus().contentEquals("true")) {

                    dataPersonalise.clear();
                    dataPersonalise = result.getData();

                    if (dataPersonalise.size() > 0) {
                        PersonalisedAdapter adapter = new PersonalisedAdapter(PersonaliseActivity.this, dataPersonalise);
                        rcvPersonalise.setAdapter(adapter);
                    } else {

                        Toast.makeText(getApplicationContext(), getResources().getString(R.string.NoData), Toast.LENGTH_SHORT).show();
                    }


                } else {

                    Toast.makeText(getApplicationContext(), result.getMessage(), Toast.LENGTH_SHORT).show();
                }

            }

            @Override
            public void onFailure(String error) {
                SantheUtility.dismissProgressDialog();
                Toast.makeText(getApplicationContext(), getResources().getString(R.string.something), Toast.LENGTH_SHORT).show();
            }
        });

        rcvPersonalise.addOnItemTouchListener(new RecyclerTouchListener(this, rcvPersonalise, new ClickListener() {
            @Override
            public void onClick(View view, final int position) {

                int itemPosition = rcvPersonalise.indexOfChild(view);

                ivImageType = view.findViewById(R.id.ivImageType);
                ivImageType.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {

                        DataCategory dataCategory = dataPersonalise.get(position);

                        dataCategory.setSelected(!dataCategory.isSelected);

                        if (dataCategory.isSelected) {

                            ivImageType.setImageResource(R.drawable.personalized);

                            seleted.add(dataPersonalise.get(position).getCat_id());

                        } else {

                            seleted.remove(dataPersonalise.get(position).getCat_id());

                            Glide.with(PersonaliseActivity.this)
                                    .load(dataPersonalise.get(position).getIcon())
                                    .apply(RequestOptions.circleCropTransform())
                                    .into(ivImageType);


                        }

                    }
                });

            }

            @Override
            public void onLongClick(View view, int position) {

            }
        }));

    }

    private void eventListerns() {

        tvStart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                String selectedCat = android.text.TextUtils.join(",", seleted);

                try {
                    if (NetworkUtil.getConnectivityStatusBoolen(PersonaliseActivity.this)) {
                        hitPersonalizeAPi(selectedCat);
                    } else {
                        SantheUtility.displayMessageAlert(getResources().getString(R.string.network), PersonaliseActivity.this);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    SantheUtility.dismissProgressDialog();
                }


            }
        });

        tvSkip.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                try {
                    if (NetworkUtil.getConnectivityStatusBoolen(PersonaliseActivity.this)) {
                        hitPersonalizeAPi("");
                    } else {
                        SantheUtility.displayMessageAlert(getResources().getString(R.string.network), PersonaliseActivity.this);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    SantheUtility.dismissProgressDialog();
                }

            }
        });

    }

    private void hitPersonalizeAPi(String selectedCat) {

        SantheUtility.showProgress(PersonaliseActivity.this, getResources().getString(R.string.pleaseWait));

        retrofitDataProvider.getPersonalizeDetails(SharedPreferencesUtility.getAuthkey(PersonaliseActivity.this),
                selectedCat, new DownlodableCallback<PersonalizedFormingPojo>() {
                    @Override
                    public void onSuccess(PersonalizedFormingPojo result) {

                        SantheUtility.dismissProgressDialog();
                        startActivity(new Intent(PersonaliseActivity.this, HomeScreenActivity.class));
                        finish();

                    }

                    @Override
                    public void onFailure(String error) {

                        SantheUtility.dismissProgressDialog();
                        Toast.makeText(getApplicationContext(), getResources().getString(R.string.something), Toast.LENGTH_SHORT).show();

                    }
                });

    }

    private void initViews() {

        ivVegetable = (ImageView) findViewById(R.id.ivVegetable);
        rcvPersonalise = (RecyclerView) findViewById(R.id.rcvCategory);
        ivFruit = (ImageView) findViewById(R.id.ivFruit);
        ivSeeds = (ImageView) findViewById(R.id.ivSeeds);
        ivGrains = (ImageView) findViewById(R.id.ivGrains);
        ivFlower = (ImageView) findViewById(R.id.ivFlower);
        ivMedical = (ImageView) findViewById(R.id.ivMedical);
        ivFertilizer = (ImageView) findViewById(R.id.ivFertilizer);
        ivFish = (ImageView) findViewById(R.id.ivFish);
        ivMilk = (ImageView) findViewById(R.id.ivMilk);
        tvStart = (TextView) findViewById(R.id.tvStart);
        tvSkip = (TextView) findViewById(R.id.tvSkip);

        int numberOfColumns = 3;
        rcvPersonalise.setLayoutManager(new GridLayoutManager(this, numberOfColumns));


    }

    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(CalligraphyContextWrapper.wrap(newBase));
    }


}
