package Train;
import Preprocess.GetSeries;
import Shapelet.MultivariateShapelet;
import Utils.Constant;
import Utils.FileUtils;
import weka.classifiers.Evaluation;
import weka.classifiers.trees.RandomForest;
import weka.core.Instances;
import weka.core.SerializationHelper;

import java.io.FileReader;
import java.util.Random;


/**
*
* @Description:   训练
* @Author:         zhangkai
* @CreateDate:     2018/6/1 8:43
* @UpdateDate:     2018/6/1 8:43
* @Version:        1.0
*/
public class Train {

    public static void init(String fileName) throws Exception{
        System.out.println("训练过程初始化=============================================");
        Constant.path = fileName;
        //获取活动具体类别
        Constant.labels = FileUtils.getLabels(Constant.path) ;
        if(Constant.labels == null || Constant.labels.isEmpty() || Constant.labels.size() == 0 || Constant.labels.size() > 1000){
            System.out.println("something is wrong 其实要么没标签，要么是文件有问题！");
            if(Constant.labels != null){
                System.out.println("label个数为：     " +Constant.labels.size());
            }
            return ;
        }
        System.out.println("训练数据集："+Constant.path);
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

    public static void generateModel(String userName) throws Exception {
        System.out.println("开始生成模型");
        String train = Constant.matrixTrain_Path;
        FileReader  reader = new FileReader(train);
        Instances instances = new Instances(reader);
        if (instances.classIndex() == -1){
            System.out.println("data.numAttributes() = "+instances.numAttributes());
            instances.setClassIndex(instances.numAttributes() - 1);
            instances.setClassIndex(instances.numAttributes() - 1);
        }
        RandomForest classifier_forest = new RandomForest();
        classifier_forest.buildClassifier(instances);
        SerializationHelper.write(Constant.model_path+"/"+userName+".model", classifier_forest);

        Evaluation evaluation_forest = new Evaluation(instances);
        evaluation_forest.crossValidateModel(classifier_forest, instances, 10, new Random(1));
        System.out.println(evaluation_forest.toSummaryString());
        System.out.println(evaluation_forest.toMatrixString());
        System.out.println(evaluation_forest.toClassDetailsString());

        System.out.println("模型生成完毕，并存入到："+Constant.model_path+"/"+userName+".model");
    }
    public static void main(String[] args) throws Exception{
        String fileName = "DataSet/hand_train.csv";
        init(fileName);
        GetSeries.paddingData(fileName,"test.csv");
        GetSeries.spiltSeries();
        MultivariateShapelet.generateShapelet();
        System.out.println("生成训练矩阵");
        MultivariateShapelet.getMatrix(Constant.matrixTrain_Path);
        System.out.println("训练矩阵生成完毕");
        generateModel("test");
    }
}
