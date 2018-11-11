package yellow7918.ajou.ac.aunager.login;


import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.widget.Button;
import android.widget.EditText;

import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;

import yellow7918.ajou.ac.aunager.BaseActivity;
import yellow7918.ajou.ac.aunager.MainActivity;
import yellow7918.ajou.ac.aunager.R;


public class SignInActivity extends BaseActivity {

    private FirebaseAuth firebaseAuth;
    private ProgressDialog progressDialog;

    private Button signInButton;
    private Button signUpButton;

    private String email;
    private String password;
    private FirebaseAuth auth;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_in);

        progressDialog = new ProgressDialog(this);
        firebaseAuth = FirebaseAuth.getInstance();

        if (firebaseAuth.getCurrentUser() != null) {
            Intent i = new Intent(SignInActivity.this, MainActivity.class);
            startActivity(i);
            finish();
        }

        EditText emailEdit = findViewById(R.id.email_edit);
        EditText passwordEdit = findViewById(R.id.password_edit);

        signInButton = findViewById(R.id.button_sign_in);
        signInButton.setOnClickListener(v -> {
            showProgressBar("잠시만 기다려 주세요.");

            email = emailEdit.getText().toString();
            password = passwordEdit.getText().toString();

            auth = FirebaseAuth.getInstance();

            if (email.length() <= 0) {
                emailEdit.setError("아이디를 입력해 주세요.");
                hideProgressBar();
            } else if (password.length() <= 0) {
                passwordEdit.setError("비밀번호를 입력해 주세요.");
                hideProgressBar();
            } else {
                Task<AuthResult> authResultTask = auth.signInWithEmailAndPassword(email, password);
                authResultTask.addOnSuccessListener(authResult -> {
                    hideProgressBar();
                    Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                    startActivity(intent);
                    finish();
                });
                authResultTask.addOnFailureListener(e -> {
                    hideProgressBar();
                    Snackbar.make(v, "로그인에 실패하였습니다.", Snackbar.LENGTH_SHORT).show();
                });
            }
        });

        signUpButton = findViewById(R.id.button_sign_up);
        signUpButton.setOnClickListener(view -> {
            Intent intent = new Intent(getBaseContext(), SignUpActivity.class);
            startActivity(intent);
        });

    }

    private void showProgressBar(String text) {
        progressDialog.setMessage(text);
        progressDialog.show();
        signInButton.setEnabled(false);
        signUpButton.setEnabled(false);
    }

    private void hideProgressBar() {
        progressDialog.dismiss();
        signInButton.setEnabled(true);
        signUpButton.setEnabled(true);
    }
}
