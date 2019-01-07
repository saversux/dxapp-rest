/*
 * Copyright (C) 2018 Heinrich-Heine-Universitaet Duesseldorf, Institute of Computer Science,
 * Department Operating Systems
 *
 * This program is free software: you can redistribute it and/or modify it under the terms of the GNU General Public
 * License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any
 * later version.
 *
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied
 * warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU General Public License for more
 * details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>
 */

package de.hhu.bsinfo.dxapp.rest.cmd;

import spark.Service;

import java.util.ArrayList;
import java.util.List;

import com.google.gson.JsonSyntaxException;

import de.hhu.bsinfo.dxapp.rest.AbstractRestCommand;
import de.hhu.bsinfo.dxapp.rest.ServiceHelper;
import de.hhu.bsinfo.dxapp.rest.cmd.requests.MetadataRequest;
import de.hhu.bsinfo.dxapp.rest.cmd.responses.MetadataResponseAllPeers;
import de.hhu.bsinfo.dxapp.rest.cmd.responses.MetadataResponseOnePeer;
import de.hhu.bsinfo.dxram.boot.BootService;
import de.hhu.bsinfo.dxram.lookup.LookupService;
import de.hhu.bsinfo.dxram.util.NodeRole;
import de.hhu.bsinfo.dxutils.NodeID;

/**
 * Get summary of all or one superpeer's metadata
 *
 * @author Julien Bernhart, 2018-11-26
 * @author Maximilian Loose,
 *  Modifications:
 *  - response body is sent with createMessageOfJavaObject method
 */
public class Metadata extends AbstractRestCommand {
    public Metadata() {
        setInfo("metadata", "nid (optional)", "Get summary of all or one superpeer's metadata");
    }

    @Override
    public void register(Service server, ServiceHelper services) {
        server.get("metadata", (request, response) -> {
            MetadataRequest metadataRequest;
            try {
                metadataRequest = gson.fromJson(request.body(), MetadataRequest.class);
            } catch (JsonSyntaxException e) {
                return createError("Please put nid into body as json.", response);
            }

            String stringNid;
            if (request.body().equals("")) {
                stringNid = null;
            }else{
                stringNid = metadataRequest.getNid();
            }

            LookupService lookup = services.getService(LookupService.class);
            BootService boot = services.getService(BootService.class);

            if (stringNid == null) {
                List<Short> nodeIds = boot.getOnlineNodeIDs();
                List<MetadataResponseOnePeer> metadataEntries = new ArrayList<>();
                for (Short nodeId : nodeIds) {
                    NodeRole curRole = boot.getNodeRole(nodeId);
                    if (curRole == NodeRole.SUPERPEER) {
                        String summary = lookup.getMetadataSummary(nodeId);
                        metadataEntries.add(new MetadataResponseOnePeer(NodeID.toHexString(nodeId), summary));
                    }
                }
                return createMessageOfJavaObject(new MetadataResponseAllPeers(metadataEntries));
            } else {
                if (!isNodeID(stringNid)) {
                    return createError("Invalid NodeID", response);
                }

                short nid = NodeID.parse(stringNid);
                if (nid == NodeID.INVALID_ID) {
                    return createError("NID invalid", response);
                }

                String summary = lookup.getMetadataSummary(nid);
                return createMessageOfJavaObject(new MetadataResponseOnePeer(NodeID.toHexString(nid), summary));
            }
        });
    }


}
