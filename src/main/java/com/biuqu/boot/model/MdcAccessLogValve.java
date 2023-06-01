package com.biuqu.boot.model;

import com.biuqu.boot.constants.CommonBootConst;
import com.biuqu.constants.Const;
import lombok.extern.slf4j.Slf4j;
import org.apache.catalina.valves.AccessLogValve;
import org.springframework.cloud.sleuth.TraceContext;
import org.springframework.util.ClassUtils;

import java.io.CharArrayWriter;

/**
 * MDC AccessLog日志对象
 *
 * @author BiuQu
 * @date 2023/3/12 23:49
 */
@Slf4j
public class MdcAccessLogValve extends AccessLogValve
{
    @Override
    public void log(CharArrayWriter message)
    {
        log.info(message.toString());
    }

    @Override
    protected AccessLogElement createAccessLogElement(String name, char pattern)
    {
        if (pattern == CommonBootConst.TRACE_TAG)
        {
            return (buf, date, request, response, time) ->
            {
                //兼容没有sleuth时的场景
                boolean existTrace = ClassUtils.isPresent(SLEUTH_TYPE, this.getClass().getClassLoader());
                if (!existTrace)
                {
                    buf.append(Const.MID_LINK);
                    return;
                }

                Object context = request.getRequest().getAttribute(TraceContext.class.getName());
                if (!(context instanceof TraceContext))
                {
                    return;
                }
                TraceContext traceContext = (TraceContext)context;
                if (CommonBootConst.TRACE_ID.equalsIgnoreCase(name))
                {
                    buf.append(traceContext.traceId());
                }
                else if (CommonBootConst.SPAN_ID.equalsIgnoreCase(name))
                {
                    buf.append(traceContext.spanId());
                }
            };
        }
        return super.createAccessLogElement(name, pattern);
    }

    /**
     * Sleuth存在的key
     */
    private static final String SLEUTH_TYPE = "org.springframework.cloud.sleuth.TraceContext";
}
