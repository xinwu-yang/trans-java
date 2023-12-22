package io.trans;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import java.io.File;
import java.util.Set;

@Slf4j
@SpringBootApplication
public class TransApplication implements CommandLineRunner {
    public static final Set<String> EXCLUDE_EXT = Set.of(".jpg", ".png");

    @Value("${d}")
    private String dir;
    @Value("${p:NOT_HANDLE}")
    private String excludePattern;
    @Value("${r:true}")
    private boolean recursive;
    private final FileHandler fileHandler;

    public TransApplication(FileHandler fileHandler) {
        this.fileHandler = fileHandler;
    }

    public static void main(String[] args) {
        SpringApplication.run(TransApplication.class, args);
    }

    @Override
    public void run(String... args) {
        String version = "2.0.0";
        log.info("Welcome to use Transcoding tool!");
        log.info("Current version: {}", version);

        File rootDir = new File(dir);
        File absRootDir = rootDir.getAbsoluteFile();
        readFiles(absRootDir);
    }

    /**
     * 读取目录文件夹和文件
     *
     * @param absRootDir 绝对路径
     */
    private void readFiles(File absRootDir) {
        File[] files = absRootDir.listFiles();
        if (files == null) {
            log.error("路径异常：{}", absRootDir.getPath());
            return;
        }
        for (File file : files) {
            String fileName = file.getName();
            if (file.isFile()) {
                if (isSkip(file)) {
                    log.info("--------------------------文件跳过--------------------------");
                    log.info("文件【{}】被标记不处理", fileName);
                    continue;
                }
                fileHandler.handle(file);
            } else if (recursive) {
                readFiles(file);
            }
        }
    }

    /**
     * 文件是否跳过处理
     *
     * @param file 文件
     * @return true 跳过 false 继续处理
     */
    private boolean isSkip(File file) {
        if (file.getName().contains(excludePattern)) {
            return true;
        }
        for (String ext : EXCLUDE_EXT) {
            if (file.getName().contains(ext)) {
                return true;
            }
        }
        return false;
    }
}
