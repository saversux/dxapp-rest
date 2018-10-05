package de.hhu.bsinfo.dxapp.rest.cmd;

import de.hhu.bsinfo.dxapp.rest.AbstractRestCommand;
import de.hhu.bsinfo.dxapp.rest.ServiceHelper;
import de.hhu.bsinfo.dxram.boot.BootService;
import spark.Service;

import java.util.ArrayList;
import java.util.List;

public class Nodelist extends AbstractRestCommand {

    public Nodelist(){
        setInfo("nodelist", "", "List all nodes");
    }

    @Override
    public void register(Service server, ServiceHelper services) {
        server.get("/nodelist", (request, response) -> {
            List<Short> nodes = services.getService(BootService.class).getOnlineNodeIDs();
            List<String> stringNodes = new ArrayList();

            for (Short node : nodes) {
                stringNodes.add(Integer.toHexString(node & 0xffff));
            }

            return gson.toJson(stringNodes);
        });
    }
}
