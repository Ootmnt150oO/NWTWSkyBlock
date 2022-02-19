package NWTW.Skyblocks;

import cn.nukkit.Player;
import cn.nukkit.Server;
import cn.nukkit.level.Position;
import cn.nukkit.math.Vector3;

import java.io.File;
import java.sql.*;
import java.util.ArrayList;
import java.util.Arrays;

public class DataBase {
    File file;
//    String host = "localhost", database = "land", user = "root", password = "";
//    int port = 3306;
    private Connection c;

    public void createTable() {
        try {
            Statement statement = c.createStatement();
            statement.execute("CREATE TABLE IF NOT EXISTS skyblocks (id integer AUTO_INCREMENT PRIMARY KEY, owner text, member text, tpzone text, size integer, locker integer)");
            statement.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void connect() throws ClassNotFoundException {
        try {
            file = new File(Loader.getInstance().getPath());
            Class.forName("org.sqlite.JDBC");
//            Class.forName("com.mysql.cj.jdbc.Driver");
//            c = DriverManager.getConnection("jdbc:mysql://" + host + ":" + port + "/" + database, user, password);
            c = DriverManager.getConnection("jdbc:sqlite:" +Loader.getInstance().getPath());
            c.setAutoCommit(true);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public boolean isConnection() {
        return (c != null);
    }

    public void disconnect() {
        if (isConnection()) {
            try {
                c.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }

    public void createAccount(Land land) {
        try {
            ResultSet result = c.createStatement().executeQuery("SELECT id FROM skyblocks WHERE owner = '" + land.getOwner() + "'");
            if (!result.next()) {
                PreparedStatement stm = c.prepareStatement("INSERT INTO skyblocks(owner,member,tpzone,size,locker) VALUES (?,?,?,?,?)");
                stm.setString(1, land.getOwner());
                stm.setString(2, "");
                stm.setString(3, land.getTpZone().getFloorX() + "~" + land.getTpZone().getFloorY() + "~" + land.getTpZone().getFloorZ());
                stm.setInt(4, land.getSize());
                stm.setInt(5, bool2int(land.isLock()));
                stm.executeUpdate();
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public int bool2int(boolean b) {
        if (b) return 1;
        return 0;
    }

    public boolean int2bool(int b) {
        return b == 1;
    }

    public void loadLand(String owners) {
        try {
//            ResultSet result = c.createStatement().executeQuery("SELECT member, tpzone, locker, size FROM skyblocks WHERE owner = '" + owners + "'");
            ResultSet result = c.createStatement().executeQuery("SELECT * FROM skyblocks");
            if (result.next()) {
                Land land = new Land();
                String mem = result.getString("member");
                String[] members = null;
                if (!mem.equals("")) {
                    String member = mem.substring(0, mem.length() - 1);
                    members = member.split("~");
                }
                String[] zone = result.getString("tpzone").split("~");
                boolean b = int2bool(result.getInt("locker"));
                int size = result.getInt("size");
                land.setLevel(Server.getInstance().getOfflinePlayer(owners).getUniqueId().toString());
                land.setOwner(owners);
                ArrayList<String> list;
                if (members != null) {
                    list = new ArrayList<>(Arrays.asList(members));
                } else {
                    list = new ArrayList<>();
                }
                land.setMember(list);
                land.setTpZone(new Position().add(Double.parseDouble(zone[0]), Double.parseDouble(zone[1]), Double.parseDouble(zone[2])).setLevel(Server.getInstance().getLevelByName(land.getLevel())));
                land.setSize(size);
                land.setLock(b);
                land.setCustomer(new ArrayList<>());
                land.setSaveZone(new Vector3().add(land.getTpZone().getFloorX(), land.getTpZone().getFloorY(), land.getTpZone().getFloorZ()));
                Loader.getInstance().addLand(land);
                Server.getInstance().loadLevel(Server.getInstance().getOfflinePlayer(land.getOwner()).getUniqueId().toString());
                Server.getInstance().getLogger().info("島主:" + land.getOwner() + "已經讀取完畢");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
    public void loadLand() {
        try {
//            ResultSet result = c.createStatement().executeQuery("SELECT member, tpzone, locker, size FROM skyblocks WHERE owner = '" + owners + "'");
            ResultSet result = this.c.createStatement().executeQuery("SELECT * FROM skyblocks");
            while (result.next()) {
                Land land = new Land();
                String mem = result.getString("member");
                String[] members = null;
                if (!mem.equals("")) {
                    String member = mem.substring(0, mem.length() - 1);
                    members = member.split("~");
                }
                String[] zone = result.getString("tpzone").split("~");
                boolean b = int2bool(result.getInt("locker"));
                int size = result.getInt("size");
                land.setLevel(Server.getInstance().getOfflinePlayer(result.getString("owner")).getUniqueId().toString());
                land.setOwner(result.getString("owner"));
                ArrayList<String> list;
                if (members != null) {
                    list = new ArrayList<>(Arrays.asList(members));
                } else {
                    list = new ArrayList<>();
                }
                land.setMember(list);
                land.setTpZone(new Position().add(Double.parseDouble(zone[0]), Double.parseDouble(zone[1]), Double.parseDouble(zone[2])).setLevel(Server.getInstance().getLevelByName(land.getLevel())));
                land.setSize(size);
                land.setLock(b);
                land.setCustomer(new ArrayList<>());
                land.setSaveZone(new Vector3().add(land.getTpZone().getFloorX(), land.getTpZone().getFloorY(), land.getTpZone().getFloorZ()));
                Loader.getInstance().addLand(land);
                Server.getInstance().loadLevel(Server.getInstance().getOfflinePlayer(land.getOwner()).getUniqueId().toString());
                Server.getInstance().getLogger().info("島主:" + land.getOwner() + "已經讀取完畢");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
    public void saveLand(Land land) {
        try {
            PreparedStatement preparedStatement = c.prepareStatement("UPDATE skyblocks SET member =?, tpzone = ?, size = ?,  locker = ? WHERE owner = ?");
            preparedStatement.setString(5, land.getOwner());
            preparedStatement.setInt(3, land.getSize());
            preparedStatement.setInt(4, bool2int(land.isLock()));
            if (!land.getMember().isEmpty()) {
                StringBuilder sb = new StringBuilder();
                for (String s : land.getMember()) {
                    sb.append(s).append("~");
                }
                preparedStatement.setString(1, sb.toString());
            } else {
                preparedStatement.setString(1, "");
            }
            String loc = land.getTpZone().getFloorX() + "~" + land.getTpZone().getFloorY() + "~" + land.getTpZone().getFloorZ();
            preparedStatement.setString(2, loc);
            preparedStatement.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
    public void delLand(Player player)  {
        try {
            PreparedStatement preparedStatement = c.prepareStatement("delete from skyblocks where owner = ?");
            preparedStatement.setString(1,player.getName());
            preparedStatement.executeUpdate();
        }catch (SQLException e){
            e.printStackTrace();
        }
    }
}
