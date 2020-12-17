package com.example.appointmentplanner;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.text.Layout;
import android.view.LayoutInflater;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

import com.google.android.material.textfield.TextInputEditText;

public class LoginFragment extends DialogFragment {

    interface loginFragmentListener{
        void clickOk(String email, String password);
        void clickCancel();
    }
    loginFragmentListener callback;

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        try {
            callback=(loginFragmentListener)context;
    } catch (ClassCastException e){
            throw new ClassCastException("loginFragmentListener must be implemented");
        }
        }

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        View view = LayoutInflater.from(getActivity()).inflate(R.layout.login_fragment, null);
        final TextInputEditText email = view.findViewById(R.id.email);
        final TextInputEditText password = view.findViewById(R.id.password);
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setPositiveButton("Okay", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                if (email.getText() != null && password.getText() != null) {
                    callback.clickOk(email.getText().toString(), password.getText().toString());
                }
            }
        })
        .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                callback.clickCancel();
            }
        });
        builder.setView(view);
        return builder.create();
    }
}
