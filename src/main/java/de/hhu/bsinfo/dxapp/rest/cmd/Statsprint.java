package de.hhu.bsinfo.dxapp.rest.cmd;

import de.hhu.bsinfo.dxapp.rest.ServiceHelper;
import de.hhu.bsinfo.dxapp.rest.AbstractRestCommand;
import spark.Service;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;

public class Statsprint extends AbstractRestCommand {

    public Statsprint(){
        setInfo("statsprint", "interval", "Get debug information all <interval> seconds");
    }

    @Override
    public void register(Service server, ServiceHelper services) {
        server.get("/statsprint", (request, response) -> {
            String interval = request.queryParams("interval");

            if (interval == null) {
                return createError("Please enter the refresh interval parameter: /statsprint?interval=[SECONDS]");
            }
            ByteArrayOutputStream os = new ByteArrayOutputStream();
            PrintStream ps = new PrintStream(os);

            services.statisticsService.getManager().printStatistics(ps);

            return htmlRefresh(os.toString(), interval);
        });
    }
}
