package Train;

import Preprocess.GetSeries;
import Shapelet.MultivariateShapelet;
import Utils.Constant;
import weka.classifiers.Classifier;
import weka.classifiers.trees.J48;
import weka.core.Instance;
import weka.core.Instances;

import java.io.FileReader;
import java.util.Random;

public class MyRandomForest
{
    private Instances instances;

    private Classifier[] classifiers;

    private String baseTree;

    private int numOfClassifiers;


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
            System.out.println("classifier" + i + "has been build");
        }
    }

    public int classifierInstance(Instance instance) throws Exception
    {
        double maxClassValue = 0.0;

        double classOfInstance = 0;

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
        System.out.println();

        return (int) classOfInstance;

    }
    //初始化 生成训练矩阵
    public static void train_init(String file_path){
        try {
            //获取训练集 预处理
            Train.init(file_path);
            GetSeries.spiltSeries();
            MultivariateShapelet.generateShapelet();
            MultivariateShapelet.getMatrix(Constant.matrixTrain_Path);
            Train.generateModel("p1");
        }catch (Exception e){
            e.printStackTrace();
        }
    }
    public static void main(String args[]) throws Exception {
        MyRandomForest my = new MyRandomForest();
//        String train = "C:/Users/Administrator/Desktop/aaa.csv";
//        String train = Constant.matrixTrain_Path;
//        String train = "DataSet/ResultMatrix/matrixTrain 8.csv";
        String train = "C:/Users/Administrator/Desktop/Papers/Dataset/TrainSet/1.csv";
        train_init(train);
        FileReader reader = new FileReader(train);
//        DataSource dataSource = new DataSource(
//                "F:\数据挖掘\weka开发相关\UCI medium\kr-vs-kp.arff");
//        DataSource dataSource = new DataSource(Constant.matrixTrain_Path);

//        Instances instances = new Instances(dataSource.getDataSet());
//
//        instances.setClassIndex(instances.numAttributes() - 1);
        Instances instances = new Instances(reader);
        instances.setClassIndex(instances.numAttributes() - 1);

        my.setInstances(instances);

        my.setNumOfClassifiers(10);

//        my.setNumOfSelectAtt(100);
        my.setNumOfSelectAtt(60);

        long start = System.currentTimeMillis();
        my.buildClassifiers();
        long end = System.currentTimeMillis();

        System.out.println("MyRandomForest has been builded!!!!!");
        System.out.println("用时：" + (end - start) / 1000.0);


        instances.deleteWithMissingClass();
        for (int i = 0; i < instances.numAttributes(); i++)
        {
            instances.deleteWithMissing(i);
        }


        int correct = 0;
        for (int i = 0; i < instances.numInstances(); i++)
        {
            if (instances.instance(i).classValue() == my.classifierInstance(instances.instance(i)))
            {
                correct++;
            }
        }
//        System.out.println(my.classifierInstance(instances.instance(0)));
        System.out.println("正确率：" + correct*1.0 / (instances.numInstances()*1.0));


//        FileReader test = new FileReader(Constant.matrixTest_Path);
        FileReader test = new FileReader("DataSet/ResultMatrix/matrixTest8.csv");

        Instances testInstances = new Instances(test);
        if (testInstances.classIndex() == -1){
            testInstances.setClassIndex(testInstances.numAttributes() - 1);
        }
        for(int i = 0;i < testInstances.numInstances(); i++){
//            System.out.println(testInstances.numInstances());
//            System.out.println(testInstances.instance(i));
            System.out.println(my.classifierInstance(testInstances.instance(i)));
        }
    }
}
