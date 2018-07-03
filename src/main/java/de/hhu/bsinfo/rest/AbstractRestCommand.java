package de.hhu.bsinfo.rest;

import com.google.gson.Gson;
import de.hhu.bsinfo.dxram.engine.AbstractDXRAMService;
import de.hhu.bsinfo.dxterm.TerminalServiceAccessor;
import spark.Service;

public abstract class AbstractRestCommand {
    protected Gson gson;


    public AbstractRestCommand(){
        gson = new Gson();
    }

    public abstract void register(Service server, ServiceHelper services);


}
