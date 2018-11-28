package Shapelet;

import java.io.*;
import java.util.ArrayList;

import ShapeletTransform.ShapeletTransform;
import ShapeletTransform.MyInstance;
import ShapeletTransform.Shapelet;
import ShapeletTransform.ClusteredShapeletTransform;
import ShapeletTransform.QualityMeasures;
import Utils.Constant;
import Utils.FileUtils;
import com.opencsv.CSVWriter;
import weka.core.Instances;

public class MultivariateShapelet {
    public static ShapeletTransform st;
    public static ArrayList<Shapelet> clusteredShapelets;
    private static void writeShapelet(ArrayList<Shapelet> clusteredShapelets, int a) throws IOException {
        // TODO Auto-generated method stub
        // FileWriter writer = new FileWriter("shapelet.csv",true);
        CSVWriter writer = new CSVWriter(new FileWriter(Constant.filePath_shapelet, true));
        for (int i = 0; i < clusteredShapelets.size(); i++) {
            double[] shapelet = clusteredShapelets.get(i).getContent();
            String[] shapelet_string = new String[shapelet.length + 2];
            for (int j = 0; j < shapelet.length; j++) {
                shapelet_string[j] = shapelet[j] + "";
                if (shapelet_string[j].equals("NAN")) {
                    System.out.println("绗�" + i + "涓猻hapelet鐨勭" + j + "涓�兼槸绌�");
                }
            }
            shapelet_string[shapelet.length] = a + "";
            shapelet_string[shapelet.length + 1] = clusteredShapelets.get(i).getShapeletClass() + "";
            writer.writeNext(shapelet_string);

        }
        writer.close();
    }

    public static Instances basicTransformExample(Instances train) {
        st = new ShapeletTransform();
        System.out.println("classsssssssssssss = " + train.instance(0).classValue());
        // Let m=train.numAttributes()-1 (series length)
        // Let n= train.numInstances() (number of series)
        int nosShapelets = (train.numAttributes() - 1) * train.numInstances() / 5;
        if (nosShapelets < ShapeletTransform.DEFAULT_NUMSHAPELETS)
            nosShapelets = ShapeletTransform.DEFAULT_NUMSHAPELETS;
        st.setNumberOfShapelets(nosShapelets);
        int minLength = (train.numAttributes() - 1) / 2;
        int maxLength = (train.numAttributes() - 1) / 1;
        System.out.println("maxlength = " + maxLength);
        if (maxLength < ShapeletTransform.DEFAULT_MINSHAPELETLENGTH)
            maxLength = ShapeletTransform.DEFAULT_MINSHAPELETLENGTH;
        st.setShapeletMinAndMax(minLength, maxLength);
        st.setQualityMeasure(QualityMeasures.ShapeletQualityChoice.F_STAT);
        //st.setLogOutputFile("ShapeletExampleLog.csv");
        Instances shapeletT = null;
        try {
            shapeletT = st.process(train);
        } catch (Exception ex) {
            System.out.println("Error performing the shapelet transform" + ex);
            ex.printStackTrace();
            System.exit(0);
        }
        return shapeletT;
    }

    public static Instances clusteredShapeletTransformExample(Instances train) {
        Instances shapeletT = null;

//        int nosShapelets = (train.numAttributes() - 1) * train.numInstances() / Constant.clust_num;
        int nosShapelets = st.getNumberOfShapelets() / 3 ;

        // System.out.println("noshapelets = "+nosShapelets);
//        if(nosShapelets > st.getNumberOfShapelets()){
//            nosShapelets = st.getNumberOfShapelets() ;
//        }
        if(nosShapelets < 2){
            nosShapelets = 2 ;
        }
        ClusteredShapeletTransform cst = new ClusteredShapeletTransform(st, nosShapelets);
        //System.out.println("----------"+st.shapelets.size());
        System.out.println(" Clustering down to " + nosShapelets + " Shapelets");
        System.out.println(" From " + st.getNumberOfShapelets() + " Shapelets");
        // ShapeletTransform st = new ShapeletTransform();
        try {
            //	System.out.println(train);
            //	System.out.println("------------"+cst.allShapelets.size());
            shapeletT = cst.process(train);
            // Instances output = cst.calDistance(train,shapelets);
            // return output;
        } catch (Exception ex) {
            System.out.println("Error performing the shapelet clustering" + ex);

            ex.printStackTrace();
            System.exit(0);
        }
        clusteredShapelets = cst.clusteredShapelets;
        return shapeletT;

    }

//    public static void getData(String path_from, String path_to) throws Exception {
//        FileUtils.delDir(path_to);
//
//        //与传感器个数有关
//        for(int  i = 0; i< Constant.number_sensor; i ++){
//            BufferedReader reader = new BufferedReader(new FileReader(new File(path_from + "/sensor_"+i+".csv")));
//            BufferedWriter writer = new BufferedWriter(new FileWriter(new File(path_to + "/sensor_"+i+".csv")));
//            String str = "";
//            int mark = 0 ;
//            int count[] = new int[Constant.number_act] ;
//            for(int j = 0; j < Constant.number_act; j ++){
//                count[j] = 0;
//            }
//            while ((str = reader.readLine()) != null){
//                str = str.replace("\"", "") ;
//                String[] strs = str.split(",");
//                if(mark < Constant.timeSeries + 3){
//                    writer.write(str);
//                    writer.write("\n");
//                    mark ++ ;
//                }else {
////					System.out.println("part"+Main.part+"label  " + strs[Main.timeSeries]);
//                    for(int k = 0; k < Constant.number_act; k ++){
//                        if (strs[Constant.timeSeries].equals(Constant.labels.get(k)) && (count[k] < Constant.part)) {
//                            count[k] ++;
//                            writer.write(str + "\n") ;
//                        }
//                    }
//                }
//            }
//            writer.close();
//            reader.close();
//        }
//        System.out.println("数据选择完毕！");
//    }
    public static void generateShapelet() throws Exception {
        String path = Constant.filePath_format;
        FileUtils.delFile(Constant.filePath_shapelet);
        //传感器个数
        for (int i = 0; i < Constant.number_sensor; i++) {
//            new RunKafkaProduce().produce((i+1)*100/Constant.number_sensor+"%");
//            new RunKafkaProduce().produce("trainSuccess");
            System.out.println("生成shapeleting:============================= " + (i+1));
            //根据训练集中的文件生成shapelet
            FileReader reader = new FileReader(new File(path + "/" + "sensor_" + i + ".csv"));
            Instances train = new Instances(reader);
            train.setClassIndex(train.numAttributes() - 1);
            basicTransformExample(train);
            clusteredShapeletTransformExample(train);
            writeShapelet(clusteredShapelets, i);
        }
        System.out.println("Shapelet生成完毕！");
    }
    /**
    * 方法实现说明
    * @author      zhangkai
    * @param     result 目标矩阵文件位置
    * @return      
    * @exception   
    * @CreateDate:     2018/6/3 8:49
    * @UpdateDate:     2018/6/3 8:49
    */
    public static void getMatrix(String result) throws Exception {
        ShapeletTransform st = new ShapeletTransform();
        int shapelet_number = FileUtils.getNumber_File(Constant.filePath_shapelet) ;
        FileUtils.delFile(result);
        FileWriter writer = new FileWriter(result);
        BufferedWriter bw = new BufferedWriter(writer);
        bw.write("@relation" + " " + "test" + "\r\n");
        for (int i = 1; i < shapelet_number + 1; i++) {
            bw.write("@attribute" + " " + "attribute" + i + " " + "numeric" + "\r\n");
        }
        if(Constant.environment == 0) {
            bw.write("@attribute" + " " + "class" + " " + "{");
            //添加属性 与动作种类有关
            for (int i = 0; i < Constant.number_act; i++) {
                if (i == (Constant.number_act - 1)) {
                    bw.write(Constant.labels.get(i) + "}" + "\r\n");
                    break;
                }
                bw.write(Constant.labels.get(i) + ",");
            }
        }else{
            Constant.labels = FileUtils.getLabels(Constant.matrixTrain_Path);
            Constant.number_act = Constant.labels.size();
            bw.write("@attribute" + " " + "class" + " " + "{");
            //添加属性 与动作种类有关
            for (int i = 0; i < Constant.number_act; i++) {
                if (i == (Constant.number_act - 1)) {
                    bw.write(Constant.labels.get(i) + "}" + "\r\n");
                    break;
                }
                bw.write(Constant.labels.get(i) + ",");
            }
        }
        bw.write("@data" + "\r\n");
        //传感器个数
        File files[] = new File[Constant.number_sensor] ;
        FileReader fileReader[] = new FileReader[Constant.number_sensor] ;
        Instances instances[] = new Instances[Constant.number_sensor] ;
        for(int i = 0; i < Constant.number_sensor; i ++){
            files[i] = new File(Constant.filePath_format + "/" + "sensor_" + i + ".csv") ;
            fileReader[i] = new FileReader(files[i]) ;
            instances[i] = new Instances(fileReader[i]) ;
        }
        String lineStr = "";
        String str = null;
        String line = new String();
        for(int i = 0; i <instances[0].numInstances(); i ++){
            ArrayList<String> arr = new ArrayList<String>();
            for(int j = 0; j < Constant.number_sensor; j ++){
                lineStr = instances[j].instance(i).toString() ;
                arr.add(lineStr) ;
            }
            if(lineStr.isEmpty()){
                System.out.println("获取实例失败");
                bw.close();
                return ;
            }
            MyInstance myInstance = new MyInstance(arr, lineStr.split(",")[lineStr.split(",").length - 1]);
            BufferedReader bufferedReader = new BufferedReader(new FileReader(new File(Constant.filePath_shapelet)));
            while ((str = bufferedReader.readLine()) != null) {
                str=str.replace("\"", "");
                for(int k = 0; k < Constant.number_sensor; k ++){
                    if (str.split(",")[str.split(",").length - 2].equals(Integer.toString(k))) {
                        //	line = line +(new DTW().getDistance(myInstance.content.get(k), str)+"")+",";
                        line = line+st.subsequenceDistance2(myInstance.getContent().get(k), str)+",";
                        //System.out.println("DTW = "+new DTW().getDistance(myInstance.content.get(k), str));
                    }
                }
            }
            if(Constant.environment==0) {
                line = line + myInstance.getInstanceClass() + "";
            }else{
                line = line + "?";
            }
            bw.write(line+"\n");
            line = "";
            bufferedReader.close();
        }
        bw.close();
    }
    public static void main(String[] args) throws Exception {

    }
}

