package bigappcompany.com.santhe.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;

import java.util.ArrayList;

import bigappcompany.com.santhe.R;
import bigappcompany.com.santhe.model.DataCategory;


public class BuyTypeAdapter extends RecyclerView.Adapter<BuyTypeAdapter.ViewHolder> {

    private Context context;
    private ArrayList<DataCategory> updates;

    public BuyTypeAdapter(Context context, ArrayList<DataCategory> updates) {
        this.context = context;
        this.updates = updates;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.buytype_item_layout,
                parent, false);

        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        holder.tvType.setText(this.updates.get(position).getCat_name());

        if (position == 0) {
            Glide.with(context)
                    .load(R.drawable.allcat)
                    .apply(RequestOptions.circleCropTransform())
                    .into(holder.ivImage);
        } else {
            Glide.with(context)
                    .load(this.updates.get(position).getIcon())
                    .apply(RequestOptions.circleCropTransform())
                    .into(holder.ivImage);
        }
    }

    @Override
    public int getItemCount() {
        return updates.size();

    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        private ImageView ivImage;
        private TextView tvType;


        public ViewHolder(View itemView) {
            super(itemView);
            tvType = itemView.findViewById(R.id.tvType);
            ivImage = itemView.findViewById(R.id.ivImage);

        }
    }
}
