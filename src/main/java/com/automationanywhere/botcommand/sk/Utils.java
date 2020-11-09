package com.automationanywhere.botcommand.sk;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URL;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.UUID;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

import org.apache.http.HttpEntity;
import org.apache.http.StatusLine;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.mime.HttpMultipartMode;
import org.apache.http.entity.mime.MultipartEntityBuilder;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.json.JSONArray;
import org.json.JSONObject;

import com.automationanywhere.botcore.api.dto.Value;

public class Utils {
	
	
    private static final String LINE_FEED = "\r\n";
    private static PrintWriter writer ;
	private static final Logger logger = LogManager.getLogger(Utils.class);
	
	public static HashMap<String,String> getLearningInstances(IQBotConnection connection) throws Exception {
		HashMap<String,String> lis = new HashMap<String, String>();
		
		URL url = new URL(connection.url+"/IQBot/api/projects");
	

		String result = GETRequest(url,connection.token);
		if (!result.contains("GET NOT WORKED")) {
			
			JSONObject json = new JSONObject(result);
			if (json.getBoolean("success")) {
				JSONArray data = (JSONArray)json.get("data");
				data.forEach(item -> {
					JSONObject jsonobj = (JSONObject) item;
		    	    String id = jsonobj.getString("id");
		    	    String name = jsonobj.getString("name");
		    	    lis.put(name, id);
				});
				
			}
			
		}
		
		return lis;
	}

	
	public static HashMap<String,Integer> getLearningInstanceDetails(IQBotConnection connection,String li_id) throws Exception {
	
		HashMap<String,Integer> details = new HashMap<String,Integer>();
		
		URL url = new URL(connection.url+"/IQBot/api/projects/"+li_id+"/detail-summary");

		String result = GETRequest(url,connection.token);
		if (!result.contains("GET NOT WORKED")) {
			
			JSONObject json = new JSONObject(result);
			if (json.getBoolean("success")) {
				JSONObject data = (JSONObject)json.get("data");
				
				String[] keys = JSONObject.getNames(data);
				
				for (int i = 0; i < keys.length; i++) {
					  Integer value= data.getInt(keys[i]);
			    	    details.put(keys[i], value);
				}				
			}
			
		}
		
		return details;
	}

	
	
	
	
	
	
	public static Integer getValidationsinQueue(IQBotConnection connection,String li_id) throws Exception {
		Integer validations = 0 ;
		
		URL url = new URL(connection.url+"/IQBot/api/validator/"+li_id);

		String result = GETRequest(url,connection.token);
		if (!result.contains("GET NOT WORKED")) {
			JSONObject json = new JSONObject(result);
			if (json.getBoolean("success"))  {
				JSONObject data = (JSONObject)json.get("data");
				validations = data.getInt("remainingDocumentReviewCount");
				
			}
			
		}
		
		return validations;
	}
	
	
	public static Integer getNofOfOutputFiles(IQBotConnection connection,String li_id, String doctype) throws Exception {
		Integer noofFiles = 0 ;
		
		URL url = new URL(connection.url+"/IQBot/gateway/learning-instances/"+li_id+"/files/list?docStatus="+doctype);

		String result = GETRequest(url,connection.token);
		if (!result.contains("GET NOT WORKED")) {
			JSONArray json = new JSONArray(result);
			noofFiles = json.length();
		}
		
		return noofFiles;
	}	
	
	public static List<String> getOutputFiles(IQBotConnection connection,String li_id, String doctype, Boolean getURL) throws Exception {
		List<String> documents = new ArrayList<String>();
		
		URL url = new URL(connection.url+"/IQBot/gateway/learning-instances/"+li_id+"/files/list?docStatus="+doctype);

		String result = GETRequest(url,connection.token);
		if (!result.contains("GET NOT WORKED")) {
			JSONArray json = new JSONArray(result);
			json.forEach(doc -> {
				if (getURL && doctype.equals("VALIDATION")) {
					String file = doc.toString();
					String file_id = file.split("_")[0];
					String validateurl = connection.url+"/IQBot/learning-instances/"+li_id+"/validator?fileid="+file_id;
					documents.add(validateurl);
				}
				else {
					documents.add(doc.toString());
				}
			});
		}
		return documents;
	}	
	
	
	public static Map<String,String> getArchives(IQBotConnection connection) throws Exception {
		HashMap<String,String> archives = new HashMap<String,String>();
		
		URL url = new URL(connection.url+"/IQBot/api/projects/archivedetails");
	
		String result = GETRequest(url,connection.token);
		if (!result.contains("GET NOT WORKED")) {
			JSONObject json =  new JSONObject(result);
			JSONArray jsonarchive = json.getJSONArray("data");
			jsonarchive.forEach(archive -> {
				JSONObject jsonobj = (JSONObject) archive;
				archives.put(jsonobj.getString("archiveName"),jsonobj.getString("archiveId"));
			});

		}
		return archives;
	}


	public static String putArchiveFile(IQBotConnection connection, String filename) throws Exception {

		URI url = new URI(connection.url+"/IQBot/gateway/backups");
		CloseableHttpClient client0 = HttpClients.createDefault();
		HttpPost uploadFile = new HttpPost(url);

		MultipartEntityBuilder builder = MultipartEntityBuilder.create();
		File f = new File(filename);
		builder.addBinaryBody("file", new FileInputStream(f), ContentType.MULTIPART_FORM_DATA, f.getName());
	    HttpEntity multipart = builder.build();
	    uploadFile.setEntity(multipart);
	    uploadFile.setHeader("x-authorization", connection.token);
	    
	     CloseableHttpResponse response = client0.execute(uploadFile);
	     int responseCode = response.getStatusLine().getStatusCode();
	     
	     if (responseCode == HttpURLConnection.HTTP_OK) { //success
		        BufferedReader in = new BufferedReader(new InputStreamReader(

		        		response.getEntity().getContent(),StandardCharsets.UTF_8.name()));

		        String inputLine;

		        StringBuffer responsebuf = new StringBuffer();

		        while ((inputLine = in .readLine()) != null) {

		            responsebuf.append(inputLine);

		        } 
		        in.close();
		        JSONObject jsonresult = new JSONObject(responsebuf.toString());
		        return jsonresult.getString("id");

		    } else {
		    	
		        return "POST NOT WORKED"+Integer.valueOf(responseCode).toString();

		    }
	
	}
	
	
	public static String putDocument(IQBotConnection connection, String li_id, String filename) throws Exception {

		URI url = new URI(connection.url+"/IQBot/gateway/organizations/1/projects/"+li_id+"/files/upload/1");
		CloseableHttpClient client0 = HttpClients.createDefault();
		HttpPost uploadFile = new HttpPost(url);

		MultipartEntityBuilder builder = MultipartEntityBuilder.create();
		builder.setMode(HttpMultipartMode.RFC6532);
		Path filepath = Paths.get(filename, new String[0]);
		File f = filepath.toFile();
		builder.addBinaryBody("fileName", new FileInputStream(f), ContentType.MULTIPART_FORM_DATA, filepath.getFileName().toString());
	    HttpEntity multipart = builder.build();
	    uploadFile.setEntity(multipart);
	    uploadFile.setHeader("x-authorization", connection.token);
	    
	     CloseableHttpResponse response = client0.execute(uploadFile);
	     int responseCode = response.getStatusLine().getStatusCode();
	     
	     if (responseCode == HttpURLConnection.HTTP_OK) { //success
		        BufferedReader in = new BufferedReader(new InputStreamReader(

		        		response.getEntity().getContent(),StandardCharsets.UTF_8.name()));

		        String inputLine;

		        StringBuffer responsebuf = new StringBuffer();

		        while ((inputLine = in .readLine()) != null) {

		            responsebuf.append(inputLine);

		        } 
		        in.close();
		        return "SUCCESS";

		    } else {
		    	
		        return "POST NOT WORKED"+Integer.valueOf(responseCode).toString();

		    }
	
	}
	
	
	public static String getArchiveFile(IQBotConnection connection,String archiveid,String filepath) throws Exception {
		HashMap<String,String> archives = new HashMap<String,String>();
		
		URL url = new URL(connection.url+"/IQBot/api/backups/"+archiveid+"/download");
	
		String status = DownloadUtility.downloadFile(url,connection.token,filepath,"export.iqba");
	
		return status;
	}
	
	
	public static String deleteLIFile(IQBotConnection connection,String li_id,String fileid) throws Exception  {
	
		URL url = new URL(connection.url+"/IQBot/gateway/learning-instances/"+li_id+"/files/"+fileid);
	
		String result = DELETERequest(url,connection.token);
		if (!result.contains("DELETE NOT WORKED")) {
			return "SUCCESS";

		}
		else
		{
			return result;
		}

	}
	
	
	public static String downloadDocuments(IQBotConnection connection,String li_id, String doctype, String filepath) throws Exception {

		Timestamp timestamp = new Timestamp(System.currentTimeMillis());
		SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddHHmmss");
		String status = "";
		
		URL url = new URL(connection.url+"/IQBot/gateway/learning-instances/"+li_id+"/files/archive?docStatus="+doctype);
		String filename = li_id+"_"+doctype+"_"+sdf.format(timestamp)+".zip";
	
		String savedfilename = DownloadUtility.downloadFile(url,connection.token,filepath,filename);
		
		if (!savedfilename.contains("ERROR")) {
			extractZipFile(filepath,savedfilename);
			status = "SUCCESS";
		}
		else
		{
			status = savedfilename;
		}
		return status;
	}

	
	public static String  POSTRequest(URL posturl, String token, JSONObject body) throws IOException  {

		int responseCode = 0;
	    HttpURLConnection postConnection = null;
		try {

	        if (token != null)
	        {
		        	postConnection = (HttpURLConnection) posturl.openConnection();
		    	    postConnection.setRequestProperty("x-authorization", token);
				    postConnection.setRequestMethod("POST");
			    	postConnection.setRequestProperty("Content-Type", "application/json;charset=utf-8");
			    	OutputStream os = postConnection.getOutputStream();
			    	os.write(body.toString().getBytes());
			    	os.flush();
			    	os.close();
			    	responseCode = postConnection.getResponseCode();
			}
 
		} catch (IOException e) {
			 logger.info("Ex : "+e.toString());
  
		}

	    if (responseCode == HttpURLConnection.HTTP_OK) { //success
	    	
	 

	        BufferedReader in = new BufferedReader(new InputStreamReader(

	            postConnection.getInputStream(),StandardCharsets.UTF_8.name()));

	        String inputLine;

	        StringBuffer response = new StringBuffer();

	        while ((inputLine = in .readLine()) != null) {

	            response.append(inputLine);

	        } in .close();

	        
	        return  response.toString() ;

	    } else {
	    	
	        return "POST NOT WORKED"+Integer.valueOf(responseCode).toString();

	    }

	}
	
    
 

	
	public static String  GETRequest(URL geturl, String token) throws IOException  {

		int responseCode = 0; 
	    HttpURLConnection getConnection = null;
		try {
			getConnection = (HttpURLConnection) geturl.openConnection();
		    getConnection.setRequestMethod("GET");
	        if (token != null)
	        {
	    	    getConnection.setRequestProperty("x-authorization", token);
	        }


		    getConnection.setRequestProperty("Content-Type", "application/json;charset=utf-8");
	

		    responseCode = getConnection.getResponseCode();
		    
		} catch (IOException e) {
		    logger.info("Ex : "+e.toString());
  
		}

		
	    if (responseCode == HttpURLConnection.HTTP_OK) { //success
	    	

	        BufferedReader in = new BufferedReader(new InputStreamReader(

	            getConnection.getInputStream(),StandardCharsets.UTF_8.name()));

	        String inputLine;

	        StringBuffer response = new StringBuffer();

	        while ((inputLine = in .readLine()) != null) {

	            response.append(inputLine);

	        } 
	        in .close();
	        return  response.toString() ;

	    } else {
	    	
	        return "GET NOT WORKED"+Integer.valueOf(responseCode).toString();

	    }

	}
	
	
	private static String  DELETERequest(URL deleteurl, String token) throws IOException  {

		int responseCode = 0;
	    HttpURLConnection deleteConnection = null;
		try {
			deleteConnection = (HttpURLConnection) deleteurl.openConnection();
			deleteConnection.setRequestMethod("DELETE");
	        if (token != null)
	        {
	        	deleteConnection.setRequestProperty("x-authorization", token);
	        }


	        deleteConnection.setDoOutput(true);

		    responseCode = deleteConnection.getResponseCode();
		    
		} catch (IOException e) {

	       System.out.println("HTTP DELETE Exception" +e.getMessage());
		}

	    if (responseCode == HttpURLConnection.HTTP_OK) { //success

	        BufferedReader in = new BufferedReader(new InputStreamReader(

	        		deleteConnection.getInputStream(),StandardCharsets.UTF_8.name()));

	        String inputLine;

	        StringBuffer response = new StringBuffer();

	        while ((inputLine = in .readLine()) != null) {

	            response.append(inputLine);

	        } in .close();

	        
	        return  response.toString() ;

	    } else {
          	System.out.println("API call '"+deleteurl.toString()+"' Status Code:'"+ new Integer(responseCode).toString()+"' Message: "+ deleteConnection.getResponseMessage());
			
	
	        return "DELETE NOT WORKED "+ Integer.valueOf(responseCode).toString();

	    }

	}
	
	
	  private static String extractZipFile(String destDir, String zipFile) throws IOException {

		    Path filePath = Paths.get(destDir, zipFile);
		    File fileZip = filePath.toFile();
		    byte[] buffer = new byte[1024];
		    
		    ZipInputStream zis = new ZipInputStream(new FileInputStream(fileZip)); 
		    try { ZipEntry zipEntry = zis.getNextEntry();
		      while (zipEntry != null) {
		        File newFile = newFile(destDir, zipEntry);
		        FileOutputStream fos = new FileOutputStream(newFile); 
		        try { int len;
		          while ((len = zis.read(buffer)) > 0) {
		            fos.write(buffer, 0, len);
		          }
		          fos.close(); } catch (Throwable throwable) { try { fos.close(); } catch (Throwable throwable1) { throwable.addSuppressed(throwable1); }  throw throwable; }
		         zipEntry = zis.getNextEntry();
		      } 
		      zis.close(); } catch (Throwable throwable) { try { zis.close(); } catch (Throwable throwable1) { throwable.addSuppressed(throwable1); }  throw throwable; }
		     fileZip.delete();
		    return "SUCCESS";
		  }



	  
	  public static File newFile(String destinationDir, ZipEntry zipEntry) throws IOException {
	    File destFile = new File(Paths.get(destinationDir).toFile(), zipEntry.getName());
	    
	    return destFile;
	  }





}
