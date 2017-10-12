package bigappcompany.com.santhe.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.util.ArrayList;

import bigappcompany.com.santhe.model.MenuListPojo;
import bigappcompany.com.santhe.R;


public class MenuListAdapter extends BaseAdapter {
	private ArrayList<MenuListPojo> _mydata = new ArrayList<MenuListPojo>();
	private LayoutInflater _Inflater = null;
	private Context _context;
	
	public MenuListAdapter(Context c, ArrayList<MenuListPojo> _data) {
		this._mydata = _data;
		this._context = c;
		this._Inflater = (LayoutInflater) c.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
	}
	
	@Override
	public int getCount() {
		return _mydata.size();
	}
	
	@Override
	public Object getItem(int position) {
		return position;
	}
	
	@Override
	public long getItemId(int position) {
		return position;
	}
	
	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		final MyHolder holder;
		
		if (convertView == null) {
			holder = new MyHolder();
			convertView = this._Inflater.inflate(R.layout.menu_item_format, null);
			holder._name = convertView.findViewById(R.id.tv_items);
			holder._profileImg = convertView.findViewById(R.id.ProfileImage);
			holder._rvItemLayout = convertView.findViewById(R.id.rvItemLayout);
			convertView.setTag(holder);
		} else {
			holder = (MyHolder) convertView.getTag();
		}
		
		holder._name.setText(this._mydata.get(position).getItem());
		
		
		if (this._mydata.get(position).getIsSelected() == true) {
			holder._rvItemLayout.setBackgroundResource(R.color.colorGreenOp);
			holder._name.setTextColor(_context.getResources().getColor(R.color.colorGreen));
			holder._profileImg.setImageResource(this._mydata.get(position).getSelectedImg());
		} else {
			holder._name.setTextColor(_context.getResources().getColor(R.color.colorBlack));
			holder._rvItemLayout.setBackgroundColor(_context.getResources().getColor(R.color.colorWhite));
			holder._profileImg.setImageResource(this._mydata.get(position).getDeselectedImg());
		}
		
		return convertView;
		
	}
	
	class MyHolder {
		RelativeLayout _rvItemLayout, _rvDummyLine;
		TextView _name;
		ImageView _profileImg;
	}
	
}
