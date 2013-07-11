/*
 *
 *  Copyright (C) Roberto Calvo Palomino
 *
 *  This program is free software: you can redistribute it and/or modify
 *  it under the terms of the GNU General Public License as published by
 *  the Free Software Foundation, either version 3 of the License, or
 *  (at your option) any later version.
 *
 *  This program is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU General Public License for more details.
 *
 *  You should have received a copy of the GNU General Public License
 *  along with this program.  If not, see http://www.gnu.org/licenses/. 
 *
 *  Author : Roberto Calvo Palomino <rocapal at gmail dot com>
 *
 */

package es.pentalo.apps.RBPiCameraControl;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import android.app.Activity;
import android.app.Fragment;
import android.app.ProgressDialog;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;
import es.pentalo.apps.RBPiCameraControl.R;
import es.pentalo.apps.RBPiCameraControl.API.Command;
import es.pentalo.apps.RBPiCameraControl.API.RBPiCamera;

public class PhotoFragment extends Fragment {
	
	private final String TAG = getClass().getSimpleName();
	
	private View myView;
	private ImageView ivPhoto;
	final List<Command> lCommands = new ArrayList<Command>();
	
	@Override
	public void onAttach(Activity activity) {
		super.onAttach(activity);	
	}


	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {

		myView = inflater.inflate(R.layout.photo, container, false);
		
		ivPhoto = (ImageView) myView.findViewById(R.id.ivPhoto);
		
		Button btImage = (Button) myView.findViewById(R.id.btShot);
		btImage.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {				
				takePicture();
			}
		});
		
		return myView;
	}
	
	private void takePicture ()
	{
		
		SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(getActivity());
		
		lCommands.clear();
		Map<String,?> keys = prefs.getAll();		
		
		for(Map.Entry<String,?> entry : keys.entrySet())
		{
			Log.d("map values",entry.getKey() + ": " + entry.getValue().toString());
			
			if (entry.getKey().startsWith("photo_"))
			{
				Command c = new Command();
				c.name = entry.getKey().split("_")[1];
				c.argument = entry.getValue().toString();
								
				lCommands.add(c);
			}
			
		}
		
		new GetPhotoTask().execute();
	
	}
	
	public class GetPhotoTask extends AsyncTask<Void, Void, Bitmap>
	{
		ProgressDialog pd;
		
		@Override
		protected void onPreExecute() {
			// TODO Auto-generated method stub
			super.onPreExecute();
			
			pd = ProgressDialog.show(getActivity(), getActivity().getString(R.string.pd_title_photo), getActivity().getString(R.string.pd_message_photo));
		}
		
		@Override
		protected Bitmap doInBackground(Void... params) {
			final RBPiCamera rbpiCamera = new RBPiCamera(getString(R.string.rbpi_url));
			return rbpiCamera.shotPhoto(lCommands);
			
		}
	
		@Override
		protected void onPostExecute(Bitmap result) {
			// TODO Auto-generated method stub
			super.onPostExecute(result);
			
			if (pd.isShowing())
				pd.dismiss();
			
			if (result != null)
				ivPhoto.setImageBitmap(result);
			else
				Toast.makeText(getActivity(), getActivity().getString(R.string.toast_error_photo), Toast.LENGTH_LONG).show();
				
		}
	}

	
}
