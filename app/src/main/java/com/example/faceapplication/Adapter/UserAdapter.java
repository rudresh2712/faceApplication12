package com.example.faceapplication.Adapter;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.faceapplication.Fragment.ProfileFragment;
import com.example.faceapplication.MainActivity;
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

import de.hdodenhof.circleimageview.CircleImageView;

import static com.example.faceapplication.R.layout.user_item;

public class UserAdapter extends RecyclerView.Adapter<UserAdapter.ViewHolder>{


    private Context mContext;
    private List<User> mUsers;
    private boolean isFrag;

    private FirebaseUser firebaseUser;

    public UserAdapter(Context mContext, List<User> mUsers,boolean isFrag) {
        this.mContext = mContext;
        this.mUsers = mUsers;
        this.isFrag=isFrag;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int viewType) {

        View view= LayoutInflater.from(mContext)
                .inflate(user_item, viewGroup,false);

        return new UserAdapter.ViewHolder(view);
    }




    @Override
    public void onBindViewHolder(@NonNull final ViewHolder viewHolder, int i) {

        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        //   NOTE THAT THESE ARE THE PASSED PARAMETERS AND NOT KEYWORDS/FUNCTIONS

        final User user=mUsers.get(i);

        viewHolder.btn_follow.setVisibility(View.VISIBLE);

        viewHolder.username.setText(user.getUsername());
        viewHolder.fullname.setText(user.getFullname());
        Glide.with(mContext).load(user.getImageurl()).into(viewHolder.image_profile);
        isFollowing(user.getId(),viewHolder.btn_follow);

        if(user.getId().equals(firebaseUser.getUid()))
            viewHolder.btn_follow.setVisibility(View.GONE);

        viewHolder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (isFrag) {
                    SharedPreferences.Editor editor = mContext.getSharedPreferences("PREFS", Context.MODE_PRIVATE)
                            .edit();
                    editor.putString("profileid", user.getId());
                    editor.apply();

                    ((FragmentActivity) mContext).getSupportFragmentManager().beginTransaction()
                            .replace(R.id.fragment_container, new ProfileFragment()).commit();
                } else {
                    Intent in = new Intent(mContext, MainActivity.class);
                    in.putExtra("publisherid", user.getId());
                    mContext.startActivity(in);

                }
            }
        });

        viewHolder.btn_follow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if(viewHolder.btn_follow.getText().toString().equals("follow"))
                {
                    FirebaseDatabase.getInstance().getReference().child("Follow").child(firebaseUser.getUid())
                            .child("following").child(user.getId()).setValue(true);
                    FirebaseDatabase.getInstance().getReference().child("Follow").child(user.getId())
                            .child("followers").child(firebaseUser.getUid()).setValue(true);

                    addNotification(user.getId());
                }
                else{
                    FirebaseDatabase.getInstance().getReference().child("Follow").child(firebaseUser.getUid())
                            .child("following").child(user.getId()).removeValue();
                    FirebaseDatabase.getInstance().getReference().child("Follow").child(user.getId())
                            .child("followers").child(firebaseUser.getUid()).removeValue();
                }

            }
        });
    }



    @Override
    public int getItemCount() {
        return mUsers.size();
    }

    private void addNotification(String userid ) {

        DatabaseReference reference=FirebaseDatabase.getInstance().getReference("Notification")
                .child(userid);

        HashMap<String,Object> hashmap=new HashMap<>();
        hashmap.put("userid",firebaseUser.getUid());
        hashmap.put("text","started following you ");
        hashmap.put("postid","");
        hashmap.put("ispost",false);

        reference.push().setValue(hashmap);


    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        public TextView username,fullname;
        public CircleImageView image_profile;
        public Button btn_follow;


        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            username=itemView.findViewById(R.id.username);
            fullname=itemView.findViewById(R.id.fullname);
            image_profile=itemView.findViewById(R.id.image_profile);
            btn_follow=itemView.findViewById(R.id.btn_follow);
        }
    }

    private void isFollowing(final String userid,final Button button)
    {
        DatabaseReference reference= FirebaseDatabase.getInstance().getReference()
                    .child("Follow").child(firebaseUser.getUid()).child("following");

        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                if(dataSnapshot.child(userid).exists())
                    button.setText("following");
                else
                    button.setText("follow");
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }
}
