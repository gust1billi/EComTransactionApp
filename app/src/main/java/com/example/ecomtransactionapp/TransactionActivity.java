package com.example.ecomtransactionapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.database.Cursor;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.RetryPolicy;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.example.ecomtransactionapp.database.DBHandler;
import com.example.ecomtransactionapp.recycler_view.Cart;
import com.example.ecomtransactionapp.recycler_view.CartAdapter;
import com.example.ecomtransactionapp.recycler_view.Product;
import com.example.ecomtransactionapp.recycler_view.ProductAdapter;
import com.example.ecomtransactionapp.recycler_view.Stub;
import com.example.ecomtransactionapp.recycler_view.StubAdapter;
import com.example.ecomtransactionapp.recycler_view.Transaction;
import com.google.android.material.navigation.NavigationView;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class TransactionActivity extends AppCompatActivity implements
        NavigationView.OnNavigationItemSelectedListener {
    private final String TAG = "TRANSACTION_ACTIVITY";

    RecyclerView mainRV, cartRV;
    LinearLayoutManager productLayoutManager, cartLayoutManager;
    ProductAdapter productAdapter; CartAdapter cartAdapter;

    List<Product> productList = new ArrayList<>();
    List<Product> filteredProductList = new ArrayList<>();
    List<Transaction> transactionList = new ArrayList<>(); // Apa yg bisa gw filter kalo ini?
    List<Stub> stubList = new ArrayList<>();
    List<Stub> filteredStubList = new ArrayList<>();
    List<Cart> cartList = new ArrayList<>();
    List<String> titleList = new ArrayList<>();

    DrawerLayout drawerLayout; NavigationView navView;
    ActionBarDrawerToggle actionBarDrawerToggle;

    ImageButton reduce, add;
    Button deleteBtn, paymentBtn, stubBtn;
    ProgressBar loadingAnimation;

    TextView waiting, dialogQTY, buyerName, orderNum, totalPriceNum, totalQtyNum;
    EditText dialog_edit_stub_name;
    SearchView productSearchView;

    DBHandler dbHandler;

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if ( actionBarDrawerToggle.onOptionsItemSelected(item) ) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.hello){
            Utils.showToast(TransactionActivity.this, "Hello World!");
            return true;
        } else if (id == R.id.products) {
//            checkProductData();
            switchRecyclerViewDisplay(0);
        } else if (id == R.id.transaksi){
            // posisi sama, tapi isi beda. Cek SQLite; if not null assign to new adapter
            Log.e(TAG, "Halaman Riwayat Transaksi");
            switchRecyclerViewDisplay(1);
        } else if (id == R.id.simpanan) {
            Log.e(TAG, "Halaman Simpanan");
            switchRecyclerViewDisplay(2);
        } else if (id == R.id.logout) {
            finish();
        }
        return false;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_transaction);
        activityInit();

        dbHandler = new DBHandler(TransactionActivity.this );

        recyclerViewInits();

        drawerLayout = findViewById(R.id.transaction_drawer_layout);
        navView = findViewById(R.id.transaction_nav_view);
        if ( navView != null ){ navView.setNavigationItemSelectedListener(this); }
        actionBarDrawerToggle = new ActionBarDrawerToggle(
                this, drawerLayout, R.string.nav_open, R.string.nav_close);
        drawerLayout.addDrawerListener(actionBarDrawerToggle);
        actionBarDrawerToggle.syncState();
        // to make the Navigation drawer icon always appear on the action bar
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        navView.setNavigationItemSelectedListener(TransactionActivity.this ); // Lambda --> On Navigation Item Listener

        loadingAnimation = findViewById(R.id.waiting);
        waiting = findViewById(R.id.waiting_text);
        buyerName = findViewById(R.id.buyer_name); buyerName.setText(R.string.default_user);
        orderNum = findViewById(R.id.order_number); orderNum.setText("1");
        totalPriceNum = findViewById(R.id.total_price_number);
        totalQtyNum = findViewById(R.id.vat_number);

        deleteBtn = findViewById(R.id.deleteBtn);
        deleteBtn.setOnClickListener(view -> clearCart());
        stubBtn = findViewById(R.id.stubBtn);
        stubBtn.setOnClickListener(view -> callStubDialog());
        paymentBtn = findViewById(R.id.paymentBtn);
        paymentBtn.setOnClickListener(view -> callPaymentActivity());

        productSearchView = findViewById(R.id.product_search_view);
        productSearchView.setOnQueryTextListener(new androidx.appcompat.widget.SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String s) { return false; /* IGNORE */ }

            @Override
            public boolean onQueryTextChange(String s) {
                if ( !productList.isEmpty() ) {
                    if ( !s.isEmpty() ) {
                        filterProducts( s ); // SET FILTERED LIST TO ADAPTER
                    } else productAdapter.setFilteredList( productList );
                    // ELSE RETURNS ORIGINAL PRODUCT LIST TO RECYCLER VIEW
                    return true;
                } else return false;
            }
        });

        checkProductData();
    }

    private void switchRecyclerViewDisplay(int value) {
        Cursor cursor;
        // Keep using the old files or create new ones?
        switch (value){
            case 0:
                checkProductData(); // Halaman Transaksi Utama,
                break;
            case 1:
                cursor = dbHandler.readAllReceipts();
                break;
            case 2:
                cursor = dbHandler.readAllStubs( );
                stubList.clear();
                if (cursor.getCount() > 0 ){
                    while (cursor.moveToNext() ){
                        stubList.add( new Stub(
                                cursor.getString(1),
                                cursor.getInt(0),
                                cursor.getInt(2),
                                cursor.getInt(3)
                        ) );
                    }

                    StubAdapter stubAdapter = new StubAdapter(TransactionActivity.this, stubList);
                    mainRV.setAdapter(stubAdapter);
                } else Utils.showToast(TransactionActivity.this, "Tabel Simpanan Kosong");
                // Kalau kosong ga jadi, Toast kosong bro

                break;
        }
    }

    private void callStubDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(TransactionActivity.this);
        LayoutInflater inflater = TransactionActivity.this.getLayoutInflater();

        builder.setView(inflater.inflate( R.layout.dialog_stub, null ) )
                .setTitle("SET STUBBED CART NAME")
                .setNegativeButton(R.string.cancel, (dialogInterface, i) -> { /* IGNORE */ })
                .setPositiveButton(R.string.stub_ok, (dialogInterface, i) -> {
                    if ( dialog_edit_stub_name.getText().length() != 0) {
                        stubWholeCart( dialog_edit_stub_name.getText().toString() );
                    } else Utils.showToast(TransactionActivity.this,
                            "Please insert stub name");
                } );
        AlertDialog dialog = builder.create(); dialog.show();
        dialog_edit_stub_name = dialog.findViewById(R.id.stub_edit_name);
    }

    private void stubWholeCart(String stubName) {
        int product_id, stub_id;
        stub_id = dbHandler.addStub(stubName);

        for ( Cart item: cartList ) {
            product_id = dbHandler.getProductId( item.getName() );
            dbHandler.addStubProduct(
                    stub_id,
                    product_id,
                    item.getQuantity() );
        }   clearCart();
        Utils.showToast(TransactionActivity.this, "Keranjang Berhasil Disimpan");
    }

    private void clearCart() {
        int size = titleList.size();
        titleList.clear(); cartList.clear();
        cartAdapter.itemPurged( size ); update_total( );
    }

    private void filterProducts( String filter ) {
        filteredProductList = new ArrayList<>();
        for ( Product filteredProduct : productList ) {
            if ( filteredProduct.getProductName().toLowerCase(Locale.ROOT)
                    .contains( filter.toLowerCase( Locale.ROOT ) )){
                filteredProductList.add( filteredProduct );
            }
        } productAdapter.setFilteredList( filteredProductList );
    }

    private void checkProductData() {
        Cursor cursor = dbHandler.readAllProducts( );
        productList.clear();

        if ( cursor.getCount() > 0 ){

            while ( cursor.moveToNext() ){
                productList.add( new Product(
                        cursor.getString(1),
                        cursor.getString(2)
                ) );
            }
            productAdapter = new ProductAdapter(TransactionActivity.this, productList );
            mainRV.setAdapter(productAdapter);
            loadingAnimation.setVisibility(View.INVISIBLE);
            waiting.setVisibility(View.INVISIBLE);
        } else {
            // CHECKS IF IT EXIST ON SQLITE IF NOT, PING API
            Bundle extras = getIntent().getExtras();
            String code = extras.getString("code");
            productAPIRequest( code ); // To give data to the Adapter
        }

    }

    private void productAPIRequest(String token) {
        String url = "https://tmiapi-dev.mitraindogrosir.co.id/api/get_data_member";
        RequestQueue queue = Volley.newRequestQueue(TransactionActivity.this);

        StringRequest request = new StringRequest(Request.Method.POST, url,
                response -> {
                    try {
                        JSONObject memberList = new JSONObject(response);
                        JSONArray memberData = memberList
                                .getJSONObject("message")
                                .getJSONArray("data_product");

                        Log.e("Status Value", memberList.getString("status"));
                        Log.e("Size", "products size is " + memberData.length());

//                        String date = getDate();
                        ExecutorService service = Executors.newFixedThreadPool(256);

                        for (int i = 0; i < memberData.length(); i++) {
                            JSONObject apple = memberData.getJSONObject(i);
                            // Log.e(TAG, apple.getString("product_name"));
//                                JSONArray barcode = apple.getJSONArray("barcode");
//                                int position = i+1;

                            service.execute( () -> {
                                try {
                                    dbHandler.addProduct(
                                            apple.getString("product_name"),
                                            Integer.parseInt( apple.getString("price" ) )
                                    );
                                } catch (Exception e) {
                                    e.printStackTrace();
                                }
                            });

                            productList.add(new Product(
                                    apple.getString("product_name"),
                                    apple.getString("product_code"),
                                    apple.getString("price")
                            ));
                        } Log.e(TAG, "LIST SIZE: " + productList.size() );
                        productAdapter = new ProductAdapter(TransactionActivity.this, productList);
                        mainRV.setAdapter(productAdapter);
                    } catch (Exception e) {
                        e.printStackTrace();
                    } finally {
                        loadingAnimation.setVisibility(View.INVISIBLE);
                        waiting.setVisibility(View.INVISIBLE);
                    }
                }, error -> {
                    Utils.showToast(TransactionActivity.this, "API Failed: " + error);
                    Log.e("Error POST VOLLEY", error.toString());
        }) {
            @Override
            public Map<String, String> getHeaders( ) {
                Map<String, String> headers = new HashMap<>();
                headers.put("Authorization", "Bearer " + token); return headers;
            }
        };

        // EXTENDS TIMEOUT TIMER. TO AVOID RTO ERROR
        request.setRetryPolicy(new RetryPolicy() {
            @Override
            public int getCurrentTimeout() {
                return 50000; // time in milliseconds
            }

            @Override
            public int getCurrentRetryCount() {
                return 50000; // time in milliseconds
            }

            @Override
            public void retry(VolleyError error) { /* IGNORE */ }
        });

        queue.add(request);
    }

    // TOMBOL UNTUK RV YG SEMUA PRODUK

    public void addToCart(String name, String price) {
        // JIKA JUDUL BELUM PERNAH MASUK KE CART
        if ( !titleList.contains( name ) ){
            titleList.add( name );
            Log.e(TAG, "TITLE LIST: " + titleList.size());
            cartList.add( new Cart( name, price ) );
            cartAdapter.newItemAdded( titleList.indexOf( name ) );
        } else {
            cartList.get( titleList.indexOf( name) ).addQuantity();
            cartAdapter.itemAdded( titleList.indexOf( name ) );
        }
        update_total();
    }
    // DIALOG POP UP UNTUK MENGGANTI QTY PRODUK DALAM CART

    public void setQuantityDialog(String product_name, String currentQty, int position) {
        AlertDialog.Builder builder = new AlertDialog.Builder(TransactionActivity.this);
        LayoutInflater inflater = TransactionActivity.this.getLayoutInflater();

        builder.setView(inflater.inflate (R.layout.dialog_quantity, null ) )
                .setTitle( "Change Quantity: " + product_name )
                .setPositiveButton(R.string.qty_ok, (dialogInterface, i) -> {
                    // USER TAPS THE CONFIRM BUTTON
                    int updatedQty = Integer.parseInt( dialogQTY.getText( ).toString( ) );
                    setProductQty(updatedQty, position);
                }).setNegativeButton(R.string.cancel, ((dialogInterface, i) -> {
                    // USER TAPS THE CANCEL BUTTON. IGNORE THIS PART
                }));

        AlertDialog dialog = builder.create(); dialog.show();

        reduce    = dialog.findViewById(R.id.reduceBtn);
        add       = dialog.findViewById(R.id.addBtn);
        dialogQTY = dialog.findViewById(R.id.text_number_qty);
        assert dialogQTY != null;
        dialogQTY.setText(currentQty);

        reduce.setOnClickListener(view -> {
            Log.e(TAG, dialogQTY.getText().toString() );
            int integerQty = Integer.parseInt( dialogQTY.getText().toString( ) );
            if ( integerQty != 0 ){
                integerQty -= 1;
            } dialogQTY.setText( String.valueOf(integerQty) );
        });

        add.setOnClickListener(view ->
                dialogQTY.setText(
                        String.valueOf(
                                Integer.parseInt( dialogQTY.getText( ).toString( ) ) + 1
                        )
                ));
    }

    public void setProductQty(int qtyNum, int position){
        Log.e(TAG, "CHANGED QTY: " + qtyNum);

        if (qtyNum != 0){
            cartList.get(position).setQuantity( qtyNum );
            update_total(); cartAdapter.itemUpdated( position );
        } else {
            if ( cartList.size() == 1){
                cartList.remove( 0 );
                titleList.remove( 0 );
            }else {
                cartList.remove( position );
                titleList.remove(position);
            }
            update_total();
            cartAdapter.itemRemoved( position );
        }
    }

    public void update_total(){
        int total_price = 0;
        int total_qty = 0;
        for (int i = 0; i < titleList.size() ; i++) {
            total_price += Integer.parseInt( cartList.get(i).getPrice( ) )
                    * cartList.get(i).getQuantity();
            total_qty += cartList.get(i).getQuantity();
        }
        totalPriceNum.setText( String.valueOf( total_price ));
        totalQtyNum.setText( String.valueOf( total_qty ) );
    }

    private void callPaymentActivity() {
        if ( !cartList.isEmpty() ){
            Intent i = new Intent(TransactionActivity.this, PaymentActivity.class);
            i.putExtra("cart", (Serializable) cartList);
            i.putExtra("paid", totalPriceNum.getText() );
            i.putExtra("qty", totalQtyNum.getText() );
            startActivity(i);
            clearCart();
        } else Utils.showToast(TransactionActivity.this, "Cart is Empty.");
    }

    private void recyclerViewInits() {
        productLayoutManager = new LinearLayoutManager(TransactionActivity.this);
        cartLayoutManager = new LinearLayoutManager(TransactionActivity.this);
        cartAdapter = new CartAdapter(TransactionActivity.this, cartList, titleList);

        mainRV = findViewById(R.id.product_recyclerView);
        cartRV = findViewById(R.id.cart_recyclerView);
        mainRV.setLayoutManager(productLayoutManager);
        cartRV.setLayoutManager(cartLayoutManager);
        cartRV.setAdapter(cartAdapter );
    }

    private void activityInit() {
        setRequestedOrientation( ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE );
    }
}