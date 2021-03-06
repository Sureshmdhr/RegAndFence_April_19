package com.suresh.geofence;

import java.util.ArrayList;
import java.util.concurrent.ExecutionException;

import org.json.JSONArray;
import org.json.JSONObject;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;

import com.suresh.network.Receiver;

public class Getdata {
	String uname;
	private Context mContext;
	
	public Getdata(String username,Context context)
	{
		this.uname=username;
		this.mContext=context;
	}

	public ArrayList<String> getData(String columnname)
	{
		ArrayList<String> result = null;
		Receiver connect=new Receiver();
		connect.setPath("extdb3.php");
		connect.addNameValuePairs("value1", this.uname);
		AsyncTask<Void, Void, String> output = connect.execute(new Void[0]);
		try 
		{
			 result=convertStringtoArrayList(output.get(),columnname);
		}
		catch (InterruptedException e) 
		{
			e.printStackTrace();
		}
		catch (ExecutionException e) 
		{
			e.printStackTrace();
		}
		return result;
	}
	
	public ArrayList<String> convertStringtoArrayList(String result,String column) 
	{
	    ArrayList<String> name = new ArrayList<String>();
	    try 
	    {
	    	JSONArray myjArray = new JSONArray(result);
	    	JSONObject myjson=null;
	    	for(int i=0; i<myjArray.length();i++){
	    		myjson = (JSONObject)myjArray.getJSONObject(i);
	    		name.add(myjson.getString(column));
	    	}
	    } 
	    catch (Exception e) 
	    {
	    	Log.e("log_tag11", "Error Parsing Data "+e.toString());
	    }
	    return name;
	}

	public ArrayList<String> getAdminData(String columnname)
	{
		ArrayList<String> result = null;
		Receiver connect=new Receiver();
		connect.setPath("activegeofence.php");
		connect.addNameValuePairs("value1", "admin");
		AsyncTask<Void, Void, String> output = connect.execute(new Void[0]);
		try 
		{
			 result=convertStringtoArrayList(output.get(),columnname);
		}
		catch (InterruptedException e) 
		{
			e.printStackTrace();
		}
		catch (ExecutionException e) 
		{
			e.printStackTrace();
		}
		return result;
	}
	
	protected ArrayList<String> getMyData(String column) 
	{
		Receiver connect=new Receiver(mContext);
		connect.setPath("activegeofence.php");
		connect.addNameValuePairs("value1", uname);
		AsyncTask<Void, Void, String> output = connect.execute(new Void[0]);
		String result=null;
		try 
		{
			result=output.get();
			return convertStringtoArrayList(result, column);
		}
		catch (InterruptedException e) 
		{
			e.printStackTrace();
		}
		catch (ExecutionException e)
		{
			e.printStackTrace();
		}
		return convertStringtoArrayList(result, column);
	}


}
