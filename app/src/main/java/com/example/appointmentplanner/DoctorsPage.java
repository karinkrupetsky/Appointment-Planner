package com.example.appointmentplanner;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.DialogInterface;
import android.content.Intent;
import android.media.Image;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Set;

public class DoctorsPage extends AppCompatActivity {
    private ArrayList<PatientInfo>infoArrayList;
    private DoctorsQueueAdapter doctorsQueueAdapter;
    private ListView doctorList;
    private boolean deleted = false;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.doctors_page_layout);
        doctorList = findViewById(R.id.doctors_list);
        TextView DocName = findViewById(R.id.name);
        TextView Docpro = findViewById(R.id.proficiency);
        //TextView DocAvailability = findViewById(R.id.available);
        doctorsQueueAdapter = new DoctorsQueueAdapter();
        doctorsQueueAdapter.setPatientList(new ArrayList<PatientInfo>());
        doctorList.setAdapter(doctorsQueueAdapter);
        infoArrayList = new ArrayList<>();
        DocName.setText(getIntent().getStringExtra("name"));
        Docpro.setText(getIntent().getStringExtra("proficiency"));
        //DocAvailability.setText(getIntent().getStringExtra("availability"));

        DatabaseReference patientQueueReference = FirebaseDatabase.getInstance().getReference("users/Doctor/" + getIntent().getStringExtra(DoctorInfo.Uid)+"/DoctorInfo/queue");
        patientQueueReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                infoArrayList.clear();
                HashMap<String,Object>queue = (HashMap<String, Object>) snapshot.getValue();
                if(queue!=null) {
                    Set<String> keySet = queue.keySet();
                    Iterator<String>iterator = keySet.iterator();
                    while (iterator.hasNext()){
                        String key = iterator.next();
                        HashMap<String,Object>patientMap = (HashMap<String, Object>) queue.get(key);
                        if(patientMap!=null) {
                            PatientInfo patientInfo = new PatientInfo();
                            patientInfo.setUid((String) patientMap.get(Patient.UID));
                            patientInfo.setPhone((String) patientMap.get(Patient.PHONE));
                            patientInfo.setPath((String) patientMap.get(Patient.PATH));
                            patientInfo.setAge((String) patientMap.get(Patient.AGE));
                            patientInfo.setName((String) patientMap.get(Patient.NAME));
                            infoArrayList.add(patientInfo);
                        }
                    }
                    doctorsQueueAdapter.setPatientList(infoArrayList);
                    doctorsQueueAdapter.notifyDataSetChanged();
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        final Button CancelAppointmentBtn = findViewById(R.id.CancelAppointment_Btn);
        CancelAppointmentBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder builder = new AlertDialog.Builder(DoctorsPage.this);
                builder.setCancelable(true)
                        .setTitle("Confirm Action")
                        .setMessage("Are you sure you want to cancel the appointment?")
                        .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                deleted = false;
                                DatabaseReference reference = FirebaseDatabase.getInstance().getReference("users/Doctor/" + getIntent().getStringExtra("uid") + "/DoctorInfo/queue");
                                reference.addValueEventListener(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                                        HashMap<String,Object>hashMap = (HashMap<String, Object>) snapshot.getValue();
                                        Set<String>KeySet = hashMap.keySet();
                                        Iterator<String>iterator = KeySet.iterator();
                                        String key;
                                        if(iterator.hasNext())
                                            key = iterator.next();
                                        while (iterator.hasNext()) {
                                            key = iterator.next();
                                            HashMap<String, Object> patient = (HashMap<String, Object>) hashMap.get(key);
                                            if (patient != null) {
                                                if (patient.get(Patient.UID).equals(FirebaseAuth.getInstance().getCurrentUser().getUid())) {
                                                    if(!deleted) {
                                                        Toast.makeText(DoctorsPage.this, "You were removed from the queue", Toast.LENGTH_SHORT).show();
                                                        DatabaseReference queueReference = FirebaseDatabase.getInstance().getReference("users/Doctor/" + getIntent().getStringExtra("uid") + "/DoctorInfo/queue/" + key);
                                                        queueReference.removeValue();
                                                        deleted = true;
                                                        break;
                                                    }
                                                }
                                            }
                                        }
                                    }

                                    @Override
                                    public void onCancelled(@NonNull DatabaseError error) {
                                    }
                                });
                                CancelAppointmentBtn.setClickable(false);
                            }
                        }).setNegativeButton("No", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                    }
                }).create();
                builder.show();
            }
        });
    }
}