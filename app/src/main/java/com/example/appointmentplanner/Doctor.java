package com.example.appointmentplanner;

import android.content.DialogInterface;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Set;


public class Doctor extends AppCompatActivity implements DoctorCredentialsFragment.DoctorCredentialsFragmentInterface {
    ArrayList<PatientInfo> arrayList = new ArrayList<>();
    private DoctorInfo doctorInfo;
    private PatientAdapter adapter= new PatientAdapter();
    public final String Proficiency = "proficiency";
    public final String Queue = "queue";
    public final String Name ="name";
    private boolean removed = false;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.doctor_layout);

        doctorInfo = new DoctorInfo();

        // creating recycler view to see the patient list as a doctor
        RecyclerView recyclerView = findViewById(R.id.recycle_view);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setItemViewCacheSize(20);
        recyclerView.setDrawingCacheEnabled(true);
        recyclerView.setDrawingCacheQuality(View.DRAWING_CACHE_QUALITY_HIGH);

        if(getIntent().hasExtra("Sign Up")) {
            DoctorCredentialsFragment doctorCradentialsFragment = new DoctorCredentialsFragment();
            doctorCradentialsFragment.show(getSupportFragmentManager(),"tag");
        }
        downloadMe();

        //when doctor press on patient he leaves the queue , means he finish with him
        adapter.setListener(new PatientAdapter.PatientListener() {
            //in that case i delete him from the queue
            @Override
            public void onPatientClicked(final int position, View view) {
                if(position == 0) {
                    removed = false;
                    AlertDialog.Builder builder = new AlertDialog.Builder(Doctor.this);
                    builder.setCancelable(true)
                            .setTitle("Confirm Action")
                            .setMessage("Is the appointment over?")
                            .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    DatabaseReference removeFromQueueReference = FirebaseDatabase.getInstance().getReference("users/Doctor/" + FirebaseAuth.getInstance().getCurrentUser().getUid() + "/DoctorInfo/queue");
                                    removeFromQueueReference.addValueEventListener(new ValueEventListener() {
                                        @Override
                                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                                            if(!removed) {
                                                HashMap<String, Object> queue = (HashMap<String, Object>) snapshot.getValue();
                                                Set<String> toRemove = queue.keySet();
                                                Iterator<String> iterator = toRemove.iterator();
                                                String key = iterator.next();
                                                DatabaseReference removeFirstReference = FirebaseDatabase.getInstance().getReference("users/Doctor/" + FirebaseAuth.getInstance().getCurrentUser().getUid() + "/DoctorInfo/queue/" + key);
                                                removeFirstReference.removeValue();
                                                removed=true;
                                            }
                                        }

                                        @Override
                                        public void onCancelled(@NonNull DatabaseError error) {

                                        }
                                    });
                                }
                            }).setNegativeButton("No", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {

                        }
                    }).create();
                    builder.show();
                }
                else {
                    Toast.makeText(Doctor.this, "You cant remove a patient you didn't see yet", Toast.LENGTH_SHORT).show();
                }
            }
        });

        adapter.setPatientList(arrayList);
        recyclerView.setAdapter(adapter);

    }

    @Override
    public void clickOk(String name, String proficiency,String address) {
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("users/Doctor/" + FirebaseAuth.getInstance().getCurrentUser().getUid());
        HashMap<String,Object> hashMap = new HashMap<>();
        DoctorInfo info = new DoctorInfo();
        info.setAvailability(true);
        info.setName(name);
        info.setProficiency(proficiency);
        info.setImagePath(address);
        info.setUid(FirebaseAuth.getInstance().getCurrentUser().getUid());
        info.setQueue(new ArrayList<PatientInfo>());
        hashMap.put("DoctorInfo",info);
        reference.setValue(hashMap);
    }

    private void downloadMe()
    {
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("users/Doctor/" + FirebaseAuth.getInstance().getCurrentUser().getUid() + "/DoctorInfo");
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                HashMap<String,Object>drInfoHash = (HashMap<String, Object>) snapshot.getValue();
                DoctorInfo info = new DoctorInfo();
                if(drInfoHash!=null) {
                    info.setProficiency((String) drInfoHash.get(DoctorInfo.Proficiency));
                    info.setName((String)drInfoHash.get(DoctorInfo.Name));
                    info.setImagePath((String)drInfoHash.get(DoctorInfo.Path));
                    info.setAvailability((boolean)drInfoHash.get(DoctorInfo.Availability));
                    if(drInfoHash.containsKey(DoctorInfo.Queue))
                    {
                        HashMap<String,Object>queue = (HashMap<String, Object>) drInfoHash.get(DoctorInfo.Queue);
                        arrayList.clear();
                        if(queue!=null) {
                            Set<String> keySet = queue.keySet();
                            Iterator<String>iterator = keySet.iterator();
                            while (iterator.hasNext()) {
                                String key = iterator.next();
                                PatientInfo patientInfo = new PatientInfo();
                                HashMap<String, Object> patient = (HashMap<String, Object>) queue.get(key);
                                if (patient != null) {
                                    patientInfo.setAge((String) patient.get(Patient.AGE));
                                    patientInfo.setPath((String) patient.get(Patient.PATH));
                                    patientInfo.setPhone((String) patient.get(Patient.PHONE));
                                    patientInfo.setName((String) patient.get(Patient.NAME));
                                    patientInfo.setUid((String) patient.get(Patient.UID));
                                    arrayList.add(patientInfo);
                                }
                            }
                            adapter.setPatientList(arrayList);
                            adapter.notifyDataSetChanged();
                        }

                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }
}

