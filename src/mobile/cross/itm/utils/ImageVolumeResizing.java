package mobile.cross.itm.utils;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;

import org.apache.commons.io.IOUtils;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.util.Log;
import android.widget.ImageView;

public class ImageVolumeResizing {

	public static final String TAG = ImageVolumeResizing.class.getSimpleName();

	public static byte[] getByteArrayFromFile(Context context, Uri uri, ImageView image) {
		byte[] fileBytes = null;
		InputStream inStream = null;
		ByteArrayOutputStream outStream = null;

		if (uri.getScheme().equals("content")) {
			try {
				
				// Test
				// Picasso.with(context).load(uri).resize(640, 640).centerCrop().noFade().into(image);
				
				inStream = context.getContentResolver().openInputStream(uri);
				outStream = new ByteArrayOutputStream();

				byte[] bytesFromFile = new byte[1024*1024*5]; // buffer size (1 MB)
				int bytesRead = inStream.read(bytesFromFile);
				while (bytesRead != -1) {
					outStream.write(bytesFromFile, 0, bytesRead);
					//outStream.flush();
					bytesRead = inStream.read(bytesFromFile);
				}

				fileBytes = outStream.toByteArray();
			}catch (IOException e) {
				Log.e(TAG, e.getMessage());
			}finally {
				try {					
					outStream.close();
					inStream.close();
				}
				catch (IOException e) { /*( Intentionally blank */ }
			}
		}
		else {
			try {
				File file = new File(uri.getPath());
				FileInputStream fileInput = new FileInputStream(file);
				fileBytes = IOUtils.toByteArray(fileInput);
			}
			catch (IOException e) {
				Log.e(TAG, e.getMessage());
			}
		}

		return fileBytes;
	}

	public static byte[] reduceImageForUpload(byte[] imageData) {

		BitmapFactory.Options options = new BitmapFactory.Options();

		// inSampleSize is used to sample smaller versions of the image
		// options.inSampleSize = 4; //tragedy

		// Decode bitmap with inSampleSize and target dimensions set
		options.inJustDecodeBounds = false;	

		options.inPurgeable = true;

		Bitmap rb = BitmapFactory.decodeByteArray(imageData, 0, imageData.length, options);

		double x, y;
		if(rb.getWidth() > rb.getHeight()){
			x = 700f;
			y = 800f/((double)rb.getWidth()/(double)rb.getHeight());
			Log.d("Bitmap size", x + ", " + y);
		}else{
			x = 700f;
			y = 800f/((double)rb.getWidth()/(double)rb.getHeight());
			Log.d("Bitmap size", x + ", " + y);
		}


		//Bitmap resizedBitmap = Bitmap.createScaledBitmap(reducedBitmap, reducedBitmap.getWidth()/3, reducedBitmap.getHeight()/3, false);
		Bitmap resizedBitmap = Bitmap.createScaledBitmap(rb, (int)x, (int)y, true);
		//Bitmap resizedBitmap = Bitmap.createScaledBitmap(reducedBitmap, reducedBitmap.getWidth()/2, reducedBitmap.getHeight()/2, false);
		//Bitmap resizedBitmap = Bitmap.createBitmap(reducedBitmap);

		ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
		resizedBitmap.compress(Bitmap.CompressFormat.JPEG, 60, outputStream);
		byte[] reducedData = outputStream.toByteArray();
		try {
			outputStream.close();
		}
		catch (IOException e) {
			// Intentionally blank
		}

		return reducedData;
	}

	/*public static String getFileName(Context context, Uri uri, String fileType) {
		String fileName = "uploaded_file.";

		if (fileType.equals("image")) {
			fileName += "jpg";
		}
		else {
			// For video, we want to get the actual file extension
			if (uri.getScheme().equals("content")) {
				// do it using the mime type
				String mimeType = context.getContentResolver().getType(uri);
				int slashIndex = mimeType.indexOf("/");
				String fileExtension = mimeType.substring(slashIndex + 1);
				fileName += fileExtension;
			}
			else {
				fileName = uri.getLastPathSegment();
			}
		}

		return fileName;
	}*/
}