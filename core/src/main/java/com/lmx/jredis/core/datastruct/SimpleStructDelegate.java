package com.lmx.jredis.core.datastruct;

import com.lmx.jredis.storage.DataTypeEnum;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Created by limingxin on 2017/12/7.
 */
@Component
@Slf4j
public class SimpleStructDelegate {
    @Value("${memorySize:1024}")
    int storeSize;
    //db size
    @Value("${dbSize:2}")
    int sharedSize = 10;

    public Map<Integer, Map<String, BaseOP>> db = new ConcurrentHashMap<>();

    public Map<String, BaseOP> select(int dbInx) {
        return db.get(dbInx);
    }

    @PostConstruct
    public void init() {
        for (int i = 0; i < sharedSize; i++) {
            SimpleKV simpleKV = new SimpleKV(storeSize);
            simpleKV.init(i);
            SimpleList simpleList = new SimpleList(storeSize);
            simpleList.init(i);
            SimpleHash simpleHash = new SimpleHash(storeSize);
            simpleHash.init(i);
            if (db.get(i) == null) {
                Map<String, BaseOP> ops = new HashMap<>();
                ops.put(DataTypeEnum.KV.getDesc(), simpleKV);
                ops.put(DataTypeEnum.LIST.getDesc(), simpleList);
                ops.put(DataTypeEnum.HASH.getDesc(), simpleHash);
                db.put(i, ops);
            } else {
                db.get(i).put(DataTypeEnum.KV.getDesc(), simpleKV);
                db.get(i).put(DataTypeEnum.LIST.getDesc(), simpleList);
                db.get(i).put(DataTypeEnum.HASH.getDesc(), simpleHash);
            }
        }
    }
}
