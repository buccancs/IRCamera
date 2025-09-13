#!/usr/bin/env python3
"""Enhanced Chinese comment translation with comprehensive mappings."""

import os
import re
from typing import Dict, List, Tuple


def get_comprehensive_translation_map() -> Dict[str, str]:
    """Get comprehensive translation mappings for Chinese technical terms."""
    return {
        # Menu and UI terms
        "菜单": "menu",
        "测温": "temperature measurement",
        "观测": "observation",
        "拍照": "capture",
        "录像": "recording",
        "点线面": "point/line/area",
        "双光": "dual light",
        "伪彩": "pseudo color",
        "Settings": "settings",
        "标靶": "target",
        "高低温": "high/low temperature",
        "温度档位": "temperature level",
        "高温源": "high temperature source",
        "低温源": "low temperature source",
        "动态识别": "dynamic recognition",
        # Technical terms
        "红外": "infrared",
        "Visible光": "visible light",
        "融合": "fusion",
        "画中画": "picture-in-picture",
        "配准": "registration",
        "增益": "gain",
        "华氏度": "Fahrenheit",
        "摄氏度": "Celsius",
        "自动Switch": "auto switch",
        "常温": "normal temperature",
        # State and status terms
        "选中": "selected",
        "未选中": "unselected",
        "Click": "click",
        "EventListener": "event listener",
        "State": "state",
        "历史遗留": "legacy",
        "Save": "saved",
        "Initialize": "initialize",
        "当前": "current",
        "Restore": "restore",
        # UI and interaction terms
        "按钮": "button",
        "Switch": "switch",
        "立即": "immediate",
        "延迟": "delayed",
        "连续": "continuous",
        "Close": "close",
        "Reset": "reset",
        "Clear": "clear",
        "Refresh": "refresh",
        "Update": "update",
        # Color and display terms
        "白热": "white hot",
        "黑热": "black hot",
        "铁红": "iron red",
        "红热": "red hot",
        "热铁": "hot iron",
        "彩虹": "rainbow",
        "颜色": "color",
        "数组": "array",
        "Configuration": "configuration",
        "预设": "preset",
        # Device and hardware terms
        "机芯": "core",
        "旋转角度": "rotation angle",
        "水印": "watermark",
        "字体": "font",
        "警示": "warning",
        "生效": "effective",
        "高亮": "highlight",
        # Data and file terms
        "代号": "code",
        "Encode": "encoding",
        "数量": "quantity",
        "档位": "level",
        "范围": "range",
        "列表": "list",
        "索引": "index",
        # Animal types (for target detection)
        "人": "human",
        "羊": "sheep",
        "狗": "dog",
        "鸟": "bird",
        # Common phrases
        "由于历史遗留": "Due to legacy constraints",
        "这里先保持旧代码逻辑": "Maintain original code logic here",
        "后面有空再考虑更改": "consider changes later when time permits",
        "丢给上层的 listener 去做": "leave to upper-layer listener to handle",
        "什么都未选中": "nothing selected",
        "所有都未选中": "all unselected",
        "不应该使用这个属性": "should not use this property",
        "若为 null 表示": "if null indicates",
        "若传递的 code 为不支持 code": "if unsupported code is passed",
        "则为全部未选中效果": "results in all unselected state",
        # Specific technical phrases
        "测温Pattern/Mode-菜单": "Temperature measurement mode - Menu",
        "观测Pattern/Mode-菜单": "Observation mode - Menu",
        "ClickEventListener": "click event listener",
        "SwitchEventListener": "switch event listener",
        "选中State": "selection state",
        "未选中State": "unselected state",
        "所用 Adapter": "Adapter used for",
        "指定选项": "specified option",
        "当前选中": "currently selected",
        "菜单类型": "menu type",
        "多选State": "multi-selection state",
        "单选": "single selection",
        "高低温点": "high/low temperature points",
        "测量Pattern/Mode": "measurement mode",
        "图标类型": "icon type",
    }


def translate_file_content(file_path: str) -> bool:
    """Translate Chinese content in a file using comprehensive mappings."""
    if not os.path.exists(file_path):
        return False

    try:
        with open(file_path, "r", encoding="utf-8") as f:
            content = f.read()

        original_content = content
        translation_map = get_comprehensive_translation_map()

        # Apply translations
        for chinese, english in translation_map.items():
            content = content.replace(chinese, english)

        # Additional pattern-based replacements
        patterns = [
            (r"(\d+)-白热", r"\1-White Hot"),
            (r"(\d+)-黑热", r"\1-Black Hot"),
            (r"(\d+)-铁红", r"\1-Iron Red"),
            (r"(\d+)-红热", r"\1-Red Hot"),
            (r"(\d+)-热铁", r"\1-Hot Iron"),
            (r"(\d+)-彩虹(\d+)", r"\1-Rainbow \2"),
            (
                r"true-(\w+)Pattern/Mode false-(\w+)Pattern/Mode",
                r"true-\1 mode false-\2 mode",
            ),
        ]

        for pattern, replacement in patterns:
            content = re.sub(pattern, replacement, content)

        # Write back if changed
        if content != original_content:
            with open(file_path, "w", encoding="utf-8") as f:
                f.write(content)
            return True

    except Exception as e:
        print(f"Error processing {file_path}: {e}")

    return False


def process_all_files():
    """Process all relevant files for Chinese translation."""
    total_files = 0
    updated_files = 0

    for root, dirs, files in os.walk("."):
        # Skip certain directories
        if any(
            skip in root for skip in [".git", "__pycache__", "build", "dist", ".eggs"]
        ):
            continue

        for file in files:
            if file.endswith((".kt", ".java", ".xml")):
                file_path = os.path.join(root, file)
                total_files += 1

                if translate_file_content(file_path):
                    updated_files += 1
                    print(f"Updated: {file_path}")

    print(f"\nProcessed {total_files} files, updated {updated_files}")
    return updated_files > 0


if __name__ == "__main__":
    process_all_files()
