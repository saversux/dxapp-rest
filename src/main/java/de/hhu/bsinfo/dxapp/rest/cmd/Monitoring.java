package de.hhu.bsinfo.dxapp.rest.cmd;

import de.hhu.bsinfo.dxapp.rest.AbstractRestCommand;
import de.hhu.bsinfo.dxapp.rest.ServiceHelper;
import de.hhu.bsinfo.dxram.monitoring.MonitoringDataStructure;
import de.hhu.bsinfo.dxram.monitoring.MonitoringService;
import de.hhu.bsinfo.dxutils.NodeID;
import spark.Service;

public class Monitoring extends AbstractRestCommand {
    @Override
    public void register(Service server, ServiceHelper services) {
        server.get("/monitor", (request, response) -> {
            String stringNid = request.queryParams("nid");

            if (stringNid == null) {
                return createError("Invalid Parameter, please use: /monitor?nid=[NID]");
            }

            short nid = NodeID.parse(stringNid);
            MonitoringService monitoring = services.getService(MonitoringService.class);

            if (nid != NodeID.INVALID_ID) {
                MonitoringDataStructure data = monitoring.getMonitoringDataFromPeer(NodeID.parse("b1bd"));
                return gson.toJson(data);

            } else {
                return createError("NID invalid");
            }

        });
    }
}
