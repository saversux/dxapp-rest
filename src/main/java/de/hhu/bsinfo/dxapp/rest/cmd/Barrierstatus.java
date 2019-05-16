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
import de.hhu.bsinfo.dxapp.rest.cmd.requests.BarrierstatusRequest;
import de.hhu.bsinfo.dxmem.data.ChunkID;
import de.hhu.bsinfo.dxram.lookup.overlay.storage.BarrierID;
import de.hhu.bsinfo.dxram.lookup.overlay.storage.BarrierStatus;
import de.hhu.bsinfo.dxram.sync.SynchronizationService;
import de.hhu.bsinfo.dxutils.NodeID;

/**
 * Get status of a barrier
 *
 * @author Julien Bernhart, 2018-11-26
 */
@Path("barrierstatus")
public class Barrierstatus extends AbstractRestCommand {
    @Override
    public CommandInfo setInfo() {
        return new CommandInfo("barrierstatus", "bid", "Get status of a barrier");
    }

    @GET
    @Produces(MediaType.TEXT_PLAIN)
    public String barrierStatus(String body) {
        if (body.equals("")) {
            throw new BadRequestException("No body in request.");
        }
        BarrierstatusRequest barrierstatusRequest;
        try {
            barrierstatusRequest = gson.fromJson(body, BarrierstatusRequest.class);
        } catch (JsonSyntaxException e) {
            throw new BadRequestException("Please put bid into body as json.");
        }
        String stringBid = barrierstatusRequest.getBid();

        if (stringBid == null) {
            throw new BadRequestException("Please put bid into body as json.");
        }

        if (!isBarrierID(stringBid)) {
            throw new BadRequestException("Invalid BarrierID");
        }

        int bid = BarrierID.parse(stringBid);

        SynchronizationService sync = ServiceHelper.getService(SynchronizationService.class);
        BarrierStatus status = sync.barrierGetStatus(bid);

        if (status == null) {
            throw new BadRequestException("Getting status of barrier "+BarrierID.toHexString(bid)+" failed");
        }

        List<BarrierstatusEntryRest> peers = new ArrayList<>();
        for (int i = 1; i < status.getSignedOnNodeIDs().length; i++) {
            peers.add(new BarrierstatusEntryRest(NodeID.toHexString(status.getSignedOnNodeIDs()[i]), ChunkID.toHexString(status.getCustomData()[i])));
        }

        BarrierstatusRest barrierstatusRest = new BarrierstatusRest(peers, BarrierID.toHexString(bid), status.getNumberOfSignedOnPeers(), status.getSignedOnNodeIDs().length);
        return gson.toJson(barrierstatusRest);
    }

    private class BarrierstatusRest {
        String bid;
        short signedOnPeers;
        int size;
        List<BarrierstatusEntryRest> peers;

        public BarrierstatusRest(List<BarrierstatusEntryRest> peers, String bid, short signedOnPeers, int size){
            this.bid = bid;
            this.signedOnPeers = signedOnPeers;
            this.size = size;
            this.peers = peers;
        }
    }

    private class BarrierstatusEntryRest {
        String nid;
        String data;

        public BarrierstatusEntryRest(String nid, String data) {
            this.data = data;
            this.nid = nid;
        }
    }
}
