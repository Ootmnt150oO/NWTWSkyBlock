package NWTW.Skyblocks;

import cn.nukkit.Player;
import cn.nukkit.Server;
import cn.nukkit.block.Block;
import cn.nukkit.block.BlockSapling;
import cn.nukkit.blockentity.BlockEntity;
import cn.nukkit.blockentity.BlockEntityChest;
import cn.nukkit.command.Command;
import cn.nukkit.command.CommandSender;
import cn.nukkit.item.Item;
import cn.nukkit.level.Level;
import cn.nukkit.level.Position;
import cn.nukkit.level.format.generic.BaseFullChunk;
import cn.nukkit.level.generator.object.tree.ObjectTree;
import cn.nukkit.math.NukkitRandom;
import cn.nukkit.math.Vector3;
import cn.nukkit.scheduler.AsyncTask;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class createCMD extends Command {

    public createCMD() {
        super("sk", "空島指令");
    }

    @Override
    public boolean execute(CommandSender commandSender, String s, String[] strings) {
        if (commandSender.isPlayer()) {
            Player player = (Player) commandSender;
            if (strings.length == 1) {
                switch (strings[0]) {
                    case "create" -> {
                        if (!Loader.getInstance().hasLand(player)) {
                            Loader.getInstance().getServer().generateLevel(player.getUniqueId().toString(), 99999999L, EmptyGenerator.class);
                            ArrayList<String> member = new ArrayList<>();
                            Land land = new Land(player.getUniqueId().toString(), player.getName(), member, new Vector3(128, 70, 128), Server.getInstance().getLevelByName(player.getUniqueId().toString()).getSpawnLocation(), 1, false);
                            Loader.getInstance().getLands().add(land);
                            Loader.getInstance().getServer().loadLevel(land.getLevel());
                            Level level = Server.getInstance().getLevelByName(land.getLevel());
                            level.setSpawnLocation(land.getTpZone());
                            createIsland(new Position(126, 63, 126, level));
                            player.sendMessage("你的空島已經創建 開始你的快樂生活吧!");
                            player.teleport(land.getTpZone());
                            Server.getInstance().getScheduler().scheduleAsyncTask(Loader.getInstance(), new AsyncTask() {
                                @Override
                                public void onRun() {
                                    Loader.getInstance().dataBase.createAccount(land);
                                }
                            });
                            level.setSpawnLocation(land.getSaveZone());
                        } else {
                            player.sendMessage("您已有自己的島嶼或已居住他人島嶼請打 /sk tp 傳送");
                        }
                        return true;
                    }
                    case "tp" -> {
                        if (Loader.getInstance().hasLand(player)) {
                            Land land = Loader.getInstance().Player2Land(player);
                            player.teleport(land.getTpZone());
                        } else {
                            player.sendMessage("請先打/sk create 創建島嶼");
                        }
                        return true;
                    }
                    case "lock" -> {
                        if (Loader.getInstance().hasLand(player)) {
                            Land land = Loader.getInstance().Player2Land(player);
                            land.setLock(true);
                            player.sendMessage("你已成功幫你的島嶼上鎖了");
                        } else {
                            player.sendMessage("請先打/sk create 創建島嶼");
                        }
                        return true;
                    }
                    case "unlock" -> {
                        if (Loader.getInstance().hasLand(player)) {
                            Land land = Loader.getInstance().Player2Land(player);
                            land.setLock(false);
                            player.sendMessage("你已成功幫你的島嶼解鎖了");
                        } else {
                            player.sendMessage("請先打/sk create 創建島嶼");
                        }
                        return true;
                    }
                    case "sp" -> {
                        if (Loader.getInstance().hasLand(player)) {
                            Land land = Loader.getInstance().Player2Land(player);
                            land.setTpZone(player.getLocation());
                            land.setSaveZone(new Vector3(land.getTpZone().getFloorX(), land.getTpZone().getFloorY(), land.getTpZone().getFloorZ()));
                            player.sendMessage("你已成功幫你的島嶼更改了傳送點");
                        } else {
                            player.sendMessage("請先打/sk create 創建島嶼");
                        }
                        return true;
                    }
                    case "list" -> {
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
                    }
                    case "help" -> {
                        player.sendMessage("-/sk create >>> 創建島嶼\n" +
                                "-/sk tp >>> 傳送居住空島\n" +
                                "-/sk tp ID >>> 參觀別人的空島*ID 的大小寫要和本人 ID 一致*\n" +
                                "-/sk lock >>> 鎖島 (島主指令)\n" +
                                "-/sk upgrade >>> 升級島嶼 (島主指令)\n" +
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
                    }
                    case "Leave" -> {
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
                    }
                    case "delete" -> {
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
                    case "upgrade" -> {
                        if (Loader.getInstance().hasLand(player)) {
                            Land land = Loader.getInstance().Player2Land(player);
                            if (!Loader.getInstance().isMember(land, player)) {
                                FormListener.UpgradeForm(player, land);
                            } else {
                                player.sendMessage("要升級島嶼請找島主喔");
                            }
                        } else {
                            player.sendMessage("請先打/sk create 創建島嶼");
                        }
                        return true;
                    }
                }
            } else if (strings.length == 2) {
                String target = strings[1];
                switch (strings[0]) {
                    case "tp":
                        if (Loader.getInstance().hasLand(target)) {
                            Land land = Loader.getInstance().PN2Land(target);
                            if (!player.isOp()) {
                                if (!land.isLock()) {
                                    player.teleport(land.getTpZone());
                                    player.sendMessage("你已經傳送到了" + target + "所在的島嶼了");
                                } else {
                                    player.sendMessage(target + "所在的島嶼已經鎖住了");
                                }
                            } else {
                                player.teleport(land.getTpZone());
                                player.sendMessage("你已經傳送到了" + target + "所在的島嶼了");
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
                                    if (Loader.getInstance().canJoin(Loader.getInstance().Player2Land(player))) {
                                        FormListener.inviteM(player1, player);
                                    }else {
                                        player.sendMessage("你的島嶼玩家已經超過最大限制 請升級島嶼進行擴充名額");
                                    }
                                } else {
                                    player.sendMessage(target + "找不到他");
                                }
                            } else {
                                player.sendMessage("只有島主才能使用");
                            }
                        } else {
                            player.sendMessage("請先打/sk create 創建島嶼");
                        }
                        return true;
                    case "kick":
                        if (Loader.getInstance().hasLand(player)) {
                            Land land = Loader.getInstance().Player2Land(player);
                            if (!Loader.getInstance().isMember(land, player)) {
                                if (land.getMember().contains(target)) {
                                    land.getMember().remove(target);
                                    player.sendMessage("你已經成功移除掉了" + target);
                                } else {
                                    player.sendMessage("只有島主才能使用此指令");
                                }
                            }
                        } else {
                            player.sendMessage("請先打/sk create 創建島嶼");
                        }
                        return true;
                    case "time":
                        if (Loader.getInstance().hasLand(player)) {
                            Land land = Loader.getInstance().Player2Land(player);
                            switch (target) {
                                case "day" -> {
                                    Server.getInstance().getLevelByName(land.getLevel()).setTime(Level.TIME_DAY);
                                    return true;
                                }
                                case "noon" -> {
                                    Server.getInstance().getLevelByName(land.getLevel()).setTime(Level.TIME_NOON);
                                    return true;
                                }
                                case "night" -> {
                                    Server.getInstance().getLevelByName(land.getLevel()).setTime(Level.TIME_NIGHT);
                                    return true;
                                }
                            }
                        }
                }
            }
        }
        return false;
    }
    private void initChest(Level lvl, int x, int y, int z) {
        BaseFullChunk chunk = lvl.getChunk(x >> 4, z >> 4);
        lvl.setBlockIdAt(x, y, z, Block.CHEST);

        while (!chunk.isLoaded()) {
            try {
                chunk.load(true);
            } catch (IOException ex) {
                ex.fillInStackTrace();
            }
        }

        // Chunk is fully loaded, no need to rerun the task, when it fully
        // loaded it will be loaded.
        cn.nukkit.nbt.tag.CompoundTag nbt = new cn.nukkit.nbt.tag.CompoundTag()
                .putList(new cn.nukkit.nbt.tag.ListTag<>("Items"))
                .putString("id", BlockEntity.CHEST)
                .putInt("x", x)
                .putInt("y", y)
                .putInt("z", z);

        BlockEntityChest e = new BlockEntityChest(chunk, nbt);
        lvl.addBlockEntity(e);
        e.spawnToAll();
            Map<Integer, Item> items = new HashMap<>();
            items.put(0, Item.get(Item.ICE, 0, 2));
            items.put(1, Item.get(Item.BUCKET, 10, 1));
            items.put(2, Item.get(Item.BONE, 0, 2));
            items.put(3, Item.get(Item.SUGARCANE, 0, 1));
            items.put(4, Item.get(Item.RED_MUSHROOM, 0, 1));
            items.put(5, Item.get(Item.BROWN_MUSHROOM, 0, 2));
            items.put(6, Item.get(Item.PUMPKIN_SEEDS, 0, 2));
            items.put(7, Item.get(Item.MELON, 0, 1));
            items.put(8, Item.get(Item.SAPLING, 0, 1));
            items.put(9, Item.get(Item.STRING, 0, 12));
            items.put(10, Item.get(Item.POTATO, 0, 32));
            items.put(11, Item.get(Item.CACTUS, 0, 1));
            items.put(12,Item.get(Item.DIRT,0,16));
            e.getInventory().setContents(items);
    }
    private void createIsland(Position pos) {
        int groundHeight = pos.getFloorY();
        int X = pos.getFloorX();
        int Z = pos.getFloorZ();
        Level world = pos.level;
        // bedrock - ensures island are not overwritten
        for (int x = X; x < X + 1; ++x) {
            for (int z = Z; z < Z + 1; ++z) {
                world.setBlockIdAt(x, groundHeight, z, Block.BEDROCK);
            }
        }
        // Add some dirt and grass
        for (int x = X - 1; x < X + 6; ++x) {
            for (int z = X - 1; z < X + 2; ++z) {
                world.setBlockIdAt(x, groundHeight + 1, z, Block.DIRT);
                world.setBlockIdAt(x, groundHeight + 2, z, Block.DIRT);
            }
        }
        for (int x = X - 2; x < X + 7; ++x) {
            for (int z = Z - 2; z < Z + 3; ++z) {
                world.setBlockIdAt(x, groundHeight + 3, z, Block.DIRT);
                world.setBlockIdAt(x, groundHeight + 4, z, Block.DIRT);
            }
        }
        for (int x = X - 3; x < X + 8; ++x) {
            for (int z = Z - 3; z < Z + 4; ++z) {
                world.setBlockIdAt(x, groundHeight + 5, z, Block.DIRT);
                world.setBlockIdAt(x, groundHeight + 6, z, Block.GRASS);
            }
        }
        // Then cut off the corners to make it round-ish
        for (int x_space = X - 3; x_space <= X + 7; x_space += 6) {
            for (int z_space = Z - 3; z_space <= Z + 3; z_space += 6) {
                world.setBlockIdAt(x_space, groundHeight + 5, z_space, Block.AIR);
                world.setBlockIdAt(x_space, groundHeight + 6, z_space, Block.AIR);
            }
        }

        for (int x_space = X - 2; x_space <= X + 6; x_space += 4) {
            for (int z_space = Z - 2; z_space <= Z + 2; z_space += 4) {
                world.setBlockIdAt(x_space, groundHeight + 3, z_space, Block.AIR);
            }

        }

        for (int x_space = X - 1; x_space <= X + 6; x_space += 2) {
            for (int z_space = Z - 1; z_space <= Z + 1; z_space += 2) {
                world.setBlockIdAt(x_space, groundHeight + 1, z_space, Block.AIR);
            }
        }
        // Sand
        world.setBlockIdAt(X, groundHeight + 1, Z, Block.SAND);
        world.setBlockIdAt(X, groundHeight + 2, Z, Block.SAND);
        world.setBlockIdAt(X, groundHeight + 3, Z, Block.SAND);
        world.setBlockIdAt(X, groundHeight + 4, Z, Block.SAND);
        world.setBlockIdAt(X, groundHeight + 5, Z, Block.SAND);

        // Done making island base Joe! Now we place the sweets (Tree)
        ObjectTree.growTree(world, X, groundHeight + 7, Z, new NukkitRandom(), BlockSapling.OAK);

        initChest(world, X, groundHeight + 7, Z + 1);
    }
}
