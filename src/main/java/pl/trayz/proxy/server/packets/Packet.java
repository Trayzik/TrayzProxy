package pl.trayz.proxy.server.packets;

import lombok.Data;

/**
 * @Author: Trayz
 **/

/**
 * Packet abstract class
 * If you want to create new packet, you need to extend this class and give parameters.
 */
@Data
public abstract class Packet {

    private int packetID;
    private byte[] customData;
    private boolean custom;

    public abstract void write(PacketBuffer out) throws Exception;

    public abstract void read(PacketBuffer in) throws Exception;

    public void setCustom(int id, byte[] data) {
        this.custom = true;
        this.packetID = id;
        this.customData = data;
    }

}
