package com.notlhos.donatemap.renderer;

import com.notlhos.donatemap.DonateMapPlugin;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.map.MapCanvas;
import org.bukkit.map.MapRenderer;
import org.bukkit.map.MapView;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.concurrent.CompletableFuture;

public class UrlRenderer extends MapRenderer {
    private final File folder;
    private final String url;
    private boolean rendered = false;

    public UrlRenderer(File folder, String url) {
        this.folder = folder;
        this.url = url;
    }

    @Override
    public void render(MapView view, MapCanvas canvas, Player player) {
        if(rendered) return;
        rendered = true;

        CompletableFuture.runAsync(() -> {
            HttpURLConnection connection = null;

            try {
                connection = (HttpURLConnection) new URL(url).openConnection();
                connection.setInstanceFollowRedirects(true);
                connection.setConnectTimeout(3000);
                connection.setReadTimeout(3000);
                connection.connect();

                InputStream inputStream = connection.getInputStream();
                BufferedImage image = ImageIO.read(ImageIO.createImageInputStream(inputStream));
                ImageIO.write(image, "JPG", new File(folder, view.getId() + ".jpg"));

                if(image.getWidth() > 128 || image.getHeight() > 128){
                    player.sendMessage(ChatColor.RED + "Слишком большое изображение, сжимаем до необходимого...");
                    canvas.drawImage(0, 0, image.getScaledInstance(128, 128, Image.SCALE_DEFAULT));
                } else canvas.drawImage(0, 0, image);

                player.sendMessage(ChatColor.GREEN + "Изображение успешно получено!");
            } catch (Exception e) {
                player.sendMessage(ChatColor.RED + "Не смогли получить изображение, попробуйте позже, либо введите другое изображение");
            } finally {
                DonateMapPlugin.rendering = false;
                if(connection != null) connection.disconnect();
            }
        });
    }
}
