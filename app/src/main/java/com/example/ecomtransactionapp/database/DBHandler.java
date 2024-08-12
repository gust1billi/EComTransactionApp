package com.example.ecomtransactionapp.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import androidx.annotation.Nullable;

public class DBHandler extends SQLiteOpenHelper {
    private static final String TAG = "DATABASE_HANDLER";
    private final Context ctx;

    private static final String COLUMN_ID = "_id";

    private static final String PRODUCTS_TABLE_NAME = "products";
    private static final String PRODUCT_NAME = "product_name";
    private static final String PRODUCT_PRICE = "product_price";
    private static final String PRODUCT_IMAGE = "product_image";

    // TABEL UNTUK PEMBELI NYA. DPT STRUK; yang sudah dibayar
    private static final String RECEIPT_TABLE_NAME = "receipts";
    private static final String RECEIPTS_TOTAL_PRICE = "receipts_total_price";
    private static final String RECEIPTS_PAID = "receipts_paid";
    private static final String RECEIPTS_KEMBALIAN = "receipts_kembalian";

    // TABEL UNTUK PEMBELI, PRODUK YG DIBELI
    private static final String RECEIPT_PRODUCT_TABLE_NAME = "receipts_product";
    private static final String RECEIPT_ID = "receipts_id";
    private static final String RECEIPT_PRODUCT_ID = "receipts_product_id";
    private static final String RECEIPT_PRODUCT_QTY = "receipts_product_qty";

    // TABEL UNTUK KARYAWAN; menyimpan pesanan yg sudah ada
    private static final String STUB_TABLE_NAME = "stubs";
    private static final String STUBS_NAME = "stubs_name";

    // TABEL UNTUK REFERENSI PRODUK DLM STUB; where stub id to product
    private static final String STUB_PRODUCT_TABLE_NAME = "stubs_product";
    private static final String STUBS_ID = "stubs_id";
    private static final String STUBS_PRODUCT_ID = "stubs_product_id";
    private static final String STUBS_PRODUCT_QTY = "stubs_product_qty";

    private static final String DATABASE_NAME= "ProductLibrary.db";
    private static final int DATABASE_VERSION= 1;

    public DBHandler(@Nullable Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
        this.ctx = context;
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String query;
        query = "CREATE TABLE "+ PRODUCTS_TABLE_NAME + " ("
                + COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
                + PRODUCT_NAME + " TEXT, "
                + PRODUCT_PRICE + " INTEGER, "
                + PRODUCT_IMAGE + " TEXT);";
        db.execSQL(query);
        query = "CREATE TABLE "+ RECEIPT_TABLE_NAME + " ("
                + COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
                + RECEIPTS_TOTAL_PRICE + " INTEGER, "
                + RECEIPTS_PAID + " INTEGER, "
                + RECEIPTS_KEMBALIAN + " INTEGER);";
        db.execSQL(query);
        query = "CREATE TABLE "+ RECEIPT_PRODUCT_TABLE_NAME + " ("
                + COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
                + RECEIPT_ID + " TEXT, "
                + RECEIPT_PRODUCT_ID + " INTEGER, "
                + RECEIPT_PRODUCT_QTY + " TEXT);";
        db.execSQL(query);
        query = "CREATE TABLE "+ STUB_TABLE_NAME + " ("
                + COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
                + STUBS_NAME + " TEXT); ";
        db.execSQL(query);
        query = "CREATE TABLE "+ STUB_PRODUCT_TABLE_NAME + " ("
                + COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
                + STUBS_ID + " INTEGER, "
                + STUBS_PRODUCT_ID + " INTEGER, "
                + STUBS_PRODUCT_QTY + " INTEGER);";
        db.execSQL(query);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int i, int i1) {
        db.execSQL("DROP TABLE IF EXISTS " + PRODUCTS_TABLE_NAME);
        db.execSQL("DROP TABLE IF EXISTS " + RECEIPT_TABLE_NAME);
        db.execSQL("DROP TABLE IF EXISTS " + RECEIPT_PRODUCT_TABLE_NAME);
        db.execSQL("DROP TABLE IF EXISTS " + STUB_TABLE_NAME);
        db.execSQL("DROP TABLE IF EXISTS " + STUB_PRODUCT_TABLE_NAME);
        onCreate(db);
    }

    public void addProduct(String name, int price){
        SQLiteDatabase db = DBHandler.this.getWritableDatabase();
        ContentValues values = assignProductTable(name, price, "");
        long result = db.insert(PRODUCTS_TABLE_NAME, null, values);
        showCallback( result, "Add Product");
    }

    public void addProductAndImage(String name, int price, String imageUri){
        SQLiteDatabase db = DBHandler.this.getWritableDatabase();
        ContentValues values = assignProductTable(name, price, imageUri);
        long result = db.insert(PRODUCTS_TABLE_NAME, null, values);
        showCallback( result, "Add Product & IMG");
    }

    public int addStub(String stubName){
        SQLiteDatabase db = DBHandler.this.getWritableDatabase();
        ContentValues stubValues = assignStub(stubName);

        long result = db.insert(STUB_TABLE_NAME, null, stubValues);
        showCallback( result, "Add STUB");
        return getStubId( stubName );
    }

    private ContentValues assignStub(String stubName) {
        ContentValues values = new ContentValues();
        values.put(STUBS_NAME, stubName);
        return values;
    }

    public void addStubProduct(int stubId, int product_id, int product_qty){
        SQLiteDatabase db = DBHandler.this.getWritableDatabase();
        ContentValues stubProductValues = assignStubProductTable(stubId, product_id, product_qty);
        long result = db.insert(STUB_PRODUCT_TABLE_NAME, null, stubProductValues);
        showCallback(result, "ADD STUB PRODUCT");
    }

    private ContentValues assignStubProductTable(int stubId, int product_id, int stub_product_qty) {
        ContentValues values = new ContentValues();
        values.put(STUBS_ID, stubId);
        values.put(STUBS_PRODUCT_ID, product_id);
        values.put(STUBS_PRODUCT_QTY, stub_product_qty);
        return values;
    }

    public int addReceipt(int total_price, int pembayaran, int kembalian) {
        SQLiteDatabase db = DBHandler.this.getWritableDatabase();
        ContentValues receiptValues = assignReceipt(total_price, pembayaran, kembalian);
        long result = db.insert(RECEIPT_TABLE_NAME, null, receiptValues);
        showCallback(result, "RECEIPT TABLE");
        return getReceiptId();
    }

    private ContentValues assignReceipt(int total_price, int pembayaran, int kembalian){
        ContentValues values = new ContentValues();
        values.put(RECEIPTS_TOTAL_PRICE, total_price);
        values.put(RECEIPTS_PAID, pembayaran);
        values.put(RECEIPTS_KEMBALIAN, kembalian);
        return values;
    }

    public void addReceiptProduct( int receipt_id, int product_id, int product_qty ){
        SQLiteDatabase db = DBHandler.this.getWritableDatabase();
        ContentValues receiptProductValues =
                assignReceiptProducts(receipt_id, product_id, product_qty);
        long result = db.insert(RECEIPT_PRODUCT_TABLE_NAME, null, receiptProductValues);
        showCallback(result, "ADD RECEIPT PRODUCTS");
    }

    private ContentValues assignReceiptProducts(int receipt_id, int product_id, int product_qty) {
        ContentValues values = new ContentValues();
        values.put(RECEIPT_ID, receipt_id);
        values.put(RECEIPT_PRODUCT_ID, product_id);
        values.put(RECEIPT_PRODUCT_QTY, product_qty);
        return values;
    }

    public Cursor readAllProducts(){
        Cursor cursor = null;
        SQLiteDatabase db = DBHandler.this.getReadableDatabase();

        if (db != null)
            cursor = db.rawQuery("SELECT * FROM " + PRODUCTS_TABLE_NAME
                            + " ORDER BY " + PRODUCT_NAME
                    , null);

        return cursor;
    }

    private ContentValues assignProductTable(String name, int price, String imageUri) {
        ContentValues values = new ContentValues();
        values.put(PRODUCT_NAME, name);
        values.put(PRODUCT_PRICE, price);
        values.put(PRODUCT_IMAGE, imageUri);
        return values;
    }

    private void showCallback( long result, String callback ){
        if (result == -1){
            callback = "Failure: " + callback;
        } else callback = "Success: "  + callback;
        Log.e(TAG, "" + callback );
    }

    private int getStubId(String name) {
        SQLiteDatabase db = DBHandler.this.getReadableDatabase();
        Cursor cursor;

        cursor = db.rawQuery(
                "SELECT " + COLUMN_ID + " FROM " + STUB_TABLE_NAME +
                        " WHERE " + STUBS_NAME + " = \"" + name + "\""
                , null);
        cursor.moveToLast(); // Move to Last, as this is only used to assign stub products
        int result = cursor.getInt(0); cursor.close();
        return result;
    }

    public int getProductId(String name) {
        SQLiteDatabase db = DBHandler.this.getReadableDatabase();
        Cursor cursor;

        cursor = db.rawQuery(
                "SELECT " + COLUMN_ID + " FROM " + PRODUCTS_TABLE_NAME +
                        " WHERE " + PRODUCT_NAME + " = \"" + name + "\""
                ,null);
        cursor.moveToFirst();
        return cursor.getInt(0);
    }

    private int getReceiptId(){
        SQLiteDatabase db = DBHandler.this.getReadableDatabase();
        Cursor cursor;

        cursor = db.rawQuery("SELECT " + COLUMN_ID + " FROM " + RECEIPT_TABLE_NAME
                , null );
        cursor.moveToLast();
        return cursor.getInt(0);
    }

}
