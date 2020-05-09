package com.example.faceapplication.Adapter;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.FragmentActivity;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.faceapplication.CommentsActivity;
import com.example.faceapplication.FollowersActivity;
import com.example.faceapplication.Fragment.PostDetailFragment;
import com.example.faceapplication.Fragment.ProfileFragment;
import com.example.faceapplication.Model.Post;
import com.example.faceapplication.Model.User;
import com.example.faceapplication.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.HashMap;
import java.util.List;

public class PostAdapter extends RecyclerView.Adapter<PostAdapter.ViewHolder>{


    public Context mContext;
    public List<Post> mPost;

    private FirebaseUser firebaseUser;

    public PostAdapter(Context mContext, List<Post> mPost) {
        this.mContext = mContext;
        this.mPost = mPost;
    }

    public class ViewHolder extends RecyclerView.ViewHolder{

        public ImageView image_profile,post_image,like,comment,save;
        public TextView username,likes,publisher,description,comments;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            image_profile=itemView.findViewById(R.id.image_profile);
            post_image=itemView.findViewById(R.id.post_image);
            save=itemView.findViewById(R.id.save);
            like=itemView.findViewById(R.id.like);
            comment=itemView.findViewById(R.id.comment);
            username=itemView.findViewById(R.id.username);
            likes=itemView.findViewById(R.id.likes);
            comments=itemView.findViewById(R.id.comments);
            description=itemView.findViewById(R.id.description);
            publisher=itemView.findViewById(R.id.publisher);

        }
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {

        View view = LayoutInflater.from(mContext).inflate(R.layout.post_item,viewGroup,false);

        return new PostAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final ViewHolder holder, int position) {

        firebaseUser= FirebaseAuth.getInstance().getCurrentUser();
         final Post post=mPost.get(position);
        Glide.with(mContext).load(post.getPostimage()).into(holder.post_image);

//        if(post.getDescription().equals(""))
//            holder.description.setVisibility(View.GONE);
//        else
//        {
            holder.description.setVisibility(View.VISIBLE);
            holder.description.setText(post.getDescription());
       // }


        if(holder.image_profile==null)
            Log.d("Error here ","Image Profile");
        if(holder.username==null)
            Log.d("Error here ","Username");
        if(holder.publisher==null)
            Log.d("Error here ","Publisher");
        if(post.getPublisher()==null)
            Log.d("Error here ","Post publisher");


        publisherInfo(holder.image_profile,holder.username,holder.publisher,post.getPublisher());
        isLiked(post.getPostid(),holder.like);
        nrLikes(holder.likes,post.getPostid());
        getComments(post.getPostid(),holder.comments);
        isSaved(post.getPostid(),holder.save);






        holder.image_profile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                SharedPreferences.Editor editor=mContext.getSharedPreferences("PREFS",Context.MODE_PRIVATE).edit();
                editor.putString("profileid",post.getPublisher());
                editor.apply();
                ((FragmentActivity)mContext).getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container
                ,new ProfileFragment()).commit();
            }
        });

        holder.username.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                SharedPreferences.Editor editor=mContext.getSharedPreferences("PREFS",Context.MODE_PRIVATE).edit();
                editor.putString("profileid",post.getPublisher());
                editor.apply();
                ((FragmentActivity)mContext).getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container
                        ,new ProfileFragment()).commit();
            }
        });

        holder.publisher.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                SharedPreferences.Editor editor=mContext.getSharedPreferences("PREFS",Context.MODE_PRIVATE).edit();
                editor.putString("profileid",post.getPublisher());
                editor.apply();
                ((FragmentActivity)mContext).getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container
                        ,new ProfileFragment()).commit();
            }
        });

        holder.post_image.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                SharedPreferences.Editor editor=mContext.getSharedPreferences("PREFS",Context.MODE_PRIVATE).edit();
                editor.putString("postid",post.getPostid());
                editor.apply();
                ((FragmentActivity)mContext).getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container
                        ,new PostDetailFragment()).commit();
            }
        });

        holder.save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(holder.save.getTag().equals("save"))
                    FirebaseDatabase.getInstance().getReference().child("Saves").child(firebaseUser.getUid())
                        .child(post.getPostid()).setValue(true);
                else
                    FirebaseDatabase.getInstance().getReference().child("Saves").child(firebaseUser.getUid())
                            .child(post.getPostid()).removeValue();
            }
        });



        holder.like.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(holder.like.getTag().equals("like")) {
                    FirebaseDatabase.getInstance().getReference().child("Likes")
                            .child(post.getPostid()).child(firebaseUser.getUid()).setValue(true);
                        addNotification(post.getPublisher(),post.getPostid());

                }else
                    FirebaseDatabase.getInstance().getReference().child("Likes")
                            .child(post.getPostid()).child(firebaseUser.getUid()).removeValue();
            }
        });

        holder.comment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent in = new Intent(mContext, CommentsActivity.class);
                in.putExtra("postid",post.getPostid());
                in.putExtra("publisherid",post.getPublisher());
                mContext.startActivity(in);
            }
        });

        holder.comments.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent in = new Intent(mContext, CommentsActivity.class);
                in.putExtra("postid",post.getPostid());
                in.putExtra("publisherid",post.getPublisher());
                mContext.startActivity(in);
            }
        });

        holder.likes.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent in= new Intent(mContext, FollowersActivity.class);
                in.putExtra("id",post.getPostid());
                in.putExtra("title","likes");
                mContext.startActivity(in);
            }
        });
    }

    private void addNotification(String userid, String postid) {

            DatabaseReference reference=FirebaseDatabase.getInstance().getReference("Notification")
                    .child(userid);

            HashMap<String,Object> hashmap=new HashMap<>();
            hashmap.put("userid",firebaseUser.getUid());
            hashmap.put("text","liked your post ");
            hashmap.put("postid",postid);
            hashmap.put("ispost",true);

            reference.push().setValue(hashmap);


    }

    @Override
    public int getItemCount() {
        return mPost.size();
    }




    private void getComments(String postid,final  TextView comments)
    {
        DatabaseReference reference= FirebaseDatabase.getInstance().getReference()
                .child("Comments").child(postid);

        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                comments.setText("View All "+dataSnapshot.getChildrenCount()+" Comments");

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }


    private void isLiked(String postid,final ImageView imageView)
    {
       final FirebaseUser firebaseUser=FirebaseAuth.getInstance().getCurrentUser();

        DatabaseReference reference= FirebaseDatabase.getInstance().getReference()
                .child("Likes").child(postid);

        //--------------------------//
//        Log.d(" HERE: ","is Liked called");
//        if( imageView.getTag()==null)
//            imageView.setTag("Like");
      //  Log.d(" Onbind: ", (String) imageView.getTag());

        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                if(dataSnapshot.child(firebaseUser.getUid()).exists())
                {
                    imageView.setImageResource(R.drawable.heart);
                    imageView.setTag("liked");
                }
                else {
                    imageView.setImageResource(R.drawable.like);
                    imageView.setTag("like");
                }

                Log.d(" Onbind: ", (String) imageView.getTag());
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

    }




    private void nrLikes(final TextView likes,String postid)
    {
            DatabaseReference reference=FirebaseDatabase.getInstance().getReference()
            .child("Likes").child(postid);
            reference.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    likes.setText(dataSnapshot.getChildrenCount()+ " likes");
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });
    }



    private void publisherInfo(final ImageView image_profile, final TextView username, final TextView publisher, final String userId)
    {
//        if(userId==null)
//            userId
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Users").child(userId);

        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                User user =dataSnapshot.getValue(User.class);
                Glide.with(mContext).load(user.getImageurl()).into(image_profile);
                username.setText(user.getUsername());
                publisher.setText(user.getUsername());
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void isSaved(final String postid, final ImageView imageView)
    {
        FirebaseUser firebaseUser=FirebaseAuth.getInstance().getCurrentUser();

        DatabaseReference reference=FirebaseDatabase.getInstance().getReference()
                .child("Saves").child(firebaseUser.getUid());
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if(dataSnapshot.child(postid).exists()){
                    imageView.setImageResource(R.drawable.business);
                    imageView.setTag("saved");
                }
                else {
                    imageView.setImageResource(R.drawable.save);
                    imageView.setTag("save");
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }
}
