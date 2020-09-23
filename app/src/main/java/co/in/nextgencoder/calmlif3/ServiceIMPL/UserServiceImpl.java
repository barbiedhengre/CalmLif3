package co.in.nextgencoder.calmlif3.ServiceIMPL;

import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

import co.in.nextgencoder.calmlif3.HomeActivity;
import co.in.nextgencoder.calmlif3.R;
import co.in.nextgencoder.calmlif3.Service.UserService;
import co.in.nextgencoder.calmlif3.UserAdapter;
import co.in.nextgencoder.calmlif3.model.User;
import co.in.nextgencoder.calmlif3.utils.CallBack;

public class UserServiceImpl implements UserService {

    FirebaseAuth firebaseAuth;
    DatabaseReference databaseReference;

    public UserServiceImpl() {
        firebaseAuth = FirebaseAuth.getInstance();
        databaseReference = FirebaseDatabase.getInstance().getReference();
    }

    @Override
    public void addUser(@NonNull CallBack<Boolean> finishedCallback, User user) {

    }

    @Override
    public void allUser(@NonNull CallBack<List<User>> finishedCallback) {

    }

    @Override
    public void updateUser(@NonNull CallBack<Boolean> finishedCallback, User user) {

    }

    @Override
    public void deleteUser(@NonNull CallBack<Boolean> finishedCallback, User user) {

    }

    @Override
    public void searchUserByName(@NonNull final CallBack<List<User>> finishedCallback, final String name) {
        databaseReference.child("users").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                List<User> searchedData = new ArrayList<User>();

                for ( DataSnapshot dataSnapshot : snapshot.getChildren()) {
                    if (dataSnapshot.child("name").getValue().toString().toLowerCase().contains( name.toLowerCase())) {
                        User user = new User( dataSnapshot.getKey(),
                                (boolean) dataSnapshot.child("verified").getValue(),
                                dataSnapshot.child("name").getValue().toString());
                        searchedData.add(user);
                    }
                }
                finishedCallback.callback( searchedData);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }
}
