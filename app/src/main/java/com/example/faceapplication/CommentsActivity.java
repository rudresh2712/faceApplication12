package com.example.faceapplication;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;


import com.bumptech.glide.Glide;
import com.example.faceapplication.Adapter.CommentAdapter;
import com.example.faceapplication.Model.Comment;
import com.example.faceapplication.Model.User;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class CommentsActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private CommentAdapter commentAdapter;
    private List<Comment> commentList;

    EditText addcomment;
    ImageView image_profile;
    TextView post;

    String postid,publisherid;
    FirebaseUser firebaseUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_comments);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("Comments");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });




        recyclerView= findViewById(R.id.recycler_view);
        recyclerView.setHasFixedSize(true);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(linearLayoutManager);
        commentList=new ArrayList<>();
//        readcomments();
        commentAdapter=new CommentAdapter(getApplicationContext(),commentList);      //  this and commentList
        recyclerView.setAdapter(commentAdapter);

        //THIS IS THE CORRECT PLACE FOR COMMENT ADAPTER


//        readcomments();
        Log.d("ON Intialization "," working");

        addcomment=findViewById(R.id.add_comment);
        image_profile=findViewById(R.id.image_profile);
        post=findViewById(R.id.post);

        firebaseUser= FirebaseAuth.getInstance().getCurrentUser();

        Intent in= getIntent();
        postid=in.getStringExtra("postid");
        publisherid=in.getStringExtra("publisherid");

        post.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(addcomment.getText().toString().equals(""))
                    Toast.makeText(CommentsActivity.this," Empty Comment ",Toast.LENGTH_LONG).show();
                else
                    addcomment();
            }
        });
//        recyclerView= findViewById(R.id.recycler_view);
//        recyclerView.setHasFixedSize(true);
//        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
//        recyclerView.setLayoutManager(linearLayoutManager);
//        commentList=new ArrayList<>();
//        commentAdapter=new CommentAdapter(this,commentList);
//        recyclerView.setAdapter(commentAdapter);

        getImage();
        readcomments();
        commentAdapter=new CommentAdapter(this,commentList);
        recyclerView.setAdapter(commentAdapter);

    }

    private void addcomment() {

        DatabaseReference reference= FirebaseDatabase.getInstance().getReference("Comments")
                .child(postid);

        HashMap<String,Object> hashMap= new HashMap<>();
        hashMap.put("comment",addcomment.getText().toString());
        hashMap.put("publisher",firebaseUser.getUid());

        reference.push().setValue(hashMap);
        addNotification();
        addcomment.setText("");
    }

    private void addNotification() {

        DatabaseReference reference=FirebaseDatabase.getInstance().getReference("Notification")
                .child(publisherid);

        HashMap<String,Object> hashmap=new HashMap<>();
        hashmap.put("userid",firebaseUser.getUid());
        hashmap.put("text","Commented: "+addcomment.getText().toString());
        hashmap.put("postid",postid);
        hashmap.put("ispost",true);

        reference.push().setValue(hashmap);


    }

    private void getImage()
    {
        DatabaseReference reference= FirebaseDatabase.getInstance().getReference("Users")
                .child(firebaseUser.getUid());

        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                User user=dataSnapshot.getValue(User.class);
                Glide.with(getApplicationContext()).load(user.getImageurl()).into(image_profile);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void readcomments()
    {
        DatabaseReference reference=FirebaseDatabase.getInstance().getReference()
                .child("Comments").child(postid);

        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                commentList.clear();
                for(DataSnapshot snapshot: dataSnapshot.getChildren())
                {
                    Comment comment=snapshot.getValue(Comment.class);
                    commentList.add(comment);
                    Log.d("Comment is: ",comment.getComment());
                }

                commentAdapter.notifyDataSetChanged();
                Log.d("read comments"," notify Dataset working");
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Log.d("ON Bind Holder,comment clicked "," NNN NOT OOTTT working");
            }
        });
    }
}
