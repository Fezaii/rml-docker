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


import static org.neo4j.driver.v1.Values.parameters;

public class Neo implements AutoCloseable
{
    // Driver objects are thread-safe and are typically made available application-wide.
    Driver driver;



    public Neo(String uri, String user, String password)
    {
        driver = GraphDatabase.driver(uri, AuthTokens.basic(user, password));
    }




    public void  addFile( final Fichier dockerfile)
    {
        try ( Session session = driver.session() )
        {
            session.writeTransaction( new TransactionWork<Void>() {
                @Override
                public Void execute(Transaction tx) {
                    return createDockerFileNode(tx, dockerfile);
                }
            });
;
        }
    }

    public void  addCompose( final Compose dockercompose)
    {
        try ( Session session = driver.session() )
        {
            session.writeTransaction( new TransactionWork<Void>() {
                @Override
                public Void execute(Transaction tx) {
                    return createDockerComposeNode(tx, dockercompose);
                }
            });
            ;
        }
    }



    private static Void createDockerFileNode( Transaction tx, Fichier fichier )
    {
        StatementResult result = tx.run("MATCH (a:DockerFile) RETURN a.hash AS hash");
        while ( result.hasNext() )
        {
            Record record = result.next();
            if (record.get("hash").asString().equals(fichier.getHash())){
                return  null;
            }
        }
        tx.run( "CREATE (a:DockerFile {name: $name,image: $image,hash: $hash,repo: $repo,path: $path})", parameters( "name", fichier.getName(),"image",fichier.getImage(),"hash",fichier.getHash(),"repo",fichier.getRepo(),"path",fichier.getPath()) );
        createImageNode(tx,fichier.getImage());
        String ch = new RandomString().nextString();
        createImageNode(tx,ch);
        createRelationships(tx,fichier.getHash(),ch);
        createRelationships2(tx,ch,fichier.getImage());
        return null;
    }

    private static Void createDockerComposeNode( Transaction tx, Compose fichier )
    {
        StatementResult result = tx.run("MATCH (a:DockerCompose) RETURN a.hash AS hash");
        while ( result.hasNext() )
        {
            Record record = result.next();
            if (record.get("hash").asString().equals(fichier.getHash())){
                return  null;
            }
        }
        tx.run( "CREATE (a:DockerCompose {name: $name,version: $version,hash: $hash,repo: $repo,path: $path})", parameters( "name", fichier.getName(),"version",fichier.getVersion(),"hash",fichier.getHash(),"repo",fichier.getRepo(),"path",fichier.getPath()) );
        return null;
    }



    private static Void createImageNode( Transaction tx, String name)
    {
        StatementResult result = tx.run("MATCH (a:Image) RETURN a.name AS name");
        while ( result.hasNext() )
        {
            Record record = result.next();
            if (record.get("name").asString().equals(name)){
            return  null;
        }
        }
        tx.run( "CREATE (a:Image {name: $name})", parameters( "name", name) );
        return null;
    }





    private static Void createRelationships( Transaction tx, String dockerfile,String image )

    {
        //System.out.println("je suis la");
        tx.run( "MATCH (a:DockerFile {hash: $hash }),(b:Image {name: $image}) MERGE (a)-[r:BUILD]->(b)", parameters( "hash", dockerfile,"image",image ));
        return null;
    }





    private static Void createRelationships2( Transaction tx, String img1,String img2 )
    {
        tx.run( "MATCH (a:Image { name:$image1 }),(b:Image {name: $image2}) MERGE (a)-[r:FROM]->(b)", parameters( "image1",img1,"image2",img2 ));
        return null;
    }





    public void close()
    {
        // Closing a driver immediately shuts down all open connections.
        driver.close();
    }


}
