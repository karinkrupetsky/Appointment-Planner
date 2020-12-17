package com.example.appointmentplanner;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.os.IBinder;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.NotificationCompat;

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

public class CountToAppointmentService extends Service {
    private final String ChannelID = "ChannelId";
    private ArrayList<PatientInfo> infoArrayList = new ArrayList<>();
    private DoctorInfo doctorInfo;
    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }
    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        doctorInfo = new DoctorInfo();
        setFListener();
        return super.onStartCommand(intent, flags, startId);
    }
    private void createNotification(){
        Intent intent = new Intent(CountToAppointmentService.this,DoctorsPage.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        //intent.putExtra("uid",getIntent().getStringExtra("uid"));

        intent.putExtra("notif",true);
        //intent.putExtra(Patient.NAME,infoArrayList.get(0).getName());
        intent.putExtra(DoctorInfo.Name,doctorInfo.getName());
        intent.putExtra(DoctorInfo.Uid,doctorInfo.getUid());
        intent.putExtra(DoctorInfo.Availability,doctorInfo.isAvailability());
        intent.putExtra(DoctorInfo.Proficiency,doctorInfo.getProficiency());
        intent.putExtra(DoctorInfo.Path,doctorInfo.getImagePath());
        PendingIntent pendingIntent = PendingIntent.getActivity(this,0,intent,PendingIntent.FLAG_CANCEL_CURRENT|PendingIntent.FLAG_UPDATE_CURRENT);
        CharSequence name = "Notif_Channel";
        String description = "Channel Description";
        int importance = NotificationManager.IMPORTANCE_DEFAULT;
        NotificationChannel channel = new NotificationChannel(ChannelID,name,importance);
        channel.setDescription(description);
        NotificationManager notificationManager = this.getSystemService(NotificationManager.class);
        if(notificationManager!=null) {
            notificationManager.createNotificationChannel(channel);
            NotificationCompat.Builder builder = new NotificationCompat.Builder(this, ChannelID)
                    .setSmallIcon(android.R.drawable.star_on)
                    .setContentTitle("Appointment Time with " + doctorInfo.getName())
                    .setContentText("Your appointment with the doctor is about to start")
                    .setPriority(NotificationCompat.PRIORITY_DEFAULT).setAutoCancel(true)
                    .setContentIntent(pendingIntent)
                    .setAutoCancel(true);
            //NotificationManagerCompat notificationManagerCompat = NotificationManagerCompat.from(this);
            //notificationManagerCompat.notify(123456, builder.build());
            startForeground(123456,builder.build());
        }
    }


    private void setFListener() {
        DatabaseReference downloadAllTheDoctorsReference = FirebaseDatabase.getInstance().getReference("users/Doctor");
        downloadAllTheDoctorsReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                HashMap<String,Object>drHashMap;
                infoArrayList.clear();
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
                        CountToAppointmentService.this.doctorInfo = info;
                        if(hashMap.containsKey(DoctorInfo.Queue)) {
                            info.setAvailability(false);
                            HashMap<String,Object>queue = (HashMap<String, Object>) hashMap.get(DoctorInfo.Queue);
                            if(queue!=null) {
                                Set<String> keySet = queue.keySet();
                                Iterator<String> iterator = keySet.iterator();
                                String key = iterator.next();
                                PatientInfo patientInfo = new PatientInfo();
                                HashMap<String, Object> patient = (HashMap<String, Object>) queue.get(key);
                                if (patient != null) {
                                    patientInfo.setAge((String) patient.get(Patient.AGE));
                                    patientInfo.setPath((String) patient.get(Patient.PATH));
                                    patientInfo.setPhone((String) patient.get(Patient.PHONE));
                                    patientInfo.setName((String) patient.get(Patient.NAME));
                                    patientInfo.setUid((String) patient.get(Patient.UID));
                                    if(patientInfo.getUid().equals(FirebaseAuth.getInstance().getCurrentUser().getUid()))
                                    {
                                        infoArrayList.add(patientInfo);
                                        createNotification();
                                    }
                                }


                            }
                        }
                        else
                            info.setAvailability(true);


                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }


    @Override
    public void onDestroy() {
        super.onDestroy();
        stopForeground(true);
    }


}
