package mji.tapia.com.service.iot;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.util.Log;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import io.reactivex.Completable;
import io.reactivex.Observable;
import io.reactivex.disposables.Disposable;
import io.reactivex.subjects.BehaviorSubject;
import io.reactivex.subjects.CompletableSubject;
import mji.tapia.com.service.iot.air_con.IoTAirConManager;
import mji.tapia.com.service.iot.air_con.IoTAirConManagerImpl;
import mji.tapia.com.service.iot.alarm.IoTAlarmManager;
import mji.tapia.com.service.iot.alarm.IoTAlarmManagerImpl;
import mji.tapia.com.service.iot.bluetooth.BluetoothDiscoveryService;
import mji.tapia.com.service.iot.bluetooth.BluetoothSerialService;
import mji.tapia.com.service.iot.curtain.IoTCurtainManager;
import mji.tapia.com.service.iot.curtain.IoTCurtainManagerImpl;
import mji.tapia.com.service.iot.light.IoTLightManager;
import mji.tapia.com.service.iot.light.IoTLightManagerImpl;
import mji.tapia.com.service.iot.message.IoTMessageManager;
import mji.tapia.com.service.iot.message.IoTMessageManagerImpl;
import mji.tapia.com.service.iot.room_state.IoTRoomStateManager;
import mji.tapia.com.service.iot.room_state.IoTRoomStateManagerImpl;

import static mji.tapia.com.service.iot.bluetooth.BluetoothDiscoveryServiceImpl.IOT_DEVICE_ID;

/**
 * Created by Sami on 2/2/2018.
 */

public class IoTServiceImpl implements IoTService {

    static private final String TAG = "IoTService";

    //map of all managers by tag
    private Map<String,IoTManager> ioTManagers;

    private Boolean isRunning;

    //map all definition for each iotmanager
    private Map<IoTCommand.CommandDefinition, IoTManager> commandDefinitionIoTManagerMap = new HashMap<>();

    private BehaviorSubject<IoTConnectionState> ioTConnectionStateBehaviorSubject = BehaviorSubject.create();

    private BluetoothDiscoveryService bluetoothDiscoveryService;

    private BluetoothSerialService bluetoothSerialService;

    private Disposable onBluetoothPairComplete;

    public IoTServiceImpl(BluetoothDiscoveryService bluetoothDiscoveryService, BluetoothSerialService bluetoothSerialService) {
        this.bluetoothDiscoveryService = bluetoothDiscoveryService;
        this.bluetoothSerialService = bluetoothSerialService;
        ioTManagers = new HashMap<>();
        ioTManagers.put(IoTAirConManager.TAG, new IoTAirConManagerImpl(commandEditor));
        //ioTManagers.put(IoTAlarmManager.TAG, new IoTAlarmManagerImpl(commandEditor));
        ioTManagers.put(IoTCurtainManager.TAG, new IoTCurtainManagerImpl(commandEditor));
        ioTManagers.put(IoTLightManager.TAG, new IoTLightManagerImpl(commandEditor));
        ioTManagers.put(IoTMessageManager.TAG, new IoTMessageManagerImpl(commandEditor));
        ioTManagers.put(IoTRoomStateManager.TAG, new IoTRoomStateManagerImpl(commandEditor));
        isRunning = false;

        for (Map.Entry<String, IoTManager> entry :ioTManagers.entrySet()) {
            List<IoTCommand.CommandDefinition> commandDefinitions = entry.getValue().loadCommands();
            for (IoTCommand.CommandDefinition cd : commandDefinitions) {
                if(commandDefinitionIoTManagerMap.containsKey(cd)) {
                    throw new RuntimeException();
                } else {
                    commandDefinitionIoTManagerMap.put(cd, entry.getValue());
                }
            }
        }
        
        bluetoothSerialService.connectionStatusChange().subscribe(connectionStatus -> {
            switch (connectionStatus){
                case CONNECTED:
                    ioTConnectionStateBehaviorSubject.onNext(IoTService.IoTConnectionState.CONNECTED);
                    updateData();
//                            bluetoothSerialService.write(IoTParser.format(new Pair[]{new Pair("LT6",0), new Pair("LT23",0)}).getBytes());
                    break;
                case DISCONNECTED:
                    ioTConnectionStateBehaviorSubject.onNext(IoTService.IoTConnectionState.DISCONNECTED);
                    break;
                case LISTENING:
                    ioTConnectionStateBehaviorSubject.onNext(IoTService.IoTConnectionState.DISCONNECTED);
                    break;
                case CONNECTING:
                    ioTConnectionStateBehaviorSubject.onNext(IoTConnectionState.CONNECTING);
                    break;
            }
        }, err -> {
            //TODO manage this error
        });

        bluetoothSerialService.read().subscribe(bytes -> {
            Log.e("test receive",new String(bytes));
            IoTCommand.RawCommand[] commands = IoTParser.parse(new String(bytes));
            for (IoTCommand.RawCommand command: commands) {
                handleCommand(command);
            }
        });
    }

    @Override
    public void start() {
        final BluetoothDiscoveryService.IOTDevice iotDevice = bluetoothDiscoveryService.getIOTDevice();
        final String hubAddress = iotDevice.address;

        //connectSerialDevice(hubAddress);

        if(iotDevice.hub != null) {
            onBluetoothPairComplete = getBluetoothDiscoveryManager().devicePairingComplete().subscribe(isFinished -> {
                if(isFinished) {
                    connectSerialDevice(hubAddress);

                    if(onBluetoothPairComplete != null) {
                        onBluetoothPairComplete.dispose();
                        getBluetoothDiscoveryManager().reset();
                    }
                }
            });

            bluetoothDiscoveryService.unpairAllDevices();
            bluetoothDiscoveryService.pairDevice(iotDevice.hub);
        }
    }

    @Override
    public void discoverBluetoothDevice(final String roomNumber) {
        final BluetoothDiscoveryService.IOTDevice iotDevice = bluetoothDiscoveryService.getIOTDevice();
        final String roomID = IOT_DEVICE_ID + roomNumber;
        final boolean isNewID = !roomID.equals(iotDevice.id);

        bluetoothDiscoveryService.setUpIOTDevice(roomNumber);

        // If the bluetooth is not enabled, turns it on.
        if (!bluetoothDiscoveryService.isBluetoothEnabled()) {
            bluetoothDiscoveryService.turnOnBluetoothAndScheduleDiscovery();
        } else {

            //Prevents the user from spamming the button and thus glitching the UI.
            if (!bluetoothDiscoveryService.isDiscovering()) {
                // Starts the discovery.
                bluetoothDiscoveryService.startDiscovery(isNewID);
            } else {
                bluetoothDiscoveryService.cancelDiscovery();
            }
        }
    }

    @Override
    public void connectSerialDevice(final String hubAddress) {
        isRunning = true;

        Log.e("TAG", "hubAddress: " + hubAddress);

        if(hubAddress == null){
            //TODO throw some error
        }
        else {
            List<BluetoothDevice> pairedDevices = bluetoothDiscoveryService.getPairedDevices();

            Log.e("TAG", "pairedDevices size: " + pairedDevices.size());

            BluetoothDevice hubDevice = null;
            for (BluetoothDevice bluetoothDevice: pairedDevices) {
                if(bluetoothDevice.getAddress().equals(hubAddress))
                    hubDevice = bluetoothDevice;
            }

            if(hubDevice != null) {
                bluetoothSerialService.stop();
                bluetoothSerialService.connect(hubDevice).subscribe(() -> {
                    //connection success
                    Log.e("TAG", "connected");
                });
            }
            else {
                Log.e("TAG", "no device found");
                //TODO throw some error
            }
        }
    }

    @Override
    public void restart() {

    }


    private Disposable reconnectionStateDisposable;
    private Disposable reconnectionTimeDisposable;

    @Override
    public void setAutoReconnection(boolean isEnable) {
        if(isEnable) {

        } else {

        }
    }

    @Override
    public boolean isRunning() {
        return isRunning;
    }

    @Override
    public Observable<IoTConnectionState> getConnectionStateObservable() {
        return ioTConnectionStateBehaviorSubject;
    }


    private int pingIndex = 0;
    private HashMap<String, CompletableSubject> pingHashMap = new HashMap<>();

    @Override
    public synchronized Completable pingIoTBox() {
        CompletableSubject completableSubject = CompletableSubject.create();
        pingIndex ++;
        String data = Integer.toString(pingIndex);
        pingHashMap.put(data, completableSubject);
        bluetoothSerialService.write(IoTParser.formatWithValues("POLS", data).getBytes());
        return completableSubject;
    }

    @Override
    public Completable updateData() {
        CompletableSubject completableSubject = CompletableSubject.create();
        Log.e("test send", ("SR\r\n"));
        bluetoothSerialService.write(("SR\r\n").getBytes());
        completableSubject.onComplete();
        return completableSubject;
    }

    @Override
    public IoTAlarmManager getAlarmManager() {
        return (IoTAlarmManager)ioTManagers.get(IoTAlarmManager.TAG);
    }

    @Override
    public IoTAirConManager getAirConManager() {
        return (IoTAirConManager)ioTManagers.get(IoTAirConManager.TAG);
    }

    @Override
    public IoTCurtainManager getCurtainManager() {
        return (IoTCurtainManager)ioTManagers.get(IoTCurtainManager.TAG);
    }

    @Override
    public IoTLightManager getLightManager() {
        return (IoTLightManager)ioTManagers.get(IoTLightManager.TAG);
    }

    @Override
    public IoTRoomStateManager getRoomManager() {
        return (IoTRoomStateManager)ioTManagers.get(IoTRoomStateManager.TAG);
    }

    @Override
    public IoTMessageManager getMessageManager() {
        return (IoTMessageManager)ioTManagers.get(IoTMessageManager.TAG);
    }

    @Override
    public BluetoothDiscoveryService getBluetoothDiscoveryManager() {
        return bluetoothDiscoveryService;
    }

    @Override
    public BluetoothSerialService getBluetoothSerialManager() {
        return bluetoothSerialService;
    }

    private void handleCommand(IoTCommand.RawCommand command) {

        Log.e("TAG", "handleCommand " + command.name);

        switch (command.name) {
            case "POLS":
                //answer ping
                bluetoothSerialService.write(IoTParser.formatWithValues("POLR", command.params.get(0)).getBytes());
                break;
            case "POLR":
                //complete ping
                if(pingHashMap.containsKey(command.params.get(0))) {
                    pingHashMap.get(command.params.get(0)).onComplete();
                }
                break;
            default:
                Log.e("TAG", "default " + command.name);
                IoTCommand.CommandDefinition commandDefinition = null;
                //search for definition
                for (IoTCommand.CommandDefinition cd : commandDefinitionIoTManagerMap.keySet()) {
                    Log.e("TAG", "cd: " + cd);
                    if(cd.equals(command.name)){
                        commandDefinition = cd;
                        break;
                    }
                }
                if(commandDefinition != null) {
                    Log.e("TAG", "commandDefinition != null: " + commandDefinition);
                    //get manager and send data
                    commandDefinitionIoTManagerMap
                            .get(commandDefinition).onCommandUpdate(commandDefinition.applyTo(command));

                }
                break;
        }
    }

    private CommandEditor commandEditor = (name, params) -> {
        CompletableSubject completableSubject = CompletableSubject.create();
        bluetoothSerialService.write(IoTParser.formatWithValues(name, params).getBytes());
        completableSubject.onComplete();
        return completableSubject;
    };
}
