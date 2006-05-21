/*
 * $Id$
 * $URL$
 */

package org.subethamail.entity.dao;

import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import javax.ejb.Stateless;
import javax.mail.internet.InternetAddress;
import javax.persistence.EntityManager;
import javax.persistence.LockModeType;
import javax.persistence.NoResultException;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.subethamail.common.NotFoundException;
import org.subethamail.common.valid.Validator;
import org.subethamail.entity.Attachment;
import org.subethamail.entity.Config;
import org.subethamail.entity.EmailAddress;
import org.subethamail.entity.Mail;
import org.subethamail.entity.MailingList;
import org.subethamail.entity.Person;
import org.subethamail.entity.Role;
import org.subethamail.entity.Subscription;

/**
 * @see DAO
 * 
 * Note:  All named queries are defined on the entities.
 *
 * @author Jeff Schnitzer
 */
@Stateless(name="DAO")
public class DAOBean implements DAO
{
	/** */
	private static Log log = LogFactory.getLog(DAOBean.class);
	
	/** */
	@PersistenceContext private EntityManager em;

	/**
	 * @see DAO#persist(Object)
	 */
	public void persist(Object obj)
	{
		this.em.persist(obj);
	}

	/**
	 * @see DAO#remove(Object)
	 */
	public void remove(Object obj)
	{
		this.em.remove(obj);
	}

	/**
	 * @see DAO#flush()
	 */
	public void flush()
	{
		this.em.flush();
	}

	/**
	 * @see DAO#lock(Object, LockModeType)
	 */
	public void lock(Object obj, LockModeType lockMode)
	{
		this.em.lock(obj, lockMode);
	}

	/**
	 * @see DAO#findConfig(String)
	 */
	public Config findConfig(String id) throws NotFoundException
	{
		Config c = this.em.find(Config.class, id);
		
		if (c == null)
			throw new NotFoundException("No config " + id);
		else
			return c;
	}

	/**
	 * @see DAO#getConfigValue(String)
	 */
	public Object getConfigValue(String id)
	{
		Config c = this.em.find(Config.class, id);
		
		if (c == null)
			return null;
		else
			return c.getValue();
	}

	/**
	 * @see org.subethamail.entity.dao.DAO#getConfig()
	 */
	public List<Config> getConfig()
	{
		List<Config> config = new ArrayList<Config>(Config.ConfigKey.ALL.size());
		for(Config.ConfigKey cfgKey : Config.ConfigKey.ALL)
		{
			try
			{
				if (log.isDebugEnabled())
					log.debug("Getting key: " + cfgKey + " value: " + findConfig(cfgKey.getKey()));
				config.add(findConfig(cfgKey.getKey()));
			}
			catch (NotFoundException e)
			{
				// It's ok, let's create it as an empty value.
				try
				{
					if (log.isDebugEnabled())
						log.debug("New config key: " + cfgKey.getKey());
					Config newConfig = new Config(cfgKey.getKey(), cfgKey.getType().newInstance());
					config.add(newConfig);
				}
				catch (Exception bad)
				{
					log.debug("Could not create missing key.", bad);
				}
			}
		}
		return config;
	}

	/**
	 * @see DAO#findEmailAddress(String)
	 */
	public EmailAddress findEmailAddress(String email) throws NotFoundException
	{
		EmailAddress e = this.getEmailAddress(email);
		
		if (e == null)
			throw new NotFoundException("No email " + email);
		else
			return e;
	}

	/**
	 * @see DAO#getEmailAddress(String)
	 */
	public EmailAddress getEmailAddress(String email)
	{
		if (log.isDebugEnabled())
			log.debug("Getting EmailAddress with id " + email);
		
		// Normalize out any weird casing in the domain part
		email = Validator.normalizeEmail(email);
		
		return this.em.find(EmailAddress.class, email);
	}

	/**
	 * @see DAO#findMail(Long)
	 */
	public Mail findMail(Long id) throws NotFoundException
	{
		if (log.isDebugEnabled())
			log.debug("Finding Mail with id " + id);
		
		Mail m = this.em.find(Mail.class, id);
		
		if (m == null)
			throw new NotFoundException("No mail " + id);
		else
			return m;
	}

	/**
	 * @see DAO#findMailByMessageId(Long, String)
	 */
	public Mail findMailByMessageId(Long listId, String messageId) throws NotFoundException
	{
		if (log.isDebugEnabled())
			log.debug("Finding Mail with Message-ID " + messageId);
		
		Query q = this.em.createNamedQuery("MailByMessageId");
		q.setParameter("listId", listId);
		q.setParameter("messageId", messageId);
		
		try
		{
			return (Mail)q.getSingleResult();
		}
		catch (NoResultException ex)
		{
			throw new NotFoundException(ex);
		}
	}
	
	/**
	 * @see DAO#findMailWantingParent(Long, String)
	 */
	@SuppressWarnings("unchecked")
	public List<Mail> findMailWantingParent(Long listId, String messageId)
	{
		if (log.isDebugEnabled())
			log.debug("Finding mail wanting ancestor with Message-ID " + messageId);
		
		Query q = this.em.createNamedQuery("WantsReferenceToMessageId");
		q.setParameter("listId", listId);
		q.setParameter("messageId", messageId);
		
		return q.getResultList();
	}

	/**
	 * @see DAO#findMailingList(Long)
	 */
	public MailingList findMailingList(Long id) throws NotFoundException
	{
		if (log.isDebugEnabled())
			log.debug("Finding MailingList with id " + id);
		
		MailingList m = this.em.find(MailingList.class, id);
		
		if (m == null)
			throw new NotFoundException("No mailing list " + id);
		else
			return m;
	}

	/**
	 * @see DAO#findMailingList(InternetAddress)
	 */
	public MailingList findMailingList(InternetAddress address) throws NotFoundException
	{
		if (log.isDebugEnabled())
			log.debug("Finding MailingList with email " + address.getAddress());
		
		Query q = this.em.createNamedQuery("MailingListByEmail");
		q.setParameter("email", address.getAddress());
		
		try
		{
			return (MailingList)q.getSingleResult();
		}
		catch (NoResultException ex)
		{
			log.debug("Not found");
			throw new NotFoundException(ex);
		}
	}

	/**
	 * @see DAO#findMailingList(URL)
	 */
	public MailingList findMailingList(URL url) throws NotFoundException
	{
		if (log.isDebugEnabled())
			log.debug("Finding MailingList with url " + url);
		
		Query q = this.em.createNamedQuery("MailingListByUrl");
		q.setParameter("url", url.toString());
		
		try
		{
			return (MailingList)q.getSingleResult();
		}
		catch (NoResultException ex)
		{
			log.debug("Not found");
			throw new NotFoundException(ex);
		}
	}
	
	/**
	 * @see DAO#findMailingLists(String)
	 */
	@SuppressWarnings("unchecked")
	public List<MailingList> findMailingLists(String query)
	{
		return findMailingLists(query, -1, -1);
	}
	
	/**
	 * @see DAO#findMailingLists(String, int, int)
	 */
	@SuppressWarnings("unchecked")
	public List<MailingList> findMailingLists(String query, int skip, int count)
	{
		Query q;
		if (query == null || query.length() == 0)
		{
			q = this.em.createNamedQuery("AllMailingLists");
		}
		else
		{
			if (log.isDebugEnabled())
				log.debug("Finding MailingLists with query: " + query);

			q = this.em.createNamedQuery("SearchMailingLists");
			q.setParameter("name", like(query));
			q.setParameter("email", like(query));
			q.setParameter("url", like(query));
			q.setParameter("description", like(query));
		}
		if (skip >= 0 && count >= 0)
		{
			q.setFirstResult(skip);
			q.setMaxResults(count);
		}
		return q.getResultList();
	}

	/**
	 * @see DAO#findAllLists()
	 */
	@SuppressWarnings("unchecked")
	public List<MailingList> findAllLists()
	{
		if (log.isDebugEnabled())
			log.debug("Finding all mailing lists");
		
		Query q = this.em.createNamedQuery("AllMailingLists");
		
		return q.getResultList();
	}

	/**
	 * @see DAO#countLists()
	 */
	public int countLists()
	{
		if (log.isDebugEnabled())
			log.debug("Counting all mailing lists");

		Query q = this.em.createNamedQuery("CountMailingLists");
		Number n = (Number) q.getSingleResult();
		return n.intValue();
	}

	/**
	 * @see DAO#countLists(String)
	 */
	public int countLists(String query)
	{
		if (log.isDebugEnabled())
			log.debug("Counting mailing lists with query: " + query);

		Query q = this.em.createNamedQuery("CountMailingListsQuery");
		q.setParameter("name", like(query));
		q.setParameter("email", like(query));
		q.setParameter("url", like(query));
		q.setParameter("description", like(query));
		Number n = (Number) q.getSingleResult();
		return n.intValue();
	}

	/**
	 * @see DAO#findPerson(Long)
	 */
	public Person findPerson(Long personId) throws NotFoundException
	{
		if (log.isDebugEnabled())
			log.debug("Finding Person with id " + personId);
		
		Person p = this.em.find(Person.class, personId);
		
		if (p == null)
			throw new NotFoundException("No person " + personId);
		else
			return p;
	}

	/**
	 * @see DAO#findMailByList(Long, int, int)
	 */
	@SuppressWarnings("unchecked")
	public List<Mail> findMailByList(Long listId, int start, int count)
	{
		if (log.isDebugEnabled())
			log.debug("Finding all mail for list " + listId);
		
		Query q = this.em.createNamedQuery("MailByList");
		q.setParameter("listId", listId);
		q.setFirstResult(start);
		q.setMaxResults(count);
		
		return q.getResultList();
	}

	/*
	 * (non-Javadoc)
	 * @see org.subethamail.entity.dao.DAO#findRole(java.lang.Long)
	 */
	public Role findRole(Long roleId) throws NotFoundException
	{
		if (log.isDebugEnabled())
			log.debug("Finding Role with id " + roleId);
		
		Role r = this.em.find(Role.class, roleId);
		
		if (r == null)
			throw new NotFoundException("No role " + roleId);
		else
			return r;
	}

	/*
	 * (non-Javadoc)
	 * @see org.subethamail.entity.dao.DAO#findSubscriptionsByRole(java.lang.Long)
	 */
	@SuppressWarnings("unchecked")
	public List<Subscription> findSubscriptionsByRole(Long roleId)
	{
		if (log.isDebugEnabled())
			log.debug("Finding Subscriptions with role " + roleId);
		
		Query q = this.em.createNamedQuery("SubscriptionsByRoleId");
		q.setParameter("roleId", roleId);
		
		return q.getResultList();
	}

	/*
	 * (non-Javadoc)
	 * @see org.subethamail.entity.dao.DAO#findAttachment(java.lang.Long)
	 */
	public Attachment findAttachment(Long attachmentId) throws NotFoundException
	{
		if (log.isDebugEnabled())
			log.debug("Finding Attachment with id " + attachmentId);
		
		Attachment a = this.em.find(Attachment.class, attachmentId);
		
		if (a == null)
			throw new NotFoundException("No attachment " + attachmentId);
		else
			return a;
	}

	/*
	 * (non-Javadoc)
	 * @see org.subethamail.entity.dao.DAO#findMailHeld(java.lang.Long)
	 */
	@SuppressWarnings("unchecked")
	public List<Mail> findMailHeld(Long listId)
	{
		if (log.isDebugEnabled())
			log.debug("Finding held mail for list " + listId);
		
		Query q = this.em.createNamedQuery("HeldMail");
		q.setParameter("listId", listId);
		
		return q.getResultList();
	}

	/*
	 * (non-Javadoc)
	 * @see org.subethamail.entity.dao.DAO#findSiteAdmins()
	 */
	@SuppressWarnings("unchecked")
	public List<Person> findSiteAdmins()
	{
		log.debug("Finding all site admins.");

		Query q = this.em.createNamedQuery("SiteAdmin");
		return q.getResultList();
	}
	
	/*
	 * (non-Javadoc)
	 * @see org.subethamail.entity.dao.DAO#countSubscribers(java.lang.Long)
	 */
	public int countSubscribers(Long listId)
	{
		if (log.isDebugEnabled())
			log.debug("Counting subscribers of list: " + listId);

		Query q = this.em.createNamedQuery("CountSubscribersOnList");
		q.setParameter("listId", listId);
		Number n = (Number) q.getSingleResult();
		return n.intValue();
	}

	/*
	 * (non-Javadoc)
	 * @see org.subethamail.entity.dao.DAO#countSubscribers(java.lang.Long, java.lang.String)
	 */
	public int countSubscribers(Long listId, String query)
	{
		if (log.isDebugEnabled())
			log.debug("Counting subscribers on list: " + listId + " with query: " + query);

		Query q = this.em.createNamedQuery("CountSubscribersOnListQuery");
		q.setParameter("listId", listId);
		q.setParameter("name", like(query));
		q.setParameter("email", like(query));
		q.setParameter("note", like(query));
		Number n = (Number) q.getSingleResult();
		return n.intValue();
	}

	/*
	 * (non-Javadoc)
	 * @see org.subethamail.entity.dao.DAO#findSubscribers(java.lang.Long, java.lang.String, int, int)
	 */
	@SuppressWarnings("unchecked")
	public List<Subscription> findSubscribers(Long listId, String query, int skip, int count)
		throws NotFoundException
	{
		Query q;
		if (query == null || query.length() == 0)
		{
			MailingList list = this.findMailingList(listId);
			return new ArrayList(list.getSubscriptions());
		}
		else
		{
			q = this.em.createNamedQuery("SubscribersOnListQuery");
			q.setParameter("listId", listId);
			q.setParameter("name", like(query));
			q.setParameter("email", like(query));
			q.setParameter("note", like(query));
		}
		
		if (skip >=0 && count >=0)
		{
			q.setFirstResult(skip);
			q.setMaxResults(count);
		}
		return q.getResultList();
	}

	/*
	 * (non-Javadoc)
	 * @see org.subethamail.entity.dao.DAO#findSubscribers(java.lang.Long, java.lang.String)
	 */
	@SuppressWarnings("unchecked")
	public List<Subscription> findSubscribers(Long listId, String query)
		throws NotFoundException
	{
		return findSubscribers(listId, query, -1, -1);
	}

	private final String like(String query)
	{
		return "%" + query + "%";
	}

	/*
	 * (non-Javadoc)
	 * @see org.subethamail.entity.dao.DAO#findSoftHoldsForPerson(java.lang.Long)
	 */
	@SuppressWarnings("unchecked")
	public List<Mail> findSoftHoldsForPerson(Long personId)
	{
		if (log.isDebugEnabled())
			log.debug("Finding soft mail holds for person " + personId);
		
		Query q = this.em.createNamedQuery("SoftHoldsByPerson");
		q.setParameter("personId", personId);
		
		return q.getResultList();
	}
}
