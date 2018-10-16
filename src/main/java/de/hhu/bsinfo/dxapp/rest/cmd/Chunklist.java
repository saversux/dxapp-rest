package de.hhu.bsinfo.dxapp.rest.cmd;

import de.hhu.bsinfo.dxapp.rest.ServiceHelper;
import de.hhu.bsinfo.dxram.chunk.ChunkService;
import de.hhu.bsinfo.dxutils.NodeID;
import de.hhu.bsinfo.dxapp.rest.AbstractRestCommand;
import spark.Service;

public class Chunklist extends AbstractRestCommand {

    public Chunklist(){
        setInfo("chunklist", "nid", "List all Chunks on Node with <nid>");
    }

    @Override
    public void register(Service server, ServiceHelper services) {
        server.get("/chunklist", (request, response) -> {
            String stringNid = request.queryParams("nid");

            if (stringNid == null) {
                return createError("Invalid Parameter, please use: /chunklist?nid=[NID]", response);
            }

            short nid = NodeID.parse(stringNid);

            if (nid != NodeID.INVALID_ID) {
                String local = services.getService(ChunkService.class).cidStatus().getAllLocalChunkIDRanges(nid).toString();
                String migrated = services.getService(ChunkService.class).cidStatus().getAllMigratedChunkIDRanges(nid).toString();
                return gson.toJson(new ChunkRangeRest(local, migrated));
            } else {
                return createError("NID invalid", response);
            }

        });
    }

    private class ChunkRangeRest {
        String localChunkRanges;
        String migratedChunkRanges;

        public ChunkRangeRest(String localChunkRanges, String migratedChunkRanges) {
            this.localChunkRanges = localChunkRanges;
            this.migratedChunkRanges = migratedChunkRanges;
        }
    }
}
