package com.fasilkom.sibi.services;

import android.content.Context;
import android.graphics.Bitmap;
import android.os.Environment;
import android.util.Log;
import android.util.LruCache;
import android.util.TimingLogger;

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
import java.util.List;

import io.reactivex.rxjava3.core.Single;
import io.reactivex.rxjava3.schedulers.Schedulers;

public class OpenCVService {

    private static OpenCVService instance;

    /* Matrix Cache */
    private LruCache<String, Mat> memoryCache;

    /* Skin Mask untuk remove background */
    private Scalar lower = new Scalar(30, 60, 60);
    private Scalar upper = new Scalar(90, 255, 255);

    private Scalar lower2 = new Scalar(0, 48, 80);
    private Scalar upper2 = new Scalar(20, 255, 255);

    /* Template Matrix */
    private Mat hsv = null;
    private Mat skinMask = null;
    private Mat whiteMatrix = new Mat(1080, 1080, CvType.CV_8UC1, new Scalar(255));

    /* Reusable */
    float[] outputArray ;

    public static OpenCVService getInstance() {
        if (instance == null) {
            instance = new OpenCVService();
        }

        return instance;
    }

//    public Single executePreprocess(List<Mat> listOfFrame) {
//        System.out.println("Check size :  " + listOfFrame.size());
//        return Single.create(emitter -> {
//
//            ArrayList<float[]> outputData = new ArrayList<>();
//
//            for (int frameIdx = 0; frameIdx < listOfFrame.size(); frameIdx++) {
//                System.out.println(frameIdx);
//
//                Mat mat =  preprocessImage(listOfFrame.get(frameIdx));
//
//                outputData.add(matToFloatArray(outputArray, mat, 224, 224, 3));
//            }
//
//            emitter.onSuccess(outputData);
//        })
//            .subscribeOn(Schedulers.newThread());
//    }


    public ArrayList<byte[]> javacvConvert(String filename) throws IOException {

        /* ================= Load Video ================== */
//        TimingLogger timingLogger = new TimingLogger("JavaCVConvert", "Start");

        File filePath = Environment.getExternalStorageDirectory();
        File dir = new File(filePath.getAbsolutePath() + "/SIBI");

        Log.d("FilePath", filePath.getAbsolutePath());

        InputStream inputStream = new FileInputStream(filename);
//        InputStream inputStream = new FileInputStream(dir + "/Siapa_Nama_Mu.mp4");
//        InputStream inputStream = new FileInputStream(dir + "/Saya_Sering_Sakit_Kepala.mp4");
//        InputStream inputStream = new FileInputStream(dir + "/Saya_Sering_Sakit_Kepala_Saya_Harus_Periksa_Ke_Bagi_an_Mana.mp4");

        FFmpegFrameGrabber grabber = new FFmpegFrameGrabber(inputStream);
        OpenCVFrameConverter.ToMat converterToMat = new OpenCVFrameConverter.ToMat();

        hsv = new Mat();
        skinMask = new Mat();

        grabber.start();

//        ArrayList<Float> outputData = new ArrayList();
        ArrayList<byte[]> outputData = new ArrayList();
//        ArrayList<Frame> outputData = new ArrayList();

//        timingLogger.addSplit("(1) Phase: Pre-Loop");

        int i = 0;

        while(true) {
            System.out.println("index : " + (i++));
//            timingLogger.addSplit("Loop");

//            Log.d("FrameExtract", "Count : " + i++);
            Frame nthFrame = grabber.grabImage();

            if (nthFrame == null) {
                break;
            }
            i++;

            Mat mat = converterToMat.convertToOrgOpenCvCoreMat(nthFrame);

            mat =  preprocessImage(mat, dir);
//            timingLogger.addSplit("(2) Phase: Post Preprocess");

            outputData.add(matToFloatArray(mat, 224, 224, 3));
//            matToFloatArrayList(mat, outputData, 224, 224, 3);
//            outputData.addAll(matToFloatArrayList(mat, 224, 224, 3));
//            outputData.add(nthFrame);
//            timingLogger.addSplit("(3) Phase: Flatten");
//            break;
        }

//        ArrayList<float[]> result = new ArrayList();
//
//        for (int j = 0; j < outputData.size(); j++) {
//            Mat mat = preprocessImage(outputData.get(j));
//            result.add(matToFloatArray(mat, 224, 224, 3));
//        }

//        timingLogger.addSplit("(4) Phase: Done All Frame");

//        timingLogger.dumpToLog();

//        return extract(outputData);
        return outputData;
//        return result;
    }

//    public ArrayList<float[]> extract(ArrayList<Frame> outputData) {
//        ArrayList<float[]> result = new ArrayList();
//        OpenCVFrameConverter.ToMat converterToMat = new OpenCVFrameConverter.ToMat();
//        float[] outputArray = new float[224 * 224 * 3];
//
//        for (int j = 0; j < outputData.size(); j++) {
//
//            Mat mat = preprocessImage(converterToMat.convertToOrgOpenCvCoreMat(outputData.get(j)));
//            result.add(matToFloatArray(outputArray, mat, 224, 224, 3));
//        }
//        return result;
//    }

    public byte[] matToFloatArray(Mat mat, int x_ax, int y_ax, int z_ax) {
//        Log.d("MatSize", "" + mat.height());
//        Log.d("MatSize", "" + mat.width());
//        Log.d("MatSize", "" + mat.channels());

        double[] rgb = new double[3];
//        Mat newMat = new Mat();
//        outputArray = new float[224 * 224 * 3];

        int idx = 0;

//        for (int i = 0; i < x_ax; i++) {
//            for (int j = 0; j < y_ax; j++) {
//                rgb = mat.get(i, j);
//                for (int k = 0; k < z_ax; k++) {
//                    outputArray[idx++] = (float) rgb[k];
//                }
//            }
//        }
//        System.out.println("helooooooooooo " + mat.type());
        byte[] outputArray = new byte[224 * 224 * 3];
//        Mat convMat = new Mat();
//        mat.convertTo(convMat, CvType.CV_32FC3);
//        convMat.get(0, 0, outputArray);
        mat.get(0,0, outputArray);

        return outputArray;
    }

    public void matToFloatArrayList(Mat mat, ArrayList<Float> arr, int x_ax, int y_ax, int z_ax) {

        double[] rgb = new double[3];

        int idx = 0;

        for (int i = 0; i < x_ax; i++) {
            for (int j = 0; j < y_ax; j++) {
                rgb =  mat.get(i, j);
                for (int k = 0; k < z_ax; k++) {
//                    outputArray[idx++] = (float) rgb[k];
                    arr.add((float) rgb[k]);
                }
            }
        }
    }

    public void saveImageToInternalStorage(Mat img, File dir, String filename) {
        Imgcodecs.imwrite(dir + "/"+filename+".png", img);
    }

    public Mat preprocessImage(Mat mat, File dir) {
//        TimingLogger timingLogger = new TimingLogger("Preprocesss", "Start");
        saveImageToInternalStorage(mat, dir, "tahap1");

        int left = (mat.width() - mat.height())/2;
        int right = (mat.width() + mat.height())/2;

        mat = mat.submat(0, mat.height(), left, right);
        saveImageToInternalStorage(mat, dir, "tahap2");
//        timingLogger.addSplit("(1) Phase: Submat");

//        Imgproc.resize(mat, mat, new Size(540, 540), 0.0, 0.0, Imgproc.INTER_AREA);

        /* Convert to HSV */
        Imgproc.cvtColor(mat, hsv, Imgproc.COLOR_BGR2HSV);
        saveImageToInternalStorage(hsv, dir, "tahap3");
//        timingLogger.addSplit("(2) Phase: BGR to HSV");

        /* Get skin mask*/
        Core.inRange(hsv, lower, upper, skinMask);
        saveImageToInternalStorage(skinMask, dir, "tahap4");
//        timingLogger.addSplit("(3) Phase: InRange");

        /* Subtract Mat[255,255] with skinMask to invert its value*/
        Core.subtract(whiteMatrix, skinMask, skinMask);
//        timingLogger.addSplit("(4) Phase: Invert skinMask");
        saveImageToInternalStorage(skinMask, dir, "tahap5");

        /* Bitwise_and to get cropped image */
        Core.bitwise_and(mat, mat, mat, skinMask);
        saveImageToInternalStorage(mat, dir, "tahap6");
//        timingLogger.addSplit("(5) Phase: First Bitwise_And");

//        timingLogger.addSplit("========= Next: Segmentasi Kulit ========");

        /* Proses Segmentasi Kulit */

        Imgproc.cvtColor(mat, hsv, Imgproc.COLOR_BGR2HSV);
        saveImageToInternalStorage(hsv, dir, "tahap7");
//        timingLogger.addSplit("(1) Phase: BGR to HSV");
        Core.inRange(hsv, lower2, upper2, skinMask);
        saveImageToInternalStorage(skinMask, dir, "tahap8");
//        timingLogger.addSplit("(2) Phase: InRange");

        Point anchor = new Point(-1, -1);

        Mat kernel = Imgproc.getStructuringElement(Imgproc.MORPH_ELLIPSE, new Size(3, 3));
        Imgproc.erode(skinMask, skinMask, kernel, anchor, 3);
        Imgproc.dilate(skinMask, skinMask, kernel, anchor, 8);
        Imgproc.erode(skinMask, skinMask, kernel, anchor, 5);
//        timingLogger.addSplit("(3) Phase: Open-Close 1st");

        kernel = Imgproc.getStructuringElement(Imgproc.MORPH_ELLIPSE, new Size(5, 5));
        Imgproc.dilate(skinMask, skinMask, kernel, anchor, 3);
        Imgproc.erode(skinMask, skinMask, kernel, anchor, 3);
//        timingLogger.addSplit("(4) Phase: Open-Close 2nd");

        kernel = Imgproc.getStructuringElement(Imgproc.MORPH_ELLIPSE, new Size(3, 3));
        Imgproc.dilate(skinMask, skinMask, kernel, anchor, 5);
        Imgproc.erode(skinMask, skinMask, kernel, anchor, 5);
//        timingLogger.addSplit("(5) Phase: Open-Close 3rd");

        saveImageToInternalStorage(skinMask, dir, "tahap9");

        Imgproc.GaussianBlur(skinMask, skinMask, new Size(3, 3), 0);
//        timingLogger.addSplit("(6) Phase: Gaussian Blur");
        saveImageToInternalStorage(skinMask, dir, "tahap10");

        Mat skin = new Mat();
        Core.bitwise_and(mat, mat, skin, skinMask);
//        timingLogger.addSplit("(7) Phase: Second Bitwise_And");
        saveImageToInternalStorage(mat, dir, "tahap11");

        /* Resize Image */
        Imgproc.resize(skin, mat, new Size(224, 224), 0.0, 0.0, Imgproc.INTER_AREA);
//        timingLogger.addSplit("(8) Phase: Resize");
        saveImageToInternalStorage(mat, dir, "tahap12");

//        timingLogger.dumpToLog();

        /* Check: Ini Butuh Param Dir, Pernah Gue Apus*/
//        Imgcodecs.imwrite(dir + "/final_default_lower.png", mat);
        return mat;
    }
}
