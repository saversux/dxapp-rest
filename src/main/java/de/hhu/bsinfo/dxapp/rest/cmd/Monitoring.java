package de.hhu.bsinfo.dxapp.rest.cmd;

import de.hhu.bsinfo.dxapp.rest.AbstractRestCommand;
import de.hhu.bsinfo.dxapp.rest.ServiceHelper;
import de.hhu.bsinfo.dxram.monitoring.MonitoringDataStructure;
import de.hhu.bsinfo.dxram.monitoring.MonitoringService;
import de.hhu.bsinfo.dxutils.NodeID;
import spark.Service;

public class Monitoring extends AbstractRestCommand {
    public Monitoring(){
        setInfo("monitoring", "nid", "Get monitoring data of given peer");
    }
    @Override
    public void register(Service server, ServiceHelper services) {
        server.get("/monitor", (request, response) -> {
            String stringNid = request.queryParams("nid");

            if (stringNid == null) {
                return createError("Invalid Parameter, please use: /monitor?nid=[NID]", response);
            }

            if (!isNodeID(stringNid)){
                return createError("Invalid NodeID", response);
            }

            short nid = NodeID.parse(stringNid);
            MonitoringService monitoring = services.getService(MonitoringService.class);

            if (nid != NodeID.INVALID_ID) {
                MonitoringDataStructure data = monitoring.getMonitoringDataFromPeer(nid);
                return gson.toJson(data);

            } else {
                return createError("NID invalid", response);
            }

        });
    }
}
