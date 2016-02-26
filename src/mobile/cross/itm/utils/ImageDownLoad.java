package mobile.cross.itm.utils;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Collections;
import java.util.Map;
import java.util.WeakHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import mobile.cross.itm.crossmobile.R;
import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Handler;
import android.util.Log;
import android.widget.ImageView;

public class ImageDownLoad {

	CacheMemoryManaging memoryCache = new CacheMemoryManaging();
	ImageFileCache fileCache;
	private Map<ImageView, String> imageViewsMap = Collections.synchronizedMap(new WeakHashMap<ImageView, String>());
	
	ExecutorService executorService;

	Handler handler = new Handler();
	Context context;

	public ImageDownLoad(Context context) {
		fileCache = new ImageFileCache(context);
		executorService = Executors.newFixedThreadPool(3);
		this.context = context;
	}

	final int stub_id = R.drawable.ic_action_camera;

	@SuppressLint("NewApi")
	public void DisplayImage(String url, ImageView imageView) {
		imageViewsMap.put(imageView, url);
		Bitmap bitmap = memoryCache.get(url);

		if (bitmap != null){
			imageView.setImageBitmap(bitmap);
		}
		else {
			queuePhoto(url, imageView);
			imageView.setImageResource(stub_id);
		}
	}

	private void queuePhoto(String url, ImageView imageView) {
		PhotoToLoad p = new PhotoToLoad(url, imageView);
		executorService.submit(new PhotosLoader(p));
	}

	private Bitmap getBitmap(String url) {

		HttpURLConnection conn = null;
		InputStream is = null;
		OutputStream os = null;

		File f = fileCache.getFile(url);
		
		// Test
		//Picasso.with(context).load(f).resize(640, 640).centerCrop().noFade().into(target);
		
		Bitmap b = decodeFile(f);
		Log.d("Bitmap Path", f.getPath());
		if (b != null && b.getWidth() != 0)
			return b;

		// else!
		// Download Images from the Internet
		try {
			Bitmap bitmap = null;
			URL imageUrl = new URL(url);
			conn = (HttpURLConnection) imageUrl.openConnection();
			conn.setDoInput(true);
			conn.setDoOutput(true);
			conn.setConnectTimeout(3000);
			conn.setReadTimeout(3000);
			conn.setInstanceFollowRedirects(true);
			is = conn.getInputStream();
			os = new FileOutputStream(f);

			//Test

			ImageStreamHelper.CopyStream(is, os);

			bitmap = decodeFile(f);
			return bitmap;
		} catch (Throwable ex) {
			ex.printStackTrace();
			Log.d("decodefile error", ex.getMessage());
			if (ex instanceof OutOfMemoryError)
				memoryCache.clear();
			return null;
		} finally{
			try {
				os.close();
				is.close();
				conn.disconnect();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}			
		}
	}

	// Decodes image and scales it to reduce memory consumption
	private Bitmap decodeFile(File f) {

		try {

			FileInputStream stream = new FileInputStream(f);

			BitmapFactory.Options options = new BitmapFactory.Options();

			options.inJustDecodeBounds = false;
			options.inPurgeable = true;
			Bitmap bitmap = BitmapFactory.decodeStream(stream, null, options);

			stream.close();
			Bitmap b = Bitmap.createBitmap(bitmap,0, 0, bitmap.getWidth(), bitmap.getHeight(), null, true);

			return b;

		} catch (FileNotFoundException e) {
		} catch (IOException e) {
			e.printStackTrace();
		}
		return null;
	}

	// Task for the queue
	private class PhotoToLoad {
		public String url;
		public ImageView imageView;

		public PhotoToLoad(String u, ImageView i) {
			url = u;
			imageView = i;
		}
	}

	class PhotosLoader implements Runnable {
		PhotoToLoad photoToLoad;

		PhotosLoader(PhotoToLoad photoToLoad) {
			this.photoToLoad = photoToLoad;
		}

		@Override
		public void run() {
			try {
				if (imageViewReused(photoToLoad))
					return;
				// else!
				Bitmap bmp = getBitmap(photoToLoad.url);
				bmp = Bitmap.createScaledBitmap(bmp, bmp.getWidth(), bmp.getHeight(), false);
				//Bitmap.createBitmap(bmp);
				memoryCache.put(photoToLoad.url, bmp);
				//if (imageViewReused(photoToLoad))
				//return;
				BitmapDisplayer bd = new BitmapDisplayer(bmp, photoToLoad);
				handler.post(bd);
			} catch (Throwable th) {
				th.printStackTrace();
			}
		}
	}

	boolean imageViewReused(PhotoToLoad photoToLoad) {
		String tag = imageViewsMap.get(photoToLoad.imageView);
		if (tag == null || !tag.equals(photoToLoad.url)){
			return true;
		}
		return false;
	}

	// Used to display bitmap in the UI thread
	class BitmapDisplayer implements Runnable {
		Bitmap bitmap;
		PhotoToLoad photoToLoad;

		public BitmapDisplayer(Bitmap b, PhotoToLoad p) {
			bitmap = b;
			photoToLoad = p;
		}

		public void run() {
			if (imageViewReused(photoToLoad))
				return;
			if (bitmap != null){
				photoToLoad.imageView.setImageBitmap(bitmap);
			}
			else{
				photoToLoad.imageView.setImageResource(stub_id);
			}
		}
	}

	public void clearCache() {
		memoryCache.clear();
		fileCache.clear();
	}

}