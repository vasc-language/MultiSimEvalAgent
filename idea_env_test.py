#!/usr/bin/env python3
"""
IntelliJ IDEA Python环境验证脚本
验证IDE是否正确配置了Python解释器和包路径
"""
import sys
import os

def main():
    print("=== IntelliJ IDEA Python环境验证 ===")
    print(f"🐍 Python解释器: {sys.executable}")
    print(f"📦 Python版本: {sys.version.split()[0]}")
    print(f"💻 平台: {sys.platform}")
    print()
    
    # 验证关键包导入
    packages = ['pdfkit', 'fitz', 'spacy']
    success_count = 0
    
    for package in packages:
        try:
            if package == 'fitz':
                import fitz
                print(f"✅ {package} (PyMuPDF): 导入成功")
                print(f"   位置: {fitz.__file__}")
            elif package == 'pdfkit':
                import pdfkit
                print(f"✅ {package}: 导入成功")
                print(f"   位置: {pdfkit.__file__}")
            elif package == 'spacy':
                import spacy
                print(f"✅ {package}: 导入成功")
                print(f"   位置: {spacy.__file__}")
            
            success_count += 1
        except ImportError as e:
            print(f"❌ {package}: 导入失败 - {e}")
        print()
    
    # 验证结果
    if success_count == len(packages):
        print("🎉 所有包都能正常导入！IDEA配置正确！")
        print("💡 如果IDEA仍显示红线，请重启IDE并清除缓存")
    else:
        print("⚠️  部分包导入失败，请检查IDEA的Python SDK配置")
        print("📋 配置路径: File → Project Structure → SDKs")
    
    print("\n📍 当前Python搜索路径:")
    for i, path in enumerate(sys.path[:5], 1):  # 只显示前5个路径
        if path:
            print(f"   {i}. {path}")

if __name__ == "__main__":
    main() 