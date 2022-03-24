/**
 *
 * Created by 公众号：字节流动 on 2021/3/16.
 * https://github.com/githubhaohao/LearnFFmpeg
 * 最新文章首发于公众号：字节流动，有疑问或者技术交流可以添加微信 Byte-Flow ,领取视频教程, 拉你进技术交流群
 *
 * */


#include <LogUtil.h>
#include "AudioDecoder.h"

void AudioDecoder::OnDecoderReady() {
    LOGCATE("AudioDecoder::OnDecoderReady");
    if(m_AudioRender) {
        AVCodecContext *codeCtx = GetCodecContext();

        //初始化格式转换工具
        m_SwrContext = swr_alloc();

        // 配置输入/输出通道类型
        av_opt_set_int(m_SwrContext, "in_channel_layout", codeCtx->channel_layout, 0);
        // 这里 AUDIO_DEST_CHANNEL_LAYOUT = AV_CH_LAYOUT_STEREO，即 立体声
        av_opt_set_int(m_SwrContext, "out_channel_layout", AUDIO_DST_CHANNEL_LAYOUT, 0);

        // 配置输入/输出采样率
        av_opt_set_int(m_SwrContext, "in_sample_rate", codeCtx->sample_rate, 0);
        av_opt_set_int(m_SwrContext, "out_sample_rate", AUDIO_DST_SAMPLE_RATE, 0);

        // 配置输入/输出数据格式
        av_opt_set_sample_fmt(m_SwrContext, "in_sample_fmt", codeCtx->sample_fmt, 0);
        av_opt_set_sample_fmt(m_SwrContext, "out_sample_fmt", DST_SAMPLT_FORMAT,  0);

        swr_init(m_SwrContext);

        LOGCATE("AudioDecoder::OnDecoderReady audio metadata sample rate: %d, channel: %d, format: %d, frame_size: %d, layout: %lld",
             codeCtx->sample_rate, codeCtx->channels, codeCtx->sample_fmt, codeCtx->frame_size,codeCtx->channel_layout);

        // 重采样后一个通道采样数
        m_nbSamples = (int)av_rescale_rnd(ACC_NB_SAMPLES, AUDIO_DST_SAMPLE_RATE, codeCtx->sample_rate, AV_ROUND_UP);
        // 重采样后一帧数据的大小
        m_DstFrameDataSze = av_samples_get_buffer_size(NULL, AUDIO_DST_CHANNEL_COUNTS,m_nbSamples, DST_SAMPLT_FORMAT, 1);

        LOGCATE("AudioDecoder::OnDecoderReady [m_nbSamples, m_DstFrameDataSze]=[%d, %d]", m_nbSamples, m_DstFrameDataSze);

        m_AudioOutBuffer = (uint8_t *) malloc(m_DstFrameDataSze);

        m_AudioRender->Init();

    } else {
        LOGCATE("AudioDecoder::OnDecoderReady m_AudioRender == null");
    }

}

void AudioDecoder::OnFrameAvailable(AVFrame *frame) {
    LOGCATE("AudioDecoder::OnFrameAvailable frame=%p, frame->nb_samples=%d", frame, frame->nb_samples);
    if(m_AudioRender) {
        // 转换，返回每个通道的样本数
        int result = swr_convert(m_SwrContext, &m_AudioOutBuffer, m_DstFrameDataSze / 2, (const uint8_t **) frame->data, frame->nb_samples);
        if (result > 0 ) {
            m_AudioRender->RenderAudioFrame(m_AudioOutBuffer, m_DstFrameDataSze);
        }
    }
}

void AudioDecoder::OnDecoderDone() {
    LOGCATE("AudioDecoder::OnDecoderDone");
    if(m_AudioRender)
        m_AudioRender->UnInit();

    if(m_AudioOutBuffer) {
        free(m_AudioOutBuffer);
        m_AudioOutBuffer = nullptr;
    }

    if(m_SwrContext) {
        swr_free(&m_SwrContext);
        m_SwrContext = nullptr;
    }
}

void AudioDecoder::ClearCache() {
    if(m_AudioRender)
        m_AudioRender->ClearAudioCache();
}
