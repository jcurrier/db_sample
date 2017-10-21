package com.example.todo.util;

import com.amazon.dax.client.dynamodbv2.AmazonDaxClient;
import com.amazon.dax.client.dynamodbv2.ClientConfig;
import com.amazon.dax.client.dynamodbv2.ClusterDaxClient;
import com.amazonaws.auth.AWSCredentials;
import com.amazonaws.auth.BasicSessionCredentials;
import com.amazonaws.auth.profile.ProfileCredentialsProvider;
import com.amazonaws.regions.Region;
import com.amazonaws.regions.Regions;
import com.amazonaws.services.dynamodbv2.AmazonDynamoDB;
import com.amazonaws.services.dynamodbv2.AmazonDynamoDBClient;
import com.amazonaws.services.securitytoken.AWSSecurityTokenServiceClient;
import com.amazonaws.services.securitytoken.model.AssumeRoleRequest;
import com.amazonaws.services.securitytoken.model.AssumeRoleResult;
import com.mongodb.MongoClient;
import com.mongodb.MongoClientURI;

/**
 * Created by Jeff on 12/6/16.
 */
public class ClientHelper {
    private final String MONGO_CONN_URI =
            "mongodb://user:pw@ds139968-a0.mlab.com:39968,ds139968-a1.mlab.com:39968/sample_db?replicaSet=rs-ds139968";
    private static final String ROLE_ARN =
            "arn:aws:iam::099090753569:role/todo_svc_role";
    private static AWSCredentials m_creds = null;

    private static ClientHelper m_instance = null;

    private ClientHelper() {
    }

    public synchronized static ClientHelper instance() {

        if(m_instance == null) {
            m_instance = new ClientHelper();
        }

        return m_instance;
    }

    public MongoClient getMongoClient() {
        MongoClientURI uri = new MongoClientURI(MONGO_CONN_URI);
        MongoClient client = new MongoClient(uri);

        return client;
    }

    public AmazonDynamoDBClient getDynamoClient() {

        AmazonDynamoDBClient client = new AmazonDynamoDBClient(new ProfileCredentialsProvider());
        client.setRegion(Region.getRegion(Regions.US_EAST_1));

        return client;
    }

    public AmazonDynamoDB getDAXClient() {
        ClientConfig daxConfig = new ClientConfig()
                .withEndpoints("devcluster.vrnjkd.clustercfg.dax.use1.cache.amazonaws.com:8111");
        AmazonDaxClient client = new ClusterDaxClient(daxConfig);

        return client;
    }
}
