package com.example.shop_app;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class ProductAdapter extends RecyclerView.Adapter<ProductAdapter.VH> {

    public interface Listener {
        void onEdit(Product p);
        void onDelete(Product p);
        void onItemDoubleClick(Product p);
    }

    private final List<Product> items = new ArrayList<>();
    private final Listener listener;

    public ProductAdapter(Listener listener) { this.listener = listener; }

    public void setItems(List<Product> newItems) {
        items.clear();
        items.addAll(newItems);
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public VH onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_product, parent, false);
        return new VH(v);
    }

    @Override
    public void onBindViewHolder(@NonNull VH h, int position) {
        Product p = items.get(position);
        h.tvName.setText(p.getName());
        h.tvCategory.setText(p.getCategory());
        h.tvPrice.setText(String.format(Locale.getDefault(), "%.0f ₽", p.getPrice()));

        int thumbPx = (int) (92 * h.itemView.getResources().getDisplayMetrics().density);
        ImageLoader.loadInto(h.itemView.getContext(), p.getImg(), h.ivImg, thumbPx);

        h.btnEdit.setOnClickListener(v -> listener.onEdit(p));
        h.btnDelete.setOnClickListener(v -> listener.onDelete(p));

        // двойной клик по карточке
        h.itemView.setOnClickListener(v -> {
            long now = System.currentTimeMillis();
            if (now - h.lastClick < 300) {
                listener.onItemDoubleClick(p);
            }
            h.lastClick = now;
        });
    }

    @Override
    public int getItemCount() { return items.size(); }

    static class VH extends RecyclerView.ViewHolder {
        ImageView ivImg;
        TextView tvName, tvCategory, tvPrice;
        ImageButton btnEdit, btnDelete;
        long lastClick = 0;

        VH(@NonNull View itemView) {
            super(itemView);
            ivImg = itemView.findViewById(R.id.ivImg);
            tvName = itemView.findViewById(R.id.tvName);
            tvCategory = itemView.findViewById(R.id.tvCategory);
            tvPrice = itemView.findViewById(R.id.tvPrice);
            btnEdit = itemView.findViewById(R.id.btnEdit);
            btnDelete = itemView.findViewById(R.id.btnDelete);
        }
    }
}