package com.example.appointmentplanner;

import android.net.Uri;
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

public class Adapter extends RecyclerView.Adapter<Adapter.AdapterViewHolder> {
    private ArrayList<DoctorInfo> doctors;
    interface DoctorListener{
        void onDoctorClicked(int position, View view);
        void onBookBtnClicked(int position, View view);
    }
    DoctorListener callback;
    public void setListener(DoctorListener listener) {
        callback = listener;
    }
    public void setDoctorList(ArrayList<DoctorInfo> arrayList) {
        doctors= arrayList;
    }
    public class AdapterViewHolder extends RecyclerView.ViewHolder{
        ImageView doctorImg;
        TextView doctorName;
        TextView proffesioncy;
        Button Book;
        TextView availability;
        public AdapterViewHolder(final View itemView){
            super(itemView);
            doctorImg = itemView.findViewById(R.id.image);
            doctorName = itemView.findViewById(R.id.doctor_name);
            proffesioncy= itemView.findViewById(R.id.type_doctor);
            availability = itemView.findViewById(R.id.available);
            Book = itemView.findViewById(R.id.bookBtn);
            Book.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if(callback!=null) {
                        callback.onBookBtnClicked(getAdapterPosition(), v);
                    }
                }
            });
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if(callback!=null){
                        callback.onDoctorClicked(getAdapterPosition(),v);

                    }
                }
            });
        }
    }


    @NonNull
    @Override
    public Adapter.AdapterViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.card_view_layout,parent,false);
        return new AdapterViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull Adapter.AdapterViewHolder holder, int position) {
        DoctorInfo info = doctors.get(position);
        holder.doctorName.setText(info.getName());
        //holder.availability.setText(info.getAvailability());
        if(info.isAvailability())
            holder.availability.setText("available");
        else
            holder.availability.setText("Not available");

        holder.proffesioncy.setText(info.getProficiency());
        Picasso.get().load(info.getImagePath()).into(holder.doctorImg);
    }

    @Override
    public int getItemCount() {
        return doctors.size();
    }

    @Override
    public int getItemViewType(int position) {
        return super.getItemViewType(position);
    }
}
