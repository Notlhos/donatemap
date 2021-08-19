package com.notlhos.donatemap.notlhosapi.command;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.function.Supplier;

public abstract class NotlhosBukkitCommand implements CommandExecutor {

    private static String[] EMPTY_ARRAY = new String[]{};

    private final Map<String, NotlhosBukkitCommand> commandArgs = new HashMap<>();

    public NotlhosBukkitCommand() {
        prepare();
    }

    @Override
    public boolean onCommand(CommandSender commandSender, Command command, String s, String[] args) {
        try {
            if(commandArgs.size() > 0 && args.length > 0 && commandArgs.containsKey(args[0])){
                commandArgs.get(args[0]).onReceiveCommand(commandSender, command, args.length > 1 ? Arrays.copyOfRange(args, 1, args.length) : EMPTY_ARRAY);
            } else onReceiveCommand(commandSender, command, args);
        }catch (RuntimeException exception){
            commandSender.sendMessage(exception.getMessage());
        }
        return false;
    }

    public abstract void onReceiveCommand(CommandSender sender, Command command, String[] args);

    public void prepare(){}

    public void addArgs(String name, NotlhosBukkitCommand command){
        commandArgs.putIfAbsent(name, command);
    }

    public void cmdVerifyArgs(int minimum, String[] args, String usage){
        if(args.length < minimum){
            throw new RuntimeException(ChatColor.RED + usage);
        }
    }

    public void cmdError(String message){
        throw new RuntimeException(ChatColor.RED + message);
    }

    public Player cmdVerifyPlayer(CommandSender sender){
        if(!(sender instanceof Player)){
            throw new RuntimeException(ChatColor.RED + "Must be player");
        }
        return (Player) sender;
    }

    public void cmdVerifyPermission(CommandSender sender, String permission){
        if(!sender.hasPermission(permission)){
            throw new RuntimeException(ChatColor.RED + "Do not have permission");
        }
    }

    public void cmdVerify(boolean predicate, String usage){
        if(!predicate){
            throw new RuntimeException(ChatColor.RED + usage);
        }
    }

    public <T> T cmdVerifyValue(Supplier<T> supplier, String usage){
        try {
            return supplier.get();
        }catch (Exception ex){
            throw new RuntimeException(ChatColor.RED + usage);
        }
    }

    public <T> T cmdVerifyOptional(Optional<T> optional, String usage){
        if(!optional.isPresent()){
            throw new RuntimeException(ChatColor.RED + usage);
        }
        return optional.get();
    }

    public int cmdVerifyInt(String number){
        try {
            return Integer.parseInt(number);
        }catch (NumberFormatException e){
            throw new RuntimeException(ChatColor.RED + "Can't cast word to number");
        }
    }

    public byte cmdVerifyByte(String number){
        try {
            return Byte.parseByte(number);
        }catch (NumberFormatException e){
            throw new RuntimeException(ChatColor.RED + "Can't cast word to number");
        }
    }

    public double cmdVerifyDouble(String number){
        try {
            return Double.parseDouble(number);
        }catch (NumberFormatException e){
            throw new RuntimeException(ChatColor.RED + "Can't cast word to number");
        }
    }

    public void send(CommandSender sender, String... messages){
        send(sender, true, messages);
    }

    public void send(CommandSender sender, boolean predicate, String... messages){
        if(predicate){
            for(String message : messages) {
                sender.sendMessage(message);
            }
        }
    }


}
