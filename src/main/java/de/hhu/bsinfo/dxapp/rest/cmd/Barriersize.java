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
import de.hhu.bsinfo.dxapp.rest.cmd.requests.BarriersizeRequest;
import de.hhu.bsinfo.dxram.lookup.overlay.storage.BarrierID;
import de.hhu.bsinfo.dxram.sync.SynchronizationService;

/**
 * Change the size of an existing barrier
 *
 * @author Julien Bernhart, 2018-12-03
 */
public class Barriersize extends AbstractRestCommand {
    public Barriersize() {
        setInfo("barriersize", "bid, size", "Change the size of an existing barrier");
    }

    @Override
    public void register(Service server, ServiceHelper services) {
        server.put("/barriersize", (request, response) -> {
            if (request.body().equals("")) {
                return createError("No body in request.", response);
            }
            BarriersizeRequest barriersizeRequest;
            try {
                barriersizeRequest = gson.fromJson(request.body(), BarriersizeRequest.class);
            } catch (JsonSyntaxException e) {
                return createError("Please put bid and size into body as json.", response);
            }
            String stringBid = barriersizeRequest.getBid();
            int size = barriersizeRequest.getSize();

            if (stringBid == null) {
                return createError("Please put bid and size into body as json.", response);
            }

            if (!isBarrierID(stringBid)) {
                return createError("Invalid BarrierID", response);
            }

            if (size < 1){
                return createError("Enter a valid size", response);
            }

            int bid = BarrierID.parse(stringBid);

            SynchronizationService sync = services.getService(SynchronizationService.class);

            if (!sync.barrierChangeSize(bid, size)) {
                return createError("Changing barrier "+BarrierID.toHexString(bid)+" size to "+size+" failed", response);
            } else {
                return createMessage("Barrier "+BarrierID.toHexString(bid)+" size changed to "+size);
            }
        });
    }
}
