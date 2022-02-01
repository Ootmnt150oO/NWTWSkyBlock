package NWTW.Skyblocks;

import cn.nukkit.Player;
import cn.nukkit.event.EventHandler;
import cn.nukkit.event.EventPriority;
import cn.nukkit.event.Listener;
import cn.nukkit.event.block.BlockBreakEvent;
import cn.nukkit.event.block.BlockPlaceEvent;
import cn.nukkit.event.entity.EntityDamageByEntityEvent;
import cn.nukkit.event.entity.EntityDamageEvent;
import cn.nukkit.event.player.PlayerInteractEvent;


public class PlayerListener implements Listener {
    @EventHandler(priority = EventPriority.LOWEST)
    public void onBreak(BlockBreakEvent event) {
        if (Loader.getInstance().hasPer(event.getPlayer(), event.getBlock().getLevel(), event.getBlock())) {
            Land land = Loader.getInstance().Level2Land(event.getPlayer().getLevel());
            if (land != null) {
                if (land.getSize() > 0)
                    land.setSize(land.getSize() - 1);
            }
        } else {
            event.setCancelled(true);
            event.getPlayer().sendMessage("你沒有此地圖編輯權限");
        }
    }

    @EventHandler(priority = EventPriority.LOWEST)
    public void onPlace(BlockPlaceEvent event) {
        if (Loader.getInstance().hasPer(event.getPlayer(), event.getBlock().getLevel(), event.getBlock())) {
            Land land = Loader.getInstance().Level2Land(event.getPlayer().getLevel());
            if (land != null) {
                land.setSize(land.getSize() + 1);
            }
        }else {
            event.getPlayer().sendMessage("你沒有此地圖編輯權限");
            event.setCancelled(true);
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
}
