package com.example.upload;


import android.app.ProgressDialog;
import android.content.Context;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;
import static com.example.upload.utils.Common.database;
import static com.example.upload.utils.Common.storage;

import com.example.upload.dialog.DialogUpload;
import com.example.upload.helper.AdapterUpload;
import com.example.upload.model.DataUpload;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {
    RecyclerView recycler_view;
    FloatingActionButton fab_add;
    Context context;
    ProgressDialog loading;
    ArrayList<DataUpload> uploadArrayList = new ArrayList<>();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        context = this;
        recycler_view = findViewById(R.id.recycler_view);
        fab_add = findViewById(R.id.fab_add);

        initLoading();
        showDialog();
        showData();
    }

    private void showData() {
        loading.show();
        database.child("upload").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                uploadArrayList.clear();
                for (DataSnapshot item : snapshot.getChildren()){
                    DataUpload upload = item.getValue(DataUpload.class);
                    uploadArrayList.add(upload);
                }
                loading.dismiss();
                AdapterUpload adapterUpload = new AdapterUpload(context, uploadArrayList);
                recycler_view.setAdapter(adapterUpload);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void showDialog() {
        fab_add.setOnClickListener(v ->{
            DialogUpload dialogUpload = new DialogUpload(context);
            dialogUpload.show(getSupportFragmentManager(), "fn-upload");
        });
    }

    private void initLoading() {
        loading = new ProgressDialog(context);
        loading.setCancelable(false);
        loading.setMessage("Loading.....");
    }
}