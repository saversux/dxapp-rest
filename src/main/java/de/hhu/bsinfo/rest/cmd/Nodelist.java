package de.hhu.bsinfo.rest.cmd;

import de.hhu.bsinfo.rest.AbstractRestCommand;
import de.hhu.bsinfo.rest.ServiceHelper;
import spark.Service;

import java.util.ArrayList;
import java.util.List;

public class Nodelist extends AbstractRestCommand {

    @Override
    public void register(Service server, ServiceHelper services) {
        server.get("/nodelist", (request, response) -> {
            List<Short> nodes  = services.bootService.getOnlineNodeIDs();
            List<String> stringNodes = new ArrayList();

            for (Short node : nodes) {
                stringNodes.add(Integer.toHexString(node & 0xffff));
            }

            return gson.toJson(stringNodes);
        });
    }
}
