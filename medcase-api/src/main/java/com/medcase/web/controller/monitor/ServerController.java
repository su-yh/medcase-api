package com.medcase.web.controller.monitor;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import com.medcase.mvc.response.R;
import com.medcase.framework.web.domain.Server;

/**
 * 服务器监控
 * 
 */
@RestController
@RequestMapping("/monitor/server")
public class ServerController {

    @PreAuthorize("@ss.hasPermi('monitor:server:list')")
    @GetMapping()
    public R<Server> getInfo() throws Exception {

        Server server = new Server();
        server.copyTo();
        return R.ofSuccess(server);
    }
}
