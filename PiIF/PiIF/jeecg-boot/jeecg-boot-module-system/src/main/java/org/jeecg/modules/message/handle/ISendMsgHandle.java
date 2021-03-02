package org.jeecg.modules.message.handle;

import com.aliyuncs.exceptions.ClientException;

public interface ISendMsgHandle {

	void SendMsg(String es_receiver, String es_title, String es_content) throws ClientException;
}
