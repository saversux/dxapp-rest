/*
 * Copyright (C) 2018 Heinrich-Heine-Universitaet Duesseldorf, Institute of Computer Science,
 * Department Operating Systems
 *
 * This program is free software: you can redistribute it and/or modify it under the terms of the GNU General Public
 * License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any
 * later version.
 *
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied
 * warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU General Public License for more
 * details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>
 */

package de.hhu.bsinfo.dxapp.rest;

import spark.Response;
import spark.Service;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

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

    public String createError(String error, Response response) {
        response.status(400);
        return gson.toJson(new ResponseError(error));
    }

    public String createMessage(String message) {
        return gson.toJson(new ResponseMessage(message));
    }

    public String htmlRefresh(String message, String interval) {
        return "<html><head><title>DXRAM Statistics</title><meta http-equiv=\"refresh\" content=\"" + interval +
                "\" ></head><body> <pre> <code>" + message + "</body></html></pre> </code>";
    }

    public Boolean isChunkID(String stringCid) {
        boolean isChunkId = (stringCid.startsWith("0x") && stringCid.length() == 18) || stringCid.length() == 16;
        return isChunkId;
    }

    public Boolean isNodeID(String stringNid) {
        boolean isNodeId = (stringNid.startsWith("0x") && stringNid.length() == 6) || stringNid.length() == 4;
        return isNodeId;
    }

    public void setInfo(String name, String param, String info) {
        this.info = new CommandInfo(name, param, info);
    }

    public CommandInfo getInfo() {
        return info;
    }

    private class CommandInfo {
        String name;
        String param;
        String info;

        public CommandInfo(String name, String param, String info) {
            this.name = name;
            this.param = param;
            this.info = info;
        }
    }

}
