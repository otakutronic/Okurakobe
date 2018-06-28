package mji.tapia.com.okurahotel;

public interface ScopedPresenter {

    void start();

    void activate();

    void deactivate();

    void destroy();

    void back();
}
