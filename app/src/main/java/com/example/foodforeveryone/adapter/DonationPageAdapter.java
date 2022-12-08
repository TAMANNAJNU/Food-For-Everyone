package com.example.foodforeveryone.adapter;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.DialogInterface;
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
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

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
        holder.tokenTv.setText(donationDataModel.getUserToken());
        holder.collectorName.setText("Collector Name: " + donationDataModel.getCollectorName());
        holder.collectorMobile.setText("Collector Mobile: " + donationDataModel.getCollectorPhone());
        //holder.collectorAddress.setText("Collector Address: " + donationDataModel.getCollectorAddress());

        if (donationDataModel.getStatus().equals("Approved")){
            holder.collectLayout.setVisibility(View.VISIBLE);
            holder.approveRejectLayout.setVisibility(View.GONE);
            holder.tokenLayout.setVisibility(View.GONE);
            holder.collectorDetailLayout.setVisibility(View.GONE);
        } else if (donationDataModel.getStatus().equals("Pending")){
            holder.approveRejectLayout.setVisibility(View.VISIBLE);
            holder.collectLayout.setVisibility(View.GONE);
            holder.tokenLayout.setVisibility(View.GONE);
            holder.collectorDetailLayout.setVisibility(View.GONE);
        } else if (donationDataModel.getStatus().equals("Requested")){
            holder.tokenLayout.setVisibility(View.VISIBLE);
            holder.collectorDetailLayout.setVisibility(View.VISIBLE);
            holder.collectLayout.setVisibility(View.GONE);
            holder.approveRejectLayout.setVisibility(View.GONE);
        }

        holder.rejectBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                AlertDialog alertDialog = new AlertDialog.Builder(context)
                        .setIcon(android.R.drawable.ic_dialog_alert)
                        .setTitle("Confirmation")
                        .setMessage("Do you really want to delete?")
                        .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                ProgressDialog progressdialog = new ProgressDialog(context);
                                progressdialog.setCancelable(false);
                                progressdialog.setMessage("Please Wait....");
                                dialogInterface.dismiss();
                                progressdialog.show();

                                FirebaseDatabase database = FirebaseDatabase.getInstance();
                                DatabaseReference reference = database.getReference();
                                reference.child("DonationInfo")
                                        .child(donationDataModel.getUserID())
                                        .child(donationDataModel.getPushKey())
                                        .removeValue();

                                progressdialog.dismiss();
                                Toast.makeText(context, "Deleted Successfully", Toast.LENGTH_LONG).show();
                            }
                        })
                        .setNegativeButton("No", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                dialogInterface.dismiss();
                            }
                        })
                        .show();
            }
        });

        holder.collectFoodBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                AlertDialog alertDialog = new AlertDialog.Builder(context)
                        .setIcon(android.R.drawable.ic_dialog_alert)
                        .setTitle("Confirmation")
                        .setMessage("Do you really want to collect?")
                        .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                ProgressDialog progressdialog = new ProgressDialog(context);
                                progressdialog.setCancelable(false);
                                progressdialog.setMessage("Please Wait....");
                                dialogInterface.dismiss();
                                progressdialog.show();

                                FirebaseDatabase database = FirebaseDatabase.getInstance();
                                DatabaseReference reference = database.getReference();

                                DocumentReference documentReference = FirebaseFirestore.getInstance().collection("users").document(FirebaseAuth.getInstance().getCurrentUser().getUid());
                                documentReference.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                                    @Override
                                    public void onSuccess(DocumentSnapshot documentSnapshot) {
                                        if (documentSnapshot.exists()){

                                            DonationDataModel model = new DonationDataModel(
                                                    donationDataModel.getDonationName(),
                                                    donationDataModel.getDonationMobileNum(),
                                                    donationDataModel.getDonationAddress(),
                                                    donationDataModel.getDonationFoodDescription(),
                                                    donationDataModel.getPushKey(),
                                                    donationDataModel.getUserID(),
                                                    donationDataModel.getUserName(),
                                                    donationDataModel.getUserMobileNo(),
                                                    donationDataModel.getUserEmail(),
                                                    "Requested",
                                                    donationDataModel.getUserToken(),
                                                    documentSnapshot.getString("name"),
                                                    documentSnapshot.getString("mobileNumber"),
                                                    ""
                                            );

                                            reference.child("DonationInfo")
                                                    .child(donationDataModel.getUserID())
                                                    .child(donationDataModel.getPushKey())
                                                    .setValue(model);
                                        }
                                    }
                                });
                                progressdialog.dismiss();
                                Toast.makeText(context, "Collected Successfully", Toast.LENGTH_LONG).show();
                            }
                        })
                        .setNegativeButton("No", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                dialogInterface.dismiss();
                            }
                        })
                        .show();
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

                AlertDialog alertDialog = new AlertDialog.Builder(context)
                        .setIcon(android.R.drawable.ic_dialog_alert)
                        .setTitle("Confirmation")
                        .setMessage("Do you really want to approve?")
                        .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                ProgressDialog progressdialog = new ProgressDialog(context);
                                progressdialog.setCancelable(false);
                                progressdialog.setMessage("Please Wait....");
                                dialogInterface.dismiss();
                                progressdialog.show();

                                FirebaseDatabase database = FirebaseDatabase.getInstance();
                                DatabaseReference reference = database.getReference();
                                reference.child("DonationInfo").child(donationDataModel.getUserID()).child(donationDataModel.getPushKey())
                                        .child("status").setValue("Approved");

                                progressdialog.dismiss();
                                Toast.makeText(context, "Approved Successfully", Toast.LENGTH_LONG).show();
                            }
                        })
                        .setNegativeButton("No", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                dialogInterface.dismiss();
                            }
                        })
                        .show();
            }
        });

        holder.requestRejectBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                AlertDialog alertDialog = new AlertDialog.Builder(context)
                        .setIcon(android.R.drawable.ic_dialog_alert)
                        .setTitle("Confirmation")
                        .setMessage("Do you really want to reject?")
                        .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                ProgressDialog progressdialog = new ProgressDialog(context);
                                progressdialog.setCancelable(false);
                                progressdialog.setMessage("Please Wait....");
                                dialogInterface.dismiss();
                                progressdialog.show();

                                FirebaseDatabase database = FirebaseDatabase.getInstance();
                                DatabaseReference reference = database.getReference();
                                reference.child("DonationInfo").child(donationDataModel.getUserID()).child(donationDataModel.getPushKey())
                                        .child("status").setValue("Approved");

                                progressdialog.dismiss();
                                Toast.makeText(context, "Rejected Successfully", Toast.LENGTH_LONG).show();
                            }
                        })
                        .setNegativeButton("No", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                dialogInterface.dismiss();
                            }
                        })
                        .show();
            }
        });

        holder.tokenTv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                setClipboard(context, donationDataModel.getUserToken());
            }
        });
    }

    @Override
    public int getItemCount() {
        return donationDataModelList.size();
    }

    public class MyDonationViewHolder extends RecyclerView.ViewHolder {
        private TextView nameDonate, mobileDonate, addressDonate, detailsDonate, tokenTv, collectorName, collectorMobile, collectorAddress;
        private Button collectFoodBtn, approvedBtn, requestRejectBtn, rejectBtn;
        private ImageView call;
        private RelativeLayout collectLayout;
        private LinearLayout tokenLayout, collectorDetailLayout, approveRejectLayout;
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
            requestRejectBtn = itemView.findViewById(R.id.requestRejectId);
            tokenLayout = itemView.findViewById(R.id.tokenLayoutId);
            tokenTv = itemView.findViewById(R.id.tokenTVId);
            collectorName = itemView.findViewById(R.id.nameCollectorId);
            collectorMobile = itemView.findViewById(R.id.mobileCollectorId);
            collectorAddress = itemView.findViewById(R.id.addressCollectorId);
            collectorDetailLayout = itemView.findViewById(R.id.collectorDetailsLayout);
            approveRejectLayout = itemView.findViewById(R.id.approveRejectLayout);
            rejectBtn = itemView.findViewById(R.id.rejectId);
        }
    }

    private void setClipboard(Context context, String text) {
        if(android.os.Build.VERSION.SDK_INT < android.os.Build.VERSION_CODES.HONEYCOMB) {
            android.text.ClipboardManager clipboard = (android.text.ClipboardManager) context.getSystemService(Context.CLIPBOARD_SERVICE);
            clipboard.setText(text);
        } else {
            android.content.ClipboardManager clipboard = (android.content.ClipboardManager) context.getSystemService(Context.CLIPBOARD_SERVICE);
            android.content.ClipData clip = android.content.ClipData.newPlainText("Copied Text", text);
            clipboard.setPrimaryClip(clip);
        }

        Toast.makeText(context, "Copied Token", Toast.LENGTH_SHORT).show();
    }

    /*private void showProgressDialog(Context activityContext){
        ProgressDialog progressdialog = new ProgressDialog(activityContext);
        progressdialog.setCancelable(false);
        progressdialog.setMessage("Please Wait....");
        progressdialog.show();
    }*/

}
