package com.iitbhilai.bus;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

public class ViewTicketActivity extends AppCompatActivity {

    List<Passenger> passengerList;
    RecyclerView recyclerView2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_ticket);

        recyclerView2 = (RecyclerView) findViewById(R.id.recyclerView2);
        recyclerView2.setHasFixedSize(true);
        recyclerView2.setLayoutManager(new LinearLayoutManager(this));

        passengerList = new ArrayList<>();

        passengerList.add(
                new Passenger(
                        "11640120","Akash Singh"
                )
        );


        passengerList.add(
                new Passenger(
                        "11640120","Akash Singh"
                )
        );

        passengerList.add(
                new Passenger(
                        "11640120","Akash Singh"
                )
        );

        passengerList.add(
                new Passenger(
                        "11640120","Akash Singh"
                )
        );

        PassengerAdapter adapter = new PassengerAdapter(this,passengerList);
        recyclerView2.setAdapter(adapter);

    }
}
