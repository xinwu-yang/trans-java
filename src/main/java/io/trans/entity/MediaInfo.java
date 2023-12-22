package io.trans.entity;

import lombok.Data;

import java.io.File;

@Data
public class MediaInfo {

    private File file;

    private Format format;

    private VideoSteam videoSteam;

    private AudioSteam audioSteam;
}
