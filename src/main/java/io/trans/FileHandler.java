package io.trans;

import cn.hutool.core.collection.ListUtil;
import cn.hutool.core.io.FileUtil;
import cn.hutool.core.util.ArrayUtil;
import cn.hutool.core.util.RuntimeUtil;
import cn.hutool.core.util.StrUtil;
import com.alibaba.fastjson2.JSON;
import com.alibaba.fastjson2.JSONArray;
import com.alibaba.fastjson2.JSONObject;
import io.trans.entity.AudioSteam;
import io.trans.entity.Format;
import io.trans.entity.MediaInfo;
import io.trans.entity.VideoSteam;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.io.BufferedReader;
import java.io.File;
import java.io.InputStreamReader;
import java.util.List;
import java.util.Set;

@Slf4j
@Component
public class FileHandler {
    public static final Set<String> EXCLUDE_CODEC = Set.of("hevc", "av1");
    @Value("${vc:av1_nvenc}")
    private String videoCodec;
    @Value("${D:false}")
    private boolean afterDelete;

    /**
     * 处理文件
     *
     * @param file 视频文件
     */
    public void handle(File file) {
        MediaInfo mediaInfo = getFileMediaInfo(file);
        if (processVideo(mediaInfo)) {
            afterDelete(mediaInfo);
        }
    }

    @SneakyThrows
    private MediaInfo getFileMediaInfo(File file) {
        String result = RuntimeUtil.execForStr("ffprobe", "-v", "quiet", "-print_format", "json", "-show_format", "-show_streams", file.getAbsolutePath());
        MediaInfo mediaInfo = new MediaInfo();
        mediaInfo.setFile(file);
        JSONObject root = JSON.parseObject(result);
        Format format = root.getObject("format", Format.class);
        mediaInfo.setFormat(format);
        JSONArray streams = root.getJSONArray("streams");
        for (int i = 0; i < streams.size(); i++) {
            JSONObject stream = streams.getJSONObject(i);
            String codecType = stream.getString("codec_type");
            if ("video".equals(codecType)) {
                VideoSteam videoSteam = streams.getObject(i, VideoSteam.class);
                mediaInfo.setVideoSteam(videoSteam);
            } else if ("audio".equals(codecType)) {
                AudioSteam audioSteam = streams.getObject(i, AudioSteam.class);
                mediaInfo.setAudioSteam(audioSteam);
            } else {
                log.info("存在其他流，类型为：{}", codecType);
            }
        }
        return mediaInfo;
    }

    /**
     * 视频处理
     *
     * @param mediaInfo 视频信息
     * @return 是否处理
     */
    @SneakyThrows
    private boolean processVideo(MediaInfo mediaInfo) {
        List<String> cmd = ListUtil.toList("ffmpeg", "-i", mediaInfo.getFile().getAbsolutePath());
        if (!EXCLUDE_CODEC.contains(mediaInfo.getVideoSteam().getCodecName())) {
            cmd.add("-c:v");
            cmd.add(videoCodec);
        }
        if (!"yuv420p".equals(mediaInfo.getVideoSteam().getPixelFormat())) {
            cmd.add("-pix_fmt");
            cmd.add("yuv420p");
        }
        if (!"aac".equals(mediaInfo.getAudioSteam().getCodecName())) {
            cmd.add("-c:a");
            cmd.add("aac");
        }
        if (mediaInfo.getAudioSteam().getChannels() > 2) {
            cmd.add("-ac");
            cmd.add("2");
        }
        String fileName = mediaInfo.getFile().getAbsolutePath();
        String outputFileName = StrUtil.subPre(fileName, fileName.lastIndexOf(".")) + "-" + videoCodec.replace("_nvenc", "").toUpperCase() + ".mp4";
        cmd.add(outputFileName);
        if (cmd.size() < 5) {
            log.info("文件【{}】无需处理！", fileName);
            return false;
        }
        log.info(cmd.toString());
        Process process = RuntimeUtil.exec(ArrayUtil.toArray(cmd, String.class));
        BufferedReader stdout = new BufferedReader(new InputStreamReader(process.getInputStream()));
        String line;
        while ((line = stdout.readLine()) != null) {
            // 控制台输出ffmpeg日志
            System.out.println(line);
        }
        return true;
    }

    /**
     * 处理完成后是否删除文件
     *
     * @param mediaInfo 文件
     */
    private void afterDelete(MediaInfo mediaInfo) {
        if (afterDelete) {
            FileUtil.del(mediaInfo.getFile());
        }
    }
}
