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
import de.hhu.bsinfo.dxapp.rest.cmd.requests.BarrierallocRequest;
import de.hhu.bsinfo.dxram.lookup.overlay.storage.BarrierID;
import de.hhu.bsinfo.dxram.sync.SynchronizationService;

/**
 * @author Julien Bernhart, 2018-11-28
 */
public class Barrieralloc extends AbstractRestCommand {
    public Barrieralloc() {
        setInfo("barrieralloc", "size", "Create a new barrier for synchronization of multiple peers");
    }

    @Override
    public void register(Service server, ServiceHelper services) {
        server.put("/barrieralloc", (request, response) -> {
            if (request.body().equals("")) {
                return createError("No body in request.", response);
            }
            BarrierallocRequest barrierallocRequest;
            try {
                barrierallocRequest = gson.fromJson(request.body(), BarrierallocRequest.class);
            } catch (JsonSyntaxException e) {
                return createError("Please put bid into body as json.", response);
            }
            int size = barrierallocRequest.getSize();

            if (size <= 0){
                return createError("Please put a valid size in body", response);
            }

            SynchronizationService sync = services.getService(SynchronizationService.class);

            int barrierId = sync.barrierAllocate(size);
            if (barrierId == BarrierID.INVALID_ID) {
                return createError("Allocating barrier failed", response);
            } else {
                BarrierallocRest barrierallocRest = new BarrierallocRest(BarrierID.toHexString(barrierId));
                return gson.toJson(barrierallocRest);
            }

        });
    }

    private class BarrierallocRest{
        String bid;

        public BarrierallocRest(String bid){
            this.bid = bid;
        }
    }
}
