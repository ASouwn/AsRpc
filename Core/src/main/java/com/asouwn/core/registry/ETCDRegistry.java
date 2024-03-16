package com.asouwn.core.registry;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.collection.ConcurrentHashSet;
import cn.hutool.cron.CronUtil;
import cn.hutool.cron.task.Task;
import cn.hutool.json.JSONUtil;
import com.asouwn.core.config.RegistryConfig;
import com.asouwn.core.model.ServerMetaInfo;
import io.etcd.jetcd.*;
import io.etcd.jetcd.options.GetOption;
import io.etcd.jetcd.watch.WatchEvent;
import lombok.extern.slf4j.Slf4j;

import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.ExecutionException;
import java.util.stream.Collectors;

@Slf4j
public class ETCDRegistry {
    private Client client;
    private KV kvClient;
    private final String PreFixDir = "/asrpc/";
    private final Set<String> nodeKeySet = new HashSet<>();
    private final Set<String> watchKeySet = new ConcurrentHashSet<>();

    /**
     * 建立与第三方注册服务中心的连接
     * @param registryConfig 第三方注册中心的配置
     */
    public void init(RegistryConfig registryConfig){
        log.info("尝试接入 {} 配置服务中心", registryConfig.getRegistryName());
        client = Client.builder()
                .endpoints(registryConfig.getRegistryAddr())
                .connectTimeout(Duration.ofMillis(registryConfig.getTimeout()))
                .build();
        kvClient = client.getKVClient();
        keepAlive();
    }

    /**
     * 将服务写入注册中心
     * @param serverMetaInfo 服务元信息，主要提供服务的访问名称与地址
     * @throws ExecutionException
     * @throws InterruptedException
     */
    public void register(ServerMetaInfo serverMetaInfo) throws ExecutionException, InterruptedException {
        String keyDir = PreFixDir+serverMetaInfo.getKey();
        ByteSequence key = ByteSequence.from(keyDir, StandardCharsets.UTF_8);
        ByteSequence value = ByteSequence.from(JSONUtil.toJsonStr(serverMetaInfo), StandardCharsets.UTF_8);
        kvClient.put(key, value).get();
//        成功注册后就放入key本地缓存
        nodeKeySet.add(keyDir);
    }

    /**
     * 从注册中心获取服务，通过前缀搜索获得所有的服务列表，配合服务选择使用
     * @param serverMetaInfo 服务元信息的部分
     * @return
     * @throws ExecutionException
     * @throws InterruptedException
     */
    public List<ServerMetaInfo> getValue(ServerMetaInfo serverMetaInfo) throws ExecutionException, InterruptedException {
        String preSearchKey = PreFixDir + serverMetaInfo.getServiceName() + "/";
        GetOption getOption = GetOption.builder().isPrefix(true).build();
        ByteSequence preKey = ByteSequence.from(preSearchKey, StandardCharsets.UTF_8);
        List<KeyValue> keyValueList = kvClient.get(preKey, getOption).get().getKvs();
        return keyValueList.stream().map(keyValue -> {
            String key = keyValue.getKey().toString(StandardCharsets.UTF_8);
            watch(key);
            String value = keyValue.getValue().toString(StandardCharsets.UTF_8);
            return JSONUtil.toBean(value, ServerMetaInfo.class);
        }).collect(Collectors.toList());
    }

    /**
     * 注销服务（服务端）
     * @param serverMetaInfo 服务元信息
     */
    public void remove(ServerMetaInfo serverMetaInfo){
        String keyDir = PreFixDir+serverMetaInfo.getKey();
        ByteSequence key = ByteSequence.from(keyDir, StandardCharsets.UTF_8);
        kvClient.delete(key);
//        注销服务后也要从本地缓存中删除
        nodeKeySet.remove(keyDir);
    }

    /**
     * 续约
     */
    public void keepAlive(){
        CronUtil.schedule("*/10 * * * * *", (Task) ()->{
            for (String key : nodeKeySet){
                try {
                    List<KeyValue> values = kvClient.get(ByteSequence.from(key, StandardCharsets.UTF_8)).get().getKvs();
//                    如果已经过期的服务，就只有服务端自己重新注册了
                    if (CollUtil.isEmpty(values))
                        continue;
                    KeyValue value = values.get(0);
                    String s = value.getValue().toString(StandardCharsets.UTF_8);
                    ServerMetaInfo serverMetaInfo = JSONUtil.toBean(s, ServerMetaInfo.class);
//                    重新注册快到期的服务就是续期了
                    register(serverMetaInfo);
                } catch (InterruptedException | ExecutionException e) {
                    throw new RuntimeException(e);
                }
            }
        });

        CronUtil.setMatchSecond(true);
        CronUtil.start();

    }

    /**
     * 监听
     */
    public void watch(String key){
        Watch watchClient = client.getWatchClient();
//        set集合一个对象只能存在一个
        boolean newWatch = watchKeySet.add(key);
        if (newWatch) {
            watchClient.watch(ByteSequence.from(key, StandardCharsets.UTF_8), watchResponse -> {
                for (WatchEvent event: watchResponse.getEvents())
                    switch (event.getEventType()){
                        case DELETE:
                        case PUT:
                        case UNRECOGNIZED:
                        default:
                            break;
                    }
            });
        }
    }
}
