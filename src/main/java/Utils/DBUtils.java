package Utils;
import com.mysql.jdbc.Connection;
import com.opencsv.CSVReader;
import com.opencsv.CSVWriter;
import scala.collection.immutable.Stream;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.TreeSet;

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
    //查询
    public static void search(String Ip, String dbName, String user, String password){
        Connection con = getConnect(Ip, dbName, user, password) ;
        if(con!=null){
            System.out.println("数据库连接成功");
        }else {
            System.out.println("It is a pity");
        }
        //2.创建statement类对象，用来执行SQL语句！！
        try {
            Statement statement = con.createStatement();
            //要执行的SQL语句
            String sql = "select * from pose where flag=0";
            //ResultSet类，用来存放获取的结果集！！
            ResultSet rs = statement.executeQuery(sql);
            String username = null;
            String label = null;
            while (rs.next()) {
                //获取stuname这列数据
                label = rs.getString("pose");
                //获取stuid这列数据
                username = rs.getString("username");
                //输出结果
                System.out.println(username + "\t" + label);
            }
            rs.close();
            con.close();
        }catch (Exception e){
            e.printStackTrace();
        }
    }
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
                for(int i=0;i<Constant.columns.length;i++){
                    if(i!=Constant.columns.length-1){
                        str += rs.getString(Constant.columns[i])+"," ;
                    }else {
                        str += rs.getString(Constant.columns[i])+"\n" ;
                    }
                }
                writer.write(str);
            }
            writer.close();
            rs.close();
            con.close();
            FileUtils.extractData(fileName, resultName, 800) ;
        }catch (Exception e){
            e.printStackTrace();
        }
        return resultName ;
    }
    public static void main(String[] args) {
//        String Ip = "localhost" ;
//        String dbName = "first_mysql_test" ;
//        String user = "root" ;
//        String password = "root" ;
//        getData("111");
    }
}
