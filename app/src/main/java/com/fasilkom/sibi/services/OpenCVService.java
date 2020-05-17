package com.fasilkom.sibi.services;

import android.content.Context;
import android.graphics.Bitmap;
import android.os.Environment;
import android.util.Log;
import android.util.TimingLogger;

import org.bytedeco.javacv.AndroidFrameConverter;
import org.bytedeco.javacv.FFmpegFrameGrabber;
import org.bytedeco.javacv.Frame;
import org.bytedeco.javacv.OpenCVFrameConverter;
import org.opencv.core.Core;
import org.opencv.core.CvType;
import org.opencv.core.Mat;
import org.opencv.core.Point;
import org.opencv.core.Scalar;
import org.opencv.core.Size;
import org.opencv.imgcodecs.Imgcodecs;
import org.opencv.imgproc.Imgproc;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;

public class OpenCVService {

    private static OpenCVService instance;

    /* Skin Mask untuk remove background */
    private Scalar lower = new Scalar (30, 60, 60);
    private Scalar upper = new Scalar (90, 255, 255);

    private Scalar lower2 = new Scalar(0, 48, 80);
    private Scalar upper2 = new Scalar(20, 255, 255);

    /* Template Matrix */
    private Mat hsv = null;
    private Mat skinMask = null;
    private Mat whiteMatrix = new Mat(1080, 1080, CvType.CV_8UC1, new Scalar(255));

    public static OpenCVService getInstance() {
        if (instance == null) {
            instance = new OpenCVService();
        }

        return instance;
    }

    public ArrayList<float[]> javacvConvert(Context context, String filename) throws IOException {

        TimingLogger timingLogger = new TimingLogger("JavaCVConvert", "Start");

        File filePath = Environment.getExternalStorageDirectory();
        File dir = new File(filePath.getAbsolutePath() + "/SIBI");

        Log.d("FilePath", filePath.getAbsolutePath());

//        InputStream inputStream = new FileInputStream(dir + "/Siapa_Nama_Mu.mp4");
        InputStream inputStream = new FileInputStream(dir + "/Saya_Sering_Sakit_Kepala.mp4");

        FFmpegFrameGrabber grabber = new FFmpegFrameGrabber(inputStream);
        AndroidFrameConverter converterToBitmap = new AndroidFrameConverter();
        OpenCVFrameConverter.ToMat converterToMat = new OpenCVFrameConverter.ToMat();

        grabber.start();

        hsv = new Mat();
        skinMask = new Mat();

        ArrayList<float[]> outputData = new ArrayList();

//        timingLogger.addSplit("(1) Phase: Pre-Loop");

        int i = 0;

        while(true) {

//            timingLogger.addSplit("Loop");
//
//            Log.d("FrameExtract", "Count : " + i++);
            Frame nthFrame = grabber.grabImage();

            if (nthFrame == null) {
                break;
            }

            Mat mat = converterToMat.convertToOrgOpenCvCoreMat(nthFrame);

            mat =  preprocessImage(mat, dir);
//            timingLogger.addSplit("(2) Phase: Post Preprocess");

            outputData.add(matToFloatArray(mat, 224, 224, 3));
//            timingLogger.addSplit("(3) Phase: Flatten");
//            break;
        }

        timingLogger.addSplit("(4) Phase: Done All Frame");

        timingLogger.dumpToLog();

        return outputData;
    }

    public float[] matToFloatArray(Mat mat, int x_ax, int y_ax, int z_ax) {
//        Log.d("MatSize", "" + mat.height());
//        Log.d("MatSize", "" + mat.width());
//        Log.d("MatSize", "" + mat.channels());

        float[] outputArray = new float[x_ax * y_ax * z_ax];
        int idx = 0;

        for (int i = 0; i < x_ax; i++) {
            for (int j = 0; j < y_ax; j++) {
                for (int k = 0; k < z_ax; k++) {
                    outputArray[idx++] = (float) mat.get(i, j)[k];
                }
            }
        }
        return outputArray;
    }

    public void saveImageToInternalStorage(Bitmap bitmap, Mat img, int frameCount, File dir) {
        Imgcodecs.imwrite(dir + "/hello.png", img);
    }

    public Mat preprocessImage(Mat mat, File dir) {
        TimingLogger timingLogger = new TimingLogger("Preprocess", "Start");

        int left = (mat.width() - mat.height())/2;
        int right = (mat.width() + mat.height())/2;

        mat = mat.submat(0, mat.height(), left, right);
        timingLogger.addSplit("(1) Phase: Submat");

//        Imgproc.resize(mat, mat, new Size(540, 540), 0.0, 0.0, Imgproc.INTER_AREA);

        /* Convert to HSV */
        Imgproc.cvtColor(mat, hsv, Imgproc.COLOR_BGR2HSV);
        timingLogger.addSplit("(2) Phase: BGR to HSV");

        /* Get skin mask*/
        Core.inRange(hsv, lower, upper, skinMask);
        timingLogger.addSplit("(3) Phase: InRange");

        /* Subtract Mat[255,255] with skinMask to invert its value*/
        Core.subtract(whiteMatrix, skinMask, skinMask);
        timingLogger.addSplit("(4) Phase: Invert skinMask");

        /* Bitwise_and to get cropped image */
        Core.bitwise_and(mat, mat, mat, skinMask);
        timingLogger.addSplit("(5) Phase: First Bitwise_And");

        timingLogger.addSplit("========= Next: Segmentasi Kulit ========");

        /* Proses Segmentasi Kulit */

        Imgproc.cvtColor(mat, hsv, Imgproc.COLOR_BGR2HSV);
        timingLogger.addSplit("(1) Phase: BGR to HSV");
        Core.inRange(hsv, lower2, upper2, skinMask);
        timingLogger.addSplit("(2) Phase: InRange");

        Point anchor = new Point(-1, -1);

        Mat kernel = Imgproc.getStructuringElement(Imgproc.MORPH_ELLIPSE, new Size(3, 3));
        Imgproc.erode(skinMask, skinMask, kernel, anchor, 3);
        Imgproc.dilate(skinMask, skinMask, kernel, anchor, 8);
        Imgproc.erode(skinMask, skinMask, kernel, anchor, 5);
        timingLogger.addSplit("(3) Phase: Open-Close 1st");

        kernel = Imgproc.getStructuringElement(Imgproc.MORPH_ELLIPSE, new Size(5, 5));
        Imgproc.dilate(skinMask, skinMask, kernel, anchor, 3);
        Imgproc.erode(skinMask, skinMask, kernel, anchor, 3);
        timingLogger.addSplit("(4) Phase: Open-Close 2nd");

        kernel = Imgproc.getStructuringElement(Imgproc.MORPH_ELLIPSE, new Size(3, 3));
        Imgproc.dilate(skinMask, skinMask, kernel, anchor, 5);
        Imgproc.erode(skinMask, skinMask, kernel, anchor, 5);
        timingLogger.addSplit("(5) Phase: Open-Close 3rd");

        Imgproc.GaussianBlur(skinMask, skinMask, new Size(3, 3), 0);
        timingLogger.addSplit("(6) Phase: Gaussian Blur");

        Mat skin = new Mat();
        Core.bitwise_and(mat, mat, skin, skinMask);
        timingLogger.addSplit("(7) Phase: Second Bitwise_And");

        /* Resize Image */
        Imgproc.resize(skin, mat, new Size(224, 224), 0.0, 0.0, Imgproc.INTER_AREA);
        timingLogger.addSplit("(8) Phase: Resize");

        timingLogger.dumpToLog();

        Imgcodecs.imwrite(dir + "/final_default_lower.png", mat);
        return mat;
    }
}
