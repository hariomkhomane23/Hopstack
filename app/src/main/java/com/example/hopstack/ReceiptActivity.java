package com.example.hopstack;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;

public class ReceiptActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_receipt);

        // Get payment details
        String paymentId = getIntent().getStringExtra("PAYMENT_ID");
        String amount = getIntent().getStringExtra("AMOUNT");
        String feeType = getIntent().getStringExtra("FEE_TYPE");

        // Set payment details in UI
        TextView txtPaymentId = findViewById(R.id.txtPaymentId);
        TextView txtAmount = findViewById(R.id.txtAmount);
        TextView txtFeeType = findViewById(R.id.txtFeeType);
        Button btnDone = findViewById(R.id.btnDone);

        txtPaymentId.setText("Transaction ID: " + paymentId);
        txtAmount.setText("Amount Paid: ₹" + amount);
        txtFeeType.setText("Payment For: " + feeType);

        // Done button click: Go back to home screen
        btnDone.setOnClickListener(v -> {
            Intent intent = new Intent(ReceiptActivity.this, MainActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
            finish();
        });
    }
}