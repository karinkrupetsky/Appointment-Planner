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
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.StorageTask;
import com.google.firebase.storage.UploadTask;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.UUID;
import static android.app.Activity.RESULT_OK;

public class DoctorCredentialsFragment extends DialogFragment {
    private ImageView imageView;
    private ProgressDialog progressDialog;
    FirebaseStorage storage =  FirebaseStorage.getInstance();
    StorageReference storageReference =  storage.getReference();
    private Uri filePath;
    private String address;
    private String firebasePath;
    private final int PICK_IMAGE_REQUEST = 71;


    interface DoctorCredentialsFragmentInterface {
        void clickOk(String name , String proficiency,String address);
    }
    DoctorCredentialsFragmentInterface callback;


    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        try {
            callback=(DoctorCredentialsFragmentInterface)context;
        } catch (ClassCastException e){
            throw new ClassCastException("PatientCredentialsFragmentInterface must be implemented");
        }
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        View view = LayoutInflater.from(getActivity()).inflate(R.layout.doctor_info_fragment, null);
        final TextInputEditText name = view.findViewById(R.id.name);
        final TextInputEditText pro = view.findViewById(R.id.pro);
        Button takePicBtn = view.findViewById(R.id.take_picture);
        imageView = view.findViewById(R.id.image);
        takePicBtn.setOnClickListener(new View.OnClickListener() {
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
                if (name.getText() != null && pro.getText()!=null) {
                    System.out.println("this is fireBasePath " + firebasePath);
                    callback.clickOk(name.getText().toString(), pro.getText().toString(),firebasePath);
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
            }
            catch (IOException e)
            {
                e.printStackTrace();
            }
        }
    }



    private void uploading(Uri pickedImage) throws IOException {

        progressDialog = new ProgressDialog(requireContext());
        progressDialog.setTitle("Uploading...");
        progressDialog.show();

        StorageReference storageReference = FirebaseStorage.getInstance().getReference();
        final StorageReference pictureReference = storageReference.child("images/"+ UUID.randomUUID().toString());
        StorageTask<UploadTask.TaskSnapshot>uploadTask;
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
