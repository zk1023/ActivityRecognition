package Predict;

import Preprocess.GetSeries;
import Shapelet.MultivariateShapelet;
import Utils.Constant;
import Utils.FileUtils;
import weka.classifiers.Classifier;
import weka.core.Instances;

import java.io.FileReader;
import java.util.ArrayList;

public class RealTimePrediction {
    public static ArrayList<String> classify(String filePath,String userName) throws Exception{
        String label=" ";
        ArrayList<String> labelList = new ArrayList<>();
        Constant.path = filePath;
        Constant.environment = 1;
        Constant.number_sensor = FileUtils.getNumber_Sensor(Constant.path);
        GetSeries.spiltSeries();
        MultivariateShapelet.getMatrix(Constant.matrixTest_Path);
        FileReader reader = new FileReader(Constant.matrixTest_Path);
        Instances instances = new Instances(reader);
        if (instances.classIndex() == -1){
            instances.setClassIndex(instances.numAttributes() - 1);
        }
        Classifier classifier_forest = (Classifier)weka.core.SerializationHelper.read(Constant.model_path+"/"+userName+".model");
        for(int i = 0;i<instances.numInstances();i++){
            System.out.println(i+":"+classifier_forest.classifyInstance(instances.instance(i)));
            labelList.add(classifier_forest.classifyInstance(instances.instance(i))+"");
            label += classifier_forest.classifyInstance(instances.instance(i))+" ";
        }
        return labelList;
    }
    public static void main(String[] args) throws Exception{
        classify("C:/Users/Administrator/Desktop/22.csv","test");
    }
}
