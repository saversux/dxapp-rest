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
import de.hhu.bsinfo.dxapp.rest.cmd.requests.BarriersignonRequest;
import de.hhu.bsinfo.dxram.lookup.overlay.storage.BarrierID;
import de.hhu.bsinfo.dxram.lookup.overlay.storage.BarrierStatus;
import de.hhu.bsinfo.dxram.sync.SynchronizationService;

/**
 * Sign into an existing barrier for synchronization
 *
 * @author Julien Bernhart, 2018-11-28
 */
public class Barriersignon extends AbstractRestCommand {
    public Barriersignon() {
        setInfo("barriersignon", "bid, data", "Sign on to an allocated barrier for synchronization");
    }

    @Override
    public void register(Service server, ServiceHelper services) {
        server.put("/barriersignon", (request, response) -> {
            if (request.body().equals("")) {
                return createError("No body in request.", response);
            }
            BarriersignonRequest barriersignonRequest;
            try {
                barriersignonRequest = gson.fromJson(request.body(), BarriersignonRequest.class);
            } catch (JsonSyntaxException e) {
                return createError("Please put bid into body as json.", response);
            }
            String stringBid = barriersignonRequest.getBid();
            long data = barriersignonRequest.getData();

            if (stringBid == null) {
                return createError("Please put bid into body as json.", response);
            }

            if (!isBarrierID(stringBid)) {
                return createError("Invalid BarrierID", response);
            }

            int bid = BarrierID.parse(stringBid);

            SynchronizationService sync = services.getService(SynchronizationService.class);
            BarrierStatus result = sync.barrierSignOn(bid, data);

            if (result == null) {
                return createError("Signing on to barrier "+ BarrierID.toHexString(bid)+" failed",response);
            }

            return createMessage("Synchronized to barrier "+ BarrierID.toHexString(bid)+" with custom data: "+data);
        });
    }
}
