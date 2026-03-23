#!/bin/bash
# ============================================================================
# Android APP 自动化编译脚本
# 功能：自动编译生成 APK
# ============================================================================

set -e

PROJECT_DIR="/home/admin/.openclaw/workspace/luban-projects/BookManager-Android"
ANDROID_HOME="$HOME/Android/Sdk"
OUTPUT_DIR="$PROJECT_DIR/app/build/outputs/apk/debug"

echo "🔨 Android APP 自动化编译"
echo "=========================================="
echo "项目目录: $PROJECT_DIR"
echo "Android SDK: $ANDROID_HOME"
echo ""

# 设置环境变量
export ANDROID_HOME
export PATH="$PATH:$ANDROID_HOME/cmdline-tools/latest/bin:$ANDROID_HOME/platform-tools:$ANDROID_HOME/build-tools/34.0.0"

cd "$PROJECT_DIR"

# 检查 Gradle Wrapper
if [ ! -f "gradlew" ]; then
    echo "📦 生成 Gradle Wrapper..."
    gradle wrapper --gradle-version 8.4
fi

# 赋予执行权限
chmod +x gradlew

echo "🧹 清理项目..."
./gradlew clean

echo ""
echo "🔨 编译 Debug APK..."
./gradlew assembleDebug

echo ""
echo "=========================================="
if [ -f "$OUTPUT_DIR/app-debug.apk" ]; then
    APK_SIZE=$(du -h "$OUTPUT_DIR/app-debug.apk" | cut -f1)
    echo "✅ 编译成功！"
    echo ""
    echo "📱 APK 文件:"
    echo "   路径: $OUTPUT_DIR/app-debug.apk"
    echo "   大小: $APK_SIZE"
    echo ""
    echo "🚀 安装方法:"
    echo "   1. 将 APK 传输到手机"
    echo "   2. 在手机上打开安装"
else
    echo "❌ 编译失败，请检查错误日志"
fi

echo "=========================================="