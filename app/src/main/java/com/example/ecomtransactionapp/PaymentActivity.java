package com.example.ecomtransactionapp;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;

import com.example.ecomtransactionapp.database.DBHandler;
import com.example.ecomtransactionapp.recycler_view.Cart;
import com.example.ecomtransactionapp.recycler_view.CartAdapter;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class PaymentActivity extends AppCompatActivity {
    private final String TAG = "PAYMENT_ACTIVITY";

    RecyclerView cartRV;
    LinearLayoutManager cartLayoutManager;
    CartAdapter cartAdapter;

    List<Cart> cartList = new ArrayList<>();

    DBHandler dbHandler;

    TextView paidAmount, totalQty, paymentNumber;
    Button exact_paid_btn, edit_paid_btn, paymentBtn;

    int total_price, receipt_id, product_id;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_payment);

        total_price = Integer.parseInt(getIntent().getStringExtra("paid") );
        dbHandler = new DBHandler(PaymentActivity.this);

        initRecyclerView(); initActivity();
        paymentNumber = findViewById(R.id.payment_number);
        paidAmount = findViewById(R.id.total_payment_to_pay);
        paidAmount.append( String.valueOf( total_price ) );
        exact_paid_btn = findViewById(R.id.exact_paid_btn);
        exact_paid_btn.setOnClickListener(view -> exact_payment());
        totalQty = findViewById(R.id.payment_product_qty);
        totalQty.append( getIntent().getStringExtra("qty") );
        paymentBtn = findViewById(R.id.pay_btn);
        paymentBtn.setOnClickListener(view -> callPaymentDialog());
    }

    private void callPaymentDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder( PaymentActivity.this );
        if (paymentNumber.getText().toString().equals("-")) {
            paymentNumber.setText("0");
        }

        builder.setTitle("Konfirmasi Pembayaran")
                .setMessage("Total Harga    : Rp " + total_price
                        + "\nTotal Dibayar  : Rp " + paymentNumber.getText()
                        + "\nUang Kembalian : Rp " +
                        (total_price - Integer.parseInt(paymentNumber.getText().toString( ) ) )
                )
                .setNegativeButton(R.string.cancel, (dialogInterface, i) -> { /* IGNORE */ })
                .setPositiveButton(R.string.payment_ok, (dialogInterface, i) -> {
                    int pembayaran = Integer.parseInt( paymentNumber.getText().toString() );
                    int kembalian = pembayaran - total_price;
                    receipt_id = dbHandler.addReceipt(
                            total_price,
                            pembayaran,
                            kembalian
                    );
                    for ( Cart item: cartList ) {
                        product_id = dbHandler.getProductId( item.getName() );
                        dbHandler.addReceiptProduct(
                                receipt_id,
                                product_id,
                                item.getQuantity()
                        );
                    } finish();
                });
        AlertDialog dialog = builder.create();

        if ( total_price <= Integer.parseInt( paymentNumber.getText().toString() ) ){
            dialog.show();
        } else Utils.showToast(PaymentActivity.this, "Jumlah yang dibayar masih kosong");
    }

    private void exact_payment() {
        paymentNumber.setText( getIntent().getStringExtra("paid") );
    }

    private void initActivity() {
        setRequestedOrientation( ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE );
    }

    private void initRecyclerView() {
        cartRV = findViewById(R.id.cart_recyclerView);
        cartLayoutManager = new LinearLayoutManager(PaymentActivity.this);

        Intent i = getIntent();
        cartList = (List<Cart>) i.getSerializableExtra("cart");
        cartAdapter = new CartAdapter(PaymentActivity.this, cartList);
        cartRV.setAdapter(cartAdapter); cartRV.setLayoutManager(cartLayoutManager);
    }
}