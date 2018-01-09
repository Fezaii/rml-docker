import org.neo4j.cypher.internal.frontend.v2_3.ast.functions.Str;
import org.neo4j.driver.v1.AuthTokens;
import org.neo4j.driver.v1.Driver;
import org.neo4j.driver.v1.GraphDatabase;
import org.neo4j.driver.v1.Session;
import org.neo4j.driver.v1.StatementResult;
import org.neo4j.driver.v1.Transaction;
import org.neo4j.driver.v1.TransactionWork;
import org.neo4j.driver.v1.*;
import java.util.concurrent.TimeoutException;
import java.io.*;
import java.io.File;
import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.rmi.RemoteException;
import java.util.logging.Level;
import java.util.logging.Logger;

import java.io.IOException;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.util.EntityUtils;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import java.io.FileNotFoundException;
import java.text.ParseException;
import com.google.gson.Gson;
import org.json.simple.*;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.StringWriter;
import java.io.Writer;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.List;
import java.util.Map;


import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;


public class Utils {


    public static boolean isDockerfile(String s){
        String[] tokens = s.split("/");
        for (int j = 0; j < tokens.length; j++){
            if(tokens[j].equals("docker-compose.yml")){
                return  true;
            }
        }
        return false;
    }


    public static ArrayList<String> alldockerfile(String ch){
        ArrayList<String> dockerfilelist = new ArrayList<String>();
        String url = "https://api.github.com/repos/"+ch+"/git/trees/master?recursive=1&access_token=votre-clé";
        try {
            CloseableHttpClient httpClient = HttpClientBuilder.create().build();
            HttpGet request = new HttpGet(url);
            request.addHeader("content-type", "application/json");
            HttpResponse result = httpClient.execute(request);
            String json = EntityUtils.toString(result.getEntity(), "UTF-8");

            //System.out.println(json);
            JsonElement jelement = new JsonParser().parse(json);
            JsonObject jo = jelement.getAsJsonObject();
            if(jo.get("tree")!= null) {
                String tree = jo.get("tree").toString();
                //tree = tree.substring(1,tree.length()-1);
                JsonElement element = new JsonParser().parse(tree);
                JsonArray jarr = element.getAsJsonArray();
                for (int i = 0; i < jarr.size(); i++) {
                    JsonObject joo = (JsonObject) jarr.get(i);
                    String fullName = joo.get("path").toString();
                    fullName = fullName.substring(1, fullName.length() - 1);
                    if (isDockerfile(fullName)) {
                        dockerfilelist.add(fullName);
                        //System.out.println(fullName);
                        //return fullName;
                    }


                }
            }

        }  catch (IOException ex) {
            System.out.println(ex.getStackTrace());
        }
        return  dockerfilelist;

    }

}