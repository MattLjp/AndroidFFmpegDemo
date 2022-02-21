# For more information about using CMake with Android Studio, read the
# documentation: https://d.android.com/studio/projects/add-native-code.html

# Sets the minimum version of CMake required to build the native library.

#声明cmake版本号
cmake_minimum_required(VERSION 3.10.2)

# 支持C++11标准和GNU扩展特性
set(CMAKE_CXX_FLAGS "${CMAKE_CXX_FLAGS} -std=gnu++11")

#此处导入头文件目录
include_directories(${CMAKE_SOURCE_DIR}/ffmpeg/include)

#将so库文件路径赋值ffmpeg_lib_dir，方便操作
set(ffmpeg_lib_dir ${CMAKE_SOURCE_DIR}/../jniLibs/${ANDROID_ABI})

#项目名称
project("native-lib")


set(jnilibs ${CMAKE_SOURCE_DIR}/../jniLibs)

#引入动态库
link_directories(
        ${jnilibs}/${ANDROID_ABI})

add_library(
        # 输出so库的名称
        native-lib
        # 设置生成库的方式，默认为SHARE动态库
        SHARED
        # 列出参与编译的所有源文件
        native-lib.cpp

        # 工具
        ${CMAKE_SOURCE_DIR}/utils/logger.h
        ${CMAKE_SOURCE_DIR}/utils/timer.c

        # 播放器
        ${CMAKE_SOURCE_DIR}/media/player/def_player/player.cpp
        ${CMAKE_SOURCE_DIR}/media/player/gl_player/gl_player.cpp

        # 解码器
        ${CMAKE_SOURCE_DIR}/media/one_frame.h
        ${CMAKE_SOURCE_DIR}/media/decoder/i_decoder.h
        ${CMAKE_SOURCE_DIR}/media/decoder/decode_state.h
        ${CMAKE_SOURCE_DIR}/media/decoder/i_decode_state_cb.cpp
        ${CMAKE_SOURCE_DIR}/media/decoder/base_decoder.cpp
        ${CMAKE_SOURCE_DIR}/media/decoder/video/v_decoder.cpp
        ${CMAKE_SOURCE_DIR}/media/decoder/audio/a_decoder.cpp

        # 编码器
        ${CMAKE_SOURCE_DIR}/media/encoder/i_encoder.h
        ${CMAKE_SOURCE_DIR}/media/encoder/base_encoder.cpp
        ${CMAKE_SOURCE_DIR}/media/encoder/i_encode_state_cb.h
        ${CMAKE_SOURCE_DIR}/media/encoder/video/v_encoder.cpp
        ${CMAKE_SOURCE_DIR}/media/encoder/audio/a_encoder.cpp

        ${CMAKE_SOURCE_DIR}/media/const.h


        # 打包器
        ${CMAKE_SOURCE_DIR}/media/muxer/ff_repack.cpp

        ${CMAKE_SOURCE_DIR}/media/muxer/mp4_muxer.cpp
        ${CMAKE_SOURCE_DIR}/media/muxer/i_muxer_cb.h

        # 渲染器
        ${CMAKE_SOURCE_DIR}/media/render/video/video_render.h
        ${CMAKE_SOURCE_DIR}/media/render/video/native_render/native_render.cpp
        ${CMAKE_SOURCE_DIR}/media/render/video/opengl_render/opengl_render.cpp

        ${CMAKE_SOURCE_DIR}/media/render/audio/audio_render.h
        ${CMAKE_SOURCE_DIR}/media/render/audio/opensl_render.cpp
        ${CMAKE_SOURCE_DIR}/media/render/video/opengl_render/opengl_pixel_receiver.h

        # OpenGL EGL核心
        ${CMAKE_SOURCE_DIR}/opengl/egl/egl_core.cpp
        ${CMAKE_SOURCE_DIR}/opengl/egl/egl_surface.cpp

        # OpenGL绘制器
        ${CMAKE_SOURCE_DIR}/opengl/drawer/drawer.cpp
        ${CMAKE_SOURCE_DIR}/opengl/drawer/video_drawer.cpp

        # OpenGL绘制代理器
        ${CMAKE_SOURCE_DIR}/opengl/drawer/proxy/drawer_proxy.cpp
        ${CMAKE_SOURCE_DIR}/opengl/drawer/proxy/def_drawer_proxy_impl.cpp

        #合成器
        ${CMAKE_SOURCE_DIR}/media/synthesizer/synthesizer.cpp
)


#初始化log库
find_library( log-lib
        log)

##江指定的源文件生成链接文件
#add_library( avutil
#        SHARED
#        IMPORTED )
#
#set_target_properties( avutil
#        PROPERTIES IMPORTED_LOCATION
#        ${ffmpeg_lib_dir}/libavutil.so )
#
#add_library( swresample
#        SHARED
#        IMPORTED )
#set_target_properties( swresample
#        PROPERTIES IMPORTED_LOCATION
#        ${ffmpeg_lib_dir}/libswresample.so )
#
#add_library( avcodec
#        SHARED
#        IMPORTED )
#set_target_properties( avcodec
#        PROPERTIES IMPORTED_LOCATION
#        ${ffmpeg_lib_dir}/libavcodec.so )
#
#add_library( avfilter
#        SHARED
#        IMPORTED)
#set_target_properties( avfilter
#        PROPERTIES IMPORTED_LOCATION
#        ${ffmpeg_lib_dir}/libavfilter.so )
#
#add_library( swscale
#        SHARED
#        IMPORTED)
#set_target_properties( swscale
#        PROPERTIES IMPORTED_LOCATION
#        ${ffmpeg_lib_dir}/libswscale.so )
#
#add_library( avformat
#        SHARED
#        IMPORTED)
#set_target_properties( avformat
#        PROPERTIES IMPORTED_LOCATION
#        ${ffmpeg_lib_dir}/libavformat.so )
#
#add_library( postproc
#        SHARED
#        IMPORTED)
#set_target_properties( postproc
#        PROPERTIES IMPORTED_LOCATION
#        ${ffmpeg_lib_dir}/libpostproc.so )
#
#add_library( h264
#        SHARED
#        IMPORTED)
#set_target_properties( h264
#        PROPERTIES IMPORTED_LOCATION
#        ${ffmpeg_lib_dir}/libh264.so )
#
#add_library( avdevice
#        SHARED
#        IMPORTED)
#set_target_properties( avdevice
#        PROPERTIES IMPORTED_LOCATION
#        ${ffmpeg_lib_dir}/libavdevice.so )

#将目标文件与库文件进行链接
target_link_libraries( # Specifies the target library.
        # 指定目标库，add_library 中配置的目标库
        native-lib

        # 列出所有需要链接的库
        avutil
        swresample
        avcodec
        avfilter
        swscale
        avformat
        postproc
        h264
        avdevice

        -landroid
        OpenSLES
        -lEGL
        -lGLESv2

        ${log-lib}
        )