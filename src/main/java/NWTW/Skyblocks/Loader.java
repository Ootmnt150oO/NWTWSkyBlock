package NWTW.Skyblocks;

import cn.nukkit.Player;
import cn.nukkit.block.Block;
import cn.nukkit.item.Item;
import cn.nukkit.level.Level;
import cn.nukkit.level.generator.Generator;
import cn.nukkit.plugin.PluginBase;
import cn.nukkit.scheduler.AsyncTask;
import cn.nukkit.utils.ConfigSection;

import java.io.File;
import java.io.IOException;
import java.util.*;

public class Loader extends PluginBase {
    private final ArrayList<Land> lands = new ArrayList<>();
    private static Loader instance;
    private  String path;
    DataBase dataBase;
    private final HashMap<String,UPGrade> upGradeHashMap = new HashMap<>();
    public static Loader getInstance() {
        return instance;
    }
    private final HashMap<String,Integer> member = new HashMap<>();
    @Override
    public void onEnable() {
        instance = this;
        saveDefaultConfig();
//        initConfig();
        loadMemberSize();
        loadUpgrade();
        path = getDataFolder()+"/database.db";
        String cfgp = getDataFolder() + "/";
        File files = new File(cfgp);
        if (!files.exists()){
            files.mkdirs();
        }
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
//                for (String s:getConfigs().getKeys())
//                dataBase.loadLand(s);
                dataBase.loadLand();
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
        return land.getOwner().equals(player.getName()) || land.getCustomer().contains(player.getName()) || land.getMember().contains(player.getName())||player.isOp();
    }
    public boolean hasPer(Player player, Level level){
        Land land = Level2Land(level);
        if (land == null) return true;
        return land.getOwner().equals(player.getName()) || land.getCustomer().contains(player.getName()) || land.getMember().contains(player.getName())||player.isOp();
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
    public boolean isMineZone(Land land,Block block){
        for (int x = 128-(land.getSize()*5);x<=128+(land.getSize()*5)+1;x++){
            for (int y = 70-(land.getSize()*5);y<=70+(land.getSize()*5)+1;y++){
                for (int z = 128-(land.getSize()*5);z<=128+(land.getSize()*5)+1;z++){
                    if (block.getFloorX() == x && block.getFloorY() == y && block.getFloorZ() == z) return true;
                }
            }
        }
        return false;
    }
    public String getPath() {
        return path;
    }
    public void addLand(Land land){
        lands.add(land);
    }
    public void loadUpgrade(){
        ConfigSection section = getConfig().getSections();
        for (String keys:section.getSection("島嶼升級材料").getKeys(false)){
            List<String> l = new ArrayList<>();
            int m = -1;
            String[] s;
            ArrayList<Item> arrayList = new ArrayList<>();
            for (String k:section.getSection("島嶼升級材料").getSection(keys).getKeys(false)){

                if (k.equals("物品")){
                   l = section.getSection("島嶼升級材料").getSection(keys).getStringList(k);
                   for (String ss:l){
                       s = ss.split(":");
                       arrayList.add(Item.get(Integer.parseInt(s[0]),Integer.parseInt(s[1]),Integer.parseInt(s[2])));
                   }
                }else {
                    m = section.getSection("島嶼升級材料").getSection(keys).getInt(k);
                }
            }
            upGradeHashMap.put(keys,new UPGrade(arrayList,m));
        }
        for (Map.Entry<String,UPGrade>entry:upGradeHashMap.entrySet()){
            System.out.println(entry.getKey()+":"+entry.getValue().getItems()+":"+entry.getValue().getMoney());
        }
    }
    public void loadMemberSize(){
        ConfigSection section = getConfig().getSections();
        for (String s:section.getSection("島嶼最大成員數量").getKeys(false)){
            member.put(s,section.getSection("島嶼最大成員數量").getInt(s));
        }
        System.out.println(member);
    }
//    public void initConfig(){
//        if (!getConfig().exists("島嶼升級材料")){
//            LinkedHashMap<String,Object> map = new LinkedHashMap<>();
//            LinkedHashMap<String,Object> map1 = new LinkedHashMap<>();
//            ArrayList<String> arrayList = new ArrayList<>();
//            arrayList.add("1:0:1");
//            arrayList.add("273:0:1");
//            map1.put("物品",arrayList);
//            map1.put("錢幣",100);
//            map.put("第二級",map1);
//            map.put("第三級",map1);
//            map.put("第四級",map1);
//            getConfig().set("島嶼升級材料",map);
//            saveConfig();
//        }
//    }
    public boolean canJoin(Land land){
        return land.getMember().size() <= member.get(int2String(land.getSize()));
    }
    public String int2String(int i){
        return switch (i) {
            case 1 -> "第一級";
            case 2 -> "第二級";
            case 3 -> "第三級";
            case 4 -> "第四級";
            default -> null;
        };
    }

    public HashMap<String, UPGrade> getUpGradeHashMap() {
        return upGradeHashMap;
    }

    public HashMap<String, Integer> getMember() {
        return member;
    }
}
