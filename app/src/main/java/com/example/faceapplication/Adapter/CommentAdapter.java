package com.example.faceapplication.Adapter;

import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.faceapplication.MainActivity;
import com.example.faceapplication.Model.Comment;
import com.example.faceapplication.Model.User;
import com.example.faceapplication.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.List;

public class CommentAdapter extends RecyclerView.Adapter<CommentAdapter.ViewHolder>{

    private Context mContext;
    private List<Comment> mComment;
    private FirebaseUser firebaseUser;



    public CommentAdapter(Context mContext, List<Comment> mComment) {
        this.mContext = mContext;
        this.mComment = mComment;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view= LayoutInflater.from(mContext).inflate(R.layout.comment_item,parent,false);
        Log.d("ON Create Holder"," working");
        return new CommentAdapter.ViewHolder(view);

//        View view= LayoutInflater.from(mContext).inflate(R.layout.comment_item,parent,false);
      //  return new CommentAdapter().ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {

        firebaseUser= FirebaseAuth.getInstance().getCurrentUser();
        final Comment comment=mComment.get(position);

        holder.comment.setText(comment.getComment());
        getUserInfo(holder.image_profile,holder.username,comment.getPublisher());

        Log.d("ON Bind Holder, outside comment clicked "," working");

        holder.comment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent in =new Intent(mContext, MainActivity.class);
                in.putExtra("publisherid",comment.getPublisher());
                mContext.startActivity(in);
                Log.d("ON Bind Holder,comment clicked "," working");
            }
        });


        holder.image_profile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent in =new Intent(mContext, MainActivity.class);
                in.putExtra("publisherid",comment.getPublisher());
                mContext.startActivity(in);
                Log.d("ON Bind Holder,img Profile clicked "," working");
            }
        });
    }

    @Override
    public int getItemCount() {
        return mComment.size();
    }



    public class ViewHolder extends RecyclerView.ViewHolder{

        public ImageView image_profile;
        public TextView username,comment;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            image_profile=itemView.findViewById(R.id.image_profile);
            username=itemView.findViewById(R.id.username);
            comment=itemView.findViewById(R.id.comment);

        }
    }



    private void getUserInfo(final ImageView imageView,final TextView username,String publisherid)
    {
        DatabaseReference reference= FirebaseDatabase.getInstance().getReference().
                child("Users").child(publisherid);
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                User user =dataSnapshot.getValue(User.class);
                Glide.with(mContext).load(user.getImageurl()).into(imageView);
                username.setText(user.getUsername());
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }
}
