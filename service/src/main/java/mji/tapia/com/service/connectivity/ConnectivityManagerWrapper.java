package mji.tapia.com.service.connectivity;

public interface ConnectivityManagerWrapper {

    boolean isConnectedToNetwork();

    NetworkData getNetworkData();
}
