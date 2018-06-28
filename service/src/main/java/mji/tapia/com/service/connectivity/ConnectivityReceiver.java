package mji.tapia.com.service.connectivity;


import io.reactivex.Observable;
import io.reactivex.Single;

public interface ConnectivityReceiver {

    Observable<Boolean> getConnectivityStatus();

    Single<Boolean> isConnected();
}
