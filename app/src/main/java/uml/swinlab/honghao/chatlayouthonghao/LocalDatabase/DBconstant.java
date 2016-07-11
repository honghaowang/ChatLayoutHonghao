package uml.swinlab.honghao.chatlayouthonghao.LocalDatabase;

/**
 * Created by Honghao on 7/10/2016.
 */
public class DBconstant {
    // Database constants
    public static final int DATABASE_VERSION = 1;
    public static final String DATABASE_NAME = "chatLayout";
    public static final String DATABASE_FILE = "chatLayout.db";

    // Table names
    public static final String MOVES_DATA_TABLE = "MovesTable";

    // MESSAGE HISTORY TABLE Attributes
    public static final String DATA_ID = "ID";
    public static final String TIME = "Time";
    public static final String TOTAL_STEP = "TotalStep";
    public static final String DURATION = "Duration";
    public static final String STEP = "Step";
    public static final String ACTIVITY = "Activity";

    // Constants for creating table
    public static final String TEXT_TYPE = " TEXT";
    public static final String INTEGER_TYPE = " INTEGER";
    public static final String COMMA_SEP = ",";

    // MESSAGE HISTORY TABLE
    public static final String CREATE_MOVES_DATA_TABLE =
            "CREATE TABLE IF NOT EXISTS " + MOVES_DATA_TABLE + " (" +
                    //DATA_ID + INTEGER_TYPE + COMMA_SEP +
                    TIME + TEXT_TYPE + COMMA_SEP +
                    TOTAL_STEP + INTEGER_TYPE + //COMMA_SEP +
                    //DURATION + INTEGER_TYPE + COMMA_SEP +
                    //STEP + INTEGER_TYPE + COMMA_SEP +
                    //ACTIVITY + TEXT_TYPE +
                    " )";

    private static final String DELETE_MOVES_DATA_TABLE =
            "DROP TABLE IF EXISTS " + MOVES_DATA_TABLE;
}
