package br.com.caelum.vraptor.tasks.jobs;

import org.hibernate.Transaction;
import org.hibernate.classic.Session;
import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;

import br.com.caelum.vraptor.tasks.TransactionalTask;

public class TransactionalJob implements Job {

	private final TransactionalTask task;
	private final Session session;

	public TransactionalJob(TransactionalTask task, Session session) {
		this.task = task;
		this.session = session;
	}

	@Override
	public void execute(JobExecutionContext context) throws JobExecutionException {

		Transaction transaction = null;

		try {
			transaction = session.beginTransaction();
			task.setup(session);
			task.execute();
			transaction.commit();
		}
		finally {
			if (transaction != null && transaction.isActive()) {
				transaction.rollback();
			}
			session.close();
		}
	}
}