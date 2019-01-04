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

import com.google.gson.JsonSyntaxException;

import de.hhu.bsinfo.dxapp.rest.AbstractRestCommand;
import de.hhu.bsinfo.dxapp.rest.ServiceHelper;
import de.hhu.bsinfo.dxapp.rest.cmd.requests.MetadataRequest;
import de.hhu.bsinfo.dxapp.rest.cmd.requests.MonitoringRequest;
import de.hhu.bsinfo.dxram.monitoring.MonitoringDataStructure;
import de.hhu.bsinfo.dxram.monitoring.MonitoringService;
import de.hhu.bsinfo.dxutils.NodeID;

/**
 * Get monitoring data of given peer
 *
 * @author Julien Bernhart, 2018-11-26
 */
public class Monitoring extends AbstractRestCommand {
    public Monitoring() {
        setInfo("monitoring", "nid", "Get monitoring data of given peer");
    }

    @Override
    public void register(Service server, ServiceHelper services) {
        server.get("/monitor", (request, response) -> {
            if (request.body().equals("")) {
                return createError("No body in request.", response);
            }
            MonitoringRequest monitoringRequest;
            try {
                monitoringRequest = gson.fromJson(request.body(), MonitoringRequest.class);
            } catch (JsonSyntaxException e) {
                return createError("Please put nid into body as json.", response);
            }
            String stringNid = monitoringRequest.getNid();

            if (stringNid == null) {
                return createError("Please put nid into body as json.", response);
            }

            if (!isNodeID(stringNid)) {
                return createError("Invalid NodeID", response);
            }

            short nid = NodeID.parse(stringNid);
            MonitoringService monitoring = services.getService(MonitoringService.class);

            if (nid != NodeID.INVALID_ID) {
                MonitoringDataStructure data = monitoring.getMonitoringDataFromPeer(nid);
                return gson.toJson(data);

            } else {
                return createError("NID invalid", response);
            }

        });
    }
}
