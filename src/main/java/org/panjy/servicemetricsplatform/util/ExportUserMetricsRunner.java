package org.panjy.servicemetricsplatform.util;

import com.opencsv.CSVWriter;
import org.panjy.servicemetricsplatform.service.StrategicLayerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.io.FileWriter;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.*;

@Component
public class ExportUserMetricsRunner implements CommandLineRunner {

    @Autowired
    private StrategicLayerService strategicLayerService;

    private static final SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");

    @Override
    public void run(String... args) throws Exception {
//        // 定义导出区间
//        Date start = sdf.parse("2025-08-01");
//        Date end = sdf.parse("2025-08-30");
//
//        // CSV 文件路径
//        String outputPath = "user_metrics_2025-07-15_to_2025-08-15.csv";
//
//        try (CSVWriter writer = new CSVWriter(new FileWriter(outputPath))) {
//            // 写表头
//            String[] header = {"date", "new_users", "active_user_count", "retention_10d", "retention_3d", "retention_7d", "churn_rate", "avg_service_hours"};
//            writer.writeNext(header);
//
//            // 逐日写数据
//            Calendar cal = Calendar.getInstance();
//            cal.setTime(start);
//
//            while (!cal.getTime().after(end)) {
//                Date currentDay = cal.getTime();
//                String dateStr = sdf.format(currentDay);
//
//                // 1. 新增用户数
//                List<String> newUsers = strategicLayerService.findNewUserByDay(currentDay);
//                int newUserCount = (newUsers == null) ? 0 : newUsers.size();
//
//                // 2. 活跃用户数
//                int activeUserCount = strategicLayerService.getActiveUserCount(currentDay);
//
//                // 3. 留存率
//                double retention10d = strategicLayerService.getRetentionRate(currentDay, 10);
//                double retention3d = strategicLayerService.getRetentionRate(currentDay, 3);
//                double retention7d = strategicLayerService.getRetentionRate(currentDay, 7);
//
//                // 4. 流失率
//                double churnRate = strategicLayerService.getChurnRate(currentDay);
//
//                // 5. 平均服务时间（小时）
//                double avgServiceTime = strategicLayerService.getAverageServiceTime(currentDay);
//
//                // 写入一行
//                String[] row = {
//                        dateStr,
//                        String.valueOf(newUserCount),
//                        String.valueOf(activeUserCount),
//                        String.format("%.2f", retention10d),
//                        String.format("%.2f", retention3d),
//                        String.format("%.2f", retention7d),
//                        String.format("%.2f", churnRate),
//                        String.format("%.2f", avgServiceTime)
//                };
//                writer.writeNext(row);
//
//                // 下一天
//                cal.add(Calendar.DAY_OF_MONTH, 1);
//            }
//
//            System.out.println("✅ 数据导出完成，保存到: " + outputPath);
//
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
    }
}
