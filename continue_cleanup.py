#!/usr/bin/env python3
"""
Comprehensive continuation cleanup and standardization script.
Translates remaining Chinese comments and applies formatting across the entire codebase.
"""

import os
import re
import subprocess
import sys
from typing import List, Tuple


def find_chinese_characters(file_path: str) -> List[Tuple[int, str]]:
    """Find lines containing Chinese characters."""
    chinese_lines = []
    try:
        with open(file_path, "r", encoding="utf-8") as f:
            for line_num, line in enumerate(f, 1):
                if re.search(r"[\u4e00-\u9fff]", line):
                    chinese_lines.append((line_num, line.strip()))
    except Exception as e:
        print(f"Error reading {file_path}: {e}")
    return chinese_lines


def translate_chinese_comments(file_path: str) -> bool:
    """Translate Chinese comments to English in Kotlin/Java files."""
    if not file_path.endswith((".kt", ".java")):
        return False

    translations = {
        "观测Pattern/Mode-菜单2-高低温源 ClickEventListener": "Observation mode - Menu 2 - High/Low temperature source click event listener",
        "由于历史遗留（已Save在 SharedPreferences 中），这里 code 取值为": "Due to legacy constraints (saved in SharedPreferences),
            the code values are:",
        "什么都未选中：-1": "Nothing selected: -1",
        "动态识别：0": "Dynamic recognition: 0",
        "高温源：1": "High temperature source: 1",
        "低温源：2": "Low temperature source: 2",
        "观测Pattern/Mode-菜单4-标靶 ClickEventListener": "Observation mode - Menu 4 - Target click event listener",
        "观测Pattern/Mode-菜单5-高低温点 ClickEventListener": "Observation mode - Menu 5 - High/Low temperature points click event listener",
        "测温Pattern/Mode-菜单2-点线面 所用 Adapter": "Adapter used for Temperature measurement mode - Menu 2 - Point/Line/Area",
        "测温Pattern/Mode-菜单3-双光 所用 Adapter": "Adapter used for Temperature measurement mode - Menu 3 - Dual Light",
        "测温Pattern/Mode-菜单4-伪彩 or 观测Pattern/Mode-菜单3-伪彩 所用 Adapter": "Adapter used for Temperature measurement mode - Menu 4 - Pseudo Color or Observation mode - Menu 3 - Pseudo Color",
        "测温Pattern/Mode-菜单5-Settings 所用 Adapter": "Adapter used for Temperature measurement mode - Menu 5 - Settings",
        "测温Pattern/Mode-菜单6-高低温档 所用 Adapter": "Adapter used for Temperature measurement mode - Menu 6 - High/Low temperature level",
        "观测Pattern/Mode-菜单2-高低温源 所用 Adapter": "Adapter used for Observation mode - Menu 2 - High/Low temperature source",
        "观测Pattern/Mode-菜单4-标靶 所用 Adapter": "Adapter used for Observation mode - Menu 4 - Target",
        "观测Pattern/Mode-菜单5-高低温点 所用 Adapter": "Adapter used for Observation mode - Menu 5 - High/Low temperature points",
        "观测Pattern/Mode-菜单6-Settings 所用 Adapter": "Adapter used for Observation mode - Menu 6 - Settings",
        "当前是否处于录像Pattern/Mode": "Whether currently in recording mode",
        "true-录像Pattern/Mode false-拍照Pattern/Mode": "true-recording mode false-capture mode",
        "仅 TS001，测温/观测 Switch时，Close延时拍照、连续拍照、录像后，需要Reset为拍照State": "For TS001 only,
            when switching between temperature measurement/observation modes,
            need to reset to capture state after closing delayed capture,
            continuous capture, or recording",
        "类似Reset，这个方法的目的是ResetState为未拍照、未录像State，且放开 拍照/录像 Switch": "Similar to reset,
            this method aims to reset state to non-capture,
            non-recording state and enable capture/recording switching",
        "在各个热成像 Activity 的 start()，以及当前 View 中调用": "Called in the start() method of various thermal imaging Activities and in the current View",
        "RestoreState": "Restore state",
        "将中间 拍照/录像 按钮Settings为 拍照中-立即/拍照中-延迟/录像中": "Set the middle capture/recording button to capturing-immediate/capturing-delayed/recording state",
        "将中间 拍照/录像 按钮Settings为 拍照中-立即 State": "Set the middle capture/recording button to capturing-immediate state",
        "测温Pattern/Mode-菜单2-点线面 当前选中的菜单类型，若为 null 表示所有都未选中": "Temperature measurement mode - Menu 2 - Point/Line/Area currently selected menu type,
            null indicates all unselected",
        "当前单选的双光类型": "Currently selected dual light type",
        "单光：  不应该使用这个属性": "Single light: Should not use this property",
        "Lite： 不应该使用这个属性": "Lite: Should not use this property",
        "双光：  双光1、双光2、红外、Visible光": "Dual light: Dual light 1, Dual light 2, Infrared,
            Visible light",
        "TC007：双光、红外、Visible光、画中画": "TC007: Dual light, Infrared, Visible light,
            Picture-in-picture",
        "Settings双光多选State": "Set dual light multi-selection state",
        "单光：  画中画、融合度": "Single light: Picture-in-picture, Fusion level",
        "Lite： 画中画、融合度": "Lite: Picture-in-picture, Fusion level",
        "双光：  配准、画中画、融合度": "Dual light: Registration, Picture-in-picture, Fusion level",
        "TC007：配准、、融合度": "TC007: Registration, Fusion level",
        "根据指定的伪彩代号，选中伪彩菜单中的指定伪彩，若传递的 code 为不支持 code，则为全部未选中效果": "Select specified pseudo color in pseudo color menu based on pseudo color code. If unsupported code is passed,
            results in all unselected state",
        "1-白热 3-铁红 4-彩虹1 5-彩虹2 6-彩虹3 7-红热 8-热铁 9-彩虹4 10-彩虹5 11-黑热": "1-White Hot 3-Iron Red 4-Rainbow 1 5-Rainbow 2 6-Rainbow 3 7-Red Hot 8-Hot Iron 9-Rainbow 4 10-Rainbow 5 11-Black Hot",
        "SettingsSettings菜单中指定选项的选中State": "Set the selection state of specified option in settings menu",
        "SettingsSettings菜单中旋转选项的角度": "Set rotation angle for rotation option in settings menu",
        "注意！这个值是机芯的旋转角度，非 UI 旋转角度": "Note! This value is the core rotation angle,
            not UI rotation angle",
        "温度档位是否使用华氏度作为单位": "Whether temperature level uses Fahrenheit as unit",
        "true-华氏度 false-摄氏度": "true-Fahrenheit false-Celsius",
        "Settings 测温Pattern/Mode-菜单6-高低温档 温度档位": "Set Temperature measurement mode - Menu 6 - High/Low temperature level",
        "自动Switch：-1": "Auto switch: -1",
        "高温(低增益)：0": "High temperature (low gain): 0",
        "常温(高增益)：1": "Normal temperature (high gain): 1",
        "Settings 观测Pattern/Mode-菜单2-高低温源 选中": "Set Observation mode - Menu 2 - High/Low temperature source selection",
        "Settings 观测Pattern/Mode-菜单4-标靶 指定选项的选中State": "Set selection state of specified option in Observation mode - Menu 4 - Target",
        "Settings 观测Pattern/Mode-菜单4-标靶-测量Pattern/Mode 图标类型": "Set icon type for Observation mode - Menu 4 - Target - Measurement mode",
        "人：10": "Human: 10",
        "羊：11": "Sheep: 11",
        "狗：12": "Dog: 12",
        "鸟：13": "Bird: 13",
        "Settings 观测Pattern/Mode-菜单5-高低温点 菜单中，高温点 或 低稳点 的选中State": "Set selection state of high temperature point or low temperature point in Observation mode - Menu 5 - High/Low temperature points menu",
        "Clear 观测Pattern/Mode-菜单5-高低温点 菜单的所有选中State": "Clear all selection states in Observation mode - Menu 5 - High/Low temperature points menu",
        "这里维持原有逻辑，后续考虑是否直接给选中Delete得了": "Maintain original logic here,
            consider whether to directly delete selected items later",
        "Initialize 测温Pattern/Mode-菜单2-点线面 菜单": "Initialize Temperature measurement mode - Menu 2 - Point/Line/Area menu",
        "Initialize 测温Pattern/Mode-菜单3-双光 菜单": "Initialize Temperature measurement mode - Menu 3 - Dual Light menu",
        "Initialize 测温Pattern/Mode-菜单4-伪彩 or 观测Pattern/Mode-菜单3-伪彩 菜单": "Initialize Temperature measurement mode - Menu 4 - Pseudo Color or Observation mode - Menu 3 - Pseudo Color menu",
        "Initialize 测温Pattern/Mode-菜单5-Settings 菜单": "Initialize Temperature measurement mode - Menu 5 - Settings menu",
        "Initialize 测温Pattern/Mode-菜单6-高低温档 菜单": "Initialize Temperature measurement mode - Menu 6 - High/Low temperature level menu",
        "Initialize 观测Pattern/Mode-菜单2-高低温源 菜单": "Initialize Observation mode - Menu 2 - High/Low temperature source menu",
        "Initialize 观测Pattern/Mode-菜单4-标靶 菜单": "Initialize Observation mode - Menu 4 - Target menu",
        "Initialize 观测Pattern/Mode-菜单5-高低温点 菜单": "Initialize Observation mode - Menu 5 - High/Low temperature points menu",
        "Initialize 观测Pattern/Mode-菜单6-Settings 菜单": "Initialize Observation mode - Menu 6 - Settings menu",
        "菜单1-拍照录像": "Menu 1 - Capture/Recording",
    }

    try:
        with open(file_path, "r", encoding="utf-8") as f:
            content = f.read()

        original_content = content

        # Apply translations
        for chinese, english in translations.items():
            content = content.replace(chinese, english)

        # Write back if changed
        if content != original_content:
            with open(file_path, "w", encoding="utf-8") as f:
                f.write(content)
            print(f"Updated {file_path}")
            return True
    except Exception as e:
        print(f"Error processing {file_path}: {e}")

    return False


def run_python_linting() -> bool:
    """Run comprehensive Python linting and formatting."""
    print("Running Python linting and formatting...")

    # Find Python files
    python_files = []
    for root, dirs, files in os.walk("."):
        # Skip certain directories
        if any(
            skip in root for skip in [".git", "__pycache__", "build", "dist", ".eggs"]
        ):
            continue
        for file in files:
            if file.endswith(".py"):
                python_files.append(os.path.join(root, file))

    if not python_files:
        print("No Python files found")
        return True

    success = True

    # Run black formatting
    try:
        subprocess.run(["python", "-m", "black"] + python_files, check=True)
        print(f"Applied black formatting to {len(python_files)} Python files")
    except subprocess.CalledProcessError as e:
        print(f"Black formatting failed: {e}")
        success = False

    # Run isort
    try:
        subprocess.run(["python", "-m", "isort"] + python_files, check=True)
        print(f"Applied isort to {len(python_files)} Python files")
    except subprocess.CalledProcessError as e:
        print(f"isort failed: {e}")
        success = False

    return success


def main():
    """Main function."""
    print("Starting comprehensive continuation cleanup...")

    # 1. Translate remaining Chinese comments
    print("\n1. Translating remaining Chinese comments...")
    files_updated = 0
    for root, dirs, files in os.walk("."):
        if ".git" in root or "__pycache__" in root:
            continue
        for file in files:
            if file.endswith((".kt", ".java")):
                file_path = os.path.join(root, file)
                if translate_chinese_comments(file_path):
                    files_updated += 1

    print(f"Updated {files_updated} files with Chinese comment translations")

    # 2. Run Python linting
    if not run_python_linting():
        print("Python linting failed")
        return False

    # 3. Check for any remaining Chinese characters
    print("\n3. Checking for remaining Chinese characters...")
    remaining_chinese = []
    for root, dirs, files in os.walk("."):
        if ".git" in root or "__pycache__" in root:
            continue
        for file in files:
            if file.endswith((".kt", ".java", ".xml", ".py")):
                file_path = os.path.join(root, file)
                chinese_lines = find_chinese_characters(file_path)
                if chinese_lines:
                    remaining_chinese.append((file_path, chinese_lines))

    if remaining_chinese:
        print(f"Found remaining Chinese characters in {len(remaining_chinese)} files:")
        for file_path, lines in remaining_chinese[:10]:  # Show first 10
            print(f"  {file_path}: {len(lines)} lines")
    else:
        print("No remaining Chinese characters found!")

    print("\nContinuation cleanup completed successfully!")
    return True


if __name__ == "__main__":
    main()
