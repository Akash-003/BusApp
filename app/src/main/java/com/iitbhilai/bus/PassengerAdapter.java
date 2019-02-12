package com.iitbhilai.bus;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import java.util.List;

public class PassengerAdapter extends RecyclerView.Adapter<PassengerAdapter.PassengerViewHolder> {
    private Context mCtx;
    private List<Passenger> passengerList;

    public PassengerAdapter(Context mCtx, List<Passenger> passengerList) {
        this.mCtx = mCtx;
        this.passengerList = passengerList;
    }

    @Override
    public PassengerAdapter.PassengerViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        //inflating and returning our view holder
        LayoutInflater inflater = LayoutInflater.from(mCtx);
        View view = inflater.inflate(R.layout.layout_view_ticket, null);
        return new PassengerAdapter.PassengerViewHolder(view);
    }

    @Override
    public void onBindViewHolder(PassengerAdapter.PassengerViewHolder holder, final int position) {
        //getting the product of the specified position
        Passenger passenger = passengerList.get(position);

        //binding the data with the viewholder views
        holder.idTextView.setText(passenger.getId());
        holder.nameTextView.setText(passenger.getName());

    }

    @Override
    public int getItemCount() {
        return passengerList.size();
    }

    class PassengerViewHolder extends RecyclerView.ViewHolder {

        TextView idTextView, nameTextView;

        public PassengerViewHolder(View itemView) {
            super(itemView);
            idTextView = itemView.findViewById(R.id.idTextView);
            nameTextView = itemView.findViewById(R.id.nameView);
        }

    }

}
