package apo.dto;

import java.io.Serializable;
import java.time.LocalDate;

import jakarta.persistence.Embeddable;

@SuppressWarnings("all")
@Embeddable
public class CurrencyId implements Serializable
{

	private LocalDate date;
	private String code;

	public CurrencyId()
	{
		;
	}

	public CurrencyId(LocalDate date, String code)
	{
		super();
		this.date = date;
		this.code = code;
	}

}
