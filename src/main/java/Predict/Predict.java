package Predict;

import Preprocess.GetSeries;
import Utils.Constant;
import Utils.FileUtils;

/**
*
* @Description:    对无标签的数据进行预测
* @Author:         zhangkai
* @CreateDate:     2018/6/2 19:57
* @UpdateDate:     2018/6/2 19:57
* @Version:        1.0
*/
public class Predict {
    /**
    * 预测过程参数初始化
    * @author      zhangkai
    * @exception
    * @CreateDate:     2018/6/2 20:02
    * @UpdateDate:     2018/6/2 20:02
    */
    public static void init() throws Exception{
        System.out.println("预测过程初始化=============================================");
        Constant.path = "DataSet/hand_test.csv";
        Constant.environment = 1;
        System.out.println("预测数据集："+Constant.path);
        System.out.println();
        //获取传感器个数
        Constant.number_sensor = FileUtils.getNumber_Sensor(Constant.path);
        System.out.println("传感器个数：" + Constant.number_sensor + "\n");
        System.out.println("数据预处理开始=============================================");
    }
    public static void main(String[] args) throws Exception{
        init();
        GetSeries.spiltSeries();
    }
}
