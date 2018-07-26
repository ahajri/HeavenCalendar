package com.ahajri.heaven.calendar.realmongo;

//import gherkin.deps.com.google.gson.Gson;
//import gherkin.deps.com.google.gson.GsonBuilder;
//import gherkin.deps.com.google.gson.internal.StringMap;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpDelete;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.BasicHttpEntity;
import org.apache.http.impl.client.HttpClients;
import org.bson.Document;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.MethodSorters;
import org.springframework.context.annotation.Bean;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.test.context.junit4.SpringRunner;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.sun.xml.internal.fastinfoset.util.StringIntMap;

import java.io.*;
import java.util.Map;

import static org.junit.Assert.*;

@RunWith(SpringRunner.class)
@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class CloudApiMongoIntegTest {


    private static String persistedId;

    @BeforeClass
    public static void setUp() throws Exception {
        print("*********setUp***********");

    }

    @AfterClass
    public static void tearDown() throws Exception {
        print("*********tearDown********");
    }

    private static void print(String message) {
        System.out.println(message);
    }

    @Bean
    public BCryptPasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Test
    public void testACreateUser() {
        try {
            Document user = new Document();
            user.append("username", "ahajri");
            user.append("password", passwordEncoder().encode("ahajri"));

            final String postUrl = "https://api.mlab.com/api/1/databases/rocsi_db/collections/users?apiKey=pSg2YGo6Qc53wzwsHVfJrEpB7jaPqMa6";

            final HttpClient httpclient = HttpClients.createDefault();
            final HttpPost httppost = new HttpPost(postUrl);

            httppost.setHeader("content-type", "application/json");
            BasicHttpEntity basicEntity = new BasicHttpEntity();
            basicEntity.setContent(new ByteArrayInputStream(prettyPrint(user).getBytes()));
            httppost.setEntity(basicEntity);
            HttpResponse response = httpclient.execute(httppost);
            int httpCode = response.getStatusLine().getStatusCode();
            print("Creation http code = " + httpCode);
            HttpEntity entity = response.getEntity();
            InputStream is = entity.getContent();
            Document data = load(is, Document.class);
            persistedId = (String) ((Map) data.get("_id")).get("$oid");
            assertNotNull(persistedId);
            is.close();
        } catch (IOException e) {
            fail(e.getMessage());
        }


    }

    @Test
    public void testBFindUserById() {
        try {
            final String getUrl = "https://api.mlab.com/api/1/databases/rocsi_db/collections/users/" + persistedId + "?apiKey=pSg2YGo6Qc53wzwsHVfJrEpB7jaPqMa6";
            final HttpClient httpclient = HttpClients.createDefault();
            final HttpGet httpGet = new HttpGet(getUrl);
            httpGet.setHeader("content-type", "application/json");
            HttpResponse response = httpclient.execute(httpGet);
            int httpCode = response.getStatusLine().getStatusCode();
            print("Search http code = " + httpCode);
            assertEquals(200, httpCode);
            HttpEntity entity = response.getEntity();
            InputStream is = entity.getContent();
            Document data = load(is, Document.class);
            assertEquals(data.get("username"), "ahajri");
            is.close();
        } catch (IOException e) {
            fail(e.getMessage());
        }

    }

    @Test
    public void testCDeleteUser() {
        try {
            final String deleteUrl = "https://api.mlab.com/api/1/databases/rocsi_db/collections/users/" + persistedId + "?apiKey=pSg2YGo6Qc53wzwsHVfJrEpB7jaPqMa6";
            final HttpClient httpclient = HttpClients.createDefault();
            final HttpDelete httpDelete = new HttpDelete(deleteUrl);
            httpDelete.setHeader("content-type", "application/json");
            HttpResponse response = httpclient.execute(httpDelete);
            HttpEntity entity = response.getEntity();
            int httpCode = response.getStatusLine().getStatusCode();
            print("Delete http code = " + httpCode);
            InputStream is = entity.getContent();
            Document data = load(is, Document.class);
            assertEquals(data.get("username"), "ahajri");
            is.close();
        } catch (IOException e) {
            fail(e.getMessage());
        }
    }

    private <T> T load(final InputStream inputStream, final Class<T> clazz) {
        try {
            if (inputStream != null) {
                final Gson gson = new Gson();
                final BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));
                return gson.fromJson(reader, clazz);
            }
        } catch (final Exception e) {
        }
        return null;
    }

    private String prettyPrint(Object obj) {
        Gson gson = new GsonBuilder().setPrettyPrinting().create();
        return gson.toJson(obj);
    }
}
