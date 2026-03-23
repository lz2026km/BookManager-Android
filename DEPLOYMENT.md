# 📚 书籍管理APP - 部署指南

## 项目信息

| 项目 | 信息 |
|------|------|
| 项目名 | BookManager |
| 包名 | com.bookmanager |
| 语言 | Kotlin |
| UI框架 | Jetpack Compose |
| 数据库 | Room |
| 最低SDK | Android 8.0 (API 26) |

---

## 一、环境准备

### 1. 安装 Android Studio
- 下载地址：https://developer.android.com/studio
- 版本要求：Hedgehog (2023.1.1) 或更高版本

### 2. 安装 JDK
- 版本：JDK 17
- 配置环境变量 JAVA_HOME

### 3. 安装 Android SDK
- 通过 Android Studio 的 SDK Manager 安装
- SDK Platform: Android 14 (API 34)
- Build Tools: 34.0.0

---

## 二、导入项目

### 方法一：直接打开
1. 打开 Android Studio
2. 选择 `Open` 或 `File > Open`
3. 选择 `BookManager-Android` 文件夹
4. 点击 `OK`

### 方法二：从版本控制导入
1. Android Studio > `Get from VCS`
2. 输入项目 URL
3. 点击 `Clone`

### Gradle 同步
- 项目打开后会自动进行 Gradle 同步
- 如果失败，点击 `File > Sync Project with Gradle Files`

---

## 三、生成 APK

### 方法一：调试版 APK
1. 点击菜单 `Build > Build Bundle(s) / APK(s) > Build APK(s)`
2. 等待构建完成
3. 点击通知中的 `locate` 找到 APK 文件
4. APK 位置：`app/build/outputs/apk/debug/app-debug.apk`

### 方法二：签名版 APK
1. 点击菜单 `Build > Generate Signed Bundle / APK`
2. 选择 `APK`，点击 `Next`
3. 创建或选择签名密钥
4. 选择 `release` 构建变体
5. 点击 `Finish`

### 命令行构建
```bash
# 调试版
./gradlew assembleDebug

# 发布版
./gradlew assembleRelease
```

---

## 四、安装到手机

### 方法一：通过 Android Studio
1. 手机开启 `开发者选项` 和 `USB 调试`
2. 用 USB 连接电脑
3. 点击 Android Studio 的 `Run` 按钮 (绿色三角形)
4. 选择目标设备，点击 `OK`

### 方法二：直接安装 APK
1. 将 APK 文件传输到手机
2. 在手机上打开 APK 文件
3. 允许安装未知来源应用
4. 点击 `安装`

---

## 五、项目结构

```
BookManager-Android/
├── app/
│   ├── src/main/
│   │   ├── java/com/bookmanager/
│   │   │   ├── data/
│   │   │   │   ├── entity/      # 数据实体
│   │   │   │   ├── dao/         # 数据访问
│   │   │   │   ├── database/    # 数据库配置
│   │   │   │   └── repository/  # 数据仓库
│   │   │   ├── ui/
│   │   │   │   ├── theme/       # 主题
│   │   │   │   └── screens/     # 页面
│   │   │   ├── viewmodel/       # 视图模型
│   │   │   ├── MainActivity.kt
│   │   │   └── BookManagerApp.kt
│   │   ├── res/                 # 资源文件
│   │   └── AndroidManifest.xml
│   ├── build.gradle.kts
│   └── proguard-rules.pro
├── build.gradle.kts
├── settings.gradle.kts
└── README.md
```

---

## 六、功能特性

### ✅ 已实现
- 书籍信息全面录入
- 图片封面管理
- Room 本地数据库
- 搜索筛选排序
- Material You 设计
- 深色模式支持
- 状态快捷修改
- 阅读进度追踪
- 个人评分系统
- 书籍分类管理
- 借阅记录框架
- 去重检测框架

### 🔧 待扩展
- 批量导入/导出
- 数据备份恢复
- 阅读打卡完善
- 统计图表展示

---

## 七、常见问题

### Q: Gradle 同步失败？
A: 检查网络连接，配置国内镜像：
```kotlin
// settings.gradle.kts
maven { url = uri("https://maven.aliyun.com/repository/google") }
maven { url = uri("https://maven.aliyun.com/repository/public") }
```

### Q: 找不到设备？
A: 
1. 确保开启 USB 调试
2. 安装手机驱动
3. 尝试切换 USB 连接模式

### Q: 安装被阻止？
A: 手机设置 > 安全 > 允许未知来源

---

## 八、技术栈

| 技术 | 用途 |
|------|------|
| Kotlin | 开发语言 |
| Jetpack Compose | UI 框架 |
| Room | 数据库 |
| ViewModel | 架构组件 |
| Coroutines | 异步处理 |
| Flow | 响应式数据流 |
| Material 3 | 设计系统 |

---

**开发团队**：鲁班系统 🔨  
**版本**：1.0.0  
**更新时间**：2026-03-23