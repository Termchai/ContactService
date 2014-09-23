package contact.service.mem;

import java.io.File;
import java.util.List;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;

import contact.entity.Contact;
import contact.service.ContactDao;
import contact.service.DaoFactory;

/**
 * Manage instances of Data Access Objects (DAO) used in the app.
 * This enables you to change the implementation of the actual ContactDao
 * without changing the rest of your application.
 * 
 * @author jim
 */
public class MemDaoFactory extends DaoFactory {
private ContactDao daoInstance;
	
	public MemDaoFactory() {
		daoInstance = new MemContactDao();
		try
		{
			JAXBContext ctx = JAXBContext.newInstance( ContactList.class );
			Unmarshaller unmarshaller = ctx.createUnmarshaller();
			File file = new File("D:\\TestMarshall.xml");
			ContactList cl = (ContactList) unmarshaller.unmarshal(file);
			for (Contact c : cl.getList())
				daoInstance.save(c);
		} 	catch (Exception e)
		{
			e.printStackTrace();;
		}
	}
	
	@Override
	public ContactDao getContactDao() {
		return daoInstance;
	}
	
	@Override
	public void shutdown() {
		//TODO here's your chance to show your skill!
		// Use JAXB to write all your contacts to a file on disk.
		// Then recreate them the next time a MemFactoryDao and ContactDao are created.
		try
		{
			JAXBContext ctx = JAXBContext.newInstance( ContactList.class );
			
			Marshaller marshaller = ctx.createMarshaller();
			List<Contact> list = getContactDao().findAll();
			ContactList cl = new ContactList();
			cl.setList(list);
			marshaller.marshal(cl, new File("D:\\TestMarshall.xml"));

		} catch (JAXBException e) 
		{
			e.printStackTrace();;
		}
		
		
	}
}
