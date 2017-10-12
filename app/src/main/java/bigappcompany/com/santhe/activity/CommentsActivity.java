package bigappcompany.com.santhe.activity;

import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.TextPaint;
import android.text.method.LinkMovementMethod;
import android.text.style.ClickableSpan;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.RequestOptions;
import com.bumptech.glide.request.target.Target;

import java.util.ArrayList;
import java.util.List;

import bigappcompany.com.santhe.R;
import bigappcompany.com.santhe.adapter.CommentsAdapter;
import bigappcompany.com.santhe.interfaces.ClickListener;
import bigappcompany.com.santhe.model.CommentPojo;
import bigappcompany.com.santhe.model.CommentsDataPojo;
import bigappcompany.com.santhe.model.CommentsItem;
import bigappcompany.com.santhe.model.CommentsPojo;
import bigappcompany.com.santhe.model.Likepojo;
import bigappcompany.com.santhe.network.DownlodableCallback;
import bigappcompany.com.santhe.network.RetrofitDataProvider;
import bigappcompany.com.santhe.other.RecyclerTouchListener;
import bigappcompany.com.santhe.utility.NetworkUtil;
import bigappcompany.com.santhe.utility.SantheUtility;
import bigappcompany.com.santhe.utility.SharedPreferencesUtility;
import uk.co.chrisjenx.calligraphy.CalligraphyConfig;
import uk.co.chrisjenx.calligraphy.CalligraphyContextWrapper;

public class CommentsActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private RelativeLayout rvClick;
    private RetrofitDataProvider retrofitDataProvider;
    private List<CommentsItem> listComments = new ArrayList<CommentsItem>();
    private String postId = "";
    private int clickedPosition = 0;
    private String commentId = "";
    private TextView tvlikeCount, tvHeading, tvDescription;
    private ImageView ivProduct, ivReply, ivProfilePicture;
    private ProgressBar progress;
    private RelativeLayout rvProduct;
    private EditText etComment;
    private TextView tvPostedTime, tvProfileName;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Window window = this.getWindow();
        window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            window.setStatusBarColor(ContextCompat.getColor(this, R.color.colorGreen));
        }
        setContentView(R.layout.comments_activity);

        CalligraphyConfig.initDefault(new CalligraphyConfig.Builder()
                .setDefaultFontPath("fonts/Pangram-Regular.otf")
                .setFontAttrId(R.attr.fontPath)
                .build()
        );
        retrofitDataProvider = new RetrofitDataProvider();


        postId = getIntent().getExtras().getString("post_id");


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

        if (NetworkUtil.getConnectivityStatusBoolen(CommentsActivity.this)) {
            HitCommentsAPi();
        } else {
            SantheUtility.displayMessageAlert(getResources().getString(R.string.network), CommentsActivity.this);
        }

    }

    private void eventListerns() {

        rvClick.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        ivReply.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (NetworkUtil.getConnectivityStatusBoolen(CommentsActivity.this)) {
                    sendComment();
                } else {
                    SantheUtility.displayMessageAlert(getResources().getString(R.string.network), CommentsActivity.this);
                }
                hideKeypad();


            }


        });


        recyclerView.addOnItemTouchListener(new RecyclerTouchListener(this, recyclerView, new ClickListener() {
            @Override
            public void onClick(View itemView, final int position) {


                clickedPosition = position;

                commentId = listComments.get(position).getCommentId();
//


                final TextView tvlike = itemView.findViewById(R.id.tvlike);
                final TextView tvRply = itemView.findViewById(R.id.tvRply);
                tvlikeCount = itemView.findViewById(R.id.tvlikeCount);


                tvRply.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {

                        Intent intent = new Intent(CommentsActivity.this, ReplyActivity.class);
                        intent.putExtra("comment_id", listComments.get(position).getCommentId());
                        startActivity(intent);
                    }
                });


                tvlike.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {

                        if (NetworkUtil.getConnectivityStatusBoolen(CommentsActivity.this)) {
                            HitLike();
                        } else {
                            SantheUtility.displayMessageAlert(getResources().getString(R.string.network), CommentsActivity.this);
                        }

                    }
                });

            }

            @Override
            public void onLongClick(View view, int position) {

            }
        }));
    }

    private void HitLike() {
        try {
            SantheUtility.showProgress(this, getResources().getString(R.string.pleaseWait));


            retrofitDataProvider.likeComment(commentId, new DownlodableCallback<Likepojo>() {
                @Override
                public void onSuccess(Likepojo result) {

                    if (result.getStatus().contains("0")) {

                        SantheUtility.dismissProgressDialog();
                        tvlikeCount.setText(result.getLikeCount());

                    } else {
                        Toast.makeText(getApplicationContext(), getResources().getString(R.string.Unable), Toast.LENGTH_SHORT).show();
                    }

                }

                @Override
                public void onFailure(String error) {

                    SantheUtility.dismissProgressDialog();

                    Toast.makeText(getApplicationContext(), getResources().getString(R.string.something), Toast.LENGTH_SHORT).show();

                }
            });

        } catch (Exception e) {
            SantheUtility.dismissProgressDialog();
            e.printStackTrace();
        }


    }

    private void sendComment() {

        if (etComment.getText().toString().length() > 0) {

            retrofitDataProvider.commentOnPost(SharedPreferencesUtility.getAuthkey(this),
                    postId, etComment.getText().toString().trim(), new DownlodableCallback<CommentPojo>() {

                        @Override
                        public void onSuccess(CommentPojo result) {

                            SantheUtility.dismissProgressDialog();
                            if (result.getStatus().contains("0")) {

                                etComment.setText("");

                                if (NetworkUtil.getConnectivityStatusBoolen(CommentsActivity.this)) {
                                    HitCommentsAPi();
                                } else {
                                    SantheUtility.displayMessageAlert(getResources().getString(R.string.network), CommentsActivity.this);
                                }

//                                Toast.makeText(getApplicationContext(), result.getMessage(), Toast.LENGTH_SHORT).show();
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
        }
    }


    private void hideKeypad() {

        InputMethodManager inputManager = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
        inputManager.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
    }


    private void initViews() {

        rvClick = (RelativeLayout) findViewById(R.id.rvClick);
        recyclerView = (RecyclerView) findViewById(R.id.recyclerView);

//        tvHeading = (TextView) findViewById(R.id.tvHeading);

        tvPostedTime = (TextView) findViewById(R.id.tvPostedTime);
        tvProfileName = (TextView) findViewById(R.id.tvProfileName);
        ivProfilePicture = (ImageView) findViewById(R.id.ivProfilePicture);

        tvDescription = (TextView) findViewById(R.id.tvDescription);
        ivProduct = (ImageView) findViewById(R.id.ivProduct);
        progress = (ProgressBar) findViewById(R.id.progress);
        rvProduct = (RelativeLayout) findViewById(R.id.rvProduct);
        etComment = (EditText) findViewById(R.id.etCommentHere);
        ivReply = (ImageView) findViewById(R.id.ivReply);

    }

    private void HitCommentsAPi() {

        SantheUtility.showProgress(this, getResources().getString(R.string.pleaseWait));

        try {

            retrofitDataProvider.listOfComments(postId, new DownlodableCallback<CommentsPojo>() {
                @Override
                public void onSuccess(CommentsPojo result) {

                    SantheUtility.dismissProgressDialog();

                    if (result.getStatus().contains("0")) {

                        CommentsDataPojo commentsPojo = result.getData();

                        settingValues(commentsPojo);

                        listComments = result.getData().getComments();
                        if (result.getData().getComments().size() > 0) {

//                            tvHeading.setText(result.getData().getComments().size() + " people comment");
                        }
                        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(CommentsActivity.this);
                        recyclerView.setLayoutManager(mLayoutManager);
                        recyclerView.setItemAnimator(new DefaultItemAnimator());
                        CommentsAdapter adaptor = new CommentsAdapter(CommentsActivity.this, listComments);
                        recyclerView.setAdapter(adaptor);

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
    }

    private void settingValues(final CommentsDataPojo commentsPojo) {

        tvPostedTime.setText(commentsPojo.getPostedOn());
        Glide.with(this)
                .load(commentsPojo.getProfile_img()).apply(RequestOptions.circleCropTransform()).into(ivProfilePicture);
        tvProfileName.setText(commentsPojo.getPosted_by());


        if (commentsPojo.getDescription().length() >= 50) {

            String s = commentsPojo.getDescription();
            s = s.substring(0, Math.min(s.length(), 50));

            SpannableString ssPhone = new SpannableString(s + " Continue Reading");
            ClickableSpan clickableSpan = new ClickableSpan() {
                @Override
                public void onClick(View textView) {
                    tvDescription.setText(commentsPojo.getDescription().trim());
                }

                @Override
                public void updateDrawState(TextPaint ds) {
                    super.updateDrawState(ds);
                    ds.setUnderlineText(false);
                    ds.setColor(getResources().getColor(R.color.colorOrange));
                    ds.setTextSize(28);

                }
            };

            ssPhone.setSpan(clickableSpan, 50, 67, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
            tvDescription.setText(ssPhone);
            tvDescription.setMovementMethod(LinkMovementMethod.getInstance());
            tvDescription.setHighlightColor(getResources().getColor(R.color.colorOrange));
        } else {

            tvDescription.setText(commentsPojo.getDescription().trim());
        }


        if (commentsPojo.getPostImage().length() > 0) {

            rvProduct.setVisibility(View.VISIBLE);
            Glide.with(this)
                    .load(commentsPojo.getPostImage())
                    .listener(new RequestListener<Drawable>() {
                        @Override
                        public boolean onLoadFailed(@Nullable GlideException e, Object model, Target<Drawable> target, boolean isFirstResource) {
                            progress.setVisibility(View.GONE);
                            return false;
                        }

                        @Override
                        public boolean onResourceReady(Drawable resource, Object model, Target<Drawable> target, DataSource dataSource, boolean isFirstResource) {
                            progress.setVisibility(View.GONE);
                            return false;
                        }
                    })
                    .into(ivProduct);
        } else {

            rvProduct.setVisibility(View.GONE);

        }
    }
}
