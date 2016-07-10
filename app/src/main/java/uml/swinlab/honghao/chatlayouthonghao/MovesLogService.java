package uml.swinlab.honghao.chatlayouthonghao;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.util.Log;
import android.widget.Toast;

import com.midhunarmid.movesapi.MovesAPI;
import com.midhunarmid.movesapi.MovesHandler;
import com.midhunarmid.movesapi.activity.ActivityData;
import com.midhunarmid.movesapi.auth.AuthData;
import com.midhunarmid.movesapi.segment.SegmentData;
import com.midhunarmid.movesapi.storyline.StorylineData;
import com.midhunarmid.movesapi.util.MovesStatus;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Timer;
import java.util.TimerTask;

/**
 * Created by Honghao on 7/6/2016.
 */
public class MovesLogService extends Service {
    private String TAG = "Moves Service";
    private Timer timer;
    private TimerTask getTimeline;
    MovesHandler<ArrayList<StorylineData>> storylineHandler;

    @Override
    public void onCreate() {
        super.onCreate();

        Log.d(TAG, "onCreate");
        timer = new Timer();
        /**
        try {
            MovesAPI.init(this, Constant.CLIENT_ID, Constant.clientSecret, Constant.MOVES_SCOPES, Constant.REDIRECT_URI);
        } catch (Exception e) {
            e.printStackTrace();
        }

        MovesHandler<AuthData> authDialogHandler = new MovesHandler<AuthData>() {
            @Override
            public void onSuccess(AuthData result) {

            }

            @Override
            public void onFailure(MovesStatus status, String message) {

            }
        };
        MovesAPI.authenticate(authDialogHandler,this);
         **/
        storylineHandler = new MovesHandler<ArrayList<StorylineData>>() {
            @Override
            public void onSuccess(final ArrayList<StorylineData> result) {
                Log.d(TAG, "result:" + result.toString());
                ArrayList<SegmentData> segment = result.get(0).getSegments();
                for(int i=0; i<segment.size(); i++) {
                    ArrayList<ActivityData> activities = segment.get(i).getActivities();

                }
            }

            @Override
            public void onFailure(MovesStatus status, String message) {
                Log.e(TAG, "Request Failed! \n"
                        + "Status Code : " + status + "\n"
                        + "Status Message : " + message + "\n\n"
                        + "Specific Message : " + status.getStatusMessage());
            }
        };

        getTimeline = new TimerTask() {
            @Override
            public void run() {
                MovesAPI.getStoryline_SingleDay(storylineHandler, getFormattedDate(), null, false);
            }
        };

        timer.schedule(getTimeline, 5000, 10000);
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    public String getFormattedDate(){
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyyMMdd");
        return dateFormat.format(Calendar.getInstance().getTime());
    }
}
