package no.hvl.dat250.Assignment1.Controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import redis.clients.jedis.UnifiedJedis;

import java.util.HashMap;
import java.util.Map;

@RestController
public class DebugController {

    @Autowired
    UnifiedJedis jedis;

    @GetMapping("/debug/redis")
    public Map<String, Object> debugRedis() {
        Map<String, Object> snapshot = new HashMap<>();
        snapshot.put("loggedInUsers", jedis.smembers("loggedInUsers"));
        snapshot.put("allKeys", jedis.keys("*"));
        return snapshot;
    }


}
