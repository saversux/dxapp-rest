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

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import de.hhu.bsinfo.dxapp.rest.AbstractRestCommand;
import de.hhu.bsinfo.dxapp.rest.ServiceHelper;
import de.hhu.bsinfo.dxapp.rest.cmd.responses.ApplicationProcessResponse;
import de.hhu.bsinfo.dxram.app.ApplicationProcess;
import de.hhu.bsinfo.dxram.app.ApplicationService;

/**
 * @author Julien Bernhart, 2019-01-07
 */
public class AppStats extends AbstractRestCommand {
    public AppStats() {
        setInfo("appstats", "", "Shows information about all running applications");
    }
    @Override
    public void register(Service server, ServiceHelper services) {
        server.get("/appstats", (request, response) -> {
            ApplicationService appService = services.getService(ApplicationService.class);

            Iterator<ApplicationProcess> iterator = appService.getRunningProcesses().iterator();
            List<ApplicationProcessResponse> list = new ArrayList<>();

            while(iterator.hasNext()){
                ApplicationProcess item = iterator.next();
                list.add(new ApplicationProcessResponse(item.getName(), item.getId(), item.getElapsedTime(), item.getArguments()));
            }

            return createMessageOfJavaObject(list);
        });
    }
}
