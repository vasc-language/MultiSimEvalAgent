#!/usr/bin/env python3
# -*- coding: utf-8 -*-
"""
关键词提取工具模块

该模块使用spaCy自然语言处理库从JD（职位描述）文本中提取关键词。
主要功能包括：
1. 清理和预处理文本内容
2. 使用中文NLP模型进行词性分析
3. 提取名词、形容词和动词作为关键词
4. 过滤停用词和无意义词汇

适用场景：
- 简历匹配系统中的JD关键词提取
- 文本内容的主题词分析
- 招聘信息的核心技能识别

依赖：
- spacy: 自然语言处理库
- zh_core_web_sm: spaCy中文语言模型

作者: MultiSimEvalAgent项目组
版本: 1.0
"""

import os
import sys
import io
import spacy
import re

# 确保 Python 输出是 UTF-8，防止 Java 读取乱码
sys.stdout = io.TextIOWrapper(sys.stdout.buffer, encoding='utf-8')

# 设置环境变量，防止 sys.argv 乱码（仅在 Windows 适用）
os.environ["PYTHONIOENCODING"] = "utf-8"

def clean_text(text):
    """
    清理和预处理文本内容
    
    该函数用于清理输入的文本，移除HTML标签和特殊字符，
    只保留中文字符、数字和英文字母，以便后续的NLP处理。
    
    Args:
        text (str): 需要清理的原始文本
        
    Returns:
        str: 清理后的文本，去除了HTML标签和特殊字符
        
    Example:
        >>> clean_text("<p>Python开发工程师！！！</p>")
        "Python开发工程师"
    """
    text = re.sub(r"<.*?>", "", text)  # 移除HTML标签
    text = re.sub(r"[^\w\s\u4e00-\u9fff]", "", text)  # 仅保留中文、数字、字母
    return text.strip()

def extract_keywords(jd_text):
    """
    从JD文本中提取关键词
    
    使用spaCy中文NLP模型对职位描述文本进行分析，
    提取其中的名词、形容词和动词作为关键词，
    同时过滤常见的停用词。
    
    Args:
        jd_text (str): 职位描述文本内容
        
    Returns:
        list: 提取出的关键词列表
        
    Raises:
        OSError: 当spaCy中文模型未安装时抛出异常
        SystemExit: 当模型加载失败时退出程序
        
    Example:
        >>> extract_keywords("负责Python后端开发，熟悉Django框架")
        ["负责", "Python", "后端", "开发", "熟悉", "Django", "框架"]
    """
    # 清理文本
    jd_text = clean_text(jd_text)

    # 加载 spaCy 中文模型
    try:
        nlp = spacy.load("zh_core_web_sm")
    except OSError:
        print("错误: 未找到 spaCy 中文模型，请先运行 'python -m spacy download zh_core_web_sm'")
        sys.exit(1)

    # 定义停用词
    custom_stop_words = {"的", "是", "在", "了", "和", "我们", "你们", "他们", "这个"}
    for word in custom_stop_words:
        nlp.vocab[word].is_stop = True

    # 解析文本
    doc = nlp(jd_text)

    # 提取关键词（名词、形容词、动词）
    keywords = [token.text for token in doc if token.text.strip() and not token.is_stop and token.pos_ in {"NOUN", "ADJ", "VERB"}]

    return keywords

if __name__ == "__main__":
    """
    主函数入口
    
    从命令行参数获取JD文本，提取关键词并输出。
    输出格式为逗号分隔的关键词字符串，便于Java程序调用。
    
    命令行用法:
        python extract_keywords.py "职位描述文本"
    
    输出:
        逗号分隔的关键词字符串
    """
    # 获取输入参数
    if len(sys.argv) < 2:
        print("请提供JD文本作为参数")
        sys.exit(1)

    jd_text = sys.argv[1]

    # 确保命令行参数是UTF-8
    if isinstance(jd_text, bytes):
        jd_text = jd_text.decode("utf-8", errors="ignore")

    # 提取关键词
    keywords = extract_keywords(jd_text)

    # 输出关键词（逗号分隔）
    print(",".join(keywords))
