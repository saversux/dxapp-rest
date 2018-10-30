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

import de.hhu.bsinfo.dxapp.rest.AbstractRestCommand;
import de.hhu.bsinfo.dxapp.rest.ServiceHelper;
import de.hhu.bsinfo.dxram.log.LogService;
import de.hhu.bsinfo.dxutils.NodeID;

public class LogInfo extends AbstractRestCommand {
    public LogInfo() {
        setInfo("loginfo", "nid", "Prints the log utilization of given peer");
    }

    @Override
    public void register(Service server, ServiceHelper services) {
        server.get("/loginfo", (request, response) -> {
            String stringNid = request.queryParams("nid");

            if (stringNid == null) {
                return createError("Invalid Parameter, please use: /loginfo?nid=[NID]", response);
            }

            if (!isNodeID(stringNid)) {
                return createError("Invalid NodeID", response);
            }

            short nid = NodeID.parse(stringNid);
            if (nid == NodeID.INVALID_ID) {
                return createError("NID invalid", response);
            }

            String utilization = services.getService(LogService.class).getCurrentUtilization(nid);

            return gson.toJson(utilization);

        });
    }
}
