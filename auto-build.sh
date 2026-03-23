#!/bin/bash
# ============================================================================
# Android APP 一键编译脚本
# 使用本地安装的 Gradle 直接编译
# ============================================================================

set -e

PROJECT_DIR="/home/admin/.openclaw/workspace/luban-projects/BookManager-Android"
ANDROID_HOME="$HOME/Android/Sdk"

echo "🔨 书籍管理APP - 一键编译"
echo "=========================================="
echo ""

# 设置环境变量
export ANDROID_HOME
export ANDROID_SDK_ROOT=$ANDROID_HOME
export PATH="$PATH:$ANDROID_HOME/cmdline-tools/latest/bin:$ANDROID_HOME/platform-tools:$ANDROID_HOME/build-tools/34.0.0"

cd "$PROJECT_DIR"

echo "🧹 清理项目..."
gradle clean --no-daemon 2>&1 | tail -5

echo ""
echo "🔨 编译 Debug APK..."
gradle assembleDebug --no-daemon 2>&1 | tail -20

echo ""
APK_FILE="$PROJECT_DIR/app/build/outputs/apk/debug/app-debug.apk"

if [ -f "$APK_FILE" ]; then
    APK_SIZE=$(du -h "$APK_FILE" | cut -f1)
    echo "=========================================="
    echo "✅ 编译成功！"
    echo ""
    echo "📱 APK 信息:"
    echo "   路径: $APK_FILE"
    echo "   大小: $APK_SIZE"
    echo ""
    echo "🚀 安装方法:"
    echo "   方法1: adb install $APK_FILE"
    echo "   方法2: 将 APK 传输到手机安装"
    echo "=========================================="
else
    echo "=========================================="
    echo "❌ APK 文件未生成"
    echo "请检查编译日志"
    echo "=========================================="
fi