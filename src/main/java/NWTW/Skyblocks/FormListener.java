package NWTW.Skyblocks;

import cn.nukkit.Player;
import cn.nukkit.Server;
import cn.nukkit.event.EventHandler;
import cn.nukkit.event.Listener;
import cn.nukkit.event.player.PlayerFormRespondedEvent;
import cn.nukkit.form.element.ElementButton;
import cn.nukkit.form.response.FormResponseSimple;
import cn.nukkit.form.window.FormWindowSimple;
import cn.nukkit.utils.TextFormat;

import java.io.File;

public class FormListener implements Listener {
    public static int memberle = -21315;
    public static int ownerle = -1241254;
    public static int inv = -548362;
    public static void memLeave(Player player){
        FormWindowSimple simple = new FormWindowSimple("島嶼系統","請問你是否要退出當前的島嶼");
        simple.addButton(new ElementButton("我已經非常確認了"));
        simple.addButton(new ElementButton("拜託讓我在想想一下吧"));
        player.showFormWindow(simple,memberle);
    }
    public static void ownerLe(Player player){
        FormWindowSimple simple = new FormWindowSimple("島嶼系統", TextFormat.RED+"請特別注意!\n請問你是否要退出當前的島嶼此步驟無法挽回");
        simple.addButton(new ElementButton("我已經非常確認了"));
        simple.addButton(new ElementButton("拜託讓我在想想一下吧"));
        player.showFormWindow(simple,memberle);
    }
    public static void inviteM(Player player){
        FormWindowSimple simple = new FormWindowSimple("島嶼系統","");
        simple.addButton(new ElementButton("我要加入島嶼並與他一起生活:"+player.getName()));
    }
    @EventHandler
    public void onFormResponse(PlayerFormRespondedEvent event) {
        Player player = event.getPlayer();
        int id = event.getFormID(); //这将返回一个form的唯一标识`id`
        if (event.wasClosed()) return;
        if(id == memberle) { //判断出这个UI界面是否是我们上面写的`menu`
            FormResponseSimple response = (FormResponseSimple) event.getResponse(); //这里需要强制类型转换一下
            int clickedButtonId = response.getClickedButtonId();
            //分类别讨论
            if (clickedButtonId == 0) {
                Land land = Loader.getInstance().Player2Land(player);
                land.getMember().remove(player.getName());
                player.sendMessage("你已經成功退出了島嶼");
            }
        }
        if (id == ownerle){
            FormResponseSimple response = (FormResponseSimple) event.getResponse(); //这里需要强制类型转换一下
            int clickedButtonId = response.getClickedButtonId();
            //分类别讨论
            if (clickedButtonId == 0) {
                Land land = Loader.getInstance().Player2Land(player);
                File file = new File(Server.getInstance().getDataPath() + "worlds/"+land.getLevel());
                    Server.getInstance().unloadLevel(Server.getInstance().getLevelByName(land.getLevel()));
                    Loader.getInstance().dataBase.delLand(player);
                    Loader.getInstance().getLands().remove(land);
                    file.delete();
                player.sendMessage("島嶼刪除成功");
            }
        }
        if (id == inv){
            FormResponseSimple response = (FormResponseSimple) event.getResponse(); //这里需要强制类型转换一下
            int clickedButtonId = response.getClickedButtonId();
            //分类别讨论
            if (clickedButtonId == 0) {
                String[] pl = response.getClickedButton().getText().split(":");
                Land land = Loader.getInstance().PN2Land(pl[1]);
                land.getMember().add(event.getPlayer().getName());
                player.sendMessage("你已成成功加入了新的島嶼");
            }
        }
    }
}
