package com.leyou.upload.service;

import com.github.tobato.fastdfs.domain.StorePath;
import com.github.tobato.fastdfs.service.FastFileStorageClient;
import com.leyou.common.enums.ExceptionEnum;
import com.leyou.common.exception.LyException;
import com.leyou.upload.config.UploadProperties;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;

@Service
@Slf4j
@EnableConfigurationProperties(UploadProperties.class)      //使，使用 @ConfigurationProperties 注解的类生效，把类UploadProperties注入到IOC容器中。
public class UploadService {
    @Autowired
    private FastFileStorageClient storageClient;
    @Autowired
    private UploadProperties prop;
    //private static final List<String> ALLOW_TYPES= Arrays.asList("image/jpeg","image/png","image/bmp");
    public String uploadImage(MultipartFile file) {

        try {
            //校验文件类型
            String contentType = file.getContentType();//返回文件的内容类型
            System.out.println("文件类型："+contentType);
            if (!prop.getAllowTypes().contains(contentType)){   ///当且仅当此字符串包含指定的char值序列时才返回true。
                throw new LyException(ExceptionEnum.UPLOAD_ERROR);
            }
            //校验文件内容
            BufferedImage image = ImageIO.read(file.getInputStream());
            if (image == null){
                throw new LyException(ExceptionEnum.UPLOAD_ERROR);
            }
            //上传到FastDFS
            //获取后缀名
            String extension= StringUtils.substringAfterLast(file.getOriginalFilename(),".");
            StorePath storePath = storageClient.uploadFile(file.getInputStream(), file.getSize(), extension, null);
            //返回路径
            return prop.getBaseUrl()+storePath.getFullPath();

        } catch (IOException e) {
            log.error("上传失败",e);        //日志
            throw new LyException(ExceptionEnum.UPLOAD_ERROR);
        }

    }
}
