package com.example.shop_app;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;

public class ProductEditorDialog {

    public interface Callback { void onSave(Product p); }

    public static void show(Context ctx, Product existing, Callback cb) {
        View view = LayoutInflater.from(ctx).inflate(R.layout.dialog_product, null);
        EditText etName = view.findViewById(R.id.etName);
        EditText etPrice = view.findViewById(R.id.etPrice);
        EditText etSku = view.findViewById(R.id.etSku);
        EditText etCategory = view.findViewById(R.id.etCategory);
        EditText etStatus = view.findViewById(R.id.etStatus);
        EditText etQty = view.findViewById(R.id.etQty);
        EditText etImg = view.findViewById(R.id.etImg);

        if (existing != null) {
            etName.setText(existing.getName());
            etPrice.setText(String.valueOf(existing.getPrice()));
            etSku.setText(String.valueOf(existing.getSku()));
            etCategory.setText(existing.getCategory());
            etStatus.setText(existing.getStatus());
            etQty.setText(String.valueOf(existing.getQuantity()));
            etImg.setText(existing.getImg());
        } else {
            etStatus.setText("active");
            etImg.setText("bflower");
        }

        new AlertDialog.Builder(ctx)
                .setTitle(existing == null ? "Новый товар" : "Редактировать товар")
                .setView(view)
                .setPositiveButton("Сохранить", (d, w) -> {
                    try {
                        Product p = new Product(
                                etName.getText().toString().trim(),
                                Double.parseDouble(etPrice.getText().toString()),
                                Integer.parseInt(etSku.getText().toString()),
                                etCategory.getText().toString().trim(),
                                etStatus.getText().toString().trim(),
                                Integer.parseInt(etQty.getText().toString()),
                                etImg.getText().toString().trim()
                        );
                        cb.onSave(p);
                    } catch (NumberFormatException e) {
                        Toast.makeText(ctx, "Проверьте числовые поля", Toast.LENGTH_SHORT).show();
                    }
                })
                .setNegativeButton("Отмена", null)
                .show();
    }
}