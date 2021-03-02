emqx数据持久化服务通过emqx的webhook实现，在使用前需开启emqx的emqx_web_hook插件

开启emqx_web_hook步骤

1.用docker命令运行一个emqx容器

2.将emqx_web_hook配置文件从容器中拷贝到宿主机

```
docker cp <容器ID>:/opt/emqx/etc/plugins/emqx_web_hook.conf /opt/emqx/node01/etc/
```

3.修改emqx_web_hook配置文件

```
###数据持久化地址
web.hook.api.url = http://192.168.2.75:7077/api/webHook/mysql
###触发事件
##web.hook.rule.client.connect.1       = {"action": "on_client_connect"}
##web.hook.rule.client.connack.1       = {"action": "on_client_connack"}
##web.hook.rule.client.connected.1     = {"action": "on_client_connected"}
##web.hook.rule.client.disconnected.1  = {"action": "on_client_disconnected"}
##web.hook.rule.client.subscribe.1     = {"action": "on_client_subscribe"}
##web.hook.rule.client.unsubscribe.1   = {"action": "on_client_unsubscribe"}
##web.hook.rule.session.subscribed.1   = {"action": "on_session_subscribed"}
##web.hook.rule.session.unsubscribed.1 = {"action": "on_session_unsubscribed"}
##web.hook.rule.session.terminated.1   = {"action": "on_session_terminated"}
web.hook.rule.message.publish.1      = {"action": "on_message_publish"}
##web.hook.rule.message.delivered.1    = {"action": "on_message_delivered"}
##web.hook.rule.message.acked.1        = {"action": "on_message_acked"}
```

4.运行带插件的emqx容器

```
docker run -d --name emqx_node01 -p 18083:18083 -p 1883:1883 -p 4369:4369 -p 8084:8084 -p 7083:8083 \
	-v /opt/emqx/node01/etc/emqx_web_hook.conf:/opt/emqx/etc/plugins/emqx_web_hook.conf \
	-e EMQX_NAME="emqx_node01" \
	-e EMQX_HOST=172.30.0.101 \
	-e EMQX_LISTENER__TCP__EXTERNAL=1883 \
	-e EMQX_LOADED_PLUGINS="emqx_auth_redis,emqx_recon,emqx_retainer,emqx_management,emqx_dashboard,emqx_web_hook" \
	-e EMQX_AUTH__REDIS__SERVER="192.168.56.104:6379" \
	-e EMQX_AUTH__REDIS__PASSWORD="123456" \
	-e EMQX_AUTH__REDIS__PASSWORD_HASH=sha256 \
	-e EMQX_ALLOW_ANONYMOUS=false \
	-e PLATFORM_ETC_DIR=/home/emqx/etc \
	-e PLATFORM_LOG_DIR=/home/emqx/log \
	-e PLATFORM_DATA_DIR=/home/emqx/data \
	--mount type=bind,source="$(pwd)"/etc,target=/home/emqx/etc \
	--mount type=bind,source="$(pwd)"/data,target=/home/emqx/data \
	--mount type=bind,source="$(pwd)"/log,target=/home/emqx/log \
	--network emqx \
	--ip 172.30.0.101 \
	emqx/emqx:v4.1.0-alpine-amd64
```

