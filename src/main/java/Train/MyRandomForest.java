package Train;

import java.io.FileReader;
import java.util.Random;

import Utils.Constant;
import weka.classifiers.Classifier;
import weka.classifiers.trees.Id3;
import weka.classifiers.trees.J48;
import weka.core.Instance;
import weka.core.Instances;

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

        for (int i = 0; i < instances.numAttributes()-instances.numAttributes() * this.numOfSelectAtt / 100; i++)
        {
            int randomInt = seed.nextInt(instances.numAttributes() - 1);

            System.out.println("randomInt: " + randomInt);

            if(instances.classIndex()!=i)
            {
                instances.deleteAttributeAt(randomInt);
            }
            else
            {
                i--;
            }

        }

        for (int i = 0; i < instances.numAttributes(); i++)
        {
            instances.deleteWithMissing(i);
        }

        System.out.println(instances.numInstances());

        return instances;
    }


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
            if (classValue[i] > maxClassValue)
            {
                maxClassValue = classValue[i];

                classOfInstance = i;
            }

        }

        return (int) classOfInstance;

    }

    public static void main(String args[]) throws Exception
    {
        MyRandomForest my = new MyRandomForest();
        String train = "C:/Users/Administrator/Desktop/aaa.csv";
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

        my.setNumOfSelectAtt(100);

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
        System.out.println("正确率：" + correct*1.0 / (instances.numInstances()*1.0));

    }
}
