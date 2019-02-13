package com.iitbhilai.bus;

import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import java.util.List;

public class BusAdapter extends RecyclerView.Adapter<BusAdapter.BusViewHolder> {
    private Context mCtx;
    private List<BusDetail> busList;
    private Context listener;
    public BusAdapter(Context mCtx, List<BusDetail> busList) {
        this.mCtx = mCtx;
        this.busList = busList;
//        listener =  mCtx;
    }

    @Override
    public BusViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        //inflating and returning our view holder
        LayoutInflater inflater = LayoutInflater.from(mCtx);
        View view = inflater.inflate(R.layout.layout_bus, null);
        return new BusViewHolder(view);
    }

    @Override
    public void onBindViewHolder(BusViewHolder holder, final int position) {
        //getting the product of the specified position
        final BusDetail bus = busList.get(position);

        //binding the data with the viewholder views
        holder.textViewTime.setText("Dep. Time: " + bus.getTime());
        holder.textViewSeats.setText("Seats Avail. : " +  (String.valueOf(bus.getSeats())));
        holder.textViewBusId.setText("Bus Id: " + bus.getBusName());

        holder.bookButton.setOnClickListener(new View.OnClickListener(){

            @Override
            public void onClick(View v) {
                Toast.makeText(mCtx, "Ok", Toast.LENGTH_LONG).show();

                if(bus.getSeats() == 0){
                    Toast.makeText(mCtx, "No Seats available", Toast.LENGTH_LONG).show();
                }else{
                    Intent intent = new Intent(mCtx, BookActivity.class);
                    intent.putExtra("BUS_ID", bus.getBusName());
                    intent.putExtra("BUS_TIME", bus.getTime());
                    intent.putExtra("BUS_SEATS_AVAIL", bus.getSeats());
                    mCtx.startActivity(intent);
                }

            }
        });
    }


    @Override
    public int getItemCount() {
        return busList.size();
    }



    class BusViewHolder extends RecyclerView.ViewHolder {

        TextView textViewTime, textViewSeats, textViewBusId;
        Button bookButton;

        public BusViewHolder(View itemView) {
            super(itemView);
            bookButton = itemView.findViewById(R.id.bookButton);
            textViewTime = itemView.findViewById(R.id.textViewTime);
            textViewSeats = itemView.findViewById(R.id.textViewSeatsAvail);
            textViewBusId = itemView.findViewById(R.id.textViewBusId);
        }

    }
}
