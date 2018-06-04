package Predict;

import Preprocess.GetSeries;
import Shapelet.MultivariateShapelet;
import Utils.Constant;
import Utils.FileUtils;
import com.sun.org.apache.bcel.internal.generic.GETSTATIC;
import weka.classifiers.Classifier;
import weka.classifiers.Evaluation;
import weka.core.Instances;

import java.io.FileReader;
import java.util.Random;

public class Test {

    public static void initTest(String filePath) throws Exception{
        System.out.println("测试过程初始化=============================================");
        Constant.path = filePath;
        //获取活动具体类别
        Constant.labels = FileUtils.getLabels(Constant.path) ;
        System.out.println("测试数据集："+Constant.path);
        System.out.println();
        //获取动作个数
        Constant.number_act  = Constant.labels.size() ;
        System.out.println("动作个数："+Constant.labels.size());
        System.out.println("动作种类：");
        for(int count = 0; count < Constant.number_act; count ++){
            System.out.print(Constant.labels.get(count) + "  ");
            if(count == Constant.number_act - 1){
                System.out.println();
            }
        }
        //获取传感器个数
        Constant.number_sensor = FileUtils.getNumber_Sensor(Constant.path);
        System.out.println("传感器个数：" + Constant.number_sensor + "\n");
        if(Constant.number_sensor == 0){
            System.out.println("something is wrong 竟然没有标签，文件错误！");
            return ;
        }
        System.out.println("数据预处理开始=============================================");
    }

    public static void showResult(String userName) throws Exception {
        System.out.println("Loading data");
        String test = Constant.matrixTest_Path ;
        FileReader  reader = new FileReader(test);
        Instances instances = new Instances(reader);
        if (instances.classIndex() == -1){
            System.out.println("data.numAttributes() = "+instances.numAttributes());
            instances.setClassIndex(instances.numAttributes() - 1);
        }
        Classifier classifier_forest = (Classifier)weka.core.SerializationHelper.read(Constant.model_path+"/"+userName+".model");

        Evaluation evaluation_forest = new Evaluation(instances);
        evaluation_forest.crossValidateModel(classifier_forest, instances, 10, new Random(1));
        System.out.println(evaluation_forest.toSummaryString());
        System.out.println(evaluation_forest.toMatrixString());
        System.out.println(evaluation_forest.toClassDetailsString());


        int k=0;
        int i;
        for(i = 0;i<instances.numInstances();i++){
            if(classifier_forest.classifyInstance(instances.instance(i))== instances.instance(i).classValue()){
                k++;
            }
            if(classifier_forest.classifyInstance(instances.instance(i))!= instances.instance(i).classValue()){
                System.out.println(i+":"+classifier_forest.classifyInstance(instances.instance(i))+"   "+"true value = "+instances.instance(i).classValue());
            }
        }
        System.out.println("right: "+k+" all: "+i);
        System.out.println("rate: "+(Double.parseDouble(k+"")/Double.parseDouble(i+"")));
    }

    public static void main(String[] args) throws Exception{
        String fileName = "DataSet/hand_test_label.csv";
        initTest(fileName);
        GetSeries.paddingData(fileName, "test.csv");
        GetSeries.spiltSeries();
        System.out.println("生成测试矩阵");
        MultivariateShapelet.getMatrix(Constant.matrixTest_Path);
        System.out.println("测试矩阵生成完毕");
        System.out.println("开始测试");
        showResult("test");
    }
}
