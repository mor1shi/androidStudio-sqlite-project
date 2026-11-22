package com.example.shop_app;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.widget.EditText;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.card.MaterialCardView;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.List;

public class MainActivity extends AppCompatActivity implements ProductAdapter.Listener {

    private Database db;
    private ProductAdapter adapter;
    private String currentCategory = null;
    private String currentSearch = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        WindowCompat.enableEdgeToEdge(getWindow());
        setContentView(R.layout.activity_main);

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets b = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(b.left, b.top, b.right, b.bottom);
            return insets;
        });

        db = new Database(this);
        adapter = new ProductAdapter(this);

        RecyclerView rv = findViewById(R.id.recycler);
        rv.setLayoutManager(new LinearLayoutManager(this));
        rv.setAdapter(adapter);

        FloatingActionButton fab = findViewById(R.id.fabAdd);
        fab.setOnClickListener(v -> {
            Intent i = new Intent(this, ProductDetailActivity.class);
            startActivity(i); // без EXTRA_ID → режим "создать"
        });

        EditText search = findViewById(R.id.search);
        search.addTextChangedListener(new TextWatcher() {
            @Override public void beforeTextChanged(CharSequence s, int a, int b, int c) {}
            @Override public void onTextChanged(CharSequence s, int a, int b, int c) {
                currentSearch = s.toString().trim();
                reload();
            }
            @Override public void afterTextChanged(Editable s) {}
        });

        // фильтры
        findViewById(R.id.catAll).setOnClickListener(v -> setCategory(null));
        findViewById(R.id.catBouquets).setOnClickListener(v -> setCategory("Букеты"));
        findViewById(R.id.catDried).setOnClickListener(v -> setCategory("Сухоцветы"));
        findViewById(R.id.catRoses).setOnClickListener(v -> setCategory("Розы"));
        findViewById(R.id.catLilies).setOnClickListener(v -> setCategory("Лилии"));

        setCategory(null);
    }

    private void setCategory(String cat) {
        currentCategory = cat;
        updateChip(R.id.catAll,      cat == null);
        updateChip(R.id.catBouquets, "Букеты".equals(cat));
        updateChip(R.id.catDried,    "Сухоцветы".equals(cat));
        updateChip(R.id.catRoses,    "Розы".equals(cat));
        updateChip(R.id.catLilies,   "Лилии".equals(cat));
        reload();
    }

    private void updateChip(int id, boolean active) {
        MaterialCardView cv = findViewById(id);
        cv.setCardBackgroundColor(active
                ? ContextCompat.getColor(this, R.color.colorAccent)
                : 0xFF2A6457);
    }

    private void reload() {
        List<Product> list;
        if (currentCategory == null) {
            list = currentSearch.isEmpty()
                    ? db.getAllProducts()
                    : db.searchProducts(currentSearch);
        } else {
            list = currentSearch.isEmpty()
                    ? db.getProductsByCategory(currentCategory)
                    : db.getProductsByCategoryAndSearch(currentCategory, currentSearch);
        }
        adapter.setItems(list);
    }

    @Override
    public void onEdit(Product p) {
        Intent i = new Intent(this, ProductDetailActivity.class);
        i.putExtra(ProductDetailActivity.EXTRA_ID, p.getId());
        startActivity(i);
    }

    @Override
    public void onDelete(Product p) {
        new androidx.appcompat.app.AlertDialog.Builder(this)
                .setMessage("Удалить «" + p.getName() + "»?")
                .setPositiveButton("Удалить", (d, w) -> {
                    db.deleteProduct(p.getId());
                    reload();
                })
                .setNegativeButton("Отмена", null)
                .show();
    }

    @Override
    public void onItemDoubleClick(Product p) {
        Intent i = new Intent(this, ProductDetailActivity.class);
        i.putExtra(ProductDetailActivity.EXTRA_ID, p.getId());
        startActivity(i);
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (adapter != null) reload(); // обновление после редактирования
    }

    @Override
    protected void onDestroy() {
        db.close();
        super.onDestroy();
    }
}