package org.example.friend.util;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * 算法工具类，提供标签相似度匹配等算法实现
 */
public class AlgorithmUtil {

    /**
     * 私有构造函数，防止实例化工具类
     */
    private AlgorithmUtil() {
    }

    /**
     * 计算两个标签列表的Jaccard相似度
     * Jaccard相似度 = 两个集合的交集大小 / 两个集合的并集大小
     *
     * @param tagList1 第一个标签列表
     * @param tagList2 第二个标签列表
     * @return 相似度值，范围在0到1之间
     */
    public static double calculateTagsSimilarity(List<String> tagList1, List<String> tagList2) {
        // 处理空值情况
        if (tagList1 == null || tagList2 == null) {
            return 0.0;
        }

        // 转换为Set去除重复标签，并便于集合操作
        Set<String> tagSet1 = new HashSet<>(tagList1);
        Set<String> tagSet2 = new HashSet<>(tagList2);

        // 计算交集大小
        Set<String> intersection = new HashSet<>(tagSet1);
        intersection.retainAll(tagSet2);
        int intersectionSize = intersection.size();

        // 计算并集大小
        Set<String> union = new HashSet<>(tagSet1);
        union.addAll(tagSet2);
        int unionSize = union.size();

        // 避免除零异常
        if (unionSize == 0) {
            return 0.0;
        }

        // 返回相似度（保留四位小数）
        return Math.round(((double) intersectionSize / unionSize) * 10000) / 10000.0;
    }
}
