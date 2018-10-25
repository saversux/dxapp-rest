package de.hhu.bsinfo.dxapp.rest.cmd;

import de.hhu.bsinfo.dxapp.rest.AbstractRestCommand;
import de.hhu.bsinfo.dxapp.rest.ServiceHelper;
import de.hhu.bsinfo.dxram.log.LogService;
import de.hhu.bsinfo.dxutils.NodeID;
import spark.Service;

public class LogInfo extends AbstractRestCommand {
    public LogInfo(){
        setInfo("loginfo", "nid", "Prints the log utilization of given peer");
    }
    @Override
    public void register(Service server, ServiceHelper services) {
        server.get("/loginfo", (request, response) -> {
            String stringNid = request.queryParams("nid");

            if (stringNid == null) {
                return createError("Invalid Parameter, please use: /loginfo?nid=[NID]", response);
            }

            if (!isNodeID(stringNid)){
                return createError("Invalid NodeID", response);
            }

            short nid = NodeID.parse(stringNid);
            if (nid == NodeID.INVALID_ID) {
                return createError("NID invalid", response);
            }

            String utilization = services.getService(LogService.class).getCurrentUtilization(nid);

            return gson.toJson(utilization);

        });
    }
}
