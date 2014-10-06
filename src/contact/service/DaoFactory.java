package contact.service;

import contact.service.mem.MemDaoFactory;

/**
 * Superclass for MemDaoFactory and JpaDaoFactory
 * @author Termchai Sadsaengchan 5510546042
 *
 */
//ERROR: This class should be abstract. See my example and the DaoFactory document.
public class DaoFactory {
	
	private static DaoFactory factory;
	protected ContactDao daoInstance;
/**
 * return instance of DaoFactory ( MemDaoFactory or JpaDaoFactory)
 * @return
 */
	public static DaoFactory getInstance() {
		if (factory == null) factory = new MemDaoFactory();
		return factory;
	}

	public DaoFactory() {
		super();
	}

	public ContactDao getContactDao() {
		return daoInstance;
	}

//Wrong formatting.
/**
 * handle when shutdown webservice
 * keep Database for next open 
 */
	public void shutdown() { 
		
	}

}