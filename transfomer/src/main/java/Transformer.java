import com.rabbitmq.client.*;
import java.util.ArrayList;
import java.io.IOException;
import java.text.ParseException;
import java.io.FileNotFoundException;
import java.util.concurrent.TimeoutException;


public class Transformer {

    private final static  String QUEUE3 = "dockerfilecontents";




    public static Compose createfichier(String ch)throws IOException{
        String sep = "[+]+";
        String[] tab = ch.split(sep);
        String delims = "[\n\\ ]+";
        String[] tokens = ch.split(delims);
        for (int i = 0; i < tokens.length; i++) {
            if (tokens[i].equals("version:")){
                return (new Compose("Dockercompose", tokens[i + 1], Utils.hashString(ch),tab[0], tab[1]));
            }else if (tokens[i].equals("version")) {
                //System.out.println("DockerFile Added");
                return (new Compose("Dockercompose", tokens[i + 2], Utils.hashString(ch),tab[0], tab[1]));
            }
        }
        return null;
    }

    public static void save(String cont)throws IOException{
      if(createfichier(cont)!= null){
           final Neo bd = new Neo("bolt://localhost:7687", "neo4j", "ahmed");
                bd.addCompose(createfichier(cont));
                System.out.println("DockerCompose Added");
                bd.close();

      }
 }
 
    public static void main(String[] argv) throws Exception,ParseException {
        ConnectionFactory factory = new ConnectionFactory();
        factory.setHost("localhost");
        Connection connection = factory.newConnection();
        Channel channel = connection.createChannel();

        channel.queueDeclare(QUEUE3, false, false, false, null);
        System.out.println("Waiting for contents");
        Consumer consumer = new DefaultConsumer(channel) {
            @Override
            public void handleDelivery(String consumerTag, Envelope envelope, AMQP.BasicProperties properties, byte[] body)
                    throws IOException{
                String message = new String(body, "UTF-8");
                //System.out.println(message);
                save(message);
                //Worker.alldockerfile(message);
                //Worker.getGitdockerfile(message);

            }
        };
        channel.basicConsume(QUEUE3, true, consumer);

    }

}