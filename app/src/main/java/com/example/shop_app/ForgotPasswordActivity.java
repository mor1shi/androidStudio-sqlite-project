package com.example.shop_app;

import android.os.Bundle;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

public class ForgotPasswordActivity extends AppCompatActivity {

    private Database db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forgot_password);

        db = new Database(this);
        EditText etLogin = findViewById(R.id.etLogin);
        EditText etPass = findViewById(R.id.etPass);
        EditText etPass2 = findViewById(R.id.etPass2);

        findViewById(R.id.btnReset).setOnClickListener(v -> {
            String login = etLogin.getText().toString().trim();
            String pass = etPass.getText().toString();
            String pass2 = etPass2.getText().toString();

            if (!db.userExists(login)) {
                Toast.makeText(this, "Пользователь не найден", Toast.LENGTH_SHORT).show();
                return;
            }
            if (pass.isEmpty() || !pass.equals(pass2)) {
                Toast.makeText(this, "Пароли пусты или не совпадают", Toast.LENGTH_SHORT).show();
                return;
            }
            db.updateUserPassword(login, pass);
            Toast.makeText(this, "Пароль изменён", Toast.LENGTH_SHORT).show();
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
