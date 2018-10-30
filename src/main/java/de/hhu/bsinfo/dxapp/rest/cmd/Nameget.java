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

import de.hhu.bsinfo.dxapp.rest.AbstractRestCommand;
import de.hhu.bsinfo.dxapp.rest.ServiceHelper;
import de.hhu.bsinfo.dxmem.data.ChunkID;
import de.hhu.bsinfo.dxram.nameservice.NameserviceService;

public class Nameget extends AbstractRestCommand {
    public Nameget() {
        setInfo("nameget", "name", "Get chunk by name from nameservice.");
    }

    @Override
    public void register(Service server, ServiceHelper services) {
        server.get("/nameget", (request, response) -> {
            String name = request.queryParams("name");

            if (name == null) {
                createError("Invalid Parameter, please use: /nameget?=[name]", response);
            }

            NameserviceService nameservice = services.getService(NameserviceService.class);

            long cid = nameservice.getChunkID(name, 2000);

            if (cid == ChunkID.INVALID_ID) {
                return createError("Could not get name entry for " + name + ", does not exist", response);
            } else {
                return gson.toJson(ChunkID.toHexString(cid));
            }

        });
    }
}
