#!/usr/bin/env python3
# -*- coding: utf-8 -*-
"""
HTML转PDF工具模块

该模块使用pdfkit库将HTML内容转换为PDF文件。
pdfkit是wkhtmltopdf的Python封装，能够高质量地将HTML/CSS渲染为PDF。

主要功能包括：
1. 将HTML字符串内容转换为PDF文件
2. 支持CSS样式和复杂布局
3. 配置wkhtmltopdf的执行路径
4. 提供错误处理和状态反馈

适用场景：
- 简历或报告的PDF生成
- 网页内容的PDF导出
- 动态HTML内容的文档化
- 批量HTML到PDF的转换

依赖：
- pdfkit: Python PDF生成库
- wkhtmltopdf: 底层PDF转换引擎（需要独立安装）

系统要求：
- Windows: 需要安装wkhtmltopdf到指定路径
- Linux/Mac: 可通过包管理器安装wkhtmltopdf

作者: MultiSimEvalAgent项目组
版本: 1.0
"""

import pdfkit
import sys

def html_to_pdf(html_content, pdf_file):
    """
    将HTML内容转换为PDF文件
    
    该函数接收HTML字符串内容，使用wkhtmltopdf引擎将其
    转换为高质量的PDF文件。支持CSS样式和复杂的HTML布局。
    
    Args:
        html_content (str): 需要转换的HTML内容字符串
        pdf_file (str): 输出PDF文件的完整路径
        
    Returns:
        None: 成功时无返回值，失败时打印错误信息
        
    Raises:
        Exception: 当wkhtmltopdf未安装或转换失败时抛出异常
        
    Example:
        >>> html = "<html><body><h1>测试文档</h1></body></html>"
        >>> html_to_pdf(html, "/path/to/output.pdf")
        PDF successfully created at /path/to/output.pdf
        
    Note:
        - 需要预先安装wkhtmltopdf程序
        - Windows路径已预配置，其他系统可能需要调整路径
        - 支持中文字符和复杂CSS样式
        - 生成的PDF保持HTML的原有格式和样式
    """
    try:
        # 配置 wkhtmltopdf 的路径
        config = pdfkit.configuration(wkhtmltopdf='C:/Program Files/wkhtmltopdf/bin/wkhtmltopdf.exe')  # Windows 路径

        # 将 HTML 字符串转换为 PDF
        pdfkit.from_string(html_content, pdf_file, configuration=config)
        print(f"PDF successfully created at {pdf_file}")
    except Exception as e:
        print(f"An error occurred: {e}")

if __name__ == "__main__":
    """
    主函数入口
    
    从命令行参数获取HTML内容和输出PDF文件路径，
    执行HTML到PDF的转换操作。
    
    命令行用法:
        python html2pdf.py "HTML内容" "/path/to/output.pdf"
    
    参数说明:
        第一个参数: HTML内容字符串（可以是完整的HTML文档）
        第二个参数: 输出PDF文件的完整路径
    
    输出:
        成功时打印PDF文件创建信息
        失败时打印错误详情
        
    示例:
        python html2pdf.py "<h1>Hello World</h1>" "output.pdf"
    """
    # 从命令行参数获取 HTML 内容和 PDF 文件路径
    html_content = sys.argv[1]  # HTML 字符串
    pdf_file = sys.argv[2]      # 输出的 PDF 文件路径
    html_to_pdf(html_content, pdf_file)