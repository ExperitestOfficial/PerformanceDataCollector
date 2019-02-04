package com.experitest.performance;

public class Transaction {
	public enum Status {
		SUCCESS, FAIL, INCOMPLETE;
	}
	private String tags;
	private String parameters;
	private String name;
	private long starttime = System.currentTimeMillis();
	private long endtime = -1;
	private Status status = Status.INCOMPLETE; 
	
	public Transaction(String name, String tags, String parameters) {
		this.name = name;
		this.tags = tags;
		this.parameters = parameters;
	}
	
	public String getParameters() {
		return parameters == null? "": parameters;
	}

	public void end(Status status) {
		this.status = status;
		this.endtime = System.currentTimeMillis();
	}
	
	public long getStarttime() {
		return starttime;
	}
	public Status getStatus() {
		return status;
	}
	public String getTags() {
		return tags;
	}
	public String getName() {
		return name;
	}
	public long getEndtime() {
		return endtime;
	}

	public String getKeyValue(String key) {
		if(tags == null) {
			return "";
		}
		String[] tagsSplit = tags.split(",");
		for(String t: tagsSplit) {
			if(t.startsWith(key + "=")) {
				return t.substring(key.length() + 1);
			}
		}
		return "";
	}
}
