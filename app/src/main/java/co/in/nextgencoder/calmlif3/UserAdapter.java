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

import com.squareup.picasso.Picasso;

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

        if( user.get(position).getPic() == null || user.get(position).getPic().isEmpty()) {
            holder.userPic
                    .setBackgroundResource( R.drawable.icon_user_anonymous);
        } else {
            Picasso.get()
                    .load( User.getPic())
                    .placeholder( R.drawable.icon_user_anonymous)
                    .into( holder.userPic);
        }

        holder.bio.setText(
                ( user.get(position).getBio() == null || user.get(position).getBio().isEmpty()) ?
                "Hello I am new on Calm Lif3" : user.get(position).getBio());


//        holder.gender.setText(
//                ( user.get(position).getGender() == null || user.get(position).getGender().isEmpty()) ?
//                        "Not disclosed" : user.get(position).getGender());

//        holder.listVerified.setText(
//                (user.get(position).isVerified()) ? "Verified user" : "Not Verified User");

        holder.relativeLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent( view.getContext(), ViewUserActivity.class);
                intent.putExtra("userId", User.getId());
                view.getContext().startActivity( intent);
            }
        });
    }


    @Override
    public int getItemCount() {
        return user.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        public TextView listName;
        public ImageView userPic;
        // public TextView gender;
        public TextView bio;
        //public TextView listVerified;
        public RelativeLayout relativeLayout;
        public ViewHolder(View itemView) {
            super(itemView);
            this.listName = (TextView) itemView.findViewById(R.id.listName);
            this.bio = (TextView) itemView.findViewById(R.id.userBio);
            this.userPic = (ImageView) itemView.findViewById(R.id.userSearchPic);
            relativeLayout = (RelativeLayout)itemView.findViewById(R.id.relativeLayout);
        }
    }
}
