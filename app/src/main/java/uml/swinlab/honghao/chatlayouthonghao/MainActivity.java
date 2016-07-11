package uml.swinlab.honghao.chatlayouthonghao;

import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
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
import android.widget.Toast;

import com.midhunarmid.movesapi.MovesAPI;
import com.midhunarmid.movesapi.MovesHandler;
import com.midhunarmid.movesapi.auth.AuthData;
import com.midhunarmid.movesapi.storyline.StorylineData;
import com.midhunarmid.movesapi.summary.SummaryData;
import com.midhunarmid.movesapi.util.MovesStatus;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.concurrent.ExecutionException;

import uml.swinlab.honghao.chatlayouthonghao.LocalDatabase.DBconstant;
import uml.swinlab.honghao.chatlayouthonghao.LocalDatabase.MovesDBHelper;

public class MainActivity extends AppCompatActivity {

    private EditText messageET;
    private ListView messagesContainer;
    private Button sendBtn;
    private ChatAdapter adapter;
    private ArrayList<ChatMessage> chatHistory;
    private String TAG = "MainActivity";
    private int textID = 1;
    private String deviceID;
    SQLiteDatabase db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        deviceID = Settings.Secure.getString(getContentResolver(), Settings.Secure.ANDROID_ID);
        initControls();

        db = new MovesDBHelper(this).getReadableDatabase();
        try {
            MovesAPI.init(this, Constant.CLIENT_ID, Constant.clientSecret, Constant.MOVES_SCOPES, Constant.REDIRECT_URI);
        } catch (Exception e) {
            e.printStackTrace();
        }

        MovesHandler<AuthData> authDialogHandler = new MovesHandler<AuthData>() {
            @Override
            public void onSuccess(AuthData result) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(MainActivity.this, "Authenticated Successfully", Toast.LENGTH_LONG).show();
                    }
                });
            }

            @Override
            public void onFailure(MovesStatus status, String message) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(MainActivity.this, "Authentication Fails", Toast.LENGTH_LONG).show();
                    }
                });
            }
        };
        MovesAPI.authenticate(authDialogHandler, this);

        Intent intent = new Intent(this, MovesLogService.class);
        startService(intent);

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

                if(messageText.equals("step")){
                    String[] row= {DBconstant.TOTAL_STEP};
                    Cursor cursor = db.query(DBconstant.MOVES_DATA_TABLE, row, null, null, null, null, null, null);
                    if(cursor == null)
                        return;
                    //cursor.moveToLast();
                    String step = cursor.getString(cursor.getCount());
                    ChatMessage chatMessage = new ChatMessage();
                    chatMessage.setId(textID);//dummy
                    chatMessage.setMessage(step);
                    chatMessage.setDate(DateFormat.getDateTimeInstance().format(new Date()));
                    chatMessage.setMe(false);
                    displayMessage(chatMessage);
                    textID++;
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
                    if(jsonObject.has("Brand")){
                        brand = jsonObject.getString("Brand");
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

    public String getFormattedDate(){
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyyMMdd");
        return dateFormat.format(Calendar.getInstance().getTime());
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
                Log.d(TAG, "Text--->" + params[0]);
                Log.d(TAG, "User--->" + deviceID);
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
