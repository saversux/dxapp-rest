package de.hhu.bsinfo.dxapp.rest.cmd;

import de.hhu.bsinfo.dxapp.rest.AbstractRestCommand;
import de.hhu.bsinfo.dxapp.rest.ServiceHelper;
import de.hhu.bsinfo.dxram.app.ApplicationService;
import de.hhu.bsinfo.dxutils.NodeID;
import spark.Service;

public class AppList extends AbstractRestCommand {
    public AppList(){
        setInfo("applist", "nid", "Lists available applications to run on a remote peer");
    }

    @Override
    public void register(Service server, ServiceHelper services) {
        server.get("/applist", (request, response) -> {
            String stringNid = request.queryParams("nid");

            if (stringNid == null){
                createError("Invalid Parameter, please use: /applist?=[NID]");
            }
            short nid = NodeID.parse(stringNid);

            if (nid == NodeID.INVALID_ID) {
                return createError("NodeID invalid");
            }

            return gson.toJson(services.getService(ApplicationService.class).getLoadedApplicationClasses());
        });
    }
}
