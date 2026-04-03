package com.lww.medical.tools;

import com.lww.kb.KnowledgeBaseService;
import dev.langchain4j.agent.tool.P;
import dev.langchain4j.agent.tool.Tool;
import java.util.*;

/**
 * 医疗专业工具集
 */
public class MedicalTools {

    private final KnowledgeBaseService knowledgeBaseService;

    public MedicalTools(KnowledgeBaseService knowledgeBaseService) {
        this.knowledgeBaseService = knowledgeBaseService;
    }

    @Tool("症状评估：输入症状描述，返回可能疾病和紧急程度评分(1-10)")
    public String assessSymptoms(@P("症状描述") String symptoms) {
        int urgency = calculateUrgency(symptoms);
        String possibleDiseases = knowledgeBaseService.search(symptoms, 3);
        return String.format("紧急程度: %d/10\n可能相关疾病:\n%s", urgency, possibleDiseases);
    }

    @Tool("科室推荐：根据症状推荐就诊科室")
    public String recommendDepartment(@P("症状") String symptoms) {
        if (symptoms.contains("胸痛") || symptoms.contains("心悸")) return "推荐科室: 心内科";
        if (symptoms.contains("头晕") || symptoms.contains("麻木")) return "推荐科室: 神经内科";
        if (symptoms.contains("外伤") || symptoms.contains("骨折")) return "推荐科室: 外科";
        return "推荐科室: 内科";
    }

    private int calculateUrgency(String symptoms) {
        if (symptoms.contains("胸痛") || symptoms.contains("呼吸困难")) return 9;
        if (symptoms.contains("发热") && symptoms.contains("咳嗽")) return 6;
        return 3;
    }
}
