package com.example.android.ersav;

import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.provider.SyncStateContract;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
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
import com.google.firebase.storage.UploadTask;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;

public class PostActivity extends AppCompatActivity {
    private static final String TAG ="PostActivity" ;
    private ImageButton select_Image;
    private EditText addDescription;
    private CheckBox checkBox;
    private Button submitImage;
    private ProgressDialog loadingBar;

    private static final int Gallery_Pick = 1;

    private Uri ImageUri;

    private String Description;
    private String saveCurrentDate;
    private String saveCurrentTime;
    private String postRandomName;
    private String downloadUri;
    private String current_user_id;
    private FirebaseUser currentUser;

    private StorageReference PostsImageReference;
    private DatabaseReference usersRef,PostsRef;
    private FirebaseAuth mAuth;

    private long countPost = 0 ;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_post);

        mAuth = FirebaseAuth.getInstance();
        //currentUser = mAuth.getCurrentUser();
       // FirebaseUser currentUser = mAuth.getCurrentUser();
       //if (currentUser != null){
          // current_user_id = currentUser.getUid();
        //}

        PostsImageReference = FirebaseStorage.getInstance().getReference();
        usersRef = FirebaseDatabase.getInstance().getReference().child("Users");
        current_user_id = usersRef.push().getKey();
        PostsRef = FirebaseDatabase.getInstance().getReference().child("Posts");




        addDescription = (EditText)findViewById(R.id.description);
        select_Image = (ImageButton)findViewById(R.id.postImage);
        submitImage = (Button)findViewById(R.id.submitBtn);

        loadingBar = new ProgressDialog(this);

        select_Image.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v)
            {
                OpenGallary();
            }
        });


        submitImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v)
            {
                ValidatePostInfo();
            }
        });

    }

    private void ValidatePostInfo()
    {
        Description = addDescription.getText().toString();
        if(ImageUri == null)
        {
            Toast.makeText(this, "Please Upload Your Image", Toast.LENGTH_SHORT).show();
        }
        else if(TextUtils.isEmpty(Description))
        {
            Toast.makeText(this, "Please Add Description of Your Emergency", Toast.LENGTH_SHORT).show();
        }
        else {
            loadingBar.setTitle("Posting");
            loadingBar.setMessage("Please Wait while we are uploading your Post");
            loadingBar.show();
            loadingBar.setCanceledOnTouchOutside(true);

            StoringImageToDB();
        }
    }

    private void StoringImageToDB()
    {
        Calendar calForDate = Calendar.getInstance();
        SimpleDateFormat currentDate = new SimpleDateFormat("dd-MMMM-yyyy");
        saveCurrentDate = currentDate.format(calForDate.getTime());


        Calendar calForTime = Calendar.getInstance();
        SimpleDateFormat currentTime = new SimpleDateFormat("HH-mm");
        saveCurrentTime = currentTime.format(calForDate.getTime());

        postRandomName = saveCurrentDate + saveCurrentTime;

        StorageReference filePath = PostsImageReference.child("Emergency Images").child(ImageUri.getLastPathSegment() + postRandomName + ".jpg");
        filePath.putFile(ImageUri).addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task)
            {
                if(task.isSuccessful())
                {
                    downloadUri = task.getResult().getDownloadUrl().toString();
                    SavingInfoToDb();
                    Toast.makeText(PostActivity.this, "Image Uploaded Successfully", Toast.LENGTH_SHORT).show();

                }else
                {
                    String message = task.getException().getMessage();
                    Toast.makeText(PostActivity.this, "Error Occurred: " + message, Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private void SavingInfoToDb()
    {


        HashMap postMap = new HashMap();
        postMap.put("uid", current_user_id);
        postMap.put("date", saveCurrentDate);
        postMap.put("time", saveCurrentTime);
        postMap.put("description", Description);
        postMap.put("emergency", downloadUri);
        postMap.put("counter", countPost);
        PostsRef.child(current_user_id).updateChildren(postMap).addOnCompleteListener(new OnCompleteListener() {
            @Override
            public void onComplete(@NonNull Task task) {
                if(task.isSuccessful()){
                    SendUserToMainActivity();
                    Toast.makeText(PostActivity.this, "Your Post is Successfully", Toast.LENGTH_SHORT).show();
                    loadingBar.dismiss();
            }else {
                    String message = task.getException().getMessage();
                    Toast.makeText(PostActivity.this, "Error has Occurred while posting" + message, Toast.LENGTH_SHORT).show();
                    loadingBar.dismiss();
                }
            }
        });
    }

    private void SendUserToMainActivity()
    {
        Intent mainIntent = new Intent(PostActivity.this, MainActivity.class);
        startActivity(mainIntent);
    }

    private void OpenGallary()
    {
        Intent galleryIntent = new Intent();
        galleryIntent.setAction(Intent.ACTION_GET_CONTENT);
        galleryIntent.setType("image/*");
        startActivityForResult(galleryIntent,Gallery_Pick);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data)
    {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == Gallery_Pick && resultCode == RESULT_OK && data!=null)
        {
            ImageUri = data.getData();
            select_Image.setImageURI(ImageUri);
        }
    }
}
