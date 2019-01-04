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
import de.hhu.bsinfo.dxapp.rest.cmd.requests.AppRunRequest;
import de.hhu.bsinfo.dxapp.rest.cmd.requests.ChunkcreateRequest;
import de.hhu.bsinfo.dxmem.data.ChunkID;
import de.hhu.bsinfo.dxram.chunk.ChunkService;
import de.hhu.bsinfo.dxutils.NodeID;
import de.hhu.bsinfo.dxapp.rest.cmd.responses.*;

/**
 * Create a chunk on specified node with specified size
 *
 * @author Julien Bernhart, 2018-11-26
 * @author Maximilian Loose,
 * Modifications:
 * - chunks IDs are now sent as long values instead of strings
 * - response body is sent with createMessageOfJavaObject method
 *
 */
public class Chunkcreate extends AbstractRestCommand {

    public Chunkcreate() {
        setInfo("chunkcreate", "nid, size", "Creates a Chunk on Node <nid> with Size <size>");
    }

    @Override
    public void register(Service server, ServiceHelper services) {
        server.put("/chunkcreate", (request, response) -> {
            if (request.body().equals("")) {
                return createError("No body in request.", response);
            }
            ChunkcreateRequest chunkcreateRequest;
            try {
                chunkcreateRequest = gson.fromJson(request.body(), ChunkcreateRequest.class);
            } catch (JsonSyntaxException e) {
                return createError("Please put nid and size into body as json.", response);
            }
            String stringNid = chunkcreateRequest.getNid();
            String stringSize = chunkcreateRequest.getSize();

            if (stringNid == null || stringSize == null) {
                return createError("Please put nid and size into body as json.",
                        response);
            }

            if (!isNodeID(stringNid)) {
                return createError("Invalid NodeID", response);
            }

            short nid = NodeID.parse(stringNid);

            //todo check if strinSize is a number
            int size = Integer.parseInt(stringSize);

            if (nid == NodeID.INVALID_ID) {
                return createError("NodeID invalid", response);
            }

            long[] chunkIDs = new long[1];

            services.getService(ChunkService.class).create().create(nid, chunkIDs, 1, size);

            return createMessageOfJavaObject(new ChunkCreateResponse(chunkIDs[0]));
        });
    }
}
