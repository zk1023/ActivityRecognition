package Predict;

import Preprocess.GetSeries;
import Shapelet.MultivariateShapelet;
import Utils.Constant;
import Utils.FileUtils;
import weka.classifiers.Classifier;
import weka.core.Instances;
import java.io.FileReader;

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

    public static void showResult(String userName) throws Exception {
        System.out.println(" Loading data");
        String predict = Constant.matrixTest_Path ;
        FileReader  reader = new FileReader(predict);
        Instances instances = new Instances(reader);
        if (instances.classIndex() == -1){
            System.out.println("data.numAttributes() = "+instances.numAttributes());
            instances.setClassIndex(instances.numAttributes() - 1);
        }
        Classifier classifier_forest = (Classifier)weka.core.SerializationHelper.read(Constant.model_path+"/"+userName+".model");

        for(int i = 0;i<instances.numInstances();i++){
	    	System.out.println(i+":"+classifier_forest.classifyInstance(instances.instance(i)));
        }
    }
    public static void main(String[] args) throws Exception{
        init();
        GetSeries.spiltSeries();
        System.out.println("生成预测矩阵");
        MultivariateShapelet.getMatrix(Constant.matrixTest_Path);
        System.out.println("预测矩阵生成完毕");
        System.out.println("开始预测");
        showResult("test");
    }
}
