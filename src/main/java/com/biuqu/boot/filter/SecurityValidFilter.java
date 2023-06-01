package com.biuqu.boot.filter;

import com.biuqu.boot.utils.ResponseUtil;
import com.biuqu.constants.Const;
import com.biuqu.encryption.Hash;
import com.biuqu.encryption.factory.EncryptionFactory;
import com.biuqu.errcode.ErrCodeEnum;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.http.HttpHeaders;
import org.apache.http.entity.ContentType;
import org.bouncycastle.util.encoders.Hex;
import org.springframework.beans.factory.annotation.Value;

import javax.servlet.*;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.nio.charset.StandardCharsets;

/**
 * 签名校验过滤器
 *
 * @author BiuQu
 * @date 2023/2/28 21:40
 */
@Slf4j
public class SecurityValidFilter implements Filter
{
    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain filterChain)
    throws IOException, ServletException
    {
        long start = System.currentTimeMillis();
        if (request instanceof HttpServletRequest)
        {
            HttpServletRequest req = (HttpServletRequest)request;
            String type = req.getContentType();
            String sign = req.getHeader(SIGN_KEY);
            if (!StringUtils.isEmpty(sign) && ContentType.APPLICATION_JSON.toString().contains(type))
            {
                CachingRequestWrapper requestWrapper = new CachingRequestWrapper(req);
                String authorization = req.getHeader(HttpHeaders.AUTHORIZATION);
                if (StringUtils.isEmpty(authorization))
                {
                    authorization = StringUtils.EMPTY;
                }
                String body = IOUtils.toString(requestWrapper.getContentAsByteArray(), StandardCharsets.UTF_8.name());
                String data = authorization + Const.LINK + body;
                byte[] hashBytes = HASH.digest(data.getBytes(StandardCharsets.UTF_8));
                String calcSign = Hex.toHexString(hashBytes);
                log.info("***json:{},sign:{},cost:{}ms", data, calcSign, System.currentTimeMillis() - start);
                if (!sign.equals(calcSign))
                {
                    String code = ErrCodeEnum.SIGNATURE_ERROR.getCode();
                    ResponseUtil.writeErrorBody((HttpServletResponse)response, code, snakeCase);
                    return;
                }
                log.info("cache and valid sign[{}] cost {}ms", req.getRequestURI(), System.currentTimeMillis() - start);
                filterChain.doFilter(requestWrapper, response);
                return;
            }
        }
        filterChain.doFilter(request, response);
    }

    /**
     * 签名key
     */
    private static final String SIGN_KEY = "sign";

    /**
     * 注入本地加密安全服务
     */
    private static final Hash HASH = EncryptionFactory.SHAHash.createAlgorithm();

    /**
     * 是否驼峰式json(默认支持)
     */
    @Value("${bq.json.snake-case:true}")
    private boolean snakeCase;
}
