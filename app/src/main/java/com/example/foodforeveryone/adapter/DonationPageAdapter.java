package com.example.foodforeveryone.adapter;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.foodforeveryone.R;
import com.example.foodforeveryone.model.DonationDataModel;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.List;

public class DonationPageAdapter extends RecyclerView.Adapter<DonationPageAdapter.MyDonationViewHolder> {
    Context context;
    List<DonationDataModel> donationDataModelList;

    public DonationPageAdapter(Context context, List<DonationDataModel> donationDataModelList) {
        this.context = context;
        this.donationDataModelList = donationDataModelList;
    }

    @NonNull
    @Override
    public MyDonationViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.donate_food_item, parent, false);
        return new MyDonationViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MyDonationViewHolder holder, int position) {
        DonationDataModel donationDataModel = donationDataModelList.get(position);
        holder.nameDonate.setText("Name: " + donationDataModel.getDonationName());
        holder.mobileDonate.setText("Mobile: " + donationDataModel.getDonationMobileNum());
        holder.addressDonate.setText("Address: " + donationDataModel.getDonationAddress());
        holder.detailsDonate.setText("Details: " + donationDataModel.getDonationFoodDescription());

        if (donationDataModel.getStatus().equals("Approved")){
            holder.collectLayout.setVisibility(View.VISIBLE);
            holder.approvedBtn.setVisibility(View.GONE);
            holder.tokenLayout.setVisibility(View.GONE);
        } else if (donationDataModel.getStatus().equals("Pending")){
            holder.approvedBtn.setVisibility(View.VISIBLE);
            holder.collectLayout.setVisibility(View.GONE);
            holder.tokenLayout.setVisibility(View.GONE);
        } else if (donationDataModel.getStatus().equals("Requested")){
            holder.tokenLayout.setVisibility(View.VISIBLE);
            holder.collectLayout.setVisibility(View.GONE);
            holder.approvedBtn.setVisibility(View.GONE);
        }

        holder.collectFoodBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                FirebaseDatabase database = FirebaseDatabase.getInstance();
                DatabaseReference reference = database.getReference();
                reference.child("DonationInfo").child(donationDataModel.getUserID()).child(donationDataModel.getPushKey())
                        .child("status").setValue("Requested");
            }
        });

        holder.call.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(Intent.ACTION_DIAL);

                intent.setData(Uri.parse("tel:" + donationDataModel.getDonationMobileNum()));
                context.startActivity(intent);
            }
        });

        holder.approvedBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                FirebaseDatabase database = FirebaseDatabase.getInstance();
                DatabaseReference reference = database.getReference();
                reference.child("DonationInfo").child(donationDataModel.getUserID()).child(donationDataModel.getPushKey())
                        .child("status").setValue("Approved");
            }
        });

        holder.requestedBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                /*FirebaseDatabase database = FirebaseDatabase.getInstance();
                DatabaseReference reference = database.getReference();
                reference.child("")*/
            }
        });
    }

    @Override
    public int getItemCount() {
        return donationDataModelList.size();
    }

    public class MyDonationViewHolder extends RecyclerView.ViewHolder {
        private TextView nameDonate, mobileDonate, addressDonate, detailsDonate;
        private Button collectFoodBtn, approvedBtn, requestedBtn;
        private ImageView call;
        private RelativeLayout collectLayout;
        private LinearLayout tokenLayout;
        public MyDonationViewHolder(@NonNull View itemView) {
            super(itemView);
            nameDonate = itemView.findViewById(R.id.nameDonateId);
            mobileDonate = itemView.findViewById(R.id.mobileDonateId);
            addressDonate = itemView.findViewById(R.id.addressDonateId);
            detailsDonate = itemView.findViewById(R.id.detailsDonateId);
            collectFoodBtn = itemView.findViewById(R.id.collectFoodId);
            call = itemView.findViewById(R.id.callButtonId);
            approvedBtn = itemView.findViewById(R.id.approvedId);
            collectLayout = itemView.findViewById(R.id.collectLayoutId);
            requestedBtn = itemView.findViewById(R.id.requestedId);
            tokenLayout = itemView.findViewById(R.id.tokenLayoutId);
        }
    }
}
