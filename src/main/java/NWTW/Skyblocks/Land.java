package NWTW.Skyblocks;

import cn.nukkit.level.Position;
import cn.nukkit.math.Vector3;

import java.util.ArrayList;

public class Land {
    private String level;
    private String owner;
    private ArrayList<String> member;
    private Vector3 saveZone;
    private Position tpZone;
    private ArrayList<String> customer;
    private int size;
    private boolean lock;

    public Land (){

    }
    public Land(String level, String owner, ArrayList<String> member, Vector3 saveZone, Position tpZone, int size, boolean lock) {
        this.level = level;
        this.owner = owner;
        this.member = member;
        this.saveZone = saveZone;
        this.tpZone = tpZone;
        this.size = size;
        this.lock = lock;
        this.customer = new ArrayList<>();
    }

    public String getLevel() {
        return level;
    }

    public void setLevel(String level) {
        this.level = level;
    }

    public String getOwner() {
        return owner;
    }

    public void setOwner(String owner) {
        this.owner = owner;
    }

    public ArrayList<String> getMember() {
        return member;
    }

    public void setMember(ArrayList<String> member) {
        this.member = member;
    }

    public Vector3 getSaveZone() {
        return saveZone;
    }

    public void setSaveZone(Vector3 saveZone) {
        this.saveZone = saveZone;
    }

    public Position getTpZone() {
        return tpZone;
    }

    public void setTpZone(Position tpZone) {
        this.tpZone = tpZone;
    }

    public int getSize() {
        return size;
    }

    public void setSize(int size) {
        this.size = size;
    }

    public ArrayList<String> getCustomer() {
        return customer;
    }

    public void setCustomer(ArrayList<String> customer) {
        this.customer = customer;
    }

    public boolean isLock() {
        return lock;
    }

    public void setLock(boolean lock) {
        this.lock = lock;
    }
}
