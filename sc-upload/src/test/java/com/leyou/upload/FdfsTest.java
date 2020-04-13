package com.leyou.upload;

import com.github.tobato.fastdfs.domain.StorePath;
import com.github.tobato.fastdfs.domain.ThumbImageConfig;
import com.github.tobato.fastdfs.service.FastFileStorageClient;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.io.File;
import java.io.FileInputStream;
import java.util.Arrays;
import java.util.List;

@RunWith(SpringRunner.class)
@SpringBootTest
public class FdfsTest {
    //与FastDFS无关
    private static final List<String> ALLOW_TYPES= Arrays.asList("image/jpeg","image/png","image/bmp");
    @Autowired
    private FastFileStorageClient storageClient;

    @Autowired
    private ThumbImageConfig thumbImageConfig;

    @Test
    public void testUpload() throws Exception{
        File file=new File("D:\\IDEA\\item\\leyou\\image\\1.png");
        //上传并且生成缩略图
        StorePath storePath=this.storageClient.uploadFile(new FileInputStream(file),file.length(),"png",null);
        //带分组的路径
        System.out.println(storePath.getFullPath());
        //不带分组的路径
        System.out.println(storePath.getPath());
        System.out.println(ALLOW_TYPES);

    }

    @Test
    public void testUploadAndCteateThumb() throws Exception{
        File file=new File("D:\\IDEA\\item\\leyou\\image\\1.png");
        //上传并且生成缩略图
        StorePath storePath=this.storageClient.uploadImageAndCrtThumbImage(new FileInputStream(file),file.length(),"png",null);
        //带分组的路径
        System.out.println(storePath.getFullPath());
        //不带分组路径
        System.out.println(storePath.getPath());

        //获取带缩略图路径
        String path = thumbImageConfig.getThumbImagePath(storePath.getPath());
        System.out.println(path);

    }
}
