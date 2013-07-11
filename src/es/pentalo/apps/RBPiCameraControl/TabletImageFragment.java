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

import android.app.Activity;
import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import es.pentalo.apps.RBPiCameraControl.Preferences.RBPreferenceFragment;
import es.pentalo.apps.RBPiCameraControl.Preferences.RBPreferenceFragment.RBPFeatures;

public class TabletImageFragment extends Fragment {

	View myView;
	
	@Override
	public void onAttach(Activity activity) {
		super.onAttach(activity);	
	}


	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {

		myView = inflater.inflate(R.layout.tablet_photo, container, false);
		
		RBPreferenceFragment ipFragment = new RBPreferenceFragment();		
		ipFragment.setFeature(RBPFeatures.PHOTO);
		getFragmentManager().beginTransaction().replace(R.id.llPreference, ipFragment).commit();
		getFragmentManager().beginTransaction().replace(R.id.llPhoto, new PhotoFragment()).commit();

		
		return myView;
	}
	
}
