package co.in.nextgencoder.calmlif3;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.HashMap;

public class RegisterActivity extends AppCompatActivity {

    EditText nameInput, mailInput, passwordInput;

    FirebaseAuth firebaseAuth;
    DatabaseReference databaseReference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        firebaseAuth = FirebaseAuth.getInstance();
        databaseReference = FirebaseDatabase.getInstance().getReference().child("users");

        nameInput = findViewById( R.id.inputName);
        mailInput = findViewById( R.id.inputMail);
        passwordInput = findViewById( R.id.inputPassword);
    }

    public void registerUser(View view) {
        final String userName = nameInput.getText().toString().trim();
        final String userMail = mailInput.getText().toString().trim();

        firebaseAuth.createUserWithEmailAndPassword( userMail, passwordInput.getText().toString().trim())
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
    }

    public void goToLogin(View view) {
        Intent intent = new Intent( this, LoginActivity.class);
        startActivity(intent);
    }

    public void googleSignIn(View view) {

    }
}