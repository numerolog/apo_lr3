package apo.dto;

import java.time.LocalDate;
import java.time.LocalDateTime;

import jakarta.persistence.Column;
import jakarta.persistence.EmbeddedId;
import jakarta.persistence.Entity;
import jakarta.persistence.EntityListeners;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;

@Entity
@EntityListeners(CurrencyInfoListener.class)
public class CurrencyInfo
{
	@EmbeddedId
	private CurrencyId id;

	private double value;

	private LocalDateTime last_updated;

	public CurrencyInfo()
	{
		;
	}

	public CurrencyInfo(CurrencyId id, double value, LocalDateTime last_updated)
	{
		super();
		this.id = id;
		this.value = value;
		this.last_updated = last_updated;
	}

	public double getValue()
	{
		return value;
	}

	public void setValue(double value)
	{
		this.value = value;
	}

	public LocalDateTime getLast_updated()
	{
		return last_updated;
	}

	public void setLast_updated(LocalDateTime last_updated)
	{
		this.last_updated = last_updated;
	}

}
