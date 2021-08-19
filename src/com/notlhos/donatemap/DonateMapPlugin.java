package com.notlhos.donatemap;

import com.notlhos.donatemap.config.Config;
import com.notlhos.donatemap.notlhosapi.command.NotlhosBukkitCommand;
import com.notlhos.donatemap.notlhosapi.storage.GsonStorage;
import com.notlhos.donatemap.renderer.FileRenderer;
import com.notlhos.donatemap.renderer.UrlRenderer;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.MapMeta;
import org.bukkit.map.MapView;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.net.URI;
import java.util.Collection;
import java.util.Objects;

public class DonateMapPlugin extends JavaPlugin {

    public static boolean rendering = false;

    private Config config;

    private File folder;
    private FileRenderer renderer;

    @Override
    public void onEnable() {
        config = GsonStorage.load(this, "config.json", Config.class);

        folder = new File(getDataFolder(), "images");
        renderer = new FileRenderer(folder);
        renderer.load();

        Objects.requireNonNull(getCommand("dmap")).setExecutor(new NotlhosBukkitCommand() {
            @Override
            public void prepare() {
                addArgs("create", new NotlhosBukkitCommand() {
                    @Override
                    public void onReceiveCommand(CommandSender sender, Command command, String[] args) {
                        cmdVerifyPermission(sender, "dmap.create");

                        Player player = cmdVerifyPlayer(sender);
                        cmdVerifyArgs(1, args, "/dmap create <url>");

                        String url = args[0];

                        //cmdVerify(url.startsWith("https://") || url.startsWith("http://"), "Url - это ссылка на изображение в интернетах");

                        if(!url.startsWith("https://") && !url.startsWith("http://")){
                            url = "https://" + url;
                        }

                        String domainName = getDomainName(url);

                        Collection<String> domains = config.getDomains();
                        cmdVerify(domains.contains(domainName), "Разрешенные сервисы: [" + String.join(",", domains) + "]");

                        cmdVerify(!rendering, "Попробуйте срендерить изображение позже");
                        rendering = true;

                        ItemStack map = new ItemStack(Material.MAP, 1);
                        MapMeta meta = (MapMeta) map.getItemMeta();

                        MapView view = Bukkit.createMap(player.getWorld());

                        view.getRenderers().clear();
                        view.addRenderer(new UrlRenderer(folder, url));

                        map.setItemMeta(meta);
                        map.setDurability(view.getId());

                        player.getInventory().addItem(map);
                        send(sender, ChatColor.GREEN + "Получаем изображение..");
                    }
                });
            }

            @Override
            public void onReceiveCommand(CommandSender sender, Command command, String[] args) {
                send(sender, ChatColor.GREEN + "/dmap create <url>" + ChatColor.GRAY + " - чтобы создать карту с изображением");
            }
        });
    }

    private static String getDomainName(String url) {
        try {
            URI uri = new URI(url);
            String domain = uri.getHost();
            return domain.startsWith("www.") ? domain.substring(4) : domain;
        }catch (Exception exception){
            return null;
        }
    }
}
