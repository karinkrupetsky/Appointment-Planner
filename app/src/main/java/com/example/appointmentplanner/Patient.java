package com.example.appointmentplanner;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
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


public class Patient extends AppCompatActivity implements FilterFragment.filterInterface,PatientCradentialsFragment.PatientCradentialsFragmentInterface {
    public static final String NAME = "name";
    public static String AGE = "age";
    public static String PHONE = "phone";
    public static String UID = "uid";
    public static String PATH = "path";
    private boolean available = true;
    private Adapter adapter = new Adapter();
    private ArrayList<DoctorInfo> doctorInfoArrayList = new ArrayList<>();
    private PatientInfo patientInfo;
    private Intent serviceIntent;



    //menu bar
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.filter_menu,menu);
        return super.onCreateOptionsMenu(menu);
    }
    // filter
    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if(item.getItemId() == R.id.filter) {
            FilterFragment filterFragment = new FilterFragment();
            filterFragment.show(getSupportFragmentManager(),"filter");

        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onCreate(@Nullable final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.patient_layout);
        RecyclerView recyclerView = findViewById(R.id.recycle_view);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setItemViewCacheSize(20);
        recyclerView.setDrawingCacheEnabled(true);
        recyclerView.setDrawingCacheQuality(View.DRAWING_CACHE_QUALITY_HIGH);


        serviceIntent = new Intent(this,CountToAppointmentService.class);
        startService(serviceIntent);


        if(getIntent().hasExtra("Sign Up")) {
            PatientCradentialsFragment patientCradentialsFragment = new PatientCradentialsFragment();
            patientCradentialsFragment.show(getSupportFragmentManager(),"tag");

        }
        else if(getIntent().hasExtra("Sign In")) {
            //currentPatient();
        }


        adapter.setDoctorList(doctorInfoArrayList);
        adapter.setListener(new Adapter.DoctorListener() {
            @Override
            public void onDoctorClicked(int position, View view) {
                Intent intent = new Intent(Patient.this, DoctorsPage.class);
                intent.putExtra("name", doctorInfoArrayList.get(position).getName());
                intent.putExtra("proficiency", doctorInfoArrayList.get(position).getProficiency());
                intent.putExtra("availability", doctorInfoArrayList.get(position).isAvailability());
                intent.putExtra("Path", doctorInfoArrayList.get(position).getImagePath());
                intent.putExtra("uid",doctorInfoArrayList.get(position).getUid());
                startActivity(intent);
            }

            @Override
            public void onBookBtnClicked(int position, View view) {
                Toast.makeText(Patient.this, "You booked a meeting! You will get a notification when it's your turn", Toast.LENGTH_SHORT).show();
                //updates the patients queue
                HashMap<String,Object>hashToUpload = new HashMap<>();
                DatabaseReference updateQueueReferenceFromScratch = FirebaseDatabase.getInstance().getReference("users/Doctor/" + doctorInfoArrayList.get(position).getUid() + "/DoctorInfo/" + DoctorInfo.Queue +"/");
                hashToUpload.put(String.valueOf(System.currentTimeMillis()),patientInfo);
                updateQueueReferenceFromScratch.updateChildren(hashToUpload);

                HashMap<String,Object>updateAvailabilityHash = new HashMap<>();
                DatabaseReference updateAvailability = FirebaseDatabase.getInstance().getReference("users/Doctor/" + doctorInfoArrayList.get(position).getUid() + "/DoctorInfo/");
                updateAvailabilityHash.put(DoctorInfo.Availability,false);
                updateAvailability.updateChildren(updateAvailabilityHash);

            }
        });
        recyclerView.setAdapter(adapter);

        //This part downloads all the doctors

        DatabaseReference downloadAllTheDoctorsReference = FirebaseDatabase.getInstance().getReference("users/Doctor");
        downloadAllTheDoctorsReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                HashMap<String,Object>drHashMap;
                doctorInfoArrayList.clear();
                for(DataSnapshot dataSnapshot : snapshot.getChildren())
                {
                    drHashMap = (HashMap<String, Object>) dataSnapshot.getValue();
                    HashMap<String,Object> hashMap =(HashMap<String, Object>) drHashMap.get("DoctorInfo");
                    DoctorInfo info = new DoctorInfo();
                    if(hashMap!=null) {
                        info.setImagePath((String) hashMap.get(DoctorInfo.ImagePath));
                        info.setName((String)hashMap.get(DoctorInfo.Name));
                        info.setProficiency((String)hashMap.get(DoctorInfo.Proficiency));
                        info.setAvailability((Boolean)hashMap.get(DoctorInfo.Availability));
                        info.setUid((String)hashMap.get(DoctorInfo.Uid));
                        if(hashMap.containsKey(DoctorInfo.Queue))
                            info.setAvailability(false);
                        else
                            info.setAvailability(true);
                        doctorInfoArrayList.add(info);

                    }
                }
                adapter.setDoctorList(doctorInfoArrayList);
                adapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        DatabaseReference meReference = FirebaseDatabase.getInstance().getReference("users/patient/" + FirebaseAuth.getInstance().getCurrentUser().getUid() + "/patientInfo");
        meReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                patientInfo = new PatientInfo();
                HashMap<String,Object>myInfoInHash = (HashMap<String, Object>) snapshot.getValue();
                if(myInfoInHash!=null) {
                    patientInfo.setUid((String) myInfoInHash.get(Patient.UID));
                    patientInfo.setPhone((String) myInfoInHash.get(Patient.PHONE));
                    patientInfo.setPath((String) myInfoInHash.get(Patient.PATH));
                    patientInfo.setAge((String) myInfoInHash.get(Patient.AGE));
                    patientInfo.setName((String) myInfoInHash.get(Patient.NAME));
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

    }

    @Override
    public void onFilter(boolean availability) {
        if(availability)
        {
            ArrayList<DoctorInfo> tempList = new ArrayList<>();
            for(int i = 0; i< doctorInfoArrayList.size(); i++){
                if(doctorInfoArrayList.get(i).isAvailability()){
                    tempList.add(doctorInfoArrayList.get(i));
                }
            }
            adapter.setDoctorList(tempList);
            adapter.notifyDataSetChanged();
            available = true;
        }
        else{

            ArrayList<DoctorInfo> tempList = new ArrayList<>();
            for(int i = 0; i< doctorInfoArrayList.size(); i++){
                if(!doctorInfoArrayList.get(i).isAvailability()){
                    tempList.add(doctorInfoArrayList.get(i));
                }
            }
            adapter.setDoctorList(tempList);
            adapter.notifyDataSetChanged();
            available = false;
        }
    }


    @Override
    public void onCancel() {
        Toast.makeText(this, "Okay, no filter :)", Toast.LENGTH_SHORT).show();
    }


    @Override
    public void clickOk(String name, String age, String phone,String fireBasePath) {
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("users/patient/"+FirebaseAuth.getInstance().getCurrentUser().getUid());
        HashMap<String,Object> hashMap = new HashMap<>();
        PatientInfo info = new PatientInfo(name,age,phone);
        info.setPath(fireBasePath);
        info.setUid(FirebaseAuth.getInstance().getCurrentUser().getUid());
        hashMap.put("patientInfo" ,info);
        patientInfo = info;
        patientInfo.setPath(fireBasePath);
        reference.setValue(hashMap);
    }

    @Override
    public void clickCancel() {

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        stopService(serviceIntent);
    }
}
