package apo.cnbcz;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;
import org.springframework.web.client.RestTemplate;

@Service
public class CnbCzService
{

	private RestTemplate restTemplate = new RestTemplate();

//	@Cacheable(value="httpCache", unless = "#result == null")
	public String getDataByDay(String date)
	{
		String url = "https://www.cnb.cz/en/financial_markets/foreign_exchange_market/exchange_rate_fixing/daily.txt?date="
				+ date;
		return restTemplate.getForObject(url, String.class);
	}

	@Cacheable(value = "httpCache", unless = "#result == null")
	public String getDataByYear(String year)
	{
		String url = "https://www.cnb.cz/en/financial_markets/foreign_exchange_market/exchange_rate_fixing/year.txt?year="
				+ year;
		return restTemplate.getForObject(url, String.class);
	}

	private DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd.MM.uuuu");

	// Костыль, если вызвать кэшируемые методы из этого же класса - они не
	// закешируются :clown:
	@Autowired
	@Lazy
	private CnbCzService _self;

	public Map<String, Double> getNow()
	{
		Map<String, Double> r = new HashMap<>();
		String data = _self.getDataByDay(formatter.format(LocalDate.now()));

		String[] lines = data.split("\n");
		for (int i = 2; i < lines.length; i++)
		{
			String line = lines[i];
			String[] parts = line.split("\\|");

			if (parts.length != 5)
				continue;

			String code = parts[3];
			double amount = (double) Integer.parseInt(parts[2]);
			double currency = Double.parseDouble(parts[4]);

			// за amount code можно купить currency крон (курс валюты к кроне)
			double total = currency / amount;
			r.put(code, total);
		}
		return r;
	}

	@Cacheable(value = "interopCache", unless = "#result == null")
	public Map<String, Map<String, Double>> getByYear(int year)
	{
		Map<String, Map<String, Double>> r = new LinkedHashMap<>();

		String data = _self.getDataByYear(String.valueOf(year));

		String[] chunks = data.split("Date");
		for (String chunk : chunks)
		{
			String[] lines = chunk.split("\n");

			String[] header = lines[0].split("\\|");

			for (int i = 1; i < lines.length; i++)
			{
				String line = lines[i];
				String[] parts = line.split("\\|");

				Map<String, Double> byDate = new HashMap<>();
				String date = parts[0];

				for (int j = 1; j < header.length; j++)
				{
					String[] headerSpl = header[j].split("\\ ");
					String code = headerSpl[1];
					double amount = (double) Integer.parseInt(headerSpl[0]);
					double currency = Double.parseDouble(parts[j]);

					// за amount code можно купить currency крон (курс валюты к кроне)
					double total = currency / amount;
					byDate.put(code, total);
				}

				r.put(date, byDate);
			}
		}
		return r;
	}

	public Map<LocalDate, Map<String, Double>> getByPeriod(LocalDate from, LocalDate to)
	{
		Map<LocalDate, Map<String, Double>> r = new LinkedHashMap<>();

		LocalDate currentDate = from;
		do
		{
			Map<String, Double> daily = _self.getByYear(currentDate.getYear()).get(formatter.format(currentDate));
			if (daily != null)
			{
				r.put(currentDate, daily);
			} else
			{
				System.err.println("No data for " + currentDate);
			}
			currentDate = currentDate.plusDays(1);
		} while (!currentDate.isAfter(to));

		return r;
	}

}
