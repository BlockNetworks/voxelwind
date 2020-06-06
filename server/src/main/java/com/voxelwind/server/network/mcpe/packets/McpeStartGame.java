package com.voxelwind.server.network.mcpe.packets;

import com.flowpowered.math.vector.Vector3f;
import com.flowpowered.math.vector.Vector3i;
import com.voxelwind.nbt.io.NBTWriter;
import com.voxelwind.nbt.io.util.NBTWriters;
import com.voxelwind.nbt.util.Varints;
import com.voxelwind.server.game.level.util.Gamerule;
import com.voxelwind.server.game.level.util.PaletteManager;
import com.voxelwind.server.game.permissions.PermissionLevel;
import com.voxelwind.server.network.NetworkPackage;
import com.voxelwind.server.network.mcpe.McpeUtil;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufOutputStream;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.extern.log4j.Log4j2;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@Log4j2
@Data
public class McpeStartGame implements NetworkPackage {
    private long entityId; // = null;
    private long runtimeEntityId; // = null;
    private int playerGamemode;
    private Vector3f playerPosition; // = null;
    private float pitch; // = null;
    private float yaw;
    private int seed; // = null;
    private int dimension; // = null;
    private int generator; // = null;
    private int worldGamemode; // = null;
    private int difficulty; // = null;
    private Vector3i worldSpawn; // = null;
    private boolean hasAchievementsDisabled; // = null;
    private int dayCycleStopTime; // = null;
    private int eduEditionOffer; // = null;
    private boolean eduFeaturesEnabled; // = null;
    private float rainLevel; // = null;
    private float lightingLevel; // = null;
    private boolean platformLockedContentConfirmed;
    private boolean multiplayer;
    private boolean broadcastToLan;
    private int xblBroadcastMode;
    private int platformBroadcastMode;
    private boolean enableCommands; // = null;
    private boolean texturepacksRequired; // = null;
    @Getter
    private final List<Gamerule> gameRules = new ArrayList<>();
    private boolean bonusChest;
    private boolean mapEnabled;
    private PermissionLevel permissionLevel;
    private int serverChunkTickRange;
    private boolean behaviourPackLocked;
    private boolean resourcePackLocked;
    private boolean fromLockedWorldTemplate;
    private boolean usingMsaGamertagsOnly;
    private boolean fromWorldTemplate;
    private boolean worldTemplateOptionLocked;
    private boolean spawnV1Villagers;
    private String gameVersion;
    private String levelId; // = null;
    private String worldName; // = null;
    private String premiumWorldTemplateId = "";
    private boolean trial;
    private boolean serverSideMovement;
    private long currentTick;
    private int enchantmentSeed;
    private String multiplayerCorrelationId; //44 total

    @Override
    public void decode(ByteBuf buffer) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void encode(ByteBuf buffer) {
        Varints.encodeSignedLong(buffer, entityId);
        Varints.encodeUnsigned(buffer, runtimeEntityId);
        Varints.encodeSigned(buffer, playerGamemode);
        McpeUtil.writeVector3f(buffer, playerPosition);
        McpeUtil.writeFloatLE(buffer, pitch);
        McpeUtil.writeFloatLE(buffer, yaw);
        Varints.encodeSigned(buffer, seed);
        Varints.encodeSigned(buffer, dimension);
        Varints.encodeSigned(buffer, generator);
        Varints.encodeSigned(buffer, worldGamemode);
        Varints.encodeSigned(buffer, difficulty);
        McpeUtil.writeBlockCoords(buffer, worldSpawn);
        buffer.writeBoolean(hasAchievementsDisabled);
        Varints.encodeSigned(buffer, dayCycleStopTime);
        buffer.writeBoolean(eduEditionOffer != 0);
        buffer.writeBoolean(eduFeaturesEnabled);
        buffer.writeFloatLE(rainLevel);
        buffer.writeFloatLE(lightingLevel);
        buffer.writeBoolean(platformLockedContentConfirmed);
        buffer.writeBoolean(multiplayer);
        buffer.writeBoolean(broadcastToLan);
        Varints.encodeSigned(buffer, xblBroadcastMode);
        Varints.encodeSigned(buffer, platformBroadcastMode);
        buffer.writeBoolean(enableCommands);
        buffer.writeBoolean(texturepacksRequired);
        Varints.encodeUnsigned(buffer, gameRules.size());
        for(Gamerule rule : gameRules){
            Object value = rule.getValue();
            McpeUtil.writeVarintLengthString(buffer, rule.getName());
            if(value instanceof Boolean){
                buffer.writeByte((byte) 1);
                buffer.writeBoolean((boolean) value);
            }else if(value instanceof Integer){
                buffer.writeByte((byte) 2);
                Varints.encodeUnsigned(buffer, (int) value);
            }else if(value instanceof Float){
                buffer.writeByte((byte) 3);
                McpeUtil.writeFloatLE(buffer, (float) value);
            }
        }
        buffer.writeBoolean(bonusChest);
        buffer.writeBoolean(mapEnabled);
        Varints.encodeSigned(buffer, permissionLevel.ordinal());
        buffer.writeIntLE(serverChunkTickRange);
        buffer.writeBoolean(behaviourPackLocked);
        buffer.writeBoolean(resourcePackLocked);
        buffer.writeBoolean(fromLockedWorldTemplate);
        buffer.writeBoolean(usingMsaGamertagsOnly);
        buffer.writeBoolean(fromWorldTemplate);
        buffer.writeBoolean(worldTemplateOptionLocked);
        buffer.writeBoolean(spawnV1Villagers);
        McpeUtil.writeVarintLengthString(buffer, gameVersion);
        McpeUtil.writeVarintLengthString(buffer, levelId);
        McpeUtil.writeVarintLengthString(buffer, worldName);
        McpeUtil.writeVarintLengthString(buffer, premiumWorldTemplateId);
        buffer.writeBoolean(trial);
        buffer.writeBoolean(serverSideMovement);
        buffer.writeLongLE(currentTick);
        Varints.encodeSigned(buffer, enchantmentSeed);

        // block palette
        try (NBTWriter stream = NBTWriters.createNetworkWriter(new ByteBufOutputStream(buffer))) {
            stream.write(PaletteManager.BLOCK_PALETTE);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        // item palette
        Varints.encodeUnsigned(buffer, PaletteManager.ITEM_PALETTE.size());
        for (ItemEntry entry : PaletteManager.ITEM_PALETTE) {
            McpeUtil.writeVarintLengthString(buffer, entry.getName());
            buffer.writeShortLE(entry.getId());
        }

        McpeUtil.writeVarintLengthString(buffer, multiplayerCorrelationId);
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class RuntimeEntry {
        private String name;
        private int id;
        private int data;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class ItemEntry {
        private String name;
        private short id;
    }
}
