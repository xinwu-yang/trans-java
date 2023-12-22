# A video transcoding tool

### 功能

- 指定目录下视频转码`av1`
- 指定目录下音频转码`aac`
- 像素格式转化为`yuv420p`
- 声道数量变更为`2`
- 支持GPU加速(默认)
- 支持递归目录

### 如何使用

1. 下载[FFmpeg](https://www.gyan.dev/ffmpeg/builds/)

2. 配置把ffmpeg下`bin`目录添加到系统PATH环境变量

3. 如何使用

```
Usage:
  -D    处理完成后是否删除源文件    
  -d string
        视频路径 (default "./")
  -p string
        指定pattern跳过处理(文件名) (default "NOT-HANDLE")
  -r    是否递归子目录(useage: -r=false) (default true)   
  -vc string
        视频编码 (default "av1_nvenc")

# 基本使用
java -jar trans-x.x.x.jar -d "D:\\demo-video\\test"

# 使用GPU转码
java -jar trans-x.x.x.jar -d "D:\\demo-video\\test" -vc hevc_nvenc
```
