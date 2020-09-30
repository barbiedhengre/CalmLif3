package co.in.nextgencoder.calmlif3.ui.profile;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.squareup.picasso.Picasso;

import java.util.Objects;

import co.in.nextgencoder.calmlif3.R;
import co.in.nextgencoder.calmlif3.ServiceIMPL.UserServiceImpl;
import co.in.nextgencoder.calmlif3.model.User;
import co.in.nextgencoder.calmlif3.utils.CallBack;


public class ProfileFragment extends Fragment {

    FirebaseAuth firebaseAuth;
    DatabaseReference databaseReference;
    FirebaseStorage storage = FirebaseStorage.getInstance();
    StorageReference storageReference;
    FirebaseUser firebaseUser;
    GoogleSignInAccount account;
    Switch privacySwitch;
    String id = null;

    private UserServiceImpl userService = new UserServiceImpl();


    private ProfileViewModel profileViewModel;
    TextView profileName, profileMail, profileVerified, profileBio;
    EditText bioEditText;
    ImageView profilePic, bioEdit, bioSubmit;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        profileViewModel = ViewModelProviders.of(this).get(ProfileViewModel.class);

        View root = inflater.inflate(R.layout.fragment_profile, container, false);
        profileName = root.findViewById(R.id.profileName);
        profileVerified = root.findViewById(R.id.profileVerified);
        profileMail = root.findViewById(R.id.profileMail);
        profilePic = root.findViewById(R.id.profilePic);
        profileBio = root.findViewById( R.id.profileBio);
        bioEditText = root.findViewById( R.id.bioedit);
        bioEdit = root.findViewById( R.id.editBioBtn);
        bioSubmit = root.findViewById( R.id.editBioSubmit);
        privacySwitch = root.findViewById( R.id.privacySwitch);

        bioEdit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                editBio();
            }
        });

        bioSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                submitBio();
            }
        });

        profileVerified.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                verifyUser();
            }
        });

        firebaseAuth = FirebaseAuth.getInstance();
        firebaseUser = firebaseAuth.getCurrentUser();
        firebaseUser.reload();
        databaseReference = FirebaseDatabase.getInstance().getReference().child("users");

        // Checking last google sign in
        account = GoogleSignIn.getLastSignedInAccount( container.getContext());
        // When user is not signed in by google signIn
        FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();

        id = ( account != null) ? account.getId() : firebaseUser.getUid();

        final DatabaseReference picReference = databaseReference.child( id).child("pic").getRef();

        picReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if( snapshot.getValue() != null)
                    Picasso.get()
                            .load( snapshot.getValue().toString())
                            .placeholder( R.drawable.icon_user_anonymous)
                            .into(profilePic);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        profileViewModel.getText().observe(getViewLifecycleOwner(), new Observer<String>() {
            @Override
            public void onChanged(@Nullable String s) {

            }
        });

        return root;
    }

    private void changePrivacy( boolean isPrivate) {
        userService.editUserPrivacy(new CallBack<Boolean>() {
            @Override
            public void callback(Boolean result) {

            }
        },  id, isPrivate);
    }

    private void verifyUser() {
        userService.sendUserVerification(new CallBack<Boolean>() {
            @Override
            public void callback(Boolean hasMailDelivered) {
                if( hasMailDelivered) {
                    Toast.makeText( getContext(), "Mail sent check your mail to verify your account", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText( getContext(), "Cant send mail check your internet connection", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private void editBio() {
        bioEdit.setVisibility( View.GONE);
        bioSubmit.setVisibility( View.VISIBLE);

        profileBio.setVisibility( View.GONE);
        bioEditText.setVisibility( View.VISIBLE);
        bioEditText.setHint("Enter new bio and let everyone know about the change");
    }

    private void submitBio() {
        String newBio = bioEditText.getText().toString();

        if( newBio.equals("") || newBio.isEmpty()) {
            Toast.makeText( getContext(), "Enter bio to edit", Toast.LENGTH_SHORT).show();
        } else {
            userService.editUserBioById(new CallBack<Boolean>() {
                @Override
                public void callback(Boolean editSuccessfull) {
                    if( editSuccessfull)
                        Toast.makeText( getContext(), "Bio updated", Toast.LENGTH_SHORT).show();
                    else
                        Toast.makeText( getContext(), "Bio updation failed", Toast.LENGTH_SHORT).show();
                }
            }, id, newBio);
        }
        bioEdit.setVisibility( View.VISIBLE);
        bioSubmit.setVisibility( View.GONE);

        profileBio.setVisibility( View.VISIBLE);
        bioEditText.setVisibility( View.GONE);
    }

    @Override
    public void onStart() {
        super.onStart();

        userService.getUserById(new CallBack<User>() {
            @Override
            public void callback(User user) {
                profileName.setText( user.getName());
                profileMail.setText( user.getMail());

                if( firebaseUser.isEmailVerified()) {
                    profileVerified.setText( "verified"  );
                } else {
                    profileVerified.setText( "click to verify");
                }

                String bio = user.getBio();

                if( bio == null || bio.isEmpty()) {
                    profileBio.setText( "Click on edit button to let every one know about you");
                } else {
                    profileBio.setText( bio);
                }
            }
        }, id);
    }
}