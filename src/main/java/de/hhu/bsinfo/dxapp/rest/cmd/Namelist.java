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
import de.hhu.bsinfo.dxapp.rest.cmd.responses.NameListResponse;
import de.hhu.bsinfo.dxram.nameservice.NameserviceService;

/**
 * Get all nameservice entries
 *
 * @author Julien Bernhart, 2018-11-26
 * @author Maximilian Loose
 *  * Modifications:
 *  * - response body is sent with createMessageOfJavaObject method
 */
public class Namelist extends AbstractRestCommand {

    public Namelist() {
        setInfo("namelist", "", "Get Namelist");
    }

    @Override
    public void register(Service server, ServiceHelper services) {
        server.get("/namelist", (request, response) -> {
            NameListResponse entries = new NameListResponse(services.getService(NameserviceService.class).getAllEntries());
            return createMessageOfJavaObject(entries);
        });
    }

}
