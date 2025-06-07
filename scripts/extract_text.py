#!/usr/bin/env python3
# -*- coding: utf-8 -*-
"""
PDF文本提取工具模块

该模块使用PyMuPDF（fitz）库从PDF文件中提取文本内容。
主要功能包括：
1. 打开和读取PDF文件
2. 逐页提取文本内容
3. 合并所有页面的文本
4. 输出UTF-8编码的文本内容

适用场景：
- 简历内容提取和分析
- PDF文档的文本内容获取
- 文档内容的数字化处理
- 批量PDF文件的文本提取

依赖：
- pymupdf (fitz): PDF处理库

作者: MultiSimEvalAgent项目组
版本: 1.0
"""

import sys
# import fitz  # PyMuPDF
import pymupdf as fitz

def extract_text_from_pdf(pdf_path):
    """
    从PDF文件中提取所有文本内容
    
    该函数打开指定的PDF文件，逐页提取文本内容，
    并将所有页面的文本合并为一个字符串返回。
    
    Args:
        pdf_path (str): PDF文件的完整路径
        
    Returns:
        str: 提取出的所有文本内容，按页面顺序合并
        
    Raises:
        Exception: 当PDF文件无法打开或读取时抛出异常
        
    Example:
        >>> text = extract_text_from_pdf("/path/to/resume.pdf")
        >>> print(text[:100])  # 打印前100个字符
        
    Note:
        - 支持大多数标准PDF格式
        - 对于图片形式的PDF（扫描件）无法提取文本
        - 提取的文本保持原有的换行和格式结构
    """
    # 打开PDF文件
    doc = fitz.open(pdf_path)
    text = ""
    # 逐页提取文本
    for page in doc:
        text += page.get_text()
    return text

if __name__ == "__main__":
    """
    主函数入口
    
    从命令行参数获取PDF文件路径，提取其中的文本内容并输出。
    输出内容使用UTF-8编码，确保中文字符正确显示。
    
    命令行用法:
        python extract_text.py /path/to/file.pdf
    
    输出:
        PDF文件中的所有文本内容
        
    错误处理:
        如果文件读取失败，将错误信息输出到stderr并退出
    """
    # 从命令行参数获取PDF文件路径
    pdf_path = sys.argv[1]
    # 提取文本并打印（使用utf-8编码）
    try:
        text = extract_text_from_pdf(pdf_path)
        print(text.encode("utf-8").decode("utf-8"))
    except Exception as e:
        print(f"Error: {e}", file=sys.stderr)
        sys.exit(1)