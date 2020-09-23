package co.in.nextgencoder.calmlif3.ServiceIMPL;

import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

import co.in.nextgencoder.calmlif3.Service.MomentService;
import co.in.nextgencoder.calmlif3.model.Moment;
import co.in.nextgencoder.calmlif3.utils.CallBack;

public class MomentServiceImpl implements MomentService {

    FirebaseAuth firebaseAuth;
    DatabaseReference databaseReference;

    public MomentServiceImpl() {
        firebaseAuth = FirebaseAuth.getInstance();
        databaseReference = FirebaseDatabase.getInstance().getReference();
    }

    @Override
    public void addMoment(@NonNull final CallBack<Boolean> finishedCallback, Moment moment) {
        String newKey = databaseReference.push().getKey();
        moment.setId( newKey);

        databaseReference.child("moments").child( moment.getId()).setValue( moment)
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

    @Override
    public void allMoment(@NonNull final CallBack<List<Moment>> finishedCallback) {
        databaseReference.child("moments").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                List<Moment> searchedData = new ArrayList<Moment>();
                for ( DataSnapshot dataSnapshot : snapshot.getChildren()) {
                    Moment moment = dataSnapshot.getValue( Moment.class);
                    searchedData.add(moment);
                }
                finishedCallback.callback( searchedData);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    @Override
    public void updateMoment(@NonNull CallBack<Boolean> finishedCallback,Moment moment) {

    }

    @Override
    public void deleteMoment(@NonNull CallBack<Boolean> finishedCallback,Moment moment) {

    }

    @Override
    public void searchMomentByTitle(@NonNull final CallBack<List<Moment>> finishedCallback, final String title) {
        databaseReference.child("moments").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                List<Moment> searchedData = new ArrayList<Moment>();

                for ( DataSnapshot dataSnapshot : snapshot.getChildren()) {
                    String dbTitle = dataSnapshot.child("title").getValue().toString();
                    if( dbTitle.toLowerCase().contains(title.toLowerCase())) {
                        Moment moment = new Moment( dataSnapshot.getKey(),
                                dataSnapshot.child("title").getValue().toString(),
                                dataSnapshot.child("mood").getValue().toString(),
                                dataSnapshot.child("momentDescription").getValue().toString());

                        searchedData.add(moment);
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
