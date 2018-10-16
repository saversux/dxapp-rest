package de.hhu.bsinfo.dxapp.rest.cmd;

import de.hhu.bsinfo.dxapp.rest.AbstractRestCommand;
import de.hhu.bsinfo.dxapp.rest.ServiceHelper;
import de.hhu.bsinfo.dxram.boot.BootService;
import de.hhu.bsinfo.dxram.util.NodeCapabilities;
import de.hhu.bsinfo.dxutils.NodeID;
import spark.Service;

public class Nodeinfo extends AbstractRestCommand {
    public Nodeinfo(){
        setInfo("nodeinfo", "nid", "Get information about a node in the network");
    }

    @Override
    public void register(Service server, ServiceHelper services) {
        server.get("/nodeinfo", (request, response) -> {
            String stringNid = request.queryParams("nid");

            BootService bootService = services.getService(BootService.class);

            if (stringNid == null) {
                return createError("Invalid Parameter, please use: /nodeinfo?nid=[NID]", response);
            }

            short nid = NodeID.parse(stringNid);

            if (nid != NodeID.INVALID_ID) {
                if (!bootService.isNodeOnline(nid)){
                    return createError("Node not available.", response);
                }else{
                    return gson.toJson(new NodeinfoRest(NodeID.toHexString(nid),
                            bootService.getNodeRole(nid).toString(),
                            bootService.getNodeAddress(nid).toString(),
                            NodeCapabilities.toString(bootService.getNodeCapabilities(nid))));
                }
            } else {
                return createError("NID invalid", response);
            }

        });
    }

    private class NodeinfoRest {
        String nid;
        String role;
        String address;
        String capabilities;

        public NodeinfoRest(String nid, String role, String address, String capabilities){
            this.nid = nid;
            this.role = role;
            this.address = address;
            this.capabilities = capabilities;
        }
    }


}
