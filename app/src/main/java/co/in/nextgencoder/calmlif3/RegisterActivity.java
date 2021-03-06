package co.in.nextgencoder.calmlif3;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.HashMap;

import co.in.nextgencoder.calmlif3.Service.UserService;
import co.in.nextgencoder.calmlif3.ServiceIMPL.UserServiceImpl;
import co.in.nextgencoder.calmlif3.model.User;
import co.in.nextgencoder.calmlif3.utils.CallBack;

public class RegisterActivity extends AppCompatActivity {

    private static final int RC_SIGN_IN = 4219;

    EditText nameInput, mailInput, passwordInput;

    UserService userService = new UserServiceImpl();

    private FirebaseAuth firebaseAuth;
    private DatabaseReference databaseReference;
    private GoogleSignInClient googleSignInClient;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        initializeInputFields();
        configureAuthentication();
        configureDatabase();
    }

    private void initializeInputFields() {
        nameInput = findViewById( R.id.inputName);
        mailInput = findViewById( R.id.inputMail);
        passwordInput = findViewById( R.id.inputPassword);
    }

    private void configureAuthentication() {
        firebaseAuth = FirebaseAuth.getInstance();

        // Configure sign-in to request the user's ID, email address, and basic
        // profile. ID and basic profile are included in DEFAULT_SIGN_IN.
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build();

        // Build a GoogleSignInClient with the options specified by gso.
        googleSignInClient = GoogleSignIn.getClient(this, gso);
    }

    private void configureDatabase() {
        databaseReference = FirebaseDatabase.getInstance().getReference().child("users");
    }

    public void registerUser(View view) {
        String result = validateInputs();
        final String userName = nameInput.getText().toString().trim();
        final String userMail = mailInput.getText().toString().trim();
        final String userPass = passwordInput.getText().toString().trim();

        if( result.equals("Form is validated")) {
            firebaseAuth.createUserWithEmailAndPassword( userMail, userPass)
                    .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete( Task<AuthResult> task) {

                            if(task.isSuccessful()) {
                                User user = new User( userName, userMail);
                                user.setId( task.getResult().getUser().getUid());

                                userService.addUser(new CallBack<Boolean>() {
                                    @Override
                                    public void callback(Boolean isUserRegistered) {
                                        if( isUserRegistered) {
                                            Toast.makeText(RegisterActivity.this, "Registered Successfully", Toast.LENGTH_SHORT).show();

                                            Intent intent = new Intent( getApplicationContext(), MainActivity.class);
                                            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                                            startActivity(intent);
                                        } else {
                                            Toast.makeText(RegisterActivity.this, "Registration Failed", Toast.LENGTH_SHORT).show();
                                        }
                                    }
                                }, user);
                            } else {
                                Toast.makeText(RegisterActivity.this, "Registration Failed", Toast.LENGTH_SHORT).show();
                            }
                        }
                    })
                    .addOnFailureListener(this, new OnFailureListener() {
                        @Override
                        public void onFailure( Exception e) {
                            Toast.makeText(RegisterActivity.this, "Connection Failure", Toast.LENGTH_SHORT).show();
                        }
                    });
        } else {
            Toast.makeText(this, result, Toast.LENGTH_SHORT).show();
        }
    }

    private String validateInputs() {
        // Taking inputs
        final String userName = nameInput.getText().toString().trim();
        final String userMail = mailInput.getText().toString().trim();
        final String userPass = passwordInput.getText().toString().trim();

        // Regular expressions to validate name
        final String nameValidationRegex = "^[a-zA-Z]*$";

        // Regular expressions to validate E-mail
        final String emailValidationRegex = "^[A-Z0-9._%+-]+@[A-Z0-9.-]+\\\\.[A-Z]{2,6}$";


        if( userName.isEmpty() || userMail.isEmpty() || userPass.isEmpty()) {
            return "All fields are manadatory";
        }

        if ( userName.matches(nameValidationRegex)) {
            return "Name must only contain alphabets";
        }

        if (userMail.matches(emailValidationRegex)) {
            return "Invalid email address";
        }

        if (userPass.length() < 8) {
            return "Password length should be greater than or equal to 8";
        }

        return "Form is validated";
    }

    // Starts Login Activity
    public void goToLogin(View view) {
        Intent intent = new Intent( this, LoginActivity.class);
        startActivity(intent);
    }

    // Sending Google Sign In Intent
    public void googleSignIn(View view) {
        Intent signInIntent = googleSignInClient.getSignInIntent();
        startActivityForResult(signInIntent, RC_SIGN_IN);
    }


    // Google Sign In Results
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == RC_SIGN_IN) {
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);

            try {
                GoogleSignInAccount account = task.getResult(ApiException.class);
                AuthCredential credential = GoogleAuthProvider.getCredential(account.getIdToken(), null);

                final String userName = account.getDisplayName();
                final String userMail = account.getEmail();

                firebaseAuth.signInWithCredential(credential)
                        .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                            @Override
                            public void onComplete(@NonNull Task<AuthResult> task) {
                                if (task.isSuccessful()) {
                                    User user = new User( userName, userMail);
                                    user.setId( task.getResult().getUser().getUid());
                                    userService.addUser(new CallBack<Boolean>() {
                                        @Override
                                        public void callback(Boolean isUserRegistered) {
                                            Toast.makeText(RegisterActivity.this, "SignIn Successful", Toast.LENGTH_SHORT).show();

                                            Intent intent = new Intent( getApplicationContext(), MainActivity.class);
                                            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                                            startActivity(intent);
                                        }
                                    }, user);
                                } else {
                                    Toast.makeText(RegisterActivity.this,"Sign In Failed", Toast.LENGTH_SHORT).show();
                                }
                            }
                        });
            } catch (ApiException e) {
                Toast.makeText(this, "Sign In Failed", Toast.LENGTH_SHORT).show();
            }
        }
    }
}