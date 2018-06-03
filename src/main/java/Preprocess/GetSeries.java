package Preprocess;
import Utils.Constant;
import Utils.FileUtils;
import com.opencsv.CSVReader;
import com.opencsv.CSVWriter;

import java.io.*;
import java.util.List;

/**
*
* @Description:    把数据转化为时间序列
* @Author:         zhangkai
* @CreateDate:     2018/5/31 22:05
* @UpdateDate:     2018/5/31 22:05
* @Version:        1.0
*/
public class GetSeries {
    /**
     * 对源数据按照动作类别进行分类
     * filePath 训练/测试文件的路径
     *  number 动作的类别
     * filePath_act 分类完成后文件的位置
     * labels 动作的标签
     * @throws Exception
     */
    public static void splitClass() throws Exception {
        String filePath = Constant.path;
        int number = Constant.number_act;
        String filePath_act = Constant.filePath_act;
        List<String> labels = Constant.labels;
        CSVReader reader=new CSVReader(new FileReader(filePath));
        FileWriter fileWriter[] = new FileWriter[number] ;
        CSVWriter csvWriter[] = new CSVWriter[number] ;
        FileUtils.delDir(filePath_act);
        for(int i = 0; i < number; i ++){
            fileWriter[i] = new FileWriter(filePath_act + "/" + labels.get(i) + ".csv") ;
            csvWriter[i] = new CSVWriter(fileWriter[i]) ;
        }
        List<String[]> list = reader.readAll();
        for(int i = 0;i<list.size();i++){
            for(int j = 0; j < number; j ++){
                if(list.get(i)[list.get(i).length-1].equals(labels.get(j))){
                    csvWriter[j].writeNext(list.get(i));
                }
            }
        }
        reader.close();
        for(int i = 0; i < number; i ++){
            csvWriter[i].close();
        }
        System.out.println("根据动作类别对数据划分完毕！");
    }

    /**
     *根据不同传感器划分时间序列
     * @throws Exception
     */
    public static void splitSensor() throws Exception {
        int number = Constant.number_act ;
        FileReader fileReader[] = new FileReader[number] ;
        CSVReader csvReader[] = new CSVReader[number] ;
        FileUtils.deleteFile(Constant.filePath_actTosensor);
        //动作种类
        for(int i = 0; i < number; i ++){
            fileReader[i] = new FileReader(Constant.filePath_act + "/" + Constant.labels.get(i) + ".csv") ;
            csvReader[i] = new CSVReader(fileReader[i]) ;
            FileUtils.createFoder(Constant.filePath_actTosensor, Constant.labels.get(i));
            writeToFile(csvReader[i].readAll(), Constant.labels.get(i)) ;
            csvReader[i].close();
        }
        System.out.println("把数据转化为时间序列 ，并把每中动作分别按照不同传感器进一步分类！");
    }

    /**
     *
     * @param list 数据
     * @param str 写入文件名
     * @throws Exception
     * Description： 时间序列转化
     */
    public static void writeToFile(List<String[]> list, String str) throws Exception {
        for(int i = 0;i<Constant.number_sensor;i++){
            CSVWriter writer = new CSVWriter(new FileWriter(Constant.filePath_actTosensor + "/" + str+"/" + "sensor_"+i+".csv"));
            extractTSeries(list,writer,i);
        }
    }
    public static void writeToFile() throws Exception {
        FileUtils.delDir(Constant.filePath_sensor);
        CSVReader csvReader = new CSVReader(new FileReader(Constant.path)) ;
        List<String[]> list = csvReader.readAll();
        for(int i = 0;i<Constant.number_sensor;i++){
            CSVWriter writer = new CSVWriter(new FileWriter(Constant.filePath_sensor+"/" + "sensor_"+i+".csv"));
            extractTSeries(list,writer,i);
        }
    }
    public static void extractTSeries(List<String[]> list,CSVWriter writer, int i) throws Exception{
        int block_size = Constant.timeSeries;
        for(int j = 0;j<list.size()/block_size;j++){
            String[] value = new String[block_size];
            for(int k = j*block_size;k<block_size*(j+1);k++){
                value[k-j*block_size]= list.get(k)[i];
            }
            writer.writeNext(value);
            writer.flush();
        }
        writer.close();
    }
    /**
    * 按照传感器划分时间序列
    * @author      zhangkai
    * @CreateDate:     2018/6/2 19:06
    * @UpdateDate:     2018/6/2 19:06
    */
    public static void senColumn() throws Exception {
        File file = new File(Constant.filePath_actTosensor);
        if(file.listFiles().length == 0){
            System.out.println(Constant.filePath_actTosensor + " 空文件夹！ ");
            return ;
        }
        String path = Constant.filePath_sensor;
        FileUtils.delDir(path);
        String file_name ;
        File[] folder = file.listFiles();
        if(FileUtils.isFileEmpty(folder[0])){
            System.out.println(folder[0].getName() + " 空文件夹！ ");
            return ;
        }
        //传感器个数
        for (int i = 0; i < Constant.number_act; i++) {
            for (int j = 0; j < Constant.number_sensor; j ++) {
                file_name = "sensor_" + j +".csv" ;
                FileWriter fileWr = new FileWriter(path + "/" + file_name, true) ;
                CSVWriter csvWr = new CSVWriter(fileWr) ;
                write(csvWr, Constant.filePath_actTosensor + "/" + Constant.labels.get(i) + "/" + file_name, Constant.labels.get(i));
            }
        }
        System.out.println("根据传感器类别对数据划分完毕！");
    }
    private static void write(CSVWriter writer, String filename, String class_value) throws Exception {
        CSVReader reader = new CSVReader(new FileReader(new File(filename)));
        List<String[]> list = reader.readAll();
        String[] list2 = new String[list.get(0).length + 1];
        for (int i = 0; i < list.size(); i++) {
            for (int j = 0; j < list.get(i).length; j++) {
                list2[j] = list.get(i)[j];
            }
            list2[list2.length - 1] = class_value;
            writer.writeNext(list2);
        }
        reader.close();
        writer.close();
    }
    /**
    * 给数据添加必要属性，使其能够被weka识别处理
    * @author      zhangkai
    * @CreateDate:     2018/6/2 20:41
    * @UpdateDate:     2018/6/2 20:41
    */
    public static void addAttribute() throws Exception {
        String path_format = Constant.filePath_format ;
        FileUtils.delDir(path_format);
        //传感器个数
        for(int i = 0; i < Constant.number_sensor; i ++){
            String fileReaderName = Constant.filePath_sensor + "/" + "sensor_"+i+".csv" ;
            String fileWriterName = path_format + "/" + "sensor_"+i+".csv" ;
            addAttribute(fileReaderName, fileWriterName);
        }
        System.out.println("数据格式化完毕！");
    }
    private static void addAttribute(String fileReaderName,String FileWriterName) throws Exception {
        FileWriter writer = new FileWriter(FileWriterName);
        FileReader reader = new FileReader(fileReaderName);
        BufferedWriter bw = new BufferedWriter(writer);
        BufferedReader br = new BufferedReader(reader);
        bw.write("@relation" + " " + "test" + "\r\n");
        //与时间序列长度有关
        for (int i = 1; i < Constant.timeSeries + 1; i++) {
            bw.write("@attribute" + " " + "attribute" + i + " " + "numeric" + "\r\n");
        }
        //根据环境来决定是否要继续加上标签
        if(Constant.environment == 0) {
            bw.write("@attribute" + " " + "class" + " " + "{");
            for (int i = 0; i < Constant.number_act; i++) {
                if (i == (Constant.number_act - 1)) {
                    bw.write(Constant.labels.get(i) + "}" + "\r\n");
                    break;
                }
                bw.write(Constant.labels.get(i) + ",");
            }
        }
        bw.write("@data" + "\r\n");
        String str = "";
        while(( str = br.readLine())!=null){
            bw.write(str);
            bw.write("\n");
        }
        bw.close();
        br.close();
    }

    public static void spiltSeries() throws Exception{
        if(Constant.environment==0) {
            splitClass();
            splitSensor();
            senColumn();
        }else if(Constant.environment==1){
            writeToFile();
        }
        addAttribute();
        System.out.println("数据预处理完毕！");
    }
    public static void main(String[] args){

    }
}
