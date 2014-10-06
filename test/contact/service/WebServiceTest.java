package contact.service;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeoutException;

import javax.ws.rs.core.Response.Status;

import main.JettyMain;

import org.eclipse.jetty.client.HttpClient;
import org.eclipse.jetty.client.api.ContentResponse;
import org.eclipse.jetty.client.api.Request;
import org.eclipse.jetty.client.util.StringContentProvider;
import org.eclipse.jetty.http.HttpMethod;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
// No Javadoc
public class WebServiceTest {
	private static String serviceUrl;
	private static HttpClient client;
// No blank lines between methods.
	@BeforeClass
	public static void doFirst( ) throws Exception {
		
		serviceUrl = JettyMain.run();
		client = new HttpClient();
		try {
			client.start();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	} 
	@AfterClass
	public static void doLast( ) throws Exception {
		// stop the Jetty server after the last test
		JettyMain.close();
	}
	



			 /**
			  * Test Success GET.
			  * @throws InterruptedException
			  * @throws ExecutionException
			  * @throws TieoutException
			  */
			 @Test
			 public void testGetPass() throws InterruptedException, ExecutionException, TimeoutException  {
//ERROR: assumes id 1001 exists, but the test suite didn't add it.
				 ContentResponse res = client.GET(serviceUrl+"contacts/1001");
				 assertEquals("Response should be 200 OK", Status.OK.getStatusCode(), res.getStatus());
				 assertTrue("Content exist!", !res.getContentAsString().isEmpty());
			 }
			 
			 /**
			  * Test Fail GET.
			  * @throws InterruptedException
			  * @throws ExecutionException
			  * @throws TimeoutException
			  */
			 @Test
			 public void testGetFail() throws InterruptedException, ExecutionException, TimeoutException {
				 ContentResponse res = client.GET(serviceUrl+"contacts/0");
				 assertEquals("The response should be 404 Not FOUND", Status.NOT_FOUND.getStatusCode(), res.getStatus());
//				 assertTrue("Content does not exist", res.getContentAsString()==null);
			 }
	
			 /**
			  * Test success POST.
			  * @throws InterruptedException
			  * @throws ExecutionException
			  * @throws TimeoutException
			  */
			 @Test
			 public void testPostPass() throws InterruptedException, ExecutionException, TimeoutException {
				 StringContentProvider content = new StringContentProvider("<contact id=\"123\">" +
							"<title>RoboEarth</title>" +
							"<name>Earth Name</name>" +
							"<email>earth@email</email>" +
							"<phoneNumber>0000000000</phoneNumber>"+
							"</contact>");
				 Request request = client.newRequest(serviceUrl+"contacts");
				 request.method(HttpMethod.POST);
				 request.content(content, "application/xml");
				 ContentResponse res = request.send();
				
				 assertEquals("POST complete ,should response 201 Created", Status.CREATED.getStatusCode(), res.getStatus());
				 res = client.GET(serviceUrl+"contacts/123");
// ERROR: create a new contact doesn't have to return request body
				 assertTrue("Content Exist", !res.getContentAsString().isEmpty() );
// You should check that Location: header was returned, then GET it.
			 }
			 
			 /**
			  * Test Fail Post.
			  * @throws InterruptedException
			  * @throws TimeoutException
			  * @throws ExecutionException
			  */
			 @Test
			 public void testPostFail() throws InterruptedException, TimeoutException, ExecutionException {
				 StringContentProvider content = new StringContentProvider("");
				 Request request = client.newRequest(serviceUrl+"contacts");
				 request.method(HttpMethod.POST);
				 request.content(content, "application/xml");
				 ContentResponse res = request.send();
				 
				 assertEquals("This contact is null", Status.BAD_REQUEST.getStatusCode(), res.getStatus());
			 }
	
			 /**
			  * Test success PUT
			  * @throws InterruptedException
			  * @throws TimeoutException
			  * @throws ExecutionException
			  */
			 @Test
			 public void testPutPass() throws InterruptedException, TimeoutException, ExecutionException {
				 StringContentProvider content = new StringContentProvider("<contact id=\"123\">" +
							"<title>UPDATE Title</title>" +
							"<name>UPDATE Name</name>" +
							"<email>update@email</email>" +
							"<phoneNumber>0123456789</phoneNumber>"+
							"</contact>");
				 Request request = client.newRequest(serviceUrl+"contacts/123");
				 request.method(HttpMethod.PUT);
				 request.content(content, "application/xml");
				 ContentResponse res = request.send();
				 
				 assertEquals("PUT Success should response 200 OK", Status.OK.getStatusCode(), res.getStatus());
			 }
			 
			 /**
			  * Test Fail PUT
			  * @throws InterruptedException
			  * @throws TimeoutException
			  * @throws ExecutionException
			  */
			 @Test
			 public void testPutFail() throws InterruptedException, TimeoutException, ExecutionException {
				 StringContentProvider content = new StringContentProvider("<contact id=\"123\">" +
							"<title>UPDATE Title</title>" +
							"<name>UPDATE Name</name>" +
							"<email>update@email</email>" +
							"<phoneNumber>0123456789</phoneNumber>"+
							"</contact>");
				 Request request = client.newRequest(serviceUrl+"contacts/3335");
				 request.method(HttpMethod.PUT);
				 request.content(content, "application/xml");
				 ContentResponse res = request.send();
				 
				 assertEquals("PUT Fail should response 400 BAD REQUEST", Status.BAD_REQUEST.getStatusCode(), res.getStatus());
			 }
			 
			 /**
			  * Test success DELETE
			  * @throws InterruptedException
			  * @throws ExecutionException
			  * @throws TimeoutException
			  */
			 @Test
			 public void testDeletePass() throws InterruptedException, ExecutionException, TimeoutException {
				 Request request = client.newRequest(serviceUrl+"contacts/123");
				 request.method(HttpMethod.DELETE);
				 ContentResponse res = request.send();
				 
				 assertEquals("DELETE success should response 200 OK", Status.OK.getStatusCode(), res.getStatus());
				 res = client.GET(serviceUrl+"contacts/123");
				// assertTrue("Contact deleted", res.getContentAsString().isEmpty());
			 }
			 
			 /**
			  * Test fail DELETE
			  * @throws InterruptedException
			  * @throws TimeoutException
			  * @throws ExecutionException
			  */
			 @Test
			 public void testDeleteFail() throws InterruptedException, TimeoutException, ExecutionException {
				 Request request = client.newRequest(serviceUrl+"contacts/3335");
				 request.method(HttpMethod.DELETE);
				 ContentResponse res = request.send();
				 
				 assertEquals("DELETE success should response 200 OK", Status.OK.getStatusCode(), res.getStatus());
			 }
}