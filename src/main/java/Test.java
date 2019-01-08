import Utils.Constant;
import Utils.FileUtils;
import com.opencsv.CSVReader;
import com.opencsv.CSVWriter;
import scala.collection.immutable.Stream;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.Reader;
import java.util.*;

public class Test {

    // 桶排序
    public static double[] bucketSort(double[] array){
        // 1.得到数列的最大值和最小值以及差值d
        double max = array[0];
        double min = array[0];
        int len = array.length;
        for(int i = 1;i < len; i++){
            if(max < array[i]){
                max = array[i];
            }
            if(min > array[i]){
                min = array[i];
            }
        }
        double d = max - min;
        //2.初始化桶
        int bucketNum = array.length;
        ArrayList<LinkedList<Double>> bucketList = new ArrayList<>();

        for(int i = 0;i < bucketNum; i++){
            bucketList.add(new LinkedList<>());
        }
        //3.遍历原始数组，将每个元素放入桶中
        for(int i = 0; i < bucketNum; i++){
            int num = (int)((array[i] - min) * (bucketNum - 1) / d);
            bucketList.get(num).add(array[i]);
        }
        //4.对每个桶内部进行排序
        for(int i = 0; i < bucketNum; i++){
            //jdk底层采用了归并排序或者归并的优化版本
            Collections.sort(bucketList.get(i));
        }
        //5.输出全部元素
        double[] sortedArray = new double[bucketNum];
        int index = 0;
        for(LinkedList<Double> list : bucketList){
            for(Double elem : list){
                sortedArray[index] = elem;
                index++;
            }
        }
        return sortedArray;
    }
    // 计数排序
    public static int[] countSort(int[] array){
        // 1.得到数列的最大值和最小值以及差值d
        int max = array[0];
        int min = array[0];
        int len = array.length;
        for(int i = 1;i < len; i++){
            if(max < array[i]){
                max = array[i];
            }
            if(min > array[i]){
                min = array[i];
            }
        }
        int d = max - min;
        //2.创建统计数组并统计元素个数
        int[] countArray = new int[d + 1];
        for(int i = 0;i < len; i++){
            countArray[array[i] - min]++;
        }
        //3.统计数组做变形，后面的元素等于前面元素之和
        int sum = 0;
        for(int i = 0; i < countArray.length; i++){
            sum += countArray[i];
            countArray[i] = sum;
        }
        //4.倒序遍历原始数列，从统计数组中找到正确的位置，输出到结果数组中
        int [] sortedArray = new int[len];
        for(int i = array.length - 1; i >= 0; i--){
            sortedArray[countArray[array[i] - min] - 1] = array[i];
            countArray[array[i] - min]--;
        }
        return sortedArray;
    }
    public static void main(String[] args) throws Exception{
        double[] array = new double[]{4.12, 6.421, 0.0023, 3.0, 2.123, 8.122};
        double[] sortedArray = bucketSort(array);
//        int[] array = new int[]{95, 94, 91, 98, 99, 90, 99, 93, 91, 92};
//        int[] sortedArray = countSort(array);
        System.out.println(Arrays.toString(sortedArray));
    }
    /**
//     * 从训练集中，每类数据提取一部分用来训练
//     * @param filePath  源文件
//     * @param targetPath  目标文件
//     * @param num 提取的num个数
//     * @return
//     * @throws Exception
     */
//    public static boolean extractData(String filePath, String targetPath, int num) throws Exception{
////        int num = 800 ;
//        //查找文件中存在的标签
//        List<String> labels = FileUtils.getLabels(filePath) ;
//        System.out.println(Arrays.toString(labels.toArray()));
//        //记录每一类标签及其对应的数目
//        HashMap<String, Integer> map = new HashMap<String, Integer>() ;
//        for(int i=0;i<labels.size();i++){
//            map.put(labels.get(i), 0) ;
//        }
//        //读取数据
//        CSVReader reader = new CSVReader(new FileReader(new File(filePath))) ;
//        List<String[]> list = reader.readAll();
//        CSVWriter writer = new CSVWriter(new FileWriter(targetPath));
//        String str = "" ;
//        int column = list.get(0).length ;
//        int row = list.size() ;
//        for(int i=0;i<row;i++){
//            //最后一列代表活动类别
//            String key = list.get(i)[column-1] ;
//            int value = map.get(key) ;
//            if(value < num){
//                value ++ ;
//                map.put(key, value) ;
//                writer.writeNext(list.get(i));
//            }
//        }
//        writer.close();
//        reader.close();
//        return true ;
//    }
    public static void writeFile(String Str) throws Exception{
        String content[] = Str.split("#")[0].split(",") ;
        int mark = 0;
        System.out.println("1:" + content[0]);
        for(int i=0;i<5;i++){
            if(content[i].equals("")||content[i].length()==0){

            }else{
                mark = 1 ;
            }
        }

//        FileWriter writer = new FileWriter(new File("C:/Users/Administrator/Desktop/22.csv")) ;
//        for(int i=0;i<content.length/10;i++){
//            for(int j=0;j<10;j++){
//                if(j!=9) {
//                    writer.append(content[(i * 10 + j)] + ",");
//                }else {
//                    writer.append(content[(i * 10 + j)]);
//                }
//            }
//            writer.append("\n") ;
//        }
//        writer.close();
//        System.out.println(content.length);
    }
    public static List<String> getLables(String fileName){
        List<String> labels = new ArrayList<>() ;
        try {
            File file = new File(fileName) ;
            if(file.exists()){
                CSVReader reader=new CSVReader(new FileReader(fileName));
                List<String[]> list = reader.readAll();
                reader.close();
                for(int i=0;i<list.size();i++){
                    if("@data".equals(list.get(i)[0])){
                        String[] str = list.get(i-1) ;
                        int len = str.length ;
                        for(int j=0;j<len;j++){
                            System.out.println(str[j]);
                            if(j==0){
                                labels.add(str[j].split("\\{")[1]) ;
                            }else if(j==len-1){
                                labels.add(str[j].split("}")[0]) ;
                            }else{
                                labels.add(str[j]) ;
                            }
                        }
                        break ;
                    }
                }
            }

        }catch (Exception e){
            e.printStackTrace();
        }
        return labels ;
    }
    public static int lengthOfLongestSubstring(String s) {
        int index = 0;
        int maxLength = 0;
        int curLength = 0;
        Map<Character,  Integer> map = new HashMap<>() ;
        for(int i = 0; i < s.length(); i++){
            if(map.get(s.charAt(i)) == null){
                map.put(s.charAt(i), i) ;
                curLength ++;
            }else{
                int n = map.get(s.charAt(i));
                index = index > n ? index : n ;
                curLength = i - index;
                map.put(s.charAt(i), i);
            }
            maxLength = maxLength > curLength ? maxLength : curLength;
        }
        return maxLength;
    }

}
