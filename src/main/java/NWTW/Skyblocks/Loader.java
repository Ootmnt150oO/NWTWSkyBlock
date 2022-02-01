package NWTW.Skyblocks;

import cn.nukkit.Player;
import cn.nukkit.block.Block;
import cn.nukkit.level.Level;
import cn.nukkit.level.generator.Generator;
import cn.nukkit.plugin.PluginBase;
import cn.nukkit.scheduler.AsyncTask;
import cn.nukkit.utils.Config;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

public class Loader extends PluginBase {
    private final ArrayList<Land> lands = new ArrayList<>();
    private static Loader instance;
    private  String path;
    private Config config;
    public static Loader getInstance() {
        return instance;
    }
    DataBase dataBase;
    @Override
    public void onEnable() {
        instance = this;
        path = getDataFolder()+"/database.db";
        String cfgp = getDataFolder() + "/";
        File files = new File(cfgp);
        if (!files.exists()){
            files.mkdirs();
        }
        config = new Config(cfgp+"/database.yml",Config.YAML);
        config.save();
        File file = new File(path);
        if (!file.exists()){
            try {
                file.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
         dataBase = new DataBase();
        try {
            dataBase.connect();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
        dataBase.createTable();
        getServer().getCommandMap().register("sk",new createCMD());
        Generator.addGenerator(EmptyGenerator.class, "emptyworld", 1);
        getServer().getPluginManager().registerEvents(new PlayerListener(),this);
        getServer().getPluginManager().registerEvents(new FormListener(),this);
        getServer().getScheduler().scheduleAsyncTask(this, new AsyncTask() {
            @Override
            public void onRun() {
                for (String s:getConfigs().getKeys())
                dataBase.loadLand(s);
            }
        });
        super.onEnable();
    }

    @Override
    public void onDisable() {
        super.onDisable();
        for (Land land:getLands())
            dataBase.saveLand(land);
        dataBase.disconnect();
    }

    public ArrayList<Land> getLands() {
        return lands;
    }
    public boolean hasLand(Player player){
        for (Land land:getLands()){
            if (player.getName().equals(land.getOwner()))
                return true;
            for (String s:land.getMember())
                if (s.equals(player.getName()))
                    return true;
        }
        return false;
    }
    public boolean hasLand(String player){
        for (Land land:getLands()){
            if (player.equals(land.getOwner()))
                return true;
            for (String s:land.getMember())
                if (s.equals(player))
                    return true;
        }
        return false;
    }
    public Land Level2Land(Level level){
        for (Land land:getLands()){
            if (land.getLevel().equals(level.getName())) return land;
        }
        return null;
    }
    public boolean hasPer(Player player, Level level, Block block) {
        Land land = Level2Land(level);
        if (land == null) return true;
        if (land.getSaveZone().getFloorX() == block.getFloorX()&& land.getSaveZone().getFloorZ() == block.getFloorZ()) return false;
        return land.getOwner().equals(player.getName()) || land.getCustomer().contains(player.getName()) || land.getMember().contains(player.getName());
    }
    public boolean hasPer(Player player, Level level){
        Land land = Level2Land(level);
        if (land == null) return true;
        return land.getOwner().equals(player.getName()) || land.getCustomer().contains(player.getName()) || land.getMember().contains(player.getName());
    }
    public Land Player2Land(Player player){
        for (Land land:getLands()){
            if (land.getOwner().equals(player.getName())) return land;
            if (land.getMember().contains(player.getName())) return land;
        }
        return null;
    }
    public Land PN2Land(String s){
        for (Land land:getLands()){
            if (land.getOwner().equals(s)) return land;
            if (land.getMember().contains(s)) return land;
        }
        return null;
    }
    public boolean isMember(Land land,Player player){
        return !land.getOwner().equals(player.getName());
    }
    public String getPath() {
        return path;
    }
    public void addLand(Land land){
        lands.add(land);
    }

    public Config getConfigs() {
        return config;
    }
}
