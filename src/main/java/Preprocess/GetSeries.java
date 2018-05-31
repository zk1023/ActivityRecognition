package Preprocess;

import Utils.FileUtils;
import com.opencsv.CSVReader;
import com.opencsv.CSVWriter;
import java.io.FileReader;
import java.io.FileWriter;
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
     * @param filePath 训练/测试文件的路径
     * @param number 动作的类别
     * @param filePath_act 分类完成后文件的位置
     * @param labels 动作的标签
     * @throws Exception
     */
    public static void SplitClass(String filePath, int number, String filePath_act, List<String> labels) throws Exception {
        CSVReader reader=new CSVReader(new FileReader(filePath));
        FileWriter fileWriter[] = new FileWriter[number] ;
        CSVWriter csvWriter[] = new CSVWriter[number] ;
        FileUtils.deleteFile(filePath_act);
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
    public static void main(String[] args){

    }
}
