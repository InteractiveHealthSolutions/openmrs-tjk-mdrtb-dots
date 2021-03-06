/**
 * The contents of this file are subject to the OpenMRS Public License
 * Version 1.0 (the "License"); you may not use this file except in
 * compliance with the License. You may obtain a copy of the License at
 * http://license.openmrs.org
 *
 * Software distributed under the License is distributed on an "AS IS"
 * basis, WITHOUT WARRANTY OF ANY KIND, either express or implied. See the
 * License for the specific language governing rights and limitations
 * under the License.
 *
 * Copyright (C) OpenMRS, LLC.  All Rights Reserved.
 */
package org.openmrs.api.db.hibernate;

import java.math.BigInteger;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Vector;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.hibernate.Criteria;
import org.hibernate.Query;
import org.hibernate.SessionFactory;
import org.hibernate.criterion.Expression;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Restrictions;
import org.hibernate.dialect.Dialect;
import org.hibernate.dialect.HSQLDialect;
import org.openmrs.Person;
import org.openmrs.Privilege;
import org.openmrs.Role;
import org.openmrs.User;
import org.openmrs.api.context.Context;
import org.openmrs.api.db.DAOException;
import org.openmrs.api.db.LoginCredential;
import org.openmrs.api.db.UserDAO;
import org.openmrs.patient.impl.LuhnIdentifierValidator;
import org.openmrs.util.Security;

/**
 * Hibernate specific database methods for the UserService
 * 
 * @see org.openmrs.api.context.Context
 * @see org.openmrs.api.db.UserDAO
 * @see org.openmrs.api.UserService
 */
public class HibernateUserDAO implements UserDAO {
	
	protected final Log log = LogFactory.getLog(getClass());
	
	/**
	 * Hibernate session factory
	 */
	private SessionFactory sessionFactory;
	
	/**
	 * Set session factory
	 * 
	 * @param sessionFactory
	 */
	public void setSessionFactory(SessionFactory sessionFactory) {
		this.sessionFactory = sessionFactory;
	}
	
	/**
	 * @see org.openmrs.api.UserService#saveUser(org.openmrs.User, java.lang.String)
	 */
	public User saveUser(User user, String password) {
		
		sessionFactory.getCurrentSession().saveOrUpdate(user);
		if (password != null) {
			//update the new user with the password
			String salt = Security.getRandomToken();
			String hashedPassword = Security.encodeString(password + salt);
			
			updateUserPassword(hashedPassword, salt, Context.getAuthenticatedUser().getUserId(), new Date(), user
			        .getUserId());
		}
		return user;
	}
	
	/**
	 * @see org.openmrs.api.UserService#getUserByUsername(java.lang.String)
	 */
	@SuppressWarnings("unchecked")
	public User getUserByUsername(String username) {
		Query query = sessionFactory.getCurrentSession().createQuery(
		    "from User u where u.retired = 0 and (u.username = ? or u.systemId = ?)");
		query.setString(0, username);
		query.setString(1, username);
		List<User> users = query.list();
		
		if (users == null || users.size() == 0) {
			log.warn("request for username '" + username + "' not found");
			return null;
		}
		
		return users.get(0);
	}
	
	/**
	 * @see org.openmrs.api.UserService#hasDuplicateUsername(org.openmrs.User)
	 */
	public boolean hasDuplicateUsername(String username, String systemId, Integer userId) {
		if (username == null || username.length() == 0)
			username = "-";
		if (systemId == null || systemId.length() == 0)
			systemId = "-";
		
		if (userId == null)
			userId = new Integer(-1);
		
		String usernameWithCheckDigit = username;
		try {
			//Hardcoding in Luhn since past user IDs used this validator.
			usernameWithCheckDigit = new LuhnIdentifierValidator().getValidIdentifier(username);
		}
		catch (Exception e) {}
		
		Query query = sessionFactory
		        .getCurrentSession()
		        .createQuery(
		            "select count(*) from User u where (u.username = :uname1 or u.systemId = :uname2 or u.username = :sysid1 or u.systemId = :sysid2 or u.systemId = :uname3) and u.userId <> :uid");
		query.setString("uname1", username);
		query.setString("uname2", username);
		query.setString("sysid1", systemId);
		query.setString("sysid2", systemId);
		query.setString("uname3", usernameWithCheckDigit);
		query.setInteger("uid", userId);
		
		Long count = (Long) query.uniqueResult();
		
		log.debug("# users found: " + count);
		if (count == null || count == 0)
			return false;
		else
			return true;
	}
	
	/**
	 * @see org.openmrs.api.UserService#getUser(java.lang.Integer)
	 */
	public User getUser(Integer userId) {
		User user = (User) sessionFactory.getCurrentSession().get(User.class, userId);
		
		return user;
	}
	
	/**
	 * @see org.openmrs.api.UserService#getAllUsers()
	 */
	@SuppressWarnings("unchecked")
	public List<User> getAllUsers() throws DAOException {
		return sessionFactory.getCurrentSession().createQuery("from User u order by u.userId").list();
	}
	
	/**
	 * @see org.openmrs.api.UserService#deleteUser(org.openmrs.User)
	 */
	public void deleteUser(User user) {
		sessionFactory.getCurrentSession().delete(user);
	}
	
	/**
	 * @see org.openmrs.api.UserService#getUsersByRole(org.openmrs.Role)
	 */
	@SuppressWarnings("unchecked")
	public List<User> getUsersByRole(Role role) throws DAOException {
		List<User> users = sessionFactory.getCurrentSession().createCriteria(User.class, "u").createCriteria("roles", "r")
		        .add(Expression.like("r.role", role.getRole())).addOrder(Order.asc("u.username")).list();
		
		return users;
		
	}
	
	/**
	 * @see org.openmrs.api.UserService#getAllPrivileges()
	 */
	@SuppressWarnings("unchecked")
	public List<Privilege> getAllPrivileges() throws DAOException {
		return sessionFactory.getCurrentSession().createQuery("from Privilege p order by p.privilege").list();
	}
	
	/**
	 * @see org.openmrs.api.UserService#getPrivilege(String)
	 */
	public Privilege getPrivilege(String p) throws DAOException {
		return (Privilege) sessionFactory.getCurrentSession().get(Privilege.class, p);
	}
	
	/**
	 * @see org.openmrs.api.db.UserDAO#deletePrivilege(org.openmrs.Privilege)
	 */
	public void deletePrivilege(Privilege privilege) throws DAOException {
		sessionFactory.getCurrentSession().delete(privilege);
	}
	
	/**
	 * @see org.openmrs.api.db.UserDAO#savePrivilege(org.openmrs.Privilege)
	 */
	public Privilege savePrivilege(Privilege privilege) throws DAOException {
		sessionFactory.getCurrentSession().saveOrUpdate(privilege);
		return privilege;
	}
	
	/**
	 * @see org.openmrs.api.UserService#purgeRole(org.openmrs.Role)
	 */
	public void deleteRole(Role role) throws DAOException {
		sessionFactory.getCurrentSession().delete(role);
	}
	
	/**
	 * @see org.openmrs.api.UserService#saveRole(org.openmrs.Role)
	 */
	public Role saveRole(Role role) throws DAOException {
		sessionFactory.getCurrentSession().saveOrUpdate(role);
		return role;
	}
	
	/**
	 * @see org.openmrs.api.UserService#getAllRoles()
	 */
	@SuppressWarnings("unchecked")
	public List<Role> getAllRoles() throws DAOException {
		return sessionFactory.getCurrentSession().createQuery("from Role r order by r.role").list();
	}
	
	/**
	 * @see org.openmrs.api.UserService#getRole(String)
	 */
	public Role getRole(String r) throws DAOException {
		return (Role) sessionFactory.getCurrentSession().get(Role.class, r);
	}
	
	/**
	 * @see org.openmrs.api.db.UserDAO#changePassword(org.openmrs.User, java.lang.String)
	 */
	public void changePassword(User u, String pw) throws DAOException {
		User authUser = Context.getAuthenticatedUser();
		
		if (authUser == null)
			authUser = u;
		
		log.debug("updating password");
		//update the user with the new password
		String salt = Security.getRandomToken();
		String newHashedPassword = Security.encodeString(pw + salt);
		
		updateUserPassword(newHashedPassword, salt, authUser.getUserId(), new Date(), u.getUserId());
		
	}
	
	/**
	 * @see org.openmrs.api.db.UserDAO#changeHashedPassword(User, String, String)
	 */
	public void changeHashedPassword(User user, String hashedPassword, String salt) throws DAOException {
		User authUser = Context.getAuthenticatedUser();
		updateUserPassword(hashedPassword, salt, authUser.getUserId(), new Date(), user.getUserId());
	}
	
	/**
	 * @param newHashedPassword
	 * @param salt
	 * @param userId
	 * @param date
	 * @param userId2
	 */
	private void updateUserPassword(String newHashedPassword, String salt, Integer changedBy, Date dateChanged,
	                                Integer userIdToChange) {
		User changeForUser = getUser(userIdToChange);
		if (changeForUser == null)
			throw new DAOException("Couldn't find user to set password for userId=" + userIdToChange);
		User changedByUser = getUser(changedBy);
		LoginCredential credentials = new LoginCredential();
		credentials.setUserId(userIdToChange);
		credentials.setHashedPassword(newHashedPassword);
		credentials.setSalt(salt);
		credentials.setChangedBy(changedByUser);
		credentials.setDateChanged(dateChanged);
		credentials.setUuid(changeForUser.getUuid());
		
		sessionFactory.getCurrentSession().merge(credentials);
	}
	
	/**
	 * @see org.openmrs.api.UserService#changePassword(java.lang.String, java.lang.String)
	 */
	public void changePassword(String pw, String pw2) throws DAOException {
		User u = Context.getAuthenticatedUser();
		LoginCredential credentials = getLoginCredential(u);
		if (!credentials.checkPassword(pw)) {
			log.error("Passwords don't match");
			throw new DAOException("Passwords don't match");
		}
		
		log.info("updating password for " + u.getUsername());
		
		// update the user with the new password
		String salt = Security.getRandomToken();
		String newHashedPassword = Security.encodeString(pw2 + salt);
		updateUserPassword(newHashedPassword, salt, u.getUserId(), new Date(), u.getUserId());
	}
	
	/**
	 * @see org.openmrs.api.UserService#changeQuestionAnswer(java.lang.String, java.lang.String,
	 *      java.lang.String)
	 */
	public void changeQuestionAnswer(String pw, String question, String answer) throws DAOException {
		User u = Context.getAuthenticatedUser();
		
		LoginCredential credentials = getLoginCredential(u);
		if (!credentials.checkPassword(pw)) {
			log.error("Passwords don't match");
			throw new DAOException("Passwords don't match");
		}
		
		log.info("Updating secret question and answer for " + u.getUsername());
		credentials.setSecretQuestion(question);
		credentials.setSecretAnswer(answer);
		updateLoginCredential(credentials);
	}
	
	/**
	 * @see org.openmrs.api.UserService#changeQuestionAnswer(User, String, String)
	 */
	public void changeQuestionAnswer(User u, String question, String answer) throws DAOException {
		Connection connection = sessionFactory.getCurrentSession().connection();
		PreparedStatement ps = null;
		try {
			
			String sql = "UPDATE `users` SET secret_question = ?, secret_answer = ?, date_changed = ?, changed_by = ? WHERE user_id = ?";
			
			// if we're in a junit test, we're probably using hsql...and hsql 
			// does not like the backtick.  Replace the backtick with the hsql 
			// escape character: the double quote (or nothing). 
			Dialect dialect = HibernateUtil.getDialect(sessionFactory);
			if (HSQLDialect.class.getName().equals(dialect.getClass().getName()))
				sql = sql.replace("`", "");
			
			ps = connection.prepareStatement(sql);
			
			ps.setString(1, question);
			ps.setString(2, answer);
			ps.setDate(3, new java.sql.Date(new Date().getTime()));
			ps.setInt(4, Context.getAuthenticatedUser().getUserId());
			ps.setInt(5, u.getUserId());
			
			ps.executeUpdate();
		}
		catch (SQLException e) {
			throw new DAOException("SQL Exception while trying to update a user's secret question", e);
		}
		finally {
			if (ps != null) {
				try {
					ps.close();
				}
				catch (SQLException e) {
					log.error("Error generated while closing statement", e);
				}
			}
		}
	}
	
	/**
	 * @see org.openmrs.api.UserService#isSecretAnswer(User, java.lang.String)
	 */
	public boolean isSecretAnswer(User u, String answer) throws DAOException {
		
		if (answer == null || answer.equals(""))
			return false;
		
		String answerOnRecord = getLoginCredential(u).getSecretAnswer();
		return (answer.equals(answerOnRecord));
	}
	
	/**
	 * @see org.openmrs.api.UserService#getUsers(java.lang.String, java.util.List, boolean)
	 */
	@SuppressWarnings("unchecked")
	public List<User> getUsers(String name, List<Role> roles, boolean includeRetired) {
		log.debug("name: " + name);
		
		// Create an HQL query like this:
		// select distinct user
		// from User as user inner join user.person.names as name
		// where (user.username like :name1 or ...and for systemId givenName familyName familyName2...)
		//   and (user.username like :name2 or ...and for systemId givenName familyName familyName2...)
		//   ...repeat for all name fragments...
		//   and user.retired = false
		// order by username asc
		
		List<String> criteria = new ArrayList<String>();
		int counter = 0;
		Map<String, String> namesMap = new HashMap<String, String>();
		if (name != null) {
			name = name.replace(", ", " ");
			String[] names = name.split(" ");
			for (String n : names) {
				if (n != null && n.length() > 0) {
					// compare each fragment of the query against username, systemId, given, middle, family, and family2
					String key = "name" + ++counter;
					String value = n + "%";
					namesMap.put(key, value);
					criteria.add("(user.username like :" + key + " or user.systemId like :" + key + " or name.givenName like :" + key + " or name.middleName like :" + key + " or name.familyName like :" + key + " or name.familyName2 like :" + key + ")");
				}
			}
		}
		if (includeRetired == false)
			criteria.add("user.retired = false");

		// build the hql query
		String hql = "select distinct user from User as user inner join user.person.names as name ";
		if (criteria.size() > 0)
			hql += "where ";
		for (Iterator<String> i = criteria.iterator(); i.hasNext(); ) {
			hql += i.next() + " ";
			if (i.hasNext())
				hql += "and ";
		}
		hql += " order by username asc";
		Query query = sessionFactory.getCurrentSession().createQuery(hql);
		for (Map.Entry<String, String> e : namesMap.entrySet())
			query.setString(e.getKey(), e.getValue());
		
		// Now apply the roles criteria
		// TODO add this to the HQL query
		// maybe: +inner join user.roles as role +where role.id in :roleIdList
		
		if (roles != null && roles.size() > 0) {
			List returnList = new Vector();
			
			log.debug("looping through to find matching roles");
			for (Object o : query.list()) {
				User u = (User) o;
				for (Role r : roles)
					if (u.hasRole(r.getRole(), true)) {
						returnList.add(u);
						break;
					}
			}
			
			return returnList;
		} else {
			log.debug("not looping because there appears to be no roles");
			return query.list();
		}
		
	}
	
	/**
	 * @see org.openmrs.api.UserService#generateSystemId()
	 */
	public Integer generateSystemId() {
		
		// TODO this algorithm will fail if someone deletes a user that is not the last one.
		
		String sql = "select count(user_id) as user_id from users";
		
		Query query = sessionFactory.getCurrentSession().createSQLQuery(sql);
		
		Object object = query.uniqueResult();
		
		Integer id = null;
		if (object instanceof BigInteger)
			id = ((BigInteger) query.uniqueResult()).intValue() + 1;
		else if (object instanceof Integer)
			id = ((Integer) query.uniqueResult()).intValue() + 1;
		else {
			log.warn("What is being returned here? Definitely nothing expected object value: '" + object + "' of class: "
			        + object.getClass());
			id = 1;
		}
		
		return id;
	}
	
	/**
	 * @see org.openmrs.api.UserService#getUsersByName(java.lang.String, java.lang.String, boolean)
	 */
	@SuppressWarnings("unchecked")
	public List<User> getUsersByName(String givenName, String familyName, boolean includeRetired) {
		List<User> users = new Vector<User>();
		String query = "from User u where u.names.givenName = :givenName and u.names.familyName = :familyName";
		if (!includeRetired)
			query += " and u.retired = false";
		Query q = sessionFactory.getCurrentSession().createQuery(query).setString("givenName", givenName).setString(
		    "familyName", familyName);
		for (User u : (List<User>) q.list()) {
			users.add(u);
		}
		return users;
	}
	
	/**
	 * @see org.openmrs.api.db.UserDAO#getPrivilegeByUuid(java.lang.String)
	 */
	public Privilege getPrivilegeByUuid(String uuid) {
		return (Privilege) sessionFactory.getCurrentSession().createQuery("from Privilege p where p.uuid = :uuid")
		        .setString("uuid", uuid).uniqueResult();
	}
	
	/**
	 * @see org.openmrs.api.db.UserDAO#getRoleByUuid(java.lang.String)
	 */
	public Role getRoleByUuid(String uuid) {
		return (Role) sessionFactory.getCurrentSession().createQuery("from Role r where r.uuid = :uuid").setString("uuid",
		    uuid).uniqueResult();
	}
	
	/**
	 * @see org.openmrs.api.db.UserDAO#getUserByUuid(java.lang.String)
	 */
	public User getUserByUuid(String uuid) {
		User ret = null;
		
		if (uuid != null) {
			uuid = uuid.trim();
			ret = (User) sessionFactory.getCurrentSession().createQuery("from User u where u.uuid = :uuid").setString(
			    "uuid", uuid).uniqueResult();
		}
		
		return ret;
	}
	
	/**
	 * @see org.openmrs.api.db.UserDAO#getLoginCredential(org.openmrs.User)
	 */
	public LoginCredential getLoginCredential(User user) {
		return (LoginCredential) sessionFactory.getCurrentSession().get(LoginCredential.class, user.getUserId());
	}
	
	/**
	 * @see org.openmrs.api.db.UserDAO#getLoginCredential(org.openmrs.User)
	 */
	public LoginCredential getLoginCredentialByUuid(String uuid) {
		if (uuid == null)
			return null;
		else
			return (LoginCredential) sessionFactory.getCurrentSession().createQuery(
			    "from LoginCredential where uuid = :uuid").setString("uuid", uuid.trim()).uniqueResult();
	}
	
	/**
	 * @see org.openmrs.api.db.UserDAO#updateLoginCredential(org.openmrs.LoginCredential)
	 */
	public void updateLoginCredential(LoginCredential credential) {
		sessionFactory.getCurrentSession().update(credential);
	}

	/**
     * @see org.openmrs.api.db.UserDAO#getUsersByPerson(org.openmrs.Person, boolean)
     */
    @SuppressWarnings("unchecked")
    public List<User> getUsersByPerson(Person person, boolean includeRetired) {
	    Criteria crit = sessionFactory.getCurrentSession().createCriteria(User.class);
	    if (person != null)
	    	crit.add(Restrictions.eq("person", person));
	    if (!includeRetired)
	    	crit.add(Restrictions.eq("retired", false));
	    return (List<User>) crit.list();
    }
	
}
