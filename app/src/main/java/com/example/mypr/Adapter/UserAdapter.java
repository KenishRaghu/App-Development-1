package com.example.mypr.Adapter;

import android.content.Context;
import android.content.Intent;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.mypr.MessageActivity;
import com.example.mypr.Model.Chat;
import com.example.mypr.Model.User;
import com.example.mypr.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.List;

public class UserAdapter extends RecyclerView.Adapter<UserAdapter.ViewHolder> {
    public Context context;
    private boolean isChat;

    String lastmessage;

    public UserAdapter(Context context, List<User> users,boolean isChat) {
        this.context = context;
        this.users = users;
        this.isChat=isChat;
    }

    public List<User> users;


    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view= LayoutInflater.from(context).inflate(R.layout.user_item,parent,false);
        return new UserAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        final User user=users.get(position);
        holder.username.setText(user.getUsername());
//        if(TextUtils.isEmpty(user.getImageURL())||user.getImageURL().equals("default"))
//        {
            holder.profile.setImageResource(R.mipmap.ic_launcher);
//        }
//        else
//        {
//            Glide.with(context).load(user.getImageURL()).into(holder.profile);
//        }

        if(isChat)
        {
            lastM(user.getId(),holder.lastmsg);
        }
        else
            holder.lastmsg.setVisibility(View.GONE);

        if(isChat)
        {
            if(user.getStatus().equals("Online"))
            {
                holder.img_on.setVisibility(View.VISIBLE);
                holder.img_off.setVisibility(View.GONE);
            }
            else
            {
                holder.img_off.setVisibility(View.VISIBLE);
                holder.img_on.setVisibility(View.GONE);
            }

        }
        else
        {
            holder.img_on.setVisibility(View.GONE);
            holder.img_off.setVisibility(View.GONE);
        }

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(context, MessageActivity.class);
                intent.putExtra("userid",user.getId());
                context.startActivity(intent);

            }
        });

    }

    @Override
    public int getItemCount() {
        return users.size();
    }
    public class ViewHolder extends RecyclerView.ViewHolder
    {
        TextView username;
        ImageView profile;
        ImageView img_on;
        ImageView img_off;
        TextView lastmsg;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            username=itemView.findViewById(R.id.username);
            profile=itemView.findViewById(R.id.profile_image);
            img_off=itemView.findViewById(R.id.img_off);
            img_on=itemView.findViewById(R.id.img_on);
            lastmsg=itemView.findViewById(R.id.last_msg);
        }
    }

    private  void lastM(final String userid, final TextView lastm)
    {
        lastmessage="default";
        final FirebaseUser firebaseUser= FirebaseAuth.getInstance().getCurrentUser();
        DatabaseReference reference= FirebaseDatabase.getInstance().getReference("Chats");
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                for(DataSnapshot snapshot:dataSnapshot.getChildren())
                {
                    Chat chat=snapshot.getValue(Chat.class);
                    assert firebaseUser != null;
                    assert chat != null;
                    if(chat.getReceiver().equals(firebaseUser.getUid())&&chat.getSender().equals(userid)||chat.getReceiver().equals(userid)&&chat.getSender().equals(firebaseUser.getUid()))
                    {
                        lastmessage=chat.getMessage();
                    }
                }

                switch (lastmessage)
                {
                    case "default":
                        lastm.setText("No Message");
                        break;
                    default:
                        lastm.setText(lastmessage);
                        break;
                }

                lastmessage="default";
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

}

