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

import de.hhu.bsinfo.dxapp.rest.cmd.responses.ChunkGetResponse;
import spark.Service;

import java.io.UnsupportedEncodingException;
import java.nio.ByteBuffer;

import com.google.gson.JsonSyntaxException;

import de.hhu.bsinfo.dxapp.rest.AbstractRestCommand;
import de.hhu.bsinfo.dxapp.rest.ServiceHelper;
import de.hhu.bsinfo.dxapp.rest.cmd.requests.AppRunRequest;
import de.hhu.bsinfo.dxapp.rest.cmd.requests.ChunkgetRequest;
import de.hhu.bsinfo.dxmem.data.ChunkID;
import de.hhu.bsinfo.dxram.chunk.ChunkAnonService;
import de.hhu.bsinfo.dxram.chunk.data.ChunkAnon;

/**
 * Get chunk with chunkid
 *
 * @author Julien Bernhart, 2018-11-26
 * @author Maximilian Loose
 * Modifications:
 * - parsing of the the String cid is not necessary anymmore because cids are sent as longs
 */
public class Chunkget extends AbstractRestCommand {

    public Chunkget() {
        setInfo("chunkget", "cid, type", "Get Chunk <cid> of Type <type>");
    }

    @Override
    public void register(Service server, ServiceHelper services) {
        server.put("/chunkget", (request, response) -> {
            long DEFAULT_VALUE_LONG = 0L;
            if (request.body().equals("")) {
                return createError("No body in request.", response);
            }
            ChunkgetRequest chunkgetRequest;
            try {
                chunkgetRequest = gson.fromJson(request.body(), ChunkgetRequest.class);
            } catch (JsonSyntaxException e) {
                return createError("Please put cid and type into body as json.", response);
            }
            long cid = chunkgetRequest.getCid();
            String type = chunkgetRequest.getType();

            if (cid == DEFAULT_VALUE_LONG || type == null) {
                return createError("Please put cid and type into body as json.",
                        response);
            }

            ChunkAnon[] chunks = new ChunkAnon[1];
            if (services.getService(ChunkAnonService.class).getAnon().get(chunks, cid) != 1) {
                return createError("Could not get Chunk", response);
            }

            ChunkAnon chunk = chunks[0];

            int offset = 0;

            boolean hex = true;

            String str = "";
            ByteBuffer byteBuffer = ByteBuffer.wrap(chunk.getData());
            byteBuffer.position(offset);
            int length = chunk.getDataSize();

            switch (type) {
                case "str":
                    try {
                        str = new String(chunk.getData(), offset, length, "US-ASCII");
                    } catch (final UnsupportedEncodingException e) {
                        return createError("Error encoding String", response);
                    }

                    break;

                case "byte":
                    for (int i = 0; i < length; i += Byte.BYTES) {
                        if (hex) {
                            str += Integer.toHexString(byteBuffer.get() & 0xFF) + ' ';
                        } else {
                            str += byteBuffer.get() + " ";
                        }
                    }
                    break;

                case "short":
                    for (int i = 0; i < length; i += java.lang.Short.BYTES) {
                        if (hex) {
                            str += Integer.toHexString(byteBuffer.getShort() & 0xFFFF) + ' ';
                        } else {
                            str += byteBuffer.getShort() + " ";
                        }
                    }
                    break;

                case "int":
                    for (int i = 0; i < length; i += java.lang.Integer.BYTES) {
                        if (hex) {
                            str += Integer.toHexString(byteBuffer.getInt()) + ' ';
                        } else {
                            str += byteBuffer.getInt() + " ";
                        }
                    }
                    break;

                case "long":
                    for (int i = 0; i < length; i += java.lang.Long.BYTES) {
                        if (hex) {
                            str += Long.toHexString(byteBuffer.getLong()) + ' ';
                        } else {
                            str += byteBuffer.getLong() + " ";
                        }
                    }
                    break;

                default:
                    return createError("Unsuported data type", response);
            }
            return createMessageOfJavaObject(new ChunkGetResponse(str));
        });
    }
}
