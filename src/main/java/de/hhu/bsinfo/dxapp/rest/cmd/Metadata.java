package de.hhu.bsinfo.dxapp.rest.cmd;

import de.hhu.bsinfo.dxapp.rest.AbstractRestCommand;
import de.hhu.bsinfo.dxapp.rest.ServiceHelper;
import de.hhu.bsinfo.dxram.boot.BootService;
import de.hhu.bsinfo.dxram.lookup.LookupService;
import de.hhu.bsinfo.dxram.util.NodeRole;
import de.hhu.bsinfo.dxutils.NodeID;
import spark.Service;

import java.util.ArrayList;
import java.util.List;

public class Metadata extends AbstractRestCommand {
    public Metadata(){
        setInfo("metadata", "nid (optional)", "Get summary of all or one superper's metadata");
    }
    @Override
    public void register(Service server, ServiceHelper services) {
        server.get("metadata", (request, response) -> {
            String stringNid = request.queryParams("nid");

            LookupService lookup = services.getService(LookupService.class);
            BootService boot = services.getService(BootService.class);

            if (stringNid == null) {
                List<Short> nodeIds = boot.getOnlineNodeIDs();
                List<MetadataEntry> metadataEntries = new ArrayList<>();
                for (Short nodeId : nodeIds) {
                    NodeRole curRole = boot.getNodeRole(nodeId);
                    if (curRole == NodeRole.SUPERPEER) {
                        String summary = lookup.getMetadataSummary(nodeId);
                        metadataEntries.add(new MetadataEntry(NodeID.toHexString(nodeId), summary));
                    }
                }
                return gson.toJson(metadataEntries);
            }else{
                if (!isNodeID(stringNid)){
                    return createError("Invalid NodeID", response);
                }

                short nid = NodeID.parse(stringNid);
                if (nid == NodeID.INVALID_ID) {
                    return createError("NID invalid", response);
                }

                String summary = lookup.getMetadataSummary(nid);
                return gson.toJson(new MetadataEntry(NodeID.toHexString(nid), summary));
            }
        });
    }

    private class MetadataEntry{
        String nid;
        String metadata;

        public MetadataEntry(String nid, String metadata){
            this.nid = nid;
            this.metadata = metadata;
        }
    }
}
