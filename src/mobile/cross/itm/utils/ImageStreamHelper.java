package mobile.cross.itm.utils;
import java.io.InputStream;
import java.io.OutputStream;

public class ImageStreamHelper {
    public static void CopyStream(InputStream is, OutputStream os){

        try{
        	final int buffer_size = is.available();
            byte[] bytes=new byte[buffer_size];
            while(true){
              int count = is.read(bytes, 0, buffer_size);
              if(count==-1){
                  break;
              }
              os.write(bytes, 0, count);
              //os.flush();
            }
            os.flush();
        }catch(Exception ex){
        	ex.printStackTrace();
        }
    }
}