package mobile.cross.itm.utils;

import java.io.File;
import android.content.Context;

public class ImageFileCache {

	private File cacheDir;

	public ImageFileCache(Context context) {
		// Find the dir to save cached images
		if (android.os.Environment.getExternalStorageState().equals(android.os.Environment.MEDIA_MOUNTED)){
			//cacheDir = new File(android.os.Environment.getExternalStorageDirectory(),"CMCacheFileStorage");
			cacheDir = context.getCacheDir();
		}
		else{
			cacheDir = context.getCacheDir();
		}
		if (!cacheDir.exists()){
			cacheDir.mkdirs();
		}
	}

	public File getFile(String url) {
		String filename = String.valueOf(url.hashCode());
		// String filename = URLEncoder.encode(url);
		File f = new File(cacheDir, filename);
		return f;
	}

	public void clear() {
		File[] files = cacheDir.listFiles();
		if (files == null)
			return;
		for (File f : files)
			f.delete();
	}
	
	public void clear(String url) {
		String filename = String.valueOf(url.hashCode());
		// String filename = URLEncoder.encode(url);
		File f = new File(cacheDir, filename);
		f.delete();
	}

}