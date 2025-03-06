# Unity MiniHost Android Demo

欢迎访问我们的项目！本项目是 [Unity 小游戏宿主 SDK](https://minihost.tuanjie.cn/) 的 Android 示例项目，项目参照小游戏宿主 SDK [Android 快速集成文档](https://minihost.tuanjie.cn/help/docs/sdk/android/quick_integrate)提供了简单的游戏启动及平台能力接入示例。

## Preview

| ![First GIF](preview/open_game.gif) | ![Second GIF](preview/multi_process.gif) |
|-------------------------------------|------------------------------------------|

## Clone Project
   本项目使用了 Git LFS 来管理大文件。在克隆项目之前，请确保你的开发环境已正确配置 Git LFS。
1. **安装 Git LFS**

   安装步骤可参考 [Install Git LFS](https://docs.github.com/en/repositories/working-with-files/managing-large-files/installing-git-large-file-storage)

2. **克隆项目**

   安装完成后，在命令行中运行以下命令以初始化 Git LFS，拉取项目以并确保所有 LFS 文件已正确下载：

   ```bash
   git lfs install
   git clone git@github.com:UnityMiniHost/AndroidDemo.git
   git lfs pull
   
## Branches Overview

   

- **main**：此分支使用动态 aar，对包体大小影响较小，能在需要时从 CDN 动态加载所需的 so 库。同时每个游戏运行在单独进程，最大运行游戏进程数可配置。

- **static-aar**：此分支使用静态 aar，游戏运行在主进程，不推荐此类实现，仅提供使用示例。

## Issue
   如果您在测试和使用 Demo 项目中遇到任何您认为不符合预期的行为，或发现代码中任何不合理的实现，请提交 Issue 反馈。🥹
