package bigappcompany.com.santhe.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.TextPaint;
import android.text.method.LinkMovementMethod;
import android.text.style.ClickableSpan;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.List;

import bigappcompany.com.santhe.R;
import bigappcompany.com.santhe.interfaces.ClickOnReadmore;
import bigappcompany.com.santhe.model.LocationOfficeDetails;


public class AgricultureLocationAdapter extends RecyclerView.Adapter<AgricultureLocationAdapter.ViewHolder> {

    private Context context;
    private List<LocationOfficeDetails> locationOfficeDetails;
    private ClickOnReadmore clickOnReadmore;

    public AgricultureLocationAdapter(Context context, List<LocationOfficeDetails> locationOfficeDetails, ClickOnReadmore clickOnReadmore) {
        this.context = context;
        this.locationOfficeDetails = locationOfficeDetails;
        this.clickOnReadmore = clickOnReadmore;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.agriculture_item_layout,
                parent, false);

        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, final int position) {


        holder.tvCall.setText(this.locationOfficeDetails.get(position).getOffice_phone());
        holder.tvLoaction.setText(this.locationOfficeDetails.get(position).getOffice_address());
        holder.tvName.setText(this.locationOfficeDetails.get(position).getOffice_name());

        if (this.locationOfficeDetails.get(position).getOffice_address().length() >= 50) {

            String s = this.locationOfficeDetails.get(position).getOffice_address();
            s = s.substring(0, Math.min(s.length(), 50));

            SpannableString ssPhone = new SpannableString(s + " read more");
            ClickableSpan clickableSpan = new ClickableSpan() {
                @Override
                public void onClick(View textView) {
                    clickOnReadmore.clickeOnOread(position);
                }

                @Override
                public void updateDrawState(TextPaint ds) {
                    super.updateDrawState(ds);
                    ds.setUnderlineText(false);
                    ds.setColor(context.getResources().getColor(R.color.colorOrange));
                }
            };

            ssPhone.setSpan(clickableSpan, 50, 60, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
            holder.tvLoaction.setText(ssPhone);
            holder.tvLoaction.setMovementMethod(LinkMovementMethod.getInstance());
            holder.tvLoaction.setHighlightColor(context.getResources().getColor(R.color.colorOrange));
        }


    }

    @Override
    public int getItemCount() {
        return locationOfficeDetails.size();

    }

    public class ViewHolder extends RecyclerView.ViewHolder {


        private TextView tvCall, tvLoaction, tvName;


        public ViewHolder(View itemView) {
            super(itemView);
            tvCall = itemView.findViewById(R.id.tvCall);
            tvLoaction = itemView.findViewById(R.id.tvLoaction);
            tvName = itemView.findViewById(R.id.tvName);

        }
    }
}
