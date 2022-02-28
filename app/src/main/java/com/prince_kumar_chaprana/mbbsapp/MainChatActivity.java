package com.prince_kumar_chaprana.mbbsapp;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserInfo;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;


public class MainChatActivity extends AppCompatActivity {

    // TODO: Add member variables here:
    private String mDisplayName;
    private ListView mChatListView;
    private EditText mInputText;
    private ImageButton mSendButton;
    ChatArrayAdapter mAdapter;

    DatabaseReference mDataReference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_chat);

        // TODO: Set up the display name and get the Firebase reference
        setDisplayName();
        mDataReference = FirebaseDatabase.getInstance("https://mbbschatapp-default-rtdb.asia-southeast1.firebasedatabase.app").getReference();
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        UserInfo profile = user.getProviderData().get(0); // getting the first one bc this is a list
        String uid = profile.getUid();
        Log.d("FlashChat", "User id is: " + uid);


        // Link the Views in the layout to the Java code
        mInputText = (EditText) findViewById(R.id.messageInput);
        mSendButton = (ImageButton) findViewById(R.id.sendButton);
        mChatListView = (ListView) findViewById(R.id.chat_list_view);

        // TODO: Send the message when the "enter" button is pressed
        mInputText.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView textView, int i, KeyEvent keyEvent) {
                sendMessage();
                return true;
            }
        });

        // TODO: Add an OnClickListener to the sendButton to send a message
        mSendButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                sendMessage();
            }
        });
    }

    // TODO: Retrieve the display name from the Shared Preferences


    public void setDisplayName() {
        SharedPreferences pref = getSharedPreferences(RegisterActivity.CHAT_PREFS,MODE_PRIVATE);
        mDisplayName = pref.getString(RegisterActivity.DISPLAY_NAME_KEY,null);
        if(mDisplayName==null) mDisplayName = "Anonymous";
    }

    private void sendMessage() {

        // TODO: Grab the text the user typed in and push the message to Firebase
        String input = mInputText.getText().toString();
        if(!input.equals("")){
            InstantMessage chat = new InstantMessage(input,mDisplayName);
            mDataReference.child("messages").push().setValue(chat);
            mInputText.setText("");
            Log.d("App","message sent");

        }
    }

    // TODO: Override the onStart() lifecycle method. Setup the adapter here.


    @Override
    protected void onStart() {
        super.onStart();
        mAdapter = new ChatArrayAdapter(this,mDataReference,mDisplayName);
        mChatListView.setAdapter(mAdapter);
    }

    @Override
    public void onStop() {
        super.onStop();

        // TODO: Remove the Firebase event listener on the adapter.
        mAdapter.cleanup();
    }

}
