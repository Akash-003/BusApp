package com.iitbhilai.bus;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.TextView;

import org.w3c.dom.Text;

public class ViewTicketActivity extends AppCompatActivity {

    @Override
    protected void onCreate( Bundle savedInstanceState ) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_ticket);
        TextView UserName = findViewById(R.id.nameView);
        TextView UserId = findViewById(R.id.idTextView);
        TextView DepartureTime = findViewById(R.id.timeView);
        TextView SeatNumber = findViewById(R.id.seatView);
        TextView txnId = findViewById(R.id.txnId);
        TextView txnDate = findViewById(R.id.txnDate);

        UserName.setText(getIntent().getStringExtra("USER_NAME"));
        UserId.setText(getIntent().getStringExtra("USER_ID"));
        DepartureTime.setText(getIntent().getStringExtra("DEPARTURE_TIME"));
        SeatNumber.setText(getIntent().getStringExtra("SEAT_NUM"));
        txnId.setText(getIntent().getStringExtra("TXNID"));
        txnDate.setText(getIntent().getStringExtra("TXNDATE"));

    }
}
