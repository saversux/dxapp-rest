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

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.RandomAccessFile;

import javax.ws.rs.BadRequestException;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import com.google.gson.JsonSyntaxException;

import de.hhu.bsinfo.dxapp.rest.AbstractRestCommand;
import de.hhu.bsinfo.dxapp.rest.CommandInfo;
import de.hhu.bsinfo.dxapp.rest.ServiceHelper;
import de.hhu.bsinfo.dxapp.rest.cmd.requests.ChunkdumpRequest;
import de.hhu.bsinfo.dxmem.data.ChunkID;
import de.hhu.bsinfo.dxram.chunk.ChunkAnonService;
import de.hhu.bsinfo.dxram.chunk.data.ChunkAnon;

/**
 * Dump chunk to file
 *
 * @author Julien Bernhart, 2018-11-26
 *  @author Maximilian Loose
 *  Modifications:
 *  - in the case of a successful response, no response body is sent
 *  - parsing of the the String cid is not necessary anymmore because cids are sent as longs
 */
@Path("chunkdump")
public class Chunkdump extends AbstractRestCommand {
    @Override
    public CommandInfo setInfo() {
        return new CommandInfo("chunkdump", "name, cid", "Creates a Filedump of Chunk <cid> with <name>.");
    }

    @PUT
    @Produces(MediaType.TEXT_PLAIN)
    public String register(String body) {
        if (body.equals("")) {
            throw new BadRequestException("No body in request.");
        }
        ChunkdumpRequest chunkdumpRequest;
        try {
            chunkdumpRequest = gson.fromJson(body, ChunkdumpRequest.class);
        } catch (JsonSyntaxException e) {
            throw new BadRequestException("Please put cid and name into body as json.");
        }
        String fileName = chunkdumpRequest.getName();
        Long cid = chunkdumpRequest.getCid();

        if (cid == 0L || fileName == null) {
            throw new BadRequestException("Please put cid and name into body as json.");
        }

        if (cid == ChunkID.INVALID_ID) {
            throw new BadRequestException("Invalid ChunkID");
        }

        ChunkAnonService chunkAnon = ServiceHelper.getService(ChunkAnonService.class);

        ChunkAnon[] chunks = new ChunkAnon[1];
        if (chunkAnon.getAnon().get(chunks, cid) != 1) {
            throw new BadRequestException("Getting chunk " + ChunkID.toHexString(cid) + " failed: " + chunks[0].getState());
        }

        ChunkAnon chunk = chunks[0];

        //p_stdout.printfln("Dumping chunk 0x%X to file %s...", cid, fileName);

        File file = new File(fileName);

        if (file.exists()) {
            if (!file.delete()) {
                throw new BadRequestException("Deleting existing file " + fileName + " failed");
            }
        }

        try {
            file.createNewFile();
        } catch (IOException e) {
            throw new BadRequestException("Could not create file.");
        }

        RandomAccessFile raFile;
        try {
            raFile = new RandomAccessFile(file, "rw");
        } catch (final FileNotFoundException ignored) {
            throw new BadRequestException("Dumping chunk failed, file not found");
        }

        try {
            raFile.write(chunk.getData());
        } catch (final IOException e) {
            throw new BadRequestException("Dumping chunk failed: " + e.getMessage());
        }

        try {
            raFile.close();
        } catch (final IOException ignore) {

        }

        return "";
    }
}
