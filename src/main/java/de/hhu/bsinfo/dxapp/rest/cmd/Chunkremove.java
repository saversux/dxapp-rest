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
import de.hhu.bsinfo.dxapp.rest.cmd.requests.ChunkputRequest;
import de.hhu.bsinfo.dxapp.rest.cmd.requests.ChunkremoveRequest;
import de.hhu.bsinfo.dxmem.data.ChunkID;
import de.hhu.bsinfo.dxram.chunk.ChunkService;

public class Chunkremove extends AbstractRestCommand {
    public Chunkremove() {
        setInfo("chunkremove", "cid", "Remove chunk with CID");
    }

    @Override
    public void register(Service server, ServiceHelper services) {
        server.put("/chunkremove", (request, response) -> {
            if (request.body().equals("")) {
                return createError("No body in request.", response);
            }
            ChunkremoveRequest chunkremoveRequest;
            try {
                chunkremoveRequest = gson.fromJson(request.body(), ChunkremoveRequest.class);
            } catch (JsonSyntaxException e) {
                return createError("Please put cid into body as json.", response);
            }
            String stringCid = chunkremoveRequest.getCid();

            if (stringCid == null) {
                return createError("Please put cid into body as json.", response);
            }

            if (!isChunkID(stringCid)) {
                return createError("Invalid ChunkID", response);
            }

            long cid = ChunkID.parse(stringCid);

            if (!isChunkID(stringCid)) {
                return createError("Invalid ChunkID", response);
            } else if (ChunkID.getLocalID(cid) == 0) {
                return createError("Removal of index chunk is not allowed", response);
            }

            if (services.getService(ChunkService.class).remove().remove(cid) != 1) {
                return createError("Removing chunk with ID " + ChunkID.toHexString(cid) + " failed", response);
            } else {
                return createMessage("Chunk " + ChunkID.toHexString(cid) + " removed");
            }
        });
    }
}
