package com.example.appointmentplanner;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.HashMap;

public class DoctorsQueueAdapter extends BaseAdapter {
    private ArrayList<HashMap<String,String>> patientInfos;
    private ArrayList<PatientInfo>infoArrayList;


    public void setPatientInfos(ArrayList<HashMap<String,String>> patientInfos1){
        patientInfos = patientInfos1;
    }
    @Override
    public int getCount() {
        //return patientInfos.size();
        return infoArrayList.size();
    }

    @Override
    public Object getItem(int position) {
        //return patientInfos.get(position);
        return infoArrayList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        LayoutInflater layoutInflater = (LayoutInflater) parent.getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        if(layoutInflater!=null) {
            convertView = layoutInflater.inflate(R.layout.queue_cell_layout , parent, false);
            TextView name = convertView.findViewById(R.id.name);
            TextView age = convertView.findViewById(R.id.age);
           // HashMap<String,String> patientInfoHashMap = patientInfos.get(position);
            name.setText(infoArrayList.get(position).getName());
            age.setText(infoArrayList.get(position).getAge());
        }
        return convertView;
    }
    public void setPatientList(ArrayList<PatientInfo> infoArrayList){
        this.infoArrayList = infoArrayList;
    }
}
