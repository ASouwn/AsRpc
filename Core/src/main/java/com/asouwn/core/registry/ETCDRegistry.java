package com.asouwn.core.registry;

import cn.hutool.json.JSONUtil;
import com.asouwn.core.config.RegistryConfig;
import com.asouwn.core.model.ServerMetaInfo;
import io.etcd.jetcd.*;
import lombok.extern.slf4j.Slf4j;

import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.stream.Collectors;

@Slf4j
public class ETCDRegistry {
    private Client client;
    private KV kvClient;
    private String PreFixDir = "/asrpc/";

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
    }
    public void register(ServerMetaInfo serverMetaInfo) throws ExecutionException, InterruptedException {
        String dir = PreFixDir+serverMetaInfo.getKey();
        ByteSequence key = ByteSequence.from(dir, StandardCharsets.UTF_8);
        ByteSequence value = ByteSequence.from(JSONUtil.toJsonStr(serverMetaInfo), StandardCharsets.UTF_8);
        kvClient.put(key, value).get();
    }
    public List<ServerMetaInfo> getValue(ServerMetaInfo serverMetaInfo) throws ExecutionException, InterruptedException {
        String preSearchKey = PreFixDir + serverMetaInfo.getPreKey();
        ByteSequence preKey = ByteSequence.from(preSearchKey, StandardCharsets.UTF_8);
        List<KeyValue> keyValueList = kvClient.get(preKey).get().getKvs();
        return keyValueList.stream().map(keyValue -> {
            String value = keyValue.getValue().toString(StandardCharsets.UTF_8);
            return JSONUtil.toBean(value, ServerMetaInfo.class);
        }).collect(Collectors.toList());
    }
    public void remove(ServerMetaInfo serverMetaInfo){
        ByteSequence key = ByteSequence.from(PreFixDir+serverMetaInfo.getPreKey(), StandardCharsets.UTF_8);
        kvClient.delete(key);
    }
}
