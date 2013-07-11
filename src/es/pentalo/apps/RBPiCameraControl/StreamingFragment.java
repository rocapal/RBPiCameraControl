package es.pentalo.apps.RBPiCameraControl;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import android.app.Activity;
import android.app.Fragment;
import android.app.ProgressDialog;
import android.content.ActivityNotFoundException;
import android.content.ComponentName;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;
import es.pentalo.apps.RBPiCameraControl.API.Command;
import es.pentalo.apps.RBPiCameraControl.API.RBPiCamera;

public class StreamingFragment  extends Fragment {
	
    private final String TAG = getClass().getSimpleName();
	private View myView;
	private final List<Command> lCommands = new ArrayList<Command>();
	
	private RBPiCamera rbpiCamera;
	 
	private final int REQUEST_CODE_VLC = 100001;
	
	@Override
	public void onAttach(Activity activity) {
		// TODO Auto-generated method stub
		super.onAttach(activity);
	}
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		
		myView = inflater.inflate(R.layout.streaming, container, false);
		
		Button btStart = (Button) myView.findViewById(R.id.btStartStreaming);
		btStart.setOnClickListener( new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				
				startStreaming();
			}
		});
		
		rbpiCamera = new RBPiCamera(getString(R.string.rbpi_url));
		
		return myView;
	}
	
	@Override
	public void onDestroy() {
		// TODO Auto-generated method stub
		super.onDestroy();
		
		stopStreaming();
	}
	
	private void startStreaming()
	{
		
			
		SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(getActivity());

		lCommands.clear();
		Map<String,?> keys = prefs.getAll();		

		for(Map.Entry<String,?> entry : keys.entrySet())
		{
			Log.d("map values",entry.getKey() + ": " + entry.getValue().toString());

			if (entry.getKey().startsWith("video_"))
			{
				Command c = new Command();
				c.name = entry.getKey().split("_")[1];
				c.argument = entry.getValue().toString();

				lCommands.add(c);
			}

		}

		new GetStreamingURL().execute();		
		
	}
	
	private void stopStreaming()
	{
		Thread th = new Thread() {				
			@Override
			public void run() {
				Boolean res = rbpiCamera.stopStreaming();
				Log.d(TAG, "StopStreaming = " + res.toString());				
			}
		};
		
		th.start();
		
	}
	
		
	
	public class GetStreamingURL extends AsyncTask<Void, String, String>
	{
		ProgressDialog pd;
		
		@Override
		protected void onPreExecute() {
			// TODO Auto-generated method stub
			super.onPreExecute();
			
			pd = ProgressDialog.show(getActivity(), getActivity().getString(R.string.pd_title_streaming), getActivity().getString(R.string.pd_message_streaming));
		}
		
		@Override
		protected void onProgressUpdate(String... values) {
			// TODO Auto-generated method stub
			super.onProgressUpdate(values);
			pd.setMessage(values[0]);
			
		}
		
		@Override
		protected String doInBackground(Void... params) {
						
			String url =  rbpiCamera.startStreaming(lCommands);
			if (url != null)
			{
				publishProgress(getString(R.string.pd_message_streaming_wait));
				
				try {
					Thread.sleep(5000);
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
			
			return url;
		}
		
		@Override
		protected void onPostExecute(String result) {			
			super.onPostExecute(result);
			
			if (pd.isShowing())
				pd.dismiss();
				
			if (result != null)
			{
				Log.d(TAG, result);
				launchVLC(result);
			}
			else
				Toast.makeText(getActivity(), getString(R.string.toast_error_streaming), Toast.LENGTH_SHORT).show();
		}
	}
		
	
	private void launchVLC (String url)
	{
		try{
			Intent i = new Intent(Intent.ACTION_VIEW);
			i.setComponent(new ComponentName("org.videolan.vlc.betav7neon", "org.videolan.vlc.betav7neon.gui.video.VideoPlayerActivity"));			
			i.setData(Uri.parse(url));			
			startActivityForResult(i, REQUEST_CODE_VLC);
		} 
		catch (ActivityNotFoundException e){
			Uri uri = Uri.parse("http://play.google.com/store/apps/details?id=org.videolan.vlc.betav7neon");
			Intent intent = new Intent (Intent.ACTION_VIEW, uri); 
			startActivity(intent);
		}		
	}
	
	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		// TODO Auto-generated method stub
		super.onActivityResult(requestCode, resultCode, data);
		
		if (requestCode == REQUEST_CODE_VLC)
		{
			stopStreaming();			
		}
	}

}
