package apo.controllers;

import java.util.List;

public class MultiResponse<T>
{

	private List<T> list;

	public MultiResponse(List<T> list)
	{
		this.list = list;
	}

	public List<T> getList()
	{
		return list;
	}

	public void setList(List<T> list)
	{
		this.list = list;
	}

}
