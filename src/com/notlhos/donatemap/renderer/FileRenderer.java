package com.notlhos.donatemap.renderer;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.map.MapCanvas;
import org.bukkit.map.MapRenderer;
import org.bukkit.map.MapView;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Set;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CopyOnWriteArraySet;

public class FileRenderer extends MapRenderer {
    private Set<Short> loadedMaps = new CopyOnWriteArraySet<>();

    private final File folder;

    public FileRenderer(File folder) {
        this.folder = folder;

        if(!folder.exists()){
            folder.mkdirs();
        }
    }

    public void load(){
        for(File image : folder.listFiles()){
            String imageName = image.getName();
            if(!imageName.endsWith(".jpg")) continue;

            try {
                MapView map = Bukkit.getMap(Short.parseShort(imageName.replace(".jpg", "")));

                map.getRenderers().clear();
                map.addRenderer(this);

            }catch (Exception exception) { }
        }
    }

    @Override
    public void render(MapView view, MapCanvas canvas, Player player) {
        if(loadedMaps.contains(view.getId())) return;

        CompletableFuture.runAsync(() -> {
            try {
                Path img = folder.toPath().resolve(view.getId() + ".jpg");
                BufferedImage image = ImageIO.read(ImageIO.createImageInputStream(Files.newInputStream(img)));

                if(image.getWidth() > 128 || image.getHeight() > 128){
                    canvas.drawImage(0, 0, image.getScaledInstance(128, 128, Image.SCALE_DEFAULT));
                } else canvas.drawImage(0, 0, image);

                loadedMaps.add(view.getId());
            } catch (Exception ignored) { }
        });
    }
}
