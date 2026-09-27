# Studio Camera / 摄影机工作台

从 Pose Studio 提取的独立客户端摄影机模组。当前版本：**0.1.0**。

| 安装包 | Minecraft | 加载器 | Java | 必需依赖 |
| --- | --- | --- | --- | --- |
| `studiocamera-forge-1.20.1-0.1.0.jar` | Java Edition 1.20.1 | Forge 47.4.10 或更新的 47.x | 17 | 无其他模组 |
| `studiocamera-fabric-1.21.11-0.1.0.jar` | Java Edition 1.21.11 | Fabric Loader 0.19.5；也支持 0.19.3 | 21 | Fabric API，测试版本 0.141.6+1.21.11 |

把与你的游戏版本对应的 **一份 JAR** 放入该实例的 `mods` 文件夹。模组只需安装在客户端，不注册服务器网络协议、实体或物品。

**与 Pose Studio 同装的规则：检测到模组 ID `posestudio` 后，Studio Camera 自动停用。** 独立版不会注册快捷键、界面或摄像机控制 Mixin，由 Pose Studio 提供摄影机功能。启动器的模组列表可能仍显示这个 JAR 的元信息，这不代表独立摄像机已启用。

## 操作

| 操作 | 默认按键 / 方法 |
| --- | --- |
| 开启 / 退出摄影机 | F9 |
| 面板 ↔ 自由飞行 | F10 |
| 隐藏界面 / 拍照模式 | F12 |
| 从拍照或自由飞行返回面板 | Esc 或面板快捷键 |
| 水平移动 | W / A / S / D |
| 上升 / 下降 | Space / 左 Shift |
| 加速 | 左 Ctrl |
| 俯仰 / 水平朝向 | 自由飞行时移动鼠标；面板中央按住左键拖动 |
| 在画面平面中移动摄影机 | 面板中央按住右键拖动 |
| 调整视野 FOV | 滚轮、面板中央中键上下拖动，或 `[ / ]` |
| 调整翻滚角 Roll | Q / E，或右侧数值输入 |
| 精确参数 | 位置 X/Y/Z、Yaw、Pitch、Roll、FOV、移动速度，点击“应用”或回车 |
| 撤回 | “撤回”按钮或 Ctrl+Z；最多 10 次，单次拖动作为一次调整 |
| 标准视角 | 前方 / 后方 / 左侧 / 右侧，以进入时玩家位置为焦点 |
| 更新预设视角的焦点 | “将预设视角焦点设为玩家” |
| 截图 | Minecraft 原有 F2；隐藏界面后也可截图 |

F9 / F10 / F12 可以在 Minecraft 的按键设置中更改，也支持绑定鼠标按钮；界面提示随这三个绑定实时更新。在数值输入框内，Ctrl+Z 保留输入框本身的文本编辑行为；点击视口后可撤回摄影机调整。

FOV 范围为 10–150 度，Pitch 范围为 -90–90 度，速度范围为 0.05–100 方块/秒；Yaw 与 Roll 会规整到 -180–180 度。相机移动与玩家输入分开，相机可以穿过方块。世界和实体按游戏原有规则运行；摄影机仅能显示客户端已加载的世界区域，无法扩大服务器发送的区块范围。

中英文界面随 Minecraft 语言切换。自由摄影机隐藏原版 HUD、手持物第一人称画面和视角晃动；拍照模式也隐藏本模组的操作提示。返回面板不会清除构图，退出、断线、切换世界或死亡会恢复进入前的 HUD 和视角类型。使用现有的世界、资源包和光影渲染。

## 构建与代码

所有工程、缓存、临时文件与测试目录保留在 E 盘。推荐项目根路径为 `E:\StudioCamera`，Windows PowerShell 执行：

```powershell
E:\StudioCamera\tools\bootstrap.ps1
E:\StudioCamera\build.ps1 all build
```

`bootstrap.ps1` 从 Adoptium 下载 Windows x64 JDK 17 / 21，并核对官方 SHA-256。`build.ps1` 设置 E 盘中的 Java、Gradle 用户目录、临时目录及 Java 用户目录；请通过它启动构建。Forge 使用 Gradle 8.8 / ForgeGradle 6.0.54，Fabric 使用 Gradle 9.2.1 / Loom 1.14.10 / Yarn 1.21.11+build.3。若改变项目根目录，请同步修改两个 `gradle.properties` 中的绝对路径。

`common/` 包含摄影机变换、镜头限制和 10 次撤回；`forge/` 与 `fabric/` 分别包含对应版本的输入、渲染、生命周期、界面和 Pose Studio 优先级适配。两个安装包均不依赖 Pose Studio 的类。

两个正式安装包只包含模组代码与资源。发布源码包从公开 Git 提交生成，不包含测试世界、日志、截图或整合包文件。

## English

Studio Camera is a standalone client-side photography camera extracted from Pose Studio. Choose the Forge 1.20.1 build (Java 17) or Fabric 1.21.11 build (Java 21, Fabric API). Fabric Loader 0.19.5 is the target; 0.19.3 is also supported.

F9 toggles the camera; F10 switches between panel and free flight; F12 hides all Studio Camera UI for screenshots. Esc returns to the panel without losing the camera. Fly with WASD, Space/Left Shift and Left Ctrl; mouse movement controls pitch/yaw, Q/E controls roll, wheel or brackets controls FOV. In the panel viewport, left drag looks, right drag pans, and middle drag zooms. Numeric position, yaw, pitch, roll, FOV and speed controls, four player-focused view presets and ten-step undo are included. The three function-key bindings can be remapped, and the hints follow the current bindings.

When a mod with ID `posestudio` is installed, Studio Camera disables its key registrations and camera injections. This coexistence behavior has been checked on Forge with Pose Studio 0.1.9. The Fabric check used a local marker with that mod ID; it does not imply the existence of a released Fabric port of Pose Studio.

The camera does not move the actual player via camera controls. The world keeps running, and the server's loaded chunks remain the visibility boundary. Current shaders, resource packs and world rendering are used. Installation packages contain no test worlds or third-party pack assets. MIT license; original Pose Studio attribution is retained.
