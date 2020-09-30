package co.in.nextgencoder.calmlif3.ServiceIMPL;

import android.widget.Toast;

import androidx.annotation.NonNull;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

import co.in.nextgencoder.calmlif3.Service.UserService;
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
    public void addUser(@NonNull final CallBack<Boolean> finishedCallback, User user) {
        databaseReference.child("users").child(user.getId()).setValue(user)
            .addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {
                    if( task.isSuccessful()) {
                        finishedCallback.callback( true);
                    } else {
                        finishedCallback.callback( false);
                    }
                }
            })
            .addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    finishedCallback.callback( false);
                }
            });
    }

    @Override
    public void allUser(@NonNull final CallBack<List<User>> finishedCallback) {
        databaseReference.child("users").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                List<User> searchedData = new ArrayList<User>();
                for ( DataSnapshot dataSnapshot : snapshot.getChildren()) {
                    User user = dataSnapshot.getValue( User.class);
                    searchedData.add(user);
                }
                finishedCallback.callback( searchedData);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    @Override
    public void updateUser(@NonNull CallBack<Boolean> finishedCallback, User user) {
        // Feature Yet to be added
    }

    @Override
    public void editUserPrivacy(@NonNull final CallBack<Boolean> finishedCallback, String id, final boolean privacy) {
        final DatabaseReference userReference = databaseReference.child("users").child(id);
        userReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                userReference.child("public").setValue( !privacy);
                finishedCallback.callback( true);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                finishedCallback.callback( true);
            }
        });
    }

    @Override
    public void editUserBioById(@NonNull final CallBack<Boolean> finishedCallback, String id, final String bio) {
        final DatabaseReference userReference = databaseReference.child("users").child(id);
        userReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                userReference.child("bio").setValue(bio);
                finishedCallback.callback( true);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                finishedCallback.callback( true);
            }
        });
    }

    @Override
    public void deleteUser(@NonNull CallBack<Boolean> finishedCallback, User user) {
        // Feature Yet to be added
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

    @Override
    public void getUserById(@NonNull final CallBack<User> finishedCallback, String id) {
        databaseReference.child("users").child(id).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                User user = snapshot.getValue( User.class);
                finishedCallback.callback(user);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

    }

    @Override
    public void sendUserVerification(@NonNull final CallBack<Boolean> finishedCallback) {
        firebaseAuth.getCurrentUser().sendEmailVerification()
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if( task.isSuccessful()) {
                            finishedCallback.callback( true);
                        } else {
                            finishedCallback.callback( false);
                        }
                    }
                });
    }

}
