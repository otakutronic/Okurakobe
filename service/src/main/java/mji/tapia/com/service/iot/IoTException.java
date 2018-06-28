package mji.tapia.com.service.iot;

/**
 * Created by Sami on 9/25/2017.
 */

public class IoTException extends Exception{

    static public final int SERVICE_NOT_ENABLED = 0;
    static public final int SERVICE_NOT_CONNECTED = 1;
    static public final int MESSAGE_TIMEOUT = 2;
    static public final int WRONG_INPUT = 3;

    private int code;

    IoTException(int code) {
        this.code = code;
    }

    int getErrorCode(){
        return code;
    }
}
