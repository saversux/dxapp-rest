package de.hhu.bsinfo.dxapp.rest;

import de.hhu.bsinfo.dxapp.rest.cmd.Monitoring;
import de.hhu.bsinfo.dxram.boot.BootService;
import de.hhu.bsinfo.dxram.chunk.ChunkAnonService;
import de.hhu.bsinfo.dxram.chunk.ChunkService;
import de.hhu.bsinfo.dxram.monitoring.MonitoringService;
import de.hhu.bsinfo.dxram.nameservice.NameserviceService;
import de.hhu.bsinfo.dxram.stats.StatisticsService;

public class ServiceHelper {
    public BootService bootService;
    public NameserviceService nameService;
    public ChunkService chunkService;
    public ChunkAnonService chunkAnonService;
    public StatisticsService statisticsService;
    public MonitoringService monitoringService;

    public ServiceHelper(BootService bootService, NameserviceService nameService, ChunkService chunkService, ChunkAnonService chunkAnonService, StatisticsService statisticsService, MonitoringService monitoringService) {
        this.bootService = bootService;
        this.nameService = nameService;
        this.chunkService = chunkService;
        this.chunkAnonService = chunkAnonService;
        this.statisticsService = statisticsService;
        this.monitoringService = monitoringService;

    }

}
