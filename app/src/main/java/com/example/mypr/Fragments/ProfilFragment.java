package com.example.mypr.Fragments;

import android.app.ProgressDialog;
import android.content.ContentResolver;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.MimeTypeMap;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.mypr.Model.User;
import com.example.mypr.R;
import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.StorageTask;
import com.google.firebase.storage.UploadTask;

import java.util.HashMap;
import java.util.Objects;

import de.hdodenhof.circleimageview.CircleImageView;

import static android.app.Activity.RESULT_OK;


public class ProfilFragment extends Fragment {

    CircleImageView profile_image;
    TextView username;

    FirebaseUser firebaseUser;
    DatabaseReference reference;

    StorageReference storageReference;
    public static final int img_request=1;
    private Uri imageURL;
    private StorageTask task;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
         View view=inflater.inflate(R.layout.fragment_profil, container, false);

         username=view.findViewById(R.id.username);
         profile_image=view.findViewById(R.id.profile_image);

         storageReference= FirebaseStorage.getInstance().getReference("uploads");

         firebaseUser= FirebaseAuth.getInstance().getCurrentUser();
         reference= FirebaseDatabase.getInstance().getReference("Users").child(firebaseUser.getUid());
       // DatabaseReference reference= FirebaseDatabase.getInstance().getReference("Users");
         reference.addValueEventListener(new ValueEventListener() {
             @RequiresApi(api = Build.VERSION_CODES.KITKAT)
             @Override
             public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                 User user=dataSnapshot.getValue(User.class);
                // assert user != null;
                 assert user != null;
                 username.setText(user.getUsername());

                 if(user.getImageURL().equals("default"))
                     profile_image.setImageResource(R.mipmap.ic_launcher);
                 else
                     Glide.with(Objects.requireNonNull(getContext())).load(user.getImageURL()).into(profile_image);

             }

             @Override
             public void onCancelled(@NonNull DatabaseError databaseError) {

             }
         });

         profile_image.setOnClickListener(new View.OnClickListener() {
             @Override
             public void onClick(View v) {
                 openImage();
             }
         });

        return view;
    }
    public void openImage()
    {
        Intent intent=new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(intent,img_request);

    }
    private String getFileExtension(Uri uri1)
    {
        ContentResolver contentResolver=getContext().getContentResolver();
        MimeTypeMap mimeTypeMap=MimeTypeMap.getSingleton();
        return mimeTypeMap.getExtensionFromMimeType(contentResolver.getType(uri1));
    }
    private void uploadImage()
    {
        final ProgressDialog progressDialog=new ProgressDialog(getContext());
        progressDialog.setMessage("Uploading...");
        progressDialog.show();

        if(imageURL!=null)
        {
           // StorageReference storageReference1=FirebaseStorage.getInstance().getReference();

            final StorageReference storageReference1=storageReference.child(System.currentTimeMillis()+ "." + getFileExtension(imageURL));
           task=storageReference1.putFile(imageURL);
            Log.i("OUTPUT",System.currentTimeMillis()+ "." + getFileExtension(imageURL));
            task.continueWithTask(new Continuation<UploadTask.TaskSnapshot,Task<Uri>>() {
                @Override
                public Task<Uri> then(@NonNull Task<UploadTask.TaskSnapshot> task) throws Exception {
                    if(!task.isSuccessful())
                        throw task.getException();
                    Log.i("Hello","error here");
                    return storageReference.getDownloadUrl();
                }
            }).addOnCompleteListener(new OnCompleteListener<Uri>() {
                @Override
                public void onComplete(@NonNull Task<Uri> task) {

                    if(task.isSuccessful())
                    {
                        Uri u=  task.getResult();
                        String myu=u.toString();
                        reference=FirebaseDatabase.getInstance().getReference("Users").child(firebaseUser.getUid());
                        HashMap<String,Object> map=new HashMap<>();
                        map.put("imageURL",myu);
                        reference.updateChildren(map);
                        progressDialog.dismiss();
                    }
                    else
                    {
                        Toast.makeText(getContext(), "Failed", Toast.LENGTH_SHORT).show();
                        progressDialog.dismiss();
                    }

                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    Toast.makeText(getContext(),e.getMessage(), Toast.LENGTH_SHORT).show();
                    progressDialog.dismiss();
                }
            });
        }
        else
        {
            Toast.makeText(getContext(), "No Image Selected!!", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode==img_request&&resultCode==RESULT_OK&& data!=null&&data.getData()!=null)
        {
                 imageURL=data.getData();
                 if(task!=null&&task.isInProgress())
                     Toast.makeText(getContext(), "Task In Progress", Toast.LENGTH_SHORT).show();
                 else
                     uploadImage();
        }
    }
}
