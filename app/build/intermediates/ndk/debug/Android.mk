LOCAL_PATH := $(call my-dir)
include $(CLEAR_VARS)

LOCAL_MODULE := app
LOCAL_LDFLAGS := -Wl,--build-id
LOCAL_SRC_FILES := \
	C:\Android\Projects\Scan2DServer_4G\app\src\main\jni\Android.mk \
	C:\Android\Projects\Scan2DServer_4G\app\src\main\jni\Application.mk \
	C:\Android\Projects\Scan2DServer_4G\app\src\main\jni\za_co_facegroup_zar_License.cpp \

LOCAL_C_INCLUDES += C:\Android\Projects\Scan2DServer_4G\app\src\main\jni
LOCAL_C_INCLUDES += C:\Android\Projects\Scan2DServer_4G\app\src\debug\jni

include $(BUILD_SHARED_LIBRARY)
