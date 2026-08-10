package com.aliyun.utils;

import com.aliyun.oss.common.utils.BinaryUtil;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.nio.charset.StandardCharsets;
import java.security.KeyFactory;
import java.security.PublicKey;
import java.security.spec.X509EncodedKeySpec;


@Slf4j
@WebServlet(asyncSupported = true)
public final class AliyunOssCallbackValidateUtils extends HttpServlet {

    private static final String HTTPS_GOSSPUBLIC_ALICDN_COM = "https://gosspublic.alicdn.com/";
    private static final String HTTP_GOSSPUBLIC_ALICDN_COM = "http://gosspublic.alicdn.com/";


    /**
     * 验证阿里云 OSS 回调请求的签名并处理响应
     *
     * <p>该方法执行以下操作：</p>
     * <ul>
     *   <li>从请求体中读取 OSS 回调内容</li>
     *   <li>验证请求签名的合法性</li>
     *   <li>根据验签结果返回相应的 HTTP 响应</li>
     * </ul>
     *
     * @param request HTTP 请求对象，包含 OSS 回调的请求头和请求体
     * @param response HTTP 响应对象，用于向 OSS 返回验签结果
     * @return OSS 回调的请求体内容，如果验签失败或发生异常则返回空字符串
     * @throws IOException 读取请求流或写入响应时发生 IO 异常
     */
    public String validate(HttpServletRequest request, HttpServletResponse response) throws IOException {
        String ossCallbackBody = "";
        try {
            ossCallbackBody = GetPostBody(request.getInputStream(), Integer.parseInt(request.getHeader("content-length")));
            boolean ret = VerifyOSSCallbackRequest(request, ossCallbackBody);
            log.info("AliyunOssCallbackValidateUtils verify result: {}", ret);
            log.info("AliyunOssCallbackValidateUtils OSS Callback Body: {}", ossCallbackBody);

            if (ret) {
                // 成功：返回请求头内容数据或标准成功标识
                response.addHeader("Content-Type", "application/json");
                response(request, response, "{\"Status\":\"OK\"}", HttpServletResponse.SC_OK);
            } else {
                // 验签失败：返回空
                response(request, response, "", HttpServletResponse.SC_BAD_REQUEST);
            }
        } catch (Exception e) {
            log.error("AliyunOssCallbackValidateUtils validate 处理 OSS 回调异常", e);
            // 异常：返回空
            response(request, response, "", HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
        }
        return ossCallbackBody;
    }

    private String executeGet(String url) throws IOException {
        java.net.URL serverUrl = new java.net.URL(url);
        HttpURLConnection conn = (HttpURLConnection) serverUrl.openConnection();
        conn.setRequestMethod("GET");
        conn.setConnectTimeout(5000);
        conn.setReadTimeout(5000);

        StringBuilder sb = new StringBuilder();
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(conn.getInputStream()))) {
            String line;
            while ((line = reader.readLine()) != null) {
                sb.append(line);
            }
        } finally {
            conn.disconnect();
        }
        return sb.toString();
    }

    private String GetPostBody(InputStream is, int contentLen) {
        if (contentLen > 0) {
            int readLen = 0;
            int readLengthThisTime = 0;
            byte[] message = new byte[contentLen];
            try {
                while (readLen != contentLen) {
                    readLengthThisTime = is.read(message, readLen, contentLen - readLen);
                    if (readLengthThisTime == -1) {// Should not happen.
                        break;
                    }
                    readLen += readLengthThisTime;
                }
                return new String(message, StandardCharsets.UTF_8);
            } catch (IOException e) {
                log.error("AliyunOssCallbackValidateUtils GetPostBody 读取回调请求体失败", e);
            }
        }
        return "";
    }


    private boolean VerifyOSSCallbackRequest(HttpServletRequest request, String ossCallbackBody) throws Exception {
        boolean ret;
        String autorizationInput = request.getHeader("Authorization");
        String pubKeyInput = request.getHeader("x-oss-pub-key-url");

        if (autorizationInput == null || pubKeyInput == null) {
            return false;
        }

        byte[] authorization = BinaryUtil.fromBase64String(autorizationInput);
        byte[] pubKey = BinaryUtil.fromBase64String(pubKeyInput);
        String pubKeyAddr = new String(pubKey, StandardCharsets.UTF_8);

        if (!pubKeyAddr.startsWith(HTTP_GOSSPUBLIC_ALICDN_COM) && !pubKeyAddr.startsWith(HTTPS_GOSSPUBLIC_ALICDN_COM)) {
            log.warn("AliyunOssCallbackValidateUtils VerifyOSSCallbackRequest 公钥地址非法: {}", pubKeyAddr);
            return false;
        }

        String retString = executeGet(pubKeyAddr);
        retString = retString.replace("-----BEGIN PUBLIC KEY-----", "");
        retString = retString.replace("-----END PUBLIC KEY-----", "").replace("\n", "").replace("\r", "");

        String queryString = request.getQueryString();
        String uri = request.getRequestURI();
        String authStr = java.net.URLDecoder.decode(uri, StandardCharsets.UTF_8);
        if (queryString != null && !queryString.isEmpty()) {
            authStr += "?" + queryString;
        }
        authStr += "\n" + ossCallbackBody;
        ret = doCheck(authStr, authorization, retString);
        return ret;
    }



    private boolean doCheck(String content, byte[] sign, String publicKey) {
        try {
            KeyFactory keyFactory = KeyFactory.getInstance("RSA");
            byte[] encodedKey = BinaryUtil.fromBase64String(publicKey);
            PublicKey pubKey = keyFactory.generatePublic(new X509EncodedKeySpec(encodedKey));
            java.security.Signature signature = java.security.Signature.getInstance("MD5withRSA");
            signature.initVerify(pubKey);
            signature.update(content.getBytes(StandardCharsets.UTF_8));
            return signature.verify(sign);

        } catch (Exception e) {
            log.error("AliyunOssCallbackValidateUtils doCheck RSA 签名校验异常", e);
        }

        return false;
    }

    private void response(HttpServletRequest request, HttpServletResponse response, String results, int status) throws IOException {
        String callbackFunName = request.getParameter("callback");
        response.addHeader("Content-Length", String.valueOf(results.length()));
        if (callbackFunName == null || callbackFunName.equalsIgnoreCase("")) response.getWriter().println(results);
        else response.getWriter().println(callbackFunName + "( " + results + " )");
        response.setStatus(status);
        response.flushBuffer();
    }

}
