package de.hhu.bsinfo.rest;

import de.hhu.bsinfo.dxram.boot.BootService;
import de.hhu.bsinfo.dxram.chunk.ChunkService;
import de.hhu.bsinfo.dxram.nameservice.NameserviceService;

public class ServiceHelper {
   public BootService bootService;
   public NameserviceService nameService;
   public ChunkService chunkService;

   public ServiceHelper(BootService bootService, NameserviceService nameService, ChunkService chunkService){
       this.bootService = bootService;
       this.nameService = nameService;
       this.chunkService = chunkService;

   }

}
