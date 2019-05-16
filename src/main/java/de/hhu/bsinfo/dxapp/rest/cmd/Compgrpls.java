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

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import de.hhu.bsinfo.dxapp.rest.AbstractRestCommand;
import de.hhu.bsinfo.dxapp.rest.CommandInfo;
import de.hhu.bsinfo.dxapp.rest.ServiceHelper;
import de.hhu.bsinfo.dxram.ms.MasterNodeEntry;
import de.hhu.bsinfo.dxram.ms.MasterSlaveComputeService;

/**
 *  List all compute groups
 *
 * @author Julien Bernhart, 2019-01-07
 */
@Path("compgrpls")
public class Compgrpls extends AbstractRestCommand {
    @Override
    public CommandInfo setInfo() {
        return new CommandInfo("compgrpls", "", "list all compute groups");
    }

    @GET
    @Produces(MediaType.TEXT_PLAIN)
    public String getCompGrps(String body) {
        MasterSlaveComputeService mscomp = ServiceHelper.getService(MasterSlaveComputeService.class);
        ArrayList<MasterNodeEntry> masters = mscomp.getMasters();

        return createMessageOfJavaObject(masters);
    }
}
