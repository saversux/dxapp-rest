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
import de.hhu.bsinfo.dxapp.rest.cmd.requests.BarriersignonRequest;
import de.hhu.bsinfo.dxram.lookup.overlay.storage.BarrierID;
import de.hhu.bsinfo.dxram.lookup.overlay.storage.BarrierStatus;
import de.hhu.bsinfo.dxram.sync.SynchronizationService;

/**
 * Sign into an existing barrier for synchronization
 *
 * @author Julien Bernhart, 2018-11-28
 */
@Path("barriersignon")
public class Barriersignon extends AbstractRestCommand {
    @Override
    public CommandInfo setInfo() {
        return new CommandInfo("barriersignon", "bid, data", "Sign on to an allocated barrier for synchronization");
    }

    @PUT
    @Produces(MediaType.TEXT_PLAIN)
    public String barrierSignon(String body) {
        if (body.equals("")) {
            throw new BadRequestException("No body in request.");
        }
        BarriersignonRequest barriersignonRequest;
        try {
            barriersignonRequest = gson.fromJson(body, BarriersignonRequest.class);
        } catch (JsonSyntaxException e) {
            throw new BadRequestException("Please put bid into body as json.");
        }
        String stringBid = barriersignonRequest.getBid();
        long data = barriersignonRequest.getData();

        if (stringBid == null) {
            throw new BadRequestException("Please put bid into body as json.");
        }

        if (!isBarrierID(stringBid)) {
            throw new BadRequestException("Invalid BarrierID");
        }

        int bid = BarrierID.parse(stringBid);

        SynchronizationService sync = ServiceHelper.getService(SynchronizationService.class);
        BarrierStatus result = sync.barrierSignOn(bid, data, false);

        if (result == null) {
            throw new BadRequestException("Signing on to barrier "+ BarrierID.toHexString(bid)+" failed");
        }

        return createMessage("Synchronized to barrier "+ BarrierID.toHexString(bid)+" with custom data: "+data);
    }
}
