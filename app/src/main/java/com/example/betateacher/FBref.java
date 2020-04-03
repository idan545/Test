package com.example.betateacher;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;


public class FBref {
    public static FirebaseAuth refAuth=FirebaseAuth.getInstance();
    public static FirebaseDatabase FBDB = FirebaseDatabase.getInstance();
    public static DatabaseReference refOffer=FBDB.getReference("LessonOffer");
    public static DatabaseReference refStudents=FBDB.getReference("Students");
    public static DatabaseReference refTeachers=FBDB.getReference("Teachers");
    public static FirebaseStorage FBST = FirebaseStorage.getInstance();
    public static StorageReference refStor=FBST.getReference();
    public static StorageReference refImages = refStor.child("Images");
}