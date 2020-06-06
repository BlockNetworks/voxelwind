package com.voxelwind.api.server;

import com.google.common.base.Preconditions;
import lombok.NonNull;
import lombok.Value;

import javax.annotation.Nonnull;
import javax.annotation.ParametersAreNonnullByDefault;
import java.awt.image.BufferedImage;
import java.util.UUID;

/**
 * Represents a player's skin.
 */
@Value
@Nonnull
public class Skin {
    private final String skinId;
    private final String geometryName;
    private final String skinResourcePatch;
    private final SkinImage skinData;
    private final SkinImage capeData;
    private final String geometryData;
    private final boolean premium;
    private final boolean persona;
    private final boolean capeOnClassic;
    private final String capeId;
    private final String fullSkinId;

    public Skin(String skinId, String geometryName, SkinImage skinImage, SkinImage capeImage, String geometryData) {
        this.skinId = skinId;
        this.geometryName = geometryName;
        this.skinResourcePatch = convertLegacyGeometryName(geometryName);
        this.skinData = skinImage;
        this.capeData = capeImage;
        this.geometryData = geometryData;
        this.premium = false;
        this.persona = false;
        this.capeOnClassic = false;
        this.capeId = "";
        this.fullSkinId = UUID.randomUUID().toString();
    }

    @Nonnull
    @ParametersAreNonnullByDefault
    public static Skin create(BufferedImage image) {
        Preconditions.checkNotNull(image, "image");
        Preconditions.checkArgument(image.getHeight() == 32 && image.getWidth() == 64, "Image is not 32x64");

        byte[] mcpeTexture = new byte[32 * 64 * 4];

        int at = 0;
        for (int i = 0; i < image.getHeight(); i++) {
            for (int i1 = 0; i1 < image.getWidth(); i1++) {
                int rgb = image.getRGB(i, i1);
                mcpeTexture[at++] = (byte) ((rgb & 0x00ff0000) >> 16);
                mcpeTexture[at++] = (byte) ((rgb & 0x0000ff00) >> 8);
                mcpeTexture[at++] = (byte) (rgb & 0x000000ff);
                mcpeTexture[at++] = (byte) ((rgb >> 24) & 0xff);
            }
        }

            return new Skin("Standard_Custom", "geometry.humanoid.custom", SkinImage.create(image), new SkinImage(0, 0, new byte[0]), "");
    }

    private static String convertLegacyGeometryName(String geometryName) {
        return "{\"geometry\" : {\"default\" : \"" + geometryName + "\"}}";
    }
}
