package com.lasarobotics.tests.camera;

import android.app.Activity;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;

import com.example.rmmurphy.alotovisionlib.android.Camera;
import com.example.rmmurphy.alotovisionlib.android.Cameras;
import com.example.rmmurphy.alotovisionlib.robotVision.RobotVision;
import com.example.rmmurphy.alotovisionlib.util.FPS;

import org.opencv.android.BaseLoaderCallback;
import org.opencv.android.CameraBridgeViewBase;
import org.opencv.android.CameraBridgeViewBase.CvCameraViewFrame;
import org.opencv.android.CameraBridgeViewBase.CvCameraViewListener2;
import org.opencv.android.LoaderCallbackInterface;
import org.opencv.android.OpenCVLoader;
import org.opencv.core.Core;
import org.opencv.core.CvType;
import org.opencv.core.Mat;
import org.opencv.core.MatOfPoint;
import org.opencv.core.Point;
import org.opencv.core.Rect;
import org.opencv.core.Scalar;
import org.opencv.core.Size;
import org.opencv.imgproc.Imgproc;

import java.util.ArrayList;
import java.util.List;

public class CameraTestActivity extends Activity implements View.OnTouchListener, CvCameraViewListener2 {

    private CameraBridgeViewBase mOpenCvCameraView;
    private float focalLength; //Camera lens focal length
    //private ObjectDetection.ObjectAnalysis objectAnalysis;
    private FPS fpsCounter;
    private RobotVision rbVis;
    private Mat mRgba;
    private Scalar mBlobColorRgba;
    private Scalar mBlobColorHsv;
    private Mat mSpectrum;
    private Size SPECTRUM_SIZE;
    private Scalar CONTOUR_COLOR;
    private boolean waitFirstTouch = true;
    private int firstTouchX;
    private int firstTouchY;

    private final BaseLoaderCallback mLoaderCallback = new BaseLoaderCallback(this) {
        @Override
        public void onManagerConnected(int status) {
            switch (status) {
                case LoaderCallbackInterface.SUCCESS: {
                    // OpenCV loaded successfully!
                    // Load native library AFTER OpenCV initialization

                    initialize();

                    mOpenCvCameraView.enableView();
                    mOpenCvCameraView.setOnTouchListener(CameraTestActivity.this);
                }
                break;
                default: {
                    super.onManagerConnected(status);
                }
                break;
            }
        }
    };

    public CameraTestActivity() {

    }

    private void initialize() {
        //GET CAMERA PROPERTIES


        //GET OBJECT IMAGE
        //Read the target image file
        /*String dir = Util.getDCIMDirectory();
        File file = new File(dir + "/beacon.png");

        if (!file.exists())
        {
            // print error and abort execution
            Log.e("CameraTester", "FAILED TO FIND IMAGE FILE!");
            System.exit(1);
        }
        Mat mTarget = Highgui.imread(file.getAbsolutePath(), Highgui.IMREAD_GRAYSCALE);
        if (mTarget.empty())
        {
            // print error and abort execution
            Log.e("CameraTester", "FAILED TO LOAD IMAGE FILE!");
            System.exit(1);
        }*/

        //ANALYZE OBJECT
        //ObjectDetection detection = new ObjectDetection(ObjectDetection.FeatureDetectorType.GFTT,
        //        ObjectDetection.DescriptorExtractorType.ORB,
        //        ObjectDetection.DescriptorMatcherType.BRUTEFORCE_HAMMING);
        //objectAnalysis = detection.analyzeObject(mTarget);

        //UPDATE COUNTER
        fpsCounter = new FPS();
    }

    /**
     * Called when the activity is first created.
     */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

        setContentView(R.layout.activity_cameratest);

        mOpenCvCameraView = (CameraBridgeViewBase) findViewById(R.id.surfaceView);
        mOpenCvCameraView.setCvCameraViewListener(this);
    }

    @Override
    public void onPause() {
        super.onPause();
        if (mOpenCvCameraView != null)
            mOpenCvCameraView.disableView();
    }

    @Override
    public void onResume() {
        super.onResume();
        if (!OpenCVLoader.initDebug()) {
            // Internal OpenCV library not found. Using OpenCV Manager for initialization
            OpenCVLoader.initAsync(OpenCVLoader.OPENCV_VERSION_3_0_0, this, mLoaderCallback);
        } else {
            // OpenCV library found inside package. Using it!
            mLoaderCallback.onManagerConnected(LoaderCallbackInterface.SUCCESS);
        }
    }

    public void onDestroy() {
        super.onDestroy();
        if (mOpenCvCameraView != null)
            mOpenCvCameraView.disableView();

    }

    public void onCameraViewStarted(int width, int height)
    {
        mRgba = new Mat(height, width, CvType.CV_8UC4);
        //mDetector = new ColorBlobDetector();
        mSpectrum = new Mat();
        mBlobColorRgba = new Scalar(255);
        mBlobColorHsv = new Scalar(255);
        SPECTRUM_SIZE = new Size(100, 35);
        CONTOUR_COLOR = new Scalar(0, 255, 0, 255);

        waitFirstTouch = true;

      /*--------------------------------------------------------------------------------------------
       * Call robot vision init here so that the opencv native bindings have been declared after the
       * camera starts.
       *------------------------------------------------------------------------------------------*/
        rbVis = new RobotVision(width, height);
    }

    public void onCameraViewStopped()
    {
        mRgba.release();
    }

/*    public boolean onTouch(View v, MotionEvent event)
    {
        int cols = mRgba.cols();
        int rows = mRgba.rows();

        rbVis.setObjectTrackState(RobotVision.State.OBJECT_IDLE);

        if( rbVis.getObjectTrackState() == RobotVision.State.OBJECT_IDLE )
        {
            int xOffset = (mOpenCvCameraView.getWidth() - cols) / 2;
            int yOffset = (mOpenCvCameraView.getHeight() - rows) / 2;

            int x = (int) event.getX() - xOffset;
            int y = (int) event.getY() - yOffset;

            if ((x < 0) || (y < 0) || (x > cols) || (y > rows)) return false;

            if(waitFirstTouch)
            {
                firstTouchX = x;
                firstTouchY = y;
                waitFirstTouch = false;
            }
            else
            {
                waitFirstTouch = true;

                if( (x > firstTouchX) && (y > firstTouchY))
                {
                    Rect touchedRect = new Rect();
                    touchedRect.x = firstTouchX;
                    touchedRect.y = firstTouchY;

                    touchedRect.width = x - firstTouchX;
                    touchedRect.height = y - firstTouchY;

                    *//*------------------------------------------------------------------------------
                     * Set the object tracker to the initialization state. On the next camera frame
                     * event this state will be entered.
                     *----------------------------------------------------------------------------*//*
                    rbVis.setObjectTrackInitRect(touchedRect);
                    rbVis.setObjectTrackState(RobotVision.State.OBJECT_TRACK_INIT);
                }
            }
        }
        return false; // don't need subsequent touch events
    }*/

    public boolean onTouch(View v, MotionEvent event)
    {
        int cols = mRgba.cols();
        int rows = mRgba.rows();

        rbVis.setObjectTrackState(RobotVision.State.OBJECT_IDLE);

        if( rbVis.getObjectTrackState() == RobotVision.State.OBJECT_IDLE )
        {
            int xOffset = (mOpenCvCameraView.getWidth() - cols) / 2;
            int yOffset = (mOpenCvCameraView.getHeight() - rows) / 2;

            int x = (int) event.getX() - xOffset;
            int y = (int) event.getY() - yOffset;

            if ((x < 0) || (y < 0) || (x > cols) || (y > rows)) return false;

            Rect touchedRect = new Rect();

            touchedRect.x = (x > 8) ? x - 8 : 0;
            touchedRect.y = (y > 8) ? y - 8 : 0;

            touchedRect.width = (x + 8 < cols) ? x + 8 - touchedRect.x : cols - touchedRect.x;
            touchedRect.height = (y + 8 < rows) ? y + 8 - touchedRect.y : rows - touchedRect.y;

            rbVis.setObjectTrackInitRect(touchedRect);
            rbVis.setObjectTrackState(RobotVision.State.OBJECT_TRACK_INIT);
        }
        return false; // don't need subsequent touch events
    }

    public Mat onCameraFrame(CvCameraViewFrame inputFrame)
    {
        mRgba = inputFrame.rgba();
        int cols = mRgba.cols();
        int rows = mRgba.rows();

      /*--------------------------------------------------------------------------------------------
       * Track the color, coordinates, and area of the selected object.
       *------------------------------------------------------------------------------------------*/
        rbVis.updateObjectTrack(mRgba);

        Rect touchedRect = rbVis.getObjectTrackInitRect();

        Imgproc.rectangle(mRgba, new Point(touchedRect.x, touchedRect.y), new Point(touchedRect.x + touchedRect.width, touchedRect.y + touchedRect.height), new Scalar(255, 0, 0, 255), 3);

        if (rbVis.isTargetLocked())
        {
            Imgproc.resize(rbVis.getBlobDetector().getSpectrum(), mSpectrum, SPECTRUM_SIZE);

            Double area;
            int x;
            int y;
            int width;
            int height;

            int[] rawTarget = rbVis.getRawTargetCoords();
            double[] filteredTarget = rbVis.getFilteredTargetCoords();
            double[] filteredAbsTarget = rbVis.getFilteredTargetCoordsAbsolute();

            if( rawTarget != null)
            {
                /*---------------------------------------------------------------------------------*
                 * Draw the raw target bounding rect.
                 *--------------------------------------------------------------------------------*/
                x = (int)(filteredAbsTarget[0] - (filteredAbsTarget[4] / 2));
                y = (int)(filteredAbsTarget[1] - (filteredAbsTarget[5] / 2));

                Imgproc.rectangle(mRgba, new Point(x, y), new Point(x + (int)filteredAbsTarget[4], y + (int)filteredAbsTarget[5]), new Scalar(0, 255, 0, 255), 3);

                //Imgproc.circle(mRgba, new Point(rawTarget[0], rawTarget[1]), 5, new Scalar(0, 255, 0, 255), -1);

                Rect rec = rbVis.getRegionOfInterestRect();
                Imgproc.rectangle(mRgba, new Point(rec.x, rec.y), new Point(rec.x + rec.width, rec.y + rec.height), new Scalar(0, 0, 255, 255), 3);


                int targetIndex =  rbVis.getTargetContourIndex();
                List<MatOfPoint> blobContours = rbVis.getContours();

                Imgproc.drawContours(mRgba, blobContours, targetIndex, new Scalar(255, 255, 255, 255),-1);

                Imgproc.putText(mRgba, "[" + (int)filteredTarget[0] + "," + (int)filteredTarget[1] + "," + (int)(filteredTarget[4] * filteredTarget[5]) + "]", new Point((int)filteredTarget[0]+cols/2 + 4, -(int)filteredTarget[1]+rows/2), Core.FONT_HERSHEY_PLAIN, 2, new Scalar(255, 255, 255, 255), 3);
                Imgproc.circle(mRgba, new Point((int)filteredAbsTarget[0], filteredAbsTarget[1]), 5, new Scalar(0, 255, 0, 255), -1);

            }

            Mat colorLabel = mRgba.submat(4, 40, 4, 40);
            colorLabel.setTo(rbVis.getObjectColorRgb());

            Mat spectrumLabel = mRgba.submat(4, 4 + mSpectrum.rows(), 70, 70 + mSpectrum.cols());
            mSpectrum.copyTo(spectrumLabel);

        }/*End if( g.getObjectTrackState() == RobotVision.State.OBJECT_TRACK)*/

        return mRgba;
    }

    private Scalar converScalarHsv2Rgba(Scalar hsvColor)
    {
        Mat pointMatRgba = new Mat();
        Mat pointMatHsv = new Mat(1, 1, CvType.CV_8UC3, hsvColor);
        Imgproc.cvtColor(pointMatHsv, pointMatRgba, Imgproc.COLOR_HSV2RGB_FULL, 4);

        return new Scalar(pointMatRgba.get(0, 0));
    }
}
