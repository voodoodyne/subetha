package org.subethamail.core.admin;

import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.concurrent.BlockingQueue;
import java.util.logging.Level;

import javax.annotation.security.PermitAll;
import javax.annotation.security.RolesAllowed;
import javax.ejb.Stateless;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;
import javax.inject.Inject;
import javax.mail.internet.InternetAddress;

import lombok.extern.java.Log;

import org.hibernate.CacheMode;
import org.hibernate.FlushMode;
import org.hibernate.ScrollMode;
import org.hibernate.ScrollableResults;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.hibernate.ejb.EntityManagerImpl;
import org.hibernate.search.FullTextSession;
import org.hibernate.search.Search;
import org.subethamail.common.NotFoundException;
import org.subethamail.core.acct.i.AuthSubscribeResult;
import org.subethamail.core.acct.i.PersonData;
import org.subethamail.core.acct.i.SubscribeResult;
import org.subethamail.core.admin.i.Admin;
import org.subethamail.core.admin.i.DuplicateListDataException;
import org.subethamail.core.admin.i.InvalidListDataException;
import org.subethamail.core.admin.i.SiteStatus;
import org.subethamail.core.lists.i.ListData;
import org.subethamail.core.lists.i.ListDataPlus;
import org.subethamail.core.post.PostOffice;
import org.subethamail.core.queue.InjectQueue;
import org.subethamail.core.queue.InjectedQueueItem;
import org.subethamail.core.smtp.SMTPService;
import org.subethamail.core.util.OwnerAddress;
import org.subethamail.core.util.PersonalBean;
import org.subethamail.core.util.Transmute;
import org.subethamail.core.util.VERPAddress;
import org.subethamail.entity.EmailAddress;
import org.subethamail.entity.Mail;
import org.subethamail.entity.MailingList;
import org.subethamail.entity.Person;
import org.subethamail.entity.Subscription;
import org.subethamail.entity.SubscriptionHold;
import org.subethamail.entity.i.Permission;
import org.subethamail.entity.i.PermissionException;

import com.caucho.remote.HessianService;

/**
 * Implementation of the Admin interface.
 *
 * @author Jeff Schnitzer
 */
@Stateless(name="Admin")
@RolesAllowed(Person.ROLE_ADMIN)
@TransactionAttribute(TransactionAttributeType.REQUIRED)
@HessianService(urlPattern="/api/Admin")
@Log
public class AdminBean extends PersonalBean implements Admin
{
	/**
	 * The set of characters from which randomly generated
	 * passwords will be obtained.
	 */
	protected static final String PASSWORD_GEN_CHARS =
		"abcdefghijklmnopqrstuvwxyz" +
		"ABCDEFGHIJKLMNOPQRSTUVWXYZ" +
		"0123456789";

	/**
	 * The length of randomly generated passwords.
	 */
	protected static final int PASSWORD_GEN_LENGTH = 6;

	/** */
	@Inject PostOffice postOffice;

	/** Unfortunately Resin CDI trips on the generic */
	//@Inject @InjectQueue BlockingQueue<InjectedQueueItem> inboundQueue;
	@SuppressWarnings("rawtypes")
	@Inject @InjectQueue BlockingQueue inboundQueue;

	/** Needed to get the fallback host */
	@Inject SMTPService smtpService;

	@Inject SiteSettings settings;
	
	/**
	 * For generating random passwords.
	 */
	protected Random randomizer = new Random();

	/**
	 * @see Admin#log(String)
	 */
	public void log(String msg)
	{
	    log.log(Level.INFO,"CLIENT:  {0}", msg);
	}

	/* (non-Javadoc)
	 * @see org.subethamail.core.admin.i.Admin#createMailingList(javax.mail.internet.InternetAddress, java.net.URL, java.lang.String, javax.mail.internet.InternetAddress[])
	 */
	public Long createMailingList(InternetAddress address, URL url, String description, InternetAddress[] initialOwners) throws DuplicateListDataException, InvalidListDataException
	{
		this.checkListAddresses(address, url);

		// Then create the mailing list and attach the owners.
		MailingList list = new MailingList(address.getAddress(), address.getPersonal(), url.toString(), description);
		this.em.persist(list);
		// TODO:  remove this code when http://opensource.atlassian.com/projects/hibernate/browse/HHH-1654
		// is fixed.  This should be performed within the constructor of MailingList.
		list.setDefaultRole(list.getRoles().iterator().next());
		list.setAnonymousRole(list.getRoles().iterator().next());

		for (InternetAddress ownerAddress: initialOwners)
		{
			EmailAddress ea = this.establishEmailAddress(ownerAddress, null);
			Subscription sub = new Subscription(ea.getPerson(), list, ea, list.getOwnerRole());

			this.em.persist(sub);

			list.getSubscriptions().add(sub);
			ea.getPerson().addSubscription(sub);

			this.postOffice.sendOwnerNewMailingList(list, ea);
		}

		return list.getId();
	}

	/* (non-Javadoc)
	 * @see org.subethamail.core.admin.i.Admin#establishPerson(javax.mail.internet.InternetAddress, java.lang.String)
	 */
	public Long establishPerson(InternetAddress address, String password)
	{
		return this.establishEmailAddress(address, password).getPerson().getId();
	}

	/**
	 * Common method that does the work.
	 */
	protected EmailAddress establishEmailAddress(InternetAddress address, String password)
	{
		try
		{
			return this.em.getEmailAddress(address.getAddress());
		}
		catch (NotFoundException ex)
		{
			// Nobody with that name, lets create

			if (password == null)
				password = this.generateRandomPassword();

			String personal = address.getPersonal();
			if (personal == null)
				personal = "";

			Person p = new Person(password, personal);
			EmailAddress e = new EmailAddress(p, address.getAddress());
			p.addEmailAddress(e);

			this.em.persist(p);
			this.em.persist(e);

			return e;
		}
	}

	/* (non-Javadoc)
	 * @see org.subethamail.core.admin.i.Admin#subscribeEmail(java.lang.Long, javax.mail.internet.InternetAddress, boolean, boolean)
	 */
	public AuthSubscribeResult subscribeEmail(Long listId, InternetAddress address, boolean ignoreHold, boolean silent) throws NotFoundException
	{
		EmailAddress addy = this.establishEmailAddress(address, null);

		SubscribeResult result = this.subscribe(listId, addy.getPerson(), addy, ignoreHold, silent);

		return new AuthSubscribeResult(
				addy.getPerson().getId(),
				addy.getId(),
				addy.getPerson().getPassword(),
				addy.getPerson().getRoles(),
				result,
				listId);
	}

	/* (non-Javadoc)
	 * @see org.subethamail.core.admin.i.Admin#subscribe(java.lang.Long, java.lang.Long, java.lang.String, boolean)
	 */
	public SubscribeResult subscribe(Long listId, Long personId, String email, boolean ignoreHold) throws NotFoundException
	{
		Person who = this.em.get(Person.class, personId);

		if (email == null)
		{
			// Subscribing with (or changing to) disabled delivery
			return this.subscribe(listId, who, null, ignoreHold, false);
		}
		else
		{
			EmailAddress addy = who.getEmailAddress(email);

			if (addy == null)
				throw new IllegalStateException("Must be one of person's email addresses");

			return this.subscribe(listId, who, addy, ignoreHold, false);
		}
	}

	/**
	 * Subscribes someone to a mailing list, or changes the delivery address
	 * of an existing subscriber.
	 * @param deliverTo can be null to disable delivery
	 * @param ignoreHold will subscribe even if a hold is requested
	 * @param silent if true will not send a welcome message to new subscribers
	 */
	protected SubscribeResult subscribe(Long listId, Person who, EmailAddress deliverTo, boolean ignoreHold, boolean silent) throws NotFoundException
	{
		MailingList list = this.em.get(MailingList.class, listId);

		Subscription sub = who.getSubscription(listId);
		if (sub != null)
		{
			// If we're already subscribed, maybe we want to change the
			// delivery address.
			sub.setDeliverTo(deliverTo);

			return SubscribeResult.OK;
		}
		else
		{
			if (!ignoreHold && list.isSubscriptionHeld())
			{
				// Maybe already held, if so, replace it; email address might be new
				SubscriptionHold hold = who.getHeldSubscriptions().get(list.getId());
				if (hold != null)
				{
					who.getHeldSubscriptions().remove(list.getId());
					this.em.remove(hold);
				}

				hold = new SubscriptionHold(who, list, deliverTo);
				this.em.persist(hold);

				// Send mail to anyone that can approve
				for (Subscription maybeModerator: list.getSubscriptions())
					if (maybeModerator.getRole().getPermissions().contains(Permission.APPROVE_SUBSCRIPTIONS)
							&& (maybeModerator.getDeliverTo() != null))
						this.postOffice.sendModeratorSubscriptionHeldNotice(maybeModerator.getDeliverTo(), hold);

				return SubscribeResult.HELD;
			}
			else
			{
				sub = new Subscription(who, list, deliverTo, list.getDefaultRole());

				this.em.persist(sub);

				who.addSubscription(sub);
				list.getSubscriptions().add(sub);

				if (!silent)
				{
					this.postOffice.sendSubscribed(list, who, deliverTo);

					// Notify anyone with APPROVE_SUBSCRIPTIONS
					for (Subscription maybeNotify: list.getSubscriptions())
						if (maybeNotify.getRole().getPermissions().contains(Permission.APPROVE_SUBSCRIPTIONS)
								&& (maybeNotify.getDeliverTo() != null))
							this.postOffice.sendModeratorSubscriptionNotice(maybeNotify.getDeliverTo(), sub, false);
				}

				// Flush any messages that might be held prior to this subscription.
				this.selfModerate(who.getId());

				return SubscribeResult.OK;
			}
		}
	}

	/*
	 * (non-Javadoc)
	 * @see org.subethamail.core.admin.i.Admin#unsubscribe(java.lang.Long, Long)
	 */
	public void unsubscribe(Long listId, Long personId) throws NotFoundException
	{
		Person who = this.em.get(Person.class, personId);
		this.unsubscribe(listId, who);
	}

	/**
	 * Does the work of unsubscribing someone.
	 */
	protected void unsubscribe(Long listId, Person who) throws NotFoundException
	{
		MailingList list = this.em.get(MailingList.class, listId);
		// Can't just call getSubscriptions().remote(listId). Workaround for hibernate bug.
		Subscription sub = who.getSubscriptions().get(listId);
		if (sub != null)
		{
			who.getSubscriptions().remove(listId);
			list.getSubscriptions().remove(sub);
			this.em.remove(sub);
		}

		// Notify anyone with APPROVE_SUBSCRIPTIONS
		for (Subscription maybeNotify: list.getSubscriptions())
			if (maybeNotify.getRole().getPermissions().contains(Permission.APPROVE_SUBSCRIPTIONS)
					&& (maybeNotify.getDeliverTo() != null))
				this.postOffice.sendModeratorSubscriptionNotice(maybeNotify.getDeliverTo(), sub, true);
	}

	/**
	 * @return a valid password.
	 */
	protected String generateRandomPassword()
	{
		StringBuffer gen = new StringBuffer(PASSWORD_GEN_LENGTH);

		for (int i=0; i<PASSWORD_GEN_LENGTH; i++)
		{
			int which = (int)(PASSWORD_GEN_CHARS.length() * this.randomizer.nextDouble());

			gen.append(PASSWORD_GEN_CHARS.charAt(which));
		}

		return gen.toString();
	}

	/*
	 * (non-Javadoc)
	 * @see org.subethamail.core.admin.i.Admin#setSiteAdmin(java.lang.Long, boolean)
	 */
	public void setSiteAdmin(Long personId, boolean value) throws NotFoundException
	{
		Person p = this.em.get(Person.class, personId);
		p.setSiteAdmin(value);


		// TODO: replace this with resin/auth code
		//this.flushJBossCredentialCache(personId);
	}

	/*
	 * (non-Javadoc)
	 * @see org.subethamail.core.admin.i.Admin#setSiteAdmin(java.lang.String, boolean)
	 */
	public void setSiteAdminByEmail(String email, boolean siteAdmin) throws NotFoundException
	{
		EmailAddress ea = this.em.getEmailAddress(email);
		ea.getPerson().setSiteAdmin(siteAdmin);

		// TODO: replace this with resin/auth code
		//this.flushJBossCredentialCache(ea.getPerson().getId());
	}


	/* (non-Javadoc)
	 * @see org.subethamail.core.admin.i.Admin#addEmail(java.lang.Long, java.lang.String)
	 */
	public void addEmail(Long personId, String email) throws NotFoundException
	{
		EmailAddress addy = this.em.findEmailAddress(email);

		// Three cases:  either addy is null, addy is already associated with
		// the person, or addy is already associated with someone else.

		// Lets quickly handle the case were we don't have to do anything
		if ((addy != null) && addy.getPerson().getId().equals(personId))
			return;

		Person who = this.em.get(Person.class, personId);

		if (addy == null)
		{
			addy = new EmailAddress(who, email);
			this.em.persist(addy);
			who.addEmailAddress(addy);
		}
		else
		{
			this.merge(addy.getPerson().getId(), who.getId());
		}

		this.selfModerate(who.getId());
	}

	/* (non-Javadoc)
	 * @see org.subethamail.core.admin.i.Admin#merge(java.lang.Long, java.lang.Long)
	 */
	public void merge(Long fromPersonId, Long toPersonId) throws NotFoundException
	{
		Person from = this.em.get(Person.class, fromPersonId);
		Person to = this.em.get(Person.class, toPersonId);

		log.log(Level.FINE,"Merging {0} into {1}", new Object[]{from, to});

		// First of all watch out for permission upgrade
		if (from.isSiteAdmin())
			to.setSiteAdmin(true);

		// Move email addresses
		for (EmailAddress addy: from.getEmailAddresses().values())
		{
		    log.log(Level.FINE," merging {0}", addy);

			addy.setPerson(to);
			to.addEmailAddress(addy);
		}

		from.getEmailAddresses().clear();

		// Move subscriptions
		for (Subscription sub: from.getSubscriptions().values())
		{
			// Keep our current subscription if there is a duplicate
			Subscription toSub = to.getSubscriptions().get(sub.getList().getId());
			if (toSub != null)
			{
			    log.log(Level.FINE," abandoning duplicate {0}", sub);

				// Special case - if the other was an owner role, upgrade this one too
				if (sub.getRole().isOwner())
					toSub.setRole(sub.getRole());

				this.em.remove(sub);
			}
			else
			{
			    log.log(Level.FINE," merging {0}", sub);

				sub.setPerson(to);
				to.addSubscription(sub);
			}
		}

		from.getSubscriptions().clear();

		// Move held subscriptions
		for (SubscriptionHold hold: from.getHeldSubscriptions().values())
		{
			Long listId = hold.getList().getId();
			if (to.getSubscriptions().containsKey(listId) || to.getHeldSubscriptions().containsKey(listId))
			{
			    log.log(Level.FINE," abandoning obsolete or duplicate {0}", hold);

				this.em.remove(hold);
			}
			else
			{
			    log.log(Level.FINE," merging {0}", hold);

				hold.setPerson(to);
				to.addHeldSubscription(hold);
			}
		}

		from.getHeldSubscriptions().clear();

		// Some of those holds we might not need anymore because we were already
		// subscribed or acquired a new subscription.
		for (SubscriptionHold hold: to.getHeldSubscriptions().values())
		{
			Long listId = hold.getList().getId();
			if (to.getSubscriptions().containsKey(listId))
			{
				to.getHeldSubscriptions().remove(listId);
				this.em.remove(hold);
			}
		}

		// Nuke the old person object
		log.log(Level.FINE," deleting person {0}", from);

		this.em.remove(from);
	}

	/* (non-Javadoc)
	 * @see org.subethamail.core.admin.i.Admin#selfModerate(java.lang.Long)
	 */
	@SuppressWarnings("unchecked")
	public int selfModerate(Long personId) throws NotFoundException
	{
		Person who = this.em.get(Person.class, personId);

		List<Mail> heldMail = this.em.findSoftHoldsForPerson(personId);

		int count = 0;

		for (Mail held: heldMail)
		{
			if (held.getList().getPermissionsFor(who).contains(Permission.POST))
			{
				held.approve();
				try {
					this.inboundQueue.put(new InjectedQueueItem(held));
				} catch (InterruptedException e) {
					throw new RuntimeException(e);
				}
				count++;
			}
		}

		return count;
	}

	/*
	 * (non-Javadoc)
	 * @see org.subethamail.core.admin.i.Admin#getSiteAdmins()
	 */
	public List<PersonData> getSiteAdmins()
	{
		List<Person> siteAdmins = this.em.findSiteAdmins();
		return Transmute.people(siteAdmins);
	}

	/*
	 * (non-Javadoc)
	 * @see org.subethamail.core.admin.i.Admin#setListAddresses(java.lang.Long, javax.mail.internet.InternetAddress, java.net.URL)
	 */
	public void setListAddresses(Long listId, InternetAddress address, URL url) throws NotFoundException, DuplicateListDataException, InvalidListDataException
	{
		MailingList list = this.em.get(MailingList.class, listId);

		InternetAddress checkAddress = list.getEmail().equals(address.getAddress()) ? null : address;
		URL checkUrl = list.getUrl().equals(url.toString()) ? null : url;
		this.checkListAddresses(checkAddress, checkUrl);

		list.setEmail(address.getAddress());
		list.setUrl(url.toString());
	}

	/**
	 * Checks whether or not the list addresses are ok (valid and not duplicates)
	 *
	 * @param address can be null to skip address checking
	 * @param url can be null to skip url checking
	 */
	protected void checkListAddresses(InternetAddress address, URL url) throws DuplicateListDataException, InvalidListDataException
	{
		boolean dupAddress = false;
		boolean dupUrl = false;

		if (address != null)
		{
			boolean ownerAddy = OwnerAddress.getList(address.getAddress()) != null;
			boolean verpAddy = VERPAddress.getVERPBounce(address.getAddress()) != null;

			if (ownerAddy || verpAddy)
				throw new InvalidListDataException("Address cannot be used", ownerAddy, verpAddy);

			try
			{
				this.em.getMailingList(address);
				dupAddress = true;
			}
			catch (NotFoundException ex) {}
		}

		if (url != null)
		{
			// TODO:  consider whether or not we should enforce any formatting of
			// the url here.  Seems like that's a job for the web front end?

			try
			{
				this.em.getMailingList(url);
				dupUrl = true;
			}
			catch (NotFoundException ex) {}
		}

		if (dupAddress || dupUrl)
			throw new DuplicateListDataException("Mailing list already exists", dupAddress, dupUrl);
	}

	/*
	 * (non-Javadoc)
	 * @see org.subethamail.core.admin.i.Admin#getLists(int, int)
	 */
	public List<ListData> getLists(int skip, int count)
	{
		return Transmute.mailingLists(this.em.findMailingLists(skip, count));
	}

	/*
	 * (non-Javadoc)
	 * @see org.subethamail.core.admin.i.Admin#getListsPlus(int, int)
	 */
	public List<ListDataPlus> getListsPlus(int skip, int count) throws NotFoundException, PermissionException
	{
		List<MailingList> mailingLists = this.em.findMailingLists(skip, count);
		List<ListDataPlus> listDatas = new ArrayList<ListDataPlus>(mailingLists.size());

		for (MailingList list : mailingLists)
		{
			ListDataPlus listData = Transmute.mailingListPlus(list,
					this.em.countSubscribers(list.getId()),
					this.em.countMailByList(list.getId()));
			listDatas.add(listData);
		}
		return listDatas;
	}

	/*
	 * (non-Javadoc)
	 * @see org.subethamail.core.admin.i.Admin#searchLists(java.lang.String, int, int)
	 */
	public List<ListData> searchLists(String query, int skip, int count)
	{
		return Transmute.mailingLists(this.em.findMailingLists(query, skip, count));
	}

	/*
	 * (non-Javadoc)
	 * @see org.subethamail.core.admin.i.Admin#searchListsPlus(java.lang.String, int, int)
	 */
	public List<ListDataPlus> searchListsPlus(String query, int skip, int count) throws NotFoundException, PermissionException
	{
		List<MailingList> mailingLists = this.em.findMailingLists(query, skip, count);
		List<ListDataPlus> listDatas = new ArrayList<ListDataPlus>(mailingLists.size());

		for (MailingList list : mailingLists)
		{
			ListDataPlus listData = Transmute.mailingListPlus(list,
				this.em.countSubscribers(list.getId()),
				this.em.countMailByList(list.getId()));
			listDatas.add(listData);
		}
		return listDatas;
	}

	/*
	 * (non-Javadoc)
	 * @see org.subethamail.core.admin.i.Admin#countLists()
	 */
	public int countLists()
	{
		return this.em.countLists();
	}

	/*
	 * (non-Javadoc)
	 * @see org.subethamail.core.admin.i.Admin#countLists(java.lang.String)
	 */
	public int countListsQuery(String query)
	{
		return this.em.countLists(query);
	}

	/*
	 * (non-Javadoc)
	 * @see org.subethamail.core.admin.i.Admin#getSiteStatus()
	 */
	public SiteStatus getSiteStatus()
	{
		return new SiteStatus(
				System.getProperty("file.encoding"),
				this.countLists(),
				this.em.countPeople(),
				this.em.countMail(),
				this.settings.getDefaultSiteUrl(),
				this.settings.getPostmaster(),
				this.smtpService.getFallbackHost()
			);
	}

	/*
	 * (non-Javadoc)
	 * @see org.subethamail.core.admin.i.Admin#getDefaultSiteUrl()
	 */
	public URL getDefaultSiteUrl()
	{
		return this.settings.getDefaultSiteUrl();
	}

	/*
	 * (non-Javadoc)
	 * @see org.subethamail.core.admin.i.Admin#deleteList(java.lang.Long, java.lang.String)
	 */
	public boolean deleteList(Long listId, String password) throws NotFoundException
	{
		Person me = this.getMe();
		if (!me.checkPassword(password))
			return false;

		MailingList list = this.em.get(MailingList.class, listId);

		// Cascading delete should take care of:
		// Subscriptions
		// SubscriptionHolds
		// Mails
		// Roles
		// EnabledFilters and FilterArguments
		this.em.remove(list);

		// Cascading persistence is not smart enough when dealing with the 2nd
		// level cache; for instance, Person objects have cached relationships
		// to (now defunct) Subscription objects.  We can just hit the problem
		// with a sledgehammer and reset the cache.
		SessionFactory sf = ((EntityManagerImpl)this.em.getDelegate()).getSession().getSessionFactory();
		sf.getCache().evictCollectionRegions();
		sf.getCache().evictEntityRegions();
		sf.getCache().evictEntityRegions();
		
		// TODO:  rebuild the search index?

		return true;
	}

	/* (non-Javadoc)
	 * @see org.subethamail.core.admin.i.Admin#setPersonName(java.lang.Long, java.lang.String)
	 */
	@PermitAll
	public void setPersonName(Long personId, String name) throws NotFoundException, PermissionException
	{
		Person pers = this.em.get(Person.class, personId);

		// Let's just cut this out right now
		if (pers.getName().equals(name))
			return;

		Person me = this.getMe();

		// Easy case, are we an admin?  No prob.
		if (me.isSiteAdmin())
		{
			pers.setName(name.trim());
			return;
		}

		// The special case (owners of lists to which the person is subscribed) only
		// works if the Person does not already have a name.
		if (pers.getName().trim().length() > 0)
			throw new PermissionException(Permission.EDIT_SUBSCRIPTIONS, "User already has a name and you can't replace it");

		// If the user is subscribed to any lists that we are an owner for
		for (Subscription mySub: me.getSubscriptions().values())
		{
			if (mySub.getRole().isOwner())
			{
				if (pers.getSubscriptions().containsKey(mySub.getList().getId()))
				{
					pers.setName(name.trim());
					return;
				}
			}
		}

		// Fallthrough case is that we were not an appropriate list owner, too bad
		throw new PermissionException(Permission.EDIT_SUBSCRIPTIONS, "You are not allowed to change this user's name");
	}

	/* (non-Javadoc)
	 * @see org.subethamail.core.admin.i.Admin#rebuildSearchIndexes()
	 */
	@Override
	public void rebuildSearchIndexes()
	{
		// For some reason this generates an exception, something about unable
		// to synchronize transactions
//		FullTextEntityManager fullTextEntityManager = Search.getFullTextEntityManager(em);
//		try
//		{
//			fullTextEntityManager.createIndexer().startAndWait();
//		}
//		catch (InterruptedException e) { throw new RuntimeException(e); }

		// This alternative code (from the Hibernate Search docs) seems to work
		// although it generates a warning message when complete.  Not going to
		// worry about it, this code doesn't run often.
		final int BATCH_SIZE = 128;
		org.hibernate.Session session = ((EntityManagerImpl)this.em.getDelegate()).getSession();		
		FullTextSession fullTextSession = Search.getFullTextSession(session);
		fullTextSession.setFlushMode(FlushMode.MANUAL);
		fullTextSession.setCacheMode(CacheMode.IGNORE);
		Transaction transaction = fullTextSession.beginTransaction();
		//Scrollable results will avoid loading too many objects in memory
		ScrollableResults results = fullTextSession.createCriteria(Mail.class)
		    .setFetchSize(BATCH_SIZE)
		    .scroll(ScrollMode.FORWARD_ONLY);
		int index = 0;
		while (results.next())
		{
		    index++;
		    fullTextSession.index(results.get(0)); //index each element
		    if (index % BATCH_SIZE == 0) {
		        fullTextSession.flushToIndexes(); //apply changes to indexes
		        fullTextSession.clear(); //free memory since the queue is processed
		    }
		}
		transaction.commit();	
	}
}
