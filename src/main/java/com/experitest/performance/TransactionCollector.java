package com.experitest.performance;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Objects;

import org.apache.commons.lang.StringEscapeUtils;
import org.openqa.selenium.Capabilities;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.remote.RemoteWebDriver;

import com.experitest.performance.Transaction.Status;

public class TransactionCollector {
	SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
	
	private String testName;
	private LinkedHashMap<String, Transaction> transactions = new LinkedHashMap<>();
	private ArrayList<Transaction> endTransactions = new ArrayList<>();
	private long start = System.currentTimeMillis();
	private String platformName;
	private String deviceName;
	private String serialNumber;
	private String manufacture;
	private String category;
	private String version;

	
	public String getDeviceName() {
		return deviceName;
	}
	public void setDeviceName(String deviceName) {
		this.deviceName = deviceName;
	}
	public TransactionCollector(WebDriver driver, String testName) {
		this.testName = testName;
		if(driver instanceof RemoteWebDriver) {
			Capabilities cp = ((RemoteWebDriver)driver).getCapabilities();
			Map<String, Object> map = cp.asMap();
			if(testName == null) {
				if(map.containsKey("testName")) {
					this.testName = String.valueOf(map.get("testName").toString());
				} else {
					this.testName = "No test name was provided";
				}
			}
			platformName = Objects.toString(map.get("platformName"),"");
			serialNumber = Objects.toString(map.get("device.serialNumber"),"");
			manufacture = Objects.toString(map.get("device.manufacture"),"");
			deviceName = Objects.toString(map.get("device.name"), "");
			category = Objects.toString(map.get("device.category"), "");
			version = Objects.toString(map.get("device.version"), "");
			
			
			
		}
	}
	public Transaction startTransaction(String name, String tags, String parameters) {
		if(transactions.containsKey(name)) {
			endTransaction(name,Status.INCOMPLETE);
		}
		Transaction t = new Transaction(name, tags, parameters);
		transactions.put(name, t);
		
		return t;
	}
	
	public Transaction endTransaction(String name, Status status) {
		Transaction t = transactions.remove(name);
		if(t == null) {
			throw new RuntimeException("Transaction named: " + name + " wasn't started");
		}
		t.end(status);
		endTransactions.add(t);
		return t;
	}
	
	public void toCsvFile(File fileName) throws IOException{
		long end = System.currentTimeMillis();
		
		for(Transaction t: transactions.values().toArray(new Transaction[0])) {
			endTransaction(t.getName(), Status.INCOMPLETE);
		}
		
		StringBuffer buf = new StringBuffer();
		buf.append("Time: "); buf.append(sdf.format(new Date(start))); buf.append(" ("); buf.append(String.valueOf(((int)((end - start)/1000)))); buf.append(" s)"); buf.append("\n");
		buf.append(testName); buf.append("\n");
		if(deviceName != null) {
			buf.append(deviceName); buf.append(" ");
		}
		String usedDeviceName = platformName + " " + deviceName + " ver: " + version + " SN: " + serialNumber;
		buf.append(usedDeviceName);buf.append("\n");
		ArrayList<String> columns = new ArrayList<>();
		columns.add("Transaction Name");
		LinkedHashSet<String> keys = new LinkedHashSet<>();
		for(Transaction t: endTransactions) {
			if(t.getTags() != null) {
				String[] tags = t.getTags().split(",");
				if(tags != null) {
					for(String tag: tags) {
						String[] values = tag.split("=");
						keys.add(values[0]);
					}
				}
			}
		}
		for(String key: keys) {
			columns.add(key);
		}
		columns.add("Time Stamp");
		columns.add("Status");
		columns.add("UX Time (ms)");
		columns.add("Parameters");
		for(String c: columns) {
			buf.append(StringEscapeUtils.escapeCsv(c));
			buf.append(",");
		}
		buf.setLength(buf.length() - 1);
		buf.append("\n");
		for(Transaction t: endTransactions) {
			buf.append(StringEscapeUtils.escapeCsv(t.getName())); buf.append(",");
			for(String key: keys) {
				buf.append(StringEscapeUtils.escapeCsv(t.getKeyValue(key)));
				buf.append(",");
			}
			buf.append(sdf.format(new Date(t.getStarttime())));
			buf.append(",");
			buf.append(t.getStatus()); buf.append(",");
			buf.append(t.getEndtime() - t.getStarttime()); buf.append(",");
			buf.append(t.getParameters());
			buf.append("\n");
		}
		FileWriter writer = new FileWriter(fileName, fileName.exists());
		writer.write(buf.toString());
		writer.close();
		
	}
}
