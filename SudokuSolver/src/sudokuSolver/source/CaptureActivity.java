package sudokuSolver.source;

import java.util.List;

import org.opencv.android.BaseLoaderCallback;
import org.opencv.android.CameraBridgeViewBase;
import org.opencv.android.OpenCVLoader;
import org.opencv.android.CameraBridgeViewBase.CvCameraViewFrame;
import org.opencv.android.LoaderCallbackInterface;
import org.opencv.android.CameraBridgeViewBase.CvCameraViewListener2;
import org.opencv.core.CvType;
import org.opencv.core.Mat;
import org.opencv.imgproc.Imgproc;

import sudokuSolver.source.R;
import sudokuSolver.source.R.id;
import sudokuSolver.source.R.layout;

import android.support.v7.app.ActionBarActivity;
import android.app.Activity;
import android.graphics.Point;
import android.hardware.Camera;
import android.hardware.Camera.Parameters;
import android.hardware.Camera.Size;
import android.os.Bundle;
import android.util.Log;
import android.view.Display;
import android.view.Menu;
import android.view.MenuItem;
import android.view.SurfaceView;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.Toast;

public class CaptureActivity extends Activity implements CvCameraViewListener2{
	private static final String TAG = "SudokuSolver::CaptureActivity";
	
	private PortraitCameraView mOpenCvCameraView;
    private boolean              mIsJavaCamera = true;
    private MenuItem             mItemSwitchCamera = null;

	private BaseLoaderCallback mLoaderCallback = new BaseLoaderCallback(this) {
        @Override
        public void onManagerConnected(int status) {
            switch (status) {
                case LoaderCallbackInterface.SUCCESS:
                {
                    Log.i(TAG, "OpenCV loaded successfully");
                    mOpenCvCameraView.enableView();
                    
                } break;
                default:
                {
                    super.onManagerConnected(status);
                } break;
            }
        }
    };
    
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		Log.i(TAG, "OnCreate Called");
		super.onCreate(savedInstanceState);
		//Remove title bar
	    this.requestWindowFeature(Window.FEATURE_NO_TITLE);

	    //Remove notification bar
	    this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);

	   
	   //set content view AFTER ABOVE sequence (to avoid crash)
		setContentView(R.layout.activity_capture);
		
		
		
		 Display display = getWindowManager().getDefaultDisplay();
		    Point size = new Point();
		    display.getSize(size);
		    int height = size.y;
		    int width = size.x;
		    /*
		    RelativeLayout mainLayout = (RelativeLayout) findViewById(R.id.relativeLayout1); 
		 // get layout parameters for that view
	        
		    RelativeLayout.LayoutParams listLayoutParams = new RelativeLayout.LayoutParams(
                    height, width);
		    	mainLayout.setLayoutParams(listLayoutParams);*/
		
		 if (mIsJavaCamera)
	            mOpenCvCameraView = (PortraitCameraView) findViewById(R.id.java_surface_view);
	        else
	            mOpenCvCameraView = (PortraitCameraView) findViewById(R.id.native_surface_view);
		 	
	        mOpenCvCameraView.setVisibility(SurfaceView.VISIBLE);
	       // Log.i("isaac_debug", "pivx: " + mOpenCvCameraView.getPivotX());
	       // mOpenCvCameraView.setScaleX(.9f);
	        //mOpenCvCameraView.setScaleX(1.1f);
	       // mOpenCvCameraView.setTranslationX(25);
	        mOpenCvCameraView.setCvCameraViewListener(this);
	        
	        
			
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		Log.i(TAG, "called onCreateOptionsMenu");
        mItemSwitchCamera = menu.add("Toggle Native/Java camera");
        return true;
	}

	 @Override
	    public boolean onOptionsItemSelected(MenuItem item) {
	        String toastMesage = new String();
	        Log.i(TAG, "called onOptionsItemSelected; selected item: " + item);

	        if (item == mItemSwitchCamera) {
	            mOpenCvCameraView.setVisibility(SurfaceView.GONE);
	            mIsJavaCamera = !mIsJavaCamera;

	            if (mIsJavaCamera) {
	                mOpenCvCameraView = (PortraitCameraView) findViewById(R.id.java_surface_view);
	                toastMesage = "Java Camera";
	            } else {
	                mOpenCvCameraView = (PortraitCameraView) findViewById(R.id.native_surface_view);
	                toastMesage = "Native Camera";
	            }

	            mOpenCvCameraView.setVisibility(SurfaceView.VISIBLE);
	            mOpenCvCameraView.setCvCameraViewListener(this);
	            mOpenCvCameraView.enableView();
	            Toast toast = Toast.makeText(this, toastMesage, Toast.LENGTH_LONG);
	            toast.show();
	        }

	        return true;
	    }
	
	 @Override
	 public void onPause(){
		 super.onPause();
	     if (mOpenCvCameraView != null)
	    	 mOpenCvCameraView.disableView();
	 }
	 
	 @Override
	    public void onResume(){
	        super.onResume();
	        OpenCVLoader.initAsync(OpenCVLoader.OPENCV_VERSION_2_4_3, this, mLoaderCallback);
	    }

	    public void onDestroy() {
	        super.onDestroy();
	        if (mOpenCvCameraView != null)
	            mOpenCvCameraView.disableView();
	    }

	@Override
	public void onCameraViewStarted(int width, int height) {
		// TODO Auto-generated method stub
		List<Size> l = getResolutionList();
		for(int i = 0; i < l.size(); i++){
			Size s = l.get(i);
			if(s != null){
				if(s.width == 1280 && s.height == 720){
					mOpenCvCameraView.setResolution(s);
				}
				Log.i("isaac_debug", "resx: " + s.width + " resy: " + s.height);
			} else{
	        	Log.i("isaac_debug", "res i s null");
			}
		}
        
		
	}

	@Override
	public void onCameraViewStopped() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public Mat onCameraFrame(CvCameraViewFrame inputFrame) {
		Mat input = inputFrame.rgba();
		//Mat big = new Mat(input.height(), input.width(), CvType.CV_8UC4);
		//Imgproc.resize(input, big, big.size());
		//Log.i("isaac_debug", "matx: " + big.size().width + " maty: " + big.size().height);
        return input;
    }
	
	
	public List<Size> getResolutionList() {
		Camera c = mOpenCvCameraView.mCamera;
		if(c != null){
			Parameters p = mOpenCvCameraView.mCamera.getParameters();
	        return mOpenCvCameraView.mCamera.getParameters().getSupportedPreviewSizes();
		}else{
			return null;
		}
		
    }
}
