package io.trans.entity;

import com.alibaba.fastjson2.annotation.JSONField;
import lombok.Data;

@Data
public class Format {

    @JSONField(name = "filename")
    private String fileName;

    @JSONField(name = "format_long_name")
    private String formatLongName;

    private float duration;

    private long size;

    @JSONField(name = "bit_rate")
    private long bitRate;
}
