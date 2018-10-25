package de.hhu.bsinfo.dxapp.rest.cmd;

import de.hhu.bsinfo.dxapp.rest.AbstractRestCommand;
import de.hhu.bsinfo.dxapp.rest.ServiceHelper;
import de.hhu.bsinfo.dxutils.NodeID;
import spark.Service;

public class Chunklocklist extends AbstractRestCommand {
    public Chunklocklist(){
        setInfo("chunklocklist","nid", "Get the list of all locked chunks of a node");
    }
    @Override
    public void register(Service server, ServiceHelper services) {
        server.get("/chunklocklist", (request, response) -> {
            String stringNid = request.queryParams("nid");

            if (stringNid == null) {
                return createError("Invalid Parameter, please use: /chunklocklist?nid=[NID]", response);
            }

            if (!isNodeID(stringNid)){
                return createError("Invalid NodeID", response);
            }

            short nid = NodeID.parse(stringNid);
            if (nid == NodeID.INVALID_ID) {
                return createError("NID invalid", response);
            }

            //TODO
            return null;

        });
    }
}
