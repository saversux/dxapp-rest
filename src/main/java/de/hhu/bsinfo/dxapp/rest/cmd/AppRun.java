package de.hhu.bsinfo.dxapp.rest.cmd;

import de.hhu.bsinfo.dxapp.rest.AbstractRestCommand;
import de.hhu.bsinfo.dxapp.rest.ServiceHelper;
import de.hhu.bsinfo.dxram.app.ApplicationService;
import de.hhu.bsinfo.dxutils.NodeID;
import spark.Service;

public class AppRun extends AbstractRestCommand {
    public AppRun(){
        setInfo("apprun", "nid, app", "Start app on remote node");
    }

    @Override
    public void register(Service server, ServiceHelper services) {
        server.get("/apprun", (request, response) -> {
            String stringNid = request.queryParams("nid");
            String appname = request.queryParams("app");

            ApplicationService appService = services.getService(ApplicationService.class);

            if (stringNid == null || appname == null){
                return createError("Invalid Parameter, please use: /apprun?nid=[NID]?app=[APPNAME]", response);
            }

            if (!isNodeID(stringNid)){
                return createError("Invalid NodeID", response);
            }

            short nid = NodeID.parse(stringNid);

            if (nid == NodeID.INVALID_ID) {
                return createError("NodeID invalid", response);
            }

            if (!appService.startApplication(nid, appname, null)) {
                return createError("Starting "+appname+" failed...", response);
            }

            return createMessage(appname+" started successful");
        });
    }
}
