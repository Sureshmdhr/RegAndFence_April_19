package com.suresh.reporting;

import java.util.ArrayList;
import java.util.List;

import org.apache.http.NameValuePair;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.DialogInterface.OnShowListener;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.suresh.extras.GPSTracker;
import com.suresh.extras.GridAdapter;
import com.suresh.form.R;

public class Reporting_pg2 extends Activity
{
	private GridView impact_gridview;
	private GridAdapter adapter;
	private ImageButton img_next;
	private ImageButton img_prev;
	private TextView skip;
	private AlertDialog alertDialog;
	private ArrayList<Integer> positions=new ArrayList<Integer>();
	private JSONArray impact_Array=new JSONArray();
	private String photo_name;
	private String parent_name;
	public static JSONObject reporting;
	
	@SuppressLint("InflateParams")
	protected void onCreate(Bundle savedInstanceState)
	{
		setContentView(R.layout.reporting_pg2);
		super.onCreate(savedInstanceState);
		
		Bundle extra = getIntent().getExtras();
		
		try 
		{
			reporting=new JSONObject(extra.getString("reporting"));
			parent_name=reporting.getJSONObject("ReportItemIncident").getString("item_name");
			Log.i("rep_2", reporting.toString());
		}
		catch (JSONException e1) 
		{
			e1.printStackTrace();
		}
		TextView tv_eventtitle=(TextView)findViewById(R.id.event_title);
		tv_eventtitle.setText(Reporting_pg1.disaster_incident);

		impact_gridview=(GridView)findViewById(R.id.impact_gridView);
		FileCache fileCache = new FileCache(Reporting_pg2.this);
		ArrayList<String> impact_array = fileCache.getFromFile(parent_name,"incident","impact");
			
		adapter=new GridAdapter(Reporting_pg2.this,impact_array);
		impact_gridview.setAdapter(adapter);
		impact_gridview.setOnItemClickListener(new OnItemClickListener() 
		{
			public void onItemClick(AdapterView<?> arg0, View arg1, final int pos,
					long arg3) 
			{
			    AlertDialog.Builder builder = new AlertDialog.Builder(Reporting_pg2.this);
			    LayoutInflater inflater = Reporting_pg2.this.getLayoutInflater();
			    final View view = inflater.inflate(R.layout.impact_choose, null);
			    builder.setView(view);
			    builder.setTitle("Information: "+adapter.getItem(pos));
			    builder.setPositiveButton("OK", null);
			    builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() 
			    {
			        public void onClick(DialogInterface dialog, int whichButton) 
			        {
			        	dialog.cancel();
			        }
			    });
			    alertDialog = builder.create();
			    alertDialog.setOnShowListener(new OnShowListener() 
			    {
					
					@Override
					public void onShow(final DialogInterface dialog)
					{
						Button ok=((AlertDialog) dialog).getButton(AlertDialog.BUTTON_POSITIVE);
						ok.setOnClickListener(new OnClickListener()
						{
							public void onClick(View arg0) 
							{
								EditText et_count = (EditText)view.findViewById(R.id.count);
								EditText et_unit =(EditText)view.findViewById(R.id.unit);
								EditText et_describe =(EditText)view.findViewById(R.id.describe);
								
								String count = et_count.getText().toString();
								String describe = et_describe.getText().toString();
								String unit = et_unit.getText().toString();
								if(count.equals("")||unit.equals(""))
								{
									Toast.makeText(getApplicationContext(), "Empty Fields", Toast.LENGTH_SHORT).show();
								}
								else
								{
									Log.i("extra", count+"\n"+describe+"\n"+unit);
									adapter.setKeyvalue(pos, "count", count);
									adapter.setKeyvalue(pos, "unit", unit);
									adapter.setKeyvalue(pos, "describe", describe);
									if(!positions.contains(pos))
										positions.add(pos);
									dialog.dismiss();
								}
							}
						});
					}
				});

			    alertDialog.show();
			}
							
		});
		
		img_next=(ImageButton)findViewById(R.id.imageButton2);
		img_next.setOnClickListener(new OnClickListener()
		{

			private JSONObject impact_Object=null;

			public void onClick(View arg0) 
			{
				if(positions.size()!=0)
				{
					 impact_Array=new JSONArray();
				 	 impact_Object=new JSONObject();
				 	 for(int i=0;i<positions.size();i++)
					{
						List<NameValuePair> impacts = adapter.getKeyvalue(positions.get(i));
						JSONObject jsonObject=new JSONObject();
						try 
						{
							GPSTracker gps=new GPSTracker(Reporting_pg2.this);
							//jsonObject.put("id",i+1);
							jsonObject.put("item_name", adapter.getItem(i));
							jsonObject.put("magnitude", impacts.get(0).getValue());
							jsonObject.put("timestamp_occurance",new FileCache(Reporting_pg2.this).getDate());
							jsonObject.put("latitude", gps.getLatitude());
							jsonObject.put("longitude",gps.getLongitude());
							jsonObject.put("units", impacts.get(1).getValue());
							jsonObject.put("description", impacts.get(2).getValue());
							impact_Object.put(String.valueOf(i+1),jsonObject);
							impact_Array.put(jsonObject);
						}
						catch (JSONException e) 
						{
							e.printStackTrace();
						}
					}
				}
				try 
				{
					reporting.put("ReportItemImpact",impact_Object);
				}
				catch (JSONException e) 
				{
					e.printStackTrace();
				}

				Intent intent=new Intent(getApplicationContext(), Reporting_pg3.class);
				intent.putExtra("reporting", reporting.toString());
				startActivity(intent);
				finish();
			}
		});
		img_prev=(ImageButton)findViewById(R.id.rep_upload);
		img_prev.setOnClickListener(new OnClickListener() 
		{
			public void onClick(View arg0)
			{
				Intent intent=new Intent(getApplicationContext(), Reporting_pg1.class);
				intent.putExtra("reporting", reporting.toString());
				startActivity(intent);
				finish();
			}
		});
		skip=(TextView)findViewById(R.id.skip);
		skip.setOnClickListener(new OnClickListener()
		{
			public void onClick(View arg0) 
			{
				Intent intent=new Intent(getApplicationContext(), Reporting_pg3.class);
				intent.putExtra("reporting", reporting.toString());
				startActivity(intent);
				finish();
			}
		});
	}
	
	public void onBackPressed() 
	{
		try
		{
			JSONObject photo=reporting.getJSONObject("Photo");
			if(photo.getBoolean("photo_taken"))
			{
				photo_name=photo.getString("name");
				new FileCache(Reporting_pg2.this).deletefile(photo_name);
				Log.i("Photo_deleted", "yes");
			}
			else
			{
				Log.i("Photo", "no");
			}
		}
		catch (JSONException e) 
		{
			e.printStackTrace();
		}
		finish();
		Intent intent=new Intent(getApplicationContext(), Reporting_pg1.class);
		startActivity(intent);
	}

}
