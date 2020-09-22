package co.in.nextgencoder.calmlif3.ui.profile;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import co.in.nextgencoder.calmlif3.HomeActivity;
import co.in.nextgencoder.calmlif3.LoginActivity;
import co.in.nextgencoder.calmlif3.R;
import co.in.nextgencoder.calmlif3.model.User;

public class ProfileFragment extends Fragment {

    FirebaseAuth firebaseAuth;
    DatabaseReference databaseReference;

    private ProfileViewModel profileViewModel;
    TextView profileName, profileMail, profileVerified;

    GoogleSignInAccount account;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        profileViewModel = ViewModelProviders.of(this).get(ProfileViewModel.class);

        View root = inflater.inflate(R.layout.fragment_profile, container, false);
        profileName = root.findViewById(R.id.profileName);
        profileVerified = root.findViewById(R.id.profileVerified);
        profileMail = root.findViewById(R.id.profileMail);

        firebaseAuth = FirebaseAuth.getInstance();
        databaseReference = FirebaseDatabase.getInstance().getReference().child("users");

        // Checking last google sign in
        account = GoogleSignIn.getLastSignedInAccount( container.getContext());



        profileViewModel.getText().observe(getViewLifecycleOwner(), new Observer<String>() {
            @Override
            public void onChanged(@Nullable String s) {

            }
        });

        return root;
    }

    @Override
    public void onStart() {
        super.onStart();

        // Check for existing Google Sign In account, if the user is already signed in
        if( account != null) {
            // When user is not signed in by google signIn

            ValueEventListener postListener = new ValueEventListener() {
                String output = "";
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    profileName.setText( snapshot.child( account.getId()).child("name").getValue().toString());
                    profileMail.setText( snapshot.child( account.getId()).child("mail").getValue().toString());
                    if( ( (boolean) snapshot.child( account.getId()).child("verified").getValue())) {
                        profileVerified.setText( "verified");
                    } else {
                        profileVerified.setText( "not verified");
                    }

                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            };

            databaseReference.addValueEventListener( postListener);
        }

    }
}