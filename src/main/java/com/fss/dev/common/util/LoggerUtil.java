package com.fss.dev.common.util;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class LoggerUtil {
	
	Logger logger;
	
	public LoggerUtil(Class<?> claz) {
		logger = LoggerFactory.getLogger(claz);
	}
	
	public void fatal(Object msg) {
		logger.error((String) msg);
	}

	public void error(Object msg) {
		logger.error((String) msg);
	}

	public void error(Object msg, Throwable exception) {
		logger.error((String) msg, exception);
	}
	
	public void info(Object msg) {
			logger.info((String) msg);
	}
	
	public void debug(Object msg) {
			logger.debug((String) msg);
	}
	
	public void warn(Object msg) {
		logger.warn((String) msg);
	}

	protected boolean isTraceEnabled() {
		return logger.isTraceEnabled();
	}

	public void trace(Object msg) {
			logger.trace((String) msg);
	}


}
