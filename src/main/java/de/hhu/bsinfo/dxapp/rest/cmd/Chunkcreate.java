package de.hhu.bsinfo.dxapp.rest.cmd;

import de.hhu.bsinfo.dxapp.rest.ServiceHelper;
import de.hhu.bsinfo.dxmem.data.ChunkID;
import de.hhu.bsinfo.dxram.chunk.ChunkService;
import de.hhu.bsinfo.dxutils.NodeID;
import de.hhu.bsinfo.dxapp.rest.AbstractRestCommand;
import spark.Service;

public class Chunkcreate extends AbstractRestCommand {

    public Chunkcreate(){
        setInfo("chunkcreate", "nid, size", "Creates a Chunk on Node <nid> with Size <size>");
    }

    @Override
    public void register(Service server, ServiceHelper services) {
        server.get("/chunkcreate", (request, response) -> {
            String stringNid = request.queryParams("nid");
            String stringSize = request.queryParams("size");

            if (stringNid == null || stringSize == null) {
                return createError("Invalid Parameter, please use: /chunkcreate?nid=[NID]?=size=[size in bytes]");
            }

            short nid = NodeID.parse(stringNid);
            int size = Integer.parseInt(stringSize);

            if (nid == NodeID.INVALID_ID) {
                return createError("NodeID invalid");
            }

            long[] chunkIDs = new long[1];

            services.getService(ChunkService.class).create().create(nid, chunkIDs,1, size);

            return createMessage("ChunkID: " + ChunkID.toHexString(chunkIDs[0]));
        });
    }
}
