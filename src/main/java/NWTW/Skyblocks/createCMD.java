package NWTW.Skyblocks;

import cn.nukkit.Player;
import cn.nukkit.Server;
import cn.nukkit.block.Block;
import cn.nukkit.block.BlockID;
import cn.nukkit.command.Command;
import cn.nukkit.command.CommandSender;
import cn.nukkit.form.window.FormWindowSimple;
import cn.nukkit.level.Level;
import cn.nukkit.math.Vector3;
import cn.nukkit.scheduler.AsyncTask;
import com.sun.org.apache.bcel.internal.generic.IF_ACMPEQ;

import java.util.ArrayList;

public class createCMD extends Command {

    public createCMD() {
        super("sk","空島指令");
    }

    @Override
    public boolean execute(CommandSender commandSender, String s, String[] strings) {
        if (commandSender.isPlayer()) {
            Player player = (Player) commandSender;
            if (strings.length == 1) {
                switch (strings[0]) {
                    case "create":
                        if (!Loader.getInstance().hasLand(player)) {
                            Loader.getInstance().getServer().generateLevel(player.getUniqueId().toString(), 99999999L, EmptyGenerator.class);
                            ArrayList<String> member = new ArrayList<>();
                            Land land = new Land(player.getUniqueId().toString(), player.getName(), member, new Vector3(128, 64, 128), Server.getInstance().getLevelByName(player.getUniqueId().toString()).getSpawnLocation(), 0, false);
                            Loader.getInstance().getLands().add(land);
                            Loader.getInstance().getServer().loadLevel(land.getLevel());
                            Level level = Server.getInstance().getLevelByName(land.getLevel());
                            level.setSpawnLocation(land.getTpZone());
                            player.sendMessage(land.getLevel());
                            Loader.getInstance().getConfigs().set(player.getName(), player.getUniqueId().toString());
                            Loader.getInstance().getConfigs().save();
                            for (int x = 127; x < 130; x++) {
                                for (int z = 127; z < 130; z++) {
                                    level.setBlock(new Vector3(x, 64, z), Block.get(BlockID.BEDROCK));
                                }
                            }
                            player.teleport(land.getTpZone());
                            Server.getInstance().getScheduler().scheduleAsyncTask(Loader.getInstance(), new AsyncTask() {
                                @Override
                                public void onRun() {
                                    Loader.getInstance().dataBase.createAccount(land);
                                }
                            });
                        } else {
                            player.sendMessage("您已有自己的島嶼或已居住他人島嶼請打 /sk tp 傳送");
                            player.teleport(Server.getInstance().getLevelByName(player.getUniqueId().toString()).getSpawnLocation());
                        }
                        return true;
                    case "tp":
                        if (Loader.getInstance().hasLand(player)) {
                            Land land = Loader.getInstance().Player2Land(player);
                            player.teleport(land.getTpZone());
                        } else {
                            player.sendMessage("請先打/sk create 創建島嶼");
                        }
                        return true;
                    case "lock":
                        if (Loader.getInstance().hasLand(player)) {
                            Land land = Loader.getInstance().Player2Land(player);
                            land.setLock(true);
                            player.sendMessage("你已成功幫你的島嶼上鎖了");
                        } else {
                            player.sendMessage("請先打/sk create 創建島嶼");
                        }
                        return true;
                    case "unlock":
                        if (Loader.getInstance().hasLand(player)) {
                            Land land = Loader.getInstance().Player2Land(player);
                            land.setLock(false);
                            player.sendMessage("你已成功幫你的島嶼解鎖了");
                        } else {
                            player.sendMessage("請先打/sk create 創建島嶼");
                        }
                        return true;
                    case "sp":
                        if (Loader.getInstance().hasLand(player)) {
                            Land land = Loader.getInstance().Player2Land(player);
                            land.setTpZone(player.getLocation());
                            land.setSaveZone(new Vector3(land.getTpZone().getFloorX(), land.getTpZone().getFloorY(), land.getTpZone().getFloorZ()));
                            player.sendMessage("你已成功幫你的島嶼更改了傳送點");
                        } else {
                            player.sendMessage("請先打/sk create 創建島嶼");
                        }
                        return true;
                    case "list":
                        if (Loader.getInstance().hasLand(player)) {
                            Land land = Loader.getInstance().Player2Land(player);
                            player.sendMessage(land.getOwner() + "[島主]");
                            if (!land.getMember().isEmpty()) {
                                for (String s1 : land.getMember()) {
                                    player.sendMessage(s1 + "[成員]");
                                }
                            }
                        } else {
                            player.sendMessage("請先打/sk create 創建島嶼");
                        }
                        return true;
                    case "help":
                        player.sendMessage("-/sk create >>> 創建島嶼\n" +
                                "-/sk tp >>> 傳送居住空島\n" +
                                "-/sk tp ID >>> 參觀別人的空島*ID 的大小寫要和本人 ID 一致*\n" +
                                "-/sk lock >>> 鎖島 (島主指令)\n" +
                                "-/sk unlock >>> 解除鎖島 (島主指令)\n" +
                                "-/sk give ID >>> [小幫手]給予臨時編輯權限*ID 的大小寫要和本人 ID 一致*\n" +
                                "-/sk sp >>> 設置島嶼出生點 (島主指令)\n" +
                                "-/sk live 想要邀請的玩家id >>> 發出住島請求\n" +
                                "-/sk time set noon >>> 調整島嶼時間為早上\n" +
                                "-/sk time set night >>> 調整島嶼時間為晚上\n" +
                                "-/sk kick ID >>> 踢除島員 (島主指令)\n" +
                                "-/sk Leave >>> 離開島嶼\n" +
                                "-/sk delete >>> 刪除島嶼 (島主指令)\n" +
                                "-/sk list >>> 查看島嶼名單");
                        return true;
                    case "Leave":
                        if (Loader.getInstance().hasLand(player)) {
                            Land land = Loader.getInstance().Player2Land(player);
                            if (Loader.getInstance().isMember(land, player)) {
                                FormListener.memLeave(player);
                            } else {
                                player.sendMessage("你是島主請使用刪除島嶼指令");
                            }
                        } else {
                            player.sendMessage("請先打/sk create 創建島嶼");
                        }
                        return true;
                    case "delete":
                        if (Loader.getInstance().hasLand(player)) {
                            Land land = Loader.getInstance().Player2Land(player);
                            if (!Loader.getInstance().isMember(land, player)) {
                                FormListener.ownerLe(player);
                            } else {
                                player.sendMessage("你是成員請使用退出島嶼指令");
                            }
                        } else {
                            player.sendMessage("請先打/sk create 創建島嶼");
                        }
                        return true;
                }
            } else if (strings.length == 2) {
                String target = strings[1];
                switch (strings[0]) {
                    case "tp":
                        if (Loader.getInstance().hasLand(target)) {
                            Land land = Loader.getInstance().PN2Land(target);
                            if (!land.isLock()) {
                                player.teleport(land.getTpZone());
                                player.sendMessage("你已經傳送到了" + target + "所在的島嶼了");
                            } else {
                                player.sendMessage(target + "所在的島嶼已經鎖住了");
                            }
                        } else {
                            player.sendMessage("他目前沒有任何一塊島嶼喔");
                        }
                        return true;
                    case "give":
                        if (Loader.getInstance().hasLand(player)) {
                            Land land = Loader.getInstance().Player2Land(player);
                            land.getCustomer().add(target);
                            player.sendMessage("你已賦予暫時編輯權限給了" + target);
                        } else {
                            player.sendMessage("請先打/sk create 創建島嶼");
                        }
                        return true;
                    case "live":
                        if (Loader.getInstance().hasLand(player)) {
                            if (!Loader.getInstance().isMember(Loader.getInstance().Player2Land(player), player)) {
                                Player player1 = Server.getInstance().getPlayer(target);
                                if (player1 != null) {
                                    FormListener.inviteM(player1);
                                } else {
                                    player.sendMessage(target + "找不到他");
                                }
                            }else {
                                player.sendMessage("只有島主才能使用");
                            }
                        }else {
                            player.sendMessage("請先打/sk create 創建島嶼");
                        }
                        return true;
                    case "kick":
                        if (Loader.getInstance().hasLand(player)){
                            Land land = Loader.getInstance().Player2Land(player);
                            if (!Loader.getInstance().isMember(land,player)){
                                if (land.getMember().contains(target)){
                                    land.getMember().remove(target);
                                    player.sendMessage("你已經成功移除掉了"+target);
                                }else {
                                    player.sendMessage("只有島主才能使用此指令");
                                }
                            }
                        }else {
                            player.sendMessage("請先打/sk create 創建島嶼");
                        }
                        return true;
                    case "time":
                        if (Loader.getInstance().hasLand(player)){
                            Land land = Loader.getInstance().Player2Land(player);
                            switch (target) {
                                case "day":
                                Server.getInstance().getLevelByName(land.getLevel()).setTime(Level.TIME_DAY);
                                return true;
                                case "noon":
                                    Server.getInstance().getLevelByName(land.getLevel()).setTime(Level.TIME_NOON);
                                    return true;
                                case "night":
                                    Server.getInstance().getLevelByName(land.getLevel()).setTime(Level.TIME_NIGHT);
                                    return true;
                            }
                        }
                }
            }
        }
        return false;
    }
}
