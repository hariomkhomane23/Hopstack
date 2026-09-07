package com.example.hopstack;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import com.razorpay.Checkout;
import com.razorpay.PaymentResultListener;
import org.json.JSONObject;

public class Payment extends AppCompatActivity implements PaymentResultListener {

    private EditText editAmount;
    private RadioGroup radioGroupFees;
    private String selectedFeeType;
    private SharedPreferences sharedPreferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_payment);

        // Initialize UI Elements
        editAmount = findViewById(R.id.editAmount);
        radioGroupFees = findViewById(R.id.radioGroupFees);
        Button btnPay = findViewById(R.id.btnPay);

        // Initialize SharedPreferences
        sharedPreferences = getSharedPreferences("PaymentPrefs", MODE_PRIVATE);

        // Handle Fee Selection
        radioGroupFees.setOnCheckedChangeListener((group, checkedId) -> {
            if (checkedId == R.id.radioHostel) {
                selectedFeeType = "Hostel Fees";
            } else if (checkedId == R.id.radioMess) {
                selectedFeeType = "Mess Fees";
            }
        });

        // Pay Button Click
        btnPay.setOnClickListener(v -> {
            if (selectedFeeType == null) {
                Toast.makeText(this, "Please select a fee type", Toast.LENGTH_SHORT).show();
                return;
            }

            // Check if payment already done
            if (sharedPreferences.contains(selectedFeeType)) {
                Toast.makeText(this, "Payment is already done!", Toast.LENGTH_LONG).show();
                showPreviousReceipt();
            } else {
                startPayment();
            }
        });

        // Preload Razorpay
        Checkout.preload(getApplicationContext());
    }

    private void startPayment() {
        Checkout checkout = new Checkout();
        checkout.setKeyID("rzp_test_qpfEXLBNmbKMOA"); // Replace with your Razorpay Key ID

        try {
            int amount = Integer.parseInt(editAmount.getText().toString()) * 100; // Convert to paise

            JSONObject options = new JSONObject();
            options.put("name", "HopStack");
            options.put("description", selectedFeeType);
            options.put("currency", "INR");
            options.put("amount", amount);
            checkout.open(this, options);
        } catch (Exception e) {
            Toast.makeText(this, "Error: " + e.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onPaymentSuccess(String razorpayPaymentID) {
        String amountPaid = editAmount.getText().toString();

        // Save payment status in SharedPreferences
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(selectedFeeType, razorpayPaymentID);
        editor.putString(selectedFeeType + "_amount", amountPaid);
        editor.apply();

        // Show Receipt
        showReceipt(razorpayPaymentID, amountPaid);
    }

    @Override
    public void onPaymentError(int code, String response) {
        Toast.makeText(this, "Payment Failed: " + response, Toast.LENGTH_LONG).show();
    }

    private void showReceipt(String paymentId, String amount) {
        Intent intent = new Intent(Payment.this, ReceiptActivity.class);
        intent.putExtra("PAYMENT_ID", paymentId);
        intent.putExtra("AMOUNT", amount);
        intent.putExtra("FEE_TYPE", selectedFeeType);
        startActivity(intent);
        finish(); // Close Payment Activity
    }

    private void showPreviousReceipt() {
        String paymentId = sharedPreferences.getString(selectedFeeType, "");
        String amountPaid = sharedPreferences.getString(selectedFeeType + "_amount", "");

        Intent intent = new Intent(Payment.this, ReceiptActivity.class);
        intent.putExtra("PAYMENT_ID", paymentId);
        intent.putExtra("AMOUNT", amountPaid);
        intent.putExtra("FEE_TYPE", selectedFeeType);
        startActivity(intent);
        finish(); // Close Payment Activity
    }
}