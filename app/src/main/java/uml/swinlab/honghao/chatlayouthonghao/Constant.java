package uml.swinlab.honghao.chatlayouthonghao;

import android.os.Environment;

import java.io.File;

/**
 * Created by Honghao on 5/21/2016.
 */
public class Constant {
    public static final String floderPath = Environment.getExternalStorageDirectory().getPath() + File.separator + "swin.honghao.fitbit";
    public static final String authorFile = floderPath + File.separator + "author.txt";

    //Use at moves authorization
    public static final String CLIENT_ID = "vZfphIR2Q9KL7nh1FMkt30G7Y8aXaHIV";
    public static final String clientSecret = "NciChFs_9pPhtVsgJs8G_t3of3J0uyVVw0ph51EQPJenH_2Jld77PMbbG_Q6Z0u4";
    public static final String REDIRECT_URI = "http://swin06.cs.uml.edu/processdata/";
    public static final int REQUEST_AUTHORIZE = 1;
    public static final String MOVES_SCOPES = "location activity";



    //
    public static final String POST_FILE_URL = "http://swin06.cs.uml.edu/processdata/autotest";
}
