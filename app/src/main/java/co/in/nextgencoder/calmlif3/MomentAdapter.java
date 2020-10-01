package co.in.nextgencoder.calmlif3;

import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

import co.in.nextgencoder.calmlif3.model.Moment;
import co.in.nextgencoder.calmlif3.model.User;

public class MomentAdapter extends RecyclerView.Adapter<MomentAdapter.ViewHolder> {

    private List<Moment> moment;

    // RecyclerView recyclerView;
    public MomentAdapter(List<Moment> moment) {
        this.moment = moment;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
        View listItem= layoutInflater.inflate(R.layout.moment_search_item, parent, false);
        ViewHolder viewHolder = new ViewHolder(listItem);
        return viewHolder;
    }

    @Override public void onBindViewHolder(final ViewHolder holder, int position) {
        final Moment Moment = moment.get(position);
        holder.title.setText( moment.get(position).getTitle());
        holder.desc.setText( moment.get(position).getMomentDescription());
        // holder.userName.setText( moment.get(position).getUser().getName());

        if( moment.get(position).getMood().equals("happy")) {
            holder.moodIcon.setBackgroundResource( R.drawable.icon_happy);
        }

        if( moment.get(position).getMood().equals("crazy")) {
            holder.moodIcon.setBackgroundResource( R.drawable.icon_crazy1);
        }

        if( moment.get(position).getMood().equals("romantic")) {
            holder.moodIcon.setBackgroundResource( R.drawable.icon_kiss);
        }

        if( moment.get(position).getMood().equals("sad")) {
            holder.moodIcon.setBackgroundResource( R.drawable.icon_sad);
        }

        if( moment.get(position).getMood().equals("angry")) {
            holder.moodIcon.setBackgroundResource( R.drawable.icon_angry);
        }

        holder.relativeLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent( view.getContext(), ViewMomentActivity.class);
                intent.putExtra("momentId", Moment.getId());
                view.getContext().startActivity( intent);
            }
        });
    }


    @Override
    public int getItemCount() {
        return moment.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        public TextView title;
        public TextView desc;
        public TextView userName;
        public ImageView moodIcon;
        public RelativeLayout relativeLayout;
        public ViewHolder(View itemView) {
            super(itemView);
            this.title = (TextView) itemView.findViewById(R.id.searchedMomentTitle);
            this.desc = (TextView) itemView.findViewById(R.id.searchedMomentDescription);
            this.moodIcon = (ImageView) itemView.findViewById(R.id.searchedMoodIcon);
            relativeLayout = (RelativeLayout)itemView.findViewById(R.id.momentListLayout);
        }
    }
}
