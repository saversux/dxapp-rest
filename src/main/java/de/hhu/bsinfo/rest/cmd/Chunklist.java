package de.hhu.bsinfo.rest.cmd;

import de.hhu.bsinfo.dxutils.NodeID;
import de.hhu.bsinfo.rest.AbstractRestCommand;
import de.hhu.bsinfo.rest.ResponseError;
import de.hhu.bsinfo.rest.ServiceHelper;
import spark.Service;

public class Chunklist extends AbstractRestCommand {

    @Override
    public void register(Service server, ServiceHelper services) {
        //server.get("/chunklist", (request, response) ->  createError("Invalid Parameter, please use: /chunklist/:nodeId"));
        server.get("/chunklist", (request, response) -> {
            String stringNid = request.queryParams("nid");

            if (stringNid == null){
                return createError("Invalid Parameter, please use: /chunklist?nid=[NID]");
            }

            short nid = NodeID.parse(stringNid);

            if(nid != NodeID.INVALID_ID){
                String local = services.chunkService.getAllLocalChunkIDRanges(nid).toString();
                String migrated = services.chunkService.getAllMigratedChunkIDRanges(nid).toString();
                return gson.toJson(new ChunkRangeRest(local, migrated));
            }else{
                return  createError("NID invalid");
            }

        });
    }

    private class ChunkRangeRest{
        String localChunkRanges;
        String migratedChunkRanges;

        public ChunkRangeRest(String localChunkRanges, String migratedChunkRanges){
            this.localChunkRanges = localChunkRanges;
            this.migratedChunkRanges = migratedChunkRanges;
        }
    }
}
