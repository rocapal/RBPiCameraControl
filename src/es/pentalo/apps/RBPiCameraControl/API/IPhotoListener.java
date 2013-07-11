package es.pentalo.apps.RBPiCameraControl.API;

import android.graphics.Bitmap;

public interface IPhotoListener {
	
	void photoSuccessfully (Bitmap photo);
	void photoFailed (String msg);

}
