package mji.tapia.com.service.iot.bluetooth;

import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.os.Bundle;
import android.os.Message;
import android.util.Log;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.UUID;

import io.reactivex.Completable;
import io.reactivex.Observable;
import io.reactivex.subjects.BehaviorSubject;
import io.reactivex.subjects.CompletableSubject;
import io.reactivex.subjects.PublishSubject;

/**
 * Created by Sami on 9/26/2017.
 */

public class BluetoothSerialServiceImpl implements BluetoothSerialService{

    private static final String TAG = "BtReadServiceImpl";

    private static final boolean D = true;

    private static final UUID SerialPortServiceClass_UUID = UUID.fromString("00001101-0000-1000-8000-00805F9B34FB");

    private ConnectThread mConnectThread;

    private ConnectedThread mConnectedThread;

    private int mState;

    private boolean mAllowInsecureConnections;

    private PublishSubject<byte[]> readPublishSubject = PublishSubject.create();

    private BehaviorSubject<ConnectionStatus> connectionStatusBehaviorSubject = BehaviorSubject.create();

    private CompletableSubject completableSubject;

    public BluetoothSerialServiceImpl(){
        connectionStatusBehaviorSubject.onNext(ConnectionStatus.DISCONNECTED);
    }


    @Override
    public Completable connect(BluetoothDevice bluetoothDevice) {
        completableSubject = CompletableSubject.create();
        if (D) Log.d(TAG, "connect to: " + bluetoothDevice);

        // Cancel any thread attempting to make a connection
        if (connectionStatusBehaviorSubject.getValue() == ConnectionStatus.CONNECTING) {
            if (mConnectThread != null) {
                mConnectThread.cancel();
                mConnectThread = null;
            }
        }

        // Cancel any thread currently running a connection
        if (mConnectedThread != null) {mConnectedThread.cancel(); mConnectedThread = null;}

        // Start the thread to connect with the given device
        mConnectThread = new ConnectThread(bluetoothDevice);
        mConnectThread.start();
        connectionStatusBehaviorSubject.onNext(ConnectionStatus.CONNECTING);

        return completableSubject;
    }

    @Override
    public Observable<ConnectionStatus> connectionStatusChange() {
        return connectionStatusBehaviorSubject;
    }

    @Override
    public Completable write(byte[] out) {
        // Create temporary object
        ConnectedThread r;
        // Synchronize a copy of the ConnectedThread
        synchronized (this) {
            if (connectionStatusBehaviorSubject.getValue() != ConnectionStatus.CONNECTED) return null;//TODO return a custom exception
            r = mConnectedThread;
        }
        // Perform the write unsynchronized
        r.write(out);
        return null;
    }

    @Override
    public Observable<byte[]> read() {
        return readPublishSubject;
    }

    /**
     * Start the ConnectedThread to begin managing a Bluetooth connection
     * @param socket  The BluetoothSocket on which the connection was made
     * @param device  The BluetoothDevice that has been connected
     */
    public synchronized void connected(BluetoothSocket socket, BluetoothDevice device) {
        if (D) Log.d(TAG, "connected");

        // Cancel the thread that completed the connection
        if (mConnectThread != null) {
            mConnectThread.cancel();
            mConnectThread = null;
        }

        // Cancel any thread currently running a connection
        if (mConnectedThread != null) {
            mConnectedThread.cancel();
            mConnectedThread = null;
        }

        // Start the thread to manage the connection and perform transmissions
        mConnectedThread = new ConnectedThread(socket);
        mConnectedThread.start();
        SocketListeningThread socketListeningThread = new SocketListeningThread(socket);
        socketListeningThread.start();
        connectionStatusBehaviorSubject.onNext(ConnectionStatus.CONNECTED);
    }

    /**
     * Stop all threads
     */
    @Override
    public synchronized void stop() {
        if (D) Log.d(TAG, "stop");


        if (mConnectThread != null) {
            mConnectThread.cancel();
            mConnectThread = null;
        }

        if (mConnectedThread != null) {
            mConnectedThread.cancel();
            mConnectedThread = null;
        }
        connectionStatusBehaviorSubject.onNext(ConnectionStatus.DISCONNECTED);
    }


    private class ConnectThread extends Thread {
        private final BluetoothSocket mmSocket;
        private final BluetoothDevice mmDevice;

        public ConnectThread(BluetoothDevice device) {
            mmDevice = device;
            BluetoothSocket tmp = null;

            // Get a BluetoothSocket for a connection with the
            // given BluetoothDevice
            try {
//                if ( mAllowInsecureConnections ) {
                    Method method;

                    method = device.getClass().getMethod("createRfcommSocket", new Class[] { int.class } );
                    tmp = (BluetoothSocket) method.invoke(device, 1);
//                }
//                else {
//                    tmp = device.createRfcommSocketToServiceRecord( SerialPortServiceClass_UUID );
//                }
            } catch (Exception e) {
                Log.e(TAG, "create() failed", e);
                completableSubject.onError(e);
            }
            mmSocket = tmp;
        }

        public void run() {
            Log.i(TAG, "BEGIN mConnectThread");
            setName("ConnectThread");

            // Make a connection to the BluetoothSocket
            try {
                // This is a blocking call and will only return on a
                // successful connection or an exception
                mmSocket.connect();
            } catch (IOException e) {
                if(completableSubject.hasObservers()) {
                    completableSubject.onError(e);
                }
                connectionStatusBehaviorSubject.onNext(ConnectionStatus.DISCONNECTED);
                // Close the socket
                try {
                    mmSocket.close();
                } catch (IOException e2) {
                    Log.e(TAG, "unable to close() socket during connection failure", e2);
                    completableSubject.onError(e2);
                }
                // Start the service over to restart listening mode
                //BluetoothSerialService.this.start();
                return;
            }

            // Reset the ConnectThread because we're done
            synchronized (BluetoothSerialServiceImpl.this) {
                mConnectThread = null;
            }

            completableSubject.onComplete();
            // Start the connected thread
            connected(mmSocket, mmDevice);
        }

        public void cancel() {
            try {
                mmSocket.close();
            } catch (IOException e) {
                Log.e(TAG, "close() of connect socket failed", e);
            }
        }
    }

    /**
     * This thread runs during a connection with a remote device.
     * It handles all incoming and outgoing transmissions.
     */
    private class ConnectedThread extends Thread {
        private final BluetoothSocket mmSocket;
        private final InputStream mmInStream;
        private final OutputStream mmOutStream;


        public ConnectedThread(BluetoothSocket socket) {
            Log.d(TAG, "create ConnectedThread");
            mmSocket = socket;
            InputStream tmpIn = null;
            OutputStream tmpOut = null;

            // Get the BluetoothSocket input and output streams
            try {
                tmpIn = socket.getInputStream();
                tmpOut = socket.getOutputStream();
            } catch (IOException e) {
                Log.e(TAG, "temp sockets not created", e);
            }

            mmInStream = tmpIn;
            mmOutStream = tmpOut;
        }

        public void run() {
            Log.i(TAG, "BEGIN mConnectedThread");
            byte[] buffer = new byte[1024];
            int bytes;

            // Keep listening to the InputStream while connected
            while (true) {
                Log.e(TAG, "connected : " + mmSocket.isConnected());
                if(!mmSocket.isConnected()) {
                    Log.e(TAG, "disconnected");
                    connectionStatusBehaviorSubject.onNext(ConnectionStatus.DISCONNECTED);
                    return;
                }
                try {
                    // Read from the InputStream
                    bytes = mmInStream.read(buffer);
                    byte[] byteArray = Arrays.copyOfRange(buffer, 0, bytes);
                    Log.e(TAG, "got byte: " + new String(byteArray));
                    readPublishSubject.onNext(byteArray);
                    // Send the obtained bytes to the UI Activity
                    //mHandler.obtainMessage(BlueTerm.MESSAGE_READ, bytes, -1, buffer).sendToTarget();
                } catch (IOException e) {
                    Log.e(TAG, "disconnected", e);
                    connectionStatusBehaviorSubject.onNext(ConnectionStatus.DISCONNECTED);
                    break;
                }
            }
        }

        /**
         * Write to the connected OutStream.
         * @param buffer  The bytes to write
         */
        public void write(byte[] buffer) {
            try {
                mmOutStream.write(buffer);

                // Share the sent message back to the UI Activity
//                mHandler.obtainMessage(BlueTerm.MESSAGE_WRITE, buffer.length, -1, buffer)
//                        .sendToTarget();
            } catch (IOException e) {
                Log.e(TAG, "Exception during write", e);
            }
        }

        public void cancel() {
            try {
                mmSocket.close();
            } catch (IOException e) {
                Log.e(TAG, "close() of connect socket failed", e);
            }
        }
    }

    private class SocketListeningThread extends Thread {

        private final BluetoothSocket mmSocket;



        public SocketListeningThread(BluetoothSocket socket) {
            mmSocket = socket;
        }

        @Override
        public void run() {

            while (mmSocket.isConnected()) {
                Log.e(TAG, "connected");
                try {
                    sleep(1000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }


            connectionStatusBehaviorSubject.onNext(ConnectionStatus.DISCONNECTED);
        }
    }
}
