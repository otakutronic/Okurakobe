package mji.tapia.com.okurahotel.ui.room_manager.light;

import mji.tapia.com.service.iot.light.IoTLightManager;

/**
 * Created by Sami on 9/26/2017.
 */

class IoTLightCommandMapper {

    static Boolean map(IoTLightManager.LightState lightState){
        return lightState.isOn;
    }

    static IoTLightManager.IoTLight map(CommandType commandType){
        switch (commandType) {
            case ROOM:
                return IoTLightManager.IoTLight.ROOM;
            case SPOT:
                return IoTLightManager.IoTLight.SPOT;
            case FOOT:
                return IoTLightManager.IoTLight.FOOT;
            case ALL:
                return IoTLightManager.IoTLight.ALL;
            default:
                return null;
        }
    }
}
