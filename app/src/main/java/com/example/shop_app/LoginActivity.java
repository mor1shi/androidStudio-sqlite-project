package com.example.shop_app;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.TextUtils;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.button.MaterialButton;

public class LoginActivity extends AppCompatActivity {

    private EditText etLogin, etPassword;
    private Database db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.login_page);

        db = new Database(this);
        etLogin = findViewById(R.id.editTextText);
        etPassword = findViewById(R.id.editTextTextPassword);

        MaterialButton btn = findViewById(R.id.button);
        btn.setText("Войти");
        btn.setOnClickListener(v -> doLogin());

        TextView tvForgot = findViewById(R.id.tvForgotPassword);
        tvForgot.setOnClickListener(v ->
                startActivity(new Intent(this, ForgotPasswordActivity.class)));

        TextView tvRegister = findViewById(R.id.tvRegister);
        tvRegister.setOnClickListener(v ->
                startActivity(new Intent(this, RegisterActivity.class)));
    }

    private void doLogin() {
        String login = etLogin.getText().toString().trim();
        String pass = etPassword.getText().toString();

        if (TextUtils.isEmpty(login) || TextUtils.isEmpty(pass)) {
            Toast.makeText(this, "Введите логин и пароль", Toast.LENGTH_SHORT).show();
            return;
        }

        String role = db.getUserRole(login, pass);
        if (role == null) {
            Toast.makeText(this, "Неверный логин или пароль", Toast.LENGTH_SHORT).show();
            return;
        }

        SharedPreferences prefs = getSharedPreferences("session", MODE_PRIVATE);
        prefs.edit().putString("login", login).putString("role", role).apply();

        startActivity(new Intent(this, MainActivity.class));
        finish();
    }

    @Override
    protected void onDestroy() {
        db.close();
        super.onDestroy();
    }
}