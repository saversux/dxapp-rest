package de.hhu.bsinfo.dxapp.rest;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import spark.Service;

public abstract class AbstractRestCommand {
    protected Gson gson;
    private CommandInfo info;

    public AbstractRestCommand() {
        gson = new GsonBuilder().setPrettyPrinting().create();
    }

    public abstract void register(Service server, ServiceHelper services);

    public String toHtml(Object o) {
        String json = gson.toJson(o);
        json = "<html> <body> <pre> <code>" + json + "</html> </body> </pre> </code>";

        return json;
    }

    public String createError(String error) {
        return gson.toJson(new ResponseError(error));
    }

    public String createMessage(String message) {
        return gson.toJson(new ResponseMessage(message));
    }

    public String htmlRefresh(String message, String interval) {
        return "<html><head><title>DXRAM Statistics</title><meta http-equiv=\"refresh\" content=\"" + interval + "\" ></head><body> <pre> <code>" + message + "</body></html></pre> </code>";
    }

    public void setInfo(String name, String param, String info) {
        this.info = new CommandInfo(name, param, info);
    }
    public CommandInfo getInfo(){
        return info;
    }

    private class CommandInfo {
        String name;
        String param;
        String info;

        public CommandInfo(String name, String param, String info){
            this.name = name;
            this.param = param;
            this.info = info;
        }
    }


}