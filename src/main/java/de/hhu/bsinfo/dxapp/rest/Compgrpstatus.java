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

import spark.Service;

import com.google.gson.JsonSyntaxException;

import de.hhu.bsinfo.dxapp.rest.cmd.requests.CompgrpstatusRequest;
import de.hhu.bsinfo.dxram.ms.MasterSlaveComputeService;

/**
 * Get status of specific compute grp
 *
 * @author Julien Bernhart, 2019-01-07
 */
public class Compgrpstatus extends AbstractRestCommand{
    public Compgrpstatus() {
        setInfo("compgrpstatus", "cgid", "get status of specific compute group");
    }
    @Override
    public void register(Service server, ServiceHelper services) {
        server.get("/compgrpstatus", (request, response) -> {
            if (request.body().equals("")) {
                return createError("No body in request.", response);
            }
            CompgrpstatusRequest compgrpstatusRequest;
            try {
                compgrpstatusRequest = gson.fromJson(request.body(), CompgrpstatusRequest.class);
            } catch (JsonSyntaxException e) {
                return createError("Please put cgid into body as json.", response);
            }
            String stringCgid = compgrpstatusRequest.getCgid();

            if (stringCgid == null) {
                return createError("Please put cgid into body as json.", response);
            }

            MasterSlaveComputeService mscomp = services.getService(MasterSlaveComputeService.class);
            MasterSlaveComputeService.StatusMaster status = mscomp.getStatusMaster(Short.valueOf(stringCgid));

            if (status == null) {
                return createError("Getting compute group status of group "+stringCgid+" failed", response);
            }

            return createMessageOfJavaObject(status);
        });
    }
}
