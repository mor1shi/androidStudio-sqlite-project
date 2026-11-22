package com.example.shop_app;

import android.os.Bundle;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

public class RegisterActivity extends AppCompatActivity {

    private Database db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        db = new Database(this);
        EditText etLogin = findViewById(R.id.etLogin);
        EditText etPass = findViewById(R.id.etPass);
        EditText etPass2 = findViewById(R.id.etPass2);

        findViewById(R.id.btnRegister).setOnClickListener(v -> {
            String login = etLogin.getText().toString().trim();
            String pass = etPass.getText().toString();
            String pass2 = etPass2.getText().toString();

            if (login.isEmpty() || pass.isEmpty()) {
                Toast.makeText(this, "Заполните все поля", Toast.LENGTH_SHORT).show();
                return;
            }
            if (!pass.equals(pass2)) {
                Toast.makeText(this, "Пароли не совпадают", Toast.LENGTH_SHORT).show();
                return;
            }
            if (db.userExists(login)) {
                Toast.makeText(this, "Пользователь уже существует", Toast.LENGTH_SHORT).show();
                return;
            }

            db.addUser(login, pass, "user");
            Toast.makeText(this, "Аккаунт создан", Toast.LENGTH_SHORT).show();
            finish();
        });

        findViewById(R.id.btnBackToLogin).setOnClickListener(v -> finish());
    }

    @Override
    protected void onDestroy() {
        db.close();
        super.onDestroy();
    }
}