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

import java.util.ArrayList;
import java.util.List;

import javax.ws.rs.BadRequestException;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import com.google.gson.JsonSyntaxException;

import de.hhu.bsinfo.dxapp.rest.AbstractRestCommand;
import de.hhu.bsinfo.dxapp.rest.CommandInfo;
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
@Path("metadata")
public class Metadata extends AbstractRestCommand {
    @Override
    public CommandInfo setInfo() {
        return new CommandInfo("metadata", "nid (optional)", "Get summary of all or one superpeer's metadata");
    }

    @GET
    @Produces(MediaType.TEXT_PLAIN)
    public String metadata(String body) {
        MetadataRequest metadataRequest;
        try {
            metadataRequest = gson.fromJson(body, MetadataRequest.class);
        } catch (JsonSyntaxException e) {
            throw new BadRequestException("Please put nid into body as json.");
        }

        String stringNid;
        if (body.equals("")) {
            stringNid = null;
        }else{
            stringNid = metadataRequest.getNid();
        }

        LookupService lookup = ServiceHelper.getService(LookupService.class);
        BootService boot = ServiceHelper.getService(BootService.class);

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
                throw new BadRequestException("Invalid NodeID");
            }

            short nid = NodeID.parse(stringNid);
            if (nid == NodeID.INVALID_ID) {
                throw new BadRequestException("NID invalid");
            }

            String summary = lookup.getMetadataSummary(nid);
            return createMessageOfJavaObject(new MetadataResponseOnePeer(NodeID.toHexString(nid), summary));
        }
    }


}
