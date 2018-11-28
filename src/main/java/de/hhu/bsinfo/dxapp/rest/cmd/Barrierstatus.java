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
public class Barrierstatus extends AbstractRestCommand {
    public Barrierstatus() {
        setInfo("barrierstatus", "bid", "Get status of a barrier");
    }

    @Override
    public void register(Service server, ServiceHelper services) {
        server.put("/barrierstatus", (request, response) -> {
            if (request.body().equals("")) {
                return createError("No body in request.", response);
            }
            BarrierstatusRequest barrierstatusRequest;
            try {
                barrierstatusRequest = gson.fromJson(request.body(), BarrierstatusRequest.class);
            } catch (JsonSyntaxException e) {
                return createError("Please put bid into body as json.", response);
            }
            String stringBid = barrierstatusRequest.getBid();

            if (stringBid == null) {
                return createError("Please put bid into body as json.", response);
            }

            if (!isBarrierID(stringBid)) {
                return createError("Invalid BarrierID", response);
            }

            int bid = BarrierID.parse(stringBid);

            SynchronizationService sync = services.getService(SynchronizationService.class);
            BarrierStatus status = sync.barrierGetStatus(bid);

            if (status == null) {
                return createError("Getting status of barrier "+BarrierID.toHexString(bid)+" failed", response);
            }

            List<BarrierstatusEntryRest> peers = new ArrayList<>();
            for (int i = 1; i < status.getSignedOnNodeIDs().length; i++) {
                peers.add(new BarrierstatusEntryRest(NodeID.toHexString(status.getSignedOnNodeIDs()[i]), ChunkID.toHexString(status.getCustomData()[i])));
            }

            BarrierstatusRest barrierstatusRest = new BarrierstatusRest(peers, BarrierID.toHexString(bid), status.getNumberOfSignedOnPeers(), status.getSignedOnNodeIDs().length);
            return gson.toJson(barrierstatusRest);

        });
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
