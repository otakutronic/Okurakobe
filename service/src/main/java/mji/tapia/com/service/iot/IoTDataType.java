package mji.tapia.com.service.iot;

import java.util.Date;

/**
 * Created by Sami on 2/5/2018.
 */

public abstract class IoTDataType<V> {

    V data;
    public IoTDataType(V data) {
        if(data instanceof Boolean ||
                data instanceof Integer ||
                data instanceof String ||
                data instanceof Date ||
                data instanceof byte[]
                ) {
            this.data = data;
        } else {
            throw new RuntimeException("unsupported type for iot message");
        }
    }

}
