package NWTW.Skyblocks;

import cn.nukkit.Player;
import cn.nukkit.Server;
import cn.nukkit.event.EventHandler;
import cn.nukkit.event.EventPriority;
import cn.nukkit.event.Listener;
import cn.nukkit.event.block.BlockBreakEvent;
import cn.nukkit.event.block.BlockPlaceEvent;
import cn.nukkit.event.entity.EntityDamageByEntityEvent;
import cn.nukkit.event.entity.EntityDamageEvent;
import cn.nukkit.event.player.PlayerInteractEvent;
import cn.nukkit.event.player.PlayerRespawnEvent;



public class PlayerListener implements Listener {
    @EventHandler(priority = EventPriority.LOWEST)
    public void onBreak(BlockBreakEvent event) {
        if (!Loader.getInstance().hasPer(event.getPlayer(), event.getBlock().getLevel(), event.getBlock())) {
            event.setCancelled(true);
            event.getPlayer().sendMessage("你沒有此地圖編輯權限");
        }else {
            Land land = Loader.getInstance().Player2Land(event.getPlayer());
            if (land == null) return;
            if (!Loader.getInstance().isMineZone(land,event.getBlock())){
                event.setCancelled(true);
                event.getPlayer().sendMessage("請勿超出你的島嶼編輯範圍");
            }
        }
    }

    @EventHandler(priority = EventPriority.LOWEST)
    public void onPlace(BlockPlaceEvent event) {
        if (!Loader.getInstance().hasPer(event.getPlayer(), event.getBlock().getLevel(), event.getBlock())) {
            event.getPlayer().sendMessage("你沒有此地圖編輯權限");
            event.setCancelled(true);
        }else {
            Land land = Loader.getInstance().Player2Land(event.getPlayer());
            if (land == null) return;
            if (!Loader.getInstance().isMineZone(land,event.getBlock())){
                event.setCancelled(true);
                event.getPlayer().sendMessage("請勿超出你的島嶼編輯範圍");
            }
        }
    }
    @EventHandler(priority = EventPriority.LOWEST)
    public void onTap(PlayerInteractEvent event){
        if (!Loader.getInstance().hasPer(event.getPlayer(),event.getBlock().getLevel())){
            event.setCancelled(true);
        }
    }
    @EventHandler(priority = EventPriority.LOWEST)
    public void onKillEntity(EntityDamageEvent event){
        if (event instanceof EntityDamageByEntityEvent){
            if (((EntityDamageByEntityEvent) event).getDamager() instanceof Player){
                Player player = (Player) ((EntityDamageByEntityEvent) event).getDamager();
                if (!Loader.getInstance().hasPer(player,player.getLevel()))event.setCancelled(true);
            }
            if (event.getEntity() instanceof Player){
                Player player = (Player) event.getEntity();
                if (!Loader.getInstance().hasPer(player,player.getLevel()))event.setCancelled(true);
            }
        }
    }
    @EventHandler
    public void onRespawn(PlayerRespawnEvent event){
        if (event.isFirstSpawn()) return;
        Player player = event.getPlayer();
        if (Loader.getInstance().hasLand(player)){
            Land land = Loader.getInstance().Player2Land(player);
            Server.getInstance().getScheduler().scheduleDelayedTask(Loader.getInstance(), new Runnable() {
                @Override
                public void run() {
                    player.teleport(land.getTpZone());
                }
            },20);
        }
    }
}
