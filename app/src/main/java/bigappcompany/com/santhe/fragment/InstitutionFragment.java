package bigappcompany.com.santhe.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.graphics.drawable.DrawerArrowDrawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

import bigappcompany.com.santhe.R;
import bigappcompany.com.santhe.activity.InstitutionDetailsActivity;
import bigappcompany.com.santhe.adapter.LocationListAdapter;
import bigappcompany.com.santhe.model.CitiesDataPojo;
import bigappcompany.com.santhe.model.CitiesPojo;
import bigappcompany.com.santhe.model.MenuListPojo;
import bigappcompany.com.santhe.network.DownlodableCallback;
import bigappcompany.com.santhe.network.RetrofitDataProvider;
import bigappcompany.com.santhe.utility.NetworkUtil;
import bigappcompany.com.santhe.utility.SantheUtility;

public class InstitutionFragment extends Fragment {

    List<CitiesDataPojo> cityData = new ArrayList<CitiesDataPojo>();
    ArrayList<MenuListPojo> ListOfLocations = new ArrayList<>();
    private View rootView;
    private ListView listView;
    private String cat_id = "";
    private RetrofitDataProvider retrofitDataProvider;
    private RelativeLayout rvToggle;
    private DrawerArrowDrawable drawerArrowDrawable;
    private float offset;
    private boolean flipped;
    private DrawerLayout drawer;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        rootView = inflater.inflate(R.layout.institution_fragment, null);
        initViews();
        eventListners();
        SetUpSlideMenu();
        SetUpDrawerLisener();
        retrofitDataProvider = new RetrofitDataProvider();


//        if (getArguments().getString("Category_type") != null) {
//
//            cat_id = getArguments().getString("Category_type");
//        }

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

        retrofitDataProvider.getCitiesDetail("institute","", new DownlodableCallback<CitiesPojo>() {
            @Override
            public void onSuccess(CitiesPojo result) {
                SantheUtility.dismissProgressDialog();
                if (result.getStatus().contains("true")) {

                    cityData.clear();
                    cityData = result.getData();
                    settingListValues(result.getData());

                } else {

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

    private void settingListValues(List<CitiesDataPojo> data) {
        MenuListPojo menupojo;
//        String[] items = new String[data.size()];
        for (int i = 0; i < data.size(); i++) {
 
            if (data.get(i).getLocation_status().equalsIgnoreCase("true")) {
                menupojo = new MenuListPojo();
                menupojo.setItem(data.get(i).getCity_name());
                menupojo.setCity_id(data.get(i).getCity_id());
    
                ListOfLocations.add(menupojo);
//            if(data.get(i).getCity_name()!=null) {
//
//                items[i] = data.get(i).getCity_name();
//            }
            }
        }

//        if (isAdded()) {
        LocationListAdapter menuListAdapter = new LocationListAdapter(getActivity(), ListOfLocations);
        listView.setAdapter(menuListAdapter);

//            listView.setAdapter(new ArrayAdapter<String>(getActivity(), android.R.layout.simple_list_item_1, items));
//        }
    }

    private void initViews() {
        listView = rootView.findViewById(R.id.listView);
        rvToggle = rootView.findViewById(R.id.rv_click);
    }

    private void eventListners() {

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {

                Intent intent = new Intent(getActivity(), InstitutionDetailsActivity.class);
                intent.putExtra("citi_id", ListOfLocations.get(position).getCity_id());
//                intent.putExtra("category_id", cat_id);
                startActivity(intent);
            }
        });

        rvToggle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DrawerMenuStatus();
            }
        });

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


    private void DrawerMenuStatus() {
        if (drawer.isDrawerVisible(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            drawer.openDrawer(GravityCompat.START);
        }
    }


}
