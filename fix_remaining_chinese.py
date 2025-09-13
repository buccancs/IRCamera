#!/usr/bin/env python3
"""
Comprehensive Chinese comment translation for remaining source files.
Excludes migration backup directories and generated files.
"""

import glob
import os
import re
from pathlib import Path

# Enhanced thermal imaging translation dictionary
THERMAL_TRANSLATIONS = {
    # UI and Menu Terms
    "菜单": "menu",
    "Settings": "settings",
    "Select": "selection",
    "选中": "selected",
    "Switch": "switch",
    "Click": "click",
    "按钮": "button",
    "State": "state",
    "Pattern/Mode": "mode",
    "类型": "type",
    "Configuration": "configuration",
    "参数": "parameter",
    "数据": "data",
    "文件": "file",
    "路径": "path",
    "Save": "save",
    "Load": "load",
    "Delete": "delete",
    "Add": "add",
    "修改": "modify",
    "Update": "update",
    "Refresh": "refresh",
    "Reset": "reset",
    "清空": "clear",
    "复制": "copy",
    "粘贴": "paste",
    "导入": "import",
    "导出": "export",
    # Thermal Imaging Terms
    "红外": "infrared",
    "热成像": "thermal imaging",
    "温度": "temperature",
    "测温": "temperature measurement",
    "伪彩": "pseudo color",
    "调色板": "palette",
    "色温": "color temperature",
    "增益": "gain",
    "对比度": "contrast",
    "亮度": "brightness",
    "饱和度": "saturation",
    "校准": "calibration",
    "标定": "calibration",
    "配准": "registration",
    "融合": "fusion",
    "Visible光": "visible light",
    "双光": "dual light",
    "白热": "white hot",
    "黑热": "black hot",
    "铁红": "iron red",
    "彩虹": "rainbow",
    "灰度": "grayscale",
    "范围": "range",
    "最高": "maximum",
    "最低": "minimum",
    "平均": "average",
    "中心": "center",
    "点": "point",
    "线": "line",
    "面": "area",
    "区域": "region",
    "框选": "selection box",
    "测量": "measurement",
    "Analysis": "analysis",
    "Report": "report",
    "图像": "image",
    "图片": "image",
    "照片": "photo",
    "视频": "video",
    "录制": "recording",
    "播放": "playback",
    "Pause": "pause",
    "Stop": "stop",
    "Start": "start",
    "End": "end",
    "完成": "complete",
    "成功": "success",
    "失败": "failed",
    "Error": "error",
    "Exception": "exception",
    "Warning": "warning",
    "提示": "tip",
    "Information": "info",
    "消息": "message",
    "通知": "notification",
    # Hardware Terms
    "设备": "device",
    "相机": "camera",
    "镜头": "lens",
    "传感器": "sensor",
    "机芯": "core",
    "模组": "module",
    "组件": "component",
    "硬件": "hardware",
    "软件": "software",
    "固件": "firmware",
    "Version": "version",
    "型号": "model",
    "序列号": "serial number",
    "Connect": "connection",
    "Disconnect": "disconnect",
    "通信": "communication",
    "Protocol": "protocol",
    "Interface": "interface",
    "端口": "port",
    "Network": "network",
    "蓝牙": "bluetooth",
    "WiFi": "WiFi",
    "USB": "USB",
    # Technical Terms
    "Algorithm": "algorithm",
    "Process": "processing",
    "计算": "calculation",
    "Convert/Transform": "conversion",
    "Parse": "parsing",
    "Encode": "encoding",
    "Decode": "decoding",
    "Compress": "compression",
    "Decompress": "decompression",
    "格式": "format",
    "Protocol": "protocol",
    "标准": "standard",
    "规范": "specification",
    "文档": "document",
    "说明": "description",
    "注释": "comment",
    "备注": "remark",
    "标记": "marker",
    "标签": "tag",
    "标题": "title",
    "名称": "name",
    "属性": "property",
    "方法": "method",
    "函数": "function",
    "变量": "variable",
    "常量": "constant",
    "枚举": "enum",
    "类": "class",
    "Interface": "interface",
    "继承": "inheritance",
    "实现": "implementation",
    "重写": "override",
    "重载": "overload",
    "构造": "constructor",
    "析构": "destructor",
    "Initialize": "initialization",
    "Destroy": "destroy",
    "Create": "create",
    "释放": "release",
    "分配": "allocate",
    "回收": "recycle",
    "Cache": "cache",
    "缓冲": "buffer",
    "Queue": "queue",
    "堆栈": "stack",
    "Thread": "thread",
    "Process": "process",
    "Task": "task",
    "作业": "job",
    "Service": "service",
    "Client": "client",
    "Service器": "server",
    "Database": "database",
    "存储": "storage",
    "内存": "memory",
    "磁盘": "disk",
    "文字": "text",
    "水印": "watermark",
    # Complex phrases patterns
    "暂时通过此全局变量，区分不同的模组:指令调用，业务processing": "Temporarily use this global variable to distinguish different modules: command invocation,
        business processing",
    "水印文字": "watermark text",
    "文字内容": "text content",
    "Show/Display文字": "display text",
    "文字大小": "text size",
    "文字颜色": "text color",
    "文字样式": "text style",
    "文字位置": "text position",
    "文字对齐": "text alignment",
}


def translate_chinese_text(text):
    """Translate Chinese text to English using thermal imaging dictionary."""
    # First try exact phrase matches
    for chinese, english in THERMAL_TRANSLATIONS.items():
        if chinese in text:
            text = text.replace(chinese, english)

    return text


def process_file(filepath):
    """Process a single file to translate Chinese comments."""
    try:
        with open(filepath, "r", encoding="utf-8", errors="ignore") as f:
            content = f.read()

        # Check if file contains Chinese characters
        if not re.search(r"[\u4e00-\u9fff]", content):
            return False

        original_content = content
        lines = content.split("\n")
        modified = False

        for i, line in enumerate(lines):
            if re.search(r"[\u4e00-\u9fff]", line):
                # Translate the line
                translated_line = translate_chinese_text(line)
                if translated_line != line:
                    lines[i] = translated_line
                    modified = True
                    print(f"  Line {i+1}: {line.strip()}")
                    print(f"    -> {translated_line.strip()}")

        if modified:
            new_content = "\n".join(lines)
            with open(filepath, "w", encoding="utf-8") as f:
                f.write(new_content)
            return True

    except Exception as e:
        print(f"Error processing {filepath}: {e}")
        return False

    return False


def main():
    """Main function to process all source files."""
    print("=== Comprehensive Chinese Comment Translation ===")

    # Define patterns for source files
    source_patterns = ["**/*.kt", "**/*.java", "**/*.xml"]

    # Exclude patterns
    exclude_patterns = [
        "**/migration_backup_*/**",
        "**/build/**",
        "**/.*/**",
        "**/.git/**",
    ]

    total_files = 0
    modified_files = 0

    for pattern in source_patterns:
        files = glob.glob(pattern, recursive=True)

        for filepath in files:
            # Skip excluded paths
            skip_file = False
            for exclude in exclude_patterns:
                if any(
                    part.startswith(exclude.replace("**/", "").replace("/**", ""))
                    for part in Path(filepath).parts
                ):
                    skip_file = True
                    break

            if skip_file:
                continue

            total_files += 1

            if process_file(filepath):
                modified_files += 1
                print(f"✓ Translated: {filepath}")

    print("\n=== Translation Complete ===")
    print(f"Files processed: {total_files}")
    print(f"Files modified: {modified_files}")


if __name__ == "__main__":
    main()
