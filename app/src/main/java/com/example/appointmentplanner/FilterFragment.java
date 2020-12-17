package com.example.appointmentplanner;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.RadioGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

public class FilterFragment extends DialogFragment {
    private boolean available = true;
    interface filterInterface{
        void onFilter(boolean availability);
        void onCancel();
    }
    filterInterface callback;

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        try {
            callback = (filterInterface) context;
        }
        catch (ClassCastException e){
            throw new ClassCastException("Activity must implement FilterFragment Interface");
        }
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        View view = LayoutInflater.from(getContext()).inflate(R.layout.filter_layout,null);
        RadioGroup radioGroup = view.findViewById(R.id.filter_radio_group);
        radioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                switch(group.getCheckedRadioButtonId()){
                    case R.id.filter_available:
                    {
                        available = true;
                        break;
                    }
                    case R.id.filter_not_available:
                    {
                        available = false;
                        break;
                    }
                    case -1:
                    {
                        available = true;
                        System.out.println(" ");
                        break;
                    }
                    default:
                }
            }
        });
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setView(view).setPositiveButton("Filter", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                callback.onFilter(available);
            }
        }).setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                callback.onCancel();
            }
        }).setCancelable(true);
        return builder.create();
    }
}
