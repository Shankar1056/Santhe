package bigappcompany.com.santhe.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.graphics.drawable.DrawerArrowDrawable;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;

import java.util.ArrayList;
import java.util.List;

import bigappcompany.com.santhe.R;
import bigappcompany.com.santhe.activity.DetailsActivity;
import bigappcompany.com.santhe.activity.HomeScreenActivity;
import bigappcompany.com.santhe.adapter.UpdatesAdapter;
import bigappcompany.com.santhe.interfaces.ClickListener;
import bigappcompany.com.santhe.model.MyUpdatesDataPojo;
import bigappcompany.com.santhe.model.UpdatePojo;
import bigappcompany.com.santhe.network.DownlodableCallback;
import bigappcompany.com.santhe.network.RetrofitDataProvider;
import bigappcompany.com.santhe.other.RecyclerTouchListener;
import bigappcompany.com.santhe.utility.NetworkUtil;
import bigappcompany.com.santhe.utility.SantheUtility;
import bigappcompany.com.santhe.utility.SharedPreferencesUtility;

public class UpdateFragment extends Fragment {
	
	private View rootView;
	private DrawerArrowDrawable drawerArrowDrawable;
	private float offset;
	private boolean flipped;
	private DrawerLayout drawer;
	private RelativeLayout rvToggle;
	private RecyclerView recyclerView;
	private RetrofitDataProvider retrofitDataProvider;
	private List<MyUpdatesDataPojo> updateList;


	@Nullable
	@Override
	public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
		super.onCreateView(inflater, container, savedInstanceState);
		rootView = inflater.inflate(R.layout.update_fragment, null);
		initViews();
		eventListners();
		retrofitDataProvider = new RetrofitDataProvider();
		SetUpSlideMenu();
		SetUpDrawerLisener();
		return rootView;
	}

	@Override
	public void onResume() {
		super.onResume();
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

	}

	private void hitApi() {
		SantheUtility.showProgress(getActivity(), getResources().getString(R.string.pleaseWait));
		retrofitDataProvider.getMyUpdates(SharedPreferencesUtility.getAuthkey(getActivity()),
				new DownlodableCallback<UpdatePojo>() {
					@Override
					public void onSuccess(UpdatePojo result) {

						SantheUtility.dismissProgressDialog();
						if (result.getStatus().contains("0")) {
							SantheUtility.dismissProgressDialog();
							updateList = new ArrayList<MyUpdatesDataPojo>();
							updateList = result.getData();
							settingValues(updateList);

						} else {

							SantheUtility.displayMessageAlert(result.getMessage(), getActivity());
							updateList = new ArrayList<MyUpdatesDataPojo>();
							settingValues(updateList);
						}

					}

					@Override
					public void onFailure(String error) {
						SantheUtility.displayMessageAlert(error, getActivity());
						SantheUtility.dismissProgressDialog();
					}
				});

	}

	private void settingValues(List<MyUpdatesDataPojo> data) {
		RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getActivity());
		recyclerView.setLayoutManager(mLayoutManager);
		recyclerView.setItemAnimator(new DefaultItemAnimator());
		UpdatesAdapter adaptor = new UpdatesAdapter(getActivity(), data);
		recyclerView.setAdapter(adaptor);
	}

	private void eventListners() {
		
		rvToggle.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {

				((HomeScreenActivity) getActivity()).callBuyFragment();
            }
		});


		recyclerView.addOnItemTouchListener(new RecyclerTouchListener(getActivity(), recyclerView, new ClickListener() {
			@Override
			public void onClick(View view, int position) {

				Intent intent= new Intent(getActivity(), DetailsActivity.class);
				intent.putExtra("productId", updateList.get(position).getProduct_id());
				intent.putExtra("From", "MyUpdateFragment");
				startActivity(intent);
			}

			@Override
			public void onLongClick(View view, int position) {

			}
		}));
		
		
	}


	private void initViews() {

		rvToggle = rootView.findViewById(R.id.rv_click);
		recyclerView=rootView.findViewById(R.id.recyclerView);
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
