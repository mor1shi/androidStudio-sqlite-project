package com.example.shop_app;

import android.content.Intent;
import android.os.Bundle;
import android.view.ViewTreeObserver;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.textfield.MaterialAutoCompleteTextView;

public class ProductDetailActivity extends AppCompatActivity {

    public static final String EXTRA_ID = "product_id";

    private Database db;
    private Product product;
    private String imageUri;
    private ImageView ivImage;
    private EditText etName, etPrice, etSku, etQty;
    private MaterialAutoCompleteTextView actCategory, actStatus;

    private final String[] categories = {"Букеты", "Сухоцветы", "Розы", "Лилии"};
    private final String[] statuses = {"active", "hidden"};

    private final ActivityResultLauncher<String[]> pickImage = registerForActivityResult(
            new ActivityResultContracts.OpenDocument(),
            uri -> {
                if (uri != null) {
                    try {
                        getContentResolver().takePersistableUriPermission(
                                uri, Intent.FLAG_GRANT_READ_URI_PERMISSION);
                    } catch (SecurityException ignored) { }
                    imageUri = uri.toString();
                    displayImage();
                }
            });

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_product_detail);

        db = new Database(this);

        int id = getIntent().getIntExtra(EXTRA_ID, -1);
        if (id > 0) {
            product = db.getProductById(id);
            if (product == null) { finish(); return; }
        } else {
            product = new Product();
            product.status = "active";
            product.category = "Букеты";
        }

        ivImage = findViewById(R.id.ivImage);
        etName = findViewById(R.id.etName);
        etPrice = findViewById(R.id.etPrice);
        etSku = findViewById(R.id.etSku);
        etQty = findViewById(R.id.etQty);
        actCategory = findViewById(R.id.actCategory);
        actStatus = findViewById(R.id.actStatus);
        TextView title = findViewById(R.id.tvTitle);

        title.setText(id > 0 ? "Товар" : "Новый товар");

        ArrayAdapter<String> catAdapter = new ArrayAdapter<>(this,
                android.R.layout.simple_dropdown_item_1line, categories);
        actCategory.setAdapter(catAdapter);
        actCategory.setThreshold(0); // показывать список сразу при клике

        ArrayAdapter<String> statusAdapter = new ArrayAdapter<>(this,
                android.R.layout.simple_dropdown_item_1line, statuses);
        actStatus.setAdapter(statusAdapter);
        actStatus.setThreshold(0);

        actCategory.setOnClickListener(v -> actCategory.showDropDown());
        actStatus.setOnClickListener(v -> actStatus.showDropDown());

        // Заполняем поля
        etName.setText(product.getName());
        etPrice.setText(String.valueOf(product.getPrice()));
        etSku.setText(String.valueOf(product.getSku()));
        etQty.setText(String.valueOf(product.getQuantity()));
        actCategory.setText(product.getCategory() != null ? product.getCategory() : "Букеты", false);
        actStatus.setText(product.getStatus() != null ? product.getStatus() : "active", false);

        imageUri = product.getImg();

        ivImage.getViewTreeObserver().addOnGlobalLayoutListener(
                new ViewTreeObserver.OnGlobalLayoutListener() {
                    @Override
                    public void onGlobalLayout() {
                        ivImage.getViewTreeObserver().removeOnGlobalLayoutListener(this);
                        displayImage();
                    }
                });

        ivImage.setOnClickListener(v -> pickImage.launch(new String[]{"image/*"}));

        findViewById(R.id.btnBack).setOnClickListener(v -> finish());
        findViewById(R.id.btnSave).setOnClickListener(v -> save());
    }

    private void displayImage() {
        if (imageUri != null && !imageUri.isEmpty()) {
            ImageLoader.loadInto(this, imageUri, ivImage, ivImage.getWidth());
        }
    }

    private void save() {
        try {
            product.name = etName.getText().toString().trim();
            product.price = Double.parseDouble(etPrice.getText().toString());
            product.sku = Integer.parseInt(etSku.getText().toString());
            product.category = actCategory.getText().toString().trim();
            product.status = actStatus.getText().toString().trim();
            product.quantity = Integer.parseInt(etQty.getText().toString());
            product.img = imageUri;

            if (product.getId() > 0) {
                db.updateProduct(product);
            } else {
                long newId = db.addProduct(product.getName(), product.getPrice(),
                        product.getSku(), product.getCategory(), product.getStatus(),
                        product.getQuantity(), product.getImg());
                product.setId((int) newId);
            }
            Toast.makeText(this, "Сохранено", Toast.LENGTH_SHORT).show();
            finish();
        } catch (NumberFormatException e) {
            Toast.makeText(this, "Проверьте числовые поля", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    protected void onDestroy() {
        db.close();
        super.onDestroy();
    }
}