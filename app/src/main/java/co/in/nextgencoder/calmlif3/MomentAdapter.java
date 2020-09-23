package co.in.nextgencoder.calmlif3;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
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

    @Override public void onBindViewHolder(ViewHolder holder, int position) {
        final Moment Moment = moment.get(position);
        holder.title.setText(moment.get(position).getTitle());
        holder.desc.setText(  moment.get(position).getMomentDescription());
        holder.relativeLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(view.getContext(),"click on item: "+Moment.getTitle(),Toast.LENGTH_LONG).show();
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
        public RelativeLayout relativeLayout;
        public ViewHolder(View itemView) {
            super(itemView);
            this.title = (TextView) itemView.findViewById(R.id.momentTitleList);
            this.desc = (TextView) itemView.findViewById(R.id.momentDescriptionList);
            relativeLayout = (RelativeLayout)itemView.findViewById(R.id.momentListLayout);
        }
    }
}
