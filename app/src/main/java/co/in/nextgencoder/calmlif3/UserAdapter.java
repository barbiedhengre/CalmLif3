package co.in.nextgencoder.calmlif3;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

import co.in.nextgencoder.calmlif3.model.User;

public class UserAdapter extends RecyclerView.Adapter<UserAdapter.ViewHolder> {

    private List<User> user;

    // RecyclerView recyclerView;  
    public UserAdapter(List<User> user) {
        this.user = user;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
        View listItem= layoutInflater.inflate(R.layout.view_search_item, parent, false);
        ViewHolder viewHolder = new ViewHolder(listItem);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        final User User = user.get(position);
        holder.listName.setText(user.get(position).getName());
        holder.listVerified.setText(  (user.get(position).isVerified()) ? "Verified user" : "Not Verified User");
        holder.relativeLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(view.getContext(),"click on item: "+User.getName(),Toast.LENGTH_LONG).show();
            }
        });
    }


    @Override
    public int getItemCount() {
        return user.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        public TextView listName;
        public TextView listVerified;
        public RelativeLayout relativeLayout;
        public ViewHolder(View itemView) {
            super(itemView);
            this.listName = (TextView) itemView.findViewById(R.id.listName);
            this.listVerified = (TextView) itemView.findViewById(R.id.listVerified);
            relativeLayout = (RelativeLayout)itemView.findViewById(R.id.relativeLayout);
        }
    }
}
