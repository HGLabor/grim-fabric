package ac.grim.grimac.checks.impl.misc;

import ac.grim.grimac.api.config.ConfigManager;
import ac.grim.grimac.checks.type.BlockPlaceCheck;
import ac.grim.grimac.player.GrimPlayer;
import ac.grim.grimac.utils.anticheat.update.BlockPlace;
import com.github.retrooper.packetevents.util.Vector3i;
import net.minecraft.block.BlockState;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class GhostBlockMitigation extends BlockPlaceCheck {

    private boolean allow;
    private int distance;

    public GhostBlockMitigation(GrimPlayer player) {
        super(player);
    }

    @Override
    public void onBlockPlace(final BlockPlace place) {
        if (allow || player.bukkitPlayer == null) return;

        World world = player.bukkitPlayer.getWorld();
        Vector3i pos = place.getPlacedBlockPos();
        Vector3i posAgainst = place.getPlacedAgainstBlockLocation();

        int x = pos.getX();
        int y = pos.getY();
        int z = pos.getZ();

        int xAgainst = posAgainst.getX();
        int yAgainst = posAgainst.getY();
        int zAgainst = posAgainst.getZ();

        try {
            for (int i = x - distance; i <= x + distance; i++) {
                for (int j = y - distance; j <= y + distance; j++) {
                    for (int k = z - distance; k <= z + distance; k++) {
                        if (i == x && j == y && k == z) {
                            continue;
                        }

                        if (i == xAgainst && j == yAgainst && k == zAgainst) {
                            continue;
                        }

                        if (!world.isChunkLoaded(i >> 4, k >> 4)) {
                            continue;
                        }

                        BlockState type = world.getBlockState(new BlockPos(i, j, k));
                        if (!type.isAir()) {
                            return;
                        }
                    }
                }
            }

            place.resync();
        } catch (Exception ignored) {
        }
    }

    @Override
    public void onReload(ConfigManager config) {
        allow = config.getBooleanElse("exploit.allow-building-on-ghostblocks", true);
        distance = config.getIntElse("exploit.distance-to-check-for-ghostblocks", 2);

        if (distance < 2 || distance > 4) distance = 2;
    }
}
