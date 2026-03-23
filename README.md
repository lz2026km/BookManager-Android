# 书籍管理APP - Android 原生项目

## 项目信息
- **项目名**: BookManager
- **包名**: com.bookmanager
- **语言**: Kotlin
- **UI框架**: Jetpack Compose
- **数据库**: Room
- **最低SDK**: Android 8.0 (API 26)

## 功能清单

### ✅ 核心功能
- 书籍信息全面录入
- 图片管理（拍照/相册/压缩）
- Room 本地数据库
- 搜索筛选排序
- Material You 风格
- 深色模式支持

### ✅ 特色功能
- 借阅记录管理
- 阅读打卡
- 书籍去重检测
- 批量操作
- 数据备份恢复

## 项目结构

```
app/
├── src/main/java/com/bookmanager/
│   ├── data/
│   │   ├── entity/          # Room 实体类
│   │   ├── dao/             # 数据访问对象
│   │   ├── database/        # 数据库配置
│   │   └── repository/      # 数据仓库
│   ├── ui/
│   │   ├── theme/           # 主题配置
│   │   ├── screens/         # 各页面
│   │   └── components/      # 通用组件
│   ├── viewmodel/           # ViewModel
│   └── util/                # 工具类
├── src/main/res/
│   └── drawable/            # 图标资源
└── build.gradle.kts         # 构建配置
```

## 部署说明

1. 用 Android Studio 打开项目目录
2. 等待 Gradle 同步完成
3. 连接手机或启动模拟器
4. 点击 Run 安装运行

---
*鲁班系统 🔨*