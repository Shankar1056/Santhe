package bigappcompany.com.santhe.fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.graphics.drawable.DrawerArrowDrawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.Toast;

import java.util.ArrayList;

import bigappcompany.com.santhe.R;
import bigappcompany.com.santhe.activity.HomeScreenActivity;
import bigappcompany.com.santhe.model.DataCategory;
import bigappcompany.com.santhe.model.PersonalisePojo;
import bigappcompany.com.santhe.network.DownlodableCallback;
import bigappcompany.com.santhe.network.RetrofitDataProvider;
import bigappcompany.com.santhe.utility.NetworkUtil;
import bigappcompany.com.santhe.utility.SantheUtility;


public class UsefulInformationFragment extends Fragment {

    private View rootView;
    private DrawerArrowDrawable drawerArrowDrawable;
    private float offset;
    private boolean flipped;
    private DrawerLayout drawer;
    private TabLayout tabLayout;
    private RelativeLayout rvToggle;
    private ViewPager contentViewPager;
    private RetrofitDataProvider retrofitDataProvider;
    private ArrayList<DataCategory> dataPersonalise = new ArrayList<DataCategory>();


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        rootView = inflater.inflate(R.layout.usefull_information_fragment, null);
        retrofitDataProvider = new RetrofitDataProvider();

        initViews();
        eventListners();
        SetUpSlideMenu();
        SetUpDrawerLisener();

        try {

            if (NetworkUtil.getConnectivityStatusBoolen(getActivity())) {
                hitApi();
            } else {
                SantheUtility.displayMessageAlert(getResources().getString(R.string.network), getActivity());
            }

        } catch (Exception e) {
            e.printStackTrace();
            SantheUtility.dismissProgressDialog();
        }
        return rootView;
    }

    private void hitApi() {
        SantheUtility.showProgress(getActivity(), getResources().getString(R.string.pleaseWait));
        retrofitDataProvider.getUfiCategories(new DownlodableCallback<PersonalisePojo>() {
            @Override
            public void onSuccess(PersonalisePojo result) {

                SantheUtility.dismissProgressDialog();

                if (result.getStatus().contentEquals("true")) {

                    dataPersonalise.clear();
                    dataPersonalise = result.getData();

                    if (dataPersonalise.size() > 0) {
                        settingValues();
                    } else {
                        SantheUtility.displayMessageAlert("No Categories found", getActivity());
                    }

                } else {

                    SantheUtility.dismissProgressDialog();
                    Toast.makeText(getActivity(), getResources().getString(R.string.NoData), Toast.LENGTH_SHORT).show();

                }

            }

            @Override
            public void onFailure(String error) {

                SantheUtility.dismissProgressDialog();
                Toast.makeText(getActivity(), getResources().getString(R.string.something), Toast.LENGTH_SHORT).show();


            }
        });
    }

    private void settingValues() {

        for (int i = 0; i < dataPersonalise.size(); i++) {

            tabLayout.addTab(tabLayout.newTab().setText(dataPersonalise.get(i).getCat_id()));
        }

        ViewPagerAdapter adapter = new ViewPagerAdapter(getChildFragmentManager(), tabLayout.getTabCount(), dataPersonalise);
        contentViewPager.setAdapter(adapter);


        tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                contentViewPager.setCurrentItem(tab.getPosition());
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {
            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {
            }
        });

        contentViewPager.addOnPageChangeListener(new TabLayout.TabLayoutOnPageChangeListener(tabLayout));


    }

    private void eventListners() {

        rvToggle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                ((HomeScreenActivity) getActivity()).callBuyFragment();
            }
        });

    }



    private void initViews() {
        contentViewPager = rootView.findViewById(R.id.content_viewpager);
        rvToggle = rootView.findViewById(R.id.rv_click);
        tabLayout = rootView.findViewById(R.id.tablayout);


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

    public class ViewPagerAdapter extends FragmentStatePagerAdapter {
        int mNumOfTabs;
        Fragment fragment = null;
        ArrayList<DataCategory> dataPersonalise;

        public ViewPagerAdapter(FragmentManager fm, int NumOfTabs, ArrayList<DataCategory> dataPersonalise) {
            super(fm);
            this.mNumOfTabs = NumOfTabs;
            this.dataPersonalise = dataPersonalise;
        }

        @Override
        public Fragment getItem(int position) {

            for (int i = 0; i < mNumOfTabs; i++) {

                if (i == position) {
                    fragment = new FruitFragment();
                    Bundle args = new Bundle();
                    args.putString("Category_type", dataPersonalise.get(position).getCat_id());
                    fragment.setArguments(args);
                    break;
                }
            }
            return fragment;

        }

        @Override
        public int getCount() {
            return mNumOfTabs;
        }

        @Override
        public CharSequence getPageTitle(int position) {
            return super.getPageTitle(position);
        }
    }


}
