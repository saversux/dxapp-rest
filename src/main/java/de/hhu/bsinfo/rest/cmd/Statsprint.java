package de.hhu.bsinfo.rest.cmd;

import de.hhu.bsinfo.dxram.nameservice.NameserviceEntryStr;
import de.hhu.bsinfo.rest.AbstractRestCommand;
import de.hhu.bsinfo.rest.ServiceHelper;
import spark.Service;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.util.List;

public class Statsprint extends AbstractRestCommand{
    @Override
    public void register(Service server, ServiceHelper services) {
        server.get("/statsprint", (request, response) -> {
            String interval = request.queryParams("interval");

            if (interval == null){
                return createError("Please enter the refresh interval parameter: /statsprint?interval=[SECONDS]");
            }
            ByteArrayOutputStream os = new ByteArrayOutputStream();
            PrintStream ps = new PrintStream(os);

            services.statisticsService.getManager().printStatistics(ps);

            return htmlRefresh(os.toString(), interval);
        });
    }
}
