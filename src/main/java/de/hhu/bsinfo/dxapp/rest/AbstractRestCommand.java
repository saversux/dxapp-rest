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

/**
 * Command Class, this is used to create new DXRAM Rest commands
 *
 * @author Julien Bernhart, 2018-11-26
 */
public abstract class AbstractRestCommand {
    protected Gson gson;
    private CommandInfo info;

    public AbstractRestCommand() {
        gson = new GsonBuilder().setPrettyPrinting().create();
    }

    public abstract void register(Service server, ServiceHelper services);

    /**
     * Wrap json objects in a html page
     * @param o
     * @return html page
     */
    public String toHtml(Object o) {
        String json = gson.toJson(o);
        json = "<html> <body> <pre> <code>" + json + "</code> </pre> </body> </html>";

        return json;
    }

    /**
     * This is used to create error message with html status 400 (wrong input)
     * @param error
     * @param response
     * @return response with html error code and specified error message
     */
    public String createError(String error, Response response) {
        response.status(400);
        return gson.toJson(new ResponseError(error));
    }

    /**
     * Create json response
     * @param message
     * @return response message
     */
    public String createMessage(String message) {
        return gson.toJson(new ResponseMessage(message));
    }

    public String createMessageOfJavaObject(Object javaObject) {
        return gson.toJson(javaObject);
    }

    /**
     * MODIFICATION!
     * Creates a html page with autorefresh (only for statsprint command)
     * @param message
     * @param interval
     * @return html page
     */
    public String htmlRefresh(String message, String interval) {
        return "<html><head><title>DXRAM Statistics</title><meta http-equiv=\"refresh\" content=\"" + interval +
                "\" ></head><body> <pre> <code>" + message + "</code> </pre> </body> </html>";
    }

    /**
     * Check whether a chunkid is valid or nod
     * @param stringCid
     * @return
     */
    public Boolean isChunkID(String stringCid) {
        boolean isChunkId = (stringCid.startsWith("0x") && stringCid.length() == 18) || stringCid.length() == 16;
        return isChunkId;
    }

    /**
     * Check whether a nodeid is valid or not
     * @param stringNid
     * @return
     */
    public Boolean isNodeID(String stringNid) {
        boolean isNodeId = (stringNid.startsWith("0x") && stringNid.length() == 6) || stringNid.length() == 4;
        return isNodeId;
    }

    /**
     * Check whether a barrierid is valid or not
     * @param stringBid
     * @return
     */
    public Boolean isBarrierID(String stringBid){
        boolean isBarrierId = (stringBid.startsWith("0x") && stringBid.length() == 10) || stringBid.length() == 8;
        return isBarrierId;
    }

    /**
     * Set information about the command
     * @param name
     * @param param
     * @param info
     */
    public void setInfo(String name, String param, String info) {
        this.info = new CommandInfo(name, param, info);
    }

    /**
     * Get information about the command
     * @return
     */
    public CommandInfo getInfo() {
        return info;
    }

    /**
     * CommandInfo object (for gson serialization)
     */
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
