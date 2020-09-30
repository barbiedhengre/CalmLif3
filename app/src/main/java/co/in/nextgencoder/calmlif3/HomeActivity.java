package co.in.nextgencoder.calmlif3;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;

import com.github.dhaval2404.imagepicker.ImagePicker;
import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.navigation.NavController;
import androidx.navigation.NavDestination;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.StaggeredGridLayoutManager;

import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.Collections;
import java.util.List;

import co.in.nextgencoder.calmlif3.Service.MomentService;
import co.in.nextgencoder.calmlif3.Service.UserService;
import co.in.nextgencoder.calmlif3.ServiceIMPL.MomentServiceImpl;
import co.in.nextgencoder.calmlif3.ServiceIMPL.UserServiceImpl;
import co.in.nextgencoder.calmlif3.model.Moment;
import co.in.nextgencoder.calmlif3.model.User;
import co.in.nextgencoder.calmlif3.utils.CallBack;


public class HomeActivity extends AppCompatActivity {

    private MomentService momentService = new MomentServiceImpl();
    private UserService userService = new UserServiceImpl();

    ToggleButton toggleButton;
    BottomNavigationView navView;

    FirebaseAuth firebaseAuth;

    FirebaseStorage storage = FirebaseStorage.getInstance();
    DatabaseReference databaseReference;
    StorageReference storageReference;

    Moment moment = new Moment();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        NavController navController = getNavigationController();

        final GoogleSignInAccount googleSignInAccount = GoogleSignIn.getLastSignedInAccount( this);

        firebaseAuth = FirebaseAuth.getInstance();
        databaseReference = FirebaseDatabase.getInstance().getReference();
        storageReference = storage.getReference();

        boolean isGoogleSignIn = false;

        if( googleSignInAccount != null) {
            isGoogleSignIn = true;
        }

        final boolean finalIsGoogleSignIn = isGoogleSignIn;

        navController.addOnDestinationChangedListener(new NavController.OnDestinationChangedListener() {
            @Override
            public void onDestinationChanged(@NonNull NavController controller, @NonNull NavDestination destination, @Nullable Bundle arguments) {
                if(destination.getId() == R.id.navigation_feed) {
                    momentService.allVerifiedPublicMoments(new CallBack<List<Moment>>() {
                        @Override
                        public void callback(List<Moment> moments) {
                            try {
                                RecyclerView recyclerView = (RecyclerView) findViewById(R.id.feedRecyclerView);
                                Collections.reverse( moments);
                                FeedAdapter adapter = new FeedAdapter( moments);
                                recyclerView.setLayoutManager(new LinearLayoutManager(HomeActivity.this));
                                recyclerView.setAdapter(adapter);
                            } catch (Exception e) { }
                        }
                    });
                }

                if(destination.getId() == R.id.navigation_my_moments) {
                    String userMail = null;
                    if(finalIsGoogleSignIn) {
                        userMail = googleSignInAccount.getEmail();
                    } else {
                        userMail = firebaseAuth.getCurrentUser().getEmail();
                    }

                    momentService.momentByUser(new CallBack<List<Moment>>() {
                        @Override
                        public void callback(List<Moment> moments) {
                            RecyclerView recyclerView = (RecyclerView) findViewById(R.id.userMoments);
                            Collections.reverse( moments);

                            MyMomentAdapter adapter = null;
                            try {
                                adapter = new MyMomentAdapter( moments);
                                recyclerView.setAdapter(adapter);
                                recyclerView.setLayoutManager(new LinearLayoutManager(HomeActivity.this));
                            } catch (Exception e) {}

                        }
                    }, userMail);
                }
            }
        });
    }

    private NavController getNavigationController( ) {
        navView = findViewById(R.id.nav_view);

        // Passing each menu ID as a set of Ids because each
        // menu should be considered as top level destinations.
        AppBarConfiguration appBarConfiguration = new AppBarConfiguration.Builder(
                R.id.navigation_feed, R.id.navigation_profile, R.id.navigation_my_moments, R.id.navigation_search, R.id.navigation_add)
                .build();

        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment);
        NavigationUI.setupActionBarWithNavController(this, navController, appBarConfiguration);
        NavigationUI.setupWithNavController(navView, navController);

        return navController;
    }

    public void editProfileImage(View view) {
        Toast.makeText(this, "Select Image", Toast.LENGTH_SHORT).show();
        selectImage();
    }

    private void selectImage() {
        ImagePicker.Companion.with(this)
                //.cropSquare()	    			//Crop image(Optional), Check Customization for more option
                //.compress(1024)			//Final image size will be less than 1 MB(Optional)
                .maxResultSize(1080, 1080)	//Final image resolution will be less than 1080 x 1080(Optional)
                .start();
    }

    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult( requestCode, resultCode, data);
        if (resultCode == -1) {
            Uri fileUri = data != null ? data.getData() : null;

            GoogleSignInAccount googleSignInAccount = GoogleSignIn.getLastSignedInAccount( this);

            try {
                final StorageReference imageReference = storageReference.child( "images/"+googleSignInAccount.getId()+"/profilePic");
                InputStream iStream =   getContentResolver().openInputStream(fileUri);
                final DatabaseReference picReference = databaseReference.child("users").child( googleSignInAccount.getId()).child("pic");

                UploadTask uploadTask = imageReference.putStream(iStream);

                Task<Uri> uriTask = uploadTask.continueWithTask(new Continuation<UploadTask.TaskSnapshot, Task<Uri>>() {
                    @Override
                    public Task<Uri> then(@NonNull Task<UploadTask.TaskSnapshot> task) throws Exception {
                        return imageReference.getDownloadUrl();
                    }
                }).addOnCompleteListener(new OnCompleteListener<Uri>() {
                    @Override
                    public void onComplete(@NonNull Task<Uri> task) {
                        if( task.isSuccessful()) {
                            Uri downloadUri = task.getResult();
                            String url = downloadUri.toString();

                            picReference.setValue(url);
                        }
                    }
                });

            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }

            ImagePicker.Companion.getFile(data);
            ImagePicker.Companion.getFilePath(data);
        } else if (resultCode == 64) {
            Toast.makeText(this, ImagePicker.Companion.getError(data), Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(this, "Task Cancelled", Toast.LENGTH_SHORT).show();
        }
    }

    public void privacyAdded( View view) {
        Switch aSwitch = (Switch) view;
        boolean isPublic = !aSwitch.isChecked();
        moment.setPublic( isPublic);
    }

    public void saveMoment(View view) {
        final EditText momentDesc = findViewById( R.id.momentText);
        final EditText momentTitle = findViewById( R.id.momentTitle);

        String momentDescription = momentDesc.getText().toString();
        String momentHeading = momentTitle.getText().toString();

        Switch aSwitch = (Switch) view;
        boolean isPublic = !aSwitch.isChecked();
        moment.setPublic( isPublic);

        moment.setTitle( momentHeading);
        moment.setMomentDescription( momentDescription);

        GoogleSignInAccount googleSignInAccount = GoogleSignIn.getLastSignedInAccount( this);

        userService.getUserById(new CallBack<User>() {
            @Override
            public void callback(User user) {
                moment.setUser( user);
                momentService.addMoment(new CallBack<Boolean>() {
                    @Override
                    public void callback(Boolean isSuccessful) {
                        if( isSuccessful) {
                            Toast.makeText(HomeActivity.this, "Moment Saved", Toast.LENGTH_SHORT).show();
                            momentDesc.setText("");
                            momentTitle.setText("");
                        } else {
                            Toast.makeText(HomeActivity.this, "Error occured", Toast.LENGTH_SHORT).show();
                        }
                    }
                }, moment);
            }
        }, firebaseAuth.getUid());
    }

    public void search( View view) {
        final EditText searchText = findViewById( R.id.searchText);
        String filter = "user";

        /*
         *  Getting entity criteria for Toggle
         */
        try {
            if(toggleButton.getText().toString().equalsIgnoreCase( "Moment" )) {
                filter = "moment";
            } else {
                filter = "user";
            }
        } catch ( Exception e) { }


        /*
        *  Filters users based on name of the users
        */
        if( filter.equalsIgnoreCase("user")) {
            String searchingUser = searchText.getText().toString().trim();
            userService.searchUserByName(new CallBack<List<User>>() {
                @Override
                public void callback(List<User> searchedData) {
                    if ( searchedData != null && !searchedData.isEmpty()) {
                        RecyclerView recyclerView = (RecyclerView) findViewById(R.id.searchList);
                        UserAdapter adapter = new UserAdapter(searchedData);
                        recyclerView.setHasFixedSize(true);
                        recyclerView.setLayoutManager(new StaggeredGridLayoutManager( 2, RecyclerView.VERTICAL));
                        recyclerView.setAdapter(adapter);
                    } else {
                        Toast.makeText(HomeActivity.this, "No result found", Toast.LENGTH_SHORT).show();
                    }
                }
            }, searchingUser);
        }

        /*
         *  Filters moments based on title of the moments
         */
        if( filter.equalsIgnoreCase("moment")) {
            momentService.searchMomentByTitle(new CallBack<List<Moment>>() {
                @Override
                public void callback(List<Moment> searchedData) {
                    if ( searchedData != null && !searchedData.isEmpty()) {
                        RecyclerView recyclerView = (RecyclerView) findViewById(R.id.searchList);
                        MomentAdapter adapter = new MomentAdapter(searchedData);
                        try {
                            recyclerView.setHasFixedSize(true);
                        } catch (Exception e) {}
                        recyclerView.setLayoutManager(new StaggeredGridLayoutManager( 2, RecyclerView.VERTICAL));
                        recyclerView.setAdapter(adapter);

                    } else {
                        Toast.makeText(HomeActivity.this, "No result found", Toast.LENGTH_SHORT).show();
                    }
                }
            }, searchText.getText().toString().trim());
        }
    }

    public void toggleSearch( View view) {
        toggleButton = (ToggleButton) view;
        EditText searchText = findViewById( R.id.searchText);

        if(toggleButton.getText().toString().equalsIgnoreCase( "Moment" )) {
            searchText.setHint("Enter moment to search");
        } else {
            searchText.setHint("Enter user to search");
        }
    }


    public void writeMoment( String mood) {
        moment.setMood( mood);

        EditText momentView = findViewById( R.id.momentText);
        momentView.setHint("Write why you are "+mood);
        momentView.setVisibility( View.VISIBLE);

        Button submitBtn = findViewById( R.id.submitButton);
        submitBtn.setVisibility( View.VISIBLE);
    }

    public void happyClicked(View view) {
        writeMoment( "happy");
    }

    public void crazyClicked(View view) {
        writeMoment( "crazy");
    }

    public void romanticClicked(View view) {
        writeMoment( "romantic");
    }

    public void sadClicked(View view) {
        writeMoment( "sad");
    }

    public void angryClicked(View view) {
        writeMoment( "angry");
    }

    public void logout( View view) {
        firebaseAuth.signOut();
        Toast.makeText(this, "Sign out successful", Toast.LENGTH_SHORT).show();

        Intent intent = new Intent(getApplicationContext(), MainActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
    }
}