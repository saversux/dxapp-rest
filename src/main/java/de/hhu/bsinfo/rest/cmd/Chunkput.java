package de.hhu.bsinfo.rest.cmd;

import de.hhu.bsinfo.dxram.data.ChunkAnon;
import de.hhu.bsinfo.dxram.data.ChunkID;
import de.hhu.bsinfo.dxutils.NodeID;
import de.hhu.bsinfo.rest.AbstractRestCommand;
import de.hhu.bsinfo.rest.ServiceHelper;
import spark.Service;

import java.nio.ByteBuffer;

public class Chunkput extends AbstractRestCommand {
    @Override
    public void register(Service server, ServiceHelper services) {
        server.get("/chunkput/:cid/:type/:data", (request, response) -> {
            long cid = ChunkID.parse(request.params(":cid"));
            String data = request.params(":data");
            String type = request.params(":type");
            int offset = 0;

            if (cid == ChunkID.INVALID_ID) {
                return "No cid specified";
            }

            if (data == null) {
                return "No data specified";
            }

            if (ChunkID.getLocalID(cid) == 0) {
                return "Put of index chunk is not allowed";
            }

            ChunkAnon[] chunks = new ChunkAnon[1];

            if (services.chunkAnonService.get(chunks, cid) != 1) {
                return "Getting chunk 0x"+cid+" failed: " + chunks[0].getState();
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
                    return "Unsupported data type.";
            }

            // put chunk back
            if (services.chunkAnonService.put(chunk) != 1) {
                return "Put to chunk 0x"+cid+" failed: "+chunk.getState();
            } else {
                return "Put to chunk 0x"+cid+" successful";
            }




        });
    }
}
