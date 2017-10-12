package bigappcompany.com.santhe.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.TextView;

import com.viewpagerindicator.CirclePageIndicator;

import bigappcompany.com.santhe.R;
import uk.co.chrisjenx.calligraphy.CalligraphyConfig;
import uk.co.chrisjenx.calligraphy.CalligraphyContextWrapper;


public class FirstTimeDetailActivity extends AppCompatActivity {
	ViewPager viewPager;
	private LayoutInflater inflater;
	private CirclePageIndicator indicator;
	private TextView tvNext, tvPrevious;
	private int positionOfPager;
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		Window window = this.getWindow();
		window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
		window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
			window.setStatusBarColor(ContextCompat.getColor(this,R.color.colorWhite));
		}

		setContentView(R.layout.first_time_detail_activity);
		CalligraphyConfig.initDefault(new CalligraphyConfig.Builder()
			.setDefaultFontPath("fonts/Pangram-Regular.otf")
			.setFontAttrId(R.attr.fontPath)
			.build()
		);
		initViews();
		eventListners();
		setPager();
		
	}
	
	private void setPager() {
		viewPager.setAdapter(new MyPagerAdapter(this));
		viewPager.setCurrentItem(getCurentItem() - 1);
		indicator.setViewPager(viewPager);
	}
	
	private void initViews() {
		
		viewPager = (ViewPager) findViewById(R.id.viewPager);
		indicator = (CirclePageIndicator) findViewById(R.id.indicator);
		tvNext = (TextView) findViewById(R.id.tvNext);
		tvPrevious = (TextView) findViewById(R.id.tvPrevious);
		
		if (positionOfPager == 0) {
			tvPrevious.setVisibility(View.INVISIBLE);
		}
	}
	
	private void eventListners() {

		tvNext.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View view) {
				if (viewPager.getCurrentItem() == viewPager.getAdapter().getCount() - 1) {
                    Intent intent = new Intent(FirstTimeDetailActivity.this, MobileActivity.class);
                    startActivity(intent);
                    finish();

				} else {
					onRightSwipe();
				}
			}
		});
		tvPrevious.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View view) {
				onLeftSwipe();
			}
		});
		
		indicator.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {
			public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
			}
			
			public void onPageSelected(int position) {
				if (position == 0) {
					positionOfPager = position;
					tvPrevious.setVisibility(View.INVISIBLE);
					tvNext.setText("Next");
				} else if (position == 1) {
					positionOfPager = position;
					tvPrevious.setVisibility(View.VISIBLE);
					tvNext.setText("Next");
				} else if (position == 2) {
					positionOfPager = getCurentItem() - 1;
					tvPrevious.setVisibility(View.VISIBLE);
					tvNext.setText("Start");
				}
				indicator.setCurrentItem(position);
			}
			
			public void onPageScrollStateChanged(int state) {
			}
		});
	}
	
	private void onLeftSwipe() {
		int pos = getItem(-1);
		viewPager.setCurrentItem(pos, true);
	}
	
	private void onRightSwipe() {
		int pos = getItem(+1);
		viewPager.setCurrentItem(pos, true);
	}
	
	private int getItem(int pos) {
		
		return viewPager.getCurrentItem() + pos;
	}
	
	private int getCurentItem() {
		return viewPager.getCurrentItem();
	}
	
	@Override
	protected void attachBaseContext(Context newBase) {
		super.attachBaseContext(CalligraphyContextWrapper.wrap(newBase));
	}
	
	private class MyPagerAdapter extends PagerAdapter {
		private FirstTimeDetailActivity context;
		
		public MyPagerAdapter(FirstTimeDetailActivity context) {
			this.context = context;
		}
		
		@Override
		public int getCount() {
			return 3;
		}
		
		@Override
		public Object instantiateItem(ViewGroup container, int position) {
			
			inflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			View pager = null;
			if (position == 0) {
				pager = inflater.inflate(R.layout.first_time_page1, null);
			} else if (position == 1) {
				pager = inflater.inflate(R.layout.first_time_page2, null);
			} else if (position == 2) {
				pager = inflater.inflate(R.layout.first_time_page3, null);
			}
			container.addView(pager, 0);
			return pager;
		}
		
		@Override
		public void destroyItem(ViewGroup container, int position, Object object) {
			container.removeView((View) object);
			object = null;
		}
		
		@Override
		public boolean isViewFromObject(View arg0, Object arg1) {
			return arg0 == arg1;
		}
	}
}

