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
import de.hhu.bsinfo.dxapp.rest.cmd.requests.NamegetRequest;
import de.hhu.bsinfo.dxapp.rest.cmd.responses.NameGetResponse;
import de.hhu.bsinfo.dxmem.data.ChunkID;
import de.hhu.bsinfo.dxram.nameservice.NameserviceService;

/**
 * Get chunk by name from nameservice
 *
 * @author Julien Bernhart, 2018-11-26
 * @author Maximilian Loose
 * Modifications:
 * - response body is sent with createMessageOfJavaObject method
 */
@Path("nameget")
public class Nameget extends AbstractRestCommand {
    @Override
    public CommandInfo setInfo() {
        return new CommandInfo("nameget", "name", "Get chunk by name from nameservice.");
    }

    @GET
    @Produces(MediaType.TEXT_PLAIN)
    public String nameGet(String body) {
        if (body.equals("")) {
            throw new BadRequestException("No body in request.");
        }
        NamegetRequest namegetRequest;
        try {
            namegetRequest = gson.fromJson(body, NamegetRequest.class);
        } catch (JsonSyntaxException e) {
            throw new BadRequestException("Please put name into body as json.");
        }
        String name = namegetRequest.getName();

        if (name == null) {
            throw new BadRequestException("Please put name into body as json.");
        }

        NameserviceService nameservice = ServiceHelper.getService(NameserviceService.class);

        long cid = nameservice.getChunkID(name, 2000);

        if (cid == ChunkID.INVALID_ID) {
            throw new BadRequestException("Could not get name entry for " + name + ", does not exist");
        } else {
            return createMessageOfJavaObject(new NameGetResponse(cid));
        }
    }
}
