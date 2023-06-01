package com.biuqu.boot.handler;

import com.biuqu.constants.Const;
import com.biuqu.errcode.ErrCodeMgr;
import com.biuqu.exception.CommonException;
import com.biuqu.handler.BaseExceptionHandler;
import com.biuqu.model.ErrCode;
import com.biuqu.model.ResultCode;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.util.CollectionUtils;
import org.springframework.validation.ObjectError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.servlet.NoHandlerFoundException;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

/**
 * 全局Rest异常处理
 * <p>
 * 注意：使用@ControllerAdvice而不是@RestControllerAdvice的原因是范围更大，凡是异常都可以捕获
 *
 * @author BiuQu
 * @date 2023/2/1 09:29
 */
@ControllerAdvice
public class GlobalExceptionHandler extends BaseExceptionHandler
{
    @ExceptionHandler({CommonException.class, NoHandlerFoundException.class, Exception.class})
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    @ResponseBody
    public ResultCode<?> handleErr(HttpServletRequest req, Exception e)
    {
        return handle(req.getRequestURI(), e);
    }

    @ExceptionHandler({MethodArgumentNotValidException.class})
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    @ResponseBody
    public ResultCode<?> handleValidErr(HttpServletRequest req, MethodArgumentNotValidException e)
    {
        List<ObjectError> errors = e.getBindingResult().getAllErrors();
        if (CollectionUtils.isEmpty(errors))
        {
            //打下标准日志
            ErrCode errCode = getByUrl(req.getRequestURI(), ErrCodeMgr.getServerErr().getCode());
            return ResultCode.error(errCode.getCode());
        }
        else
        {
            for (ObjectError error : errors)
            {
                String code = error.getDefaultMessage();
                if (!StringUtils.isEmpty(code) && code.contains(Const.LINK))
                {
                    return ResultCode.build(ErrCodeMgr.getValid(code));
                }
            }
            return ResultCode.build(ErrCodeMgr.getServerErr());
        }
    }

    @Override
    protected ErrCode getByUrl(String url, String code)
    {
        LOGGER.error("current[{}] request happened err:{}", url, code);
        return ErrCodeMgr.get(code);
    }

    /**
     * 日志句柄
     */
    private static final Logger LOGGER = LoggerFactory.getLogger(GlobalExceptionHandler.class);
}
