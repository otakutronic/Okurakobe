package mji.tapia.com.service.connectivity;


import io.reactivex.Single;

public interface NetworkUtils {

    Single<Boolean> isConnectedToInternet();

    Single<NetworkData> getActiveNetworkData();
}