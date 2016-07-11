package uml.swinlab.honghao.chatlayouthonghao;

import android.app.Service;
import android.content.ContentValues;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.util.Log;

import com.midhunarmid.movesapi.MovesAPI;
import com.midhunarmid.movesapi.MovesHandler;
import com.midhunarmid.movesapi.activity.ActivityData;
import com.midhunarmid.movesapi.segment.SegmentData;
import com.midhunarmid.movesapi.storyline.StorylineData;
import com.midhunarmid.movesapi.summary.SummaryData;
import com.midhunarmid.movesapi.util.MovesStatus;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Timer;
import java.util.TimerTask;

import uml.swinlab.honghao.chatlayouthonghao.LocalDatabase.DBconstant;
import uml.swinlab.honghao.chatlayouthonghao.LocalDatabase.MovesDBHelper;

/**
 * Created by Honghao on 7/6/2016.
 */
public class MovesLogService extends Service {
    private String TAG = "Moves Service";
    private Timer timer;
    private TimerTask getTimeline;
    MovesHandler<ArrayList<StorylineData>> storylineHandler;
    private Boolean firstTime = true;
    SQLiteDatabase db;

    @Override
    public void onCreate() {
        super.onCreate();

        Log.d(TAG, "onCreate");
        timer = new Timer();

        db = new MovesDBHelper(this).getWritableDatabase();
        storylineHandler = new MovesHandler<ArrayList<StorylineData>>() {
            @Override
            public void onSuccess(final ArrayList<StorylineData> result) {
                Log.e(TAG, String.valueOf(result.size()));
                if(firstTime) {
                    ArrayList<SegmentData> segments = result.get(0).getSegments();

                    for (int i = 0; i < segments.size(); i++) {
                        ArrayList<ActivityData> activityDatas = segments.get(i).getActivities();
                        for(int j=0; j<segments.size(); j++){
                            ContentValues values = new ContentValues();
                            values.put(DBconstant.TIME, getTime());
                            values.put(DBconstant.TOTAL_STEP, activityDatas.get(j).getSteps());
                            db.insert(DBconstant.MOVES_DATA_TABLE, null, values);
                        }
                    }
                    firstTime = false;
                }
                else {
                    int size = result.get(0).getSegments().size();
                    ContentValues values = new ContentValues();
                    SegmentData lastSegement = result.get(0).getSegments().get(size);
                    size = lastSegement.getActivities().size();
                    ActivityData lastActivity = lastSegement.getActivities().get(size);
                    values.put(DBconstant.TIME, getTime());
                    values.put(DBconstant.TOTAL_STEP, lastActivity.getSteps());
                    db.insert(DBconstant.MOVES_DATA_TABLE, null, values);
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

        timer.schedule(getTimeline, 5000, 600000);
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

    public String getTime(){
        SimpleDateFormat time = new SimpleDateFormat("HH:mm:SS MM-dd-yyyy");
        return time.format(Calendar.getInstance().getTime());
    }
}
