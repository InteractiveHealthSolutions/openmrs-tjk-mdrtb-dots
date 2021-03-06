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
package org.openmrs.notification.db.hibernate;

import java.util.Collections;
import java.util.Date;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.hibernate.Criteria;
import org.hibernate.SessionFactory;
import org.hibernate.criterion.Expression;
import org.hibernate.criterion.Order;
import org.openmrs.User;
import org.openmrs.api.db.DAOException;
import org.openmrs.notification.Alert;
import org.openmrs.notification.db.AlertDAO;

/**
 * Hibernate specific implementation of the
 */
public class HibernateAlertDAO implements AlertDAO {
	
	private final Log log = LogFactory.getLog(getClass());
	
	/**
	 * Hibernate session factory
	 */
	private SessionFactory sessionFactory;
	
	public HibernateAlertDAO() {
	}
	
	/**
	 * Set session factory
	 * 
	 * @param sessionFactory
	 */
	public void setSessionFactory(SessionFactory sessionFactory) {
		this.sessionFactory = sessionFactory;
	}
	
	/**
	 * @see org.openmrs.notification.db.AlertDAO#saveAlert(org.openmrs.notification.Alert)
	 */
	public Alert saveAlert(Alert alert) throws DAOException {
		sessionFactory.getCurrentSession().saveOrUpdate(alert);
		return alert;
	}
	
	/**
	 * @see org.openmrs.notification.db.AlertDAO#getAlert(java.lang.Integer)
	 */
	public Alert getAlert(Integer alertId) throws DAOException {
		return (Alert) sessionFactory.getCurrentSession().get(Alert.class, alertId);
	}
	
	/**
	 * @see org.openmrs.notification.db.AlertDAO#deleteAlert(org.openmrs.notification.Alert)
	 */
	public void deleteAlert(Alert alert) throws DAOException {
		sessionFactory.getCurrentSession().delete(alert);
	}
	
	/**
	 * @see org.openmrs.notification.AlertService#getAllAlerts(boolean)
	 */
	@SuppressWarnings("unchecked")
	public List<Alert> getAllAlerts(boolean includeExpired) throws DAOException {
		Criteria crit = sessionFactory.getCurrentSession().createCriteria(Alert.class);
		
		// exclude the expired alerts unless requested
		if (includeExpired == false)
			crit.add(Expression.or(Expression.isNull("dateToExpire"), Expression.gt("dateToExpire", new Date())));
		
		return crit.list();
	}
	
	/**
	 * @see org.openmrs.notification.db.AlertDAO#getAlerts(org.openmrs.User, boolean, boolean)
	 */
	@SuppressWarnings("unchecked")
	public List<Alert> getAlerts(User user, boolean includeRead, boolean includeExpired) throws DAOException {
		log.debug("Getting alerts for user " + user + " read? " + includeRead + " expired? " + includeExpired);
		
		Criteria crit = sessionFactory.getCurrentSession().createCriteria(Alert.class, "alert");
		
		if (user != null && user.getUserId() != null) {
			crit.createCriteria("recipients", "recipient");
			crit.add(Expression.eq("recipient.recipient", user));
		} else {
			// getting here means we passed in no user or a blank user.
			// a null recipient column means get stuff for the anonymous user
			//crit.add(Expression.isNull("recipient.recipient"));
			
			// returning an empty list for now because the above throws an error.
			// we may need to remodel how recipients are handled to get anonymous users alerts
			return Collections.emptyList();
		}
		
		// exclude the expired alerts unless requested
		if (includeExpired == false)
			crit.add(Expression.or(Expression.isNull("dateToExpire"), Expression.gt("dateToExpire", new Date())));
		
		// exclude the read alerts unless requested
		if (includeRead == false && (user != null && user.getUserId() != null)) {
			crit.add(Expression.eq("alertRead", false));
			crit.add(Expression.eq("recipient.alertRead", false));
		}
		
		crit.addOrder(Order.desc("dateChanged"));
		
		return crit.list();
	}
	
}
