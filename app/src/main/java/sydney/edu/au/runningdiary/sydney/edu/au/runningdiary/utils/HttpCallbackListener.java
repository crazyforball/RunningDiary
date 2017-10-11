package sydney.edu.au.runningdiary.sydney.edu.au.runningdiary.utils;

/**
 * Created by yang on 9/28/17.
 */

public interface HttpCallbackListener {
    void onFinish(String response);
    void onError(Exception e);
}
