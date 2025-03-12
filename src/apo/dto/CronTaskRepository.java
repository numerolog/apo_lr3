package apo.dto;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface CronTaskRepository extends JpaRepository<CronTask, Integer>
{

	@Query("SELECT t FROM CronTask t WHERE t.enabled")
	List<CronTask> findEnabledTasks();

}
