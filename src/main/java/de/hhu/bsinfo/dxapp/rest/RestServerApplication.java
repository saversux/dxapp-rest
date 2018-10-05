package de.hhu.bsinfo.dxapp.rest;

import com.google.gson.Gson;
import de.hhu.bsinfo.dxapp.rest.cmd.*;
import de.hhu.bsinfo.dxram.app.AbstractApplication;
import de.hhu.bsinfo.dxram.boot.BootService;
import de.hhu.bsinfo.dxram.chunk.ChunkAnonService;
import de.hhu.bsinfo.dxram.chunk.ChunkService;
import de.hhu.bsinfo.dxram.engine.AbstractDXRAMService;
import de.hhu.bsinfo.dxram.engine.DXRAMVersion;
import de.hhu.bsinfo.dxram.generated.BuildConfig;
import de.hhu.bsinfo.dxram.monitoring.MonitoringService;
import de.hhu.bsinfo.dxram.nameservice.NameserviceService;
import de.hhu.bsinfo.dxram.stats.StatisticsService;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import spark.Service;

import java.util.ArrayList;
import java.util.List;

public class RestServerApplication extends AbstractApplication {
    private static Service server;
    private volatile boolean run;
    private Gson gson;
    private static final Logger LOGGER = LogManager.getFormatterLogger(RestServerApplication.class.getSimpleName());

    @Override
    public DXRAMVersion getBuiltAgainstVersion() {
        return BuildConfig.DXRAM_VERSION;
    }

    @Override
    public String getApplicationName() {
        return "RestServer";
    }

    @Override
    public void main(String[] args) {
        ServiceHelper services = new ServiceHelper(this);

        gson = new Gson();
        run = true;

        startServer(2);

        List<Object> commandInfo = new ArrayList<>();
        List<AbstractRestCommand> restCommands = new ArrayList<>();
        restCommands.add(new Chunkget());
        restCommands.add(new Chunklist());
        restCommands.add(new Nodelist());
        restCommands.add(new Namereg());
        restCommands.add(new Namelist());
        restCommands.add(new Chunkcreate());
        restCommands.add(new Chunkput());
        restCommands.add(new Chunkdump());
        restCommands.add(new Statsprint());
        restCommands.add(new Monitoring());
        restCommands.add(new AppList());
        restCommands.add(new AppRun());
        restCommands.add(new Chunkremove());
        restCommands.add(new Nodeinfo());
        restCommands.add(new Nameget());
        restCommands.add(new LogInfo());
        restCommands.add(new Lookuptree());
        restCommands.add(new Metadata());

        for (AbstractRestCommand c : restCommands) {
            c.register(server, services);
            commandInfo.add(c.getInfo());
        }

        server.get("/", (req, res) -> gson.toJson(commandInfo));
        LOGGER.debug("REST SERVER started");

        while (run) {
            try {
                sleep(10000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    protected <T extends AbstractDXRAMService> T getService(final Class<T> p_class) {
        return super.getService(p_class);
    }

    private static void startServer(int maxThreads) {
        server = Service.ignite().port(8009);
    }

    @Override
    public void signalShutdown() {
        server.stop();
        run = false;
    }

}
