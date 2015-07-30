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

typedef struct MATCH_PAIR
{
	int nA;
	int nB;
} MATCH_PAIR;


int FindMatchingPoints(const CvSeq* tKeypoints, const CvSeq* tDescriptors, const CvSeq* srcKeypoints, const CvSeq* srcDescriptors, int descriptor_size, MATCH_PAIR *pMatchPair);
int FindNearestPoints(const float* pA, int laplacian, const CvSeq* srcKeypoints, const CvSeq* srcDescriptors, int descriptor_size);
void rotateImage(IplImage* src, IplImage* dst, int direction);
int findCropPointX(IplImage* src);
int yw_panorama(int panorama_mode = 1 , int feature = 1, int num_img = 0, int _devideBy = 3, char* _folder_path = NULL, char* saveFileName = NULL);
void blending(IplImage* background, IplImage* mask);

/*blending.h*/
IplImage *poisson_blend(IplImage *I, IplImage *mask, int posx, int posy);
void poisson_solver(const IplImage *img, IplImage *gxx, IplImage *gyy, Mat &result);
void transpose(double *mat, double *mat_t, int h, int w);
void idst(double *gtest, double *gfinal, int h, int w);
void dst(double *gtest, double *gfinal, int h, int w);
void lapy(const IplImage *img, IplImage *gyy);
void lapx(const IplImage *img, IplImage *gxx);
void getGradienty(const IplImage *img, IplImage *gy);
void getGradientx(const IplImage *img, IplImage *gx);

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
	LOGI( "Start make Panorama Image! \n");
	yw_panorama(panorama_mode, feature, num_img, _devideBy, _folder_path, _saveFileName);
	LOGI( "End make Panorama Image! \n");
}


int yw_panorama(int _panorama_mode, int _feature, int _num_img, int _devideBy, char* _folder_path, char* saveFileName)
{
	cv::initModule_nonfree();
	///////////////////////////////////////////////////////////////////////////////////////////
	double start, finish;
	start = clock();

	int panorama_mode = _panorama_mode; // Horizontal == 1 Vertical == 2 Special == 3
	int feature = _feature; // SURF == 1  SIFT == 2
	int num_img = _num_img;
	int devideBy = _devideBy;

	char folder_path[200] = {'\0', };
	strcpy(folder_path, _folder_path);
	char save_file_name[200] = {'\0', };
	strcpy(save_file_name, saveFileName);

	char* file_path_Side = (char*)malloc(strlen(folder_path) + 20);
	char* file_path_Center = (char*)malloc(strlen(folder_path) + 20);

	char* save_path = (char*)malloc(strlen(folder_path) + 20);

	IplImage* SideImg;
	IplImage* OSideImg;
	IplImage* CenterImg;
	IplImage* OCenterImg;
	IplImage* TempImg;
	IplImage* OTempImg;
	CvSize down;

	for (int i = 1; i < num_img; i++){
		//printf("start %d\n", i);

		//영상 읽기
		if (panorama_mode == MODE_HORIZONTAL){
			if (i == 1){
				sprintf(file_path_Center, "%s/tri_mera%d.jpg", folder_path, i);
//				CenterImg = cvLoadImage(file_path_Center, CV_LOAD_IMAGE_GRAYSCALE);
//				OCenterImg = cvLoadImage(file_path_Center, CV_LOAD_IMAGE_UNCHANGED);
				TempImg = cvLoadImage(file_path_Center, CV_LOAD_IMAGE_GRAYSCALE);
				if(TempImg == NULL){
					/*return ERROR_*/
				}
				OTempImg = cvLoadImage(file_path_Center, CV_LOAD_IMAGE_UNCHANGED);
				down = cvSize(TempImg->width/devideBy, TempImg->height/devideBy);
				CenterImg = cvCreateImage(down,TempImg->depth,TempImg->nChannels);
				OCenterImg = cvCreateImage(down, OTempImg->depth,OTempImg->nChannels);
				cvResize(TempImg,CenterImg);
				cvResize(OTempImg,OCenterImg);
				cvReleaseImage(&TempImg); cvReleaseImage(&OTempImg);
			}
			else{
				sprintf(file_path_Center, "%s/tri_mera_pano%d.jpg", folder_path, i - 1);
				CenterImg = cvLoadImage(file_path_Center, CV_LOAD_IMAGE_GRAYSCALE);
				if(CenterImg == NULL){
					/*return ERROR_*/
				}
				OCenterImg = cvLoadImage(file_path_Center, CV_LOAD_IMAGE_UNCHANGED);
//				TempImg = cvLoadImage(file_path_Center, CV_LOAD_IMAGE_GRAYSCALE);
//				OTempImg = cvLoadImage(file_path_Center, CV_LOAD_IMAGE_UNCHANGED);
//				down = cvSize(TempImg->width/devideBy, TempImg->height/devideBy);
//				CenterImg = cvCreateImage(down,TempImg->depth,TempImg->nChannels);
//				OCenterImg = cvCreateImage(down, OTempImg->depth,OTempImg->nChannels);
//				cvResize(TempImg,CenterImg);
//				cvResize(OTempImg,OCenterImg);
//				cvReleaseImage(&TempImg); cvReleaseImage(&OTempImg);
			}
			sprintf(file_path_Side, "%s/tri_mera%d.jpg", folder_path, i + 1);
//			SideImg = cvLoadImage(file_path_Side, CV_LOAD_IMAGE_GRAYSCALE);
//			OSideImg = cvLoadImage(file_path_Side, CV_LOAD_IMAGE_UNCHANGED);
			TempImg = cvLoadImage(file_path_Side, CV_LOAD_IMAGE_GRAYSCALE);
			if(TempImg == NULL){
				/*return ERROR_*/
			}
			OTempImg = cvLoadImage(file_path_Side, CV_LOAD_IMAGE_UNCHANGED);
			down = cvSize(TempImg->width/devideBy, TempImg->height/devideBy);
			SideImg = cvCreateImage(down,TempImg->depth,TempImg->nChannels);
			OSideImg = cvCreateImage(down,OTempImg->depth,OTempImg->nChannels);
			cvResize(TempImg, SideImg);
			cvResize(OTempImg, OSideImg);
			cvReleaseImage(&TempImg); cvReleaseImage(&OTempImg);
		}
		else if (panorama_mode == MODE_VERTICAL){
			CvSize org_size;
			CvSize rot_size;
			if (i == 1){
				sprintf(file_path_Center, "%s/tri_mera%d.jpg", folder_path, i);
				IplImage* img1_gray; //= cvLoadImage(file_path_Center, CV_LOAD_IMAGE_GRAYSCALE);
				IplImage* img1_org; //= cvLoadImage(file_path_Center, CV_LOAD_IMAGE_UNCHANGED);
				/*for down size*/
				TempImg = cvLoadImage(file_path_Center, CV_LOAD_IMAGE_GRAYSCALE);
				if(TempImg == NULL){
					/*return ERROR_*/
				}
				OTempImg = cvLoadImage(file_path_Center, CV_LOAD_IMAGE_UNCHANGED);
				down = cvSize(TempImg->width/devideBy, TempImg->height/devideBy);
				img1_gray = cvCreateImage(down,TempImg->depth,TempImg->nChannels);
				img1_org = cvCreateImage(down,OTempImg->depth,OTempImg->nChannels);
				cvResize(TempImg,img1_gray);
				cvResize(OTempImg,img1_org);
				cvReleaseImage(&TempImg); cvReleaseImage(&OTempImg);
				/*end for down size*/
				org_size = cvSize(img1_gray->width, img1_gray->height);
				rot_size = cvSize(img1_gray->height, img1_gray->width);
				CenterImg = cvCreateImage(rot_size, img1_gray->depth, img1_gray->nChannels);
				OCenterImg = cvCreateImage(rot_size, img1_org->depth, img1_org->nChannels);
				rotateImage(img1_gray, CenterImg, ANGLE_COUNTER_CLOCKWISE_90);
				rotateImage(img1_org, OCenterImg, ANGLE_COUNTER_CLOCKWISE_90);
				cvReleaseImage(&img1_gray); cvReleaseImage(&img1_org);
			}
			else{
				sprintf(file_path_Center, "%s/tri_mera_pano%d.jpg", folder_path, i - 1);
				CenterImg = cvLoadImage(file_path_Center, CV_LOAD_IMAGE_GRAYSCALE);
				if(CenterImg == NULL){
					/*return ERROR_*/
				}
				OCenterImg = cvLoadImage(file_path_Center, CV_LOAD_IMAGE_UNCHANGED);
//				TempImg = cvLoadImage(file_path_Center, CV_LOAD_IMAGE_GRAYSCALE);
//				OTempImg = cvLoadImage(file_path_Center, CV_LOAD_IMAGE_UNCHANGED);
//				down = cvSize(TempImg->width/devideBy, TempImg->height/devideBy);
//				SideImg = cvCreateImage(down,TempImg->depth,TempImg->nChannels);
//				OSideImg = cvCreateImage(down,OTempImg->depth,OTempImg->nChannels);
//				cvResize(TempImg, SideImg);
//				cvResize(OTempImg, OSideImg);
//				cvReleaseImage(&TempImg); cvReleaseImage(&OTempImg);
			}

			sprintf(file_path_Side, "%s/tri_mera%d.jpg", folder_path, i + 1);
			IplImage* img2_gray;  // = cvLoadImage(file_path_Side, CV_LOAD_IMAGE_GRAYSCALE);
			IplImage* img2_org;  // = cvLoadImage(file_path_Side, CV_LOAD_IMAGE_UNCHANGED);
			/*for down size*/
			TempImg = cvLoadImage(file_path_Side, CV_LOAD_IMAGE_GRAYSCALE);
			if(TempImg == NULL){
				/*return TempImg*/
			}
			OTempImg = cvLoadImage(file_path_Side, CV_LOAD_IMAGE_UNCHANGED);
			down = cvSize(TempImg->width/devideBy, TempImg->height/devideBy);
			img2_gray = cvCreateImage(down,TempImg->depth,TempImg->nChannels);
			img2_org = cvCreateImage(down,OTempImg->depth,OTempImg->nChannels);
			cvResize(TempImg,img2_gray);
			cvResize(OTempImg,img2_org);
			cvReleaseImage(&TempImg); cvReleaseImage(&OTempImg);
			/*end for down size*/
			org_size = cvSize(img2_gray->width, img2_gray->height);
			rot_size = cvSize(img2_gray->height, img2_gray->width);
			SideImg = cvCreateImage(rot_size, img2_gray->depth, img2_gray->nChannels);
			OSideImg = cvCreateImage(rot_size, img2_org->depth, img2_org->nChannels);
			rotateImage(img2_gray, SideImg, ANGLE_COUNTER_CLOCKWISE_90);
			rotateImage(img2_org, OSideImg, ANGLE_COUNTER_CLOCKWISE_90);
			cvReleaseImage(&img2_gray); cvReleaseImage(&img2_org);
		}
		else{
			/*handle error : select panorama mode!*/

		}

		///////////////////////////////////////////////////////////////////////////////////////////

		CvPoint2D32f *pt1 = NULL;
		CvPoint2D32f *pt2 = NULL;

		int num_good_match = 0;

		if (feature == FEATURE_SURF){
			//surf 특징점 찾기
			CvMemStorage* storage = cvCreateMemStorage(0);
			CvSURFParams params = cvSURFParams(3000, 0);

			//T1 Img
			CvSeq *T1_Keypoints = NULL;
			CvSeq *T1_Descriptors = NULL;
			cvExtractSURF(SideImg, NULL, &T1_Keypoints, &T1_Descriptors, storage, params);

			//T2 Img
			CvSeq *T2_Keypoints = NULL;
			CvSeq *T2_Descriptors = NULL;
			cvExtractSURF(CenterImg, NULL, &T2_Keypoints, &T2_Descriptors, storage, params);

			if(T1_Keypoints == NULL || T2_Keypoints == NULL){
				/*return ERROR_*/
			}

			//특징점 뿌리기 1
			CvSURFPoint* surf1;
			for (int i = 0; i < (T1_Keypoints ? T1_Keypoints->total : 0); i++)
			{
				surf1 = (CvSURFPoint*)cvGetSeqElem(T1_Keypoints, i);
			}

			//특징점 뿌리기 2
			CvSURFPoint* surf2;
			for (int i = 0; i < (T2_Keypoints ? T2_Keypoints->total : 0); i++)
			{
				surf2 = (CvSURFPoint*)cvGetSeqElem(T2_Keypoints, i);
			}
			///////////////////////////////////////////////////////////////////////////////////////////

			//매칭
			MATCH_PAIR *pMatchPair = new MATCH_PAIR[T1_Keypoints->total];
			int descriptor_size = params.extended ? 128 : 64;
			num_good_match = FindMatchingPoints(T1_Keypoints, T1_Descriptors, T2_Keypoints, T2_Descriptors, descriptor_size, pMatchPair);

			if(num_good_match < 4){
				/*return ERROR_*/
			}

			pt1 = new CvPoint2D32f[num_good_match];
			pt2 = new CvPoint2D32f[num_good_match];


			for (int k = 0; k < num_good_match; k++)
			{
				//매칭 k번째의 T1, T2에서의 키 포인트 정보 뽑기
				surf1 = (CvSURFPoint*)cvGetSeqElem(T1_Keypoints, pMatchPair[k].nA);
				pt1[k] = surf1->pt;

				surf2 = (CvSURFPoint*)cvGetSeqElem(T2_Keypoints, pMatchPair[k].nB);
				pt2[k] = surf2->pt;
			}
		}
		else if (feature == FEATURE_SIFT){

			num_good_match = 0;

			Mat img_1(SideImg);
			Mat img_2(CenterImg);
			SiftFeatureDetector detector(nFeatures, nOctavelayers, ContrastThreshold, EdgeThreshold, SIGMA);

			std::vector<KeyPoint> keypoints_1, keypoints_2;


			detector.detect(img_1, keypoints_1);
			detector.detect(img_2, keypoints_2);

			//-- Step 2: Calculate descriptors (feature vectors)
			//SurfDescriptorExtractor extractor;
			SiftDescriptorExtractor extractor(nFeatures, nOctavelayers, ContrastThreshold, EdgeThreshold, SIGMA);

			Mat descriptors_1, descriptors_2;

			extractor.compute(img_1, keypoints_1, descriptors_1);
			extractor.compute(img_2, keypoints_2, descriptors_2);

			//-- Step 3: Matching descriptor vectors using FLANN matcher
			FlannBasedMatcher matcher;
			std::vector< DMatch > matches;
			matcher.match(descriptors_1, descriptors_2, matches);

			double max_dist = 0; double min_dist = 100;

			//-- Quick calculation of max and min distances between keypoints
			for (int i = 0; i < descriptors_1.rows; i++)
			{
				double dist = matches[i].distance;
				if (dist < min_dist) min_dist = dist;
				if (dist > max_dist) max_dist = dist;
			}
			std::vector< DMatch > good_matches;
			for (int i = 0; i < descriptors_1.rows; i++)
			{
				if (matches[i].distance <= max(2 * min_dist, 0.02))
					//if (matches[i].distance <= 2 * min_dist)
				{
					good_matches.push_back(matches[i]);
					num_good_match++;
				}
			}
			if (num_good_match < 4){
				/*return ERROR_*/
			}

			//-- Draw only "good" matches
			Mat img_matches;
			drawMatches(img_1, keypoints_1, img_2, keypoints_2, good_matches, img_matches, Scalar::all(-1), Scalar::all(-1),
				vector<char>(), DrawMatchesFlags::NOT_DRAW_SINGLE_POINTS);

			pt1 = new CvPoint2D32f[num_good_match];
			pt2 = new CvPoint2D32f[num_good_match];

			for (int i = 0; i < num_good_match; i++){
				pt1[i] = keypoints_1[good_matches[i].queryIdx].pt;
				pt2[i] = keypoints_2[good_matches[i].trainIdx].pt;
			}
		}
		else{
			/*handle error : select feature*/
		}

		///////////////////////////////////////////////////////////////////////////////////////////
		//호모그래피 계산
		CvMat M1, M2;
		double H[9];
		CvMat mxH = cvMat(3, 3, CV_64F, H);
		M1 = cvMat(1, num_good_match, CV_32FC2, pt1);
		M2 = cvMat(1, num_good_match, CV_32FC2, pt2);
		cvFindHomography(&M1, &M2, &mxH, CV_RANSAC, 2);

		///////////////////////////////////////////////////////////////////////////////////////////
		//모자이크 영상 만들기
		IplImage* WarpImg = cvCreateImage(cvSize(OCenterImg->width * 2, OCenterImg->height), OCenterImg->depth, OCenterImg->nChannels);

		IplImage* tempWarp = cvCreateImage(cvSize(OCenterImg->width * 2, OCenterImg->height), OCenterImg->depth, OCenterImg->nChannels);

		cvWarpPerspective(OSideImg, WarpImg, &mxH);

		cvWarpPerspective(OSideImg, tempWarp, &mxH);

		cvSetImageROI(WarpImg, cvRect(0, 0, OCenterImg->width, OCenterImg->height));
		cvCopy(OCenterImg, WarpImg);
		cvResetImageROI(WarpImg);

		blending(WarpImg, tempWarp);

		//crop invalid area
		int x = findCropPointX(WarpImg);

		IplImage* PanoImg = cvCreateImage(cvSize(x, WarpImg->height), WarpImg->depth, WarpImg->nChannels);
		cvSetImageROI(WarpImg, cvRect(0, 0, x, WarpImg->height));
		cvCopy(WarpImg, PanoImg);

		if (i != num_img - 1){
			sprintf(save_path, "%s/tri_mera_pano%d.jpg", folder_path, i);
			cvSaveImage(save_path, PanoImg);
		}
		else
		{
			if (panorama_mode == MODE_HORIZONTAL){
				cvSaveImage(save_file_name, PanoImg);
			}
			else{
				IplImage* vertical_panorama = cvCreateImage(cvSize(PanoImg->height, PanoImg->width), PanoImg->depth, PanoImg->nChannels);
				rotateImage(PanoImg, vertical_panorama, ANGLE_CLOCKWISE_90);
				cvSaveImage(save_file_name, vertical_panorama);
				cvReleaseImage(&vertical_panorama);
			}
		}
		///////////////////////////////////////////////////////////////////////////////////////////
		cvReleaseImage(&SideImg);
		cvReleaseImage(&CenterImg);
		cvReleaseImage(&OSideImg);
		cvReleaseImage(&OCenterImg);
		cvReleaseImage(&WarpImg);
	}
	finish = clock();
	LOGI( "panorama!!!");

	/*return SUCCESS*/
	return 1;

}



int FindMatchingPoints(const CvSeq* tKeypoints, const CvSeq* tDescriptors, const CvSeq* srcKeypoints, const CvSeq* srcDescriptors, int descriptor_size, MATCH_PAIR *pMatchPair)
{
	int i;
	float* pA;
	int nMatchB;
	CvSURFPoint* surfA;
	int k = 0;
	for (i = 0; i < tDescriptors->total; i++)
	{
		pA = (float*)cvGetSeqElem(tDescriptors, i);
		surfA = (CvSURFPoint*)cvGetSeqElem(tKeypoints, i);
		nMatchB = FindNearestPoints(pA, surfA->laplacian, srcKeypoints, srcDescriptors, descriptor_size);
		if (nMatchB > 0)
		{
			pMatchPair[k].nA = i;
			pMatchPair[k].nB = nMatchB;
			k++;
		}
	}

	return k;
}
int FindNearestPoints(const float* pA, int laplacian, const CvSeq* srcKeypoints, const CvSeq* srcDescriptors, int descriptor_size)
{
	int i, k;
	float* pB;
	CvSURFPoint *surfB;
	int nMatch = -1;
	double sum2, min1 = 10000, min2 = 10000;
	for (i = 0; i < srcDescriptors->total; i++)
	{
		surfB = (CvSURFPoint*)cvGetSeqElem(srcKeypoints, i);
		pB = (float*)cvGetSeqElem(srcDescriptors, i);
		if (laplacian != surfB->laplacian)
			continue;

		sum2 = 0.0f;
		for (k = 0; k < descriptor_size; k++)	{ sum2 += (pA[k] - pB[k])*(pA[k] - pB[k]); }

		if (sum2 < min1)
		{
			min2 = min1;
			min1 = sum2;
			nMatch = i;
		}
		else if (sum2 < min2)	{ min2 = sum2; }
	}
	if (min1 < 0.6*min2)
		return nMatch;

	return -1;
}
void rotateImage(IplImage* src, IplImage* dst, int direction){

	if (src == NULL || dst == NULL){
		/*handle error*/
	}
	else if ((direction != ANGLE_CLOCKWISE_90) && (direction != ANGLE_COUNTER_CLOCKWISE_90)){
		/*handle error*/
	}
	else if ((src->width != dst->height) || (src->height != dst->width)){
		/*handle error*/
	}


	IplImage *srcB, *srcG, *srcR,
		*dstB, *dstG, *dstR;
	CvPoint2D32f centerPoint;
	CvMat* rotationMatrix = cvCreateMat(2, 3, CV_32FC1);

	if (src->nChannels == 3){
		srcB = cvCreateImage(cvGetSize(src), IPL_DEPTH_8U, 1);
		srcG = cvCreateImage(cvGetSize(src), IPL_DEPTH_8U, 1);
		srcR = cvCreateImage(cvGetSize(src), IPL_DEPTH_8U, 1);

		dstB = cvCreateImage(cvGetSize(dst), IPL_DEPTH_8U, 1);
		dstG = cvCreateImage(cvGetSize(dst), IPL_DEPTH_8U, 1);
		dstR = cvCreateImage(cvGetSize(dst), IPL_DEPTH_8U, 1);

		cvSplit(src, srcB, srcG, srcR, NULL);
	}

	/*calculate rotation matrix*/
	if (direction == ANGLE_COUNTER_CLOCKWISE_90){
		if (src->width > src->height) // wide image Okay
		{
			centerPoint = cvPoint2D32f(src->width / 2, src->width / 2);
			cv2DRotationMatrix(centerPoint, ANGLE_COUNTER_CLOCKWISE_90, 1, rotationMatrix);
		}
		else if (src->width < src->height) // narrow image Okay
		{
			centerPoint = cvPoint2D32f(src->width / 2, src->width / 2);
			cv2DRotationMatrix(centerPoint, ANGLE_COUNTER_CLOCKWISE_90, 1, rotationMatrix);
		}
		else // width == height  square image
		{
			centerPoint = cvPoint2D32f(src->width / 2, src->width / 2);
			cv2DRotationMatrix(centerPoint, ANGLE_COUNTER_CLOCKWISE_90, 1, rotationMatrix);
		}
	}
	else if (direction == ANGLE_CLOCKWISE_90){
		if (src->height < src->width) // wide image Okay
		{
			centerPoint = cvPoint2D32f(src->height / 2, src->height / 2);
			cv2DRotationMatrix(centerPoint, ANGLE_CLOCKWISE_90, 1, rotationMatrix);
		}
		else if (src->height > src->width) // narrow image
		{
			centerPoint = cvPoint2D32f(src->height / 2, src->height / 2);
			cv2DRotationMatrix(centerPoint, ANGLE_CLOCKWISE_90, 1, rotationMatrix);
		}
		else // height == width  square image
		{
			centerPoint = cvPoint2D32f(src->height / 2, src->height / 2);
			cv2DRotationMatrix(centerPoint, ANGLE_CLOCKWISE_90, 1, rotationMatrix);
		}
	}

	/*rotate*/
	if (src->nChannels == 3){
		cvWarpAffine(srcB, dstB, rotationMatrix, CV_INTER_LINEAR + CV_WARP_FILL_OUTLIERS);
		cvWarpAffine(srcG, dstG, rotationMatrix, CV_INTER_LINEAR + CV_WARP_FILL_OUTLIERS);
		cvWarpAffine(srcR, dstR, rotationMatrix, CV_INTER_LINEAR + CV_WARP_FILL_OUTLIERS);

		cvMerge(dstB, dstG, dstR, NULL, dst);

		cvReleaseImage(&srcB);
		cvReleaseImage(&srcG);
		cvReleaseImage(&srcR);
		cvReleaseImage(&dstB);
		cvReleaseImage(&dstG);
		cvReleaseImage(&dstR);
	}
	else if (src->nChannels == 1)
	{
		cvWarpAffine(src, dst, rotationMatrix, CV_INTER_LINEAR + CV_WARP_FILL_OUTLIERS);
	}

	cvReleaseMat(&rotationMatrix);
}
int findCropPointX(IplImage* src){

	//계산 시간을 줄이기 위해서 영상크기 줄임
	IplImage* down = cvCreateImage(cvSize(src->width / 2, src->height / 2), src->depth, src->nChannels);
	cvResize(src, down);

	int i = 0;
	double min_val = 0;
	double max_val = 0;
	int line_thickness = src->width/40;
	CvPoint max_loc, min_loc;
	int x = 0;

	IplImage* correlationImage = cvCreateImage(cvSize(1, 1), IPL_DEPTH_32F, 1);
	IplImage* rightLineImage = cvCreateImage(cvSize(line_thickness, down->height), down->depth, down->nChannels);

	cvSetZero(rightLineImage);
	cvResetImageROI(down);

	for (int i = line_thickness + 1; i < down->width / 2; i += line_thickness)
	{
		cvSetImageROI(down, cvRect(down->width - i, 0, line_thickness, down->height));
		cvMatchTemplate(rightLineImage, down, correlationImage, CV_TM_CCOEFF_NORMED);
		cvMinMaxLoc(correlationImage, &min_val, &max_val, &min_loc, &max_loc);
		cvResetImageROI(down);
		if (max_val < 0.5){
			x = down->width - i;
			break;
		}
	}

	cvReleaseImage(&down);
	cvReleaseImage(&correlationImage);
	cvReleaseImage(&rightLineImage);
	return x * 2;
}
void blending(IplImage* background, IplImage* mask){

	IplImage* bback = NULL;
	IplImage* mmask = NULL;
	int thick_back = 40;
	int thick_mask = 20;
	int height = background->height;
	int depth = background->depth;
	int channels = background->nChannels;
	CvPoint LTPos_back = cvPoint(background->width/2 - thick_back/2,0);
	CvPoint LTPos_mask = cvPoint(background->width/2 - thick_mask/2,0);

	cvSetImageROI(background, cvRect(LTPos_back.x, LTPos_back.y, thick_back, height));
	cvSetImageROI(mask, cvRect(LTPos_mask.x, LTPos_mask.y, thick_mask, height));
	bback = cvCreateImage(cvSize(thick_back, height), depth, channels);
	mmask = cvCreateImage(cvSize(thick_mask, height), depth, channels);
	cvCopy(background, bback);
	cvCopy(mask, mmask);
	cvSetZero(background);

	cvCopy(poisson_blend(bback, mmask, 0, (thick_back - thick_mask) / 2), background);

	cvResetImageROI(background);
	cvResetImageROI(mask);
	cvReleaseImage(&bback);
	cvReleaseImage(&mmask);
}

void getGradientx(const IplImage *img, IplImage *gx)
{
	int w = img->width;
	int h = img->height;
	int channel = img->nChannels;

	cvZero(gx);
	for (int i = 0; i < h; i++)
		for (int j = 0; j < w; j++)
			for (int c = 0; c < channel; ++c)
			{
				CV_IMAGE_ELEM(gx, float, i, j*channel + c) =
					(float)CV_IMAGE_ELEM(img, uchar, i, (j + 1)*channel + c) - (float)CV_IMAGE_ELEM(img, uchar, i, j*channel + c);
			}
}

void getGradienty(const IplImage *img, IplImage *gy)
{
	int w = img->width;
	int h = img->height;
	int channel = img->nChannels;

	cvZero(gy);
	for (int i = 0; i < h; i++)
		for (int j = 0; j < w; j++)
			for (int c = 0; c < channel; ++c)
			{
				CV_IMAGE_ELEM(gy, float, i, j*channel + c) =
					(float)CV_IMAGE_ELEM(img, uchar, (i + 1), j*channel + c) - (float)CV_IMAGE_ELEM(img, uchar, i, j*channel + c);

			}
}
void lapx(const IplImage *img, IplImage *gxx)
{
	int w = img->width;
	int h = img->height;
	int channel = img->nChannels;

	cvZero(gxx);
	for (int i = 0; i < h; i++)
		for (int j = 0; j < w - 1; j++)
			for (int c = 0; c < channel; ++c)
			{
				CV_IMAGE_ELEM(gxx, float, i, (j + 1)*channel + c) =
					(float)CV_IMAGE_ELEM(img, float, i, (j + 1)*channel + c) - (float)CV_IMAGE_ELEM(img, float, i, j*channel + c);
			}
}

void lapy(const IplImage *img, IplImage *gyy)
{
	int w = img->width;
	int h = img->height;
	int channel = img->nChannels;

	cvZero(gyy);
	for (int i = 0; i < h - 1; i++)
		for (int j = 0; j < w; j++)
			for (int c = 0; c < channel; ++c)
			{
				CV_IMAGE_ELEM(gyy, float, i + 1, j*channel + c) =
					(float)CV_IMAGE_ELEM(img, float, (i + 1), j*channel + c) - (float)CV_IMAGE_ELEM(img, float, i, j*channel + c);

			}
}

void dst(double *gtest, double *gfinal, int h, int w)
{

	int k, r, z;
	unsigned long int idx;

	Mat temp = Mat(2 * h + 2, 1, CV_32F);
	Mat res = Mat(h, 1, CV_32F);

	int p = 0;
	for (int i = 0; i < w; i++)
	{
		temp.at<float>(0, 0) = 0.0;

		for (int j = 0, r = 1; j < h; j++, r++)
		{
			idx = j*w + i;
			temp.at<float>(r, 0) = gtest[idx];
		}

		temp.at<float>(h + 1, 0) = 0.0;

		for (int j = h - 1, r = h + 2; j >= 0; j--, r++)
		{
			idx = j*w + i;
			temp.at<float>(r, 0) = -1 * gtest[idx];
		}

		Mat planes[] = { Mat_<float>(temp), Mat::zeros(temp.size(), CV_32F) };

		Mat complex1;
		merge(planes, 2, complex1);

		dft(complex1, complex1, 0, 0);

		Mat planes1[] = { Mat::zeros(complex1.size(), CV_32F), Mat::zeros(complex1.size(), CV_32F) };

		// planes1[0] = Re(DFT(I)), planes1[1] = Im(DFT(I))
		split(complex1, planes1);

		std::complex<double> two_i = std::sqrt(std::complex<double>(-1));

		double fac = -2 * imag(two_i);

		for (int c = 1, z = 0; c < h + 1; c++, z++)
		{
			res.at<float>(z, 0) = planes1[1].at<float>(c, 0) / fac;
		}

		for (int q = 0, z = 0; q < h; q++, z++)
		{
			idx = q*w + p;
			gfinal[idx] = res.at<float>(z, 0);
		}
		p++;
	}

}

void idst(double *gtest, double *gfinal, int h, int w)
{
	int nn = h + 1;
	unsigned long int idx;
	dst(gtest, gfinal, h, w);
	for (int i = 0; i < h; i++)
		for (int j = 0; j < w; j++)
		{
			idx = i*w + j;
			gfinal[idx] = (double)(2 * gfinal[idx]) / nn;
		}

}

void transpose(double *mat, double *mat_t, int h, int w)
{

	Mat tmp = Mat(h, w, CV_32FC1);
	int p = 0;
	unsigned long int idx;
	for (int i = 0; i < h; i++)
	{
		for (int j = 0; j < w; j++)
		{

			idx = i*(w)+j;
			tmp.at<float>(i, j) = mat[idx];
		}
	}
	Mat tmp_t = tmp.t();

	for (int i = 0; i < tmp_t.size().height; i++)
		for (int j = 0; j < tmp_t.size().width; j++)
		{
			idx = i*tmp_t.size().width + j;
			mat_t[idx] = tmp_t.at<float>(i, j);
		}

}
void poisson_solver(const IplImage *img, IplImage *gxx, IplImage *gyy, Mat &result)
{

	int w = img->width;
	int h = img->height;
	int channel = img->nChannels;

	unsigned long int idx, idx1;

	IplImage *lap = cvCreateImage(cvGetSize(img), 32, 1);

	for (int i = 0; i < h; i++)
		for (int j = 0; j < w; j++)
			CV_IMAGE_ELEM(lap, float, i, j) = CV_IMAGE_ELEM(gyy, float, i, j) + CV_IMAGE_ELEM(gxx, float, i, j);

	Mat bound(img);

	for (int i = 1; i < h - 1; i++)
		for (int j = 1; j < w - 1; j++)
		{
			bound.at<uchar>(i, j) = 0.0;
		}

	double *f_bp = new double[h*w];


	for (int i = 1; i < h - 1; i++)
		for (int j = 1; j < w - 1; j++)
		{
			idx = i*w + j;
			f_bp[idx] = -4 * (int)bound.at<uchar>(i, j) + (int)bound.at<uchar>(i, (j + 1)) + (int)bound.at<uchar>(i, (j - 1))
				+ (int)bound.at<uchar>(i - 1, j) + (int)bound.at<uchar>(i + 1, j);
		}


	Mat diff = Mat(h, w, CV_32FC1);
	for (int i = 0; i < h; i++)
	{
		for (int j = 0; j < w; j++)
		{
			idx = i*w + j;
			diff.at<float>(i, j) = (CV_IMAGE_ELEM(lap, float, i, j) - f_bp[idx]);
		}
	}
	double *gtest = new double[(h - 2)*(w - 2)];
	for (int i = 0; i < h - 2; i++)
	{
		for (int j = 0; j < w - 2; j++)
		{
			idx = i*(w - 2) + j;
			gtest[idx] = diff.at<float>(i + 1, j + 1);

		}
	}
	///////////////////////////////////////////////////// Find DST  /////////////////////////////////////////////////////

	double *gfinal = new double[(h - 2)*(w - 2)];
	double *gfinal_t = new double[(h - 2)*(w - 2)];
	double *denom = new double[(h - 2)*(w - 2)];
	double *f3 = new double[(h - 2)*(w - 2)];
	double *f3_t = new double[(h - 2)*(w - 2)];
	double *img_d = new double[(h)*(w)];

	dst(gtest, gfinal, h - 2, w - 2);

	transpose(gfinal, gfinal_t, h - 2, w - 2);

	dst(gfinal_t, gfinal, w - 2, h - 2);

	transpose(gfinal, gfinal_t, w - 2, h - 2);

	int cx = 1;
	int cy = 1;

	for (int i = 0; i < w - 2; i++, cy++)
	{
		for (int j = 0, cx = 1; j < h - 2; j++, cx++)
		{
			idx = j*(w - 2) + i;
			denom[idx] = (float)2 * cos(pi*cy / ((double)(w - 1))) - 2 + 2 * cos(pi*cx / ((double)(h - 1))) - 2;

		}
	}

	for (idx = 0; idx < (w - 2)*(h - 2); idx++)
	{
		gfinal_t[idx] = gfinal_t[idx] / denom[idx];
	}


	idst(gfinal_t, f3, h - 2, w - 2);

	transpose(f3, f3_t, h - 2, w - 2);

	idst(f3_t, f3, w - 2, h - 2);

	transpose(f3, f3_t, w - 2, h - 2);

	for (int i = 0; i < h; i++)
	{
		for (int j = 0; j < w; j++)
		{
			idx = i*w + j;
			img_d[idx] = (double)CV_IMAGE_ELEM(img, uchar, i, j);
		}
	}
	for (int i = 1; i < h - 1; i++)
	{
		for (int j = 1; j < w - 1; j++)
		{
			idx = i*w + j;
			img_d[idx] = 0.0;
		}
	}
	int id1, id2;
	for (int i = 1, id1 = 0; i < h - 1; i++, id1++)
	{
		for (int j = 1, id2 = 0; j < w - 1; j++, id2++)
		{
			idx = i*w + j;
			idx1 = id1*(w - 2) + id2;
			img_d[idx] = f3_t[idx1];
		}
	}

	for (int i = 0; i < h; i++)
	{
		for (int j = 0; j < w; j++)
		{
			idx = i*w + j;
			if (img_d[idx] < 0.0)
				result.at<uchar>(i, j) = 0;
			else if (img_d[idx] > 255.0)
				result.at<uchar>(i, j) = 255.0;
			else
				result.at<uchar>(i, j) = img_d[idx];
		}
	}

}

IplImage *poisson_blend(IplImage *I, IplImage *mask, int posx, int posy)
{

	unsigned long int idx;

	if (I->nChannels < 3)
	{
		printf("Enter RGB image\n");
		exit(0);
	}

	IplImage *grx = cvCreateImage(cvGetSize(I), 32, 3);
	IplImage *gry = cvCreateImage(cvGetSize(I), 32, 3);

	IplImage *sgx = cvCreateImage(cvGetSize(mask), 32, 3);
	IplImage *sgy = cvCreateImage(cvGetSize(mask), 32, 3);

	IplImage *S = cvCreateImage(cvGetSize(I), 8, 3);
	IplImage *ero = cvCreateImage(cvGetSize(I), 8, 3);
	IplImage *res = cvCreateImage(cvGetSize(I), 8, 3);

	cvZero(S);
	cvZero(res);


	IplImage *O = cvCreateImage(cvGetSize(I), 8, 3);
	IplImage *error = cvCreateImage(cvGetSize(I), 8, 3);


	int w = I->width;
	int h = I->height;
	int channel = I->nChannels;

	int w1 = mask->width;
	int h1 = mask->height;
	int channel1 = mask->nChannels;

	getGradientx(I, grx);
	getGradienty(I, gry);

	getGradientx(mask, sgx);
	getGradienty(mask, sgy);

	for (int i = posx, ii = 0; i < posx + h1; i++, ii++)
		for (int j = 0, jj = posy; j < w1; j++, jj++)
			for (int c = 0; c < channel; ++c)
			{
				CV_IMAGE_ELEM(S, uchar, i, jj*channel + c) = 255;
			}

	IplImage* bmaskx = cvCreateImage(cvGetSize(ero), 32, 3);
	cvConvertScale(S, bmaskx, 1.0 / 255.0, 0.0);

	IplImage* bmasky = cvCreateImage(cvGetSize(ero), 32, 3);
	cvConvertScale(S, bmasky, 1.0 / 255.0, 0.0);

	for (int i = posx, ii = 0; i < posx + h1; i++, ii++)
		for (int j = 0, jj = posy; j < w1; j++, jj++)
			for (int c = 0; c < channel; ++c)
			{
				CV_IMAGE_ELEM(bmaskx, float, i, jj*channel + c) = CV_IMAGE_ELEM(sgx, float, ii, j*channel + c);
				CV_IMAGE_ELEM(bmasky, float, i, jj*channel + c) = CV_IMAGE_ELEM(sgy, float, ii, j*channel + c);
			}

	cvErode(S, ero, NULL, 1);

	IplImage* smask = cvCreateImage(cvGetSize(ero), 32, 3);
	cvConvertScale(ero, smask, 1.0 / 255.0, 0.0);

	IplImage* srx32 = cvCreateImage(cvGetSize(res), 32, 3);
	cvConvertScale(res, srx32, 1.0 / 255.0, 0.0);

	IplImage* sry32 = cvCreateImage(cvGetSize(res), 32, 3);
	cvConvertScale(res, sry32, 1.0 / 255.0, 0.0);

	for (int i = 0; i < h; i++)
		for (int j = 0; j < w; j++)
			for (int c = 0; c < channel; ++c)
			{
				CV_IMAGE_ELEM(srx32, float, i, j*channel + c) =
					(CV_IMAGE_ELEM(bmaskx, float, i, j*channel + c)*CV_IMAGE_ELEM(smask, float, i, j*channel + c));
				CV_IMAGE_ELEM(sry32, float, i, j*channel + c) =
					(CV_IMAGE_ELEM(bmasky, float, i, j*channel + c)*CV_IMAGE_ELEM(smask, float, i, j*channel + c));
			}

	cvNot(ero, ero);

	IplImage* smask1 = cvCreateImage(cvGetSize(ero), 32, 3);
	cvConvertScale(ero, smask1, 1.0 / 255.0, 0.0);

	IplImage* grx32 = cvCreateImage(cvGetSize(res), 32, 3);
	cvConvertScale(res, grx32, 1.0 / 255.0, 0.0);

	IplImage* gry32 = cvCreateImage(cvGetSize(res), 32, 3);
	cvConvertScale(res, gry32, 1.0 / 255.0, 0.0);

	for (int i = 0; i < h; i++)
		for (int j = 0; j < w; j++)
			for (int c = 0; c < channel; ++c)
			{
				CV_IMAGE_ELEM(grx32, float, i, j*channel + c) =
					(CV_IMAGE_ELEM(grx, float, i, j*channel + c)*CV_IMAGE_ELEM(smask1, float, i, j*channel + c));
				CV_IMAGE_ELEM(gry32, float, i, j*channel + c) =
					(CV_IMAGE_ELEM(gry, float, i, j*channel + c)*CV_IMAGE_ELEM(smask1, float, i, j*channel + c));
			}


	IplImage* fx = cvCreateImage(cvGetSize(res), 32, 3);
	IplImage* fy = cvCreateImage(cvGetSize(res), 32, 3);

	for (int i = 0; i < h; i++)
		for (int j = 0; j < w; j++)
			for (int c = 0; c < channel; ++c)
			{
				CV_IMAGE_ELEM(fx, float, i, j*channel + c) =
					(CV_IMAGE_ELEM(grx32, float, i, j*channel + c) + CV_IMAGE_ELEM(srx32, float, i, j*channel + c));
				CV_IMAGE_ELEM(fy, float, i, j*channel + c) =
					(CV_IMAGE_ELEM(gry32, float, i, j*channel + c) + CV_IMAGE_ELEM(sry32, float, i, j*channel + c));
			}


	IplImage *gxx = cvCreateImage(cvGetSize(I), 32, 3);
	IplImage *gyy = cvCreateImage(cvGetSize(I), 32, 3);

	lapx(fx, gxx);
	lapy(fy, gyy);

	IplImage *rx_channel = cvCreateImage(cvGetSize(I), IPL_DEPTH_32F, 1);
	IplImage *gx_channel = cvCreateImage(cvGetSize(I), IPL_DEPTH_32F, 1);
	IplImage *bx_channel = cvCreateImage(cvGetSize(I), IPL_DEPTH_32F, 1);

	cvCvtPixToPlane(gxx, rx_channel, gx_channel, bx_channel, 0);

	IplImage *ry_channel = cvCreateImage(cvGetSize(I), IPL_DEPTH_32F, 1);
	IplImage *gy_channel = cvCreateImage(cvGetSize(I), IPL_DEPTH_32F, 1);
	IplImage *by_channel = cvCreateImage(cvGetSize(I), IPL_DEPTH_32F, 1);

	cvCvtPixToPlane(gyy, ry_channel, gy_channel, by_channel, 0);

	IplImage *r_channel = cvCreateImage(cvGetSize(I), 8, 1);
	IplImage *g_channel = cvCreateImage(cvGetSize(I), 8, 1);
	IplImage *b_channel = cvCreateImage(cvGetSize(I), 8, 1);

	cvCvtPixToPlane(I, r_channel, g_channel, b_channel, 0);

	Mat resultr = Mat(h, w, CV_8UC1);
	Mat resultg = Mat(h, w, CV_8UC1);
	Mat resultb = Mat(h, w, CV_8UC1);

	clock_t tic = clock();

	poisson_solver(r_channel, rx_channel, ry_channel, resultr);
	poisson_solver(g_channel, gx_channel, gy_channel, resultg);
	poisson_solver(b_channel, bx_channel, by_channel, resultb);

	clock_t toc = clock();

	printf("Execution time: %f seconds\n", (double)(toc - tic) / CLOCKS_PER_SEC);

	IplImage *final = cvCreateImage(cvGetSize(I), 8, 3);

	for (int i = 0; i < h; i++)
		for (int j = 0; j < w; j++)
		{
			CV_IMAGE_ELEM(final, uchar, i, j * 3 + 0) = resultr.at<uchar>(i, j);
			CV_IMAGE_ELEM(final, uchar, i, j * 3 + 1) = resultg.at<uchar>(i, j);
			CV_IMAGE_ELEM(final, uchar, i, j * 3 + 2) = resultb.at<uchar>(i, j);
		}

	return final;

}
