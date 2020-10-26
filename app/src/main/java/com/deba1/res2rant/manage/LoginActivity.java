package com.deba1.res2rant.manage;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.deba1.res2rant.manage.models.User;
import com.deba1.res2rant.manage.models.UserRole;
import com.google.android.material.snackbar.BaseTransientBottomBar;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

public class LoginActivity extends AppCompatActivity {
    private EditText emailField, passwordField;
    private LinearLayout loading;
    private final FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
    private final FirebaseFirestore db = FirebaseFirestore.getInstance();
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        emailField = findViewById(R.id.loginEmailField);
        emailField.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {}

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                if (!AppHelper.validateRegex(charSequence.toString(), "^[\\w-]+@([\\w-]+\\.)+[\\w-]{2,}$")) {
                    emailField.setError("Invalid Email");
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });
        emailField.setOnEditorActionListener((textView, i, keyEvent) -> {
            Snackbar.make(textView, keyEvent.toString(), BaseTransientBottomBar.LENGTH_LONG).show();
            return false;
        });

        passwordField = findViewById(R.id.loginPasswordField);

        loading = findViewById(R.id.loadingContainer);

        Button loginButton = findViewById(R.id.loginButton);
        loginButton.setOnClickListener(view -> {
            loading.setVisibility(View.VISIBLE);
            String email = emailField.getText().toString();
            String password = passwordField.getText().toString();

            firebaseAuth.signInWithEmailAndPassword(email, password)
                    .addOnSuccessListener(authResult -> {
                        FirebaseUser firebaseUser = authResult.getUser();
                        assert firebaseUser != null;
                        String uid = firebaseUser.getUid();
                        db.collection("users")
                                .document(uid)
                                .get()
                                .addOnSuccessListener(snapshot -> {
                                    User user = snapshot.toObject(User.class);
                                    assert user != null;
                                    redirectToDashboard(user);
                                })
                                .addOnFailureListener(e -> {
                                    loading.setVisibility(View.GONE);
                                    Snackbar.make(view, String.format("Login Failed due to %s", e.getMessage()), BaseTransientBottomBar.LENGTH_SHORT).show();
                                });
                    })
                    .addOnFailureListener(e -> {
                        loading.setVisibility(View.GONE);
                        Snackbar.make(view, String.format("Login Failed due to %s", e.getMessage()), BaseTransientBottomBar.LENGTH_SHORT).show();
                    });
        });
    }

    private void redirectToDashboard(User user) {
        Intent intent;
        if (user.role.equals(UserRole.MANAGER.name())) {
            intent = new Intent(LoginActivity.this, ManagerActivity.class);
            intent.putExtra("name", user.name);
            intent.putExtra("sub", user.email);
            startActivity(intent);
            finish();
        }
        else if (user.role.equals(UserRole.CHEF.name())) {
            intent = new Intent(LoginActivity.this, ChefActivity.class);
            intent.putExtra("name", user.name);
            intent.putExtra("sub", user.email);
            startActivity(intent);
            finish();
        }
        else {
            Toast.makeText(getApplicationContext(), "Only manager and chef can login.", Toast.LENGTH_LONG).show();
            firebaseAuth.signOut();
        }
    }
}
