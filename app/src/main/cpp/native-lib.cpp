#include <jni.h>
#include <string>
#include <opencv2/core/core.hpp>
#include <opencv2/imgproc/imgproc.hpp>

using namespace std;
using namespace cv;


extern "C"{
jstring
    Java_opencvdm2_zj_com_opencvdemo2_MainActivity_stringFromJNI(
        JNIEnv *env,
        jobject /* this */) {
    std::string hello = "Hello from C++";
    return env->NewStringUTF(hello.c_str());
    }

    void Java_opencvdm2_zj_com_javaopencvdm2_JavaNativeActivity_salt(
            JNIEnv *env, jobject instance,
            jlong matArray,
            jint nbrElem
    ){
        Mat &mGr=*(Mat *)matArray;
        for (int k = 0; k < nbrElem; ++k) {
            int i=rand()%mGr.cols;
            int j=rand()%mGr.rows;
            mGr.at<uchar >(j,i)=255;
        }
    }

void Java_opencvdm2_zj_com_javaopencvdm2_JavaNativeActivity_fsMat(
        JNIEnv *env,jobject instance,jlong matArray
){
    Mat& mFs=*(Mat *)matArray;
    Mat element=getStructuringElement(MORPH_RECT,cvSize(15,15));
    Mat dstImage;
    erode(mFs,dstImage,element);
    mFs=dstImage;

}

void  Java_opencvdm2_zj_com_javaopencvdm2_JavaNativeActivity_mohuMat(
        JNIEnv *env,jobject instance,jlong matArray
){
    Mat& mMh=*(Mat *)matArray;
    Mat dstImage;
    blur(mMh,dstImage,cvSize(7,7));
    mMh=dstImage;
}

void Java_opencvdm2_zj_com_javaopencvdm2_JavaNativeActivity_canny(
        JNIEnv *env,jobject instance,jlong matArray
){
    Mat& mCanny=*(Mat*)matArray;
    Mat dstImage,edge;
    blur(mCanny,edge,Size(3,3));
    Canny(edge,dstImage,130,30,3);
    mCanny=dstImage;


}
//sobel算子边缘检测
void Java_opencvdm2_zj_com_javaopencvdm2_JavaNativeActivity_sobel(
        JNIEnv *env,jobject instance,jlong matArray
){
    Mat& matSobel=*(Mat*)matArray;
    Mat grad_x,grad_y;
    Mat abs_gradx,abs_grady,dst;
    //求X方向梯度
    Sobel(matSobel,grad_x,CV_16S,1,0,3,1,1,BORDER_DEFAULT);
    convertScaleAbs(grad_x,abs_gradx);
    //求Y方向上的梯度
    Sobel(matSobel,grad_y,CV_16S,0,1,3,1,1,BORDER_DEFAULT);
    convertScaleAbs(grad_y,abs_grady);
    addWeighted(abs_gradx,0.5,abs_grady,0.5,0,dst);
    matSobel=dst;

}

void Java_opencvdm2_zj_com_javaopencvdm2_JavaNativeActivity_laplacian(
        JNIEnv *env,jobject instance,jlong matArray
){
    Mat& matLap=*(Mat*)matArray;
    Mat gray_src,dst,abs_dst;
    //使用高斯消除噪声
    GaussianBlur(matLap,matLap,Size(3,3),0,0,BORDER_DEFAULT);
    //转换为灰度图
    cvtColor(matLap,gray_src,CV_RGB2GRAY);
    //使用Laplacian
    Laplacian(gray_src,dst,CV_16S,3,1,0,BORDER_DEFAULT);
//计算绝对值，并将结果转换为8位
    convertScaleAbs(dst,abs_dst);
    matLap=abs_dst;
}

//Scharr算子边缘检测
void Java_opencvdm2_zj_com_javaopencvdm2_JavaNativeActivity_scharr(
        JNIEnv *env,jobject instance,jlong matArray
){
    Mat& matScharr=*(Mat*)matArray;
    Mat grad_x,grad_y,abs_gradx,abs_grady,dst;
    //求X方向上的梯度
    Scharr(matScharr,grad_x,CV_16S,1,0,1,0,BORDER_DEFAULT);
    convertScaleAbs(grad_x,abs_gradx);
    //Y方向上梯度
    Scharr(matScharr,grad_y,CV_16S,0,1,1,0,BORDER_DEFAULT);
    convertScaleAbs(grad_y,abs_grady);
    addWeighted(abs_gradx,0.5,abs_grady,0.5,0,dst);
    matScharr=dst;
}


JNIEXPORT jintArray JNICALL Java_opencvdm2_zj_com_javaopencvdm2_JavaNativeActivity_grayProc(JNIEnv *env,jobject instance,jintArray buf,jint w,jint h){
    jint  *cbuf;
    jboolean ptfalse = false;
    cbuf=env->GetIntArrayElements(buf,&ptfalse);
}

JNIEXPORT void JNICALL
Java_opencvdm2_zj_com_gpuimage_GPUImageNativeLibrary_YUVtoARBG(JNIEnv *env, jclass type,
                                                               jbyteArray yuv420sp, jint width,
                                                               jint height, jintArray rgbOut) {
    int             sz;
    int             i;
    int             j;
    int             Y;
    int             Cr = 0;
    int             Cb = 0;
    int             pixPtr = 0;
    int             jDiv2 = 0;
    int             R = 0;
    int             G = 0;
    int             B = 0;
    int             cOff;
    int w = width;
    int h = height;
    sz = w * h;

    jint *rgbData = (jint*) ((*env)->GetPrimitiveArrayCritical(env, rgbOut, 0));
    jbyte* yuv = (jbyte*) (*env)->GetPrimitiveArrayCritical(env, yuv420sp, 0);

    for(j = 0; j < h; j++) {
        pixPtr = j * w;
        jDiv2 = j >> 1;
        for(i = 0; i < w; i++) {
            Y = yuv[pixPtr];
            if(Y < 0) Y += 255;
            if((i & 0x1) != 1) {
                cOff = sz + jDiv2 * w + (i >> 1) * 2;
                Cb = yuv[cOff];
                if(Cb < 0) Cb += 127; else Cb -= 128;
                Cr = yuv[cOff + 1];
                if(Cr < 0) Cr += 127; else Cr -= 128;
            }

            //ITU-R BT.601 conversion
            //
            //R = 1.164*(Y-16) + 2.018*(Cr-128);
            //G = 1.164*(Y-16) - 0.813*(Cb-128) - 0.391*(Cr-128);
            //B = 1.164*(Y-16) + 1.596*(Cb-128);
            //
            Y = Y + (Y >> 3) + (Y >> 5) + (Y >> 7);
            R = Y + (Cr << 1) + (Cr >> 6);
            if(R < 0) R = 0; else if(R > 255) R = 255;
            G = Y - Cb + (Cb >> 3) + (Cb >> 4) - (Cr >> 1) + (Cr >> 3);
            if(G < 0) G = 0; else if(G > 255) G = 255;
            B = Y + Cb + (Cb >> 1) + (Cb >> 4) + (Cb >> 5);
            if(B < 0) B = 0; else if(B > 255) B = 255;
            rgbData[pixPtr++] = 0xff000000 + (B << 16) + (G << 8) + R;
        }
    }

    (*env)->ReleasePrimitiveArrayCritical(env, rgbOut, rgbData, 0);
    (*env)->ReleasePrimitiveArrayCritical(env, yuv420sp, yuv, 0);
}

JNIEXPORT void JNICALL
Java_opencvdm2_zj_com_gpuimage_GPUImageNativeLibrary_YUVtoRBGA(JNIEnv *env, jclass type,
                                                               jbyteArray yuv420sp, jint width,
                                                               jint height, jintArray rgbOut) {
    int             sz;
    int             i;
    int             j;
    int             Y;
    int             Cr = 0;
    int             Cb = 0;
    int             pixPtr = 0;
    int             jDiv2 = 0;
    int             R = 0;
    int             G = 0;
    int             B = 0;
    int             cOff;
    int w = width;
    int h = height;
    sz = w * h;

    jint *rgbData = (jint*) ((*env)->GetPrimitiveArrayCritical(env, rgbOut, 0));
    jbyte* yuv = (jbyte*) (*env)->GetPrimitiveArrayCritical(env, yuv420sp, 0);

    for(j = 0; j < h; j++) {
        pixPtr = j * w;
        jDiv2 = j >> 1;
        for(i = 0; i < w; i++) {
            Y = yuv[pixPtr];
            if(Y < 0) Y += 255;
            if((i & 0x1) != 1) {
                cOff = sz + jDiv2 * w + (i >> 1) * 2;
                Cb = yuv[cOff];
                if(Cb < 0) Cb += 127; else Cb -= 128;
                Cr = yuv[cOff + 1];
                if(Cr < 0) Cr += 127; else Cr -= 128;
            }

            //ITU-R BT.601 conversion
            //
            //R = 1.164*(Y-16) + 2.018*(Cr-128);
            //G = 1.164*(Y-16) - 0.813*(Cb-128) - 0.391*(Cr-128);
            //B = 1.164*(Y-16) + 1.596*(Cb-128);
            //
            Y = Y + (Y >> 3) + (Y >> 5) + (Y >> 7);
            R = Y + (Cr << 1) + (Cr >> 6);
            if(R < 0) R = 0; else if(R > 255) R = 255;
            G = Y - Cb + (Cb >> 3) + (Cb >> 4) - (Cr >> 1) + (Cr >> 3);
            if(G < 0) G = 0; else if(G > 255) G = 255;
            B = Y + Cb + (Cb >> 1) + (Cb >> 4) + (Cb >> 5);
            if(B < 0) B = 0; else if(B > 255) B = 255;
            rgbData[pixPtr++] = 0xff000000 + (R << 16) + (G << 8) + B;
        }
    }

    (*env)->ReleasePrimitiveArrayCritical(env, rgbOut, rgbData, 0);
    (*env)->ReleasePrimitiveArrayCritical(env, yuv420sp, yuv, 0);
}


}
