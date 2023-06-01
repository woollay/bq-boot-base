package com.biuqu.boot.filter;

import org.apache.commons.io.IOUtils;
import org.springframework.web.util.ContentCachingRequestWrapper;

import javax.servlet.ReadListener;
import javax.servlet.ServletInputStream;
import javax.servlet.http.HttpServletRequest;
import java.io.ByteArrayInputStream;
import java.io.IOException;

/**
 * 带缓存的HttpServletRequestWrapper
 * <p>
 * 参考:https://cloud.tencent.com/developer/ask/sof/82496
 * <p>
 * 思路：
 * 先从request对象中读取body二进制并缓存，后面每次再读取时，获取其缓存副本即可
 *
 * @author BiuQu
 * @date 2023/2/28 22:45
 */
public class CachingRequestWrapper extends ContentCachingRequestWrapper
{
    public CachingRequestWrapper(HttpServletRequest request)
    {
        super(request);
        // init cache in ContentCachingRequestWrapper
        super.getParameterMap();
        // first option for application/x-www-form-urlencoded
        body = super.getContentAsByteArray();
        if (body.length == 0)
        {
            try
            {
                // second option for other body formats
                body = IOUtils.toByteArray(super.getInputStream());
            }
            catch (IOException ex)
            {
                body = new byte[0];
            }
        }
    }

    @Override
    public ServletInputStream getInputStream()
    {
        return new CachingRequestInputStream(body);
    }

    /**
     * body二进制
     */
    private byte[] body;

    private static class CachingRequestInputStream extends ServletInputStream
    {
        public CachingRequestInputStream(byte[] bytes)
        {
            inputStream = new ByteArrayInputStream(bytes);
        }

        @Override
        public int read()
        {
            return inputStream.read();
        }

        @Override
        public boolean isFinished()
        {
            return inputStream.available() == 0;
        }

        @Override
        public boolean isReady()
        {
            return true;
        }

        @Override
        public void setReadListener(ReadListener readlistener)
        {
        }

        private final ByteArrayInputStream inputStream;
    }
}
