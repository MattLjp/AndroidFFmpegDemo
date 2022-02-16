# For more information about using CMake with Android Studio, read the
# documentation: https://d.android.com/studio/projects/add-native-code.html

# Sets the minimum version of CMake required to build the native library.

#声明cmake版本号
cmake_minimum_required(VERSION 3.10.2)

set(ffmpeg_head_dir ${CMAKE_SOURCE_DIR}/ffmpeg)

#此处导入头文件目录
include_directories(${ffmpeg_head_dir}/include)

#将so库文件路径赋值ffmpeg_lib_dir，方便操作
set(ffmpeg_lib_dir ${CMAKE_SOURCE_DIR}/../jniLibs/${ANDROID_ABI})

#项目名称
project("android-ffmpeg")


add_library(
        # 输出so库的名称
        android-ffmpeg
        # 设置生成库的方式，默认为SHARE动态库
        SHARED
        # 列出参与编译的所有源文件
        native-lib.cpp)


#初始化log库
find_library( log-lib
        log)

#江指定的源文件生成链接文件
add_library( avutil
        SHARED
        IMPORTED )

set_target_properties( avutil
        PROPERTIES IMPORTED_LOCATION
        ${ffmpeg_lib_dir}/libavutil.so )

add_library( swresample
        SHARED
        IMPORTED )
set_target_properties( swresample
        PROPERTIES IMPORTED_LOCATION
        ${ffmpeg_lib_dir}/libswresample.so )

add_library( avcodec
        SHARED
        IMPORTED )
set_target_properties( avcodec
        PROPERTIES IMPORTED_LOCATION
        ${ffmpeg_lib_dir}/libavcodec.so )

add_library( avfilter
        SHARED
        IMPORTED)
set_target_properties( avfilter
        PROPERTIES IMPORTED_LOCATION
        ${ffmpeg_lib_dir}/libavfilter.so )

add_library( swscale
        SHARED
        IMPORTED)
set_target_properties( swscale
        PROPERTIES IMPORTED_LOCATION
        ${ffmpeg_lib_dir}/libswscale.so )

add_library( avformat
        SHARED
        IMPORTED)
set_target_properties( avformat
        PROPERTIES IMPORTED_LOCATION
        ${ffmpeg_lib_dir}/libavformat.so )

add_library( postproc
        SHARED
        IMPORTED)
set_target_properties( postproc
        PROPERTIES IMPORTED_LOCATION
        ${ffmpeg_lib_dir}/libpostproc.so )

add_library( h264
        SHARED
        IMPORTED)
set_target_properties( h264
        PROPERTIES IMPORTED_LOCATION
        ${ffmpeg_lib_dir}/libh264.so )

#将目标文件与库文件进行链接
target_link_libraries( # Specifies the target library.
        # 指定目标库，add_library 中配置的目标库
        android-ffmpeg

        # 列出所有需要链接的库
        avutil
        swresample
        avcodec
        avfilter
        swscale
        avformat
        postproc
        h264

        ${log-lib}
        )