package com.prince_kumar_chaprana.mbbsapp;

import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;

import java.util.ArrayList;

public class ChatArrayAdapter extends BaseAdapter {

    private Activity mActivity;
    DatabaseReference databaseReference;
    String Aname;
    ArrayList<DataSnapshot> mSnapShot;

    //retrieve chat message
    private ChildEventListener mListener = new ChildEventListener() {
        @Override
        public void onChildAdded(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {

            mSnapShot.add(snapshot);
            notifyDataSetChanged();
        }

        @Override
        public void onChildChanged(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {

        }

        @Override
        public void onChildRemoved(@NonNull DataSnapshot snapshot) {

        }

        @Override
        public void onChildMoved(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {

        }

        @Override
        public void onCancelled(@NonNull DatabaseError error) {

        }
    };

    public ChatArrayAdapter(Activity mActivity, DatabaseReference databaseReference, String name) {
        this.mActivity = mActivity;
        this.databaseReference = databaseReference.child("messages");
        mSnapShot = new ArrayList<>();
        this.Aname = name;
        this.databaseReference.addChildEventListener(mListener);
    }

    static class ViewHolder{
        TextView authorName;
        TextView body;
        LinearLayout.LayoutParams params;
    }

    @Override
    public int getCount() {
        return mSnapShot.size();
    }

    @Override
    public InstantMessage getItem(int i) {
        DataSnapshot snapshot = mSnapShot.get(i);
        //convet to class and return it
        return snapshot.getValue(InstantMessage.class);
    }

    @Override
    public long getItemId(int i) {
        return 0;
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        if(view==null){
            LayoutInflater inflator = (LayoutInflater) mActivity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            view = inflator.inflate(R.layout.chat_msg_row,viewGroup,false);
            final ViewHolder holder = new ViewHolder();
            holder.authorName = (TextView) view.findViewById(R.id.author);
            holder.body = (TextView) view.findViewById(R.id.message);
            holder.params = (LinearLayout.LayoutParams) holder.authorName.getLayoutParams();
            //temparey stor holder so dont load again and again
            view.setTag(holder);
        }

        final InstantMessage message = getItem(i);
        //to reuse old holder
        final  ViewHolder holder = (ViewHolder) view.getTag();
        String name = message.getAuthor();
        holder.authorName.setText(name);

        boolean isMe = message.getAuthor().equals(Aname);
        setChatRowAppearance(isMe,holder);
        String msg = message.getMessage();
        holder.body.setText(msg);

        return view;
    }

    void setChatRowAppearance(boolean isItme,ViewHolder holder){
        if(isItme){
            holder.params.gravity = Gravity.END;
            holder.authorName.setTextColor(Color.GREEN);
            holder.body.setBackgroundResource(R.drawable.bubble1);
        }else{
            holder.params.gravity = Gravity.START;
            holder.authorName.setTextColor(Color.BLUE);
            holder.body.setBackgroundResource(R.drawable.bubble2);
        }

        holder.authorName.setLayoutParams(holder.params);
        holder.body.setLayoutParams(holder.params);
    }

    //stop chackign for database
    public void cleanup(){
        databaseReference.removeEventListener(mListener);
    }
}
