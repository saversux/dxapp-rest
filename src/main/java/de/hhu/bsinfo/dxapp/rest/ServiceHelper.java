package de.hhu.bsinfo.dxapp.rest;

import de.hhu.bsinfo.dxram.engine.AbstractDXRAMService;

public class ServiceHelper {
    private RestServerApplication app;

    public ServiceHelper(RestServerApplication app) {
        this.app = app;
    }

    public <T extends AbstractDXRAMService> T getService(final Class<T> p_class) {
        return app.getService(p_class);
    }

}
