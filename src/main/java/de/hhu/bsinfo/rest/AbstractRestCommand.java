package de.hhu.bsinfo.rest;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import spark.Service;

public abstract class AbstractRestCommand {
    protected Gson gson;

    public AbstractRestCommand(){
        gson = new GsonBuilder().setPrettyPrinting().create();;
    }

    public abstract void register(Service server, ServiceHelper services);

}
