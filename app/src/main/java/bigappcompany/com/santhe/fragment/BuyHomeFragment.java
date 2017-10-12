package bigappcompany.com.santhe.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.graphics.drawable.DrawerArrowDrawable;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.Toast;

import java.util.ArrayList;

import bigappcompany.com.santhe.R;
import bigappcompany.com.santhe.activity.DetailsActivity;
import bigappcompany.com.santhe.activity.HomeScreenActivity;
import bigappcompany.com.santhe.adapter.BuyContentAdapter;
import bigappcompany.com.santhe.adapter.BuyTypeAdapter;
import bigappcompany.com.santhe.interfaces.ClickListener;
import bigappcompany.com.santhe.model.DataCategory;
import bigappcompany.com.santhe.model.MyUpdatesDataPojo;
import bigappcompany.com.santhe.model.PersonalisePojo;
import bigappcompany.com.santhe.model.UpdatePojo;
import bigappcompany.com.santhe.network.DownlodableCallback;
import bigappcompany.com.santhe.network.RetrofitDataProvider;
import bigappcompany.com.santhe.other.RecyclerTouchListener;
import bigappcompany.com.santhe.utility.NetworkUtil;
import bigappcompany.com.santhe.utility.SantheUtility;

public class BuyHomeFragment extends Fragment {

    private View rootView;
    private DrawerArrowDrawable drawerArrowDrawable;
    private float offset;
    private boolean flipped;
    private DrawerLayout drawer;
    private RelativeLayout rvToggle, rvDiscussion, rvSell, rvBuy;
    private RecyclerView rcvContent, rcvType;
    private FragmentTransaction fragmentTransaction;
    private Fragment fragment;
    private RetrofitDataProvider retrofitDataProvider;
    private ArrayList<MyUpdatesDataPojo> dataBuyUpdates = new ArrayList<MyUpdatesDataPojo>();
    private ArrayList<DataCategory> dataPersonalise = new ArrayList<DataCategory>();
    private String categoryId = "";

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        rootView = inflater.inflate(R.layout.buy_home_fragment, null);
        initViews();
        eventListners();
        SetUpSlideMenu();
        SetUpDrawerLisener();
        retrofitDataProvider = new RetrofitDataProvider();

        try {
            hitApi();
        } catch (Exception e) {
            e.printStackTrace();
            SantheUtility.dismissProgressDialog();
        }

        try {

            hitbuyDetails();
        } catch (Exception e) {
            e.printStackTrace();
            SantheUtility.dismissProgressDialog();
        }

        return rootView;
    }

    private void hitbuyDetails() {


        if (NetworkUtil.getConnectivityStatusBoolen(getActivity())) {

            try {
                SantheUtility.showProgress(getActivity(), getResources().getString(R.string.pleaseWait));

                retrofitDataProvider.getBuyListing(categoryId, new DownlodableCallback<UpdatePojo>() {
                    @Override
                    public void onSuccess(UpdatePojo result) {

                        SantheUtility.dismissProgressDialog();

                        if (result.getStatus().contains("0")) {

                            settingListValues(result);

                        } else {
                            SantheUtility.dismissProgressDialog();
                            Toast.makeText(getActivity(), getResources().getString(R.string.NoData), Toast.LENGTH_SHORT).show();

                            dataBuyUpdates.clear();
                            BuyContentAdapter adapter = new BuyContentAdapter(getActivity(), dataBuyUpdates);
                            rcvContent.setAdapter(adapter);
                        }

                    }

                    @Override
                    public void onFailure(String error) {

                        Toast.makeText(getActivity(), getResources().getString(R.string.NoData), Toast.LENGTH_SHORT).show();
                        SantheUtility.dismissProgressDialog();

                    }
                });
            } catch (Exception e) {

                e.printStackTrace();
                SantheUtility.dismissProgressDialog();
            }
        } else {
            SantheUtility.displayMessageAlert(getResources().getString(R.string.network), getActivity());
        }
    }

    private void settingListValues(UpdatePojo result) {

        dataBuyUpdates.clear();
        dataBuyUpdates = (ArrayList<MyUpdatesDataPojo>) result.getData();
        if (dataBuyUpdates.size() > 0) {

//                                RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getActivity());

            GridLayoutManager mLayoutManager = new GridLayoutManager(getActivity(), 2);

            // Create a custom SpanSizeLookup where the first item spans both columns
            mLayoutManager.setSpanSizeLookup(new GridLayoutManager.SpanSizeLookup() {
                @Override
                public int getSpanSize(int position) {

                    if (dataBuyUpdates.size() > 4) {

                        if (position < 4) {
                            return 1;
                        }

                    } else if (dataBuyUpdates.size() > 2) {

                        if (position >= 2) {

                            return 2;

                        } else if (position < 2) {

                            return 1;

                        }
                    }

                    return 2;

                }
            });
            rcvContent.setLayoutManager(mLayoutManager);
            rcvContent.setItemAnimator(new DefaultItemAnimator());


            BuyContentAdapter adapter = new BuyContentAdapter(getActivity(), dataBuyUpdates);
            rcvContent.setAdapter(adapter);
        }
    }

    private void hitApi() {


        if (NetworkUtil.getConnectivityStatusBoolen(getActivity())) {

            SantheUtility.showProgress(getActivity(), getResources().getString(R.string.pleaseWait));

            retrofitDataProvider.getCategories(new DownlodableCallback<PersonalisePojo>() {
                @Override
                public void onSuccess(PersonalisePojo result) {

                    SantheUtility.dismissProgressDialog();

                    if (result.getStatus().contentEquals("true")) {
                        dataPersonalise.clear();
                        DataCategory dataCategory = new DataCategory();
                        dataCategory.setCat_name("All");
                        dataPersonalise.add(dataCategory);

                        if (null != result.getData()) {

                            if (result.getData().size() > 0) {
                                for (int i = 0; i < result.getData().size(); i++) {
                                    dataPersonalise.add(result.getData().get(i));
                                }
                            }
                        }
                        if (dataPersonalise.size() > 0) {

                            BuyTypeAdapter adapter = new BuyTypeAdapter(getActivity(), dataPersonalise);
                            rcvType.setAdapter(adapter);
                            adapter.notifyDataSetChanged();

                        } else {
                            Toast.makeText(getActivity(), getResources().getString(R.string.NoData), Toast.LENGTH_SHORT).show();
                        }
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

        rcvType.addOnItemTouchListener(new RecyclerTouchListener(getActivity(), rcvType, new ClickListener() {
            @Override
            public void onClick(View view, int position) {

                if (dataPersonalise.size() > 0) {
                    categoryId = dataPersonalise.get(position).getCat_id();
                    hitbuyDetails();
                }

            }

            @Override
            public void onLongClick(View view, int position) {

            }
        }));

        rcvContent.addOnItemTouchListener(new RecyclerTouchListener(getActivity(), rcvContent, new ClickListener() {
            @Override
            public void onClick(View view, int position) {

                if (dataBuyUpdates.size() > 0) {
                    Intent intent = new Intent(getActivity(), DetailsActivity.class);
                    intent.putExtra("productId", dataBuyUpdates.get(position).getProduct_id());
                    intent.putExtra("From", "BuyHomeFragment");
                    startActivity(intent);
                }
            }

            @Override
            public void onLongClick(View view, int position) {
            }
        }));

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
        rcvContent = rootView.findViewById(R.id.rcvContent);
//        rcvContent.addItemDecoration(new DividerItemDecoration(10));
        rcvType = rootView.findViewById(R.id.rcvType);
        rvDiscussion = rootView.findViewById(R.id.rvDiscussion);
        rvSell = rootView.findViewById(R.id.rvSell);
        rvBuy = rootView.findViewById(R.id.rvBuy);

//        rcvType.setLayoutManager(new GridLayoutManager(getActivity(), 4));
        rcvType.setLayoutManager(new LinearLayoutManager(getActivity(), LinearLayoutManager.HORIZONTAL, false));
//        rcvType.setLayoutManager(new GridLayoutManager(this, 4));

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
