package second.test.joolmera;

public class Panorama {
    static 
    {
    	try
    	{ 
    		// Load necessary libraries.
    		System.loadLibrary("opencv_java");
    		System.loadLibrary("nonfree");
    		System.loadLibrary("nonfree_jni");
    	}
    	catch( UnsatisfiedLinkError e )
		{
           System.err.println("Native code library failed to load.\n" + e);		
		}
    }

    public static native int makePanorama(int panorama_mode, int feature, int num_img, int devideBy,  String folder_path, String saveFileName);
}
