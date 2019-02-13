package com.iitbhilai.bus;

import android.app.ProgressDialog;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.firestore.WriteBatch;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ViewTicketActivity extends AppCompatActivity {

    List<Passenger> passengerList;
    RecyclerView recyclerView2;
    FirebaseFirestore db;
    private ProgressDialog mProgress;
    private static final String TAG  ="ViewTicketActivity";





    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //show progress bar
        mProgress = new ProgressDialog(this);
        mProgress.setTitle("Processing...");
        mProgress.setMessage("Please wait...");
        mProgress.setCancelable(false);
        mProgress.setIndeterminate(true);
        mProgress.show();

        setContentView(R.layout.activity_view_ticket);

        //initialize db
        db = FirebaseFirestore.getInstance();

        recyclerView2 = (RecyclerView) findViewById(R.id.recyclerView2);
        recyclerView2.setHasFixedSize(true);
        recyclerView2.setLayoutManager(new LinearLayoutManager(this));

        //get data from database
        db.collection("Users/" + FirebaseAuth.getInstance().getCurrentUser().getEmail() + "/BookedTickets").get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete( @NonNull Task<QuerySnapshot> task ) {
                if(task.isSuccessful()){
                    passengerList = new ArrayList<>();
                    for (DocumentSnapshot documentSnapshot : task.getResult()){
                        Log.d(TAG, documentSnapshot.toString());
                        passengerList.add(new Passenger(documentSnapshot.getId().toString(), documentSnapshot.getData().get("user_name").toString(), documentSnapshot.getData().get("dep_time").toString(), documentSnapshot.getData().get("seat_number").toString(), documentSnapshot.getData().get("book_time").toString(), documentSnapshot.getData().get("bus_id").toString()));
                    }
//
                    PassengerAdapter adapter = new PassengerAdapter(getApplicationContext(),passengerList);
                    recyclerView2.setAdapter(adapter);

                    if(mProgress.isShowing() && mProgress != null){
                        mProgress.dismiss();
                    }

                }else{
                    Toast.makeText(getApplicationContext(), "Some thing went wrong", Toast.LENGTH_LONG).show();

                }
            }
        });


//



    }
}
