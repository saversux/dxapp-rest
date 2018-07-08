package de.hhu.bsinfo.rest.cmd;

import de.hhu.bsinfo.dxram.data.ChunkID;
import de.hhu.bsinfo.dxutils.NodeID;
import de.hhu.bsinfo.rest.AbstractRestCommand;
import de.hhu.bsinfo.rest.ResponseError;
import de.hhu.bsinfo.rest.ResponseMessage;
import de.hhu.bsinfo.rest.ServiceHelper;
import spark.Service;

import java.text.ParseException;

public class Chunkcreate extends AbstractRestCommand {
    @Override
    public void register(Service server, ServiceHelper services) {
        //server.get("/chunkcreate", (request, response) -> createError("Invalid Parameter, please use: /chunkcreate/:nid/:size"));
        server.get("/chunkcreate", (request, response) -> {
            String stringNid = request.queryParams("nid");
            String stringSize = request.queryParams("size");

            if (stringNid == null || stringSize == null){
                return createError("Invalid Parameter, please use: /chunkcreate?nid=[NID]?=size=[size in bytes]");
            }

            short nid = NodeID.parse(stringNid);
            int size = Integer.parseInt(stringSize);

            if (nid == NodeID.INVALID_ID) {
                return createError("NodeID invalid");
            }

            long[] chunkIDs;

            if (services.bootService.getNodeID() == nid) {
                chunkIDs = services.chunkService.create(size, 1);
            } else {
                chunkIDs = services.chunkService.createRemote(nid, size);
            }


            return createMessage("ChunkID: 0x"+Long.toHexString(chunkIDs[0]));

        });
    }
}
