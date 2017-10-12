package bigappcompany.com.santhe.adapter;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.support.annotation.Nullable;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;

import java.util.ArrayList;
import java.util.Map;
import java.util.NavigableMap;
import java.util.TreeMap;

import bigappcompany.com.santhe.R;
import bigappcompany.com.santhe.model.MyUpdatesDataPojo;


public class BuyContentAdapter extends RecyclerView.Adapter<BuyContentAdapter.ViewHolder> {

    private static final NavigableMap<Long, String> suffixes = new TreeMap<>();

    static {
        suffixes.put(1_000L, "k");
        suffixes.put(1_000_000L, "M");
        suffixes.put(1_000_000_000L, "B");
        suffixes.put(1_000_000_000_000L, "T");
        suffixes.put(1_000_000_000_000_000L, "Q");
        suffixes.put(1_000_000_000_000_000_000L, "E");
    }

    int count = 0;
    private Context context;
    private ArrayList<MyUpdatesDataPojo> buyContentPojo;

    public BuyContentAdapter(Context context, ArrayList<MyUpdatesDataPojo> buyContentPojo) {
        this.context = context;
        this.buyContentPojo = buyContentPojo;
    }

    public static String format(long value) {
        //Long.MIN_VALUE == -Long.MIN_VALUE so we need an adjustment here
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

        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.dash_list_item_test,
                parent, false);

        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {
        MyUpdatesDataPojo item = buyContentPojo.get(position);
//
        if (getItemCount() > 4) {
            if (position < 4) {

                holder.llBottomItem.setVisibility(View.VISIBLE);
                holder.cvBigItem.setVisibility(View.GONE);
            } else {

                holder.llBottomItem.setVisibility(View.GONE);
                holder.cvBigItem.setVisibility(View.VISIBLE);

            }
        } else {

            if (getItemCount() > 2) {

                if (position >= 2) {
                    holder.cvBigItem.setVisibility(View.VISIBLE);
                    holder.llBottomItem.setVisibility(View.GONE);
                } else if (position < 2) {
                    holder.cvBigItem.setVisibility(View.GONE);
                    holder.llBottomItem.setVisibility(View.VISIBLE);
                }
            } else {

                holder.cvBigItem.setVisibility(View.VISIBLE);
                holder.llBottomItem.setVisibility(View.GONE);
            }

        }


        holder.tvType.setText(this.buyContentPojo.get(position).getProduct_name());
        holder.tvCategoryType.setText(this.buyContentPojo.get(position).getCategory());

        holder.tvAmount.setText("Rs" + this.buyContentPojo.get(position).getPrice_per_kg() + " /-per" + " " + this.buyContentPojo.get(position).getQuantity());

        try {
            holder.tvWeight.setText(format(Long.parseLong(this.buyContentPojo.get(position).getTotal_quantity().replaceAll(",", ""))));
        } catch (Exception e) {
            e.printStackTrace();
        }

//        Glide.with(context)
//                .load(this.buyContentPojo.get(position).getProduct_image())
//                .into(holder.ivImageType);

        Glide.with(context)
                .load(this.buyContentPojo.get(position).getProduct_image())
                .listener(new RequestListener<Drawable>() {
                    @Override
                    public boolean onLoadFailed(@Nullable GlideException e, Object model, Target<Drawable> target, boolean isFirstResource) {
                        holder.progress.setVisibility(View.GONE);
                        return false;
                    }

                    @Override
                    public boolean onResourceReady(Drawable resource, Object model, Target<Drawable> target, DataSource dataSource, boolean isFirstResource) {
                        holder.progress.setVisibility(View.GONE);
                        return false;
                    }
                })
                .into(holder.ivImageType);


        holder.tvStype.setText(this.buyContentPojo.get(position).getProduct_name());

        holder.tvSCategory.setText(this.buyContentPojo.get(position).getCategory());
        try {
            holder.tvsWeight.setText(format(Long.parseLong(this.buyContentPojo.get(position).getTotal_quantity().replaceAll(",", ""))));
        } catch (Exception e) {
            e.printStackTrace();
        }

        holder.tvSAmount.setText("Rs" + this.buyContentPojo.get(position).getPrice_per_kg() + "/" + this.buyContentPojo.get(position).getQuantity());
//        Glide.with(context)
//                .load(this.buyContentPojo.get(position).getProduct_image())
//                .into(holder.ivSproductImage);

        Glide.with(context)
                .load(this.buyContentPojo.get(position).getProduct_image())
                .listener(new RequestListener<Drawable>() {
                    @Override
                    public boolean onLoadFailed(@Nullable GlideException e, Object model, Target<Drawable> target, boolean isFirstResource) {
                        holder.progress1.setVisibility(View.GONE);
                        return false;
                    }

                    @Override
                    public boolean onResourceReady(Drawable resource, Object model, Target<Drawable> target, DataSource dataSource, boolean isFirstResource) {
                        holder.progress1.setVisibility(View.GONE);
                        return false;
                    }
                })
                .into(holder.ivSproductImage);


    }

    @Override
    public int getItemCount() {
        return buyContentPojo.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        public ProgressBar progress, progress1;
        private ImageView ivImageType, ivSproductImage;
        private TextView tvCategoryType;
        private TextView tvAmount, tvWeight, tvType;
        private TextView tvStype, tvSCategory, tvsWeight, tvSAmount;
        private LinearLayout llBottomItem, llTopItem;
        private CardView cvBigItem;


        public ViewHolder(View itemView) {
            super(itemView);
            llBottomItem = itemView.findViewById(R.id.llBottomItem);
            llTopItem = itemView.findViewById(R.id.llTopItem);
            tvCategoryType = itemView.findViewById(R.id.tvCategoryType);
            tvAmount = itemView.findViewById(R.id.tvAmount);
            tvWeight = itemView.findViewById(R.id.tvWeight);
            tvType = itemView.findViewById(R.id.tvType);
            ivImageType = itemView.findViewById(R.id.ivImageType);
            cvBigItem = itemView.findViewById(R.id.cvBigItem);
            tvStype = itemView.findViewById(R.id.tvStype);
            tvSCategory = itemView.findViewById(R.id.tvSCategory);
            tvsWeight = itemView.findViewById(R.id.tvsWeight);
            tvSAmount = itemView.findViewById(R.id.tvSAmount);
            ivSproductImage = itemView.findViewById(R.id.ivSproductImage);
            progress = itemView.findViewById(R.id.progress);
            progress1 = itemView.findViewById(R.id.progress1);


        }
    }
}
