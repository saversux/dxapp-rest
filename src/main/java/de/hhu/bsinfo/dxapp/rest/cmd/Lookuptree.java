package de.hhu.bsinfo.dxapp.rest.cmd;

import de.hhu.bsinfo.dxapp.rest.AbstractRestCommand;
import de.hhu.bsinfo.dxapp.rest.ServiceHelper;
import de.hhu.bsinfo.dxram.lookup.LookupService;
import de.hhu.bsinfo.dxram.lookup.overlay.storage.LookupTree;
import de.hhu.bsinfo.dxutils.NodeID;
import spark.Service;

public class Lookuptree extends AbstractRestCommand {
    public Lookuptree(){
        setInfo("lookuptree", "nid", "Get the look up tree of a specified node");
    }
    @Override
    public void register(Service server, ServiceHelper services) {
        server.get("/lookuptree", (request, response) -> {
            String stringNid = request.queryParams("nid");

            if (stringNid == null) {
                return createError("Invalid Parameter, please use: /lookuptree?nid=[NID]");
            }

            short nid = NodeID.parse(stringNid);
            if (nid == NodeID.INVALID_ID) {
                return createError("NID invalid");
            }

            LookupService lookup = services.getService(LookupService.class);

            short respSuperpeer = lookup.getResponsibleSuperpeer(nid);

            if (respSuperpeer == NodeID.INVALID_ID) {
                return createError("No responsible superpeer for "+NodeID.toHexString(nid)+" found");
            }

            LookupTree tree = lookup.getLookupTreeFromSuperpeer(respSuperpeer, nid);
            return gson.toJson(tree);
        });
    }
}
