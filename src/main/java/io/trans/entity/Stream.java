package io.trans.entity;

import com.alibaba.fastjson2.annotation.JSONField;
import lombok.Data;

@Data
public class Stream {

    private int index;

    @JSONField(name = "codec_name")
    private String codecName;

    @JSONField(name = "codec_type")
    private String codecType;
}
