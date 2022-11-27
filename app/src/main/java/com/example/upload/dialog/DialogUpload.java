package com.example.upload.dialog;


import static com.example.upload.utils.Common.database;
import static com.example.upload.utils.Common.getFileExtension;
import static com.example.upload.utils.Common.randomIdentifier;
import static com.example.upload.utils.Common.storage;

import android.Manifest;
import android.app.Activity;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.example.upload.R;
import com.example.upload.model.DataUpload;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.karumi.dexter.Dexter;
import com.karumi.dexter.MultiplePermissionsReport;
import com.karumi.dexter.PermissionToken;
import com.karumi.dexter.listener.PermissionRequest;
import com.karumi.dexter.listener.multi.MultiplePermissionsListener;

import java.io.IOException;
import java.util.List;

public class DialogUpload extends BottomSheetDialogFragment {
    Context context;
    ImageView iv_review;
    ImageButton ib_search;
    EditText et_title, et_description;
    Button btn_save;
    ProgressDialog loading;
    int ACCESS_DATA = 40;
    Bitmap bitmap;
    String extension;
    Uri uri;
    boolean is_success = true;
    String title, description, file_name;


    public DialogUpload(Context context) {
        this.context = context;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.modal_data, container, false);
        findById(v);
        initLoading();
        findImage();
        clickSave();
        return v;
    }

    private void clickSave() {
        btn_save.setOnClickListener(vv ->{
            is_success = true;
            title = et_title.getText().toString().trim();
            description = et_description.getText().toString().trim();

            notEmpty(description, et_description);
            notEmpty(title, et_title);

            if (is_success){
                String key = String.valueOf(System.currentTimeMillis());
                uploadData(key, uri);
            }

        });
    }

    private void uploadData(String key, Uri uri) {
        loading.setCancelable(false);
        StorageReference str = storage.child("media").child("upload").child(key);
        str.putFile(uri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                str.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                    @Override
                    public void onSuccess(Uri u) {
                        saveData(key, u.toString());
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(context, e.getMessage(), Toast.LENGTH_LONG).show();
                    }
                });
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {

            }
        }).addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onProgress(@NonNull UploadTask.TaskSnapshot snapshot) {
                float progress = 100.0f * snapshot.getBytesTransferred() / snapshot.getTotalByteCount();
                loading.setMessage(String.format("Upload %.2f", progress) + "%");
                loading.show();
            }
        });
    }

    private void saveData(String key, String s) {
        DataUpload upload = new DataUpload(
                s,
                file_name,
                title,
                description
        );
        loading.setMessage("Loading...");
        loading.show();
        database.child("upload").child(key).setValue(upload)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void unused) {
                        Toast.makeText(context, "Data Berhasil tersimpan!", Toast.LENGTH_LONG).show();
                        loading.dismiss();
                        dismiss();
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(context, e.getMessage(), Toast.LENGTH_LONG).show();
                    }
                });
    }

    private void findImage() {
        ib_search.setOnClickListener(v ->{
            Dexter.withContext(context).withPermissions(Manifest.permission.WRITE_EXTERNAL_STORAGE)
                    .withListener(new MultiplePermissionsListener(){

                        @Override
                        public void onPermissionsChecked(MultiplePermissionsReport multiplePermissionsReport) {
                            browseDocuments();
                        }

                        @Override
                        public void onPermissionRationaleShouldBeShown(List<PermissionRequest> list, PermissionToken permissionToken) {
                            permissionToken.continuePermissionRequest();
                        }
                    }).check();
        } );
    }

    private void browseDocuments() {
        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        intent.addCategory(Intent.CATEGORY_OPENABLE);
        intent.setType("image/*");
        startActivityForResult(Intent.createChooser(intent, "Cari Gambar"), ACCESS_DATA);
    }

    private void notEmpty(String s, EditText et){
        if (s.isEmpty()){
            et.setError("Data tidak boleh kosong!");
            et.requestFocus();
            is_success = false;
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == ACCESS_DATA && resultCode == Activity.RESULT_OK){
            if (data != null){
                uri = data.getData();
            }
            try {
                bitmap = MediaStore.Images.Media.getBitmap(context.getContentResolver(), uri);
                iv_review.setImageBitmap(bitmap);
                extension = getFileExtension(context, uri);
                String random = randomIdentifier(10);
                file_name = "IMG_" + random + "."+ extension;
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private void initLoading() {
        loading = new ProgressDialog(context);
        loading.setCancelable(false);
        loading.setMessage("Loading.....");
    }
    private void findById(View v) {
        iv_review = v.findViewById(R.id.iv_review);
        ib_search = v.findViewById(R.id.ib_search);
        et_title =v.findViewById(R.id.et_title);
        et_description = v.findViewById(R.id.et_description);
        btn_save =v.findViewById(R.id.btn_save);
    }

    @Override
    public void onStart() {
        super.onStart();
        Dialog dialog = getDialog();
        if (dialog != null){
            dialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        }
    }
}
