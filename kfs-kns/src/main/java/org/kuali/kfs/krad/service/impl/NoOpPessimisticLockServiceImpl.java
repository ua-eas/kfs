package org.kuali.kfs.krad.service.impl;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.kuali.kfs.kns.service.SessionDocumentService;
import org.kuali.kfs.krad.document.Document;
import org.kuali.kfs.krad.document.authorization.PessimisticLock;
import org.kuali.kfs.krad.service.PessimisticLockService;
import org.kuali.rice.kim.api.identity.Person;

/**
 * A {@link PessimisticLockService} implementation which does nothing.
 * Use to improve performance in applications that do not use pessimistic locking at all.
 */
public class NoOpPessimisticLockServiceImpl implements PessimisticLockService {

	@Override
	public void delete(String id) {
	}

	@Override
	public PessimisticLock generateNewLock(String documentNumber) {
		return null;
	}

	@Override
	public PessimisticLock generateNewLock(String documentNumber, String lockDescriptor) {
		return null;
	}

	@Override
	public PessimisticLock generateNewLock(String documentNumber, Person user) {
		return null;
	}

	@Override
	public PessimisticLock generateNewLock(String documentNumber, String lockDescriptor, Person user) {
		return null;
	}

	@Override
	public List<PessimisticLock> getPessimisticLocksForDocument(String documentNumber) {
		return new ArrayList<PessimisticLock>();
	}

	@Override
	public List<PessimisticLock> getPessimisticLocksForSession(String sessionId) {
		return new ArrayList<PessimisticLock>();
	}

	@Override
	public boolean isPessimisticLockAdminUser(Person user) {
		return false;
	}

	@Override
	public void releaseAllLocksForUser(List<PessimisticLock> locks, Person user) {
	}

	@Override
	public void releaseAllLocksForUser(List<PessimisticLock> locks, Person user, String lockDescriptor) {
	}

	@Override
	public PessimisticLock save(PessimisticLock lock) {
		return null;
	}

	@Override
	public Map establishLocks(Document document, Map editMode, Person user) {
		return null;
	}

	@Override
	public void establishWorkflowPessimisticLocking(Document document) {
	}

	@Override
	public void releaseWorkflowPessimisticLocking(Document document) {
	}

	@Override
	public Set getDocumentActions(Document document, Person user, Set<String> documentActions) {
		return null;
	}

}
