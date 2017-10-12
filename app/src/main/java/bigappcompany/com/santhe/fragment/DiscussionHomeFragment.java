package bigappcompany.com.santhe.fragment;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.ColorStateList;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.graphics.drawable.DrawerArrowDrawable;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupMenu;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

import bigappcompany.com.santhe.R;
import bigappcompany.com.santhe.activity.CommentsActivity;
import bigappcompany.com.santhe.activity.HomeScreenActivity;
import bigappcompany.com.santhe.activity.PostActivity;
import bigappcompany.com.santhe.activity.PostEditActivity;
import bigappcompany.com.santhe.adapter.BuyTypeAdapter;
import bigappcompany.com.santhe.adapter.DiscussionAdapter;
import bigappcompany.com.santhe.interfaces.ClickListener;
import bigappcompany.com.santhe.model.CommentPojo;
import bigappcompany.com.santhe.model.DataCategory;
import bigappcompany.com.santhe.model.DiscussionDataIPojo;
import bigappcompany.com.santhe.model.DiscussionPojo;
import bigappcompany.com.santhe.model.MobileRegisterPojo;
import bigappcompany.com.santhe.model.PersonalisePojo;
import bigappcompany.com.santhe.network.DownlodableCallback;
import bigappcompany.com.santhe.network.RetrofitDataProvider;
import bigappcompany.com.santhe.other.RecyclerTouchListener;
import bigappcompany.com.santhe.utility.NetworkUtil;
import bigappcompany.com.santhe.utility.SantheUtility;
import bigappcompany.com.santhe.utility.SharedPreferencesUtility;

public class DiscussionHomeFragment extends Fragment {

    public static final int PAGE_SIZE = 10;
    List<DiscussionDataIPojo> postList = new ArrayList<DiscussionDataIPojo>();
    private View rootView;
    private DrawerArrowDrawable drawerArrowDrawable;
    private float offset;
    private boolean flipped;
    private DrawerLayout drawer;
    private RelativeLayout rvToggle, rvDiscussion, rvSell, rvBuy;
    private RecyclerView rcvContent;
    private ImageView ivPost;
    private RetrofitDataProvider retrofitDataProvider;
    private EditText etComment;
    private int clickedPosition = 0;
    private ImageView ivOption;
    private ArrayList<DataCategory> dataPersonalise = new ArrayList<DataCategory>();
    private RecyclerView rcvType;
    private String categoryId = "";
    private FloatingActionButton btnFab;
    private TextView tvNoOfComments;
    private LinearLayout llcontentLayout;
    private LinearLayoutManager mLayoutManager;
    private int currentPage = 0;
    private DiscussionAdapter contentAdaptor;
    private boolean isLoading = false;
    private boolean isLastPage = false;
    private RecyclerView.OnScrollListener recyclerViewOnScrollListener = new RecyclerView.OnScrollListener() {
        @Override
        public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
            super.onScrollStateChanged(recyclerView, newState);
        }

        @Override
        public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
            super.onScrolled(recyclerView, dx, dy);
            int visibleItemCount = mLayoutManager.getChildCount();
            int totalItemCount = mLayoutManager.getItemCount();
            int firstVisibleItemPosition = mLayoutManager.findFirstVisibleItemPosition();


            if (!isLoading && !isLastPage) {
                if ((visibleItemCount + firstVisibleItemPosition) >= totalItemCount
                        && firstVisibleItemPosition >= 0
                        && totalItemCount >= PAGE_SIZE) {
//                    load


                    settingValues(currentPage);
                }
            }

        }
    };

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        rootView = inflater.inflate(R.layout.discussion_home_fragment, null);
        initViews();
        eventListners();
        retrofitDataProvider = new RetrofitDataProvider();
        SetUpSlideMenu();
        SetUpDrawerLisener();
        postList.clear();

        if (NetworkUtil.getConnectivityStatusBoolen(getActivity())) {
            hitPersonaliseApi();
        } else {
            SantheUtility.displayMessageAlert(getResources().getString(R.string.network), getActivity());
        }
        return rootView;
    }

    @Override
    public void onResume() {
        super.onResume();
        postList.clear();
        contentAdaptor.clearAllItems();
        currentPage = 0;
        rcvContent.addOnScrollListener(recyclerViewOnScrollListener);
        settingValues();

    }

    private void hitPersonaliseApi() {

        SantheUtility.showProgress(getActivity(), getResources().getString(R.string.pleaseWait));

        retrofitDataProvider.getCategories(new DownlodableCallback<PersonalisePojo>() {
            @Override
            public void onSuccess(PersonalisePojo result) {

                SantheUtility.dismissProgressDialog();

                if (result.getStatus().contentEquals("true")) {

                    dataPersonalise.clear();
                    DataCategory dataCategory = new DataCategory();
                    dataCategory.setCat_name("All");
                    dataCategory.setCat_id("");
                    dataPersonalise.add(dataCategory);

                    if (null != result.getData()) {

                        if (result.getData().size() > 0) {

                            for (int i = 0; i < result.getData().size(); i++) {
                                dataPersonalise.add(result.getData().get(i));
                            }
                        }
                    }

                    if (dataPersonalise.size() > 0) {

                        rcvType.setLayoutManager(new GridLayoutManager(getActivity(), 4));
                        rcvType.setLayoutManager(new LinearLayoutManager(getActivity(), LinearLayoutManager.HORIZONTAL, false));
                        BuyTypeAdapter adapter = new BuyTypeAdapter(getActivity(), dataPersonalise);
                        rcvType.setAdapter(adapter);

                        adapter.notifyDataSetChanged();

                    } else {

                        Toast.makeText(getActivity(), getResources().getString(R.string.NoData), Toast.LENGTH_SHORT).show();
                    }

                } else {

                    Toast.makeText(getActivity(), result.getMessage(), Toast.LENGTH_SHORT).show();
                    SantheUtility.dismissProgressDialog();
                }

            }

            @Override
            public void onFailure(String error) {
                SantheUtility.dismissProgressDialog();
                Toast.makeText(getActivity(), getResources().getString(R.string.something), Toast.LENGTH_SHORT).show();

            }
        });
    }

    private void settingValues(int count) {

        isLoading = true;
        currentPage += 1;

        if (NetworkUtil.getConnectivityStatusBoolen(getActivity())) {

            SantheUtility.showProgress(getActivity(), getResources().getString(R.string.pleaseWait));
            retrofitDataProvider.getDiscussiomListing("" + currentPage, categoryId, new DownlodableCallback<DiscussionPojo>() {
                @Override
                public void onSuccess(DiscussionPojo result) {
                    isLoading = false;
                    SantheUtility.dismissProgressDialog();


                    if (result.getStatus().contains("0")) {

                        if (result.getData().size() > 0) {

                            postList = result.getData();
                            postList.addAll(result.getData());
                            contentAdaptor.addItems(result.getData());

                        }
                    } else if ((result.getStatus().contains("-1"))) {
                        isLoading = false;

                        Toast.makeText(getActivity(), result.getMessage(), Toast.LENGTH_SHORT).show();
                        postList.clear();
                        contentAdaptor.clearAllItems();
//                        Toast.makeText(getActivity(), getResources().getString(R.string.NoData), Toast.LENGTH_SHORT).show();

                        SantheUtility.dismissProgressDialog();

                    } else if (result.getStatus().contains("-2")) {
                        isLoading = false;
                        currentPage--;
                        isLastPage = true;

                    }
                }

                @Override
                public void onFailure(String error) {
                    SantheUtility.dismissProgressDialog();
                    Toast.makeText(getActivity(), getResources().getString(R.string.something), Toast.LENGTH_SHORT).show();
                }
            });
        } else {
            SantheUtility.displayMessageAlert(getResources().getString(R.string.network), getActivity());
        }
    }

    private void settingValues() {

        isLoading = false;


        if (NetworkUtil.getConnectivityStatusBoolen(getActivity())) {

            SantheUtility.showProgress(getActivity(), getResources().getString(R.string.pleaseWait));

            retrofitDataProvider.getDiscussiomListing("" + currentPage, categoryId, new DownlodableCallback<DiscussionPojo>() {
                @Override
                public void onSuccess(DiscussionPojo result) {

                    SantheUtility.dismissProgressDialog();

                    if (result.getStatus().contains("0")) {

                        if (result.getData().size() > 0) {

                            postList.addAll(result.getData());
                            contentAdaptor.addItems(result.getData());
                        }

                        if (result.getData().size() >= PAGE_SIZE) {

                        } else {
                            isLastPage = true;
                        }
                    } else if ((result.getStatus().contains("-1"))) {

                        Toast.makeText(getActivity(), result.getMessage(), Toast.LENGTH_SHORT).show();
                        postList.clear();
                        contentAdaptor.clearAllItems();
                        SantheUtility.dismissProgressDialog();

                    } else if (result.getStatus().contains("-2")) {

                        currentPage--;
                        isLastPage = true;
                        isLoading = false;

                    }
                }

                @Override
                public void onFailure(String error) {
                    SantheUtility.dismissProgressDialog();
                    Toast.makeText(getActivity(), getResources().getString(R.string.something), Toast.LENGTH_SHORT).show();
                }
            });
        } else {
            SantheUtility.displayMessageAlert(getResources().getString(R.string.network), getActivity());
        }
    }

    private void eventListners() {

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


        rcvContent.addOnItemTouchListener(new RecyclerTouchListener(getActivity(), rcvContent, new ClickListener() {
            @Override
            public void onClick(View view, final int position) {

                clickedPosition = position;
                tvNoOfComments = view.findViewById(R.id.tvCommentsCount);
                etComment = view.findViewById(R.id.etCommentHere);
                final ImageView ivSend = view.findViewById(R.id.ivSend);
                llcontentLayout = view.findViewById(R.id.llcontentLayout);

                ivOption = view.findViewById(R.id.ivOption);

                if (postList.get(position).getUser_id().contains(SharedPreferencesUtility.getAuthkey(getActivity()))) {

                    ivOption.setVisibility(View.VISIBLE);

                } else {

                    ivOption.setVisibility(View.GONE);

                }

                llcontentLayout.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {

                        Intent intent = new Intent(getActivity(), CommentsActivity.class);
                        intent.putExtra("post_id", postList.get(position).getDpId());
                        startActivity(intent);

                    }
                });

                tvNoOfComments.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {

                        Intent intent = new Intent(getActivity(), CommentsActivity.class);
                        intent.putExtra("post_id", postList.get(position).getDpId());
                        startActivity(intent);
                    }
                });


                ivOption.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {

                        PopupMenu popup = new PopupMenu(getActivity(), view);
                        MenuInflater inflater = popup.getMenuInflater();
                        inflater.inflate(R.menu.main, popup.getMenu());
                        popup.show();

                        popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                            public boolean onMenuItemClick(MenuItem item) {
                                if (item.getItemId() == R.id.menu_Edit) {

                                    Intent intent = new Intent(getActivity(), PostEditActivity.class);
                                    intent.putExtra("postDetail", postList.get(position));
                                    startActivity(intent);


                                } else if (item.getItemId() == R.id.menu_Delete) {

                                    android.app.AlertDialog.Builder alertDialogBuilder = new android.app.AlertDialog.Builder(getActivity());
                                    alertDialogBuilder.setMessage("You really want to Delete ?").setCancelable(false).setPositiveButton("Yes",
                                            new DialogInterface.OnClickListener() {
                                                public void onClick(DialogInterface dialog, int id) {


                                                    if (NetworkUtil.getConnectivityStatusBoolen(getActivity())) {

                                                        HitDeleteApi(postList.get(position).getDpId());
                                                    } else {
                                                        SantheUtility.displayMessageAlert(getResources().getString(R.string.network), getActivity());

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

                                return true;
                            }

                        });

                    }
                });

                ivSend.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        sendComment();
                        hideKeypad();
                    }
                });

            }

            @Override
            public void onLongClick(View view, int position) {

            }
        }));

        btnFab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent intent = new Intent(getActivity(), PostActivity.class);
                startActivity(intent);

            }
        });

        rcvType.addOnItemTouchListener(new RecyclerTouchListener(getActivity(), rcvType, new ClickListener() {
            @Override
            public void onClick(View view, int position) {

                isLastPage = false;

                if (dataPersonalise.size() > 0) {

                    Log.d("Raki", "categoryId  :" + categoryId + "data from server :" + dataPersonalise.get(position).getCat_id());

                    if (!categoryId.contentEquals(dataPersonalise.get(position).getCat_id())) {

                        currentPage = 0;
                        contentAdaptor.clearAllItems();
                        postList.clear();
                        categoryId = dataPersonalise.get(position).getCat_id();
                        settingValues();


                        Log.d("Raki", "I am In" + currentPage);
                    } else {

                        settingValues();

                    }



                }
            }

            @Override
            public void onLongClick(View view, int position) {

            }
        }));


    }

    private void hideKeypad() {

        InputMethodManager inputManager = (InputMethodManager) getActivity().getSystemService(getActivity().INPUT_METHOD_SERVICE);
        inputManager.hideSoftInputFromWindow(getActivity().getCurrentFocus().getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
    }

    private void HitDeleteApi(String postId) {

        try {

            SantheUtility.showProgress(getActivity(), getResources().getString(R.string.pleaseWait));

            retrofitDataProvider.deleteDiscussionItem(postId, new DownlodableCallback<MobileRegisterPojo>() {
                @Override
                public void onSuccess(MobileRegisterPojo result) {
                    SantheUtility.dismissProgressDialog();
                    if (result.getStatus().contains("0")) {

                        settingValues();
                    } else {

                        SantheUtility.displayMessageAlert(getResources().getString(R.string.unable), getActivity());
                        SantheUtility.dismissProgressDialog();
                    }
                }

                @Override
                public void onFailure(String error) {
                    SantheUtility.displayMessageAlert(getResources().getString(R.string.something), getActivity());
                    SantheUtility.dismissProgressDialog();

                }
            });
        } catch (Exception e) {

            e.printStackTrace();
            SantheUtility.dismissProgressDialog();
        }

    }

    private void sendComment() {

        if (etComment.getText().toString().length() > 0) {

            retrofitDataProvider.commentOnPost(SharedPreferencesUtility.getAuthkey(getActivity()),
                    postList.get(clickedPosition).getDpId(), etComment.getText().toString().trim(), new DownlodableCallback<CommentPojo>() {

                        @Override
                        public void onSuccess(CommentPojo result) {

                            SantheUtility.dismissProgressDialog();
                            if (result.getStatus().contains("0")) {

                                etComment.setText("");

                                tvNoOfComments.setText(result.getCount() + " comments");


                                Toast.makeText(getActivity(), result.getMessage(), Toast.LENGTH_SHORT).show();
                            } else {

                                Toast.makeText(getActivity(), result.getMessage(), Toast.LENGTH_SHORT).show();
                            }
                        }

                        @Override
                        public void onFailure(String error) {

                            SantheUtility.dismissProgressDialog();
                            Toast.makeText(getActivity(), getResources().getString(R.string.something), Toast.LENGTH_SHORT).show();
                        }
                    });
        }
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
        rcvContent = rootView.findViewById(R.id.rcvContent);
        rcvType = rootView.findViewById(R.id.rcvType);
        btnFab = rootView.findViewById(R.id.btnFab);
        btnFab.setBackgroundTintList(ColorStateList.valueOf(getResources().getColor(R.color.colorOrange)));

//        mLayoutManager = new LinearLayoutManager(getActivity());
        mLayoutManager = new LinearLayoutManager(getActivity(), LinearLayoutManager.VERTICAL, false);
        rcvContent.setLayoutManager(mLayoutManager);
        rcvContent.setItemAnimator(new DefaultItemAnimator());
        contentAdaptor = new DiscussionAdapter();
        rcvContent.setAdapter(contentAdaptor);
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


}
