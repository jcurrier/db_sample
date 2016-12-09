package com.example.todo.util;

import com.amazonaws.auth.AWSCredentials;
import com.amazonaws.auth.BasicSessionCredentials;
import com.amazonaws.auth.profile.ProfileCredentialsProvider;
import com.amazonaws.regions.Region;
import com.amazonaws.regions.Regions;
import com.amazonaws.services.dynamodbv2.AmazonDynamoDBClient;
import com.amazonaws.services.securitytoken.AWSSecurityTokenServiceClient;
import com.amazonaws.services.securitytoken.model.AssumeRoleRequest;
import com.amazonaws.services.securitytoken.model.AssumeRoleResult;

/**
 * Created by Jeff on 12/6/16.
 */
public class ClientHelper {
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


    public AmazonDynamoDBClient getDynamoClient() {

        /*
        AWSSecurityTokenServiceClient stsClient = new AWSSecurityTokenServiceClient();

        AssumeRoleRequest assumeRequest = new AssumeRoleRequest()
                .withRoleArn(ROLE_ARN)
                .withRoleSessionName("todo");

        AssumeRoleResult assumeResult =
                stsClient.assumeRole(assumeRequest);

        BasicSessionCredentials tmpCreds =
                new BasicSessionCredentials(
                        assumeResult.getCredentials().getAccessKeyId(),
                        assumeResult.getCredentials().getSecretAccessKey(),
                        assumeResult.getCredentials().getSessionToken());

        AmazonDynamoDBClient dynamoClient = new AmazonDynamoDBClient(tmpCreds);
        dynamoClient.setRegion(Region.getRegion(Regions.US_WEST_2));

        // Step 2. AssumeRole returns temporary security credentials for
        // the IAM role.

        BasicSessionCredentials temporaryCredentials =
                new BasicSessionCredentials(
                        assumeResult.getCredentials().getAccessKeyId(),
                        assumeResult.getCredentials().getSecretAccessKey(),
                        assumeResult.getCredentials().getSessionToken());

        // Step 3. Make DynamoDB service calls to read data from a
        // DynamoDB table, stored in research@example.com, using the
        // temporary security credentials from the DynamoDB-ReadOnly-role
        // that were returned in the previous step.

        AmazonDynamoDBClient dynamoClient = new AmazonDynamoDBClient(temporaryCredentials);
        */

        AmazonDynamoDBClient client = new AmazonDynamoDBClient(new ProfileCredentialsProvider());
        client.setRegion(Region.getRegion(Regions.US_WEST_2));

        return client;
    }
}
