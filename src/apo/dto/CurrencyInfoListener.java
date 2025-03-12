package apo.dto;

import java.time.LocalDateTime;

import jakarta.persistence.PreUpdate;

public class CurrencyInfoListener
{

	@PreUpdate
	public void setLastUpdated(Object entity)
	{
		if (!(entity instanceof CurrencyInfo))
			return;

		((CurrencyInfo) entity).setLast_updated(LocalDateTime.now());
	}

}
