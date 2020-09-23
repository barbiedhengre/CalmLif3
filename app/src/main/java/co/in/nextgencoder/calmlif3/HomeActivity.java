package co.in.nextgencoder.calmlif3;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;

import com.github.dhaval2404.imagepicker.ImagePicker;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.navigation.NavController;
import androidx.navigation.NavDestination;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
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
    TextView textView;
    FirebaseAuth firebaseAuth;
    DatabaseReference databaseReference;
    Moment moment = new Moment();
    BottomNavigationView navView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        navView = findViewById(R.id.nav_view);
        // Passing each menu ID as a set of Ids because each
        // menu should be considered as top level destinations.
        AppBarConfiguration appBarConfiguration = new AppBarConfiguration.Builder(
                R.id.navigation_feed, R.id.navigation_profile, R.id.navigation_notifications, R.id.navigation_search, R.id.navigation_add)
                .build();
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment);
        NavigationUI.setupActionBarWithNavController(this, navController, appBarConfiguration);
        NavigationUI.setupWithNavController(navView, navController);

        navController.addOnDestinationChangedListener(new NavController.OnDestinationChangedListener() {
            @Override
            public void onDestinationChanged(@NonNull NavController controller, @NonNull NavDestination destination, @Nullable Bundle arguments) {
                if(destination.getId() == R.id.navigation_feed) {
                    momentService.allMoment(new CallBack<List<Moment>>() {
                        @Override
                        public void callback(List<Moment> moments) {
                            RecyclerView recyclerView = (RecyclerView) findViewById(R.id.feedRecyclerView);
                            FeedAdapter adapter = new FeedAdapter( moments);
                            recyclerView.setLayoutManager(new LinearLayoutManager(HomeActivity.this));
                            recyclerView.setAdapter(adapter);
                        }
                    });
                }

            }
        });

//        navView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
//            @Override
//            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
//                if( item.getItemId() == R.id.navigation_feed) {
//                    momentService.allMoment(new CallBack<List<Moment>>() {
//                        @Override
//                        public void callback(List<Moment> moments) {
//                            RecyclerView recyclerView = (RecyclerView) findViewById(R.id.feedRecyclerView);
//                            MomentAdapter adapter = new MomentAdapter( moments);
//                            recyclerView.setLayoutManager(new LinearLayoutManager(HomeActivity.this));
//                            recyclerView.setAdapter(adapter);
//                        }
//                    });
//                }
//
//                return true;
//            }
//        });

        textView = findViewById( R.id.logout);
        firebaseAuth = FirebaseAuth.getInstance();
        databaseReference = FirebaseDatabase.getInstance().getReference();
    }

    @Override
    protected void onStart() {
        super.onStart();

    }

    public void saveMoment(View view) {
        final EditText momentDesc = findViewById( R.id.momentText);
        final EditText momentTitle = findViewById( R.id.momentTitle);

        String momentDescription = momentDesc.getText().toString();
        String momentHeading = momentTitle.getText().toString();

        moment.setTitle( momentHeading);
        moment.setMomentDescription( momentDescription);

        GoogleSignInAccount googleSignInAccount = GoogleSignIn.getLastSignedInAccount( this);
        User user = new User( googleSignInAccount.getDisplayName(), googleSignInAccount.getEmail());
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
                        recyclerView.setLayoutManager(new LinearLayoutManager(HomeActivity.this));
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
                        recyclerView.setHasFixedSize(true);
                        recyclerView.setLayoutManager(new LinearLayoutManager(HomeActivity.this));
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
}