package com.iitbhilai.bus;

import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.annotation.RequiresApi;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.WriteBatch;
import com.paytm.pgsdk.PaytmOrder;
import com.paytm.pgsdk.PaytmPGService;
import com.paytm.pgsdk.PaytmPaymentTransactionCallback;

import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.UUID;

public class BookActivity extends AppCompatActivity {

    private static final String TAG = "BookActivity";
    public String merchantId, orderId, customerId, channelId, website,
            callbackUrl, taxAmount, industryType, merchantKey, checkSum;

    EditText nameEditText;
    EditText idEditText;
    TextView busIdView;
    Button addPassbtn;

    Passenger passenger;
    List<Passenger> passengers;
    FirebaseFirestore db;

    //Network connectivity
    private NetworkChangeReciever1 mNetworkChangeReciever = null;
    AlertDialog dialog;
    private ProgressDialog mProgress;


    String seatAvailable;
    String seatNumber;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_book);

        //register network broadcast reciever
        mNetworkChangeReciever = new NetworkChangeReciever1();
        mNetworkChangeReciever.setMainActivity(this);
        getApplicationContext().registerReceiver(mNetworkChangeReciever, new IntentFilter("android.net.conn.CONNECTIVITY_CHANGE"));

        nameEditText = (EditText)findViewById(R.id.nameTextView);
        idEditText = (EditText)findViewById(R.id.idTextView);
        addPassbtn = (Button)findViewById(R.id.addPassenger);
        busIdView = findViewById(R.id.busidView);
        busIdView.setText("Bus ID: " + getIntent().getStringExtra("BUS_ID"));

        passengers = new ArrayList<>();
        //initialize db
        db = FirebaseFirestore.getInstance();


        addPassbtn.setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.O)
            @Override
            public void onClick(View view) {
                addPassenger();
            }
        });
        nameEditText.setText("");
        idEditText.setText("");
    }

    private void addToDatabase( final String busID, final String Name, final String userid, final String depTime ) {
        final String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(Calendar.getInstance().getTime());
//
//        //show progress dialog
//        mProgress = new ProgressDialog(this);
//        mProgress.setTitle("Processing...");
//        mProgress.setMessage("Please wait...");
//        mProgress.setCancelable(false);
//        mProgress.setIndeterminate(true);
//        mProgress.show();


        final DocumentReference documentReference = db.collection("Buses").document(getIntent().getStringExtra("BUS_ID"));
        documentReference.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete( @NonNull Task<DocumentSnapshot> task ) {
                if(task.isSuccessful()){
                    DocumentSnapshot document = task.getResult();

                    seatAvailable = document.getData().get("seats_available").toString();
                    String totalSeats = document.getData().get("total_seats").toString();
                    seatNumber = String.valueOf(Integer.parseInt(totalSeats) - Integer.parseInt(seatAvailable) + 1);
                    Map<String, Object> data = new HashMap<>();
                    data.put("seat_number", seatNumber);
                    data.put("bus_id", busID);
                    data.put("dep_time", depTime);
                    data.put("user_name", Name);
                    data.put("book_time", timeStamp);

////                                     Get a new write batch
                    WriteBatch batch = db.batch();
//
                    DocumentReference usersRef = db.collection("Users/" + FirebaseAuth.getInstance().getCurrentUser().getEmail() + "/BookedTickets").document(userid);
                    batch.set(usersRef, data);

                    DocumentReference busRef = db.collection("Buses").document(busID);
                    batch.update(busRef, "seats_available", String.valueOf(Integer.parseInt(seatAvailable) - 1));

                    batch.commit().addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete( @NonNull Task<Void> task ) {
                            if(task.isSuccessful()){
                                Intent i = new Intent(BookActivity.this, ViewTicketActivity.class);
                                startActivity(i);
//                                if(mProgress.isShowing() && mProgress != null){
//                                    mProgress.dismiss();
//                                }
//                                //initiate transaction
//                                startPaymentTransaction();
                            }else{
//                                if(mProgress.isShowing() && mProgress != null){
//                                    mProgress.dismiss();
//                                }
                                Log.d(TAG, "Error getting documents: ", task.getException());

                                Toast.makeText(getApplicationContext(), "some this went wrong!! try again" + task.getException(), Toast.LENGTH_LONG).show();
                            }
                        }
                    });
                }else{
//                    if(mProgress.isShowing() && mProgress != null){
//                        mProgress.dismiss();
//                    }
                    Toast.makeText(getApplicationContext(), "Error getting documents", Toast.LENGTH_SHORT).show();
                    Log.d(TAG, "Error getting documents: ", task.getException());

                }

            }
        })  ;

    }

    @Override
    protected void onStart() {
        super.onStart();
    }

    private void addPassenger(){
        String pass_name = nameEditText.getText().toString().trim();
        String pass_id = idEditText.getText().toString().trim();

        if(!TextUtils.isEmpty(pass_name) && !TextUtils.isEmpty(pass_id) && pass_id.length()==8 && pass_id.charAt(0)=='1' && pass_id.charAt(1)=='1'){
            startPaymentTransaction();
        }

        else {
            Toast.makeText(this,"Please provide Valid Details",Toast.LENGTH_SHORT).show();
        }
    }

    void startPaymentTransaction(){
        //            String key = databasePassengers.push().getKey();

        mProgress = new ProgressDialog(this);
        mProgress.setTitle("Processing...");
        mProgress.setMessage("Please wait...");
        mProgress.setCancelable(false);
        mProgress.setIndeterminate(true);
        mProgress.show();


        merchantId = "sNUXVx29909317083112";
        merchantKey = "AaQBvILsmgNdgb4h";
        customerId = generateStringID(nameEditText.getText().toString());
        orderId = orderId();
        channelId = "WAP";
        website = "DEFAULT";
        taxAmount = "1.00";
        callbackUrl = "https://securegw.paytm.in/theia/paytmCallback?ORDER_ID=" + orderId;
        industryType = "Retail";
        String url = "https://phppayment.herokuapp.com/checksum.php";
        //has network connection
        final HashMap<String, String> paytmParams = new HashMap<String, String>();
        paytmParams.put("MID",merchantId);
        paytmParams.put("ORDER_ID",orderId);
        paytmParams.put("CHANNEL_ID",channelId);
        paytmParams.put("CUST_ID",customerId);
        paytmParams.put("TXN_AMOUNT",taxAmount);
        paytmParams.put("WEBSITE",website);
        paytmParams.put("INDUSTRY_TYPE_ID",industryType);
        paytmParams.put("CALLBACK_URL", callbackUrl);

        JSONObject param = new JSONObject(paytmParams);
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, url, param, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse( JSONObject response ) {
//                            Toast.makeText(getApplicationContext(), String.valueOf(), Toast.LENGTH_LONG).show();
                Log.d("getResponse", String.valueOf(response));
                checkSum = response.optString("CHECKSUMHASH");

                if(checkSum.trim().length() != 0){
                    if(mProgress != null && mProgress.isShowing()){
                        mProgress.dismiss();
                    }
                    Toast.makeText(getApplicationContext(), checkSum, Toast.LENGTH_LONG).show();
                    onStartTransaction();
                }
//                            Toast.makeText(getApplicationContext(), String.valueOf(response), Toast.LENGTH_LONG).show();
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse( VolleyError error ) {
                if(mProgress != null && mProgress.isShowing()){
                    mProgress.dismiss();
                }
                Toast.makeText(getApplicationContext(), "some error has occurred", Toast.LENGTH_LONG).show();
                error.printStackTrace();
            }
        });



        Volley.newRequestQueue(getApplicationContext()).add(jsonObjectRequest);
    }

    public String orderId() {
        Random r = new Random(System.currentTimeMillis());
        return "ORDER" + (1 + r.nextInt(2)) * 10000 + r.nextInt(10000);
    }

    private String generateStringID(String custName) {
        String uuid = UUID.randomUUID().toString() + custName;
        return uuid.replaceAll("-", "");
    }

    public void onStartTransaction() {
        Toast.makeText(getApplicationContext(), "onStartTransaction", Toast.LENGTH_LONG).show();

        PaytmPGService Service = PaytmPGService.getProductionService();
        Map<String, String> paramMap = new HashMap<String, String>();

        // these are mandatory parameters
        paramMap.put("CALLBACK_URL", callbackUrl);
        paramMap.put("CHANNEL_ID",channelId);
        paramMap.put("CHECKSUMHASH",checkSum);
        paramMap.put("CUST_ID",customerId);
        paramMap.put("INDUSTRY_TYPE_ID", industryType);
        paramMap.put("MID", merchantId);
        paramMap.put("ORDER_ID", orderId);
        paramMap.put("TXN_AMOUNT", taxAmount);
        paramMap.put("WEBSITE", website);


        PaytmOrder Order = new PaytmOrder((HashMap<String, String>) paramMap);


        Service.initialize(Order, null);

        Service.startPaymentTransaction(this, true, true,
                new PaytmPaymentTransactionCallback() {
                    @Override
                    public void someUIErrorOccurred(String inErrorMessage) {
                        Toast.makeText(getApplicationContext(), "some ui error occured ", Toast.LENGTH_LONG).show();
//                        deleteFromDatebase(getIntent().getStringExtra("BUS_ID"), idEditText.getText().toString(), seatAvailable);
                        // Some UI Error Occurred in Payment Gateway Activity.
                        // // This may be due to initialization of views in
                        // Payment Gateway Activity or may be due to //
                        // initialization of webview. // Error Message details
                        // the error occurred.
                    }


                    @Override
                    public void onTransactionResponse( final Bundle inResponse) {
                        Log.d("LOG", "Payment Transaction is successful " + inResponse);
                        addToDatabase(getIntent().getStringExtra("BUS_ID"), nameEditText.getText().toString(), idEditText.getText().toString(), getIntent().getStringExtra("BUS_TIME"));

                    }

                    @Override
                    public void networkNotAvailable() { // If network is not
                        // available, then this
                        //                        deleteFromDatebase();
//                        deleteFromDatebase(getIntent().getStringExtra("BUS_ID"), idEditText.getText().toString(), seatAvailable);


                        Toast.makeText(getApplicationContext(), "network not available", Toast.LENGTH_LONG).show();
                        // method gets called.
                    }

                    @Override
                    public void clientAuthenticationFailed(String inErrorMessage) {
                        //                        deleteFromDatebase();
//                        deleteFromDatebase(getIntent().getStringExtra("BUS_ID"), idEditText.getText().toString(), seatAvailable);


                        Toast.makeText(getApplicationContext(), "client authentication failed", Toast.LENGTH_LONG).show();
                        // This method gets called if client authentication
                        // failed. // Failure may be due to following reasons //
                        // 1. Server error or downtime. // 2. Server unable to
                        // generate checksum or checksum response is not in
                        // proper format. // 3. Server failed to authenticate
                        // that client. That is value of payt_STATUS is 2. //
                        // Error Message describes the reason for failure.
                    }

                    @Override
                    public void onErrorLoadingWebPage(int iniErrorCode,
                                                      String inErrorMessage, String inFailingUrl) {
                        //                        deleteFromDatebase();
//                        deleteFromDatebase(getIntent().getStringExtra("BUS_ID"), idEditText.getText().toString(), seatAvailable);


                        Toast.makeText(getApplicationContext(), "error loading webpage", Toast.LENGTH_LONG).show();

                    }

                    // had to be added: NOTE
                    @Override
                    public void onBackPressedCancelTransaction() {
                        //                        deleteFromDatebase();
//                        deleteFromDatebase(getIntent().getStringExtra("BUS_ID"), idEditText.getText().toString(), seatAvailable);


                        Toast.makeText(BookActivity.this,"Back pressed. Transaction cancelled",Toast.LENGTH_LONG).show();
                    }

                    @Override
                    public void onTransactionCancel(String inErrorMessage, Bundle inResponse) {
//                        deleteFromDatebase(getIntent().getStringExtra("BUS_ID"), idEditText.getText().toString(), seatAvailable);

                        //                        deleteFromDatebase();

                        Log.d("LOG", "Payment Transaction Failed " + inErrorMessage);
                        Toast.makeText(getBaseContext(), "Payment Transaction Failed ", Toast.LENGTH_LONG).show();
                    }

                });
    }

    private void deleteFromDatebase( String bus_id, String user_id, String seatAvailable ) {
        //show progress dialog
        mProgress = new ProgressDialog(this);
        mProgress.setTitle("Processing...");
        mProgress.setMessage("Please wait...");
        mProgress.setCancelable(false);
        mProgress.setIndeterminate(true);
        mProgress.show();


        WriteBatch batch = db.batch();
        DocumentReference busIDRef = db.document("Buses/" + bus_id);
        DocumentReference userIdRef = db.document("Users/" + FirebaseAuth.getInstance().getCurrentUser().getEmail() + "/BookedTickets/" + user_id);

        batch.update(busIDRef, "seats_available", seatAvailable);
        batch.delete(userIdRef);
        batch.commit().addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete( @NonNull Task<Void> task ) {
                if(task.isSuccessful()){
                    if (mProgress.isShowing() && mProgress != null){
                        mProgress.dismiss();
                    }
                    Toast.makeText(getApplicationContext(), "Booking error!! Try to book again", Toast.LENGTH_LONG).show();
                }else{
                    if (mProgress.isShowing() && mProgress != null){
                        mProgress.dismiss();
                    }
                    Toast.makeText(getApplicationContext(), "Your ticket is not booked", Toast.LENGTH_LONG).show();
                }

            }
        });

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
    @Override
    protected void onDestroy(){
        try {
            getApplicationContext().unregisterReceiver(mNetworkChangeReciever);
        } catch (IllegalArgumentException e){
            Log.v("Main Activity: ", " unregisterReciever");
        }
        super.onDestroy();
    }
}

class NetworkChangeReciever1 extends BroadcastReceiver {

    private BookActivity bookActivity;

    @Override
    public void onReceive( final Context context, final Intent intent) {

        if(checkInternet(context))
        {
            if (bookActivity.dialog!= null && bookActivity.dialog.isShowing()) {
                bookActivity.dialog.dismiss();
            }
        }
        else{
            bookActivity.showBadNetworkDialog();
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

    public void setMainActivity( BookActivity main){
        bookActivity = main;
    }
}