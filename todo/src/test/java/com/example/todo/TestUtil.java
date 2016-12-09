package com.example.todo;

/**
 * Created by Jeff on 11/21/16.
 */
public class TestUtil {
    public final static String BASIC_AUTH_HEADER = "Authorization";

    public final static String TEST_USER_ID = "test";
    private final static String TEST_USER_CREDS = TEST_USER_ID+":super_secret";
    public final static String BASIC_AUTH_VALUE =
            "Basic "+ java.util.Base64.getEncoder().encodeToString(TEST_USER_CREDS.getBytes());

}
