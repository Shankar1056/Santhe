package bigappcompany.com.santhe.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;

import java.util.List;
import java.util.Map;
import java.util.NavigableMap;
import java.util.TreeMap;

import bigappcompany.com.santhe.R;
import bigappcompany.com.santhe.model.MyUpdatesDataPojo;


public class UpdatesAdapter extends RecyclerView.Adapter<UpdatesAdapter.ViewHolder> {

    private static final NavigableMap<Long, String> suffixes = new TreeMap<>();

    static {
        suffixes.put(1_000L, "k");
        suffixes.put(1_000_000L, "M");
        suffixes.put(1_000_000_000L, "B");
        suffixes.put(1_000_000_000_000L, "T");
        suffixes.put(1_000_000_000_000_000L, "Q");
        suffixes.put(1_000_000_000_000_000_000L, "E");
    }

    private Context context;
    private List<MyUpdatesDataPojo> updates;

    public UpdatesAdapter(Context context, List<MyUpdatesDataPojo> updates) {
        this.context = context;
        this.updates = updates;
    }

    public static String format(long value) {

        if (value == Long.MIN_VALUE) return format(Long.MIN_VALUE + 1);
        if (value < 0) return "-" + format(-value);
        if (value < 1000) return Long.toString(value); //deal with easy case

        Map.Entry<Long, String> e = suffixes.floorEntry(value);
        Long divideBy = e.getKey();
        String suffix = e.getValue();

        long truncated = value / (divideBy / 10); //the number part of the output times 10
        boolean hasDecimal = truncated < 100 && (truncated / 10d) != (truncated / 10);
        return hasDecimal ? (truncated / 10d) + suffix : (truncated / 10) + suffix;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.updates_item_layout,
                parent, false);

        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {

        holder.tvType.setText(this.updates.get(position).getProduct_name());
        holder.tvCategoryType.setText(this.updates.get(position).getCategory());
        holder.tvAmount.setText("Rs." + this.updates.get(position).getPrice_per_kg() + "/- per" + " " + this.updates.get(position).getQuantity());


        try {
            holder.tvWeight.setText(format(Long.parseLong(this.updates.get(position).getTotal_quantity().replaceAll(",", ""))));
        } catch (Exception e) {

            e.printStackTrace();
        }
        Glide.with(context)
                .load(this.updates.get(position).getProduct_image())
                .into(holder.ivitemImg);

    }

    @Override
    public int getItemCount() {
        return updates.size();

    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        private ImageView ivitemImg;
        private TextView tvCategoryType, tvAmount, tvWeight, tvType;

        public ViewHolder(View itemView) {
            super(itemView);
            tvType = itemView.findViewById(R.id.tvType);
            ivitemImg = itemView.findViewById(R.id.ivitemImg);
            tvCategoryType = itemView.findViewById(R.id.tvCategoryType);
            tvAmount = itemView.findViewById(R.id.tvAmount);
            tvWeight = itemView.findViewById(R.id.tvWeight);

        }
    }
}
