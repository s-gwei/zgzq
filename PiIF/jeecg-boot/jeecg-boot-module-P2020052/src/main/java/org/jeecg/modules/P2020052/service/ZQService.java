package org.jeecg.modules.P2020052.service;

import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;
import java.util.Map;

@Service
public interface ZQService {
    List TaskExecutionTable(List<Map<String, Object>> result);

    void exportTable(List<Map<String, Object>> result, HttpServletResponse response, String fileName) throws IOException;
}
