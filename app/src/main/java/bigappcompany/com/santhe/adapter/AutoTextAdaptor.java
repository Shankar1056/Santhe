package bigappcompany.com.santhe.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;

import bigappcompany.com.santhe.R;
import bigappcompany.com.santhe.model.DataCategory;

public class AutoTextAdaptor extends BaseAdapter implements Filterable {
    private Context _context;
    private LayoutInflater _Inflater = null;
    private ArrayList<DataCategory> _mydata;
    private ArrayList<DataCategory> _textvalue;

    private ValueFilter valueFilter;

    public AutoTextAdaptor(Context c, ArrayList<DataCategory> _aList) {
        this._mydata = _aList;
        _textvalue = _aList;
        this._context = c;

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

        this._Inflater = (LayoutInflater) _context
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);


        final MyHolder holder;

        if (_Inflater != null) {

            if (convertView == null) {
                holder = new MyHolder();
                convertView = this._Inflater.inflate(R.layout.autocomplete_layout,
                        null);

                holder._autocomplete = convertView
                        .findViewById(R.id.tvAutoText);


                convertView.setTag(holder);

            } else {
                holder = (MyHolder) convertView.getTag();
            }


            holder._autocomplete.setText(this._mydata.get(position).getCat_name());
        }
        return convertView;
    }

    @Override
    public Filter getFilter() {
        if (valueFilter == null) {
            valueFilter = new ValueFilter();
        }

        return valueFilter;
    }

    class MyHolder {

        TextView _autocomplete;
        ImageView _CustomerImg;

    }

    private class ValueFilter extends Filter {
        // Invoked in a worker thread to filter the data according to the
        // constraint.
        @Override
        protected FilterResults performFiltering(CharSequence constraint) {
            FilterResults results = new FilterResults();
            if (constraint != null && constraint.length() > 0) {
                ArrayList<DataCategory> filterList = new ArrayList<DataCategory>();
                _mydata = _textvalue;
                for (int i = 0; i < _mydata.size(); i++) {
                    if ((_mydata.get(i).getCat_name().toUpperCase())
                            .contains(constraint.toString().toUpperCase())) {
                        DataCategory AutoTextPojo = new DataCategory();
                        AutoTextPojo = (_mydata.get(i));
                        filterList.add(AutoTextPojo);
                    }
                }
                results.count = filterList.size();
                results.values = filterList;
            } else {
                results.count = _textvalue.size();
                results.values = _textvalue;
            }
            return results;
        }

        // Invoked in the UI thread to publish the filtering results in the user
        // interface.
        @SuppressWarnings("unchecked")
        @Override
        protected void publishResults(CharSequence constraint,
                                      FilterResults results) {
            _mydata = (ArrayList<DataCategory>) results.values;

            notifyDataSetChanged();
        }
    }
}
