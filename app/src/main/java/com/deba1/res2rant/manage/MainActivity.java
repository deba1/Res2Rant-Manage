package com.deba1.res2rant.manage;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.widget.Toast;

import com.deba1.res2rant.manage.models.User;
import com.deba1.res2rant.manage.models.UserRole;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;

import androidx.appcompat.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity {
    FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
    FirebaseFirestore db = FirebaseFirestore.getInstance();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.splash);
        if (firebaseAuth.getUid() == null) {
            new Handler().postDelayed(() -> {
                startActivity(new Intent(MainActivity.this, LoginActivity.class));
                finish();
            }, 1500);
        }
        else {
            db.collection("users")
                    .document(firebaseAuth.getUid())
                    .get()
                    .addOnSuccessListener(snapshot -> {
                        User user = snapshot.toObject(User.class);
                        assert user != null;
                        redirectToDashboard(user);
                    })
                    .addOnFailureListener(e -> {
                        Intent intent = new Intent(getApplicationContext(), LoginActivity.class);
                        intent.putExtra("error", e.getMessage());
                        startActivity(intent);
                        finish();
                    });
        }
    }

    private void redirectToDashboard(User user) {
        Intent intent;
        if (user.role.equals(UserRole.MANAGER.name())) {
            intent = new Intent(MainActivity.this, ManagerActivity.class);
        }
        else if (user.role.equals(UserRole.CHEF.name())) {
            intent = new Intent(MainActivity.this, ChefActivity.class);
        }
        else {
            Toast.makeText(getApplicationContext(), "Only manager and chef can login.", Toast.LENGTH_LONG).show();
            firebaseAuth.signOut();
            intent = new Intent(MainActivity.this, LoginActivity.class);
        }
        intent.putExtra("name", user.name);
        intent.putExtra("sub", user.email);
        startActivity(intent);
        finish();
    }
}