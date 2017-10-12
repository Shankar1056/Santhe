package bigappcompany.com.santhe.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import bigappcompany.com.santhe.R;
import bigappcompany.com.santhe.model.RepliesItem;

import static bigappcompany.com.santhe.adapter.CommentsAdapter.GetTodaysDate;


public class ReplyAdapter extends RecyclerView.Adapter<ReplyAdapter.ViewHolder> {

    private Context context;
    private List<RepliesItem> discussionPojo;

    public ReplyAdapter(Context context, List<RepliesItem> discussionPojo) {
        this.context = context;
        this.discussionPojo = discussionPojo;
    }

    private static String GetDateTimeDifference(Date startDate, Date endDate) {

        //milliseconds
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

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.reply_item_fragment,
                parent, false);

        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {


        holder.tvComment.setText(this.discussionPojo.get(position).getReply());
        holder.tvName.setText(this.discussionPojo.get(position).getRepliedBy());
        Glide.with(context)
                .load(this.discussionPojo.get(position).getRProfilePic())
                .apply(RequestOptions.circleCropTransform())
                .into(holder.ivProfilePicture);

        Date endDate = new Date();
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");

        try {
            endDate = formatter.parse(this.discussionPojo.get(position).getRepliedOn());
        } catch (java.text.ParseException e) {
            e.printStackTrace();
        }
        try {
            holder.tvTime.setText(GetDateTimeDifference(endDate, GetTodaysDate()));
        } catch (Exception e) {
            e.printStackTrace();
        }


    }

    @Override
    public int getItemCount() {
        return discussionPojo.size();

    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        private ImageView ivProfilePicture;
        private TextView tvTime;
        private TextView tvComment, tvName;


        private ViewHolder(View itemView) {
            super(itemView);
            ivProfilePicture = itemView.findViewById(R.id.ivProfilePicture);
            tvTime = itemView.findViewById(R.id.tvTime);
            tvComment = itemView.findViewById(R.id.tvComment);
            tvName = itemView.findViewById(R.id.tvName);


        }
    }
}
