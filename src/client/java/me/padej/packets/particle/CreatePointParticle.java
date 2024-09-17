package me.padej.packets.particle;

import net.minecraft.client.MinecraftClient;
import net.minecraft.util.math.BlockPos;

import java.util.List;

import static me.padej.packets.Packets.POINT;
import static me.padej.packets.Packets.POS_POINT;

public class CreatePointParticle {
    public static void afterExplosion(List<BlockPos> affectedBlocks, double X, double Y, double Z) {
        double maxDistance = 0;
        for (BlockPos pos : affectedBlocks) {
            double distance = Math.sqrt(pos.getSquaredDistance(X, Y, Z));
            if (distance > maxDistance) {
                maxDistance = distance;
            }
        }
        for (BlockPos pos : affectedBlocks) {
            double distance = Math.sqrt(pos.getSquaredDistance(X, Y, Z));
            PointParticle.proportion = distance / maxDistance;
            MinecraftClient.getInstance().world.addParticle(
                    POINT,
                    pos.getX() + 0.5,
                    pos.getY() + 0.5,
                    pos.getZ() + 0.5,
                    0, 0, 0
            );
        }
    }

    public static void defaultParticle(double X, double Y, double Z, int firstColor, int secondColor, float size, int age, float gravity) {
        PosPointParticle.firstColor = firstColor;
        PosPointParticle.secondColor = secondColor;
        PosPointParticle.size = size;
        PosPointParticle.age = age;
        PosPointParticle.gravity = gravity;
        MinecraftClient.getInstance().world.addParticle(POS_POINT, X, Y, Z, 0, 0, 0);
        particleReset();
    }

    public static void particleReset() {
        PosPointParticle.firstColor = 0xFFFFFF;
        PosPointParticle.secondColor = 0xBEBEBE;
        PosPointParticle.size = .75F;
        PosPointParticle.age = 60;
    }

}
