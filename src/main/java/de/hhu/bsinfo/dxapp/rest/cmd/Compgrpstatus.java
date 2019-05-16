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

import javax.ws.rs.BadRequestException;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import com.google.gson.JsonSyntaxException;

import de.hhu.bsinfo.dxapp.rest.AbstractRestCommand;
import de.hhu.bsinfo.dxapp.rest.CommandInfo;
import de.hhu.bsinfo.dxapp.rest.ServiceHelper;
import de.hhu.bsinfo.dxapp.rest.cmd.requests.CompgrpstatusRequest;
import de.hhu.bsinfo.dxram.ms.MasterSlaveComputeService;

/**
 * Get status of specific compute grp
 *
 * @author Julien Bernhart, 2019-01-07
 */
@Path("compgrpstatus")
public class Compgrpstatus extends AbstractRestCommand {
    @Override
    public CommandInfo setInfo() {
        return new CommandInfo("compgrpstatus", "cgid", "get status of specific compute group");
    }

    @GET
    @Produces(MediaType.TEXT_PLAIN)
    public String getCompGrpStatus(String body) {
        if (body.equals("")) {
            throw new BadRequestException("No body in request.");
        }
        CompgrpstatusRequest compgrpstatusRequest;
        try {
            compgrpstatusRequest = gson.fromJson(body, CompgrpstatusRequest.class);
        } catch (JsonSyntaxException e) {
            throw new BadRequestException("Please put cgid into body as json.");
        }
        String stringCgid = compgrpstatusRequest.getCgid();

        if (stringCgid == null) {
            throw new BadRequestException("Please put cgid into body as json.");
        }

        MasterSlaveComputeService mscomp = ServiceHelper.getService(MasterSlaveComputeService.class);
        MasterSlaveComputeService.StatusMaster status = mscomp.getStatusMaster(Short.valueOf(stringCgid));

        if (status == null) {
            throw new BadRequestException("Getting compute group status of group "+stringCgid+" failed");
        }

        return createMessageOfJavaObject(status);
    }
}
