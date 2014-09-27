package contact.resource;


import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;

import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.GenericEntity;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;
import javax.ws.rs.core.UriInfo;
import javax.xml.bind.JAXBElement;

import contact.entity.Contact;
import contact.service.ContactDao;
import contact.service.mem.MemDaoFactory;

@Path("/contacts")
/**
 * handle path "contacts" from JettyMain
 * @author Termchai Sadsaengchan 5510546042
 *
 */
public class ContactResource {
	/* Dao (database by arraylist! */
	ContactDao dao = MemDaoFactory.getInstance().getContactDao();
	
	@Context
	UriInfo uriInfo;
	
	/**
	 * return contact by id in dao
	 * @param id
	 * @return contact by id
	 */
	@GET
	@Path("{id}")
	public Response getContactById(@PathParam("id") String id)
	{
		Contact c = dao.find(Long.parseLong(id));
		if (c==null)
			return Response.status(Status.NOT_FOUND).build();
		return Response.ok(c).build();
	}
	
	/**
	 * return contact
	 * have 2 case
	 * 1. not query - return all contacts
	 * 2. query string - return contact that substring of query string
	 * @param qstr to find contact that title contain qstr or null
	 * @return
	 */
	@GET
	@Produces (MediaType.APPLICATION_XML)
	public Response getContact( @QueryParam("q") String qstr )
	{
		GenericEntity<List<Contact>> entity;
		if (qstr == null)
			entity = new GenericEntity<List<Contact>>(dao.findAll()) {};
		else
		{
			List<Contact> daoList = dao.findAll();
			List<Contact> list = new ArrayList<Contact>();
			
			for (Contact c : daoList)
			{
				if (c.getTitle().toLowerCase().contains(qstr.toLowerCase()))
					list.add(c);
			}
			
			if (list.size() == 0)
				return Response.status(Status.NOT_FOUND).build();
			
			entity = new GenericEntity<List<Contact>>(list) {};
		}

		return Response.ok(entity).build();
	}
	
	/**
	 * create new contact in dao
	 * @param element contact from xml body
	 * @param uriInfo
	 * @return hyperlink that contact
	 * @throws URISyntaxException
	 */
	@POST
	@Consumes( { MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON } )
	public Response post( JAXBElement<Contact> element, @Context UriInfo uriInfo ) throws URISyntaxException 
	{
		Contact contact = element.getValue();
		if (dao.find(contact.getId()) != null)
			return Response.status(Status.CONFLICT).build();
		dao.save( contact );
		return Response.created(new URI("http://localhost:8080/contacts/" + contact.getId())).build();
	}
	
	
	/**
	 * update data in contact by id
	 * @param id id of contach
	 * @param element body xml that want to update contact
	 * @return
	 * @throws URISyntaxException
	 */
	@PUT
	@Consumes( { MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON } )
	@Path("{id}")
	public Response put(@PathParam("id") String id, JAXBElement<Contact> element) throws URISyntaxException
	{
		if (dao.find(Long.parseLong(id)) == null) return Response.status(Status.BAD_REQUEST).build();
		Contact contact = element.getValue();
		contact.setId(Long.parseLong(id));
		dao.update(contact);
		return Response.ok().build();
		
	}
	
	/**
	 * delete contact by id
	 * @param id
	 */
	@DELETE
	@Path("{id}")
	public Response delete(@PathParam("id") Long id)
	{
		dao.delete(id);
		return Response.ok().build();
	}

}
