package com.voxelwind.api.server;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.ToString;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;

@Getter
@ToString(exclude = {"data"})
@RequiredArgsConstructor
public class SkinImage {
    private final int width;
    private final int height;
    private final byte[] data;

    public static SkinImage create(BufferedImage image) {
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        for(int y = 0; y < image.getHeight(); y++) {
            for(int x = 0; x < image.getWidth(); x++) {
                Color color = new Color(image.getRGB(x, y), true);
                out.write(color.getRed());
                out.write(color.getGreen());
                out.write(color.getBlue());
                out.write(color.getAlpha());
            }
        }
        image.flush();
        return new SkinImage(image.getWidth(), image.getHeight(), out.toByteArray());
    }
}
