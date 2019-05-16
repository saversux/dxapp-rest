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

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;

import javax.ws.rs.BadRequestException;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;

import de.hhu.bsinfo.dxapp.rest.AbstractRestCommand;
import de.hhu.bsinfo.dxapp.rest.CommandInfo;
import de.hhu.bsinfo.dxapp.rest.ServiceHelper;
import de.hhu.bsinfo.dxram.stats.StatisticsService;

/**
 * Return HTML page with autorefresh, which contains information about the cluster
 *
 * @author Julien Bernhart, 2018-11-26
 */
@Path("statsprint")
public class Statsprint extends AbstractRestCommand {
    @Override
    public CommandInfo setInfo() {
        return new CommandInfo("statsprint", "interval", "Get debug information all <interval> seconds");
    }

    @GET
    @Produces(MediaType.TEXT_HTML)
    public String register(@QueryParam("interval") String interval, String body) {
        if (interval == null) {
            throw new BadRequestException("Please enter the refresh interval parameter: /statsprint?interval=[SECONDS]");
        }
        ByteArrayOutputStream os = new ByteArrayOutputStream();
        PrintStream ps = new PrintStream(os);

        ServiceHelper.getService(StatisticsService.class).getManager().printStatistics(ps);

        return htmlRefresh(os.toString(), interval);
    }
}
