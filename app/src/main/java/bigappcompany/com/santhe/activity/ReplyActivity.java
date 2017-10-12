package bigappcompany.com.santhe.activity;

import android.content.Context;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

import bigappcompany.com.santhe.R;
import bigappcompany.com.santhe.adapter.ReplyAdapter;
import bigappcompany.com.santhe.model.MobileRegisterPojo;
import bigappcompany.com.santhe.model.RepliesItem;
import bigappcompany.com.santhe.model.RplyPojo;
import bigappcompany.com.santhe.network.DownlodableCallback;
import bigappcompany.com.santhe.network.RetrofitDataProvider;
import bigappcompany.com.santhe.utility.NetworkUtil;
import bigappcompany.com.santhe.utility.SantheUtility;
import bigappcompany.com.santhe.utility.SharedPreferencesUtility;
import uk.co.chrisjenx.calligraphy.CalligraphyConfig;
import uk.co.chrisjenx.calligraphy.CalligraphyContextWrapper;

public class ReplyActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private RetrofitDataProvider retrofitDataProvider;
    private String comment_id;
    private List<RepliesItem> listComments = new ArrayList<RepliesItem>();
    private RelativeLayout rv_click;
    private ImageView ivReply;
    private EditText etCommentHere;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Window window = this.getWindow();
        window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            window.setStatusBarColor(ContextCompat.getColor(this, R.color.colorGreen));
        }
        setContentView(R.layout.reply_activity);
        CalligraphyConfig.initDefault(new CalligraphyConfig.Builder()
                .setDefaultFontPath("fonts/Pangram-Regular.otf")
                .setFontAttrId(R.attr.fontPath)
                .build()
        );

        retrofitDataProvider = new RetrofitDataProvider();
        comment_id = getIntent().getExtras().getString("comment_id");
        initViews();
        eventListerns();
    }

    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(CalligraphyContextWrapper.wrap(newBase));
    }

    @Override
    protected void onResume() {
        super.onResume();
        HitCommentsAPi();
    }

    private void HitCommentsAPi() {


        if (NetworkUtil.getConnectivityStatusBoolen(ReplyActivity.this)) {
            try {
                SantheUtility.showProgress(this, getResources().getString(R.string.pleaseWait));
                retrofitDataProvider.listOfReplies(comment_id, new DownlodableCallback<RplyPojo>() {
                    @Override
                    public void onSuccess(RplyPojo result) {

                        SantheUtility.dismissProgressDialog();

                        if (result.getStatus().contains("0")) {

                            if (null != result.getData().getReplies()) {

                                listComments = result.getData().getReplies();

                                if (listComments.size() > 0) {

                                    RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(ReplyActivity.this);
                                    recyclerView.setLayoutManager(mLayoutManager);
                                    recyclerView.setItemAnimator(new DefaultItemAnimator());
                                    ReplyAdapter adaptor = new ReplyAdapter(ReplyActivity.this, listComments);
                                    recyclerView.setAdapter(adaptor);
                                }
                            }


                        } else {
                            Toast.makeText(getApplicationContext(), getResources().getString(R.string.NoData), Toast.LENGTH_SHORT).show();
                        }

                    }

                    @Override
                    public void onFailure(String error) {
                        SantheUtility.dismissProgressDialog();

                        Toast.makeText(getApplicationContext(), getResources().getString(R.string.something), Toast.LENGTH_SHORT).show();

                    }
                });
            } catch (Exception e) {
                e.printStackTrace();
                SantheUtility.dismissProgressDialog();

            }

        } else {
            SantheUtility.displayMessageAlert(getResources().getString(R.string.network), ReplyActivity.this);

        }

    }

    private void hideKeypad() {

        InputMethodManager inputManager = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
        inputManager.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
    }

    private void eventListerns() {

        rv_click.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        ivReply.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (NetworkUtil.getConnectivityStatusBoolen(ReplyActivity.this)) {
                    hitReplyApi();
                } else {
                    SantheUtility.displayMessageAlert(getResources().getString(R.string.network), ReplyActivity.this);

                }
                hideKeypad();


            }
        });

    }

    private void hitReplyApi() {
        try {
            retrofitDataProvider.replyOnComment(SharedPreferencesUtility.getAuthkey(this),
                    comment_id, etCommentHere.getText().toString(), new DownlodableCallback<MobileRegisterPojo>() {

                        @Override
                        public void onSuccess(MobileRegisterPojo result) {

                            SantheUtility.dismissProgressDialog();

                            if (result.getStatus().contains("0")) {

                                etCommentHere.setText("");

                                HitCommentsAPi();
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


        } catch (Exception e) {

            e.printStackTrace();
        }
    }

    private void initViews() {
        recyclerView = (RecyclerView) findViewById(R.id.recyclerView);
        etCommentHere = (EditText) findViewById(R.id.etCommentHere);
        ivReply = (ImageView) findViewById(R.id.ivReply);
        rv_click = (RelativeLayout) findViewById(R.id.rv_click);

    }

}
