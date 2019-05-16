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

import javax.ws.rs.BadRequestException;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import com.google.gson.JsonSyntaxException;

import de.hhu.bsinfo.dxapp.rest.AbstractRestCommand;
import de.hhu.bsinfo.dxapp.rest.CommandInfo;
import de.hhu.bsinfo.dxapp.rest.ServiceHelper;
import de.hhu.bsinfo.dxapp.rest.cmd.requests.BarrierallocRequest;
import de.hhu.bsinfo.dxram.lookup.overlay.storage.BarrierID;
import de.hhu.bsinfo.dxram.sync.SynchronizationService;

/**
 * Create a new barrier
 *
 * @author Julien Bernhart, 2018-11-28
 */
@Path("barrieraloc")
public class Barrieralloc extends AbstractRestCommand {
    @Override
    public CommandInfo setInfo() {
        return new CommandInfo("barrieralloc", "size", "Create a new barrier for synchronization of multiple peers");
    }

    @PUT
    @Produces(MediaType.TEXT_PLAIN)
    public String barrierAloc(String body) {
        if (body.equals("")) {
            throw new BadRequestException("No body in request.");
        }
        BarrierallocRequest barrierallocRequest;
        try {
            barrierallocRequest = gson.fromJson(body, BarrierallocRequest.class);
        } catch (JsonSyntaxException e) {
            throw new BadRequestException("Please put bid into body as json.");
        }
        int size = barrierallocRequest.getSize();

        if (size <= 0){
            throw new BadRequestException("Please put bid into body as json.");
        }

        SynchronizationService sync = ServiceHelper.getService(SynchronizationService.class);

        int barrierId = sync.barrierAllocate(size);
        if (barrierId == BarrierID.INVALID_ID) {
            throw new BadRequestException("Allocating barrier failed");
        } else {
            BarrierallocRest barrierallocRest = new BarrierallocRest(BarrierID.toHexString(barrierId));
            return gson.toJson(barrierallocRest);
        }
    }

    private class BarrierallocRest{
        String bid;

        public BarrierallocRest(String bid){
            this.bid = bid;
        }
    }
}
