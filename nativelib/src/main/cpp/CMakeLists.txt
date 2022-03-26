#声明cmake版本号
cmake_minimum_required(VERSION 3.10.2)

# 支持C++11标准和GNU扩展特性
set(CMAKE_CXX_FLAGS "${CMAKE_CXX_FLAGS} -std=gnu++11")

#项目名称
project("ffmpeglib")

set(libname ffmpeglib)
set(jnilibs ${CMAKE_SOURCE_DIR}/../jniLibs)


#此处导入头文件目录
include_directories(
        include
        glm
        common
        ${CMAKE_SOURCE_DIR}/recorder
        ${CMAKE_SOURCE_DIR}/util
        ${CMAKE_SOURCE_DIR}/player
        ${CMAKE_SOURCE_DIR}/player/decoder
        ${CMAKE_SOURCE_DIR}/player/render
        ${CMAKE_SOURCE_DIR}/player/render/audio
        ${CMAKE_SOURCE_DIR}/player/render/video
)


#引入动态库
link_directories(
        ${jnilibs}/${ANDROID_ABI})

file(GLOB src-files
        ${CMAKE_SOURCE_DIR}/*.cpp
        ${CMAKE_SOURCE_DIR}/util/*.cpp
        ${CMAKE_SOURCE_DIR}/common/*.cpp
        ${CMAKE_SOURCE_DIR}/player/*.cpp
        ${CMAKE_SOURCE_DIR}/player/decoder/*.cpp
        ${CMAKE_SOURCE_DIR}/player/render/video/*.cpp
        ${CMAKE_SOURCE_DIR}/player/render/audio/*.cpp
        ${CMAKE_SOURCE_DIR}/recorder/SingleVideoRecorder.cpp
        ${CMAKE_SOURCE_DIR}/recorder/SingleAudioRecorder.cpp
        ${CMAKE_SOURCE_DIR}/recorder/MediaRecorderContext.cpp
        ${CMAKE_SOURCE_DIR}/recorder/GLCameraRender.cpp
        ${CMAKE_SOURCE_DIR}/recorder/MediaRecorder.cpp
        )

add_library(
        # 输出so库的名称
        ${libname}

        # 设置生成库的方式，默认为SHARE动态库
        SHARED

        # 列出参与编译的所有源文件
        ${src-files}
)


#初始化log库
find_library( # Sets the name of the path variable.
        log-lib

        # Specifies the name of the NDK library that
        # you want CMake to locate.
        log)

set(third-party-libs
        avformat
        avcodec
        avfilter
        swresample
        swscale
        avutil
#        fdk-aac
#        x264
        )

set(native-libs
        android
        mediandk
        EGL
        GLESv3
        OpenSLES
        log
        m
        z
        )

#将目标文件与库文件进行链接
target_link_libraries(
        # 指定目标库，add_library 中配置的目标库
        ${libname}

        # 列出所有需要链接的库
        ${log-lib}
        ${third-party-libs}
        ${native-libs}
        )