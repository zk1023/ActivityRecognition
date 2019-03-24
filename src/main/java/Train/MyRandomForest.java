package Train;

import Preprocess.GetSeries;
import Shapelet.MultivariateShapelet;
import weka.classifiers.Classifier;
import weka.classifiers.Evaluation;
import weka.classifiers.trees.J48;
import weka.classifiers.trees.RandomForest;
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

    /**
     * 建立分类器
     * @throws Exception
     */
    public void buildClassifiers() throws Exception
    {
        this.classifiers = new J48[this.numOfClassifiers];

        for (int i = 0; i < this.numOfClassifiers; i++)
        {
            this.classifiers[i] = new J48();
        }

        for (int i = 0; i < this.numOfClassifiers; i++)
        {
            Instances newInstances = this.getRandomIntances(this.instances);
            //Instances newInstances = this.instances;
            this.classifiers[i].buildClassifier(newInstances);
        }
    }

    /**
     * 进行预测
     * @param instance
     * @return
     * @throws Exception
     */
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
        if(maxClassValue < 12){
            classOfInstance = -1;
        }
        return (int) classOfInstance;
    }
    //初始化 生成训练矩阵
    public static void generateTrainMatrix(String train_path, String trainmatrix_path){
        try {
            //获取训练集 预处理
            Train.init(train_path);
            GetSeries.spiltSeries();
            MultivariateShapelet.generateShapelet();
            MultivariateShapelet.getMatrix(trainmatrix_path);
            System.out.println("训练矩阵生成完毕");
        }catch (Exception e){
            e.printStackTrace();
        }
    }
    // 测试矩阵带标签
    public static void generateTestMatrix(String test_path, String testmatrix_path){
        try {
            //获取训练集 预处理
            System.out.println("预测过程初始化=============================================");
            Train.init(test_path);
            GetSeries.spiltSeries();
            MultivariateShapelet.getMatrix(testmatrix_path);
            System.out.println("预测矩阵生成完毕");
        }catch (Exception e){
            e.printStackTrace();
        }
    }
    //初始化 生成测试矩阵
    public static void main(String args[]) throws Exception {
        MyRandomForest my = new MyRandomForest();
        //训练集和测试集的文件位置
        String train_path;
        train_path = "C:\\Users\\Administrator\\Desktop\\Papers\\Dataset\\WISDM\\TrainSet\\Train\\train3_5.csv";
        train_path = "C:\\Users\\Administrator\\Desktop\\Papers\\Dataset\\DAXIN\\TrainSet\\Train\\object1_5_train.csv";
        String test_path;
        test_path = "C:\\Users\\Administrator\\Desktop\\Papers\\Dataset\\WISDM\\TestSet\\Test\\test3_5.csv";
        test_path = "C:\\Users\\Administrator\\Desktop\\Papers\\Dataset\\DAXIN\\TestSet\\Test\\object1_test.csv";
        //训练矩阵 、测试矩阵位置
        String testObject1 = "DataSet/ResultMatrix/matrixTestO1.csv";
        String testObject = "DataSet/ResultMatrix/matrixTest.csv";
        String trainObject1_7 = "DataSet/ResultMatrix/matrixTrainO1_7.csv";
        String trainObject1_6 = "DataSet/ResultMatrix/matrixTrainO1_6.csv";
        String trainObject1_5 = "DataSet/ResultMatrix/matrixTrainO1_5.csv";
        String trainObject1_4 = "DataSet/ResultMatrix/matrixTrainO1_4.csv";
        //选择源文件生成训练矩阵 和 测试矩阵
        String trainMatrix = trainObject1_5;
        String testMatrix = testObject;
        //生成训练矩阵 和 测试矩阵
        //generateTrainMatrix(train_path, trainMatrix);
        //generateTestMatrix(test_path, testMatrix);
        //根据训练矩阵 建立分类器
        FileReader train = new FileReader(trainMatrix);
        Instances instances = new Instances(train);
        //设置最后一列为标签
        instances.setClassIndex(instances.numAttributes() - 1);
        my.setInstances(instances);
        //建立随机森林分类器
        my.setNumOfClassifiers(20);
//        my.setNumOfSelectAtt(100);
        my.setNumOfSelectAtt(60);
        long start = System.currentTimeMillis();
        my.buildClassifiers();
        long end = System.currentTimeMillis();
        System.out.println("MyRandomForest has been builded!!!!!");
        System.out.println("用时：" + (end - start) / 1000.0);
        //删除没有类标签的样本
        instances.deleteWithMissingClass();
        for (int i = 0; i < instances.numAttributes(); i++)
        {
            instances.deleteWithMissing(i);
        }
        //训练weka自带的随机森林分类器
        RandomForest classifier_forest = new RandomForest();
        classifier_forest.buildClassifier(instances);
        //weka自带随机森林分类器评价
        Evaluation evaluation_forest = new Evaluation(instances);
        evaluation_forest.crossValidateModel(classifier_forest, instances, 10, new Random(1));
        System.out.println(evaluation_forest.toSummaryString());
        System.out.println(evaluation_forest.toMatrixString());
        System.out.println(evaluation_forest.toClassDetailsString());
        //预测过程
        FileReader test = new FileReader(testMatrix);
        Instances testInstances = new Instances(test);
        if (testInstances.classIndex() == -1){
            testInstances.setClassIndex(testInstances.numAttributes() - 1);
        }
        //从训练样本中获取样本的标签
        String s[] = instances.classAttribute().toString().split("\\{")[1].split("}")[0].split(",");
        List<String> label = new ArrayList<>();
        for(String key : s){
            label.add(key);
        }
        System.out.println(Arrays.toString(label.toArray()));
        //预测测试用例
        int correct_my = 0;
        int correct_weka = 0;
        int sum = testInstances.numInstances();
        String realLabels[] = {"biking", "downstairs", "jogging", "sitting", "standing", "upstairs", "walking"};
        for(int i = 0;i < sum; i++){
            //真实的标签
            //int realLabel = new Double(testInstances.instance(i).classValue()).intValue();
            //手动构建的随机森林预测标签
            int resMyRF = new Double(my.classifierInstance(testInstances.instance(i))).intValue();
            //weka随机森林你预测标签
            int resWekaRF = new Double(classifier_forest.classifyInstance(testInstances.instance(i))).intValue();
            //if(resMyRF >= 0){
            //    resMyRF = resWekaRF;
            //}
            //System.out.print("   Real: " + label.get(realLabel));
            int realLabel = i / 10;
            System.out.print("   Real: " + realLabels[i/10]);
            if(resMyRF < 0){
                System.out.print(" ------Predict: " + "unknown activity");
            }else{
                System.out.print(" ------Predict: " + label.get(resMyRF));
            }
            System.out.println("   wekaRF:" + label.get(resWekaRF));
            //if(realLabel == resMyRF){
            //    correct_my++;
            //}
            //if(realLabel == resWekaRF){
            //    correct_weka++;
            //}
        }
        System.out.println("测试样本总数为： " + sum);
        //System.out.println("myRF正确率：" + correct_my*1.0 / (sum*1.0));
        //System.out.println("wekaRF正确率：" + correct_weka*1.0 / (sum*1.0));
    }
}
