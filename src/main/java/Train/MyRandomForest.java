package Train;

import Preprocess.GetSeries;
import Shapelet.MultivariateShapelet;
import Utils.Constant;
import Utils.FileUtils;
import weka.classifiers.Classifier;
import weka.classifiers.trees.J48;
import weka.core.Instance;
import weka.core.Instances;

import java.io.FileReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;

public class MyRandomForest
{
    private Instances instances;

    private Classifier[] classifiers;

    private String baseTree;

    //分类器个数
    private int numOfClassifiers;

    //属性个数
    private long numOfSelectAtt;

    public Instances getInstances()
    {
        return instances;
    }

    public void setInstances(Instances instances)
    {
        this.instances = instances;
    }

    public Classifier[] getClassifiers()
    {
        return classifiers;
    }

    public void setClassifiers(Classifier[] classifiers)
    {
        this.classifiers = classifiers;
    }

    public String getBaseTree()
    {
        return baseTree;
    }

    public void setBaseTree(String baseTree)
    {
        this.baseTree = baseTree;
    }

    public int getNumOfClassifiers()
    {
        return numOfClassifiers;
    }

    public void setNumOfClassifiers(int numOfClassifiers)
    {
        this.numOfClassifiers = numOfClassifiers;
    }

    public long getNumOfSelectAtt()
    {
        return numOfSelectAtt;
    }

    public void setNumOfSelectAtt(long numOfSelectAtt)
    {
        this.numOfSelectAtt = numOfSelectAtt;
    }

    public Instances getRandomIntances(Instances instances)
    {
        Random seed = new Random();
        //随机选取样本
        Instances newInstances = new Instances(instances, 0);
        int randomInt;
        int len = instances.numInstances();
        for (int i = 0; i < this.numOfSelectAtt; i++)
        {
            randomInt = seed.nextInt(len);
//            System.out.print(randomInt + " ");
            newInstances.add(instances.instance(randomInt));
        }
//        System.out.println();
//        System.out.println("newInstances.numInstances(): " + newInstances.numInstances());
        //随机选取特征
        len = instances.numAttributes();
        len = len < 100 ? 100 : len;
        randomInt = seed.nextInt(len / 100);
        for(int i = 0; i < randomInt; i++){
            newInstances.deleteAttributeAt(seed.nextInt(newInstances.numAttributes() - 1));
        }
//        System.out.println("newInstances.numAttributes(): " + newInstances.numAttributes());
        return newInstances;
    }

//    public Instances getRandomIntances(Instances instances)
//    {
//        Random seed = new Random();
//        //随机选取样本
//        Instances newInstances ;
//        int randomInt;
//        for (int i = 0; i < instances.numAttributes()-instances.numAttributes() * this.numOfSelectAtt / 100; i++)
//        {
//            randomInt = seed.nextInt(instances.numAttributes() - 1);
//
//            System.out.println("randomInt: " + randomInt);
//
//            if(instances.classIndex() != i)
//            {
//                instances.deleteAttributeAt(randomInt);
//            }
//            else
//            {
//                i--;
//            }
//
//        }
//
//        for (int i = 0; i < instances.numAttributes(); i++)
//        {
//            instances.deleteWithMissing(i);
//        }
//
//        System.out.println(instances.numInstances());
//        System.out.println(instances.numAttributes());
//
//        return instances;
//    }


    public void buildClassifiers() throws Exception
    {
//        this.classifiers = new Id3[this.numOfClassifiers];
//
//        for (int i = 0; i < this.numOfClassifiers; i++)
//        {
//            this.classifiers[i] = new Id3();
//        }
        this.classifiers = new J48[this.numOfClassifiers];

        for (int i = 0; i < this.numOfClassifiers; i++)
        {
            this.classifiers[i] = new J48();
        }

        for (int i = 0; i < this.numOfClassifiers; i++)
        {
            Instances newInstances = this.getRandomIntances(this.instances);

            this.classifiers[i].buildClassifier(newInstances);
        }
    }

    public int classifierInstance(Instance instance) throws Exception
    {
        double maxClassValue = 0.0;

        double classOfInstance = 0;
        //类别总数
        double classValue[] = new double[this.instances.numClasses()];
        for (int i = 0; i < this.instances.numClasses(); i++)
        {
            classValue[i] = 0.0;
        }

        for (int i = 0; i < this.numOfClassifiers; i++)
        {
            classValue[(int) classifiers[i].classifyInstance(instance)]++;
        }

        for (int i = 0; i < this.instances.numClasses(); i++)
        {
            System.out.print(classValue[i]+" ");
            if (classValue[i] > maxClassValue)
            {
                maxClassValue = classValue[i];

                classOfInstance = i;
            }
        }

        return (int) classOfInstance;

    }
    //初始化 生成训练矩阵
    public static void train_init(String file_path){
        try {
            //获取训练集 预处理
            Train.init(file_path);
            GetSeries.spiltSeries();
            MultivariateShapelet.generateShapelet();
            MultivariateShapelet.getMatrix("DataSet/ResultMatrix/matrixTrainP3_6.csv");
            System.out.println("训练矩阵生成完毕");
        }catch (Exception e){
            e.printStackTrace();
        }
    }
    // 测试矩阵带标签
    public static void test_init(String file_path){
        try {
            //获取训练集 预处理
            System.out.println("预测过程初始化=============================================");
            Train.init(file_path);
            GetSeries.spiltSeries();
            MultivariateShapelet.getMatrix("DataSet/ResultMatrix/matrixTestP3_6.csv");
            System.out.println("预测矩阵生成完毕");
        }catch (Exception e){
            e.printStackTrace();
        }
    }
    //测试矩阵不带标签
    public static void test(String file_path){
        try {
            //获取训练矩阵 预处理
            System.out.println("预测过程初始化=============================================");
            Constant.path = file_path;
            Constant.environment = 1;
            System.out.println("预测数据集："+Constant.path);
            System.out.println();
            //获取传感器个数
            Constant.number_sensor = FileUtils.getNumber_Sensor(Constant.path);
            System.out.println("传感器个数：" + Constant.number_sensor + "\n");
            System.out.println("数据预处理开始=============================================");
            GetSeries.spiltSeries();
            System.out.println("生成预测矩阵");
            MultivariateShapelet.getMatrix(Constant.matrixTest_Path);
            System.out.println("预测矩阵生成完毕");
        }catch (Exception e){
            e.printStackTrace();
        }
    }
    //初始化 生成测试矩阵
    public static void main(String args[]) throws Exception {
        MyRandomForest my = new MyRandomForest();
//        String train = "C:/Users/Administrator/Desktop/aaa.csv";
//        String train = Constant.matrixTrain_Path;
//        String train = "DataSet/ResultMatrix/matrixTrain 8.csv";
        String train;
//        train = "C:/Users/Administrator/Desktop/Papers/Dataset/TrainSet/1.csv";
        train = "C:\\Users\\Administrator\\Desktop\\Papers\\Dataset\\WISDM\\TrainSet\\Train\\train3_6.csv";
        String test_path = "C:\\Users\\Administrator\\Desktop\\Papers\\Dataset\\WISDM\\TestSet\\Test\\test3_6.csv";
//        Train.generateModel("p1");
        //生成训练矩阵 和 测试矩阵
        train_init(train);
        test_init(test_path);
        //test(test_path);
        //训练矩阵 、测试矩阵位置
        String trainMatrix3_6 = "DataSet/ResultMatrix/matrixTrainP3_6.csv";
        String trainMatrix3_5 = "DataSet/ResultMatrix/matrixTrainP3_5.csv";
        String testMatrix3_6 = "DataSet/ResultMatrix/matrixTestP3_6.csv";

        //根据训练矩阵 建立分类器
        FileReader reader = new FileReader(trainMatrix3_6);
        Instances instances = new Instances(reader);
        //设置最后一列为标签
        instances.setClassIndex(instances.numAttributes() - 1);
        my.setInstances(instances);
        //建立随机森林分类器
        my.setNumOfClassifiers(10);
//        my.setNumOfSelectAtt(100);
        my.setNumOfSelectAtt(60);
        //long start = System.currentTimeMillis();
        my.buildClassifiers();
        //long end = System.currentTimeMillis();
        System.out.println("MyRandomForest has been builded!!!!!");
        //获取样本的标签
        String s[] = instances.classAttribute().toString().split("\\{")[1].split("}")[0].split(",");
        List<String> label = new ArrayList<>();
        for(String key : s){
            label.add(key);
        }
        System.out.println(Arrays.toString(label.toArray()));
        //System.out.println("用时：" + (end - start) / 1000.0);
        //删除没有类标签的样本
        //instances.deleteWithMissingClass();
        //for (int i = 0; i < instances.numAttributes(); i++)
        //{
        //    instances.deleteWithMissing(i);
        //}


        int correct = 0;
        for (int i = 0; i < instances.numInstances(); i++)
        {
            if (instances.instance(i).classValue() == my.classifierInstance(instances.instance(i)))
            {
                correct++;
            }
        }
        System.out.println("样例总数为：" + instances.numInstances());

//        System.out.println(my.classifierInstance(instances.instance(0)));
        System.out.println("正确率：" + correct*1.0 / (instances.numInstances()*1.0));


//        FileReader test = new FileReader(Constant.matrixTest_Path);
        //预测过程
        System.out.println("Downstairs,Jogging,Sitting,Standing,Upstairs,Walking");
        String testLabel[] = {"Downstairs","Jogging" ,"Sitting", "Standing", "Upstairs", "Walking"};
        FileReader test = new FileReader(testMatrix3_6);

        Instances testInstances = new Instances(test);
        if (testInstances.classIndex() == -1){
            testInstances.setClassIndex(testInstances.numAttributes() - 1);
        }
        for(int i = 0;i < testInstances.numInstances(); i++){
            //System.out.println(testInstances.instance(i));
            if(i % 10 != 0){
                System.out.println("------Predict: " + label.get(new Double(my.classifierInstance(testInstances.instance(i))).intValue()));
            }else{
                System.out.println("RealLabel " + testLabel[i/10] + ": ");
            }
        }
        System.out.println("测试样本为： " + testInstances.numInstances());
    }
}
