package com.ahajri.heaven.calendar.mongo.cloud;

import java.util.ArrayList;
import java.util.List;

import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.message.BasicNameValuePair;
import org.bson.Document;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.ahajri.heaven.calendar.exception.BusinessException;

@Service
public class CloudApiMongoService {
	
	@Value("${mlab.api.key}")
	protected String apiKey;

	@Value("${mlab.db.user}")
	protected String dbUser;
	
	@Value("${mlab.db.password}")
	protected String dbPwd;
	
	@Value("${mlab.db.name}")
	protected String dbName;

	@Value("${mlab.db.port}")
	protected String dbPort;
	
	@Value("${mlab.db.host}")
	protected String dbHost;
	
	/**
	 *  
	 * @param collectionName
	 * @param document
	 * @throws BusinessException
	 */
	public HttpResponse insertOne(String collectionName, Document document) throws BusinessException {
		try {
			String postUrl = "https://api.mlab.com/api/1/databases/"+dbName+"/collections/"+collectionName+"?apiKey="+apiKey;
			HttpClient httpclient = HttpClients.createDefault();
			HttpPost httppost = new HttpPost(postUrl);

			// Request parameters and other properties.
			List<NameValuePair> params = new ArrayList<NameValuePair>(2);
			document.entrySet().forEach(entry -> {
				params.add(new BasicNameValuePair(entry.getKey(), (String) entry.getValue()));
			});
			
			
			httppost.setEntity(new UrlEncodedFormEntity(params, "UTF-8"));

			HttpResponse response = httpclient.execute(httppost);
			return response;
			
			
		} catch (Exception e) {
			throw new BusinessException(e, "Could not insert document");
		}
	}
}