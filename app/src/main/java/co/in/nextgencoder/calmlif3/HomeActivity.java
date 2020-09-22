package co.in.nextgencoder.calmlif3;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
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
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;

import co.in.nextgencoder.calmlif3.R;
import co.in.nextgencoder.calmlif3.model.Moment;
import co.in.nextgencoder.calmlif3.model.User;


public class HomeActivity extends AppCompatActivity {

    TextView textView;
    FirebaseAuth firebaseAuth;
    DatabaseReference databaseReference;
    Moment moment = new Moment();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        BottomNavigationView navView = findViewById(R.id.nav_view);
        // Passing each menu ID as a set of Ids because each
        // menu should be considered as top level destinations.
        AppBarConfiguration appBarConfiguration = new AppBarConfiguration.Builder(
                R.id.navigation_feed, R.id.navigation_profile, R.id.navigation_notifications, R.id.navigation_search, R.id.navigation_add)
                .build();
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment);
        NavigationUI.setupActionBarWithNavController(this, navController, appBarConfiguration);
        NavigationUI.setupWithNavController(navView, navController);

        textView = findViewById( R.id.logout);
        firebaseAuth = FirebaseAuth.getInstance();
        databaseReference = FirebaseDatabase.getInstance().getReference();
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

        databaseReference.child("moments").child( googleSignInAccount.getId()).push().setValue( moment)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if( task.isSuccessful()) {
                            Toast.makeText(HomeActivity.this, "Moment Saved", Toast.LENGTH_SHORT).show();
                            momentDesc.setText("");
                            momentTitle.setText("");
                        } else {
                            Toast.makeText(HomeActivity.this, "Error occured", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }


    public void toggleSearch( View view) {
        ToggleButton toggleButton = (ToggleButton) view;
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