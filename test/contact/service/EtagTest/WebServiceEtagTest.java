package contact.service.EtagTest;

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
import org.eclipse.jetty.http.HttpHeader;
import org.eclipse.jetty.http.HttpMethod;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
/**
 * test webservice with Etag
 * @author Termchai Sadsaengchan 5510546042
 *
 */
public class WebServiceEtagTest {
	private static String serviceUrl;
	private static HttpClient client;
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
			 public void testGetPassEtag() throws InterruptedException, ExecutionException, TimeoutException  {
				 Request req = client.newRequest(serviceUrl + "contacts/1").header(HttpHeader.IF_NONE_MATCH, null).method(HttpMethod.GET);
				 ContentResponse cr = req.send();
				 assertEquals("Response should be 200 OK", Status.OK.getStatusCode(), cr.getStatus());
				 assertTrue("Content exist!", !cr.getContentAsString().isEmpty());
				 assertTrue("Have Etag.",!cr.getHeaders().get(HttpHeader.ETAG).isEmpty());
			 }
	
			 /**
			  * Test success POST.
			  * @throws InterruptedException
			  * @throws ExecutionException
			  * @throws TimeoutException
			  */
			 @Test
			 public void testPostPassEtag() throws InterruptedException, ExecutionException, TimeoutException {
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
				 assertTrue("Content Exist", !res.getContentAsString().isEmpty() );
				 assertTrue("Have Etag.", res.getHeaders().get(HttpHeader.ETAG )!= null);
			 }

	
			 /**
			  * Test success PUT
			  * @throws InterruptedException
			  * @throws TimeoutException
			  * @throws ExecutionException
			  */
			 @Test
			 public void testPutPassEtag() throws InterruptedException, TimeoutException, ExecutionException {
				 Request req = client.newRequest(serviceUrl + "contacts/1001").method(HttpMethod.GET);
				 ContentResponse cr = req.send();
				 String etag = cr.getHeaders().get(HttpHeader.ETAG);
				 
				 StringContentProvider content = new StringContentProvider("<contact id=\"123\">" +
							"<title>UPDATE Title</title>" +
							"<name>UPDATE Name</name>" +
							"<email>update@email</email>" +
							"<phoneNumber>0123456789</phoneNumber>"+
							"</contact>");
				 Request request = client.newRequest(serviceUrl+"contacts/1001");
				 request.method(HttpMethod.PUT);
				 request.header(HttpHeader.IF_MATCH, etag);
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
			 public void testPutFailEtag() throws InterruptedException, TimeoutException, ExecutionException {
				 Request req = client.newRequest(serviceUrl + "contacts/1001").method(HttpMethod.GET);
				 ContentResponse cr = req.send();
				 String etag = cr.getHeaders().get(HttpHeader.ETAG)+"1";
				 
				 StringContentProvider content = new StringContentProvider("<contact id=\"123\">" +
							"<title>UPDATE Title</title>" +
							"<name>UPDATE Name</name>" +
							"<email>update@email</email>" +
							"<phoneNumber>0123456789</phoneNumber>"+
							"</contact>");
				 Request request = client.newRequest(serviceUrl+"contacts/1001");
				 request.method(HttpMethod.PUT);
				 request.header(HttpHeader.IF_MATCH, etag);
				 request.content(content, "application/xml");
				 ContentResponse res = request.send();
				 
				 assertEquals("PUT Fail should respond Precondition failed", Status.PRECONDITION_FAILED.getStatusCode(), res.getStatus());
			 }
			 
			 /**
			  * Test success DELETE
			  * @throws InterruptedException
			  * @throws ExecutionException
			  * @throws TimeoutException
			  */
			 @Test
			 public void testDeletePassEtag() throws InterruptedException, ExecutionException, TimeoutException {
				 Request req = client.newRequest(serviceUrl + "contacts/123").method(HttpMethod.GET);
				 ContentResponse cr = req.send();
				 assertTrue("Have Etag.", cr.getHeaders().get(HttpHeader.ETAG)!=null);
				 String etag = cr.getHeaders().get(HttpHeader.ETAG);
				 
				 Request request = client.newRequest(serviceUrl+"contacts/123");
				 request.method(HttpMethod.DELETE);
				 ContentResponse res = request.header(HttpHeader.IF_MATCH, etag).send();
				 
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
			 public void testDeleteFailEtag() throws InterruptedException, TimeoutException, ExecutionException {
				 Request req = client.newRequest(serviceUrl + "contacts/123").method(HttpMethod.GET);
				 ContentResponse cr = req.send();
				 assertTrue("Have Etag.", cr.getHeaders().get(HttpHeader.ETAG)!=null);
				 String etag = cr.getHeaders().get(HttpHeader.ETAG)+"1";
				 
				 Request request = client.newRequest(serviceUrl+"contacts/123");
				 request.method(HttpMethod.DELETE).header(HttpHeader.IF_MATCH, etag);
				 ContentResponse res = request.send();
				 
				 assertEquals("DELETE Fail should respond Precondition failed", Status.PRECONDITION_FAILED.getStatusCode(), res.getStatus());
			 }
}