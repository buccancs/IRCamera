@echo off
chcp 65001

call gradlew clean

echo "开始编译测试版本"
call gradlew :app:assembleDebug


@rem call gradlew :app:assembleDebug

echo "编译打包完成，apk文件在根目录outputs/"

pause