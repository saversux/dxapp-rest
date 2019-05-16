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
import de.hhu.bsinfo.dxapp.rest.cmd.requests.BarriersizeRequest;
import de.hhu.bsinfo.dxram.lookup.overlay.storage.BarrierID;
import de.hhu.bsinfo.dxram.sync.SynchronizationService;

/**
 * Change the size of an existing barrier
 *
 * @author Julien Bernhart, 2018-12-03
 */
@Path("barriersize")
public class Barriersize extends AbstractRestCommand {
    @Override
    public CommandInfo setInfo() {
        return new CommandInfo("barriersize", "bid, size", "Change the size of an existing barrier");
    }

    @PUT
    @Produces(MediaType.TEXT_PLAIN)
    public String barrierSize(String body) {
        if (body.equals("")) {
            throw new BadRequestException("No body in request.");
        }
        BarriersizeRequest barriersizeRequest;
        try {
            barriersizeRequest = gson.fromJson(body, BarriersizeRequest.class);
        } catch (JsonSyntaxException e) {
            throw new BadRequestException("Please put bid and size into body as json.");
        }
        String stringBid = barriersizeRequest.getBid();
        int size = barriersizeRequest.getSize();

        if (stringBid == null) {
            throw new BadRequestException("Please put bid and size into body as json.");
        }

        if (!isBarrierID(stringBid)) {
            throw new BadRequestException("Invalid BarrierID");
        }

        if (size < 1){
            throw new BadRequestException("Enter a valid size");
        }

        int bid = BarrierID.parse(stringBid);

        SynchronizationService sync = ServiceHelper.getService(SynchronizationService.class);

        if (!sync.barrierChangeSize(bid, size)) {
            throw new BadRequestException("Changing barrier "+BarrierID.toHexString(bid)+" size to "+size+" failed");
        } else {
            return createMessage("Barrier "+BarrierID.toHexString(bid)+" size changed to "+size);
        }
    }
}
