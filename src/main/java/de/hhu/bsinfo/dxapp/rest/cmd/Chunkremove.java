package de.hhu.bsinfo.dxapp.rest.cmd;

import de.hhu.bsinfo.dxapp.rest.AbstractRestCommand;
import de.hhu.bsinfo.dxapp.rest.ServiceHelper;
import de.hhu.bsinfo.dxmem.data.ChunkID;
import de.hhu.bsinfo.dxram.chunk.ChunkService;
import de.hhu.bsinfo.dxutils.NodeID;
import spark.Service;

public class Chunkremove extends AbstractRestCommand {
    public Chunkremove(){
        setInfo("chunkremove", "cid", "Remove chunk with CID");
    }

    @Override
    public void register(Service server, ServiceHelper services) {
        server.get("/chunkremove", (request, response) -> {
            String stringCid = request.queryParams("cid");

            if (stringCid == null) {
                return createError("Invalid Parameter, please use: /chunkremove?cid=[CID]", response);
            }

            long cid = ChunkID.parse(stringCid);

            if (cid == ChunkID.INVALID_ID) {
                return createError("ChunkID invalid", response);
            }else if (ChunkID.getLocalID(cid) == 0){
                return createError("Removal of index chunk is not allowed", response);
            }

            if (services.getService(ChunkService.class).remove().remove(cid) != 1) {
                return createError("Removing chunk with ID "+ChunkID.toHexString(cid)+" failed", response);
            } else {
                return createMessage("Chunk "+ChunkID.toHexString(cid)+" removed");
            }
        });
    }
}
