package com.example.upload.utils;

import android.content.ContentResolver;
import android.content.Context;
import android.net.Uri;
import android.webkit.MimeTypeMap;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.HashSet;
import java.util.Random;
import java.util.Set;

public class Common {
    public static DatabaseReference database = FirebaseDatabase.getInstance().getReference();
    public static StorageReference storage = FirebaseStorage.getInstance().getReference();

    public static String getFileExtension(Context context, Uri uri){
        String extension;
        ContentResolver contentResolver = context.getContentResolver();
        MimeTypeMap mimeTypeMap = MimeTypeMap.getSingleton();
        extension = mimeTypeMap.getExtensionFromMimeType(contentResolver.getType(uri));
        return extension;
    }

    public static String randomIdentifier(int length){
        String lexicon = "ABCDEFGHIJKLMNOPQRSTUVWXYZ1234567890";
        Set<String> identifiers = new HashSet<>();
        Random rand = new Random();
        StringBuilder builder = new StringBuilder();
        while (builder.toString().length() == 0){

            for (int i = 0; i < length; i++) {
                builder.append(lexicon.charAt(rand.nextInt(lexicon.length())));
            }
            if (identifiers.contains(builder.toString())){
                builder = new StringBuilder();
            }
        }
        return builder.toString();
    }
}
