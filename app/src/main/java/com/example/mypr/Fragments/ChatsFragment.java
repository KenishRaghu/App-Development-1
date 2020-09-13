package com.example.mypr.Fragments;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.mypr.Adapter.UserAdapter;
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
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.messaging.FirebaseMessagingService;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;


public class ChatsFragment extends Fragment {

    private RecyclerView recyclerView;
    private UserAdapter userAdapter;
     FirebaseUser firebaseUser;
    private List<User> users;
 DatabaseReference databaseReference;
    private List<String> userlist;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState)
    {
        View view=inflater.inflate(R.layout.fragment_chats, container, false);

        recyclerView=view.findViewById(R.id.recycler_view);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        firebaseUser= FirebaseAuth.getInstance().getCurrentUser();
        userlist=new ArrayList<>();
        databaseReference= FirebaseDatabase.getInstance().getReference("Chats");
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                userlist.clear();

                for(DataSnapshot snapshot:dataSnapshot.getChildren())
                {
                    Chat chat=snapshot.getValue(Chat.class);
                    assert chat!=null;

                    if(chat.getSender().equals(firebaseUser.getUid()))
                    {
                        userlist.add(chat.getReceiver());
                    }
                    if(chat.getReceiver().equals(firebaseUser.getUid()))
                        userlist.add(chat.getSender());
                }

                Set<String> set=new HashSet<String>(userlist);
                userlist.clear();
                userlist.addAll(set);

                readChats();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

//        updateToken(FirebaseInstanceId.getInstance().getToken());
        return view;



    }

//    private void updateToken(String token)
//    {
//        DatabaseReference reference=FirebaseDatabase.getInstance().getReference("Tokens");
//        Token token1=new Token(token);
//        reference.child(firebaseUser.getUid()).setValue(token);
//    }

    private void readChats()
    {
        users=new ArrayList<>();

        databaseReference=FirebaseDatabase.getInstance().getReference("Users");

        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                //users.clear();

                for(DataSnapshot snapshot:dataSnapshot.getChildren())
                {
                    User user=snapshot.getValue(User.class);

                    for(String id:userlist)
                    {
                        assert user!=null;
                        if(user.getId().equals(id))
                        {
                            users.add(user);
                        }
                    }

                }
                userAdapter=new UserAdapter(getContext(),users,true);
                recyclerView.setAdapter(userAdapter);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }
}
