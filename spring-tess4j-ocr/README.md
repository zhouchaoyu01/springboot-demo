# SpringBoot + Tess4J 实现本地与远程图片的文字识别

[SpringBoot + Tess4J 实现本地与远程图片的文字识别 (qq.com)](https://mp.weixin.qq.com/s/Wr-N_mY14yYxbMJVDoejjg)



```xml
<dependency>
            <groupId>net.sourceforge.tess4j</groupId>
            <artifactId>tess4j</artifactId>
            <version>4.5.4</version>
        </dependency>
```



**项目的 resources 文件夹下新建 tessdata 文件夹，然后把上面下载的 `.traineddata` 格式的语言模型文件复制到 tessdata 下。**



```
server:
  port: 8888

# 训练数据文件夹的路径
tess4j:
  datapath: D://Project//spring-framework-demos//spring-tess4j-ocr//src//main//resources//tessdata
```



```java
@Service
public class OcrService {
       @Value("${tess4j.datapath}")
    private String dataPath;


    public String recognizeText(File imageFile) throws TesseractException {
        Tesseract tesseract = new Tesseract();
        
        // 设定训练文件的位置（如果是标准英文识别，此步可省略）
        //tesseract.setDatapath("你的tessdata各语言集合包地址");
        tesseract.setDatapath(dataPath);
        tesseract.setLanguage("chi_sim");
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
```

在这段代码中，`recognizeText(File imageFile)`方法负责执行对本地文件的OCR任务，而`recognizeTextFromUrl(String imageUrl)`方法则先将远程图片下载到本地，然后再执行OCR。



```java
@RestController
@RequestMapping("/api/ocr")
public class OcrController {

    private final OcrService ocrService;

    // 使用构造器注入OcrService
    public OcrController(OcrService ocrService) {
        this.ocrService = ocrService;
    }

    @PostMapping("/upload")
    public ResponseEntity<String> uploadImage(@RequestParam("file") MultipartFile file) {
        try {
            File convFile = new File(System.getProperty("java.io.tmpdir")+"/"+file.getOriginalFilename());
            file.transferTo(convFile);
            String result = ocrService.recognizeText(convFile);
            return ResponseEntity.ok(result);
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.badRequest().body("识别发生错误：" + e.getMessage());
        }
    }

    @GetMapping("/recognize-url")
    public ResponseEntity<String> recognizeFromUrl(@RequestParam("imageUrl") String imageUrl) {
        try {
            String result = ocrService.recognizeTextFromUrl(imageUrl);
            return ResponseEntity.ok(result);
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.badRequest().body("从URL识别发生错误：" + e.getMessage());
        }
    }
}
```

在这个控制器中，我们创建了两个端点：`/api/ocr/upload`用于处理用户上传的本地图片，而`/api/ocr/recognize-url`则处理给定URL的远程图片。



bug:

```
Error opening data file src/main/resources/tessdata/chi_sim.traineddata
Please make sure the TESSDATA_PREFIX environment variable is set to your "tessdata" directory.
Failed loading language 'chi_sim'
Tesseract couldn't load any languages!
Warning: Invalid resolution 0 dpi. Using 70 instead.
```

改完配置文件方式

