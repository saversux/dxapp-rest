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

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;

import de.hhu.bsinfo.dxapp.rest.AbstractRestCommand;
import de.hhu.bsinfo.dxapp.rest.ServiceHelper;
import de.hhu.bsinfo.dxram.stats.StatisticsService;

/**
 * Return HTML page with autorefresh, which contains information about the cluster
 *
 * @author Julien Bernhart, 2018-11-26
 */
public class Statsprint extends AbstractRestCommand {

    public Statsprint() {
        setInfo("statsprint", "interval", "Get debug information all <interval> seconds");
    }

    @Override
    public void register(Service server, ServiceHelper services) {
        server.get("/statsprint", (request, response) -> {
            String interval = request.queryParams("interval");

            if (interval == null) {
                return createError("Please enter the refresh interval parameter: /statsprint?interval=[SECONDS]",
                        response);
            }
            ByteArrayOutputStream os = new ByteArrayOutputStream();
            PrintStream ps = new PrintStream(os);

            services.getService(StatisticsService.class).getManager().printStatistics(ps);

            return htmlRefresh(os.toString(), interval);
        });
    }
}
