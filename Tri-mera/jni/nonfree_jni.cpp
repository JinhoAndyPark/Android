// A simple demo of JNI interface to implement SIFT detection for Android application using nonfree module in OpenCV4Android.
// Created by Guohui Wang 
// Email: robertwgh_at_gmail_com
// Data: 2/26/2014

#include <time.h>
#include <stdio.h>
#include <jni.h>
#include <android/log.h>
#include <iostream>
#include <string.h>
#include <math.h>

#include <opencv\cv.h>
#include <opencv\cxcore.h>
#include <opencv\highgui.h>
#include <opencv2\opencv.hpp>
#include <opencv2/core/core.hpp>
#include <opencv2/highgui/highgui.hpp>
#include <opencv2/nonfree/nonfree.hpp>
#include <opencv2/nonfree/features2d.hpp>
#include <opencv2\stitching\stitcher.hpp>

#define pi 3.1416
/*SIFT Parameters*/
#define nFeatures 150
#define nOctavelayers 3
#define ContrastThreshold 0.04
#define EdgeThreshold 10.0
#define SIGMA 1.6

/*Feature*/
#define FEATURE_SURF 1 //default
#define FEATURE_SIFT 2

/*Mode*/
#define MODE_HORIZONTAL 1 //defualt
#define MODE_VERTICAL 2
#define MODE_SPECIAL 3

/*Rotate Angle*/
#define ANGLE_COUNTER_CLOCKWISE_90 90
#define ANGLE_CLOCKWISE_90 -90

using namespace cv;
using namespace std;

#define  LOG_TAG    "nonfree_jni_demo"
#define  LOGI(...)  __android_log_print(ANDROID_LOG_INFO,LOG_TAG,__VA_ARGS__)



typedef unsigned char uchar;


int yw_panorama(int panorama_mode = 1 , int feature = 1, int num_img = 0, int _devideBy = 3, char* _folder_path = NULL, char* saveFileName = NULL);

extern "C" {
    JNIEXPORT jint JNICALL Java_second_test_joolmera_Panorama_makePanorama(JNIEnv* env, jobject obj, jint panorama_mode, jint feature, jint num_img, jint devideBy, jstring folder_path, jstring saveFileName);
};

JNIEXPORT jint JNICALL Java_second_test_joolmera_Panorama_makePanorama(JNIEnv* env, jobject obj, jint panorama_mode, jint feature, jint num_img, jint devideBy, jstring folder_path, jstring saveFileName)
{
   const char* tmp_folder_path = env->GetStringUTFChars(folder_path, NULL);
   const char* tmp_saveFileName = env->GetStringUTFChars(saveFileName, NULL);
   char _folder_path[200];
   char _saveFileName[100];
   int _devideBy =  devideBy;
   strcpy(_folder_path,tmp_folder_path);
   strcpy(_saveFileName,tmp_saveFileName);
   LOGI( "start make Panorama Image! \n");
   yw_panorama(panorama_mode, feature, num_img, _devideBy, _folder_path, _saveFileName);
   LOGI( "End make Panorama Image! \n");
}


int yw_panorama(int _panorama_mode, int _feature, int _num_img, int _devideBy, char* _folder_path, char* saveFileName)
{
   //cv::initModule_nonfree();
   ///////////////////////////////////////////////////////////////////////////////////////////

   LOGI( "panorama start");
   int panorama_mode = _panorama_mode; // Horizontal == 1 Vertical == 2 Special == 3
   int feature = _feature; // SURF == 1  SIFT == 2
   int num_img = _num_img;
   int devideBy = _devideBy;

   char folder_path[200] = { '\0', };
   strcpy(folder_path, _folder_path);
   char save_file_name[200] = { '\0', };
   strcpy(save_file_name, saveFileName);

   char* file_path_Side = (char*)malloc(strlen(folder_path) + 20);
   char* file_path_Center = (char*)malloc(strlen(folder_path) + 20);

   char* save_path = (char*)malloc(strlen(folder_path) + 20);


   IplImage* OSideImg;
   IplImage* OTempImg;
   CvSize down;

   Mat _tempMat;

   vector<Mat> vImg;
   Mat rImg;

   for (int i = 1; i < num_img; i++)
   {
      //영상 읽기
      if (panorama_mode == MODE_HORIZONTAL){

         sprintf(file_path_Side, "%s/tri_mera%d.jpg", folder_path, i);
         OTempImg = cvLoadImage(file_path_Side, CV_LOAD_IMAGE_UNCHANGED);

         if(OTempImg == NULL){
            LOGI( "cannot load image");
         }
         down = cvSize(OTempImg->width / devideBy, OTempImg->height / devideBy);
         OSideImg = cvCreateImage(down, OTempImg->depth, OTempImg->nChannels);
         cvResize(OTempImg, OSideImg);
         cvReleaseImage(&OTempImg);
         LOGI( "image load");
      }
      else if (panorama_mode == MODE_VERTICAL){
         CvSize org_size;
         CvSize rot_size;

         sprintf(file_path_Side, "%s/tri_mera%d.jpg", folder_path, i);
         IplImage* img2_org;
         /*down size*/
         OTempImg = cvLoadImage(file_path_Side, CV_LOAD_IMAGE_UNCHANGED);
         down = cvSize(OTempImg->width / devideBy, OTempImg->height / devideBy);
         img2_org = cvCreateImage(down, OTempImg->depth, OTempImg->nChannels);
         cvResize(OTempImg, img2_org);
         cvReleaseImage(&OTempImg);

         rot_size = cvSize(img2_org->height, img2_org->width);
         OSideImg = cvCreateImage(rot_size, img2_org->depth, img2_org->nChannels);
         rotateImage(img2_org, OSideImg, ANGLE_COUNTER_CLOCKWISE_90);
         cvReleaseImage(&img2_org);
      }
      else{
         /*handle error : select panorama mode!*/

      }

      ///////////////////////////////////////////////////////////////////////////////////////////

      //vImg.push_back(imread(file_path_Side));

      LOGI( "panorama push_back");
      vImg.push_back(*(new Mat(OSideImg,true)));

      ///////////////////////////////////////////////////////////////////////////////////////////
      cvReleaseImage(&OSideImg);
   }
   LOGI( "panorama stitch_start");
   Stitcher stitcher = Stitcher::createDefault();
   stitcher.stitch(vImg, rImg);
   if (panorama_mode == MODE_HORIZONTAL){
      cv::imwrite(save_file_name, rImg);
   }
//   else{
//      IplImage* PanoImg = cvCloneImage(&(IplImage)rImg);
//      IplImage* vertical_panorama = cvCreateImage(cvSize(PanoImg->height, PanoImg->width), PanoImg->depth, PanoImg->nChannels);
//      rotateImage(PanoImg, vertical_panorama, ANGLE_CLOCKWISE_90);
//      cvSaveImage(save_file_name, vertical_panorama);
//      cvReleaseImage(&vertical_panorama);
//   }

   LOGI( "panorama end");

   /*return SUCCESS*/
   return 1;

}