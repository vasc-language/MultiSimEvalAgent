# enhanced_diagnose.py
import os
import sys
import subprocess
import pdfkit

print("=== 增强 PDF 转换环境诊断 ===")
print(f"Python 版本: {sys.version}")
print(f"Python 路径: {sys.executable}")

# 检查 pdfkit
try:
    print(f"✅ pdfkit 已安装，版本: {pdfkit.__version__}")
except Exception as e:
    print(f"❌ pdfkit 问题: {e}")

# 检查 wkhtmltopdf 多种方式
print("\n=== wkhtmltopdf 检查 ===")

# 方式1：PATH 检查
try:
    result = subprocess.run(['wkhtmltopdf', '--version'],
                            capture_output=True, text=True, timeout=10)
    if result.returncode == 0:
        print(f"✅ wkhtmltopdf 在 PATH 中: {result.stdout.strip()}")
    else:
        print(f"❌ wkhtmltopdf PATH 错误: {result.stderr}")
except FileNotFoundError:
    print("❌ wkhtmltopdf 未在 PATH 中找到")
except Exception as e:
    print(f"❌ wkhtmltopdf PATH 测试异常: {e}")

# 方式2：直接路径检查
wkhtmltopdf_path = r'C:\Program Files\wkhtmltopdf\bin\wkhtmltopdf.exe'
if os.path.exists(wkhtmltopdf_path):
    print(f"✅ wkhtmltopdf 文件存在: {wkhtmltopdf_path}")
    try:
        result = subprocess.run([wkhtmltopdf_path, '--version'],
                                capture_output=True, text=True, timeout=10)
        print(f"✅ wkhtmltopdf 可执行: {result.stdout.strip()}")
    except Exception as e:
        print(f"❌ wkhtmltopdf 执行错误: {e}")
else:
    print(f"❌ wkhtmltopdf 文件不存在: {wkhtmltopdf_path}")

# 方式3：pdfkit 配置测试
try:
    config = pdfkit.configuration(wkhtmltopdf=wkhtmltopdf_path)
    print(f"✅ pdfkit 配置成功")

    # 简单测试
    pdfkit.from_string('Hello World!', 'test.pdf', configuration=config)
    if os.path.exists('test.pdf'):
        print(f"✅ PDF 生成测试成功")
        os.remove('test.pdf')  # 清理测试文件
    else:
        print(f"❌ PDF 生成失败")

except Exception as e:
    print(f"❌ pdfkit 测试失败: {e}")

print("\n=== PATH 环境变量 ===")
path_entries = os.environ.get('PATH', '').split(';')
wkhtmltopdf_paths = [p for p in path_entries if 'wkhtmltopdf' in p.lower()]
if wkhtmltopdf_paths:
    print(f"✅ PATH 中的 wkhtmltopdf 路径: {wkhtmltopdf_paths}")
else:
    print("❌ PATH 中未找到 wkhtmltopdf 路径")