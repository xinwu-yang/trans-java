package io.trans.entity;

import com.alibaba.fastjson2.annotation.JSONField;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
public class VideoSteam extends Stream {

    @JSONField(name = "pix_fmt")
    private String pixelFormat;
}
