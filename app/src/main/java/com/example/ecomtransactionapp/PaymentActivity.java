package com.example.ecomtransactionapp;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;

import com.example.ecomtransactionapp.database.DBHandler;
import com.example.ecomtransactionapp.recycler_view.Cart;
import com.example.ecomtransactionapp.recycler_view.CartAdapter;

import java.util.ArrayList;
import java.util.List;

public class PaymentActivity extends AppCompatActivity {
    private final String TAG = "PAYMENT_ACTIVITY";

    RecyclerView cartRV;
    LinearLayoutManager cartLayoutManager;
    CartAdapter cartAdapter;

    List<Cart> cartList = new ArrayList<>();

    DBHandler dbHandler;

    TextView paidAmount, totalQty, paymentNumber;
    Button exact_paid_btn, paymentBtn;

    Button btn1, btn2, btn3, btn4, btn5, btn6, btn7, btn8, btn9,
            btnC, btn0, btn00, btn000;
    ImageButton btnBackspace;
    int total_price, receipt_id, product_id;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_payment);

        total_price = Integer.parseInt(getIntent().getStringExtra("paid") );
        dbHandler = new DBHandler(PaymentActivity.this);

        initRecyclerView(); initActivity(); keyboardInit();
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
                        ( Integer.parseInt(paymentNumber.getText().toString( ) ) - total_price )
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
        } else Utils.showToast(PaymentActivity.this, "Jumlah yang dibayar masih kurang");
    }

    private void exact_payment() {
        paymentNumber.setText( getIntent().getStringExtra("paid") );
    }

    private void editPaymentAmount(String s) {
        if ( s.equals("C") ){
            paymentNumber.setText("");
        } else if ( s.equals("del") ){
            if ( paymentNumber.length() <= 1  ){
                paymentNumber.setText("");
            } else paymentNumber.setText( Utils.chop(
                    String.valueOf(paymentNumber.getText( ) ) ) );
        } else {
            if (paymentNumber.getText().equals("-") ) paymentNumber.setText("");
            paymentNumber.append(s);
        }
    }

    private void keyboardInit() {
        btn0 = findViewById(R.id.btn0);
        btn0.setOnClickListener(view -> editPaymentAmount("0"));
        btn1 = findViewById(R.id.btn1);
        btn1.setOnClickListener(view -> editPaymentAmount("1"));
        btn2 = findViewById(R.id.btn2);
        btn2.setOnClickListener(view -> editPaymentAmount("2"));
        btn3 = findViewById(R.id.btn3);
        btn3.setOnClickListener(view -> editPaymentAmount("3"));
        btn4 = findViewById(R.id.btn4);
        btn4.setOnClickListener(view -> editPaymentAmount("4"));
        btn5 = findViewById(R.id.btn5);
        btn5.setOnClickListener(view -> editPaymentAmount("5"));
        btn6 = findViewById(R.id.btn6);
        btn6.setOnClickListener(view -> editPaymentAmount("6"));
        btn7 = findViewById(R.id.btn7);
        btn7.setOnClickListener(view -> editPaymentAmount("7"));
        btn8 = findViewById(R.id.btn8);
        btn8.setOnClickListener(view -> editPaymentAmount("8"));
        btn9 = findViewById(R.id.btn9);
        btn9.setOnClickListener(view -> editPaymentAmount("9"));
        btn00 = findViewById(R.id.btn00);
        btn00.setOnClickListener(view -> editPaymentAmount("00"));
        btn000 = findViewById(R.id.btn000);
        btn000.setOnClickListener(view -> editPaymentAmount("000"));
        btnC = findViewById(R.id.btnClear);
        btnC.setOnClickListener(view -> editPaymentAmount("C"));
        btnBackspace = findViewById(R.id.btnBackspace);
        btnBackspace.setOnClickListener(view -> editPaymentAmount("del"));
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