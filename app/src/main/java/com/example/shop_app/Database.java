package com.example.shop_app;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import androidx.annotation.Nullable;

import java.util.ArrayList;
import java.util.List;

public class Database extends SQLiteOpenHelper {

    private static final String DB_NAME = "shop_db";
    private static final int DB_VERSION = 4;

    public static class UserTable {
        public static final String TABLE_NAME = "users";
        public static final String COLUMN_ID = "id";
        public static final String COLUMN_USERNAME = "name";
        public static final String COLUMN_PASSWORD = "password";
        public static final String COLUMN_ROLE = "role";
    }

    public static class ProductsTable {
        public static final String TABLE_NAME = "products";
        public static final String COLUMN_ID = "id";
        public static final String COLUMN_PRODUCTNAME = "productname";
        public static final String COLUMN_PRODUCTPRICE = "productprice";
        public static final String COLUMN_SKU = "sku";
        public static final String COLUMN_CATEGORY = "category";
        public static final String COLUMN_PRODUCTSTATUS = "productstatus";
        public static final String COLUMN_QUANTITY = "quantity";
        public static final String COLUMN_IMG = "img";
    }

    public Database(@Nullable Context context) {
        super(context, DB_NAME, null, DB_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("CREATE TABLE " + UserTable.TABLE_NAME + " (" +
                UserTable.COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                UserTable.COLUMN_USERNAME + " TEXT, " +
                UserTable.COLUMN_PASSWORD + " TEXT, " +
                UserTable.COLUMN_ROLE + " TEXT);");

        db.execSQL("CREATE TABLE " + ProductsTable.TABLE_NAME + " (" +
                ProductsTable.COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                ProductsTable.COLUMN_PRODUCTNAME + " TEXT, " +
                ProductsTable.COLUMN_PRODUCTPRICE + " REAL, " +
                ProductsTable.COLUMN_SKU + " INTEGER, " +
                ProductsTable.COLUMN_CATEGORY + " TEXT, " +
                ProductsTable.COLUMN_PRODUCTSTATUS + " TEXT, " +
                ProductsTable.COLUMN_QUANTITY + " INTEGER, " +
                ProductsTable.COLUMN_IMG + " TEXT);");

        // дефолтный админ
        db.execSQL("INSERT INTO " + UserTable.TABLE_NAME +
                " (name, password, role) VALUES ('admin','admin','admin');");

        // демо товары по всем категориям
        seed(db, "Архидея белая цапля", 2490, 1001, "Букеты",    "active",  5, "whiteorcid");
        seed(db, "Белый сад",    3200, 1002, "Букеты",    "active",  3, "whitebouquet");
        seed(db, "Тёплый вечер", 1890, 1003, "Сухоцветы", "active",  8, "dryflowers");
        seed(db, "Красные розы", 3500, 1004, "Розы",      "active", 12, "roses");
        seed(db, "Белые лилии",  2900, 1005, "Лилии",     "active",  6, "whitelily");
    }

    private void seed(SQLiteDatabase db, String name, double price, int sku,
                      String cat, String status, int qty, String img) {
        ContentValues v = new ContentValues();
        v.put(ProductsTable.COLUMN_PRODUCTNAME, name);
        v.put(ProductsTable.COLUMN_PRODUCTPRICE, price);
        v.put(ProductsTable.COLUMN_SKU, sku);
        v.put(ProductsTable.COLUMN_CATEGORY, cat);
        v.put(ProductsTable.COLUMN_PRODUCTSTATUS, status);
        v.put(ProductsTable.COLUMN_QUANTITY, qty);
        v.put(ProductsTable.COLUMN_IMG, img);
        db.insert(ProductsTable.TABLE_NAME, null, v);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + UserTable.TABLE_NAME);
        db.execSQL("DROP TABLE IF EXISTS " + ProductsTable.TABLE_NAME);
        onCreate(db);
    }

    // USERS

    public long addUser(String username, String password, String role) {
        SQLiteDatabase db = getWritableDatabase();
        ContentValues v = new ContentValues();
        v.put(UserTable.COLUMN_USERNAME, username);
        v.put(UserTable.COLUMN_PASSWORD, password);
        v.put(UserTable.COLUMN_ROLE, role);
        long id = db.insert(UserTable.TABLE_NAME, null, v);
        db.close();
        return id;
    }

    public String getUserRole(String username, String password) {
        SQLiteDatabase db = getReadableDatabase();
        Cursor c = db.query(UserTable.TABLE_NAME, new String[]{UserTable.COLUMN_ROLE},
                UserTable.COLUMN_USERNAME + "=? AND " + UserTable.COLUMN_PASSWORD + "=?",
                new String[]{username, password}, null, null, null);
        String role = null;
        if (c.moveToFirst()) role = c.getString(0);
        c.close();
        db.close();
        return role;
    }

    public boolean userExists(String username) {
        SQLiteDatabase db = getReadableDatabase();
        Cursor c = db.query(UserTable.TABLE_NAME, new String[]{UserTable.COLUMN_ID},
                UserTable.COLUMN_USERNAME + "=?", new String[]{username},
                null, null, null);
        boolean exists = c.moveToFirst();
        c.close();
        db.close();
        return exists;
    }

    public boolean updateUserPassword(String username, String newPassword) {
        SQLiteDatabase db = getWritableDatabase();
        ContentValues v = new ContentValues();
        v.put(UserTable.COLUMN_PASSWORD, newPassword);
        int rows = db.update(UserTable.TABLE_NAME, v,
                UserTable.COLUMN_USERNAME + "=?", new String[]{username});
        db.close();
        return rows > 0;
    }

    // PRODUCTS

    // CREATE
    public long addProduct(String name, double price, int sku,
                           String category, String status, int quantity, String img) {
        SQLiteDatabase db = getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(ProductsTable.COLUMN_PRODUCTNAME, name);
        values.put(ProductsTable.COLUMN_PRODUCTPRICE, price);
        values.put(ProductsTable.COLUMN_SKU, sku);
        values.put(ProductsTable.COLUMN_CATEGORY, category);
        values.put(ProductsTable.COLUMN_PRODUCTSTATUS, status);
        values.put(ProductsTable.COLUMN_QUANTITY, quantity);
        values.put(ProductsTable.COLUMN_IMG, img);
        long id = db.insert(ProductsTable.TABLE_NAME, null, values);
        db.close();
        return id;
    }

    // READ

    public List<Product> getAllProducts() { return queryProducts(null, null); }

    public List<Product> searchProducts(String q) {
        return queryProducts(ProductsTable.COLUMN_PRODUCTNAME + " LIKE ?",
                new String[]{"%" + q + "%"});
    }

    public List<Product> getProductsByCategory(String category) {
        return queryProducts(ProductsTable.COLUMN_CATEGORY + "=?", new String[]{category});
    }

    public List<Product> getProductsByCategoryAndSearch(String category, String q) {
        return queryProducts(
                ProductsTable.COLUMN_CATEGORY + "=? AND " +
                        ProductsTable.COLUMN_PRODUCTNAME + " LIKE ?",
                new String[]{category, "%" + q + "%"});
    }

    public Product getProductById(int id) {
        List<Product> list = queryProducts(ProductsTable.COLUMN_ID + "=?",
                new String[]{String.valueOf(id)});
        return list.isEmpty() ? null : list.get(0);
    }

    private List<Product> queryProducts(String where, String[] args) {
        List<Product> result = new ArrayList<>();
        SQLiteDatabase db = getReadableDatabase();
        Cursor c = db.query(ProductsTable.TABLE_NAME, null, where, args, null, null,
                ProductsTable.COLUMN_ID + " DESC");
        if (c.moveToFirst()) {
            do {
                result.add(new Product(
                        c.getInt(c.getColumnIndexOrThrow(ProductsTable.COLUMN_ID)),
                        c.getString(c.getColumnIndexOrThrow(ProductsTable.COLUMN_PRODUCTNAME)),
                        c.getDouble(c.getColumnIndexOrThrow(ProductsTable.COLUMN_PRODUCTPRICE)),
                        c.getInt(c.getColumnIndexOrThrow(ProductsTable.COLUMN_SKU)),
                        c.getString(c.getColumnIndexOrThrow(ProductsTable.COLUMN_CATEGORY)),
                        c.getString(c.getColumnIndexOrThrow(ProductsTable.COLUMN_PRODUCTSTATUS)),
                        c.getInt(c.getColumnIndexOrThrow(ProductsTable.COLUMN_QUANTITY)),
                        c.getString(c.getColumnIndexOrThrow(ProductsTable.COLUMN_IMG))
                ));
            } while (c.moveToNext());
        }
        c.close();
        db.close();
        return result;
    }

    // UPDATE

    public int updateProduct(Product p) {
        SQLiteDatabase db = getWritableDatabase();
        ContentValues v = new ContentValues();
        v.put(ProductsTable.COLUMN_PRODUCTNAME, p.getName());
        v.put(ProductsTable.COLUMN_PRODUCTPRICE, p.getPrice());
        v.put(ProductsTable.COLUMN_SKU, p.getSku());
        v.put(ProductsTable.COLUMN_CATEGORY, p.getCategory());
        v.put(ProductsTable.COLUMN_PRODUCTSTATUS, p.getStatus());
        v.put(ProductsTable.COLUMN_QUANTITY, p.getQuantity());
        v.put(ProductsTable.COLUMN_IMG, p.getImg());
        int rows = db.update(ProductsTable.TABLE_NAME, v,
                ProductsTable.COLUMN_ID + "=?", new String[]{String.valueOf(p.getId())});
        db.close();
        return rows;
    }

    // DELETE
    public int deleteProduct(int id) {
        SQLiteDatabase db = getWritableDatabase();
        int rows = db.delete(ProductsTable.TABLE_NAME,
                ProductsTable.COLUMN_ID + "=?", new String[]{String.valueOf(id)});
        db.close();
        return rows;
    }
}