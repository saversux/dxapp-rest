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

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import de.hhu.bsinfo.dxapp.rest.AbstractRestCommand;
import de.hhu.bsinfo.dxapp.rest.CommandInfo;
import de.hhu.bsinfo.dxapp.rest.ServiceHelper;
import de.hhu.bsinfo.dxapp.rest.cmd.responses.NodeListResponse;
import de.hhu.bsinfo.dxram.boot.BootService;

/**
 * Get a list of all nodes
 *
 * @author Julien Bernhart, 2018-11-26
 * @author Maximilian Loose
 *  Modifications:
 *  - response body is sent with createMessageOfJavaObject method
 */
@Path("nodelist")
public class Nodelist extends AbstractRestCommand {
    @Override
    public CommandInfo setInfo() {
        return new CommandInfo("nodelist", "", "List all nodes");
    }

    @GET
    @Produces(MediaType.TEXT_PLAIN)
    public String listNodes(String body) {
        List<Short> nodes = ServiceHelper.getService(BootService.class).getOnlineNodeIDs();
        List<String> stringNodes = new ArrayList();

        for (Short node : nodes) {
            stringNodes.add(Integer.toHexString(node & 0xffff));
        }

        return createMessageOfJavaObject(new NodeListResponse(stringNodes));
    }
}
