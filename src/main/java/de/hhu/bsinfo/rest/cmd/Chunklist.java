package de.hhu.bsinfo.rest.cmd;

import de.hhu.bsinfo.dxutils.NodeID;
import de.hhu.bsinfo.rest.AbstractRestCommand;
import de.hhu.bsinfo.rest.ServiceHelper;
import spark.Service;

public class Chunklist extends AbstractRestCommand {

    @Override
    public void register(Service server, ServiceHelper services) {
        server.get("/chunklist/", (request, response) -> "Please enter NID as first parameter.");
        server.get("/chunklist/:nodeId", (request, response) -> {
            short nid = NodeID.parse(request.params(":nodeId"));

            if(nid != NodeID.INVALID_ID){
                return services.chunkService.getAllLocalChunkIDRanges(nid);
            }else{
                return "NID invalid.";
            }

        });
    }
}
