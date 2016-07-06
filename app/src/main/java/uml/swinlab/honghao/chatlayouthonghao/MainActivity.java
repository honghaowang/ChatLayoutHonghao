package uml.swinlab.honghao.chatlayouthonghao;

import android.os.AsyncTask;
import android.provider.Settings;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.text.DateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.concurrent.ExecutionException;

public class MainActivity extends AppCompatActivity {

    private EditText messageET;
    private ListView messagesContainer;
    private Button sendBtn;
    private ChatAdapter adapter;
    private ArrayList<ChatMessage> chatHistory;
    private String TAG = "MainActivity";
    private int textID = 1;
    private String deviceID;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        deviceID = Settings.Secure.getString(getContentResolver(), Settings.Secure.ANDROID_ID);
        initControls();
    }

    private void initControls(){
        messagesContainer = (ListView) findViewById(R.id.messagesContainer);
        messageET = (EditText) findViewById(R.id.messageEdit);
        sendBtn = (Button) findViewById(R.id.chatSendButton);

        TextView meLabel = (TextView) findViewById(R.id.meLbl);
        TextView companionLabel = (TextView) findViewById(R.id.friendLabel);
        RelativeLayout container = (RelativeLayout) findViewById(R.id.container);
        companionLabel.setText("Food Agent");

        loadDummyHistory();

        sendBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String messageText = messageET.getText().toString();
                if (TextUtils.isEmpty(messageText)) {
                    return;
                }

                ChatMessage chatMessage = new ChatMessage();
                chatMessage.setId(textID);//dummy
                chatMessage.setMessage(messageText);
                chatMessage.setDate(DateFormat.getDateTimeInstance().format(new Date()));
                chatMessage.setMe(true);

                messageET.setText("");

                displayMessage(chatMessage);
                textID++;
                String res=null;
                String brand = null, food = null;
                try {
                     res = new Send07().execute(messageText).get();
                    JSONObject jsonObject = new JSONObject(res);
                    if(jsonObject.has("Manufacture")){
                        brand = jsonObject.getString("Manufacture");
                    }
                    if(jsonObject.has("Food")){
                        food = jsonObject.getString("Food");
                    }
                    res = "Food: " + food + "\n" + "Manufacture: " + brand;
                } catch (InterruptedException e){
                    e.printStackTrace();
                } catch (ExecutionException e){
                    e.printStackTrace();
                } catch (JSONException e){
                    e.printStackTrace();
                }

                ChatMessage resMess = new ChatMessage();
                resMess.setId(textID);//dummy
                resMess.setMessage(res);
                resMess.setDate(DateFormat.getDateTimeInstance().format(new Date()));
                resMess.setMe(false);

                displayMessage(resMess);
                textID++;
            }
        });
    }

    public void displayMessage(ChatMessage message) {
        adapter.add(message);
        adapter.notifyDataSetChanged();
        scroll();
    }

    private void scroll() {
        messagesContainer.setSelection(messagesContainer.getCount() - 1);
    }

    private void loadDummyHistory(){

        chatHistory = new ArrayList<ChatMessage>();

        //Set message from agent
        ChatMessage msg = new ChatMessage();
        msg.setId(textID);
        msg.setMe(false);
        msg.setMessage("Hi");
        msg.setDate(DateFormat.getDateTimeInstance().format(new Date()));
        chatHistory.add(msg);
        textID++;
        ChatMessage msg1 = new ChatMessage();
        msg1.setId(textID);
        msg1.setMe(false);
        msg1.setMessage("What did you eat?");
        msg1.setDate(DateFormat.getDateTimeInstance().format(new Date()));
        chatHistory.add(msg1);
        textID++;
        adapter = new ChatAdapter(MainActivity.this, new ArrayList<ChatMessage>());
        messagesContainer.setAdapter(adapter);

        for(int i=0; i<chatHistory.size(); i++) {
            ChatMessage message = chatHistory.get(i);
            displayMessage(message);
        }

    }

    public class Send07 extends AsyncTask<String, Void, String> {

        @Override
        protected String doInBackground(String... params) {
            String TAG = "Send07";
            String jsonStr = null;
            try {
                Log.d(TAG, params[0]);
                Log.d(TAG, deviceID);
                HttpResponse response = null;
                HttpClient client = new DefaultHttpClient();
                String swin07URL = "http://swin07.cs.uml.edu:8080/";
                HttpGet get = new HttpGet(swin07URL);
                get.setHeader("User", deviceID);
                get.setHeader("Text", params[0]);
                response = client.execute(get);
                jsonStr = EntityUtils.toString(response.getEntity());
                Log.e(TAG, String.valueOf(response.getStatusLine().getStatusCode()));
                Log.e(TAG, jsonStr);

            } catch (IOException e) {
                e.printStackTrace();
            }
            return jsonStr;
        }

        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);
        }
    }
}
