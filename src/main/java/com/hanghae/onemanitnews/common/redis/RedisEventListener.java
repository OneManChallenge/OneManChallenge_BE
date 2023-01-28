package com.hanghae.onemanitnews.common.redis;

import org.springframework.data.redis.connection.Message;
import org.springframework.data.redis.connection.MessageListener;
import org.springframework.stereotype.Component;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
public class RedisEventListener implements MessageListener {
	@Override
	public void onMessage(Message message, byte[] pattern) {
		log.info(new String(pattern) + " | " + message.toString());
	}
}
