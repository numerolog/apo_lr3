package apo.controllers;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import apo.cnbcz.CnbCzService;
import apo.dto.CurrencyId;
import apo.dto.CurrencyInfo;
import apo.dto.CurrencyInfoRepository;

@Controller
public class MainController
{

	@Autowired
	private CnbCzService cnb_cz_service;

	@Autowired
	private CurrencyInfoRepository currency_info_repository;

	private ExecutorService executor = Executors.newSingleThreadExecutor();

	public boolean syncCode(LocalDate date, Map<String, Double> dayData, String code)
	{
		if (!dayData.containsKey(code))
		{
			System.err.println("No data for " + code);
			return false;
		}

		var value = dayData.get(code);
		var id = new CurrencyId(date, code);
		CurrencyInfo ci = currency_info_repository.findById(id).orElse(null);
		if (ci == null)
		{
			ci = new CurrencyInfo(id, value, LocalDateTime.now());
		} else
		{
			ci.setValue(value);
		}

		currency_info_repository.save(ci);

		return true;
	}

	public void syncToday(String code)
	{
		executor.submit(() ->
		{
			var today = cnb_cz_service.getNow();
			syncCode(LocalDate.now(), today, code);
		});
	}

	private DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd.MM.uuuu");

	// http://127.0.0.1:8080/schedule_sync?start_date=10.10.2021&end_date=10.10.2022&codes=RUB%20EUR%20CNY
	@GetMapping("/schedule_sync")
	public ResponseEntity<String> schedule_sync(@RequestParam("start_date") String start_date,
			@RequestParam("end_date") String end_date, @RequestParam("codes") String codes_)
	{
		try
		{
			LocalDate from = LocalDate.parse(start_date, formatter);
			LocalDate to = LocalDate.parse(end_date, formatter);

			if (to.isAfter(LocalDate.now()))
				throw new IllegalArgumentException("end date in future");

			if (to.isBefore(from))
				throw new IllegalArgumentException("end date < start date");

			if (codes_.isBlank())
				throw new IllegalArgumentException("codes empty");

			String[] codes = codes_.split("\\ ");
			if (new HashSet<>(Arrays.asList(codes)).size() != codes.length)
				throw new IllegalArgumentException("codes duplicate");
				
			executor.execute(() ->
			{
				System.err.println("Sync " + from + " ... " + to);
				var periodData = cnb_cz_service.getByPeriod(from, to);
				periodData.forEach((day, dayData) ->
				{
					for (String code : codes)
						syncCode(day, dayData, code);
				});
			});

			return ResponseEntity.ok("scheduled");
		} catch (Throwable t)
		{
			return ResponseEntity.badRequest().body(t.getMessage());
		}
	}

	//http://127.0.0.1:8080/stat?start_date=10.10.2021&end_date=10.10.2022&codes=RUB%20EUR%20CNY%20INR%20BRL%20BGN%20CAD%20DKK%20HKD%20HUF%20ISK%20ILS%20PHP%20PLN
	@GetMapping("/stat")
	public ResponseEntity<MultiResponse<CurrencyResponse>> stat(@RequestParam("start_date") String start_date,
			@RequestParam("end_date") String end_date, @RequestParam("codes") String codes_)
	{
		try
		{
			LinkedList<CurrencyResponse> r = new LinkedList<>();
			LocalDate from = LocalDate.parse(start_date, formatter);
			LocalDate to = LocalDate.parse(end_date, formatter);

			if (to.isAfter(LocalDate.now()))
				throw new IllegalArgumentException("end date in future");

			if (to.isBefore(from))
				throw new IllegalArgumentException("end date < start date");

			if (codes_.isBlank())
				throw new IllegalArgumentException("codes empty");

			String[] codes = codes_.split("\\ ");
			if (new HashSet<>(Arrays.asList(codes)).size() != codes.length)
				throw new IllegalArgumentException("codes duplicate");
			
			for (String code : codes)
			{
				if (currency_info_repository.getMin(code, from, to) == null)
					throw new IllegalArgumentException("no data for " + code);

				r.add(new CurrencyResponse(code, currency_info_repository.getMin(code, from, to),
						currency_info_repository.getMax(code, from, to),
						currency_info_repository.getAvg(code, from, to)));
			}

			return ResponseEntity.ok(new MultiResponse<>(r));
		} catch (Throwable t)
		{
			return (ResponseEntity<MultiResponse<CurrencyResponse>>) (Object) ResponseEntity.badRequest()
					.body(t.getMessage());
		}
	}

}
