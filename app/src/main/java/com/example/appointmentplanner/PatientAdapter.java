package com.example.appointmentplanner;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.squareup.picasso.Picasso;

import java.util.ArrayList;

public class PatientAdapter extends RecyclerView.Adapter<PatientAdapter.AdapterViewHolder> {
    private ArrayList<PatientInfo> patients;
    interface PatientListener {
        void onPatientClicked(int position, View view);
    }
    PatientAdapter.PatientListener callback;
    public void setListener(PatientAdapter.PatientListener listener) {
        callback = listener;
    }
    public void setPatientList(ArrayList<PatientInfo> arrayList) {
        patients= arrayList;
    }
    public class AdapterViewHolder extends RecyclerView.ViewHolder{
        ImageView doctorImg;
        TextView doctorName;
        TextView phone;
        TextView availability;
        public AdapterViewHolder(View itemView){
            super(itemView);
            doctorImg = itemView.findViewById(R.id.image);
            doctorName = itemView.findViewById(R.id.doctor_name);
            phone= itemView.findViewById(R.id.type_doctor);
            availability = itemView.findViewById(R.id.available);
            availability.setVisibility(View.GONE);
            Button button = itemView.findViewById(R.id.bookBtn);
            button.setVisibility(View.GONE);
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if(callback!=null){
                        callback.onPatientClicked(getAdapterPosition(),v);

                    }
                }
            });
        }
    }


    @NonNull
    @Override
    public PatientAdapter.AdapterViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.card_view_layout,parent,false);
        return new PatientAdapter.AdapterViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull PatientAdapter.AdapterViewHolder holder, int position) {
        PatientInfo info = patients.get(position);
        holder.doctorName.setText(info.getName());
        //holder.availability.setText(info.getAvailability());
        holder.phone.setText(info.getPhone());
        Picasso.get().load(info.getPath()).into(holder.doctorImg);
    }

    @Override
    public int getItemCount() {
        return patients.size();
    }

    @Override
    public int getItemViewType(int position) {
        return super.getItemViewType(position);
    }
}
