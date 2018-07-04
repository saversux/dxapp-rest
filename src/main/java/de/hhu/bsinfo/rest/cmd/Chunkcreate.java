package de.hhu.bsinfo.rest.cmd;

import de.hhu.bsinfo.dxram.data.ChunkID;
import de.hhu.bsinfo.dxutils.NodeID;
import de.hhu.bsinfo.rest.AbstractRestCommand;
import de.hhu.bsinfo.rest.ServiceHelper;
import spark.Service;

import java.text.ParseException;

public class Chunkcreate extends AbstractRestCommand {
    @Override
    public void register(Service server, ServiceHelper services) {
        server.get("/chunkcreate/:nid/:size", (request, response) -> {
            short nid = NodeID.parse(request.params(":nid"));
            int size = Integer.parseInt(request.params(":size"));

            if (nid == NodeID.INVALID_ID) {
                return "No nid specified";
            }

            long[] chunkIDs;

            if (services.bootService.getNodeID() == nid) {
                chunkIDs = services.chunkService.create(size, 1);
            } else {
                chunkIDs = services.chunkService.createRemote(nid, size);
            }


            return "ChunkID: 0x"+Long.toHexString(chunkIDs[0]);

        });
    }
}
