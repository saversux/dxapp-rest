package de.hhu.bsinfo.rest.cmd;

import de.hhu.bsinfo.dxram.data.ChunkID;
import de.hhu.bsinfo.rest.AbstractRestCommand;
import de.hhu.bsinfo.rest.ServiceHelper;
import spark.Service;

public class Chunkget extends AbstractRestCommand {

    @Override
    public void register(Service server, ServiceHelper services) {
        server.get("/chunkget/:chunkId", (request, response) -> {
            long cid = ChunkID.parse(request.params(":chunkId"));

            if(cid != ChunkID.INVALID_ID){
                return "dummy";
            }else{
                return "CID invalid.";
            }

        });
    }
}
