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
import de.hhu.bsinfo.dxapp.rest.cmd.requests.ChunkremoveRequest;
import de.hhu.bsinfo.dxmem.data.ChunkID;
import de.hhu.bsinfo.dxram.chunk.ChunkService;

/**
 * Remove chunk with specified chunkid
 *
 * @author Julien Bernhart, 2018-11-26
 * @author Maximilian Loose,
 * Modifications:
 * - parsing of the the String cid is not necessary anymmore because cids are sent as longs
 */
@Path("chunkremove")
public class Chunkremove extends AbstractRestCommand {
    @Override
    public CommandInfo setInfo() {
        return new CommandInfo("chunkremove", "cid", "Remove chunk with CID");
    }

    @PUT
    @Produces(MediaType.TEXT_PLAIN)
    public String removeChunk(String body) {
        if (body.equals("")) {
            throw new BadRequestException("No body in request.");
        }
        ChunkremoveRequest chunkremoveRequest;
        try {
            chunkremoveRequest = gson.fromJson(body, ChunkremoveRequest.class);
        } catch (JsonSyntaxException e) {
            throw new BadRequestException("Please put cid into body as json.");
        }
        Long cid = chunkremoveRequest.getCid();

        if (cid == 0L) {
            throw new BadRequestException("Please put cid into body as json.");
        }


        if (ChunkID.getLocalID(cid) == 0) {
            throw new BadRequestException("Removal of index chunk is not allowed");
        }
        if (ServiceHelper.getService(ChunkService.class).remove().remove(cid) != 1) {
            throw new BadRequestException("Removing chunk with ID " + ChunkID.toHexString(cid) + " failed");
        } else {
            return "";
        }
    }
}
