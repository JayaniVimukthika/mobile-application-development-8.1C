package com.example.llamachatbot;

import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;


import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import okhttp3.OkHttpClient;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import java.util.ArrayList;

public class ChatActivity extends AppCompatActivity {

    private TextView chatHeader;
    private ListView chatListView;
    private EditText messageInput;
    private ImageButton sendButton;
    private ChatAdapter chatAdapter;
    private ArrayList<ChatMessage> chatMessages;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);

        // Set status bar color
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.LOLLIPOP) {
            android.view.Window window = this.getWindow();
            window.addFlags(android.view.WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.setStatusBarColor(this.getResources().getColor(android.R.color.black)); // Change to desired color
        }

        chatHeader = findViewById(R.id.chat_header);
        chatListView = findViewById(R.id.chat_list);
        messageInput = findViewById(R.id.message_input);
        sendButton = findViewById(R.id.send_button);

        chatMessages = new ArrayList<>();
        chatAdapter = new ChatAdapter(this, chatMessages);
        chatListView.setAdapter(chatAdapter);

        String username = getIntent().getStringExtra("username");
        if (username != null) {
            chatHeader.setText("Chatting as " + username);
        } else {
            chatHeader.setText("Chatting as Guest");
        }

//        Retrofit retrofit = new Retrofit.Builder()
//                .baseUrl("http://10.0.2.2:5000/") // Base URL must end with a slash
//                .addConverterFactory(GsonConverterFactory.create())
//                .build();

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("http://10.0.2.2:5000/")
                .addConverterFactory(GsonConverterFactory.create())
                .client(new OkHttpClient.Builder().readTimeout(10, java.util.concurrent.TimeUnit.MINUTES).build()) // this will set the read timeout for 10mins (IMPORTANT: If not your request will exceed the default read timeout)
                .build();

        ChatService chatService = retrofit.create(ChatService.class);



        sendButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String message = messageInput.getText().toString().trim();

                if (!message.isEmpty()) {

                    chatMessages.add(new ChatMessage(message, true));
                    chatAdapter.notifyDataSetChanged();

                    chatService.sendMessage(createJsonBody(message)).enqueue(new Callback<ChatResponse>() {
                        @Override
                        public void onResponse(Call<ChatResponse> call, Response<ChatResponse> response) {

                            if (response.isSuccessful()) {
                                String botResponse = response.body().getResponse();
                                chatMessages.add(new ChatMessage(botResponse, false));
                                chatAdapter.notifyDataSetChanged();
                            }
                        }

                        @Override
                        public void onFailure(Call<ChatResponse> call, Throwable t) {
                            Toast.makeText(ChatActivity.this,"fail",Toast.LENGTH_SHORT).show();
                            chatMessages.add(new ChatMessage("Sorry, I couldn't understand that. Please try again.", false));
                            chatAdapter.notifyDataSetChanged();
                        }
                    });

                    messageInput.setText("");
                }
            }
        });
    }

    public JSONObject createJsonBody(String message) {
        try {
            // Create a JSON object
            JSONObject jsonObject = new JSONObject();

            // Add the userMessage
            jsonObject.put("userMessage", message);

            // Create a JSON array for chat history
            JSONArray chatHistoryArray = new JSONArray();

            jsonObject.put("chatHistory", chatHistoryArray);

            // Convert the JSON object to a string
            return jsonObject;
        } catch (JSONException e) {
            e.printStackTrace();
            // Handle JSON exception
            return null; // or throw an exception depending on your requirements
        }
    }


}
