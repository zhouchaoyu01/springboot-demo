package com.coding.service;

import net.sourceforge.tess4j.Tesseract;
import net.sourceforge.tess4j.TesseractException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.InputStream;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;

/**
 * @description <>
 * @author: zhouchaoyu
 * @Date: 2024-02-19
 */
@Service
public class OcrService {


    @Value("${tess4j.datapath}")
    private String dataPath;

    public String recognizeText(File imageFile) throws TesseractException {
        Tesseract tesseract = new Tesseract();

        // 设定训练文件的位置（如果是标准英文识别，此步可省略）
        tesseract.setDatapath(dataPath);
        tesseract.setLanguage("chi_sim");
        //使用 OSD 进行自动页面分割以进行图像处理
        tesseract.setPageSegMode(1);
        //设置引擎模式是神经网络LSTM引擎
        tesseract.setOcrEngineMode(1);
        return tesseract.doOCR(imageFile);
    }

    public String recognizeTextFromUrl(String imageUrl) throws Exception {
        URL url = new URL(imageUrl);
        InputStream in = url.openStream();
        Files.copy(in, Paths.get("downloaded.jpg"), StandardCopyOption.REPLACE_EXISTING);

        File imageFile = new File("downloaded.jpg");
        return recognizeText(imageFile);
    }
}