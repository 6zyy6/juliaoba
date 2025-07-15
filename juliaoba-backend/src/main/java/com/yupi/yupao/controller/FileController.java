package com.yupi.yupao.controller;

import com.yupi.yupao.common.BaseResponse;
import com.yupi.yupao.common.ErrorCode;
import com.yupi.yupao.common.ResultUtils;
import com.yupi.yupao.exception.BusinessException;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import java.io.File;
import java.io.IOException;
import java.util.UUID;

/**
 * 文件上传接口
 */
@RestController
@RequestMapping("/file")
@CrossOrigin(origins = {"http://localhost:3000"})
@Slf4j
public class FileController {

    private static final String FILE_UPLOAD_PATH = "upload/";

    /**
     * 文件上传
     *
     * @param file
     * @param request
     * @return
     */
    @PostMapping("/upload")
    public BaseResponse<String> uploadFile(@RequestParam("file") MultipartFile file, HttpServletRequest request) {
        if (file.isEmpty()) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "文件为空");
        }
        // 文件名
        String fileName = file.getOriginalFilename();
        if (StringUtils.isBlank(fileName)) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "文件名为空");
        }
        // 校验文件大小
        long fileSize = file.getSize();
        // 限制文件大小为5MB
        if (fileSize > 5 * 1024 * 1024) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "文件大小不能超过5MB");
        }
        // 校验文件后缀
        String suffix = fileName.substring(fileName.lastIndexOf("."));
        if (!StringUtils.equalsAnyIgnoreCase(suffix, ".jpg", ".jpeg", ".png", ".gif")) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "文件格式不支持");
        }

        // 目录不存在则创建
        File directory = new File(FILE_UPLOAD_PATH);
        if (!directory.exists()) {
            if (!directory.mkdirs()) {
                throw new BusinessException(ErrorCode.SYSTEM_ERROR, "创建文件目录失败");
            }
        }

        // 生成随机文件名
        String uuid = UUID.randomUUID().toString().replaceAll("-", "");
        String newFileName = uuid + suffix;
        String filePath = FILE_UPLOAD_PATH + newFileName;

        try {
            // 保存文件
            file.transferTo(new File(filePath));
            // 返回可访问的URL
            String baseUrl = request.getScheme() + "://" + request.getServerName() + ":" + request.getServerPort() + "/api/";
            String fileUrl = baseUrl + filePath;
            return ResultUtils.success(fileUrl);
        } catch (IOException e) {
            log.error("文件上传失败", e);
            throw new BusinessException(ErrorCode.SYSTEM_ERROR, "文件上传失败");
        }
    }
} 