package kafka;

import Predict.RealTimePrediction;
import Train.Train;
import Utils.Constant;
import kafka.consumer.ConsumerConfig;
import kafka.consumer.ConsumerIterator;
import kafka.consumer.KafkaStream;
import kafka.javaapi.consumer.ConsumerConnector;
import kafka.serializer.StringDecoder;
import kafka.utils.VerifiableProperties;

import java.io.File;
import java.io.FileWriter;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;

public class RunKafkaConsumer {

    private final ConsumerConnector consumer;

    private final static  String TOPIC="test5";

    private RunKafkaConsumer(){
        Properties props=new Properties();
        //zookeeper
        props.put("zookeeper.connect", Constant.Ip+":2181");
        //topic
        props.put("group.id","test");

        //Zookeeper 超时
        props.put("zookeeper.session.timeout.ms", "4000");
        props.put("zookeeper.sync.time.ms", "200");
        props.put("auto.commit.interval.ms", "1000");
        props.put("auto.offset.reset", "smallest");


        props.put("serializer.class", "kafka.serializer.StringEncoder");
        ConsumerConfig config=new ConsumerConfig(props);

        consumer= kafka.consumer.Consumer.createJavaConsumerConnector(config);
    }


    public static void makeFile(String Str) throws Exception{
        String content[] = Str.split("#")[0].split(",") ;
        FileWriter writer = new FileWriter(new File("C:/Users/Administrator/Desktop/22.csv")) ;
        for(int i=0;i<content.length/10;i++){
            for(int j=0;j<10;j++){
                if(j!=9) {
                    writer.append(content[(i * 10 + j)] + ",");
                }else {
                    writer.append(content[(i * 10 + j)]);
                }
            }
            writer.append("\n") ;
        }
        writer.close();
        System.out.println(content.length);
    }
    public static int judgeData(String Str){
        String content[] = Str.split("#")[0].split(",") ;
        int mark = 0;
        for(int i=2;i<10;i++){
            if(("").equals(content[i])||content[i].length()==0){

            }else{
                mark = 1 ;
            }
        }
        return mark ;
    }
    void consume() throws Exception{
        Map<String, Integer> topicCountMap = new HashMap<String, Integer>();
        topicCountMap.put(TOPIC, new Integer(1));

        StringDecoder keyDecoder = new StringDecoder(new VerifiableProperties());
        StringDecoder valueDecoder = new StringDecoder(new VerifiableProperties());

        Map<String, List<KafkaStream<String, String>>> consumerMap =
                consumer.createMessageStreams(topicCountMap,keyDecoder,valueDecoder);
        KafkaStream<String, String> stream = consumerMap.get(TOPIC).get(0);
        ConsumerIterator<String, String> it = stream.iterator();
        String label;
        String content;
        boolean flag = false;
        int messageCount = 0;
        String username ="";
        while (it.hasNext()){
            username ="";
            content = it.next().message() ;
            System.out.println(content);
            if(content.split("#").length == 1 && content.split(",").length <= 10 ){
                continue;
            }
            if("startTrain".equals(content.split("#")[0])){
                flag = Train.train(content.split("#")[1]) ;
                if(flag){
                    new RunKafkaProduce().produce("trainSuccess");
                }else{
                    new RunKafkaProduce().produce("trainFailed");
                }
                continue ;
            }
            if(judgeData(content) == 0){
                continue;
            }
            username = content.split("#")[7];
            if("".equals(username)){
                continue;
            }
            makeFile(content);
            label = RealTimePrediction.classify("C:/Users/Administrator/Desktop/22.csv",username) ;

            System.out.println(label+"-->"+content);
//            System.out.println(it.next().message());
//            messageCount++;
//            if(messageCount == 100){
//                System.out.println("Consumer端一共消费了" + messageCount + "条消息！");
//            }
        }
    }

    public static void main(String[] args) throws Exception {
        new RunKafkaConsumer().consume();
    }

}
