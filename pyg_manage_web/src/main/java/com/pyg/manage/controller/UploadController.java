package com.pyg.manage.controller;

import com.pyg.entity.Result;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import util.FastDFSClient;

/**
 * created by 沈小云 on 2018/10/16  16:14
 */
@RestController
public class UploadController {
    @Value("${FILE_SERVER_URL}")
    private String FILE_SERVER_URL;

    @RequestMapping("/upload")
    public Result uploadFile(MultipartFile file){
        String filename = file.getOriginalFilename();
        String extName = filename.substring(filename.lastIndexOf(".")+1);
        try{
            FastDFSClient fastDFSClient = new FastDFSClient("classpath:config/fdfs_client.conf");
            String path = fastDFSClient.uploadFile(file.getBytes(), extName);
            String url = FILE_SERVER_URL + path;
            return  new Result(true,url);
        }catch (Exception e){
            e.printStackTrace();
            return new Result(false,"上传失败");
        }
    }
}
