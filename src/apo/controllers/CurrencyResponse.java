package apo.controllers;

public class CurrencyResponse
{
	private String code;
	private double min;
	private double max;
	private double avg;

	public CurrencyResponse(String code, Double min, Double max, Double avg)
	{
		super();
		this.code = code;
		this.min = min == null ? 0 : min;
		this.max = max == null ? 0 : max;
		this.avg = avg == null ? 0 : avg;
	}

	public String getCode()
	{
		return code;
	}

	public void setCode(String code)
	{
		this.code = code;
	}

	public double getMin()
	{
		return min;
	}

	public void setMin(double min)
	{
		this.min = min;
	}

	public double getMax()
	{
		return max;
	}

	public void setMax(double max)
	{
		this.max = max;
	}

	public double getAvg()
	{
		return avg;
	}

	public void setAvg(double avg)
	{
		this.avg = avg;
	}

}
