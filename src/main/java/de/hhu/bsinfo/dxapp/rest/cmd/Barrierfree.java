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

import com.google.gson.JsonSyntaxException;

import de.hhu.bsinfo.dxapp.rest.AbstractRestCommand;
import de.hhu.bsinfo.dxapp.rest.ServiceHelper;
import de.hhu.bsinfo.dxapp.rest.cmd.requests.BarrierfreeRequest;
import de.hhu.bsinfo.dxram.lookup.overlay.storage.BarrierID;
import de.hhu.bsinfo.dxram.sync.SynchronizationService;

/**
 * Free an allocated barrier
 *
 * @author Julien Bernhart, 2018-12-02
 */
public class Barrierfree extends AbstractRestCommand {
    public Barrierfree() {
        setInfo("barrierfree", "bid", "Free an allocated barrier");
    }

    @Override
    public void register(Service server, ServiceHelper services) {
        server.put("/barrierfree", (request, response) -> {
            if (request.body().equals("")) {
                return createError("No body in request.", response);
            }
            BarrierfreeRequest barrierfreeRequest;
            try {
                barrierfreeRequest = gson.fromJson(request.body(), BarrierfreeRequest.class);
            } catch (JsonSyntaxException e) {
                return createError("Please put bid into body as json.", response);
            }
            String stringBid = barrierfreeRequest.getBid();

            if (stringBid == null) {
                return createError("Please put bid into body as json.", response);
            }

            if (!isBarrierID(stringBid)) {
                return createError("Invalid BarrierID", response);
            }

            int bid = BarrierID.parse(stringBid);

            SynchronizationService sync = services.getService(SynchronizationService.class);

            if (!sync.barrierFree(bid)) {
                return createError("Freeing barrier failed", response);
            } else {
                return createMessage("Barrier "+BarrierID.toHexString(bid)+" free'd");
            }
        });
    }
}
