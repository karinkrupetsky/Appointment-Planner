package com.example.appointmentplanner;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.StorageTask;
import com.google.firebase.storage.UploadTask;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.UUID;

import static android.app.Activity.RESULT_OK;

public class PatientCradentialsFragment extends DialogFragment {
    private final int PICK_IMAGE_REQUEST = 72;
    private ImageView imageView;
    private Uri filePath;
    private String address;
    private String firebasePath;




    interface PatientCradentialsFragmentInterface{
        void clickOk(String name , String age, String phone,String fireBasePath);
        void clickCancel();
    }
    PatientCradentialsFragmentInterface callback;


    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        try {
            callback=(PatientCradentialsFragment.PatientCradentialsFragmentInterface)context;
        } catch (ClassCastException e){
            throw new ClassCastException("PatientCradentialsFragmentInterface must be implemented");
        }
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        View view = LayoutInflater.from(getActivity()).inflate(R.layout.patient_info_fragment, null);
        final TextInputEditText age = view.findViewById(R.id.age);
        final TextInputEditText phone = view.findViewById(R.id.phone);
        final TextInputEditText name = view.findViewById(R.id.name);
        imageView = view.findViewById(R.id.image);
        Button takePic = view.findViewById(R.id.take_picture);
        takePic.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
                intent.setType("image/*");
                intent.setAction(Intent.ACTION_GET_CONTENT);
                startActivityForResult(Intent.createChooser(intent, "Select Picture"), PICK_IMAGE_REQUEST);
            }
        });
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setPositiveButton("Okay", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                if (age.getText() != null && phone.getText() != null && name.getText() != null) {
                    System.out.println("cal - firebasepath: " + firebasePath);
                    callback.clickOk(name.getText().toString(), age.getText().toString() , phone.getText().toString(),firebasePath);
                }
            }
        }).setCancelable(false);
        builder.setView(view);
        return builder.create();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK
                && data != null && data.getData() != null )
        {
            Uri pickedImage = data.getData();

            filePath = data.getData();
            try {
                Bitmap bitmap = MediaStore.Images.Media.getBitmap(requireContext().getContentResolver(),filePath);
                imageView.setImageBitmap(bitmap);
                uploading(pickedImage);
                //uploadImage();
            }
            catch (IOException e)
            {
                e.printStackTrace();
            }
        }
    }


    private void uploading(Uri pickedImage) throws IOException {
        final ProgressDialog progressDialog = new ProgressDialog(requireContext());
        progressDialog.setTitle("Uploading...");
        progressDialog.show();

        StorageReference storageReference = FirebaseStorage.getInstance().getReference();
        final StorageReference pictureReference = storageReference.child("images/"+ UUID.randomUUID().toString());
        StorageTask<UploadTask.TaskSnapshot> uploadTask;
        System.out.println("this is pickedImage: " + pickedImage);
        Bitmap bitmap =MediaStore.Images.Media.getBitmap(requireContext().getContentResolver(),pickedImage);
        bitmap = Bitmap.createScaledBitmap(bitmap,500,480,false);
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG,70,out);

        uploadTask = pictureReference.putStream(new ByteArrayInputStream(out.toByteArray()));
        uploadTask.continueWithTask(new Continuation<UploadTask.TaskSnapshot, Task<Uri>>() {
            @Override
            public Task<Uri> then(@NonNull Task<UploadTask.TaskSnapshot> task) throws Exception {
                double pro = (100.0*task.getResult().getBytesTransferred())/task.getResult().getTotalByteCount();
                progressDialog.setMessage("Uploading " + (int)pro + "%");
                if(!task.isSuccessful())
                {
                    if(task.getException()!=null)
                    {
                        throw task.getException();
                    }
                }
                return pictureReference.getDownloadUrl();
            }
        }).addOnCompleteListener(new OnCompleteListener<Uri>() {
            @Override
            public void onComplete(@NonNull Task<Uri> task) {
                if(task.isSuccessful())
                {
                    Uri uri = task.getResult();
                    if(uri!=null){
                        String sUri = uri.toString();
                        firebasePath = sUri;
                        progressDialog.dismiss();
                    }
                }
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                System.out.println("failed to upload");
                progressDialog.dismiss();
            }
        });
    }


}
