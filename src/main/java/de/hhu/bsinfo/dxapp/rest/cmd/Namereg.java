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
import de.hhu.bsinfo.dxapp.rest.cmd.requests.NameregRequest;
import de.hhu.bsinfo.dxmem.data.ChunkID;
import de.hhu.bsinfo.dxram.nameservice.NameserviceService;

/**
 * Register a Chunk in the nameservice.
 *
 * @author Julien Bernhart, 2018-11-26
 * - parsing of the the String cid is not necessary anymmore because cids are sent as longs
 */
public class Namereg extends AbstractRestCommand {

    public Namereg() {
        setInfo("namereg", "cid, name", "Register Chunk <cid> with <name>");
    }

    @Override
    public void register(Service server, ServiceHelper services) {
        server.put("/namereg", (request, response) -> {
            if (request.body().equals("")) {
                return createError("No body in request.", response);
            }
            NameregRequest nameregRequest;
            try {
                nameregRequest = gson.fromJson(request.body(), NameregRequest.class);
            } catch (JsonSyntaxException e) {
                return createError("Please put name and cid into body as json.", response);
            }
            Long cid  = nameregRequest.getCid();
            String name = nameregRequest.getName();

            if (cid == 0L || name == null) {
                return createError("Please put name and cid into body as json.",
                        response);
            }
            if (cid != ChunkID.INVALID_ID) {
                services.getService(NameserviceService.class).register(cid, name);
                response.status(200);
                return "";
            } else {
                return createError("CID invalid", response);
            }
        });
    }
}
