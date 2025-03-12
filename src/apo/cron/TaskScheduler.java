package apo.cron;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.scheduling.annotation.SchedulingConfigurer;
import org.springframework.scheduling.concurrent.ThreadPoolTaskScheduler;
import org.springframework.scheduling.config.ScheduledTaskRegistrar;
import org.springframework.scheduling.support.CronTrigger;
import org.springframework.stereotype.Component;

import apo.controllers.MainController;
import apo.dto.CronTask;
import apo.dto.CronTaskRepository;

@Component
public class TaskScheduler implements SchedulingConfigurer, ApplicationListener<ContextRefreshedEvent>
{

	private ThreadPoolTaskScheduler task_scheduler;

	@Autowired
	private CronTaskRepository cron_task_repository;

	@Autowired
	private MainController controller;

	public TaskScheduler()
	{
		this.task_scheduler = new ThreadPoolTaskScheduler();
		this.task_scheduler.initialize();
	}

	@Override
	public void configureTasks(ScheduledTaskRegistrar taskRegistrar)
	{
		taskRegistrar.setScheduler(task_scheduler);
	}

//	@Value("${cron}")
//	String cronRule;

	// Надо выделить событие в отдельный поток, чтобы прогузились все бины, и уже
	// тогда спавнить задачу (так как кэш при postinit еще может не существовать)
	@Override
	public void onApplicationEvent(ContextRefreshedEvent event)
	{
		task_scheduler.submitCompletable(() ->
		{
			List<CronTask> cronTasks = cron_task_repository.findEnabledTasks();
			for (CronTask cronTask : cronTasks)
				task_scheduler.schedule(() -> controller.syncToday(cronTask.getCode()),
						new CronTrigger(cronTask.getRule()));
		});
	}

}
