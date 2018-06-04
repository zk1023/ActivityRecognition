package Utils;

import java.util.ArrayList;
import java.util.List;

/**
 *
 * @Description:   记录一些文件路径及其他用到的一些变量
 * @Author:         zhangkai
 * @CreateDate:     2018/5/31 22:18
 * @UpdateDate:     2018/5/31 22:18
 * @Version:        1.0
 */
public class Constant {
    //模型存放位置
    public static String model_path = "models" ;
    //训练或者测试数据路径
    public static String path = "" ;
    //填充缺失值后的文件位置
    public static String path_pad = "" ;
    //按照动作分类后的文件位置
    public static String filePath_act = "DataSet/Act";
    //每个动作按照传感器分类后的文件位置
    public static String filePath_actTosensor = "DataSet/ActSpiltSensor";
    //按照传感器分类后的文件位置
    public static String filePath_sensor = "DataSet/Sensor";
    //按照传感器分类,给数据加上能让weka识别的必要属性
    public static String filePath_format = "DataSet/Format";
    //按照传感器分类,给数据加上能让weka识别的必要属性
    public static String filePath_shapelet = "DataSet/Shapelet/shapelet.csv";
    //训练矩阵
    public static String matrixTrain_Path = "DataSet/ResultMatrix/matrixTrain.csv" ;
    //测试矩阵
    public static String matrixTest_Path = "DataSet/ResultMatrix/matrixTest.csv" ;
    //时间序列长度
    public static int timeSeries = 40 ;
    //是否对训练集数据进行选择  1代表选择部分数据进行训练或测试  0代表不对数据进行选择
    public static int chooseData = 1;
    //传感器种类个数
    public static int number_sensor = 0 ;
    //动作种类个数
    public static int number_act = 0 ;
    //标签类别
    public static List<String> labels = new ArrayList<String>() ;
    //从数据中抽出一部分进行训练或测试，每个传感器类别中抽取part个数据
    public static int part = 40 ;
    //标志 0为实验 1为线上识别
    public static int environment = 0 ;
    //shapelet聚类数量设置
    public static int clust_num = 1500;
}
