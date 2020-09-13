package com.example.mypr.Adapter;

import android.content.Context;
import android.content.Intent;
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

import java.util.List;

public class MessageAdapter extends RecyclerView.Adapter<MessageAdapter.ViewHolder> {
    public Context context;
    private String imageURL;
    public List<Chat> mChat;

    public static final int msgr=1;
    public static final int msgl=0;

    FirebaseUser firebaseUser;

    public MessageAdapter(Context context, List<Chat> mChat,String imageURL) {
        this.context = context;
        this.mChat = mChat;
        this.imageURL=imageURL;
    }




    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        if(viewType==msgr) {
            View view = LayoutInflater.from(context).inflate(R.layout.chat_item_right, parent, false);
            return new MessageAdapter.ViewHolder(view);
        }
        else
        {
            View view = LayoutInflater.from(context).inflate(R.layout.chat_item_left, parent, false);
            return new MessageAdapter.ViewHolder(view);
        }

    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {

        Chat chat=mChat.get(position);

        holder.show_message.setText(chat.getMessage());

        if(imageURL.equals("default"))
            holder.profile.setImageResource(R.mipmap.ic_launcher);
        else
            Glide.with(context).load(imageURL).into(holder.profile);

        if(mChat.size()-1==position)
        {
            if(chat.isIsseen())
            {
                holder.seenmsg.setText("Seen");
            }
            else
                holder.seenmsg.setText("Delivered");
        }
        else
            holder.seenmsg.setVisibility(View.GONE);

    }

    @Override
    public int getItemCount() {
        return mChat.size();
    }
    public class ViewHolder extends RecyclerView.ViewHolder
    {
        TextView show_message;
        ImageView profile;
        TextView seenmsg;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            show_message=itemView.findViewById(R.id.show_message);
            profile=itemView.findViewById(R.id.profile_image);
            seenmsg=itemView.findViewById(R.id.seen_msg);
        }
    }

    @Override
    public int getItemViewType(int position) {

        firebaseUser= FirebaseAuth.getInstance().getCurrentUser();
        if(mChat.get(position).getSender().equals(firebaseUser.getUid()))
            return msgr;
        else
        return msgl;
    }
}


