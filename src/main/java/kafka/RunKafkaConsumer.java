package kafka;

import Predict.RealTimePrediction;
import kafka.consumer.ConsumerConfig;
import kafka.consumer.ConsumerIterator;
import kafka.consumer.KafkaStream;
import kafka.javaapi.consumer.ConsumerConnector;
import kafka.serializer.StringDecoder;
import kafka.utils.VerifiableProperties;
import org.apache.kafka.clients.producer.KafkaProducer;

import java.io.File;
import java.io.FileWriter;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;

public class RunKafkaConsumer {

    private final ConsumerConnector consumer;

    private final static  String TOPIC="test6";

    private RunKafkaConsumer(){
        Properties props=new Properties();
        //zookeeper
        props.put("zookeeper.connect","219.216.64.41:2181");
        //topic
        props.put("group.id","logstest");

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
        for(int i=0;i<5;i++){
            if(content[i].equals("")||content[i].length()==0){

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

        int messageCount = 0;
        String label;
        String content;
        while (it.hasNext()){
            content = it.next().message() ;
            if(judgeData(content) == 0){
                continue;
            }
            makeFile(content);
            label = RealTimePrediction.classify("C:/Users/Administrator/Desktop/22.csv","pb_20180828") ;
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
