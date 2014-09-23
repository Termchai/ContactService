package contact.service.mem;

import java.util.List;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;
import contact.entity.Contact;
/**
 * Contact XML Form
 * @author Termchai Sadsaengchan 5510546042
 *
 */

@XmlRootElement
@XmlAccessorType(XmlAccessType.FIELD)
public class ContactList {
	private List<Contact> list;
	public ContactList()
	{
		
	}

	public List<Contact> getList() {
		return list;
	}

	public void setList(List<Contact> list) {
		this.list = list;
	}
	
}
