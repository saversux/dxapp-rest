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
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import com.google.gson.JsonSyntaxException;

import de.hhu.bsinfo.dxapp.rest.AbstractRestCommand;
import de.hhu.bsinfo.dxapp.rest.CommandInfo;
import de.hhu.bsinfo.dxapp.rest.ServiceHelper;
import de.hhu.bsinfo.dxapp.rest.cmd.requests.NameregRequest;
import de.hhu.bsinfo.dxmem.data.ChunkID;
import de.hhu.bsinfo.dxram.nameservice.NameserviceService;

/**
 * Register a Chunk in the nameservice.
 *
 * @author Julien Bernhart, 2018-11-26
 * @author Maximilian Loose
 * - parsing of the the String cid is not necessary anymmore because cids are sent as longs
 */
@Path("namereg")
public class Namereg extends AbstractRestCommand {
    @Override
    public CommandInfo setInfo() {
        return new CommandInfo("namereg", "cid, name", "Register Chunk <cid> with <name>");
    }

    @PUT
    @Produces(MediaType.TEXT_PLAIN)
    public String nameReg(String body) {
        if (body.equals("")) {
            throw new BadRequestException("No body in request.");
        }
        NameregRequest nameregRequest;
        try {
            nameregRequest = gson.fromJson(body, NameregRequest.class);
        } catch (JsonSyntaxException e) {
            throw new BadRequestException("Please put name and cid into body as json.");
        }
        Long cid  = nameregRequest.getCid();
        String name = nameregRequest.getName();

        if (cid == 0L || name == null) {
            throw new BadRequestException("Please put name and cid into body as json.");
        }
        if (cid != ChunkID.INVALID_ID) {
            ServiceHelper.getService(NameserviceService.class).register(cid, name);
            return "";
        } else {
            throw new BadRequestException("CID invalid");
        }
    }
}
