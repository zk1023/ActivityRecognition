package Utils;

import com.mysql.jdbc.Connection;
import com.mysql.jdbc.PreparedStatement;

import java.io.File;
import java.io.FileWriter;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.HashMap;
import java.util.Map;

import static Utils.Constant.columns;

/**
 * Created by Administrator on 2018/10/28.
 */
public class DBUtils {
    //与数据库建立链接
    public static Connection getConnect(String Ip, String dbName, String user, String password){
//        String dbName = "first_mysql_test" ;
//        String dbName = "pose" ;
        //声明Connection对象
        Connection con = null;
        //驱动程序名
        String driver = "com.mysql.jdbc.Driver";
        //URL指向要访问的数据库名mydata
        String url = "jdbc:mysql://"+Ip+":3306/"+dbName  ;
        //MySQL配置时的用户名
//        String user = "root";
        //MySQL配置时的密码
//        String password = "123456";
        try {
            //加载驱动程序
            Class.forName(driver);
            //getConnection()方法，连接MySQL数据库！！
            con = (Connection) DriverManager.getConnection(url,user,password);
            if(!con.isClosed())
                System.out.println("Succeeded connecting to the Database!");
        } catch(ClassNotFoundException e) {
            //数据库驱动类异常处理
            System.out.println("Sorry,can't find the Driver!");
            e.printStackTrace();
        } catch(SQLException e) {
            //数据库连接失败异常处理
            e.printStackTrace();
        }catch (Exception e) {
            // TODO: handle exception
            e.printStackTrace();
        }
        return con ;
    }
    //与数据库建立链接2
    public static Connection getConnect(){
        String Ip = Constant.Ip ;
        String dbName = Constant.dbName;
        String user = Constant.user ;
        String password = Constant.password ;
        //声明Connection对象
        Connection con = null;
        //驱动程序名
        String driver = "com.mysql.jdbc.Driver";
        //URL指向要访问的数据库名mydata
        String url = "jdbc:mysql://"+Ip+":3306/"+dbName  ;
        //MySQL配置时的用户名
//        String user = "root";
        //MySQL配置时的密码
//        String password = "123456";
        try {
            //加载驱动程序
            Class.forName(driver);
            //getConnection()方法，连接MySQL数据库！！
            con = (Connection) DriverManager.getConnection(url,user,password);
            if(!con.isClosed())
                System.out.println("Succeeded connecting to the Database!");
        } catch(ClassNotFoundException e) {
            //数据库驱动类异常处理
            System.out.println("Sorry,can't find the Driver!");
            e.printStackTrace();
        } catch(SQLException e) {
            //数据库连接失败异常处理
            e.printStackTrace();
        }catch (Exception e) {
            // TODO: handle exception
            e.printStackTrace();
        }
        return con ;
    }
    //查询

    /**
     * @param user
     * 查询该表中 类别及对应的数目
     * 2019/1/9
     */
    public static void search(String user){
        HashMap<String, Integer> map = new HashMap<>();
        Connection conn = getConnect();
        if(conn != null){
            System.out.println("数据库连接成功！");
        }else{
            System.out.println("数据库连接失败……");
        }
        //2.创建statement类对象，用来执行SQL语句！！
        try {
            Statement statement = conn.createStatement();
            //要执行的SQL语句
            String sql = "select label from " +"`"+user+"`";
            //ResultSet类，用来存放获取的结果集！！
            ResultSet rs = statement.executeQuery(sql);
            int count = 0;
            String label;
            while (rs.next()) {
                label = rs.getString("label");
                count = map.getOrDefault(label, 0) + 1;
                map.put(label, count);
            }
            rs.close();
            conn.close();
            for(Map.Entry<String, Integer> entry : map.entrySet()){
                System.out.println(entry.getKey() + " : " + entry.getValue());
            }
        }catch (Exception e){
            e.printStackTrace();
        }
    }
    // 删除

    /**
     *
     * @param user 数据库表名
     * @param label 字段
     * 2019/1/9
     */
    public static void delete(String user, String label) {
        Connection conn = getConnect();
        String sql = "delete from `" + user + "` where label = " + label;
        PreparedStatement pstmt;
        int num;
        try {
            pstmt = (PreparedStatement) conn.prepareStatement(sql);
            num = pstmt.executeUpdate();
            if(num == 0){
                System.out.println("sorry，删除失败，表中无匹配项目");
            }else{
                System.out.println("Deleted successful");
            }
            pstmt.close();
            conn.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    /**2019/1/9
     * 插入数据
     * @return
     */
    public static int insert(String user, String[] data) {
        Connection conn = getConnect();
        int i = 0;
        int len = Constant.columns.length;
        String columns = "";
        String values = "";
        for(int k = 0; k < len; k++){
            columns = (k != len - 1 ? columns + Constant.columns[k] + "," : columns + Constant.columns[k]);
            values = (k != len - 1 ? values + "?," : values + "?");
        }
        String sql = "insert into `" + user + "`(" + columns + ") values(" + values + ")";
        PreparedStatement pstmt;
        try {
            pstmt = (PreparedStatement) conn.prepareStatement(sql);
            for(int k = 1; k <= len; k++){
                pstmt.setString(k, data[k]);
            }
            i = pstmt.executeUpdate();
            System.out.println(i);
            pstmt.close();
            conn.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return i;
    }

    /**
     * 2019/1/9
     * @param userName
     * @return
     * 从数据库中获取训练集
     */
    public static String getData(String userName){
        String fileName = "DataSet/TrainSet/"+userName+".csv" ;
        String resultName = "DataSet/TrainSet/"+userName+"_mini.csv" ;
        System.out.println(fileName);
        String Ip = Constant.Ip ;
        String dbName = Constant.dbName;
        String user = Constant.user ;
        String password = Constant.password ;
//        String[] columns = {"x1","x2","x3","x4","x5","x6","x7","x8","x9","x10","label"} ;
        Connection con = getConnect(Ip, dbName, user, password) ;
        if(con!=null){
            System.out.println("数据库连接成功");
        }else {
            System.out.println("It is a pity");
        }
        try {
            //创建statement类对象，用来执行SQL语句！！
            Statement statement = con.createStatement();
            //要执行的SQL语句
            String sql = "select * from "+"`"+userName+"`";
            System.out.println(sql);
            //ResultSet类，用来存放获取的结果集！！
            ResultSet rs = statement.executeQuery(sql);
            //创建文件
            FileWriter writer=new FileWriter(new File(fileName));
            //获取数据库表的元数据
//            ResultSetMetaData data = rs.getMetaData();
//            System.out.println(data.getColumnCount());
            String str = "" ;
            while (rs.next()) {
                //获取各个列数据
//                username = rs.getString("username");
                str = "" ;
                for(int i = 0; i< columns.length; i++){
                    if(i!= columns.length-1){
                        str += rs.getString(columns[i])+"," ;
                    }else {
                        str += rs.getString(columns[i])+"\n" ;
                    }
                }
                writer.write(str);
            }
            writer.close();
            rs.close();
            con.close();
            FileUtils.extractData(fileName, resultName, 400) ;
        }catch (Exception e){
            e.printStackTrace();
        }
        return resultName ;
    }
    public static void main(String[] args) {
        String[] data = {"1","1","1","1","1","1","1","1","1","1","1","1","1","1","1","1" };
        //insert("111", data);
        //delete("111", "1");
        //search("111");
//        String Ip = "localhost" ;
//        String dbName = "first_mysql_test" ;
//        String user = "root" ;
//        String password = "root" ;
//        getData("111");
    }
}
