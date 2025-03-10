package org.barrelmc.barrel.network.translator.java;

import com.github.steveice10.mc.protocol.packet.ingame.server.entity.player.ServerPlayerPositionRotationPacket;
import com.github.steveice10.packetlib.packet.Packet;
import com.nukkitx.math.vector.Vector3f;
import com.nukkitx.protocol.bedrock.data.AuthoritativeMovementMode;
import com.nukkitx.protocol.bedrock.packet.MovePlayerPacket;
import org.barrelmc.barrel.network.translator.interfaces.JavaPacketTranslator;
import org.barrelmc.barrel.player.Player;

public class ClientPlayerPositionPacket implements JavaPacketTranslator {

    @Override
    public void translate(Packet pk, Player player) {
        com.github.steveice10.mc.protocol.packet.ingame.client.player.ClientPlayerPositionPacket packet = (com.github.steveice10.mc.protocol.packet.ingame.client.player.ClientPlayerPositionPacket) pk;

        if (player.isImmobile()) {
            player.getJavaSession().send(new ServerPlayerPositionRotationPacket(player.x, player.y, player.z, player.yaw, player.pitch, 1, false));
            return;
        }
        player.setOldPosition(player.getVector3f());
        player.setPosition(packet.getX(), packet.getY(), packet.getZ());

        if (player.getStartGamePacketCache().getPlayerMovementSettings().getMovementMode() == AuthoritativeMovementMode.CLIENT) {
            MovePlayerPacket movePlayerPacket = new MovePlayerPacket();

            movePlayerPacket.setRuntimeEntityId(player.getRuntimeEntityId());
            movePlayerPacket.setPosition(player.getVector3f());
            movePlayerPacket.setRotation(Vector3f.from(player.getPitch(), player.getYaw(), player.getYaw()));
            movePlayerPacket.setMode(MovePlayerPacket.Mode.NORMAL);
            movePlayerPacket.setOnGround(packet.isOnGround());
            movePlayerPacket.setRidingRuntimeEntityId(0);
            movePlayerPacket.setTeleportationCause(MovePlayerPacket.TeleportationCause.UNKNOWN);
            movePlayerPacket.setEntityType(0);

            player.getBedrockClient().getSession().sendPacket(movePlayerPacket);
        }
    }
}
