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

import java.nio.ByteBuffer;

import javax.ws.rs.BadRequestException;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import com.google.gson.JsonSyntaxException;

import de.hhu.bsinfo.dxapp.rest.AbstractRestCommand;
import de.hhu.bsinfo.dxapp.rest.CommandInfo;
import de.hhu.bsinfo.dxapp.rest.ServiceHelper;
import de.hhu.bsinfo.dxapp.rest.cmd.requests.ChunkputRequest;
import de.hhu.bsinfo.dxmem.data.ChunkID;
import de.hhu.bsinfo.dxram.chunk.ChunkAnonService;
import de.hhu.bsinfo.dxram.chunk.data.ChunkAnon;

/**
 * Put data with specific type on chunk
 *
 * @author Julien Bernhart, 2018-11-26
 * @author Maximilian Loose,
 * Modifications:
 *   - parsing of the the String cid is not necessary anymmore because cids are sent as longs
 *   - in the case of an succesful response a response body is not needed. The value of the status code is
 *   enough to indicate a succesful response.
 *
 */
@Path("chunkput")
public class Chunkput extends AbstractRestCommand {
    @Override
    public CommandInfo setInfo() {
        return new CommandInfo("chunkput", "cid, data, type", "Put <data> with <type> on Chunk with <cid>");
    }

    @PUT
    @Produces(MediaType.TEXT_PLAIN)
    public String register(String body) {
        long DEFAULT_VALUE_LONG = 0L;
        if (body.equals("")) {
            throw new BadRequestException("No body in request.");
        }
        ChunkputRequest chunkputRequest;
        try {
            chunkputRequest = gson.fromJson(body, ChunkputRequest.class);
        } catch (JsonSyntaxException e) {
            throw new BadRequestException("Please put cid, type and data into body as json.");
        }

        Long cid = chunkputRequest.getCid();
        String data = chunkputRequest.getData();
        String type = chunkputRequest.getType();

        ChunkAnonService chunkAnon = ServiceHelper.getService(ChunkAnonService.class);
        int offset = 0;

        if (cid == DEFAULT_VALUE_LONG || data == null || type == null) {
            throw new BadRequestException("Please put cid, type and data into body as json.");
        }

        if (cid == ChunkID.INVALID_ID) {
            throw new BadRequestException("No cid specified");
        }

        if (data == null) {
            throw new BadRequestException("No data specified");
        }

        if (ChunkID.getLocalID(cid) == 0) {
            throw new BadRequestException("Put of index chunk is not allowed");
        }

        ChunkAnon[] chunks = new ChunkAnon[1];

        if (chunkAnon.getAnon().get(chunks, cid) != 1) {
            throw new BadRequestException("Getting chunk " + ChunkID.toHexString(cid) + " failed: " + chunks[0].getState());
        }

        ChunkAnon chunk = chunks[0];
        if (offset == -1) {
            // wipe chunk
            for (int i = 0; i < chunk.getDataSize(); i++) {
                chunk.getData()[i] = 0;
            }
            offset = 0;
        }

        if (offset > chunk.sizeofObject()) {
            offset = chunk.sizeofObject();
        }

        ByteBuffer byteBuffer = ByteBuffer.wrap(chunk.getData());
        byteBuffer.position(offset);

        switch (type) {
            case "str":
                byte[] bytes = data.getBytes(java.nio.charset.StandardCharsets.US_ASCII);

                try {
                    int size = byteBuffer.capacity() - byteBuffer.position();
                    if (bytes.length < size) {
                        size = bytes.length;
                    }
                    byteBuffer.put(bytes, 0, size);
                } catch (final Exception ignored) {
                    // that's fine, trunc data
                }

                break;

            case "byte":
                try {
                    byteBuffer.put((byte) (Integer.parseInt(data) & 0xFF));
                } catch (final Exception ignored) {
                    // that's fine, trunc data
                }

                break;

            case "short":
                try {
                    byteBuffer.putShort((short) (Integer.parseInt(data) & 0xFFFF));
                } catch (final Exception ignored) {
                    // that's fine, trunc data
                }

                break;

            case "int":
                try {
                    byteBuffer.putInt(Integer.parseInt(data));
                } catch (final Exception ignored) {
                    // that's fine, trunc data
                }

                break;

            case "long":
                try {
                    byteBuffer.putLong(Long.parseLong(data));
                } catch (final Exception ignored) {
                    // that's fine, trunc data
                }

                break;

            case "hex":
                String[] tokens = data.split(" ");

                for (String str : tokens) {
                    try {
                        byteBuffer.put((byte) Integer.parseInt(str, 16));
                    } catch (final Exception ignored) {
                        // that's fine, trunc data
                    }
                }

                break;

            default:
                throw new BadRequestException("Unsupported data type.");
        }

        // put chunk back
        if (chunkAnon.putAnon().put(chunk) != 1) {
            throw new BadRequestException("Put to chunk " + ChunkID.toHexString(cid) + " failed: " + chunk.getState());
        } else {
            return "";
        }
    }
}
