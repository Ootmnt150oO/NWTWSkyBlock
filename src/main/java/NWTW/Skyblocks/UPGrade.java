package NWTW.Skyblocks;

import cn.nukkit.item.Item;

import java.util.ArrayList;

public class UPGrade {
    private ArrayList<Item> items;
    private int money;

    public UPGrade(ArrayList<Item> items, int money) {
        this.items = items;
        this.money = money;
    }

    public int getMoney() {
        return money;
    }

    public void setMoney(int money) {
        this.money = money;
    }

    public ArrayList<Item> getItems() {
        return items;
    }

    public void setItems(ArrayList<Item> items) {
        this.items = items;
    }
    public void addItem(Item item){
        this.items.add(item);
    }
}
