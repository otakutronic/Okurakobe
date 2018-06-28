package mji.tapia.com.okurahotel.configuration;

import java.util.UUID;

public final class ViewIdGenerator {

    public String newId() {
        return UUID.randomUUID().toString();
    }
}
