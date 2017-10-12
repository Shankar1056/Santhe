package bigappcompany.com.santhe.adapter;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.support.annotation.Nullable;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.RequestOptions;
import com.bumptech.glide.request.target.Target;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import bigappcompany.com.santhe.R;
import bigappcompany.com.santhe.model.DiscussionDataIPojo;
import bigappcompany.com.santhe.utility.SharedPreferencesUtility;


public class DiscussionAdapter extends RecyclerView.Adapter<DiscussionAdapter.ViewHolder> {
    private final ArrayList<DiscussionDataIPojo> discussionPojo = new ArrayList<>();

    public static String GetDateTimeDifference(Date startDate, Date endDate) {

        long different = endDate.getTime() - startDate.getTime();
        Log.d("TimeAgo", "startDate : " + startDate);
        Log.d("TimeAgo", "endDate : " + endDate);
        Log.d("TimeAgo", "different : " + different);

        long secondsInMilli = 1000;
        long minutesInMilli = secondsInMilli * 60;
        long hoursInMilli = minutesInMilli * 60;
        long daysInMilli = hoursInMilli * 24;
        long monthInMilli = daysInMilli * 30;

        long elapsedMonths = different / monthInMilli;
        different = different % monthInMilli;

        long elapsedDays = different / daysInMilli;
        different = different % daysInMilli;

        long elapsedHours = different / hoursInMilli;
        different = different % hoursInMilli;

        long elapsedMinutes = different / minutesInMilli;
        different = different % minutesInMilli;

        long elapsedSeconds = different / secondsInMilli;
        long totalSecond = (elapsedSeconds + (60 * elapsedMinutes) + (3600 * elapsedHours) + (86400 * elapsedDays) + (2592000 * elapsedMonths));

        Log.i("elapsedMonths", "" + elapsedMonths);
        Log.i("elapsedDays", "" + elapsedDays);
        Log.i("elapsedHours", "" + elapsedHours);
        Log.i("elapsedMinutes", "" + elapsedMinutes);
        Log.i("elapsedSeconds", "" + elapsedSeconds);
        if (totalSecond < 60) {
            return "now";
        }
        if ((totalSecond >= 60) && (!(totalSecond >= 120))) {
            return elapsedMinutes + " min ago";
        }
        if ((totalSecond >= 120) && (!(totalSecond >= 3600))) {
            return elapsedMinutes + " mins ago";
        }
        if ((totalSecond >= 3600) && (!(totalSecond >= 7200))) {
            return elapsedHours + " hour ago";
        }
        if ((totalSecond >= 7200) && (!(totalSecond >= 86400))) {
            return elapsedHours + " hours ago";
        }
        if ((totalSecond >= 86400) && (totalSecond < 172800)) {
            return elapsedDays + " day ago";
        }
        if ((totalSecond >= 172800) && (!(totalSecond >= 2592000))) {
            return elapsedDays + " days ago";
        }
        if ((totalSecond >= 2592000) && (!(totalSecond >= 5184000))) {
            return elapsedMonths + " month ago";
        }
        if ((totalSecond >= 5184000) && (!(totalSecond < 31104000))) {
            return elapsedMonths + " months ago";
        } else {
            return "" + startDate;
        }


    }

    public static Date GetTodaysDate() {

        Calendar c = Calendar.getInstance();
        SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        String formattedDate = df.format(c.getTime());
        Date startDate = new Date();
        try {
            startDate = df.parse(formattedDate);
        } catch (java.text.ParseException e) {
            e.printStackTrace();
        }
        return startDate;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.discussion_item_fragment,
                parent, false);
        return new ViewHolder(view);
    }

    public void addItem(DiscussionDataIPojo item) {
        discussionPojo.add(item);
        notifyItemInserted(discussionPojo.size() - 1);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {
        Context context = holder.itemView.getContext();
        Glide.with(context)
                .load(this.discussionPojo.get(position).getUserProfilePic())
                .apply(RequestOptions.circleCropTransform())
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
                .into(holder.ivProfilePicture);


        if (this.discussionPojo.get(position).getPostImage().length() > 0) {
            holder.rvProduct.setVisibility(View.VISIBLE);
            Glide.with(context)
                    .load(this.discussionPojo.get(position).getPostImage())
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
                    .into(holder.ivProduct);
        } else {

            holder.rvProduct.setVisibility(View.GONE);

        }

        if (this.discussionPojo.get(position).getUser_id().contains(SharedPreferencesUtility.getAuthkey(context))) {
            holder.ivOption.setVisibility(View.VISIBLE);
        } else {
            holder.ivOption.setVisibility(View.GONE);
        }


        holder.tvProfileName.setText(this.discussionPojo.get(position).getPostedBy());
        holder.tvProductName.setText(this.discussionPojo.get(position).getCategoryName());
        holder.tvDescription.setText(this.discussionPojo.get(position).getDescription().trim());
        holder.tvCommentsCount.setText(this.discussionPojo.get(position).getCommentsCount() + " comments");
        Date endDate = new Date();
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");

        try {
            endDate = formatter.parse(this.discussionPojo.get(position).getPostedDate());
        } catch (java.text.ParseException e) {
            e.printStackTrace();
        }


        try {
            holder.tvPostedTime.setText(GetDateTimeDifference(endDate, GetTodaysDate()));
        } catch (Exception e) {
            e.printStackTrace();
        }


    }

    @Override
    public int getItemCount() {
        return discussionPojo.size();

    }

    public void addItems(List<DiscussionDataIPojo> data) {
        discussionPojo.addAll(data);
        notifyDataSetChanged();
    }

    public void clearAllItems() {
        discussionPojo.clear();
        notifyDataSetChanged();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        private ImageView ivProfilePicture, ivProduct, ivOption;
        private TextView tvProfileName, tvProductName;
        private TextView tvPostedTime, tvDescription;
        private TextView tvCommentsCount;
        private RelativeLayout rvProduct;
        private ProgressBar progress;

        public ViewHolder(View itemView) {
            super(itemView);

            ivProfilePicture = itemView.findViewById(R.id.ivProfilePicture);
            ivProduct = itemView.findViewById(R.id.ivProduct);
            tvProductName = itemView.findViewById(R.id.tvProductName);
            tvCommentsCount = itemView.findViewById(R.id.tvCommentsCount);
            tvProfileName = itemView.findViewById(R.id.tvProfileName);
            tvPostedTime = itemView.findViewById(R.id.tvPostedTime);
            tvDescription = itemView.findViewById(R.id.tvDescription);
            ivOption = itemView.findViewById(R.id.ivOption);
            rvProduct = itemView.findViewById(R.id.rvProduct);
            progress = itemView.findViewById(R.id.progress);


        }
    }
}
