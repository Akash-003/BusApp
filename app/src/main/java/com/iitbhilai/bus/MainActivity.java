package com.iitbhilai.bus;

import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.support.annotation.NonNull;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {
    List<BusDetail> busList;
    RecyclerView recyclerView;
    AlertDialog dialog;
    private static final String TAG = "MainActivity";

    FirebaseFirestore db;
    private ProgressDialog mProgress;


    //Network connectivity
    private NetworkChangeReciever mNetworkChangeReciever = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
//
        //register network broadcast reciever
        mNetworkChangeReciever = new NetworkChangeReciever();
        mNetworkChangeReciever.setMainActivity(this);
        getApplicationContext().registerReceiver(mNetworkChangeReciever, new IntentFilter("android.net.conn.CONNECTIVITY_CHANGE"));

        mProgress = new ProgressDialog(this);
        mProgress.setTitle("Processing...");
        mProgress.setMessage("Please wait...");
        mProgress.setCancelable(false);
        mProgress.setIndeterminate(true);
        mProgress.show();
        recyclerView = (RecyclerView) findViewById(R.id.recyclerView);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));


        db = FirebaseFirestore.getInstance();
        db.collection("Buses").get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete( @NonNull Task<QuerySnapshot> task ) {
                if(task.isSuccessful()){
                    busList = new ArrayList<>();
                    for (DocumentSnapshot document : task.getResult()) {
                        Log.d(TAG, document.getId() + " => " + document.getData());
//                        if(document.get("seats_available").toString() == "0"){
//
//                        }
                        busList.add(new BusDetail(document.getId(), document.getData().get("time").toString(), Integer.parseInt(document.getData().get("seats_available").toString())));
                    }
                    BusAdapter adapter = new BusAdapter(getBaseContext(), busList);
                    recyclerView.setAdapter(adapter);
                    if(mProgress != null && mProgress.isShowing()){
                        mProgress.dismiss();
                    }

                }else{
                    Toast.makeText(getApplicationContext(), "Error getting documents", Toast.LENGTH_SHORT).show();
                    Log.d(TAG, "Error getting documents: ", task.getException());

                    if(mProgress != null && mProgress.isShowing()){
                        mProgress.dismiss();
                    }
                }
            }
        });
    }

//    public void startBookActivity(int index){
//
//        Intent i = new Intent(MainActivity.this, BookActivity.class);
//        startActivity(i);
//
//    }

    @Override
    protected void onStart() {
        super.onStart();

    }
    @Override
    protected void onDestroy(){
        try {
            getApplicationContext().unregisterReceiver(mNetworkChangeReciever);
        } catch (IllegalArgumentException e){
            Log.v("Main Activity: ", " unregisterReciever");
        }
        super.onDestroy();
    }
    public void showBadNetworkDialog(){
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage(R.string.dialog_message).setTitle(R.string.dialog_title);
        builder.setPositiveButton("exit", new DialogInterface.OnClickListener() {
            @Override
            public void onClick( DialogInterface dialog, int which) {
                finish();
            }
        });
        builder.setCancelable(false);
        dialog = builder.create();
        dialog.show();
    }

}

class NetworkChangeReciever extends BroadcastReceiver {

    private MainActivity mainActivity;

    @Override
    public void onReceive( final Context context, final Intent intent) {

        if(checkInternet(context))
        {
            if (mainActivity.dialog!= null && mainActivity.dialog.isShowing()) {
                mainActivity.dialog.dismiss();
            }
        }
        else{
            mainActivity.showBadNetworkDialog();
        }

    }

    boolean checkInternet( Context context) {
        ServiceManager serviceManager = new ServiceManager(context);
        if (serviceManager.isNetworkAvailable()) {
            return true;
        } else {
            return false;
        }
    }

    public void setMainActivity(MainActivity main){
        mainActivity = main;
    }
}
