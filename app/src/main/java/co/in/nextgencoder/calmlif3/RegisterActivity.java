package co.in.nextgencoder.calmlif3;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
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

public class RegisterActivity extends AppCompatActivity {

    private static final int RC_SIGN_IN = 4219;

    EditText nameInput, mailInput, passwordInput;

    FirebaseAuth firebaseAuth;
    DatabaseReference databaseReference;

    GoogleSignInClient googleSignInClient;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        firebaseAuth = FirebaseAuth.getInstance();
        databaseReference = FirebaseDatabase.getInstance().getReference().child("users");

        nameInput = findViewById( R.id.inputName);
        mailInput = findViewById( R.id.inputMail);
        passwordInput = findViewById( R.id.inputPassword);

        // Configure sign-in to request the user's ID, email address, and basic
        // profile. ID and basic profile are included in DEFAULT_SIGN_IN.
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build();

        // Build a GoogleSignInClient with the options specified by gso.
        googleSignInClient = GoogleSignIn.getClient(this, gso);
    }

    public void registerUser(View view) {

        boolean isFormValid = false;
        String alertToGive = "";

        String nameValidationRegex = "^[a-zA-Z]*$";
        String emailValidationRegex = "[a-zA-Z0-9._-]+@[a-z]+\\.+[a-z]+";

        final String userName = nameInput.getText().toString().trim();
        final String userMail = mailInput.getText().toString().trim();
        final String userPass = passwordInput.getText().toString().trim();


        if( userName.isEmpty() || userMail.isEmpty() || userPass.isEmpty()) {
            alertToGive = "All fields are manadatory";
        } else {
            if ( userName.trim().matches(nameValidationRegex)) {
                if (userMail.trim().matches(emailValidationRegex)) {
                    alertToGive = "Valid email address";

                    if (userPass.trim().length() < 8) {
                        alertToGive = "Password length should be greater than or equal to 8";
                    } else {
                        isFormValid = true;
                    }
                } else {
                    alertToGive = "Invalid email address";
                }
            } else {
                alertToGive = "Name must only contain alphabets";
            }
        }


        if(isFormValid) {
            firebaseAuth.createUserWithEmailAndPassword( userMail, userPass)
                    .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete( Task<AuthResult> task) {
                            if(task.isSuccessful()) {
                                Toast.makeText(RegisterActivity.this, "Registered Successfully", Toast.LENGTH_SHORT).show();

                                databaseReference.child( firebaseAuth.getCurrentUser().getUid()).child("name").setValue(userName);
                                databaseReference.child( firebaseAuth.getCurrentUser().getUid()).child("mail").setValue(userMail);

                                Intent intent = new Intent( getApplicationContext(), MainActivity.class);
                                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                                startActivity(intent);
                            }
                            else Toast.makeText(RegisterActivity.this, "Registration Failed", Toast.LENGTH_SHORT).show();
                        }
                    })
                    .addOnFailureListener(this, new OnFailureListener() {
                        @Override
                        public void onFailure( Exception e) {
                            Toast.makeText(RegisterActivity.this, "Connection Failure", Toast.LENGTH_SHORT).show();
                        }
                    });
        } else {
                Toast.makeText(this, alertToGive, Toast.LENGTH_SHORT).show();
        }
    }

    public void goToLogin(View view) {
        Intent intent = new Intent( this, LoginActivity.class);
        startActivity(intent);
    }

    public void googleSignIn(View view) {
        Intent signInIntent = googleSignInClient.getSignInIntent();
        startActivityForResult(signInIntent, RC_SIGN_IN);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == RC_SIGN_IN) {
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);

            try {
                GoogleSignInAccount account = task.getResult(ApiException.class);
                AuthCredential credential = GoogleAuthProvider.getCredential(account.getIdToken(), null);

                final String userName = account.getEmail();
                final String userMail = account.getDisplayName();
                final String userId = account.getId();

                firebaseAuth.signInWithCredential(credential)
                        .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                            @Override
                            public void onComplete(@NonNull Task<AuthResult> task) {
                                if (task.isSuccessful()) {
                                    databaseReference.child( userId).child("name").setValue(userName);
                                    databaseReference.child( userId).child("mail").setValue(userMail);

                                    Toast.makeText(RegisterActivity.this, "Login Successfull", Toast.LENGTH_SHORT).show();

                                    Intent intent = new Intent( getApplicationContext(), MainActivity.class);
                                    intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                                    startActivity(intent);
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