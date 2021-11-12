package com.protech.inc.world;
 

//imported classes
import android.app.Activity;
import android.os.Bundle;
import android.widget.Spinner;
import android.widget.ImageView;
import java.util.ArrayList;
import android.widget.Toast;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import java.util.HashMap;
import android.widget.SpinnerAdapter;
import android.widget.BaseAdapter;
import android.view.View;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.content.Context;
import android.widget.ArrayAdapter;
import android.graphics.drawable.GradientDrawable;
import android.widget.Button;
import android.widget.EditText;
import android.graphics.Color;
import android.widget.AdapterView;


public class MainActivity extends Activity { 

private Spinner languageFrom;  //spinner language from
private Spinner languageTo;    //spinner language to
private ImageView swap;        //swap imageview not yet code
private Button translate;      //translate button
private EditText input;        //text input edittext
private TextView translated;   //translated textview
private String protech;        //string that collect data from asset language json
private String codeFrom;       //language code we get this on spinner languageFrom item selected
private String codeTo;         //language code we get this on spinner languageTo item selected
private String api;            //string api uri
private String from;           
private String to;
private String text;
private String result;         //our result from server 
private RequestNetwork rn;     
private RequestNetwork.RequestListener my_request_listener;
private ArrayList<HashMap<String,Object>> lmap = new ArrayList<HashMap<String,Object>>();
private HashMap<String, Object> param = new HashMap<>();
private android.graphics.drawable.GradientDrawable dd = new android.graphics.drawable.GradientDrawable();
     
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
		rn = new RequestNetwork(this);
		
//widget and views ID
 languageFrom = findViewById(R.id.language_from);
 languageTo = findViewById(R.id.language_to);
 translated = findViewById(R.id.translated);
 translate = findViewById(R.id.translate_button);
 input = findViewById(R.id.input);
 swap = findViewById(R.id.swap);
 
 
 
 
    //button and edittext design
   dd.setColor(Color.parseColor("#2196f3"));
   dd.setCornerRadius(15);
   dd.setStroke(3,Color.parseColor("#cccccc"));
   translate.setElevation(5);
   input.setElevation(5);
   translate.setBackground(dd);
 
 
		//load language.json from assets start
		try {
			java.io.InputStream is = this.getAssets().open("Language.json");
			int size = is.available();
			byte[] buffer = new byte[size];
			is.read(buffer);
			is.close();
			protech = new String(buffer, "UTF-8");
			lmap = new Gson().fromJson(protech, new TypeToken<ArrayList<HashMap<String, Object>>>(){}.getType());
			languageFrom.setAdapter(new MyAdapter(lmap));
			languageTo.setAdapter(new MyAdapter(lmap));
			((ArrayAdapter)languageFrom.getAdapter()).notifyDataSetChanged();
			((ArrayAdapter)languageTo.getAdapter()).notifyDataSetChanged();
			}catch(Exception e){
				
			}
		//load language from assets end
			
		
		    //spinner languageFrom on item selected
			languageFrom.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener(){
				@Override
				public void onItemSelected(AdapterView<?> parent,View v,int position,long id){
					
					codeFrom = lmap.get(position).get("code").toString();
				}
				@Override
				public void onNothingSelected(AdapterView<?> parent){
					
					Toast.makeText(getApplicationContext(),"Nothing was selected",Toast.LENGTH_SHORT).show();
					
				}
			});
		//spinner languageTo on item selected
		languageTo.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener(){
				@Override
				public void onItemSelected(AdapterView<?> parent,View v,int position,long id){
					
					codeTo = lmap.get(position).get("code").toString();
				}
				@Override
				public void onNothingSelected(AdapterView<?> parent){

					Toast.makeText(getApplicationContext(),"Nothing was selected",Toast.LENGTH_SHORT).show();

				}
			});
			
			//on translate button click
			translate.setOnClickListener(new View.OnClickListener(){
				@Override
				public void onClick(View vi){
					
					param.put("User-Agent", "Mozilla/5.0");
					text = input.getText().toString();
					from = codeFrom;
					to = codeTo;
					try {
						api = "https://translate.googleapis.com/translate_a/single?client=gtx&sl="+from+"&tl="+to+"&dt=t&q="+java.net.URLEncoder.encode(text, "utf-8");
					} catch (Exception e) {
						
						return;
					}
					rn.setParams(param, RequestNetworkController.REQUEST_PARAM);
					rn.startRequestNetwork(RequestNetworkController.GET, api, "hidepain", my_request_listener);
				
				}
			});
		    my_request_listener = new RequestNetwork.RequestListener() {
			@Override
			public void onResponse(String param1, String param2, HashMap<String, Object> param3) {
				final String tag = param1;
				final String response = param2;
				final HashMap<String, Object> responseHeaders = param3;
				try {
					result = new org.json.JSONArray(response).getJSONArray(0).getJSONArray(0).getString(0);
				} catch (org.json.JSONException e) {
					result = "Server sent an invalid response";
				}
				translated.setText(result);
			}
			@Override
			public void onErrorResponse(String param1, String param2) {
				final String tag = param1;
				final String message = param2;
		
			}
		};
        
    }
	
	//subclass for spanner adapter
	public class MyAdapter extends BaseAdapter {
		ArrayList<HashMap<String, Object>> data;
		public MyAdapter(ArrayList<HashMap<String, Object>> raa) {
			data = raa;
		}

		@Override
		public int getCount() {
			return data.size();
		}

		@Override
		public HashMap<String, Object> getItem(int index) {
			return data.get(index);
		}

		@Override
		public long getItemId(int index) {
			return index;
		}
		@Override
		public View getView(final int position, View v, ViewGroup container) {
			LayoutInflater inflater = (LayoutInflater)getBaseContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			View view = v;
			if (view == null) {
				view = inflater.inflate(R.layout.list, null);
			}

			final LinearLayout line = view.findViewById(R.id.line);
			final TextView name = view.findViewById(R.id.name);

			name.setText(lmap.get(position).get("name").toString());
			Animation animation;animation = AnimationUtils.loadAnimation(
				getApplicationContext(),android.R.anim.slide_in_left);animation.setDuration(200);line.startAnimation(animation); animation = null;
			

			return view;
		}
} 

}
