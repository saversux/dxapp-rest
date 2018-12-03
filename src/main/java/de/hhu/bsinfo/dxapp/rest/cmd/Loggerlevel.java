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

package de.hhu.bsinfo.dxapp.rest.cmd;

import spark.Service;

import java.util.Arrays;
import java.util.List;

import com.google.gson.JsonSyntaxException;

import de.hhu.bsinfo.dxapp.rest.AbstractRestCommand;
import de.hhu.bsinfo.dxapp.rest.ServiceHelper;
import de.hhu.bsinfo.dxapp.rest.cmd.requests.LoggerlevelRequest;
import de.hhu.bsinfo.dxram.logger.LoggerService;
import de.hhu.bsinfo.dxutils.NodeID;

/**
 * Change the output level of the logger
 *
 * @author Julien Bernhart, 2018-12-03
 */
public class Loggerlevel extends AbstractRestCommand {
    public Loggerlevel() {
        setInfo("loggerlevel", "level, nid", "Change the output level of the logger");
    }

    @Override
    public void register(Service server, ServiceHelper services) {
        server.put("/loggerlevel", (request, response) -> {
            if (request.body().equals("")) {
                return createError("No body in request.", response);
            }
            LoggerlevelRequest loggerlevelRequest;
            try {
                loggerlevelRequest = gson.fromJson(request.body(), LoggerlevelRequest.class);
            } catch (JsonSyntaxException e) {
                return createError("Please put nid and level into body as json.", response);
            }
            String stringNid = loggerlevelRequest.getNid();
            String level = loggerlevelRequest.getLevel();

            if (level == null) {
                return createError("Please put level into body as json.", response);
            }

            List<String> levels = Arrays.asList("error","disabled","warn","info","debug","trace");

            if (!levels.contains(level)){
                return createError("Invalid logger level, allowed: error, disabled, warn, info, debug, trace",response);
            }else{
                LoggerService logger = services.getService(LoggerService.class);

                if (stringNid == null) {
                    LoggerService.setLogLevel(level);
                    return createMessage("Loggerlevel set to "+level);
                } else {
                    if (!isNodeID(stringNid)) {
                        return createError("Invalid NodeID", response);
                    }

                    short nid = NodeID.parse(stringNid);

                    logger.setLogLevel(level, nid);
                    return createMessage("Loggerlevel of "+NodeID.toHexString(nid)+" set to "+level);
                }
            }
        });
    }
}
