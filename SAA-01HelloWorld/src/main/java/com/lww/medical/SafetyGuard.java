package com.lww.medical;

import org.springframework.stereotype.Component;
import java.util.Arrays;
import java.util.List;

/**
 * 医疗安全机制
 */
@Component
public class SafetyGuard {

    private static final List<String> EMERGENCY_KEYWORDS = Arrays.asList(
        "胸痛", "呼吸困难", "大出血", "昏迷", "休克", "窒息",
        "严重外伤", "中毒", "心脏骤停", "抽搐不止"
    );

    private static final String DISCLAIMER =
        "⚠️ 重要提示：本系统仅供参考，不能替代专业医生诊断。如遇紧急情况，请立即拨打120或前往医院急诊。";

    private static final String EMERGENCY_ALERT =
        "🚨 紧急提示：检测到可能的紧急情况，请立即：\n" +
        "1. 拨打 120 急救电话\n" +
        "2. 前往最近医院急诊\n" +
        "3. 联系您的主治医生";

    /**
     * 检测紧急情况
     */
    public boolean detectEmergency(String message) {
        if (message == null) return false;
        return EMERGENCY_KEYWORDS.stream().anyMatch(message::contains);
    }

    /**
     * 内容过滤 - 避免确诊性表述
     */
    public String filterResponse(String response) {
        if (response == null) return response;

        // 替换确诊性表述
        response = response.replaceAll("您患有", "可能存在");
        response = response.replaceAll("确诊为", "疑似");
        response = response.replaceAll("必须服用", "建议咨询医生后服用");

        return response;
    }

    public String getDisclaimer() {
        return DISCLAIMER;
    }

    public String getEmergencyAlert() {
        return EMERGENCY_ALERT;
    }
}
